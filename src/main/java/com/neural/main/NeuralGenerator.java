package com.neural.main;

import com.neural.neuron.NeuralLayer;
import com.neural.neuron.NeuralNode;
import com.neural.neuron.NeuralOutput;

import javax.sound.midi.Soundbank;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Virgis on 2017.06.24.
 */
public class NeuralGenerator {

    public List<NeuralNet> neuralNets = new ArrayList<>();


    public void generateRandomNetworks(int genSize, int generationNumber){
        for (int i = 0; i < genSize; i++) {
            neuralNets.add(generateRandomNet(generationNumber));
        }
    }

    public NeuralNet generateRandomNet(int generationNumber){
        NeuralNet randomNet = new NeuralNet();

        int minHiddenLayers = 0;
        int maxHiddenLayers = 5;
        int minNodes = 1;
        int maxNodes = 50;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int layersCount = random.nextInt(minHiddenLayers, maxHiddenLayers + 1);
        int prevCount = 48;

        //generate hidden
        for (int i = 0; i < layersCount; i++) {
            int nodesCount = random.nextInt(minNodes, maxNodes + 1);
            List<NeuralNode> nodes = new ArrayList<>(nodesCount);
            for (int j = 0; j < nodesCount; j++) {
                int linkCount = random.nextInt(1, prevCount + 1);
                NeuralNode node = generateRandomNode(prevCount,  linkCount);
                nodes.add(node);
            }
            prevCount = nodesCount;
            NeuralLayer layer = new NeuralLayer(nodes, false);
            randomNet.layers.add(layer);
        }

        //generate outputs links

        NeuralOutput[] outputs = randomNet.outputs;

        for (int i = 0; i < outputs.length; i++) {
            int linkCount = random.nextInt(1, prevCount + 1);
            Set<Integer> indexes = new HashSet<>();
            Map<Integer, Double> inWeights = new HashMap<>();
            for (int j = 0; j < linkCount; j++) {
                int index = 0;
                if (prevCount > 1) {
                    index = random.nextInt(0, prevCount);
                }
                double inWeight = random.nextDouble();
                indexes.add(index);
                inWeights.put(index, inWeight);
            }
            outputs[i].setInputs(indexes);
            outputs[i].setInWeights(inWeights);
        }

        randomNet.generationNumber = generationNumber;
        randomNet.parent1 = -1;
        randomNet.parent2 = -1;
        randomNet.type = NeuralNet.GenType.Generated;
        return randomNet;
    }

    private NeuralNode generateRandomNode(int prevCount, int linkCount) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Set<Integer> indexes = new HashSet<>();
        Map<Integer, Double> inWeights = new HashMap<>();

        for (int i = 0; i < linkCount; i++) {
            int index = 0;
            if (prevCount > 1) {
                index = random.nextInt(0, prevCount);
            }
            double inWeight = random.nextDouble();
            indexes.add(index);
            inWeights.put(index, inWeight);
        }

        NeuralNode node = new NeuralNode(indexes, inWeights);
        return node;
    }


    //--------------------------------------------------------------------------------------------------------

    public List<NeuralNet> mutateGeneration(List<NeuralNet> bestNetworks){
        List<NeuralNet> newGeneration = new ArrayList<>();
        for (int i = 0; i < bestNetworks.size(); i++) {
            for  (int j = 0; j < 100; j++) {
                NeuralNet net = bestNetworks.get(i).cloneNet();
                net.mutateNet();
                net.type = NeuralNet.GenType.Mutated;
                newGeneration.add(net);
            }
            System.out.println("Done mutating net #" + String.valueOf(i));
        }
        return newGeneration;
    }


    public List<NeuralNet> breedNewGeneration(List<NeuralNet> bestNetworks){
        List<NeuralNet> newGeneration = new ArrayList<>();
        newGeneration.addAll(bestNetworks);

        for (int i = 0; i < bestNetworks.size(); i++) {
            NeuralNet net_i = bestNetworks.get(i);
            for (int j = i + 1; j < bestNetworks.size(); j++) {
              //  System.out.println("Breading " + String.valueOf(i) + " " + String.valueOf(j));
                NeuralNet net_j = bestNetworks.get(j);
              //  System.out.println("b1");
                NeuralNet net_combined = combineNetworks(net_i, net_j);
               // System.out.println("b2");
                net_combined.generationNumber = net_i.generationNumber + 1;
               // System.out.println("b3");
                net_combined.parent1 = i;
                net_combined.parent2 = j;
               // System.out.println("b4");
                newGeneration.add(net_combined);
               // System.out.println("b5");
                net_combined.fixLinks();
                net_combined.type = NeuralNet.GenType.Combined;
            }
            System.out.println("Done breeding net #" + String.valueOf(i));
        }
        return newGeneration;
        //neuralNets = newGeneration;
    }

    private NeuralNet combineNetworks(NeuralNet net_i, NeuralNet net_j) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        boolean up = random.nextBoolean();
        List<NeuralLayer> hiddenLayers_i = net_i.getHalfHiddenLayers(up);
        List<NeuralLayer> hiddenLayers_j = net_j.getHalfHiddenLayers(!up);
        List<NeuralOutput> outputs_i = net_i.getHalfOutputs(up);
        List<NeuralOutput> outputs_j = net_j.getHalfOutputs(!up);

        NeuralNet net = new NeuralNet();
        net.combineHidden(hiddenLayers_i, up, hiddenLayers_j);
        net.combineOutputs(outputs_i, up, outputs_j);

        net.normaliseOutputs(net.layers.get(net.layers.size() - 1).layer.size());

        net.swapHiddenNodes();
        return net;
    }
}
