import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.PMF;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.util.Range;
import es.upm.etsisi.cf4j.util.plot.LinePlot;

import java.util.Random;

public class RandomComparison {
    private static final int[] PROBABILITIES = {0, 1, 1, 1};
    private static final int[] NUM_FACTORS = Range.ofIntegers(2, 1, 14);
    private static final int NUM_ITERS = 300;
    private static final long RANDOM_SEED = 43L;

    public RandomComparison(){
    }

    public static void main(String[] args){
        try {

            Random random = new Random(RANDOM_SEED);

            DataModel datamodel = DataModel.load("ml1M");

            LinePlot plot = new LinePlot(NUM_FACTORS, "Number of factors", "MAE");

            plot.addSeries("Gm");
            plot.addSeries("GM");

            for (User user : datamodel.getUsers()){
                user.getDataBank().setInt("age", PROBABILITIES[random.nextInt(PROBABILITIES.length)]);
            }

            for (User user : datamodel.getTestUsers()){
                user.getDataBank().setInt("age", PROBABILITIES[random.nextInt(PROBABILITIES.length)]);
            }

            int count0 = 0, count1 = 0;

            for (User user : datamodel.getUsers()){
                if(user.getDataBank().getInt("age") == 1)
                    count1++;
                if(user.getDataBank().getInt("age") == 0)
                    count0++;
            }

            System.out.print("\n===========================");
            System.out.print("\nPorcentaje de usuarios en el grupo minoritario : " + (double) count0/ datamodel.getNumberOfUsers());
            System.out.print("\n===========================\n\n");

            for (int factor : NUM_FACTORS){

                // Evaluate PMF Recommender
                Recommender pmf = new PMF(datamodel, factor, NUM_ITERS, 0.045, 0.01, RANDOM_SEED);
                pmf.fit();

                QualityMeasure g1 = new DAMAE(pmf, 0, true);
                QualityMeasure g2 = new DAMAE(pmf, 0);

                double g1Score = g1.getScore();
                plot.setValue("Gm", factor, g1Score);

                double g2Score = g2.getScore();
                plot.setValue("GM", factor, g2Score);
            }

            plot.printData("0", "0.0000");
            plot.draw();

        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }
}