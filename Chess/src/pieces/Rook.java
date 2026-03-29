package pieces;

import model.Board;
import model.Cell;
import model.Color;

public class Rook extends Piece{
    public Rook(Color color){
        super(color);
    }

    @Override
    public boolean canMove(Board board, Cell from, Cell to) {
        return (from.getRow() == to.getRow() || from.getCol() == to.getCol());
    }
}
