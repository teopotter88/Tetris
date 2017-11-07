package application;

import java.util.Arrays;
import java.util.List;

public class tiles {

	//setting variables
    public int distance;
    public List<Direction> directions;
    public pieceMove parent;
    public int x, y;

    //the tile constructor, which helps us create tiles in tetrisFinal script
    public tiles(int distance, Direction... direction) {
        this.distance = distance;
        this.directions = Arrays.asList(direction);
    }

    //general parent for tiles which grants movement
    public void setParent(pieceMove parent) {
        this.parent = parent;

        int dx = 0, dy = 0;

        for (Direction d : directions) {
            dx += distance * d.x;
            dy += distance * d.y;
        }

        x = parent.x + dx;
        y = parent.y + dy;
    }
    
    //setting directions for tiles
    public void setDirection(Direction... direction) {
        this.directions = Arrays.asList(direction);

        int dx = 0, dy = 0;

        for (Direction d : directions) {
            dx += distance * d.x;
            dy += distance * d.y;
        }

        x = parent.x + dx;
        y = parent.y + dy;
    }

    //copying tiles and thus spawning them with spawn() method
    public tiles copy() {
        return new tiles(distance, directions.toArray(new Direction[0]));
    }
}
