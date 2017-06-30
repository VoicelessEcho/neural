package com.neural.main;

import com.google.gson.Gson;
import com.neural.game.GameGrid;
import com.neural.workers.BreederWorker;
import com.neural.workers.GeneratorWorker;
import com.neural.workers.MutatorWorker;
import com.neural.workers.NetWorker;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by Virgis on 2017.06.24.
 */
public class Controller {
    private static Logger logger = Logger.getLogger(Controller.class);
    List<GameGrid> maps = new ArrayList<>();

    public Gson gson = new Gson();
    public int mapsCount = 0;

    public FitnessTracker fitnessTracker;
    public NeuralGenerator generator;
    public AtomicInteger workerCount;
    public int generation;

    public List<GameGrid> loadedMaps = new ArrayList<>();
    public List<NeuralNet> prevBest = new ArrayList<>();
    public List<NeuralNet> allTimeBest = new ArrayList<>();

    public ExecutorService executorService = Executors.newFixedThreadPool(4);
    public void start(String fname, String generationNumber) {
        logger.info("Starting...");

        generation = 0;
        int genCount = 1000000;

        generator = new NeuralGenerator();

        if (fname == null || generationNumber == null) {
            generator.generateRandomNetworks(110, generation);
        }
        else {
            loadGeneration(fname, Integer.parseInt(generationNumber));
            generation = Integer.parseInt(generationNumber);
        }
        fitnessTracker = new FitnessTracker();

        loadMaps();

        for (generation = generation; generation < generation + genCount; generation++) {
            maps.clear();
            for (int i = 0; i < mapsCount; i++) {
                GameGrid grid = new GameGrid();
                grid.generateGrid();

                maps.add(grid);
            }
            maps.addAll(loadedMaps);



            List<NeuralNet> neuralNets = generator.neuralNets;
            fitnessTracker.setNetworks(neuralNets);
            fitnessTracker.resetScores();

            workerCount = new AtomicInteger(0);


            for (int i = 0; i < maps.size(); i++) {
                workerCount.incrementAndGet();
                NetWorker worker = new NetWorker(fitnessTracker, maps.get(i), i, workerCount, prevBest);
                executorService.execute(worker);
            }

            while (workerCount.get() != 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Still working... " + String.valueOf(workerCount.get()));
            }

            List<NeuralNet> bestOfBest = fitnessTracker.getBestNetworks(10);

            List<NeuralNet> newAllBest = new ArrayList<>();
            newAllBest.addAll(setAllTimeBest(bestOfBest, allTimeBest));
            allTimeBest.clear();
            allTimeBest.addAll(newAllBest);

            File fileBest = new File("./generations/generation_all_time_best.txt");
            String bestAllTimeJson = gson.toJson(bestOfBest);
            File file = new File("./generations/generation_" + String.valueOf(generation) + ".txt");
            String bestJson = gson.toJson(bestOfBest);
            try {
                List<String> lines = new ArrayList<>();
                lines.add(bestAllTimeJson);
                Files.write(fileBest.toPath(), lines, Charset.forName("UTF-8"));
            }
            catch (IOException e){
                e.printStackTrace();
            }

            if (generation%100 == 0) {
                try {
                    List<String> lines = new ArrayList<>();
                    lines.add(bestJson);
                    Files.write(file.toPath(), lines, Charset.forName("UTF-8"));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String jsonStr = gson.toJson(fitnessTracker.getScores());
            logger.info("GENERATION " + generation);
            logger.info(jsonStr);
            //logger.info("BEST:");
            //logger.info(bestJson);

            System.out.println("--------- breeding ---------------");

            workerCount.addAndGet(3);

            BreederWorker bw = new BreederWorker(bestOfBest, allTimeBest, generator, workerCount);
            GeneratorWorker gw = new GeneratorWorker(1000, generator, generation, workerCount);
            MutatorWorker mw = new MutatorWorker(bestOfBest, allTimeBest, generator, workerCount);

            prevBest.clear();
            prevBest.addAll(bestOfBest);

            executorService.submit(bw);
            executorService.submit(gw);
            executorService.submit(mw);
            while (workerCount.get() != 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Still breeding... " + String.valueOf(workerCount.get()));
            }

            generator.neuralNets.clear();
            generator.neuralNets.addAll(bw.getNets());
            generator.neuralNets.addAll(gw.getNets());
            generator.neuralNets.addAll(mw.getNets());


            /*//generation = generation + 1;
            generator.breedNewGeneration(bestOfBest);
            for (int i = 0; i < 10; i++) {
                generator.neuralNets.add(generator.generateRandomNet(generation));
            }*/
            System.out.println("----------------------------------");
            System.out.println("----GENERATION: " + String.valueOf(generation) + " ---------");


        }
        //System.out.println(jsonStr);
        executorService.shutdown();
    }

    private List<NeuralNet> setAllTimeBest(List<NeuralNet> bestOfBest, List<NeuralNet> allTimeBest) {
        List<NeuralNet> atb = new ArrayList<>();
        atb.addAll(allTimeBest);

        for (NeuralNet bestNet : bestOfBest) {
            if (atb.size() < 10){
                atb.add(bestNet);
            }
            else {
                int minScore = Integer.MAX_VALUE;
                int minIndex = 0;
                for (int i = 0; i < atb.size(); i++) {
                    NeuralNet abNet = atb.get(i);
                    if (abNet.score < minScore){
                        minScore = abNet.score;
                        minIndex = i;
                    }
                }

                if (bestNet.score > minScore){
                    atb.remove(minIndex);
                    atb.add(bestNet);
                }
            }
        }

        return atb;
    }

    private void loadGeneration(String fName, int generationNumber) {
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

        NeuralNet[] neuralNets = gson.fromJson(line, NeuralNet[].class);
        generator.neuralNets.addAll(Arrays.asList(neuralNets));
        generation = generationNumber;
    }

    private void loadMaps() {
        File f = new File("./maps/maps.txt");
        BufferedReader bf = null;
        try {
            bf = Files.newBufferedReader(f.toPath(),  Charset.forName("UTF-8"));
            String line = "";

            while (true){
                line = bf.readLine();
                if (line == null){
                    break;
                }
                int size = Integer.parseInt(line);
                String[] lines = new String[size];

                for (int i = 0; i < size; i++) {
                    line = bf.readLine();
                    lines[i] = line;
                }
                GameGrid grid = new GameGrid();
                grid.generateGrid(size, lines);
                loadedMaps.add(grid);
            }

            bf.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
