package com.javarush.task.task35.task3513;

import java.util.*;

public class Model {
    private static final int FIELD_WIDTH = 4;
    private Tile[][] gameTiles;
    int score;
    int maxTile;
    private Stack<Tile[][]> previousStates = new Stack<>();
    private Stack<Integer> previousScores = new Stack<>();
    private boolean isSaveNeeded = true;

    public Model() {
        resetGameTiles();
    }

    private void addTile() {
        List<Tile> emptyTiles = getEmptyTiles();
        if (emptyTiles.isEmpty()) return;
        Tile emptyTile = emptyTiles.get((int) (emptyTiles.size() * Math.random()));
        emptyTile.value = Math.random() < 0.9 ? 2 : 4;
    }

    private List<Tile> getEmptyTiles() {
        List<Tile> tiles = new ArrayList<>();
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                if (gameTiles[i][j].value == 0) tiles.add(gameTiles[i][j]);
            }
        }
        return tiles;
    }

    public void resetGameTiles() {
        gameTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                gameTiles[i][j] = new Tile();
            }
        }
        addTile();
        addTile();
    }

    private boolean compressTiles(Tile[] tiles) {
        int insertPosition = 0;
        boolean result = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (!tiles[i].isEmpty()) {
                if (i != insertPosition) {
                    tiles[insertPosition] = tiles[i];
                    tiles[i] = new Tile();
                    result = true;
                }
                insertPosition++;
            }
        }
        return result;
    }

    private boolean mergeTiles(Tile[] tiles) {
        Tile[] oldTiles = Arrays.copyOf(tiles, tiles.length);
        LinkedList<Tile> tileList = new LinkedList<>();
        boolean result = false;

        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (tiles[i].isEmpty()) continue;
            if (i < FIELD_WIDTH - 1 && tiles[i].value == tiles[i + 1].value) {
                int updatedValue = tiles[i].value * 2;
                if (maxTile < updatedValue) maxTile = updatedValue;
                score += updatedValue;
                tileList.addLast(new Tile(updatedValue));
                tiles[i + 1] = new Tile();
                result = true;
            } else {
                tileList.addLast(tiles[i]);
            }
            tiles[i] = new Tile();
        }

        for (int i = 0; i < tileList.size(); i++) {
            tiles[i] = tileList.get(i);
        }
        return result;
    }

    public boolean canMove() {
        if (!(getEmptyTiles().size() == 0)) return true;

        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                Tile tile = gameTiles[i][j];
                if ((i < FIELD_WIDTH - 1 && tile.value == gameTiles[i + 1][j].value) ||
                        (j < FIELD_WIDTH - 1 && tile.value == gameTiles[i][j + 1].value)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void left() {
        if (isSaveNeeded) saveState(gameTiles);
        boolean flag = false;
        for (int i = 0; i < FIELD_WIDTH; i++) {
            if (compressTiles(gameTiles[i]) || mergeTiles(gameTiles[i])) flag = true;
        }
        if (flag) addTile();
        isSaveNeeded = true;
    }

    public void down() {
        saveState(gameTiles);
        gameTiles = rotateClockWise(gameTiles);
        left();
        gameTiles = rotateClockWise(gameTiles);
        gameTiles = rotateClockWise(gameTiles);
        gameTiles = rotateClockWise(gameTiles);
    }
    public void right() {
        saveState(gameTiles);
        gameTiles = rotateClockWise(gameTiles);
        gameTiles = rotateClockWise(gameTiles);
        left();
        gameTiles = rotateClockWise(gameTiles);
        gameTiles = rotateClockWise(gameTiles);
    }

    public void up() {
        saveState(gameTiles);
        gameTiles = rotateClockWise(gameTiles);
        gameTiles = rotateClockWise(gameTiles);
        gameTiles = rotateClockWise(gameTiles);
        left();
        gameTiles = rotateClockWise(gameTiles);
    }

    private Tile[][] rotateClockWise(Tile[][] matrix) {
        int size = matrix.length;
        Tile[][] ret = new Tile[size][size];

        for (int i = 0; i < size; ++i)
            for (int j = 0; j < size; ++j)
                ret[i][j] = matrix[size - j - 1][i]; //***

        return ret;
    }

    public Tile[][] getGameTiles() {
        return gameTiles;
    }

    private void saveState(Tile[][] tiles) {
        Tile[][] tempTiles = new Tile[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH; j++) {
                tempTiles[i][j] = new Tile(tiles[i][j].value);
            }
        }
        previousStates.push(tempTiles);
        previousScores.push(score);
        isSaveNeeded = false;
    }

    public void rollback() {
        if (!previousStates.isEmpty() && !previousScores.isEmpty()) {
            gameTiles = previousStates.pop();
            score = previousScores.pop();
        }
    }
}
