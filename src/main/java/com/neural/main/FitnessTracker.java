package com.neural.main;

import javafx.collections.transformation.SortedList;

import java.io.Serializable;
import java.util.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Virgis on 2017.06.25.
 */
public class FitnessTracker implements Serializable{
    public List<NeuralNet> networks = null;
    public List<AtomicInteger> scores = null;

    public FitnessTracker(List<NeuralNet> networks, List<AtomicInteger> scores) {
        this.networks = networks;
        this.scores = scores;
    }

    public FitnessTracker() {
        networks = new ArrayList<>();
        scores = new ArrayList<>();
    }

    public List<NeuralNet> getNetworks() {
        return networks;
    }

    public void setNetworks(List<NeuralNet> networks) {
        this.networks = networks;
    }

    public List<AtomicInteger> getScores() {
        return scores;
    }

    public void setScores(List<AtomicInteger> scores) {
        this.scores = scores;
    }

    public void incrementScore(int index, int delta){
        scores.get(index).addAndGet(delta);
    }

    public void resetScores() {
        scores.clear();
        for (int i = 0; i < networks.size(); i++) {
            scores.add(new AtomicInteger(0));
        }
    }

    public List<NeuralNet> getBestNetworks(int count) {
        Map<Integer, List<NeuralNet>> neuralNets = new HashMap<>();
        List<Integer> unsortedScores = new ArrayList<>();
        for (int i = 0; i < this.scores.size(); i++) {
            AtomicInteger score = scores.get(i);
            List<NeuralNet> nets = neuralNets.get(score.get());
            if (nets == null){
                nets = new ArrayList<>();
                neuralNets.put(score.get(), nets);
            }
            nets.add(this.networks.get(i));
            unsortedScores.add(score.get());
        }

        Collections.sort(unsortedScores);

        List<NeuralNet> bestNets = new ArrayList<>();
        for (int j = 0; j < count; j++) {
            int score = unsortedScores.remove(unsortedScores.size() - 1);
            List<NeuralNet> nets = neuralNets.get(score);
            NeuralNet n = nets.remove(0);
            n.score = score;
            bestNets.add(n);
        }

        return bestNets;
    }
}
