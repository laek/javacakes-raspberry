/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.makers.javacakesraspberry;

import java.util.Scanner;

/**
 *
 * @author laura
 */
public class Main {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        while(true) {

            System.out.println("Enter Input:");

            String userInput = input.nextLine();

            if(userInput.equals("a")) {

                TakePicture picture = new TakePicture();

            } else {

                System.out.println("Try again...");

            }
        }
    }
}