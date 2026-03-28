package models;

import java.util.Map;

public class Board {
    private Map<Integer, Integer> snakes;
    private Map<Integer, Integer> ladders;
    private int size;
    public Board(int size, Map<Integer, Integer> snakes, Map<Integer, Integer> ladders){
        this.size = size ;
        this.snakes = snakes;
        this.ladders =  ladders;
    }

    public int getSize() {
        return size;
    }

    public int getFinalPosition(int pos) {
        if (snakes.containsKey(pos)) {
            return snakes.get(pos);
        }
        if (ladders.containsKey(pos)) {
            return ladders.get(pos);
        }
        return pos;



    }
}

