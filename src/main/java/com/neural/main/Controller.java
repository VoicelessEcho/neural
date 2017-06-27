package com.neural.main;

import com.google.gson.Gson;
import com.neural.game.GameGrid;
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
    public int mapsCount = 7;

    public FitnessTracker fitnessTracker;
    public NeuralGenerator generator;
    public AtomicInteger workerCount;
    public int generation = 0;

    public List<GameGrid> loadedMaps = new ArrayList<>();

    public ExecutorService executorService = Executors.newFixedThreadPool(8);
    public void start(String fname, String generationNumber) {
        logger.info("Starting...");


        int genCount = 1000000;

        generator = new NeuralGenerator();

        if (fname == null || generationNumber == null) {
            generator.generateRandomNetworks(110, generation);
        }
        else {
            loadGeneration(fname, Integer.parseInt(generationNumber));
        }
        fitnessTracker = new FitnessTracker();

        loadMaps();

        for (generation = 0; generation < genCount; generation++) {
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
                NetWorker worker = new NetWorker(fitnessTracker, maps.get(i), i, workerCount);
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
            File file = new File("./generations/generation_" + String.valueOf(generation) + ".txt");
            String bestJson = gson.toJson(bestOfBest);

            try {
                List<String> lines = new ArrayList<>();
                lines.add(bestJson);
                Files.write(file.toPath(), lines, Charset.forName("UTF-8"));

            } catch (IOException e) {
                e.printStackTrace();
            }
            String jsonStr = gson.toJson(fitnessTracker.getScores());
            logger.info("GENERATION " + generation);
            logger.info(jsonStr);
            logger.info("BEST:");
            logger.info(bestJson);

            System.out.println("--------- breeding ---------------");
            //generation = generation + 1;
            generator.breedNewGeneration(bestOfBest);
            for (int i = 0; i < 10; i++) {
                generator.neuralNets.add(generator.generateRandomNet(generation));
            }
            System.out.println("----------------------------------");
            System.out.println("----GENERATION: " + String.valueOf(generation) + " ---------");


        }
        //System.out.println(jsonStr);
        executorService.shutdown();
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
