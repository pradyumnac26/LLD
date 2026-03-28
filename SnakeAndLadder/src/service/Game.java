package service;

import models.Board;
import models.Dice;
import models.GameState;
import models.Player;

import java.util.List;

public class Game {
    private Board board;
    private List<Player> players;
    private Dice dice;
    private int currentPlayerIndex ;
    private Player winner;
    private GameState state;

    public Game(Board board, List<Player> players, Dice dice){
        this.board = board;
        this.players = players;
        this.dice = dice;
        this.currentPlayerIndex = 0;
        this.state = GameState.IN_PROGRESS;
    }

    public Board getBoard() {
        return board;
    }

    public GameState getState() {
        return state;
    }

    public int getCurrentPlayer() {
        return currentPlayerIndex;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getWinner() {
        return winner;
    }

    public String rollAndMove() {
        if (state == GameState.FINISHED) {
            return "Game Over";
        }

        Player currentPlayer = players.get(currentPlayerIndex);
        int diceValue = dice.roll();
        int currentPosition = currentPlayer.getPosition();
        int newPosition = currentPosition + diceValue;

        if (newPosition > board.getSize()) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            return currentPlayer.getName() + " rolled " + diceValue +
                    " and stays at " + currentPosition;
        }

        int finalPosition = board.getFinalPosition(newPosition);
        currentPlayer.setPosition(finalPosition);

        if (finalPosition == board.getSize()) {
            winner = currentPlayer;
            state = GameState.FINISHED;
            return currentPlayer.getName() + " rolled " + diceValue +
                    " and won the game!";
        }

        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

        return currentPlayer.getName() + " rolled " + diceValue +
                " and moved from " + currentPosition + " to " + finalPosition;
    }
}
