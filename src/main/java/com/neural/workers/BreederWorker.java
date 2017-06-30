package com.neural.workers;

import com.neural.main.NeuralGenerator;
import com.neural.main.NeuralNet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Virgis on 2017.06.28.
 */
public class BreederWorker implements Runnable {
    public List<NeuralNet> nets = new ArrayList<>();

    public List<NeuralNet> betsOfBest;
    public List<NeuralNet> allTimeBest;
    public NeuralGenerator generator;
    public AtomicInteger workerCount;

    public BreederWorker(List<NeuralNet> betsOfBest, List<NeuralNet> allTimeBest, NeuralGenerator generator, AtomicInteger workerCount) {
        this.betsOfBest = betsOfBest;
        this.allTimeBest = allTimeBest;
        this.generator = generator;
        this.workerCount = workerCount;
    }

    @Override
    public void run() {
        nets.addAll(generator.breedNewGeneration(betsOfBest));
        nets.addAll(generator.breedNewGeneration(allTimeBest));
        workerCount.decrementAndGet();
    }

    public List<NeuralNet> getNets() {
        return nets;
    }
}
