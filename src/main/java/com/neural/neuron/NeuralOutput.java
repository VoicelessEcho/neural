package com.neural.neuron;

import com.neural.main.NeuralNet;

import java.io.Serializable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Virgis on 2017.06.24.
 */
public class NeuralOutput extends NeuralNode implements Serializable{
    public NeuralNet.Dir dir1;
    public NeuralNet.Dir dir2;

    public NeuralOutput(NeuralNet.Dir dir1, NeuralNet.Dir dir2) {
        super();
        this.dir1 = dir1;
        this.dir2 = dir2;
    }

    public NeuralOutput() {
        super();
        this.dir1 = null;
        this.dir2 = null;
    }

    public NeuralNet.Dir getDir1() {
        return dir1;
    }

    public void setDir1(NeuralNet.Dir dir1) {
        this.dir1 = dir1;
    }

    public NeuralNet.Dir getDir2() {
        return dir2;
    }

    public void setDir2(NeuralNet.Dir dir2) {
        this.dir2 = dir2;
    }

    public NeuralNode cloneNode() {
        NeuralNode node = new NeuralOutput(dir1, dir2);
        for (int input : inputs) {
            int index = input;
            double weight = inWeights.get(input);
            node.inputs.add(input);
            node.inWeights.put(index, weight);
        }
        return node;
    }
}
