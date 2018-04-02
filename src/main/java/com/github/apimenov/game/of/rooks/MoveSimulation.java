package com.github.apimenov.game.of.rooks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class MoveSimulation {

    private final ExecutorService executor;

    private final List<Rook> rooks;


    public MoveSimulation(int numberOfRooks){
        executor = Executors.newFixedThreadPool(numberOfRooks);
        rooks = new ArrayList<>(numberOfRooks);
        Board board = new Board();
        CyclicBarrier barrier = new CyclicBarrier(numberOfRooks);
        List<BoardCell> startingCells = board.getStartingCells(numberOfRooks);
        for (int i = 0; i < numberOfRooks; i++) {
            rooks.add(new Rook(i,startingCells.get(i), board,barrier));
        }
    }

    public void run() throws InterruptedException {
        List<Callable<Object>> tasks = rooks.stream()
                .map(Executors::callable)
                .collect(Collectors.toList());

        executor.invokeAll(tasks);
        executor.shutdownNow();
    }

    public static void main(String[] args) throws InterruptedException {
        new MoveSimulation(6).run();
    }
}
