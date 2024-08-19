package com.Dylan.www.util;

import com.Dylan.www.Main;
import com.Dylan.www.domain.Card;
import com.Dylan.www.model.Player;
import com.Dylan.www.model.BSPlayer;

import java.util.*;

public class BullShit implements CardGame, Peekable<Card> {

    private Scanner scanner;

    private List<Card> gameDeck, cardPile;
    private List<BSPlayer> players;

    private int turns = 0;

    private String faceInPlay;
    // Used in the event BSPlayer calls bluff and we need to see cards added to cardPile
    private int numCardsPrevPlaced;

    public BullShit(Scanner scanner) {
        this.scanner = scanner;
        this.gameDeck = Card.getStandardDeck();
        this.cardPile = new ArrayList<>(52);
        this.players = BSPlayer.getPlayers(this, scanner);

        BufferSource.clearConsole(Main.initialLinesToPrint);
    }

    public String getFaceInPlay() { return faceInPlay; }

    public void setNumCardsPrevPlaced(int numCardsPrevPlaced) {
        this.numCardsPrevPlaced = numCardsPrevPlaced;
    }

    public int bluff(BSPlayer player) {
        System.out.println("How many cards to bluff? ");
        int toBluff = BufferSource.getInteger(">> ");
        int initialHandSize = player.handSize();

        if(toBluff >= player.handSize()) {
            System.out.println("Bluffing all your cards.");
            player.dumpHand(cardPile);
            BufferSource.clearConsole(Main.generalLinesToPrint);
            return initialHandSize;
        }

        // *  This loop is defining the logic of what cards to bluff when the player decides to bluff,
        // *  as I have explicitly decided that a player will not be able to individually choose
        // *  which cards to bluff, as that would make this demo way too cumbersome on ME as
        // *  the tester/sole player of this game/challenge, and perhaps anybody I showcase this
        // *  too.
        // *
        // *  Card.FACES.length = 13 : [2, 3, 4, 5, 6, 7, 8, 9, 10, J, Q, K, A]
        // *
        // *  How the logic works is that the first loop, i, is initialized to the index of the
        // *  current face in play in Card[] Card.FACES. From there, it continues until
        // *  cardsRemoved == toBluff, and is incremented by the number of players
        // *  in the game. This, in essence, is making i the index in Card.FACES of
        // *  all the faces that, ideally, the player would NOT want to bluff with, as
        // *  when this player's next couple turns come up, those are the cards
        // *  with the faces the player will be able to place without bluffing.
        // *
        // *  From there I initialize toRemove with either an empty array, or an
        // *  array containing all the Cards in the player's hand that have the
        // *  same face as the faceInPlay. This means the player bluffs first with
        // *  the cards that they could have otherwise placed as face cards, adding to
        // * the intricacy, of the game, as there is a one in ten chance a message will
        // * appear when the players bluffs hinting they may or may not have bluffed.
        // *
        // *  After, I create an inner loop of j, where j will be reassigned to rIndex
        // *  modulo'd by Card.FACES.length, where rIndex will hold the index in
        // *  Card.FACES of the faces the player would ideally WANT to bluff with.
        // * rIndex is then used to search the player's hand for any cards that
        // * have the same face as Card.FACES[rIndex], and if so, add them all to
        // * the list toRemove of type Card.
        // *
        // * Depending on how many cards have been removed, either all of toRemove
        // * will be removed from the player's hand, or only the first few indexes
        // * needed to make cardsRemoved == toBluff.

        List<Card> toRemove;
        int cardsRemoved = 0;
        for(int i = Arrays.asList(Card.FACES).indexOf(faceInPlay); cardsRemoved < toBluff; i += getPlayerCount()) {
            // * Initialize or reset toRemove so that when conditions below use toRemove.get(k),
            // * they are actually removing newly added cards, instead
            // * of attempting to remove already removed Cards
            toRemove = player.searchHand(Card.FACES[Arrays.asList(Card.FACES).indexOf(faceInPlay)]);

            if (toRemove.size() >= toBluff) {
                for (int j = 0; j < toBluff; j++) {
                    cardPile.add(toRemove.get(j));
                    cardsRemoved += player.removeFromHand(toRemove.get(j));
                }
            } else {
                for (int j = i + 1; cardsRemoved < toBluff &&  j < i + getPlayerCount(); j++) {
                    int rIndex = j % Card.FACES.length; // Remove index
                    toRemove.addAll(player.searchHand(Card.FACES[rIndex]));

                    if(cardsRemoved + toRemove.size() <= toBluff)
                        for(int k = 0; k < toRemove.size(); k++) {
                            cardPile.add(toRemove.get(k));
                            cardsRemoved += player.removeFromHand(toRemove.get(k));
                        }
                    else
                        for(int k = 0; k < toBluff - cardsRemoved; k++) {
                            cardPile.add(toRemove.get(k));
                            cardsRemoved += player.removeFromHand(toRemove.get(k));
                        }

                    // * Reset toRemove so that when conditions above use toRemove.get(k),
                    // * they are actually removing the newly added cards from .addAll, instead
                    // * of attempting to remove already removed Cards
                    toRemove = new ArrayList<>(toBluff - cardsRemoved);
                }
            }
        }

        BufferSource.clearConsole(Main.generalLinesToPrint);
        return initialHandSize - player.handSize();
    }

    public int placeFaceCards(BSPlayer player) {
        String faceInPlay = getFaceInPlay();
        List<Card> faceCardsInHand = player.searchHand(faceInPlay);

        if(faceCardsInHand.size() == 0) {
            System.out.println(ERR + " You don't have any face cards in hand.");
            return 0;
        }

        cardPile.addAll(faceCardsInHand);
        player.removeFromHand(faceCardsInHand);

        BufferSource.clearConsole(Main.generalLinesToPrint);
        return faceCardsInHand.size();
    }

    /**
     *
     * @param bluffCaller The BSPlayer calling the bluff
     * @return True if operation was successful (turns > 0)
     */
    public boolean callBluff(BSPlayer bluffCaller) {
        if(turns == 1) {
            System.out.println(ERR + " Can't call bluff on first turn of game.");
            return false;
        } else if(numCardsPrevPlaced == 0) {
            System.out.println(ERR + " No cards were placed last turn.");
            return false;
        }

        BufferSource.clearConsole(Main.generalLinesToPrint);

        int indexOfPrevFaceInPlay = Arrays.asList(Card.FACES).indexOf(faceInPlay) - 1;
        String prevFaceInPlay = Card.FACES[indexOfPrevFaceInPlay > -1 ? indexOfPrevFaceInPlay : 12];

        System.out.println(ACT + " " + bluffCaller.getName() + " called BS!\n"
                + "| Previous face : " + prevFaceInPlay + " |");

        Card[] prevPlaced = peekBack();

        boolean wasBluffing = false;
        for(Card c : prevPlaced)
            if(!c.getFace().equalsIgnoreCase(prevFaceInPlay)) {
                wasBluffing = true;
                break;
            }

        BSPlayer loser;
        BSPlayer prevPlayer = players.get(players.indexOf(bluffCaller) > 0 ? players.indexOf(bluffCaller) - 1 : players.size() - 1);

        if(wasBluffing)
            loser = prevPlayer;
        else
            loser = bluffCaller;

        System.out.printf("%s %s %s bluffing!%n", UPD, prevPlayer.getName(), (wasBluffing ? "was" : "was not"));
        loser.addToHand(cardPile);
        loser.sortHand();

        cardPile.clear();
        return true;
    }

    /**
     *
     *  Sets the rank (order of play) for the players playing the cardGame;
     *  Ex. Player with rank = 1 will play before a player with rank >1
     *
     */
    private void setRanks() {
        List<Integer> ranks = new ArrayList<>(players.size());
        for(int i = 0; i < players.size(); i++)
            ranks.add(i);
        Collections.shuffle(ranks);
        for(int i = 0; i < ranks.size(); i++)
            players.get(i).setRank(ranks.get(i));
        players.sort(null);
    }

    private void dealHands() {
        shuffle();
        int cIndex = 0;
        for(int i = 0; i < gameDeck.size() / players.size(); i++) {
            for(int j = 0; j < players.size(); j++)
                players.get(j).addToHand(gameDeck.get(cIndex++));
        }
        for(Player p : players)
            p.sortHand();
    }

    @Override
    public Card[] peekBack() {
        System.out.println(cardPile.subList(cardPile.size() - numCardsPrevPlaced, cardPile.size()));
        Card[] peekCards = new Card[numCardsPrevPlaced];
        for(int i = 0; i < peekCards.length; i++)
            peekCards[i] = cardPile.get(cardPile.size() - 1 - i);

        return peekCards;
    }

    @Override
    public int getTurnsPlayed() {
        return turns;
    }

    @Override
    public int getPlayerCount() { return players.size(); }

    @Override
    public void shuffle() {
        Collections.shuffle(gameDeck);
    }

    @Override
    public void printGameDeck() {
        Card.printDeck(gameDeck, "Game Deck", 4);
    }

    @Override
    public int printPlayerOptions() {
        final String[] options = {"Place Face Cards", "Bluff", "Call Bluff"};
        System.out.println("[ Options ]");
        for(int i = 0; i < options.length; i++)
            System.out.printf("\t%d. %s%n".formatted(i + 1, options[i]));
        return options.length;
    }

    @Override
    public void play() {
        setRanks();
        dealHands();

        int fIndex = 12; // Card.FACES.length-1 to get A as first face in rotation

        Player currentPlayer, previousPlayer = null;
        Player winner = null;
        for(Iterator<? extends Player> li = players.iterator(); winner == null; ) {
            turns++;
            faceInPlay = Card.FACES[fIndex++];
            System.out.println("\nFace in Play: " + faceInPlay);

            currentPlayer = li.next();
            currentPlayer.play(cardPile);

            if(previousPlayer != null && previousPlayer.handIsEmpty())
                winner = previousPlayer;

            System.out.println(UPD + " " + currentPlayer.getName() + " placed " + numCardsPrevPlaced + " cards.");

            previousPlayer = currentPlayer;

            if(!li.hasNext())
                li = players.iterator();
            if(fIndex > 12)
                fIndex = 0;
        }
        System.out.println("\n==================================");
        System.out.println(winner.getName() + " has won " + getClass().getSimpleName().replace('i', '*') + "!");
        System.out.println("==================================");

    }
}
