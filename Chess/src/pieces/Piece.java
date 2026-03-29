
package pieces;

import model.Cell;
import model.Color;
import model.Board;

public abstract class Piece {
    protected Color color;

    public Piece(Color color) {
        this.color = color;
    }

    public abstract boolean canMove(Board board, Cell from, Cell to);

    public Color getColor() {
        return color;
    }
}