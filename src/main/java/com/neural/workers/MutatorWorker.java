package com.neural.workers;

import com.neural.main.NeuralGenerator;
import com.neural.main.NeuralNet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Virgis on 2017.06.28.
 */
public class MutatorWorker implements Runnable {
    public List<NeuralNet> nets = new ArrayList<>();


    public List<NeuralNet> bestOfBest;
    public NeuralGenerator generator;
    public AtomicInteger workerCount;

    public MutatorWorker(List<NeuralNet> bestOfBest, NeuralGenerator generator, AtomicInteger workerCount) {
        this.bestOfBest = bestOfBest;
        this.generator = generator;
        this.workerCount = workerCount;
    }

    @Override
    public void run() {
        nets.addAll(generator.mutateGeneration(bestOfBest));
        workerCount.decrementAndGet();
    }

    public List<NeuralNet> getNets() {
        return nets;
    }
}
