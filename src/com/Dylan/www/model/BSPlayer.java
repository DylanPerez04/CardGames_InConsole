package com.Dylan.www.model;

import com.Dylan.www.util.BufferSource;
import com.Dylan.www.util.BullShit;
import com.Dylan.www.util.CardGame;
import com.Dylan.www.domain.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BSPlayer extends Player {

    private BullShit game;

    public BSPlayer(BullShit game, Scanner scanner) {
        super(scanner);
        this.game = game;
    }

    public BSPlayer(BullShit game, Scanner scanner, String name) {
        super(scanner, name);
        this.game = game;
    }

    @Override
    public boolean play(List<Card> cardPile) {
        String faceInPlay = game.getFaceInPlay();
        List<Card> faceCardsInHand = searchHand(faceInPlay);

        System.out.println(this.getName() + "'s turn vvvvvvvv");
        this.printHand();

        System.out.println("Card count : " + handSize());
        System.out.printf("You have %d %s's.%n", faceCardsInHand.size(), faceInPlay);

        game.printPlayerOptions();

        int cardsPlaced = 0;
        boolean playedTurn = false;
        while(!playedTurn) {
            int option = BufferSource.getInteger(CardGame.INP);
            switch(option) {
                case 1: // Place Face Cards
                    cardsPlaced = game.placeFaceCards(this);
                    if(cardsPlaced == 0)
                        break;

                    playedTurn = true;
                    continue;
                case 2: // Bluff
                    cardsPlaced = game.bluff(this);
                    playedTurn = true;
                    continue;
                case 3: // Call Bluff
                    if(!game.callBluff(this))
                        break;
                    playedTurn = true;
                    continue;
                default:
                    break;
            }
        }
        game.setNumCardsPrevPlaced(cardsPlaced);

        return this.handIsEmpty();
    }

    public static List<BSPlayer> getPlayers(BullShit game, Scanner scanner) {
        List<BSPlayer> players;

        // int playerCount = 3; initialized for debugging
        int playerCount = 0;
        while(playerCount < 3 || playerCount > 52)
            playerCount = BufferSource.getInteger("Number of players: ");

        players = new ArrayList<>(playerCount);

        if(BufferSource.getNextToken("Add names (y/n)? ").equalsIgnoreCase("y")) {
            String playerName;
            for (int i = 0; i < playerCount; i++) {
                System.out.print(">> ");
                if(scanner.hasNextLine()) {
                    playerName = scanner.nextLine();
                    boolean alreadyExists = false;
                    for(BSPlayer p : players)
                        if(p.getName().equals(playerName)) {
                            alreadyExists = true;
                            break;
                        }

                    if(!alreadyExists) {
                        players.add(new BSPlayer(game, scanner, playerName));
                        continue;
                    }
                    System.out.println(CardGame.ERR + " Player with name " + playerName + " already exists.");
                    i--;
                }
            }
        } else {
            for(int i = 0; i < playerCount; i++)
                players.add(new BSPlayer(game, scanner));
        }

        return players;
    }

}
