import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.PMF;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.util.Range;
import es.upm.etsisi.cf4j.util.plot.LinePlot;

import java.util.Random;

public class RDABMFComp {
    private static final int[][] PROBABILITIES = {{0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                                                    {0, 0, 0, 0, 0, 0, 0, 0, 1, 1},
                                                    {0, 0, 0, 0, 0, 0, 0, 1, 1, 1},
                                                    {0, 0, 0, 0, 0, 0, 1, 1, 1, 1},
                                                    {0, 0, 0, 0, 0, 1, 1, 1, 1, 1},
                                                    {0, 0, 0, 0, 1, 1, 1, 1, 1, 1},
                                                    {0, 0, 0, 1, 1, 1, 1, 1, 1, 1},
                                                    {0, 0, 1, 1, 1, 1, 1, 1, 1, 1},
                                                    {0, 1, 1, 1, 1, 1, 1, 1, 1, 1}};
    public static final double[] XAXIS = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9};
    private static final int[] NUM_FACTORS = Range.ofIntegers(2, 1, 14);
    private static final int FACTOR = 10;
    private static final int NUM_ITERS = 300;
    private static final long RANDOM_SEED = 43L;

    public RDABMFComp(){
    }

    public static void main(String[] args){
        try {

            Random random = new Random(13L);

            DataModel datamodel = DataModel.load("ml1M");

            LinePlot plot = new LinePlot(XAXIS, "Percent of users", "MAE");

            plot.addSeries("Error General");
            plot.addSeries("Gm");
            plot.addSeries("GM");

            for (double naxis : XAXIS){

                int[] probability = PROBABILITIES[(int)(naxis*10.)-1];

                for (User user : datamodel.getUsers()){
                    user.getDataBank().setInt("age", probability[random.nextInt(probability.length)]);
                }

                for (User user : datamodel.getTestUsers()){
                    user.getDataBank().setInt("age", probability[random.nextInt(probability.length)]);
                }

                int count0 = 0, count1 = 0;

                for (User user : datamodel.getUsers()){
                    if(user.getDataBank().getInt("age") == 1)
                        count1++;
                    if(user.getDataBank().getInt("age") == 0)
                        count0++;
                }

                System.out.println("===========================");
                System.out.print("\nRelacion Viejos/Jóvenes = " + (double) count1/ (double) count0);

                // Evaluate PMF Recommender
                Recommender pmf = new PMF(datamodel, FACTOR, NUM_ITERS, RANDOM_SEED);
                pmf.fit();

                Recommender dabmf = new RDABMF(datamodel, FACTOR, NUM_ITERS, RANDOM_SEED);
                dabmf.fit();

                QualityMeasure pmfq = new DAMAE(pmf, 1);

                QualityMeasure g1 = new DAMAE(dabmf, 0, true);
                QualityMeasure g2 = new DAMAE(dabmf, 0);

                double pmfScore = pmfq.getScore();
                plot.setValue("Error General", naxis, pmfScore);

                double g1Score = g1.getScore();
                plot.setValue("Gm", naxis, g1Score);

                double g2Score = g2.getScore();
                plot.setValue("GM", naxis, g2Score);
            }

            plot.printData("0", "0.0000");
            plot.draw();


            plot = new LinePlot(NUM_FACTORS, "Numbre of Factors", "MAE");


            int[] probability = PROBABILITIES[6];

            for (User user : datamodel.getUsers()){
                user.getDataBank().setInt("age", probability[random.nextInt(probability.length)]);
            }

            for (User user : datamodel.getTestUsers()){
                user.getDataBank().setInt("age", probability[random.nextInt(probability.length)]);
            }

            plot.addSeries("YoungPMF");
            plot.addSeries("OldPMF");

            for (int factors : NUM_FACTORS) {
                Recommender pmf = new PMF(datamodel, factors, NUM_ITERS, RANDOM_SEED);
                pmf.fit();

                QualityMeasure ydamae = new DAMAE(pmf, 0);
                QualityMeasure odamae = new DAMAE(pmf, 0, true);

                double youngScore = ydamae.getScore();
                plot.setValue("YoungPMF", factors, youngScore);

                double oldScore = odamae.getScore();
                plot.setValue("OldPMF", factors, oldScore);
            }

            plot.addSeries("YoungRDABMF");
            plot.addSeries("OldRDABMF");

            for (int factors : NUM_FACTORS) {
                Recommender abmf = new RDABMF(datamodel, factors, NUM_ITERS, RANDOM_SEED);
                abmf.fit();

                QualityMeasure ydamae = new DAMAE(abmf, 0);
                QualityMeasure odamae = new DAMAE(abmf, 0, true);

                double youngScore = ydamae.getScore();
                plot.setValue("YoungRDABMF", factors, youngScore);

                double oldScore = odamae.getScore();
                plot.setValue("OldRDABMF", factors, oldScore);
            }

            plot.printData("0", "0.0000");
            plot.draw();

        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }
}