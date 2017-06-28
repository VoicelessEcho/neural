package com.neural.game;

import com.neural.main.NeuralNet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Virgis on 2017.06.25.
 */
public class GameGrid {
    public List<Integer> legalXStart_ = new ArrayList<>();
    public List<Integer> legalYStart_ = new ArrayList<>();

    public List<Unit> p1Units = new ArrayList<>();
    public List<Unit> p2Units = new ArrayList<>();

    public int[][] startingGrid;
    public int[][] grid;
    public int maxEmptyCellsPercent = 10;
    public int size;

    public List<Integer> legalXStart = new ArrayList<>();
    public List<Integer> legalYStart = new ArrayList<>();

    public int p1Score = 0;
    public int p2Score = 0;

    public void generateGrid(int s, String[] gridStrings){
        legalXStart.clear();
        legalYStart.clear();

        grid = new int[7][7];
        for (int y = 0; y < gridStrings.length; y++) {
            String row = gridStrings[y];

            for (int x = 0; x < gridStrings.length; x++) {
                Character c = row.charAt(x);
                int h = -1;
                switch (c){
                    case '.':
                        break;
                    default:
                        h = Integer.parseInt("" + c);
                }
                grid[x][y] = h;
                if (h > -1 && h < 4){
                    legalXStart.add(x);
                    legalYStart.add(y);
                }
            }
        }

        if (s < 7){
            for (int x = s; x < 7; x++) {
                for (int y = s; y < 7; y++) {
                    grid[x][y] = -1;
                }
            }
        }

        legalXStart_.addAll(legalXStart);
        legalYStart_.addAll(legalYStart);

        startingGrid = copyGrid(grid);
    }

    public void generateGrid(){
        legalXStart.clear();
        legalYStart.clear();

        ThreadLocalRandom random = ThreadLocalRandom.current();
        size = random.nextInt(5, 8);

        grid = new int[7][7];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int cell = random.nextInt(100);
                if (cell > maxEmptyCellsPercent - 1){
                    grid[x][y] = 0;
                    legalXStart.add(x);
                    legalYStart.add(y);
                }
                else {
                    grid[x][y] = -1;
                }
            }
            for (int y = size; y < 7; y++) {
                grid[x][y] = -1;
            }
        }
        for (int x = size; x < 7; x++) {
            for (int y = 0; y < 7; y++) {
                grid[x][y] = -1;
            }
        }

        legalXStart_.addAll(legalXStart);
        legalYStart_.addAll(legalYStart);

        startingGrid = copyGrid(grid);
    }

    public void generateUnits(int unitPerTeamCount){
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < unitPerTeamCount; i++) {
            Unit p1Unit = null;
            int index = random.nextInt(legalXStart.size());
            p1Unit = new Unit(legalXStart.remove(index), legalYStart.remove(index));
            p1Units.add(p1Unit);

            Unit p2Unit = null;
            index = random.nextInt(legalXStart.size());
            p2Unit = new Unit(legalXStart.remove(index), legalYStart.remove(index));
            p2Units.add(p2Unit);
        }


    }

    public int[] getP1Inputs(){
        int[][] currentGrid = copyGrid(grid);
        for (int i = 0; i < p1Units.size(); i++) {
            Unit p1Unit = p1Units.get(i);
            Unit p2Unit = p2Units.get(i);

            currentGrid[p1Unit.x][p1Unit.y] = currentGrid[p1Unit.x][p1Unit.y] + 10;
            currentGrid[p2Unit.x][p2Unit.y] = currentGrid[p2Unit.x][p2Unit.y] - 15;
        }
        int[] arrGrid = getGridAsArray(currentGrid);
        int[] arrP1 = new int[arrGrid.length + 1];
        arrP1[0] = p1Score + 100;
        arrP1[1] = p2Score + 100;
        for (int i = 0; i < arrGrid.length - 2; i++) {
            arrP1[i + 2] = arrGrid[i];
        }
        return arrP1;
    }

    public int[][] getP1Grid(){
        int[][] currentGrid = copyGrid(grid);
        for (int i = 0; i < p1Units.size(); i++) {
            Unit p1Unit = p1Units.get(i);
            Unit p2Unit = p2Units.get(i);

            currentGrid[p1Unit.x][p1Unit.y] = currentGrid[p1Unit.x][p1Unit.y] + 10;
            currentGrid[p2Unit.x][p2Unit.y] = currentGrid[p2Unit.x][p2Unit.y] - 15;
        }
        return currentGrid;
    }

    public int[][] getP2Grid(){
        int[][] currentGrid = copyGrid(grid);
        for (int i = 0; i < p2Units.size(); i++) {
            Unit p1Unit = p1Units.get(i);
            Unit p2Unit = p2Units.get(i);

            currentGrid[p1Unit.x][p1Unit.y] = currentGrid[p1Unit.x][p1Unit.y] - 15;
            currentGrid[p2Unit.x][p2Unit.y] = currentGrid[p2Unit.x][p2Unit.y] + 10;
        }
        return currentGrid;
    }

    public int[] getP2Inputs(){
        int[][] currentGrid = copyGrid(grid);
        for (int i = 0; i < p1Units.size(); i++) {
            Unit p1Unit = p1Units.get(i);
            Unit p2Unit = p2Units.get(i);

            currentGrid[p1Unit.x][p1Unit.y] = currentGrid[p1Unit.x][p1Unit.y] - 15;
            currentGrid[p2Unit.x][p2Unit.y] = currentGrid[p2Unit.x][p2Unit.y] + 10;
        }
        int[] arrGrid = getGridAsArray(currentGrid);
        int[] arrP2 = new int[arrGrid.length + 1];
        arrP2[0] = p2Score + 100;
        arrP2[1] = p1Score + 100;
        for (int i = 0; i < arrGrid.length - 2; i++) {
            arrP2[i + 2] = arrGrid[i];
        }
        return arrP2;
    }


    public int[] getGridAsArray(int[][] grid){
        int[] arr = new int[49];
        int i = 0;
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 7; y++) {
                arr[i] = grid[x][y];
                i++;
            }
        }
        return arr;
    }

    private int[][] copyGrid(int[][] grid) {
        int[][] copy = new int[7][7];
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 7; y++) {
                copy[x][y] = grid[x][y];
            }
        }
        return copy;
    }

    public void resetGrid(){
        grid = copyGrid(startingGrid);
        legalXStart.clear();
        legalYStart.clear();
        legalXStart.addAll(legalXStart_);
        legalYStart.addAll(legalYStart_);
        p1Score = 0;
        p2Score = 0;
    }

    public void resetUnits(){
        p1Units.clear();
        p2Units.clear();
    }

    public String cellsView(int[][] grid){
        String sep = "+";
        int size = 7;
        for (int i = 0; i < size; i++) {
            sep += "---+";
        }
        sep += "\n";

        String[] sarr = new String[size];
        Arrays.fill(sarr, "|");
        ArrayList<String> strList = new ArrayList<>();
        strList.addAll(Arrays.asList(sarr));

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                String s = strList.get(y);
                String tok = "" + String.valueOf(grid[x][y]) + " |";
                if (tok.length() < 4){
                    tok = " " + tok;
                }



                s += tok;
                strList.remove(y);
                strList.add(y, s);
            }
        }
        String lines = sep;
        for (String line : strList) {
            lines += line + "\n" + sep;
        }
        System.out.println(lines);
        return lines;
    }

    public boolean unitExist(int x, int y, List<Unit> units){
        for (Unit unit : units) {
            if (unit.x == x && unit.y == y){
                return true;
            }
        }
        return false;
    }

    public int validateAndExecuteP1Move(int unitIndex, NeuralNet.Dir dir1, NeuralNet.Dir dir2){
        int score = 0;
        Unit unit = p1Units.get(unitIndex);

        int x = unit.x;
        int y = unit.y;

        int dir1H, dir2H;

        int xDir1 = dirX(x, dir1);
        int yDir1 = dirY(y, dir1);

        //are coordinates in game area?
        if (!isValidCoordinates(xDir1, yDir1)){
            return score;
        }

        //is move legal?
        if (!legalMovePosition(xDir1, yDir1, grid[x][y])){
            return score;
        }

        //is position empty?
        if (!nonOccupiedPosition(xDir1, yDir1, p1Units) || !nonOccupiedPosition(xDir1, yDir1, p2Units)){
            return score;
        }

        int xDir2 = dirX(xDir1, dir2);
        int yDir2 = dirY(yDir1, dir2);

        //are coordinates in game area?
        if (!isValidCoordinates(xDir2, yDir2)){
            return score;
        }
        //is available to build?
        if (!legalBuildPosition(xDir2, yDir2)){
            return score;
        }

        //is occupied by enemies?
        if (!nonOccupiedPosition(xDir2, yDir2, p2Units)){
            return score;
        }

        //is occupied by teamMates?
        if (!nonOccupiedPosition(xDir2, yDir2, p1Units)){
            //is occupied by self?
            if (xDir2 != x || yDir2 != y){
                return score;
            }
        }

        dir1H = grid[xDir1][yDir1];
        dir2H = grid[xDir2][yDir2];

        score += (dir1H * 2) + 1;
        if (dir1H == 3){
            score += 10;
            p1Score = p1Score + 1;
        }
        if (dir2H < 3) {
            score += dir2H + 1;
        }

        unit.x = xDir1;
        unit.y = yDir1;

        grid[xDir2][yDir2] = grid[xDir2][yDir2] + 1;

        return score;
    }

    public int validateAndExecuteP2Move(int unitIndex, NeuralNet.Dir dir1, NeuralNet.Dir dir2){
        int score = 0;
        Unit unit = p2Units.get(unitIndex);

        int x = unit.x;
        int y = unit.y;

        int dir1H, dir2H;

        int xDir1 = dirX(x, dir1);
        int yDir1 = dirY(y, dir1);

        //are coordinates in game area?
        if (!isValidCoordinates(xDir1, yDir1)){
            return score;
        }

        //is move legal?
        if (!legalMovePosition(xDir1, yDir1, grid[x][y])){
            return score;
        }

        //is position empty?
        if (!nonOccupiedPosition(xDir1, yDir1, p2Units) || !nonOccupiedPosition(xDir1, yDir1, p1Units)){
            return score;
        }

        int xDir2 = dirX(xDir1, dir2);
        int yDir2 = dirY(yDir1, dir2);

        //are coordinates in game area?
        if (!isValidCoordinates(xDir2, yDir2)){
            return score;
        }
        //is available to build?
        if (!legalBuildPosition(xDir2, yDir2)){
            return score;
        }

        //is occupied by enemies?
        if (!nonOccupiedPosition(xDir2, yDir2, p1Units)){
            return score;
        }

        //is occupied by teamMates?
        if (!nonOccupiedPosition(xDir2, yDir2, p2Units)){
            //is occupied by self?
            if (xDir2 != x || yDir2 != y){
                return score;
            }
        }

        dir1H = grid[xDir1][yDir1];
        dir2H = grid[xDir2][yDir2];

        score += (dir1H * 2) + 1;
        if (dir1H == 3){
            score += 10;
            p2Score = p2Score + 1;
        }
        if (dir2H < 3) {
            score += dir2H + 1;
        }

        unit.x = xDir1;
        unit.y = yDir1;

        return score;
    }

    private boolean isValidCoordinates(int xDir1, int yDir1) {
        if (xDir1 < 0 || yDir1 < 0){
            return false;
        }
        if (xDir1 > 6 || yDir1 > 6){
            return false;
        }
        return true;
    }

    public int dirX(int x, NeuralNet.Dir dir){
        int newX = -1;
        switch (dir){
            case N:
                newX = x;
                break;
            case W:
                newX = x - 1;
                break;
            case E:
                newX = x + 1;
                break;
            case S:
                newX = x;
                break;
            case NW:
                newX = x - 1;
                break;
            case NE:
                newX = x + 1;
                break;
            case SW:
                newX = x - 1;
                break;
            case SE:
                newX = x + 1;
                break;
        }
        return newX;
    }

    public int dirY(int y, NeuralNet.Dir dir){
        int newY = -1;
        switch (dir){
            case N:
                newY = y - 1;
                break;
            case W:
                newY = y;
                break;
            case E:
                newY = y;
                break;
            case S:
                newY = y + 1;
                break;
            case NW:
                newY = y - 1;
                break;
            case NE:
                newY = y - 1;
                break;
            case SW:
                newY = y + 1;
                break;
            case SE:
                newY = y + 1;
                break;
        }
        return newY;
    }

    /**
     *
     * @param x
     * @param y
     * @return true if position h >= 0 && < 4
     */
    public boolean legalMovePosition(int x, int y, int currentH){
        boolean legalMove = false;
        //if in playable h
        if (grid[x][y] >= 0 && grid[x][y] < 4){
            int gridH = grid[x][y];
            //if gridH id greater then current
            if (gridH > currentH){
                if (gridH - currentH == 1){
                    legalMove = true;
                }
                else {
                    legalMove = false;
                }
            }
            //going down or to the same level
            else {
                legalMove = true;
            }
        }
        else {
            legalMove = false;
        }


        return legalMove;
    }

    /**
     *
     * @param x
     * @param y
     * @param units
     * @return returns true if no unit is present in x,y position
     */
    public boolean nonOccupiedPosition(int x, int y, List<Unit> units){
        for (Unit unit : units ) {
            if (unit.x == x && unit.y == y){
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param x
     * @param y
     * @return true if position h >= 0 && < 4
     */
    public boolean legalBuildPosition(int x, int y){
        return grid[x][y] >= 0 && grid[x][y] < 4;
    }
}
