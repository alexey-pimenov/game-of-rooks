package com.github.apimenov.game.of.rooks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Board {

    static final int SIZE = 8;

    private BoardCell[][] cells = new BoardCell[SIZE][SIZE];

    Board() {
        for (int x = 0; x < cells.length; x++) {
            BoardCell[] row = cells[x];
            for (int y = 0; y < row.length; y++) {
                row[y] = new BoardCell(x, y);
            }

        }

    }

    public BoardCell getCell(int x, int y) {
        return cells[x][y];
    }


    public List<BoardCell> getStartingCells(int numberOfCells) {
        List<BoardCell> cellList = Arrays.stream(cells).flatMap(Arrays::stream).collect(Collectors.toList());
        Collections.shuffle(cellList);
        return  new ArrayList<>(cellList.subList(0,numberOfCells));
    }


}
