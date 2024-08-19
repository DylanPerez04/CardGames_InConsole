package com.Dylan.www.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Card {

    public enum Suit {
        CLUB,
        DIAMOND,
        HEART,
        SPADE;

        public char asAsciiChar() {
            return switch(this) {
                case CLUB -> (char) 9827;
                case DIAMOND -> (char) 9830;
                case HEART -> (char) 9829;
                case SPADE -> (char) 9824;
            };
        }
    }

    private Suit suit;
    private String face;
    private int rank;

    public static final String[] FACES = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

    private Card(Suit suit, String face) {
        this.suit = suit;
        this.face = face;
        this.rank = switch(face.toUpperCase()) {
            case "A" -> 12;
            case "K" -> 11;
            case "Q" -> 10;
            case "J" -> 9;
            default -> Integer.parseInt(face) - 2;
        };
    }

    public int getRank() { return rank; }
    public Suit getSuit() { return suit; }
    public String getFace() { return face; }

    public static Card getNumericCard(Suit suit, int number) {
        return new Card(suit, String.valueOf(number));
    }

    public static Card getFaceCard(Suit suit, char abbrev) {
        return new Card(suit, String.valueOf(abbrev));
    }

    public static List<Card> getStandardDeck() {
        List<Card> deck = new ArrayList<>();
        for(Suit s : Suit.values()) {
            for(int i = 2; i <= 10; i++)
                deck.add(getNumericCard(s, i));
            for(String f : Arrays.asList(FACES).subList(FACES.length-4, FACES.length))
                deck.add(getFaceCard(s, f.charAt(0)));
        }
        return deck;
    }

    public static void printDeck(List<Card> list, String description, int rowCount) {
        System.out.println("-----------------");
        if(!description.isEmpty())
            System.out.println(description);
        int cardCount = list.size();
        int cardsToRemain = cardCount % rowCount;

        int cIndex = 0;
        for(int i = 0; i < rowCount; i++) {
            for(int j = 0; j < cardCount / rowCount; j++)
                System.out.print(list.get(cIndex++) + " ");
            if(i == rowCount - cardsToRemain)
                System.out.print(list.get(cIndex++) + " ");
            System.out.println();
        }

    }

    public static void printDeck(List<Card> list) {
        printDeck(list, "Current Deck", 4);
    }

    @Override
    public String toString() {
        return "%s%c(%d)".formatted(face, suit.asAsciiChar(), rank);
    }

    @Override
    public boolean equals(Object obj) {
        return this.suit.equals(((Card) obj).suit) && this.face.equals(((Card) obj).face);
    }
}
