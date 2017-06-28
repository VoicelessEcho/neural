package com.neural.neuron;

import com.neural.main.NeuralNet;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Virgis on 2017.06.25.
 */
public class NeuralLayer implements Serializable {
    public List<NeuralNode> layer = null;
    public boolean lastLayer;

    public NeuralLayer() {
        layer = new ArrayList<>();
        lastLayer = false;
    }

    public NeuralLayer(List<NeuralNode> layer, boolean lastLayer) {
        this.layer = layer;
        this.lastLayer = lastLayer;
    }

    public List<NeuralNode> getLayer() {
        return layer;
    }

    public void setLayer(List<NeuralNode> layer) {
        this.layer = layer;
    }

    public boolean isLastLayer() {
        return lastLayer;
    }

    public void setLastLayer(boolean lastLayer) {
        this.lastLayer = lastLayer;
    }

    public double[] computeOutputs(NeuralNet net, double[] inputs) {
        double[] outputs = new double[layer.size()];
        for (int i = 0; i < layer.size(); i++) {
            NeuralNode node = layer.get(i);
            outputs[i] = node.computeOutputs(net, inputs);
        }

        return outputs;
    }

    public NeuralLayer getHalfNodes(boolean up) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<NeuralNode> nodes = new ArrayList<>();
        boolean modLeave = random.nextBoolean();
        int size = layer.size()/2;
        if (layer.size()/2 == 1 && modLeave){
            size = size + 1;
        }
        if (size == 0){
            size = 1;
        }

        if (up){
            for (int i = 0; i < size; i++) {
                NeuralNode node = layer.get(i).cloneNode();
                nodes.add(node);
            }
        }
        else {
            for (int i = 1; i <= size; i++) {
                NeuralNode node = layer.get(layer.size() - i).cloneNode();
                nodes.add(node);
            }
        }
        NeuralLayer layer_ = new NeuralLayer(nodes, this.lastLayer);
        if (layer_.layer.size() > 30){
            System.out.println("bugas");
        }
        return layer_;
    }

    public void normaliseNodes(int prevCount) {
        for (int i = 0; i < layer.size(); i++) {
            NeuralNode node = layer.get(i);
            node.normalise(prevCount);

        }
    }

    public int expandLayer(int prevCount, NeuralLayer layer__) {
        List<NeuralNode> layer_ = layer__.layer;
        ThreadLocalRandom  random = ThreadLocalRandom.current();
        int expandSize = 1;
        if (layer_.size() > 1) {
            expandSize = random.nextInt(1, layer_.size() + 1);
        }
        if (expandSize > 50){
            System.out.println("bugas");
        }
        for (int i = 0; i < expandSize; i++) {
            NeuralNode node = new NeuralNode();
            int linkCount = 1;
            if (prevCount > 1) {
                linkCount = random.nextInt(1, prevCount + 1);
            }
            Set<Integer> inputs_ = new HashSet<>();
            Map<Integer, Float> weights = new HashMap<>();
            for (int j = 0; j < linkCount; j++) {
                int index = random.nextInt(prevCount);
                float linkW = random.nextFloat();

                inputs_.add(index);
                weights.put(index, linkW);
            }
            node.inputs = inputs_;
            node.inWeights = weights;
            layer_.add(node);
        }

        return layer_.size();
    }

    public void swapNodes() {
        int swapChance = 30;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < layer.size(); i++) {
            int swapValue = random.nextInt(1,101);
            if (swapChance <= swapValue){
                NeuralNode node = layer.remove(i);
                int newPos = 0;
                if (layer.size() != 0) {
                    newPos = random.nextInt(layer.size());
                }
                layer.add(newPos, node);
            }
        }
    }

    public int fixLinks(NeuralNet parent, int prevCount) {
        for (int i = 0; i < layer.size(); i++) {
            if (layer.size() > 60){
                System.out.println("bugas");
            }
            NeuralNode node = layer.get(i);
            node.fixLinks(prevCount);
        }

        return this.layer.size();
    }


    public NeuralLayer cloneLayer(){
        List<NeuralNode> layer_c = new ArrayList<>();
        for (int i = 0; i < layer.size(); i++) {
            NeuralNode node_ = layer.get(i).cloneNode();
            layer_c.add(node_);
        }

        NeuralLayer layer_ = new NeuralLayer(layer_c, false);
        return layer_;
    }

    public void mutateLayer() {
        for (int i = 0; i < layer.size(); i++) {
            NeuralNode node = layer.get(i);
            node.mutateNode();
        }
    }
}
