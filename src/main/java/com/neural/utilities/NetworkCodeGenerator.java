package com.neural.utilities;

import com.google.gson.Gson;
import com.neural.main.NeuralNet;
import com.neural.neuron.NeuralLayer;
import com.neural.neuron.NeuralNode;
import com.neural.neuron.NeuralOutput;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Virgis on 2017.06.29.
 */
public class NetworkCodeGenerator {


    public static void main(String[] args){
        String fName = args[0];
        int index = Integer.parseInt(args[1]);

        File f = new File("./generations/" + fName);
        BufferedReader bf = null;
        String line = null;
        try {
            bf = Files.newBufferedReader(f.toPath(),  Charset.forName("UTF-8"));
            line =  bf.readLine();
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        NeuralNet[] neuralNets = gson.fromJson(line, NeuralNet[].class);

        NetworkCodeGenerator generator = new NetworkCodeGenerator();
        generateNetworkCode(neuralNets[index], "./code/" + fName + "_" + args[1]);

    }

    private static void generateNetworkCode(NeuralNet neuralNet, String fileName) {
        File file = new File(fileName);
        List<String> lines = new ArrayList<>();

        lines.add("public static NeuralNet createNet(){");
        lines.add("List<NeuralLayer> layers = new ArrayList<>();");

        for (int i = 0; i < neuralNet.layers.size(); i++) {
            NeuralLayer layer = neuralNet.layers.get(i);

            lines.add("        //----layer #" + String.valueOf(i) + "----------------");

            List<NeuralNode> layer_ = layer.layer;
            lines.add("NeuralLayer l_" + String.valueOf(i) + " = new NeuralLayer();");
            lines.add("layers.add(l_" + String.valueOf(i) + ");");


            for (int j = 0; j < layer_.size(); j++) {
                NeuralNode node = layer_.get(j);
                lines.add("        //----node #" + String.valueOf(j) + "----------------");
                lines.add("NeuralNode node_l" + String.valueOf(i) + "_" + String.valueOf(j) + " = new NeuralNode();");
                String nodeInputs = "node_l" + String.valueOf(i) + "_" + String.valueOf(j) + ".inputs.addAll(Arrays.asList(";
                String nodeInW = "double[] weights_i" + String.valueOf(i) + "_" + String.valueOf(j) + " = new double[]{";

                List<Integer> inputs = new ArrayList<>();
                inputs.addAll(node.inputs);

                for (int k = 0; k < inputs.size(); k++) {
                    int in = inputs.get(k);
                    double w = node.inWeights.get(in);

                    nodeInputs += String.valueOf(in);
                    nodeInW += String.valueOf(w) + "f";

                    if (k + 1 < inputs.size()){
                        nodeInputs += ", ";
                        nodeInW += ", ";
                    }
                }

                nodeInputs += "));";
                nodeInW += "};";

                lines.add(nodeInputs);
                lines.add(nodeInW);
                lines.add("setInWeights(node_l" + String.valueOf(i) + "_" + String.valueOf(j)  + ", weights_i" + String.valueOf(i) + "_" + String.valueOf(j) + ");");
                lines.add("l_" + String.valueOf(i) + ".layer.add(node_l" + String.valueOf(i) + "_" + String.valueOf(j)  + ");");
            }
        }

        lines.add("");


        lines.add("NeuralOutput outputs[] = new NeuralOutput[" + String.valueOf(neuralNet.outputs.length) + "];");
        for (int i = 0; i < neuralNet.outputs.length; i++) {
            NeuralOutput output = neuralNet.outputs[i];

            lines.add("        //----output #" + String.valueOf(i) + "----------------");
            lines.add("NeuralOutput out_" + String.valueOf(i) + " = new NeuralOutput(NeuralNet.Dir." + output.dir1.name() + ", NeuralNet.Dir." + output.dir2.name() + ");");
            String nodeInputs = "out_" + String.valueOf(i) + ".inputs.addAll(Arrays.asList(";
            String nodeInW = "double[] weights_o" + String.valueOf(i) + " = new double[]{";
            List<Integer> inputs = new ArrayList<>();
            inputs.addAll(output.inputs);

            for (int k = 0; k < inputs.size(); k++) {
                int in = inputs.get(k);
                double w = output.inWeights.get(in);

                nodeInputs += String.valueOf(in);
                nodeInW += String.valueOf(w) + "f";

                if (k + 1 < inputs.size()){
                    nodeInputs += ", ";
                    nodeInW += ", ";
                }
            }

            nodeInputs += "));";
            nodeInW += "};";

            lines.add(nodeInputs);
            lines.add(nodeInW);
            lines.add("setInWeights(out_" + String.valueOf(i) + ", weights_o" + String.valueOf(i) + ");");
            lines.add("outputs[" + String.valueOf(i) + "] = out_" + String.valueOf(i) + ";");
            lines.add("");
        }

        lines.add("NeuralNet net = new NeuralNet(layers, outputs);");
        lines.add("net.type = NeuralNet.GenType." + neuralNet.type.name() + ";");

        lines.add("net.generationNumber = " + String.valueOf(neuralNet.generationNumber) + ";");
        lines.add("net.parent1 = " + String.valueOf(neuralNet.parent1) + ";");
        lines.add("net.parent2 = " + String.valueOf(neuralNet.parent2) + ";");
        lines.add("net.score = " + String.valueOf(neuralNet.score) + ";");
        lines.add("");
        lines.add("return net;");

        lines.add("}");
        try{
            Files.write(file.toPath(), lines, Charset.forName("UTF-8"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


}
