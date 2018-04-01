package com.github.apimenov.game.of.rooks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoardCell {

    private final Lock lock = new ReentrantLock(true);

    private final int x;

    private final int y;

    public BoardCell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean lock() throws InterruptedException {
       return lock.tryLock(5, TimeUnit.SECONDS);
    }

    public void unlock(){
        lock.unlock();
    }

    @Override
    public String toString() {
        return "[cell "+x+","+y+"]";
    }


}
