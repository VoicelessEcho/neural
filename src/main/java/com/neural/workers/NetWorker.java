package com.neural.workers;

import com.neural.game.GameGrid;
import com.neural.main.FitnessTracker;
import com.neural.main.NeuralNet;
import com.neural.neuron.NeuralOutput;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Virgis on 2017.06.25.
 */
public class NetWorker implements Runnable{
    public FitnessTracker fitnessTracker;
    public GameGrid grid;
    public int id;
    public AtomicInteger workerCount;
    public List<NeuralNet> prevBest;

    public NetWorker(FitnessTracker fitnessTracker, GameGrid grid, int id, AtomicInteger workerCount, List<NeuralNet> prevBest) {
        this.fitnessTracker = fitnessTracker;
        this.grid = grid;
        this.id = id;
        this.workerCount = workerCount;
        this.prevBest = prevBest;
    }

    @Override
    public void run() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        int networksCount = fitnessTracker.networks.size();
        System.out.println("Networks count: " + String.valueOf(networksCount));
        int unitPerTeamCount = 1;
        grid.generateUnits(unitPerTeamCount);

        for (int i = 0; i < networksCount; i++) {
            for (int j = 0; j < 10; j++) {
                NeuralNet p1Net = fitnessTracker.networks.get(i);
                NeuralNet p2Net = null;
                int index = 0;
                if (prevBest != null && prevBest.size() != 0){
                    index = random.nextInt(prevBest.size());
                    p2Net = prevBest.get(index);
                }
                else {
                    index = random.nextInt(fitnessTracker.getNetworks().size());
                    p2Net = fitnessTracker.getNetworks().get(index);
                }

                grid.resetGrid();
                grid.resetUnits();
                grid.generateUnits(unitPerTeamCount);

                int p1Score = 0;
                int p2Score = 0;
                boolean p1Stop = false;
                boolean p2Stop = false;

                while (!p1Stop) {
                    if (j % 2 == 0) {
                        if (!p1Stop) {
                            NeuralOutput[] output = p1Net.execute(grid.getP1Inputs());
                            int score = grid.validateAndExecuteP1Move(0, output[0].dir1, output[1].dir2);
                            p1Score += score;
                            p1Stop = score == 0;

                            if (!p1Stop) {
                                if (p2Stop && grid.p1Score > grid.p2Score) {
                                    p1Stop = true;
                                }
                            }
                        }
                        if (!p2Stop) {
                            NeuralOutput[] output = p2Net.execute(grid.getP2Inputs());
                            NeuralOutput[] moveOut = new NeuralOutput[output.length/2];
                            NeuralOutput[] buildOut = new NeuralOutput[output.length/2];
                            for (int k = 0; k < moveOut.length; k++) {
                                moveOut[k] = output[k];
                                buildOut[k] = output[k + moveOut.length/2];
                            }

                            int score = grid.validateAndExecuteP2Move(0, moveOut, buildOut);
                            p2Score += score;
                            p2Stop = score == 0;

                            /*if (!p2Stop) {
                                if (p1Stop && grid.p2Score > grid.p1Score) {
                                    p2Stop = true;
                                }
                            }*/
                        }
                    }
                    else {
                        if (!p2Stop) {
                            NeuralOutput[] output = p2Net.execute(grid.getP2Inputs());
                            NeuralOutput[] moveOut = new NeuralOutput[output.length/2];
                            NeuralOutput[] buildOut = new NeuralOutput[output.length/2];
                            for (int k = 0; k < moveOut.length; k++) {
                                moveOut[k] = output[k];
                                buildOut[k] = output[k + moveOut.length/2];
                            }

                            int score = grid.validateAndExecuteP2Move(0, moveOut, buildOut);
                            p2Score += score;
                            p2Stop = score == 0;

                            if (!p2Stop) {
                                if (p1Stop && grid.p2Score > grid.p1Score) {
                                    p2Stop = true;
                                }
                            }
                        }
                        if (!p1Stop) {
                            NeuralOutput[] output = p1Net.execute(grid.getP1Inputs());
                            int score = grid.validateAndExecuteP1Move(0, output[0].dir1, output[1].dir2);
                            p1Score += score;
                            p1Stop = score == 0;

                            /*if (!p1Stop) {
                                if (p2Stop && grid.p1Score > grid.p2Score) {
                                    p1Stop = true;
                                }
                            }*/
                        }
                    }
                }

                if (grid.p1Score > grid.p2Score){
                    p1Score += 100;
                }

                fitnessTracker.scores.get(i).addAndGet(p1Score);

                //System.out.println("Map " + String.valueOf(id) + " game " + String.valueOf(i) + " " + String.valueOf(j) + " done...");
            }

        }

        System.out.println("Map " + String.valueOf(id) + " done...");
        workerCount.decrementAndGet();
    }
}
