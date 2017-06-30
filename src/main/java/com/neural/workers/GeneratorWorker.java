package com.neural.workers;

import com.neural.main.NeuralGenerator;
import com.neural.main.NeuralNet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Virgis on 2017.06.28.
 */
public class GeneratorWorker implements Runnable{
    public List<NeuralNet> nets = new ArrayList<>();
    public int genCount = 10;
    public NeuralGenerator generator;
    public int generation;
    public AtomicInteger workerCount;

    public GeneratorWorker(int genCount, NeuralGenerator generator, int generation, AtomicInteger workerCount) {
        this.genCount = genCount;
        this.generator = generator;
        this.generation = generation;
        this.workerCount = workerCount;
    }

    @Override
    public void run() {
        for (int i = 0; i < genCount; i++) {
            nets.add(generator.generateRandomNet(generation));
            if (i % 100 == 0) {
                System.out.println("Generated net #" + String.valueOf(i));
            }
        }
        workerCount.decrementAndGet();
    }

    public List<NeuralNet> getNets() {
        return nets;
    }
}
