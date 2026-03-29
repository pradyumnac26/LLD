package pieces;

import model.Board;
import model.Cell;
import model.Color;

public class Pawn extends Piece{
    public Pawn(Color color){
        super(color);
    }
    @Override
    public boolean canMove(Board board, Cell from, Cell to) {
        int rowDiff = to.getRow() - from.getRow();
        int colDiff = Math.abs(from.getCol() - to.getCol());

        Piece targetPiece = to.getPiece();
        if (color == Color.WHITE) {
            if (rowDiff == 1 && colDiff == 0 ) {
                return targetPiece == null;
            }
            if (from.getRow() == 1 && rowDiff == 2 && colDiff == 0) {
                return targetPiece == null;
            }
            if (rowDiff == 1 && colDiff == 1) {
                return targetPiece != null && targetPiece.getColor() != this.color;
            }

        }
        else { // BLACK

            // 1. Black moves one step forward
            if (rowDiff == -1 && colDiff == 0) {
                return targetPiece == null;
            }

            // 2. Black moves two steps forward from starting row
            if (from.getRow() == 6 && rowDiff == -2 && colDiff == 0) {
                return targetPiece == null;
            }

            // 3. Black captures diagonally
            if (rowDiff == -1 && colDiff == 1) {
                return targetPiece != null && targetPiece.getColor() != this.color;
            }
        }

        return false;
    }
    }
