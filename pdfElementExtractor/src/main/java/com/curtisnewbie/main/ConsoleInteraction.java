package com.curtisnewbie.main;

import java.util.Scanner;

/**
 * ------------------------------------
 * 
 * Author: Yongjie Zhuang
 * 
 * ------------------------------------
 * 
 * <p>
 * Class for console interaction
 * </p>
 */
public class ConsoleInteraction {

    public static final Scanner sc = new Scanner(System.in);

    static void displayIntro() {
        System.out.println("------------------------------------");
        System.out.println();
        System.out.println("PDF Elements Extractor powered by Apache PDFBox");
        System.out.println();
        System.out.println("Yongjie Zhuang");
        System.out.println();
        System.out.println("------------------------------------");
    }

    /**
     * Interact with users and return a {@code Parameters} object containing the
     * parameters we need to undertake operations.
     * 
     * @return parameters for pdf I/O and processing
     */
    static Parameters getParamFromConsole() {
        System.out.println("Enter the absolute path to the pdf file:");
        String from = readLine();
        System.out.println("Enter the absolute path to the directory where the elements should be extracted to:");
        String to = readLine();
        System.out.println();
        return new Parameters(from, to);
    }

    /**
     * Read a response (next line) from console
     * 
     * @return response
     */
    static String readLine() {
        if (sc.hasNextLine()) {
            return sc.nextLine();
        }
        return null;
    }

}