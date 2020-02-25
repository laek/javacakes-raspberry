package com.makers.javacakesraspberry;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.sound.sampled.*;
import javax.swing.*;

public class Intercom extends JFrame implements Runnable {
    /** Program Version */
    public static final String VERSION = "0.17";

    /** Program Date */
    public static final String DATE = "15 June 2019";

    /** Default port number */
    private static final String DEFAULT_PORT = "56789";

    /** Default destination address */
    private static final String DEFAULT_ADDRESS = "localhost";

    /** User home directory */
    private static final File USER_HOME =
            new File(System.getProperty("user.home"));

    /** Properties file */
    private static final File PROPERTIES_FILE =
            new File(USER_HOME,".intercom");

    /** PCM_SIGNED AudioFormat */
    private static final AudioFormat PCM =
            new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,8000f,16,1,2,8000f,false);

    /** ULAW AudioFormat */
    private static final AudioFormat ULAW =
            new AudioFormat(AudioFormat.Encoding.ULAW,8000f,8,1,1,8000f,false);

    /** ALAW AudioFormat */
    private static final AudioFormat ALAW =
            new AudioFormat(AudioFormat.Encoding.ALAW,8000f,8,1,1,8000f,false);

    /** Program properties */
    private final Properties properties = new Properties();

    /** Transmit RingBuffer to access audio data from TargetDataLine in a
     *  AudioInputStream so that the format can be changed to ULAW */
    private final RingBuffer txBuf = new RingBuffer(10000);

    /** Receive RingBuffer to access received audio data in a AudioInptStream
     *  so that the format can be change back to PCM_SIGNED */
    private final RingBuffer rxBuf = new RingBuffer(10000);

    /** Thread that reads data from the transmit RingBuffer, assembles it into
     *  a DatagramPacket and transmits it */
    private final Thread sendThread;
    /** Thread that reads DatagramPackets and writes the data to the receive
     *  RingBuffer */
    private final Thread receiveThread;

    /** Thread that reads the receive RingBuffer, converts the data to
     *  PCM_SIGNED format and writes it to the SourceDataLine */
    private final Thread audioThread;

    /** A JMenu that displays a moving symbol, changed when datagram packets
     *  are received */
    private final ActivityJMenu activity;

    /** Flag to control the thread reading data from the TargetDataLine */
    private volatile boolean runFlag;

    /** TargetDataLine to collect audio from the microphone */
    private volatile TargetDataLine tdl;

    /** DatagramSocket used to receive packets of audio data from another copy
     *  of Intercom */
    private volatile DatagramSocket rxSocket;

    /**
     * Creates a new Intercom GUI and three of the data transfer threads
     */
    public Intercom() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        loadProperties(properties);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu file = menuBar.add(new JMenu("File"));
        JMenu help = menuBar.add(new JMenu("Help"));
        activity = new ActivityJMenu();
        menuBar.add(activity);

        JMenuItem mi;

        mi = file.add("Set Port");
        mi.addActionListener(event -> {
            String value = JOptionPane.showInputDialog(this,"Enter Port",
                    properties.getProperty("port",DEFAULT_PORT));
            if (value != null) {
                try {
                    int port = Integer.parseInt(value);
                    if (port < 1024 || port > 65535)
                        throw new IllegalArgumentException(
                                "port number out of range (1024 - 65535)");
                    properties.put("port",value);
                    storeProperties(properties);
                    rxSocket.close();
                } catch (IllegalArgumentException iae) {
                    JOptionPane.showMessageDialog(this,iae,"Invalid Port",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        mi = file.add("Set Address");
        mi.addActionListener(event -> {
            String value = JOptionPane.showInputDialog(this,"Enter Address",
                    properties.getProperty("address",DEFAULT_ADDRESS));
            if (value != null) {
                try {
                    InetAddress inet = InetAddress.getByName(value);
                    properties.setProperty("address",value);
                    storeProperties(properties);
                } catch (UnknownHostException uhe) {
                    JOptionPane.showMessageDialog(this,uhe,"Invalid Address",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        file.add(new JSeparator());
        mi = file.add("Quit");
        mi.addActionListener(event -> dispose());

        mi = help.add("About");
        mi.addActionListener(event -> JOptionPane.showMessageDialog(this,
                "Intercom\n" +
                        "Version: " + VERSION + " - " + DATE +
                        "\nWritten by: Knute Johnson","About Intercom",
                JOptionPane.INFORMATION_MESSAGE));

        JLabel talkButton = new JLabel("TALK",JLabel.CENTER);
        talkButton.setOpaque(true);
        talkButton.setBackground(Color.GREEN.darker());
        talkButton.setForeground(Color.WHITE);
        talkButton.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));
        talkButton.setFont(new Font(Font.SANS_SERIF,Font.BOLD,48));
        talkButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                talkButton.setBackground(Color.RED.darker());
                runFlag = true;
                new Thread(Intercom.this).start();
            }
            public void mouseReleased(MouseEvent me) {
                talkButton.setBackground(Color.GREEN.darker());
                runFlag = false;
            }
        });
        add(talkButton,BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        try {
            tdl = AudioSystem.getTargetDataLine(PCM);
            //tdl.addLineListener(event ->
            // System.out.printf("TDL: %s%n",event.getType()));
            tdl.open();
        } catch (LineUnavailableException lue) {
            lue.printStackTrace();
            JOptionPane.showMessageDialog(this,lue,"FATAL ERROR",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
        }

        // The sendThread creates the sending DatagramSocket, reads data from
        // the transmit RingBuffer, converts it to ULAW and transmits it in a
        // datagram to the other Intercom program.
        sendThread = new Thread(() -> {
            while (true) {
                try (DatagramSocket socket = new DatagramSocket();
                     AudioInputStream pcm = new AudioInputStream(
                             txBuf.getInputStream(),PCM,AudioSystem.NOT_SPECIFIED);
                     AudioInputStream ais =
                             AudioSystem.getAudioInputStream(ULAW,pcm)) {

                    byte[] buf = new byte[800];
                    int bytesRead;
                    while ((bytesRead = ais.read(buf,0,buf.length)) != -1) {
                        //System.out.printf("Bytes read from txbuf: %d\n",
                        // bytesRead);
                        DatagramPacket packet = new DatagramPacket(buf,
                                bytesRead,new InetSocketAddress(
                                properties.getProperty("address",DEFAULT_ADDRESS),
                                Integer.parseInt(properties.getProperty("port",
                                        DEFAULT_PORT))));
                        socket.send(packet);
                        //System.out.printf("Send packet length: %d\n",
                        // packet.getLength());
                        Thread.sleep(50);
                    }
                } catch (IOException|InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });
        sendThread.setDaemon(true);

        // The receiveThread creates the receive DatagramSocket, reads the
        // inbound datagram packets from the other Intercom program  and writes
        // the data to the receive RingBuffer.
        receiveThread = new Thread(() -> {
            while (true) {
                try {
                    rxSocket = new DatagramSocket(Integer.parseInt(
                            properties.getProperty("port",DEFAULT_PORT)));
                    byte[] buf = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buf,buf.length);
                    while (true) {
                        rxSocket.receive(packet);
                        rxBuf.getOutputStream().write(packet.getData(),
                                packet.getOffset(),packet.getLength());
                        activity.update();
                        //System.out.printf("Receive packet length: %d\n",
                        // packet.getLength());
                        //System.out.println(
                        //rxBuf.getInputStream().available() +
                        //" - " + txBuf.getInputStream().available());
                    }
                } catch (IOException ioe) {
                    if (ioe instanceof SocketException)
                        System.out.println("Port Changed");
                    else
                        ioe.printStackTrace();
                } finally {
                    rxSocket.close();
                }
            }

        });
        receiveThread.setDaemon(true);

        // The audioThread reads the data in the receive RingBuffer, converts
        // the AudioFormat to PCM_SIGNED and writes that data to a
        // SourceDataLine.
        audioThread = new Thread(() -> {
            while (true) {
                try (AudioInputStream ais = new AudioInputStream(
                        rxBuf.getInputStream(),ULAW,AudioSystem.NOT_SPECIFIED);
                     AudioInputStream pcm =
                             AudioSystem.getAudioInputStream(PCM,ais);
                     SourceDataLine sdl = AudioSystem.getSourceDataLine(
                             pcm.getFormat())) {
                    //System.out.println(sdl.getFormat());
                    //System.out.println(sdl.getBufferSize());
                    //sdl.addLineListener(event ->
                    //System.out.printf("SDL: %s%n",event.getType()));
                    sdl.open();
                    sdl.start();

                    byte[] buf = new byte[800];
                    int bytesRead;
                    while ((bytesRead = pcm.read(buf)) != -1) {
                        sdl.write(buf,0,bytesRead);
                        //System.out.printf("Bytes read from rxbuf: %d\n",
                        // bytesRead);
                    }
                } catch (IOException|LineUnavailableException ex) {
                    ex.printStackTrace();
                }
            }
        });
        audioThread.setDaemon(true);
    }

    /**
     * Starts the three transfer threads.
     */
    public void start() {
        if (sendThread.getState() == Thread.State.NEW)
            sendThread.start();
        if (receiveThread.getState() == Thread.State.NEW)
            receiveThread.start();
        if (audioThread.getState() == Thread.State.NEW)
            audioThread.start();
    }

    /**
     * Reads data from the TargetDataLine (microphone) and writes that data to
     * the transmit RingBuffer when the talk button is pressed.
     */
    public void run() {
        try {
            tdl.start();
            byte[] buf = new byte[800];
            txBuf.getOutputStream().write(buf);
            int bytesRead;
            while (runFlag) {
                bytesRead = tdl.read(buf,0,buf.length);
                txBuf.getOutputStream().write(buf,0,bytesRead);
                //System.out.println(bytesRead);
            }
            Arrays.fill(buf,(byte)0);
            txBuf.getOutputStream().write(buf);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            tdl.stop();
            tdl.flush();
        }
    }

    /**
     * Loads the program properties from a data file
     *
     * @param   properties  destination for Properties read from file
     */
    private void loadProperties(Properties properties) {
        try (FileReader reader = new FileReader(PROPERTIES_FILE)) {
            properties.load(reader);
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(this,ioe,
                    "Unable to Load Properties - Using Defaults",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Stores the program properties to a data file
     *
     * @param   properties  source of Properties to be written to file
     */
    private void storeProperties(Properties properties) {
        try (FileWriter writer = new FileWriter(PROPERTIES_FILE)) {
            properties.store(writer,"Intercom");
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(this,ioe,
                    "Unable to Store Properties",JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * This RingBuffer is an unprotected circular buffer used to move byte data
     * to and from streams.
     */
    private static class RingBuffer {
        /** The RingBuffer's buffer */
        private final byte[] ringBuffer;

        /** An InputStream to read data from the RingBuffer */
        private final RingBuffer.InputStream is;

        /** An OutputStream to write data to the RingBuffer */
        private final RingBuffer.OutputStream os;

        /** The head of the buffer where data is read from first */
        private int head;

        /** The tail of the buffer where new data is written to the buffer */
        private int tail;

        /**
         * Creates a new RingBuffer of the specified size in bytes
         *
         * @param   bufSize number of bytes of space in the buffer
         */
        public RingBuffer(int bufSize) {
            ringBuffer = new byte[bufSize];
            is = new RingBuffer.InputStream();
            os = new RingBuffer.OutputStream();
        }

        /**
         * Gets the InputStream from this buffer
         *
         * @return  the buffer's InputStream
         */
        public RingBuffer.InputStream getInputStream() {
            return is;
        }

        /**
         * Gets this buffer's OutputStream
         *
         * @return  the buffer's OutputStream
         */
        public RingBuffer.OutputStream getOutputStream() {
            return os;
        }

        /**
         * An InputStream to read data from this buffer
         */
        class InputStream extends java.io.InputStream implements AutoCloseable {
            /** Flag to mark a closed stream */
            private volatile boolean closedFlag;

            /**
             *  Read a single byte from the buffer.  If there is no data in the
             *  buffer this code will block until data is available.
             *
             *  @return the byte read stored in an int
             *  @throws IOException if an attempt is made to read from a closed
             *          stream
             */
            @Override public int read() throws IOException {
                if (closedFlag)
                    throw new IOException("stream closed");

                synchronized (ringBuffer) {
                    while (head == tail)
                        try {
                            ringBuffer.wait();
                        } catch(InterruptedException ie) { }

                    int value = ringBuffer[head] & 0xff;

                    if (++head == ringBuffer.length)
                        head = 0;

                    return value;
                }
            }

            /**
             * Reads len bytes into the byte buffer starting at the off.
             *
             * @param   buf byte buffer to store the data that is read
             * @param   off offset within buffer where first byte is stored
             * @param   len number of bytes to attempt to read
             *
             * @return  number of bytes read and stored in buf
             *
             * @throws  IOException if an error occurs reading data
             * @throws  NullPointerException if buf is null
             * @throws  IndexOutOfBoundsException if off &lt; 0, len &lt; 0 or
             *          len &gt; buf.length - off
             */
            @Override public int read(byte[] buf, int off, int len) throws
                    IOException {
                if (buf == null)
                    throw new NullPointerException("null buffer");
                if (off < 0 || len < 0 || len > buf.length - off)
                    throw new IndexOutOfBoundsException();

                if (len == 0)
                    return 0;

                int bytesRead = len;

                buf[off++] = (byte)read();
                --len;
                while (available() > 0 && len > 0) {
                    buf[off++] = (byte)read();
                    --len;
                }

                return bytesRead - len;
            }

            /**
             * Read bytes from the stream storing them into buf.  This is the
             * same as calling read(buf,0,buf.length)
             *
             * @return  number of bytes read and stored in buf
             *
             * @param   buf byte buffer to store read bytes
             *
             * @throws  IOException if an error occurs reading a byte
             */
            @Override public int read(byte[] buf) throws IOException {
                return read(buf,0,buf.length);
            }

            /**
             * Gets the number of bytes of data that may be read from the
             * InputStream.
             *
             * @return  number of bytes available to be read
             */
            @Override public int available() {
                synchronized (ringBuffer) {
                    if (head == tail)
                        return 0;

                    if (head < tail)
                        return tail - head;
                    else
                        return ringBuffer.length - head + tail;
                }
            }

            /**
             * Closes this stream
             *
             * @throws  IOException if an error occurs
             */
            @Override public void close() throws IOException {
                super.close();
                closedFlag = true;
            }
        }


        /**
         * An OutputStream to write data to this buffer
         */
        class OutputStream extends java.io.OutputStream implements
                AutoCloseable {
            /** Flag to mark a closed stream */
            private volatile boolean closedFlag;

            /**
             * Write a single byte to the buffer.
             *
             * @param   b byte to be written
             *
             * @throws  IOException if an error occurs
             */
            @Override public void write(int b) throws IOException {
                if (closedFlag)
                    throw new IOException("stream closed");

                synchronized (ringBuffer) {
                    ringBuffer[tail] = (byte)(b & 0xff);

                    if (++tail == ringBuffer.length)
                        tail = 0;

                    ringBuffer.notifyAll();
                }
            }

            /**
             * Write bytes from a byte array to this stream.
             *
             * @param   buf data to be written
             * @param   off index of first byte to be written
             * @param   len number of bytes to write
             *
             * @throws  IOException if an error occurs writing to the stream
             * @throws  NullPointerException if buf is null
             * @throws  IndexOutOfBoundsException if off &lt; 0, len &lt; 0 or
             *          off + len &gt; buf.length
             */
            @Override public void write(byte[] buf, int off, int len) throws
                    IOException {
                if (buf == null)
                    throw new NullPointerException();
                if (off < 0 || len < 0 || off + len > buf.length)
                    throw new IndexOutOfBoundsException();

                synchronized (ringBuffer) {
                    for (int i=off; i<off + len; i++)
                        write(buf[i]);
                }
            }

            /**
             * Write the contents of the byte array to the buffer.  Same as
             * calling write(buf,0,len).
             *
             * @param   buf byte array to write to stream
             *
             * @throws  IOException if an error occurs
             */
            @Override public void write(byte[] buf) throws IOException {
                write(buf,0,buf.length);
            }

            /**
             * Closes this stream.
             *
             * @throws  IOException if an error occurs
             */
            @Override public void close() throws IOException {
                super.close();
                closedFlag = true;
            }
        }
    }

    /**
     * A JMenu used to signal an activity occuring in the program.
     */
    private static class ActivityJMenu extends JMenu {
        /** Characters to display in the ActivityJMenu */
        private static final String[] text = { "|","/","-","\\" };

        /** Index of the next characther to display */
        private int index;

        /*
         * Create a new ActivityJMenu
         */
        public ActivityJMenu() {
            super(" ");
        }

        /**
         * Update the display by showing the next character
         */
        public void update() {
            EventQueue.invokeLater(() ->
                    setText(text[index = ++index % text.length]));
        }
    }

    /**
     * Main program entry point, creates a new Intercom and starts it running.
     *
     * @param   args    command line arguments (not used)
     */
    public static void main(String... args) {
        EventQueue.invokeLater(() -> new Intercom().start());
    }
}

