import model.ChessGame;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

            ChessGame chessGame = new ChessGame();
            chessGame.setPlayers("Alice", "Bob");
            chessGame.start();
        }
    }
