package application;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import static application.tetrisFinal.TILE_SIZE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class pieceMove {

	//setting variables
    public int x, y;

    public Color color;

    public List<tiles> pieces;

   //setting tile characteristics
    public pieceMove(Color color, tiles... pieces) {
        this.color = color;
        this.pieces = new ArrayList<>(Arrays.asList(pieces));

        for (tiles piece : this.pieces)
            piece.setParent(this);
    }

    //tile movement, change in position
    public void move(int dx, int dy) {
        x += dx;
        y += dy;

        pieces.forEach(p -> {
            p.x += dx;
            p.y += dy;
        });
    }

    //movement in both axes
    public void move(Direction direction) {
        move(direction.x, direction.y);
    }

    //physical properties of tile being displayed graphically
    public void draw(GraphicsContext g) {
        g.setFill(color);

        pieces.forEach(p -> g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE));
    }

    //90 degree rotation of tile method
    public void rotateBack() {
        pieces.forEach(p -> p.setDirection(p.directions.stream().map(Direction::prev).collect(Collectors.toList()).toArray(new Direction[0])));
    }

    //90 degree rotation to original position
    public void rotate() {
        pieces.forEach(p -> p.setDirection(p.directions.stream().map(Direction::next).collect(Collectors.toList()).toArray(new Direction[0])));
    }
    
    //removing, detaching pieces from the tiles
    public void detach(int x, int y) {
        pieces.removeIf(p -> p.x == x && p.y == y);
    }

    //copying pieces
    public pieceMove copy() {
        return new pieceMove(color, pieces.stream()
                .map(tiles::copy)
                .collect(Collectors.toList())
                .toArray(new tiles[0]));
    }
}
