# Snake and Ladder game 

## Requirements

- Design a snake and ladder board game, where multiple players roll a dice (say one dice)
- The board consists of 100 cells. 
- There are snakes and ladders across the board, so if someone hits the snake ka head then they will slide to the tail (downward)
- If someone counters a ladder at its bottom, we slide to the top. 
- No invalid moves (more than 100 u cant go, 2 ppl should not coninue, it should be alternate.) 
- The game starts at 0, and ends at 100

## Entity 

- Game 
- Board
- Dice
- Snakes
- Ladders
- Players

## Relationship 
Game has-a a board, List of players, Dice 
Boards will have -> Map of snakes,  Map of ladders

## Class Design 

```java 

class Dice { 
    + roll() -> int (random 0-> 6)
}

```

```java 
class Snake {
    - tail : int 
    - head : int 
    
    + gethead() 
     + getTail()   
        
}

```

```java 
class Ladder {
    - bottom : int 
    - top : int 
    
    + getBottom() 
    + getTop()
}

```

```java 
class Player {
    - name : string, 
    - position : int 
    
    + getname() 
    + getPositions() 
    + setPosition()    
}

```

```java 
class Board { 
    snakes : Map<int, int> (head, tail)
    ladders : Map<int, int> (bottom, top)
    
    + Board(size, snakes, ladders)
    + getSize() 
    + getFinalPosition()  -> do operations based on the map of snakes, ladders.  
}

```
```java 
enum models.GameState {
    IN_PROGRESS, 
    FINISHED
}
```

```java 
import models.GameState;

class Game {
    board :Board
    players :List<Players>
    dice :Dice
    currentPlayerIndex :int
    winner :Player
    state:GameState
    
    +rollAndMove()
    +getBoard() 
    +getPlayers() 
    +getDice() 
    + getCurrentPlayerIndexPosition() 
    + getWinner()
}

```

## Implementation of Core Methods 
```java 
public int roll() { 
    return random(0, 6)
}
```

```java 
public int getFinalPosition(pos){ 
    if snakes.containesKey(pos) { 
        finalpos = snakes.get(pos);
        return finalpos;
    }
    if ladders.containsKey(pos) { 
        finalpos = ladders.get(pos)
        return finalpos;        
    }
    return pos;
    
}
```
### `Game.rollAndMove `
```java 
public string rollAndMove() {
    if state == "FINISHED" {
        System.out.println("game over"); 
    }
    num = dice.roll() ; 
    currentplayer = players.get(currentPlayerIndex); 
    pos = currentPlayer.getPosition(); 
    newpos = pos + dice.roll();
    if newPos > board.getSize() {
        "stay at pos itself, do not move to newPos"
    }
    finalPos = board.getFinalPosition(newPosition)
     currentPlayer.setPosition(finalpos);       
    
    if finalPos == board.getSize() {
        winner = currentPlayer ; 
        state = FINISHED
        currentPLayer.getName() + " won".
    }
    
    currentPlayerIndex = (currentPlayerIndex + 1)% players.size();
    return currentPlayer.getName() + " rolled " + diceValue +
            " → moved to " + newPosition +
            " → final position " + finalPosition
    
}

```

