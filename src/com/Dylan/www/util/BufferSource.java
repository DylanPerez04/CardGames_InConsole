package com.Dylan.www.util;

import java.util.Scanner;

public interface BufferSource {

    static int getInteger(String toRepeat) {
        Scanner scanner = new Scanner(System.in);

        Integer buffer = null;
        while(buffer == null) {
            System.out.print(toRepeat);
            if(scanner.hasNextInt()) {
                buffer = scanner.nextInt();
            } else {
                scanner.nextLine();
            }
        }
        return buffer;
    }

    static String getNextToken(String toRepeat) {
        Scanner scanner = new Scanner(System.in);

        String buffer = null;
        while(buffer == null) {
            System.out.print(toRepeat);
            if(scanner.hasNext()) {
                buffer = scanner.next();
            } else {
                scanner.nextLine();
            }
        }
        return buffer;
    }

    static void clearConsole(int linesToPrint) {
        for(int i = 0; i < linesToPrint; i++)
            System.out.println();
    }

}
