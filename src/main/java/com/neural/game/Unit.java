package com.neural.game;

/**
 * Created by Virgis on 2017.06.25.
 */
public class Unit {
    public int x;
    public int y;

    public Unit(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Unit getClone(){
        return new Unit(x, y);
    }
}
