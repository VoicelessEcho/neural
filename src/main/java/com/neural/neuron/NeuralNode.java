package com.neural.neuron;

import com.neural.main.NeuralNet;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Virgis on 2017.06.24.
 */
public class NeuralNode implements Serializable{
    public Set<Integer> inputs;
    public Map<Integer, Float> inWeights;

    public NeuralNode(Set<Integer> inputs, Map<Integer, Float> inWeights) {
        this.inputs = inputs;
        this.inWeights = inWeights;
    }

    public NeuralNode() {
        inputs = new HashSet<>();
        inWeights = new HashMap<>();
    }

    public Set<Integer> getInputs() {
        return inputs;
    }

    public void setInputs(Set<Integer> inputs) {
        this.inputs = inputs;
    }

    public Map<Integer, Float> getInWeights() {
        return inWeights;
    }

    public void setInWeights(Map<Integer, Float> inWeights) {
        this.inWeights = inWeights;
    }

    public float computeOutputs(NeuralNet net, float[] inputs) {
        try {
            float out = 0;
            for (Integer index : this.inputs) {
                float in = inputs[index];
                float inW = inWeights.get(index);
                out += in * inW;
            }
            return out;
        }
        catch (Exception e){
            NeuralNode n = this;
            e.printStackTrace();

        }
        return 0;
    }

    public NeuralNode cloneNode() {
        NeuralNode node = new NeuralNode();
        Set<Integer> nodeInputs = new HashSet<>();
        Map<Integer, Float> nodeW = new HashMap<>();
        for (int input : inputs) {
            int index = new Integer(input);
            float weight = new Float(inWeights.get(input));

            nodeInputs.add(index);
            nodeW.put(index, weight);
        }
        node.inputs = nodeInputs;
        node.inWeights = nodeW;

        return node;
    }

    public void normalise(int prevCount) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int newWPercent = 1;
        int extinctPercent = 50;

        List<Integer> inputs_ = new ArrayList<>();
        inputs_.addAll(inputs);

        for (int i = 0; i < inputs_.size(); i++) {
            int input = inputs_.get(i);
            if (input < prevCount){
                int newWTest = random.nextInt(1, 100);
                if (newWTest <= newWPercent){
                    inWeights.put(input, random.nextFloat());
                }
            }
            else {
                int extinctTest = random.nextInt(1, 100);
                if (extinctTest <= extinctPercent){
                    inputs.remove(input);
                    inWeights.remove(input);
                }
                else {
                    inputs.remove(input);
                    float w = inWeights.remove(input);
                    int newInput = 0;
                    if (prevCount > 1){
                        newInput = random.nextInt(prevCount);
                    }
                    inWeights.put(newInput, w);
                }
            }
        }
    }

    public void fixLinks(int prevCount) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<Integer> ins = new ArrayList<>();
        ins.addAll(inputs);
        for (int i = 0; i < ins.size(); i++) {
            int in = ins.get(i);
            float w = inWeights.get(in);
            if (in >= prevCount){
                int newIn = random.nextInt(prevCount);
                inputs.remove(in);
                inWeights.remove(in);
                inputs.add(newIn);
                inWeights.put(newIn, w);
            }
        }
    }
}
