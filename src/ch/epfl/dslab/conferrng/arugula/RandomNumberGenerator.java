/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.epfl.dslab.conferrng.arugula;

import java.util.Random;

/**
 *
 * @author slv
 */
public class RandomNumberGenerator {

    private int seed = new Random().nextInt();
    private Random randomGenerator = new Random(seed);
    private static RandomNumberGenerator instance = new RandomNumberGenerator();

    public void setInitialSeed(int seed) {
        this.seed = seed;
        randomGenerator = new Random(seed);
    }

    public static void staticSetInitialSeed(int seed) {
        instance.setInitialSeed(seed);
    }

    public int getNextInt(){
        return randomGenerator.nextInt();
    }

    public static int staticGetNextInt(){
        return instance.getNextInt();
    }

    public double getNextDouble(){
        return randomGenerator.nextDouble();
    }

    public static double staticGetNextDouble(){
        return instance.getNextDouble();
    }

}
