package com.github.apimenov.game.of.rooks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Ладья ,которая собственно двигается по доске
 */
public class Rook implements Runnable{

    private static final int MAX_NUMBER_OF_MOVES = 50;
    private final int rookNumber;
    private final Board board;
    private final CyclicBarrier startBarrier;

    private BoardCell currentCell;

    private final Random random = new Random();

    public Rook(int rookNumber, BoardCell currentCell, Board board, CyclicBarrier barrier) {
        this.rookNumber = rookNumber;
        this.board = board;
        this.currentCell = currentCell;
        this.startBarrier = barrier;

    }


    @Override
    public void run() {
        System.out.println(this + ": prepared");
        try {
            currentCell.lock();
            startBarrier.await();
        }catch (InterruptedException | BrokenBarrierException e){
            //never happen
        }
        System.out.println(this + ": started");
        for (int i = 0; i < MAX_NUMBER_OF_MOVES; i++) {
            //Вычисляем направление и кол-во ходов
            Direction  currentDirection = generateNewDirection();
            int movesToTarget = generateNumberOfMoves(currentDirection);

            //движение к намеченной ячейке
            moveToTarget(currentDirection,movesToTarget);

            //Ждем после каждых двух ходов
            if (i %2 != 0){
                System.out.println(this + ": waiting");
                try {
                    Thread.sleep((long) (200+random.nextInt(100)));
                } catch (InterruptedException e) {
                    //swallow
                }
            }

        }
        currentCell.unlock();
        System.out.println(this+": finished");

    }

    /**
     * Движение к намеченной яйчейке в цикле
     * @param direction
     * @param numberOfMoves
     */
    private void moveToTarget(Direction direction,int numberOfMoves){
        while (numberOfMoves !=0){
            try {
                if(!tryToMove(direction)){
                    System.out.println(this+": target cell is locked");
                    return;
                }
                numberOfMoves--;
            } catch (InterruptedException e) {
               //should never happen
            }
        }
        System.out.println(this + ": Successfully arrived at target ");
    }

    /**
     * Попытка движения к ячейке
     * @param direction
     * @return
     * @throws InterruptedException
     */
    private boolean tryToMove(Direction direction) throws InterruptedException {
        BoardCell cell = nextCell(direction);
        System.out.println(this+": moving to:" + cell);
        if(!cell.lock()){
            return  false;
        }
        currentCell.unlock();
        currentCell = cell;
        return true;


    }

    private enum Direction{
        UP,DOWN,RIGHT,LEFT
    }

    /**
     * Метод возвращающий новую позицию для ладьи относительно текущей
     * @return
     */
    private Direction generateNewDirection() {
        // Очень по тупому определяем не уткнулась ли ладья в угол
        List<Direction> possibleDirections = new ArrayList<>(Arrays.asList(Direction.values()));
        if (currentCell.getX() == 0){
            possibleDirections.remove(Direction.LEFT);
        }else if(currentCell.getX() == (Board.SIZE-1)){
            possibleDirections.remove(Direction.RIGHT);
        }

        if (currentCell.getY() == 0){
            possibleDirections.remove(Direction.UP);
        }else if(currentCell.getY() == (Board.SIZE-1)){
            possibleDirections.remove(Direction.DOWN);
        }

       return possibleDirections.get(random.nextInt(possibleDirections.size()));




    }

    /**
     * Кол-во движения по направлению относительно текущей позиции
     * @param direction
     * @return
     */
    private int generateNumberOfMoves(Direction direction){
       switch (direction){
           case UP:
            return random.nextInt(currentCell.getY()) + 1;
           case DOWN:
            return random.nextInt(Board.SIZE - currentCell.getY() -1) + 1;
           case LEFT:
            return random.nextInt(currentCell.getX()) + 1;
           case RIGHT:
               return random.nextInt(Board.SIZE - currentCell.getX() -1) + 1;
           default:
             throw new IllegalArgumentException("Unknown direction");
        }

    }


    private BoardCell nextCell( Direction currentDirection){
       return targetCell(currentDirection,1);
    }

    private BoardCell targetCell( Direction currentDirection,int numberOfMoves){
        int delta = (currentDirection == Direction.UP || currentDirection == Direction.LEFT)? -1:1;
        delta *= numberOfMoves;

        if(currentDirection == Direction.LEFT || currentDirection == Direction.RIGHT){
            return board.getCell(currentCell.getX()+ delta,currentCell.getY());
        }else{
            return board.getCell(currentCell.getX(),currentCell.getY() + delta);
        }
    }


    @Override
    public String toString() {
        return "[Rook-"+rookNumber+"]" + currentCell;
    }
}
