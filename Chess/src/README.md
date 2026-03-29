# Design Chess

## Requirements 
- Design this popular board game which has 8x8 cells on the board. 
- and initially the board has 2 players playerwhite and player black
- Each player has 8 pawns , and 2 rooks, 2 knights, 2 bishops, 1 queen and 1 king. 
- Each of pieces have their own rules for moving. 
- if 2 different colors end up on the same position then whose ever turn was last will capture the earlier piece which was on that position. 
- The game ends in a draw when there is no checkmate possible by both sides. 
- The game ends with a winner when one player makes sure that the king is being attacked fom all ends in al positions and king cant move. 
- It is rarely impossible to design the entire thing in an interview in one hour, so we can ignore the checkmate logic, stalemate logic, 
- and ust design a simple way to initialize a board, make moves, switch turns,, they will be expecting a proper class hierarchy and logic for the pieces.. 
- 

## Entities 
- Gmame (orchestrator for turn order, move execution, checmate detection)
- Board 
- Players - PlayerWhite, PlayerBlack 
- Piece (rook, knight, bishop, queen , king, pawn)
- Cell (row, col object)
- Move - value object src cell, and destination cell .

## Relationships
Game has a board, players, 
Board has pieces
Piece has Colors 

## Class Design 
