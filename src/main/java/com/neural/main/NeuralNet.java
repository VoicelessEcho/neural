package com.neural.main;

import com.neural.neuron.NeuralLayer;
import com.neural.neuron.NeuralNode;
import com.neural.neuron.NeuralOutput;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Virgis on 2017.06.25.
 */
public class NeuralNet implements Serializable{
    public int generationNumber;
    public int parent1;
    public int parent2;
    public int score;

    public enum Dir {N, W, E, S, NW, NE, SW, SE}

    public List<NeuralLayer> layers;
    public NeuralOutput[] outputs;

    public NeuralNet(List<NeuralLayer> layers, NeuralOutput[] outputs) {
        this.layers = layers;
        this.outputs = outputs;
    }

    public NeuralNet() {
        layers = new ArrayList<>();
        outputs = new NeuralOutput[]{
                new NeuralOutput(Dir.N, Dir.N),
                new NeuralOutput(Dir.N, Dir.W),
                new NeuralOutput(Dir.N, Dir.E),
                new NeuralOutput(Dir.N, Dir.S),
                new NeuralOutput(Dir.N, Dir.NW),
                new NeuralOutput(Dir.N, Dir.NE),
                new NeuralOutput(Dir.N, Dir.SW),
                new NeuralOutput(Dir.N, Dir.SE),

                new NeuralOutput(Dir.W, Dir.N),
                new NeuralOutput(Dir.W, Dir.W),
                new NeuralOutput(Dir.W, Dir.E),
                new NeuralOutput(Dir.W, Dir.S),
                new NeuralOutput(Dir.W, Dir.NW),
                new NeuralOutput(Dir.W, Dir.NE),
                new NeuralOutput(Dir.W, Dir.SW),
                new NeuralOutput(Dir.W, Dir.SE),

                new NeuralOutput(Dir.E, Dir.N),
                new NeuralOutput(Dir.E, Dir.W),
                new NeuralOutput(Dir.E, Dir.E),
                new NeuralOutput(Dir.E, Dir.S),
                new NeuralOutput(Dir.E, Dir.NW),
                new NeuralOutput(Dir.E, Dir.NE),
                new NeuralOutput(Dir.E, Dir.SW),
                new NeuralOutput(Dir.E, Dir.SE),

                new NeuralOutput(Dir.S, Dir.N),
                new NeuralOutput(Dir.S, Dir.W),
                new NeuralOutput(Dir.S, Dir.E),
                new NeuralOutput(Dir.S, Dir.S),
                new NeuralOutput(Dir.S, Dir.NW),
                new NeuralOutput(Dir.S, Dir.NE),
                new NeuralOutput(Dir.S, Dir.SW),
                new NeuralOutput(Dir.S, Dir.SE),

                new NeuralOutput(Dir.NE, Dir.N),
                new NeuralOutput(Dir.NE, Dir.W),
                new NeuralOutput(Dir.NE, Dir.E),
                new NeuralOutput(Dir.NE, Dir.S),
                new NeuralOutput(Dir.NE, Dir.NW),
                new NeuralOutput(Dir.NE, Dir.NE),
                new NeuralOutput(Dir.NE, Dir.SW),
                new NeuralOutput(Dir.NE, Dir.SE),

                new NeuralOutput(Dir.NW, Dir.N),
                new NeuralOutput(Dir.NW, Dir.W),
                new NeuralOutput(Dir.NW, Dir.E),
                new NeuralOutput(Dir.NW, Dir.S),
                new NeuralOutput(Dir.NW, Dir.NW),
                new NeuralOutput(Dir.NW, Dir.NE),
                new NeuralOutput(Dir.NW, Dir.SW),
                new NeuralOutput(Dir.NW, Dir.SE),

                new NeuralOutput(Dir.SW, Dir.N),
                new NeuralOutput(Dir.SW, Dir.W),
                new NeuralOutput(Dir.SW, Dir.E),
                new NeuralOutput(Dir.SW, Dir.S),
                new NeuralOutput(Dir.SW, Dir.NW),
                new NeuralOutput(Dir.SW, Dir.NE),
                new NeuralOutput(Dir.SW, Dir.SW),
                new NeuralOutput(Dir.SW, Dir.SE),

                new NeuralOutput(Dir.SE, Dir.N),
                new NeuralOutput(Dir.SE, Dir.W),
                new NeuralOutput(Dir.SE, Dir.E),
                new NeuralOutput(Dir.SE, Dir.S),
                new NeuralOutput(Dir.SE, Dir.NW),
                new NeuralOutput(Dir.SE, Dir.NE),
                new NeuralOutput(Dir.SE, Dir.SW),
                new NeuralOutput(Dir.SE, Dir.SE),
        };
    }

    public NeuralNet cloneNet() {
        List<NeuralLayer> layers_ = new ArrayList<>();
        for (int i = 0; i < layers.size(); i++) {
            NeuralLayer layer = layers.get(i);
            NeuralLayer layer_ = layer.cloneLayer();
            layers_.add(layer_);
        }

        NeuralOutput[] outputs_ = new NeuralOutput[outputs.length];
        for (int i = 0; i < outputs.length; i++) {
            outputs_[i] = (NeuralOutput) outputs[i].cloneNode();
        }

        NeuralNet clone = new NeuralNet(layers_, outputs_);
        return clone;
    }

    public void mutateNet() {
        for (int i = 0; i < layers.size(); i++) {
            NeuralLayer layer = layers.get(i);
            layer.mutateLayer();
        }
        for (int i = 0; i < outputs.length; i++) {
            outputs[i].mutateNode();
        }
    }

    public NeuralOutput execute(int[] inputs){

        double[] prevOutput = new double[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            prevOutput[i] = inputs[i];
        }

        //calc hidden
        for (int i = 0; i < layers.size(); i++) {
            NeuralLayer layer = layers.get(i);
            double [] outputs = layer.computeOutputs(this, prevOutput);
            prevOutput = outputs;
        }

        //calc outputs
        double[] outValues = computeOutputs(prevOutput);

        int index = 0;
        double max = Double.MIN_VALUE;
        for (int i = 0; i < outValues.length; i++) {
            if (outValues[i] > max){
                max = outValues[i];
                index = i;
            }
        }

        NeuralOutput node = this.outputs[index];

        return node;
    }

    public List<NeuralLayer> getLayers() {
        return layers;
    }

    public void setLayers(List<NeuralLayer> layers) {
        this.layers = layers;
    }

    public NeuralOutput[] getOutputs() {
        return outputs;
    }

    public void setOutputs(NeuralOutput[] outputs) {
        this.outputs = outputs;
    }

    public double[] computeOutputs(double[] inputs) {
        double[] outputs = new double[this.outputs.length];
        for (int i = 0; i < this.outputs.length; i++) {
            NeuralNode node = this.outputs[i];
            outputs[i] = node.computeOutputs(this, inputs);
        }

        return outputs;
    }

    public List<NeuralLayer> getHalfHiddenLayers(boolean up) {
        List<NeuralLayer> hiddenLayers = new ArrayList<>();
        for (int i = 0; i < layers.size(); i++) {
            NeuralLayer layer = layers.get(i);
            hiddenLayers.add(layer.getHalfNodes(up));
            up = !up;
        }
        return hiddenLayers;
    }

    public List<NeuralOutput> getHalfOutputs(boolean up) {
        List<NeuralOutput> outs = new ArrayList<>();
        if (up){
            for (int i = 0; i < 32; i++) {
                NeuralOutput out = (NeuralOutput) outputs[i].cloneNode();
                outs.add(out);
            }
        }
        else {
            for (int i = 32; i < 64; i++) {
                NeuralOutput out = (NeuralOutput) outputs[i].cloneNode();
                outs.add(out);
            }
        }
        return outs;
    }

    public void combineHidden(List<NeuralLayer> hiddenLayers_i, boolean up, List<NeuralLayer> hiddenLayers_j) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int minLayers = hiddenLayers_i.size();
        int maxLayers = hiddenLayers_i.size();
        if (hiddenLayers_j.size() < minLayers){
            minLayers = hiddenLayers_j.size();
        }
        if (hiddenLayers_j.size() > maxLayers){
            maxLayers = hiddenLayers_j.size();
        }

        List<NeuralLayer> mergedHidden = new ArrayList<>();
        for (int i = 0; i < minLayers; i++) {
            List<NeuralNode> nodeList = new ArrayList<>();
            if (up){
                nodeList.addAll(hiddenLayers_i.get(i).layer);
                nodeList.addAll(hiddenLayers_j.get(i).layer);
            }
            else {
                nodeList.addAll(hiddenLayers_j.get(i).layer);
                nodeList.addAll(hiddenLayers_i.get(i).layer);
            }
            NeuralLayer newLayer = new NeuralLayer(nodeList, false);
            mergedHidden.add(newLayer);
        }

        int prevCount = 49;
        for (int i = 0; i < mergedHidden.size(); i++) {
            NeuralLayer l = mergedHidden.get(i);
            l.normaliseNodes(prevCount);
            prevCount = l.layer.size();
        }

        for (int i = minLayers; i < maxLayers ; i++) {
            boolean extinct = random.nextBoolean();
            if (!extinct){
                List<NeuralNode> nodeList = new ArrayList<>();
                if (hiddenLayers_i.size() > hiddenLayers_j.size()){
                    nodeList.addAll(hiddenLayers_i.get(i).layer);
                }
                else {
                    nodeList.addAll(hiddenLayers_j.get(i).layer);
                }
                NeuralLayer newLayer = new NeuralLayer(nodeList, false);
                mergedHidden.add(newLayer);
                if (mergedHidden.size() > 5){
                    System.out.println("bugas");
                }
                prevCount = newLayer.expandLayer(prevCount, newLayer);
            }
        }
        layers.addAll(mergedHidden);
    }

    public List<NeuralOutput> combineOutputs(List<NeuralOutput> outputs_i, boolean up, List<NeuralOutput> outputs_j) {
        List<NeuralOutput> outputs = new ArrayList<>();
        if (up){
            outputs.addAll(outputs_i);
            outputs.addAll(outputs_j);
        }
        else {
            outputs.addAll(outputs_j);
            outputs.addAll(outputs_i);
        }
        return outputs;
    }

    public void normaliseOutputs(int prevCount){
        for (int i = 0; i < outputs.length; i++) {
            NeuralOutput output = outputs[i];
            output.normalise(prevCount);
        }
    }

    public void swapHiddenNodes() {
        for (NeuralLayer l : layers ) {
            l.swapNodes();
        }
    }

    public void fixLinks() {
        int prevCount = 49;
        if (layers.size() > 20){
            System.out.println("bugas");
        }
        for (int i = 0; i < layers.size(); i++) {
            prevCount = layers.get(i).fixLinks(this, prevCount);
        }
    }
}
