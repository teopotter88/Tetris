package application;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class tetrisFinal extends Application {

	//Setting main elements for canvas and declaring variables
    public static final int TILE_SIZE = 40;
    public static final int GRID_WIDTH = 15;
    public static final int GRID_HEIGHT = 15;

    private double time;
    private GraphicsContext g;

    private int[][] grid = new int[GRID_WIDTH][GRID_HEIGHT];

    private List<pieceMove> original = new ArrayList<>();
    private List<pieceMove> kaans = new ArrayList<>();

    private pieceMove selected;
    Pane root = new Pane();

    private Parent createContent() {
        root.setPrefSize(GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE);
        //setting canvas size
        Canvas canvas = new Canvas(GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE);
        g = canvas.getGraphicsContext2D();

        //creating custom tiles through the methods from tile and pieceMove scripts
        root.getChildren().addAll(canvas);
        original.add(new pieceMove(Color.AQUA,
                new tiles(0, Direction.DOWN),
                new tiles(1, Direction.LEFT),
                new tiles(1, Direction.RIGHT),
                new tiles(2, Direction.RIGHT)
        ));
        original.add(new pieceMove(Color.PURPLE,
                new tiles(0, Direction.DOWN),
                new tiles(1, Direction.LEFT),
                new tiles(1, Direction.RIGHT),
                new tiles(1, Direction.DOWN)
        ));

        original.add(new pieceMove(Color.GREEN,
                new tiles(0, Direction.DOWN),
                new tiles(1, Direction.RIGHT),
                new tiles(2, Direction.RIGHT),
                new tiles(1, Direction.DOWN)));
       			

        original.add(new pieceMove(Color.GRAY,
                new tiles(0, Direction.DOWN),
                new tiles(1, Direction.RIGHT),
                new tiles(1, Direction.RIGHT, Direction.DOWN),
                new tiles(1, Direction.RIGHT, Direction.DOWN, Direction.RIGHT)));
        
        original.add(new pieceMove(Color.BLUE,
                new tiles(0, Direction.DOWN),
                new tiles(1, Direction.DOWN),
                new tiles(2, Direction.DOWN),
                new tiles(1, Direction.LEFT, Direction.DOWN, Direction.DOWN),
        		new tiles(2, Direction.LEFT, Direction.DOWN)));


        original.add(new pieceMove(Color.RED,
                new tiles(0, Direction.DOWN),
                new tiles(1, Direction.RIGHT),
                new tiles(1, Direction.RIGHT, Direction.DOWN),
                new tiles(1, Direction.DOWN)));
        
        original.add(new pieceMove(Color.PINK,
        		new tiles(1, Direction.DOWN),
        		new tiles(2, Direction.DOWN),
        		new tiles(3, Direction.DOWN),
        		new tiles(4, Direction.DOWN)
        		));
        
       original.add(new pieceMove(Color.BLACK,
    		   new tiles(0, Direction.DOWN),
       		   new tiles(1, Direction.DOWN),
       		   new tiles(1, Direction.DOWN, Direction.RIGHT)
    		   ));
       
       original.add(new pieceMove(Color.BROWN,
    		   new tiles(0, Direction.DOWN),
    		   new tiles(1, Direction.RIGHT),
    		   new tiles(1, Direction.DOWN),
    		   new tiles(2, Direction.DOWN),
    		   new tiles(1, Direction.LEFT)
    		   ));

       spawn();

       //Tile falling animation updating every 0.5 seconds
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                time += 0.017;

                if (time >= 0.5) {
                    update();
                    render();
                    time = 0;
                }
            }
        };
        timer.start();

        return root;
    }

    private void update() {
        makeMove(p -> p.move(Direction.DOWN), p -> p.move(Direction.UP), true);
    }

    private void render() {
        g.clearRect(0, 0, GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE);

        kaans.forEach(p -> p.draw(g));
    }

    //tile falling to the bottom of the grid
    private void placePiece(tiles piece) {
        grid[piece.x][piece.y]++;
    }

    //tile being removed from the grid
    private void removePiece(tiles piece) {
        grid[piece.x][piece.y]--;
    }

    //checking tile position so it doesn't go off screen
    private boolean isOffscreen(tiles piece) {
        return piece.x < 0 || piece.x >= GRID_WIDTH
                || piece.y < 0 || piece.y >= GRID_HEIGHT;
    }

    //if tile reached bottom limit, cancel movement
    private void makeMove(Consumer<pieceMove> onSuccess, Consumer<pieceMove> onFail, boolean endMove) {
        selected.pieces.forEach(this::removePiece);

        onSuccess.accept(selected);

        boolean offscreen = selected.pieces.stream().anyMatch(this::isOffscreen);

        if (!offscreen) {
            selected.pieces.forEach(this::placePiece);
        } else {
            onFail.accept(selected);

            selected.pieces.forEach(this::placePiece);

            if (endMove) {
                sweep();
            }

            return;
        }

        if (!isValidState()) {
            selected.pieces.forEach(this::removePiece);

            onFail.accept(selected);

            selected.pieces.forEach(this::placePiece);

            if (endMove) {
                sweep();
            }
        }
    }
    
    //check y & x coordinates of tiles
    private boolean isValidState() {
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                if (grid[x][y] > 1) {
                    return false;
                }
            }
        }

        return true;
    }

    //deleting tiles that create a row
    private void sweep() {
        List<Integer> rows = sweepRows();
        rows.forEach(row -> {
            for (int x = 0; x < GRID_WIDTH; x++) {
                for (pieceMove kaan : kaans) {
                    kaan.detach(x, row);
                }

                grid[x][row]--;
            }
        });

        rows.forEach(row -> {
            kaans.stream().forEach(kaan -> {
                kaan.pieces.stream()
                        .filter(piece -> piece.y < row)
                        .forEach(piece -> {
                            removePiece(piece);
                            piece.y++;
                            placePiece(piece);
                        });
            });
        });

        spawn();
    }

    //method for row deleting
    private List<Integer> sweepRows() {
        List<Integer> rows = new ArrayList<>();

        outer:
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                if (grid[x][y] != 1) {
                    continue outer;
                }
            }

            rows.add(y);
        }

        return rows;
    }

    //spawning new random tile from top when a tile stops moving
    private void spawn() {
        pieceMove kaan = original.get(new Random().nextInt(original.size())).copy();
        kaan.move(GRID_WIDTH / 2, 0);

        selected = kaan;

        kaans.add(kaan);
        kaan.pieces.forEach(this::placePiece);

        if (!isValidState()) {
            System.out.println("Game Over");
            System.exit(0);
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(createContent());

        //keyboard input rotation and movement of tiles
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.R) {
                makeMove(p -> p.rotate(), p -> p.rotateBack(), false);
            } else if (e.getCode() == KeyCode.LEFT) {
                makeMove(p -> p.move(Direction.LEFT), p -> p.move(Direction.RIGHT), false);
            } else if (e.getCode() == KeyCode.RIGHT) {
                makeMove(p -> p.move(Direction.RIGHT), p -> p.move(Direction.LEFT), false);
            } else if (e.getCode() == KeyCode.DOWN) {
                makeMove(p -> p.move(Direction.DOWN), p -> p.move(Direction.UP), true);
            }

            render();
        });

        stage.setScene(scene);
        stage.show();
    }
    
    //launch the application
    public static void main(String[] args) {
        launch(args);
    }
}