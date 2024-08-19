package com.Dylan.www.model;

import com.Dylan.www.domain.Card;

import java.util.*;

public abstract class Player implements Comparable<Player> {

    private static int player_count = 1;

    private final String name;
    private final List<Card> hand;
    private int rank;

    protected Scanner scanner;

    public Player(Scanner scanner) { this(scanner, "Player" + player_count); }

    public Player(Scanner scanner, String name) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.scanner = scanner;
        player_count++;
    }

    /**
     *
     * @param cardPile The List of cards in which all players will collectively discard their cards
     *                 from their hands
     * @return True if the player has won, false otherwise
     */
    public abstract boolean play(List<Card> cardPile);
    public String getName() {
        return name;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int handSize() { return hand.size(); }

    public boolean handIsEmpty() { return hand.isEmpty(); }

    public void sortHand() {
        Collections.sort(hand, (o1, o2) -> Integer.compare(o1.getRank(), o2.getRank()));
    }

    public List<Card> searchHand(String face) {
        List<Card> subList = new ArrayList<>();
        for(Card c : hand)
            if(c.getFace().equalsIgnoreCase(face))
                subList.add(c);
        return subList;
    }

    /**
     * Dumps the player's hand into @param cardPile, clearing the player's
     * hand in the process.
     *
     * @param cardPile The List of Cards to dump player's hand into
     */
    public void dumpHand(List<Card> cardPile) {
        cardPile.addAll(List.copyOf(hand));
        hand.clear();
    }


    public boolean addToHand(Card... cards) {
        Card[] toAdd = cards;
        if(Arrays.asList(cards).contains(null))
            throw new IllegalArgumentException("Null Card not allowed.");
        return Collections.addAll(hand, cards);
    }

    public boolean addToHand(List<Card> cards) {
        List<Card> toAdd = cards;
        if(cards.contains(null))
            throw new IllegalArgumentException("Null Card not allowed.");
        return hand.addAll(toAdd);
    }

    public int removeFromHand(Card... cards) {
        return removeFromHand(Arrays.asList(cards));
    }

    public int removeFromHand(List<Card> cards) {
        List<Card> toRemove = cards;
        if(toRemove.contains(null))
            throw new IllegalArgumentException("Null Card not allowed.");
        hand.removeAll(toRemove);
        return toRemove.size();
    }

    protected void printHand() {
        Card.printDeck(hand, "", 1);
    }

    @Override
    public String toString() {
        StringBuilder toString = new StringBuilder(name + " (" + rank + ") \n[ ");
        if(hand != null)
            for(Card c : hand)
                toString.append(c.toString() + " ");
        toString.append("]");
        return toString.toString();
    }

    @Override
    public int compareTo(Player o) {
        return ((Integer) this.rank).compareTo(o.rank);
    }
}
