package com.Dylan.www;

import com.Dylan.www.util.*;

import java.util.Scanner;

public class Main {

    /**
     * initialLinesToPrint
     *
     * Number of new lines needed to clear initial BullShit.getPlayers() prompt
     */
    public static int initialLinesToPrint = 20;

    /**
     * generalLinesToPrint
     *
     * Number of new lines needed to clear general BullShit.play() prompts and messages
     */
    public static int generalLinesToPrint = 30;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        CardGame game= new BullShit(scanner);
        game.play();
    }

}
