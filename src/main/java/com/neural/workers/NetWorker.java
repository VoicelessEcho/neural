package com.neural.workers;

import com.neural.game.GameGrid;
import com.neural.main.FitnessTracker;
import com.neural.main.NeuralNet;
import com.neural.neuron.NeuralOutput;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Virgis on 2017.06.25.
 */
public class NetWorker implements Runnable{
    public FitnessTracker fitnessTracker;
    public GameGrid grid;
    public int id;
    public AtomicInteger workerCount;

    public NetWorker(FitnessTracker fitnessTracker, GameGrid grid, int id, AtomicInteger workerCount) {
        this.fitnessTracker = fitnessTracker;
        this.grid = grid;
        this.id = id;
        this.workerCount = workerCount;
    }

    @Override
    public void run() {
        int networksCount = fitnessTracker.networks.size();

        int unitPerTeamCount = 1;
        grid.generateUnits(unitPerTeamCount);

        for (int i = 0; i < networksCount; i++) {
            for (int j = 0; j < networksCount; j++) {
                NeuralNet p1Net = fitnessTracker.networks.get(i);
                NeuralNet p2Net = fitnessTracker.networks.get(j);

                grid.resetGrid();
                grid.resetUnits();
                grid.generateUnits(unitPerTeamCount);

                int p1Score = 0;
                int p2Score = 0;
                boolean p1Stop = false;
                boolean p2Stop = false;

                while (!p1Stop && ! p2Stop){
                    if (!p1Stop){
                        NeuralOutput output  = p1Net.execute(grid.getP1Inputs());
                        int score = grid.validateAndExecuteP1Move(0, output.dir1, output.dir2);
                        p1Score += score;
                        p1Stop = score == 0;

                        if (!p1Stop){
                            if (p2Stop && grid.p1Score > grid.p2Score){
                                p1Stop = true;
                            }
                        }
                    }
                    if (!p2Stop){
                        NeuralOutput output  = p2Net.execute(grid.getP2Inputs());
                        int score = grid.validateAndExecuteP2Move(0, output.dir1, output.dir2);
                        p2Score += score;
                        p2Stop = score == 0;

                        if (!p2Stop){
                            if (p1Stop && grid.p2Score > grid.p1Score){
                                p2Stop = true;
                            }
                        }
                    }
                }

                if (grid.p1Score > grid.p2Score){
                    p1Score += 100;
                    p2Score -= 50;
                }
                else {
                    p1Score -= 50;
                    p2Score += 100;
                }

                fitnessTracker.scores.get(i).addAndGet(p1Score);
                fitnessTracker.scores.get(j).addAndGet(p2Score);

                //System.out.println("Map " + String.valueOf(id) + " game " + String.valueOf(i) + " " + String.valueOf(j) + " done...");
            }

        }

        System.out.println("Map " + String.valueOf(id) + " done...");
        workerCount.decrementAndGet();
    }
}
