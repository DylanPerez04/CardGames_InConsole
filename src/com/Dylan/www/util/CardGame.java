package com.Dylan.www.util;

public interface CardGame {

    String ERR = "[!]";
    String UPD ="[*]";
    String ACT = "[#]";
    String INP = ">> ";

    void shuffle();

    void play();

    int getPlayerCount();

    int getTurnsPlayed();

    int printPlayerOptions();

    default void printGameDeck() {}

}
