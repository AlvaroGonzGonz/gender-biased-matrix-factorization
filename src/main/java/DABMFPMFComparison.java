import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.PMF;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.util.Range;
import es.upm.etsisi.cf4j.util.plot.LinePlot;

public class DABMFPMFComparison {
    private static final int[] AGES = {1, 18, 25, 35, 45, 50};
    private static final int[] NUM_FACTORS = Range.ofIntegers(2, 1, 5);
    private static final int NUM_ITERS = 500;
    private static final long RANDOM_SEED = 43L;

    public DABMFPMFComparison(){
    }

    public static void main(String[] args){
        try {
            DataModel datamodel = DataModel.load("ml1M");

            LinePlot plot = new LinePlot(NUM_FACTORS, "Number of latent factors", "MAE");

            for(int age: AGES) {

                plot.addSeries("Young" + age);
                plot.addSeries("Old" + age);

                // Evaluate PMF Recommender
                for (int factors : NUM_FACTORS) {
                    Recommender pmf = new PMF(datamodel, factors, NUM_ITERS, RANDOM_SEED);
                    pmf.fit();

                    Recommender abmf = new GBMF(datamodel, factors, NUM_ITERS, RANDOM_SEED);
                    abmf.fit();

                    QualityMeasure pmfyamae = new DAMAE(pmf, age);
                    QualityMeasure pmfoamae = new DAMAE(pmf, age, true);

                    QualityMeasure abmfyamae = new DAMAE(abmf, age);
                    QualityMeasure abmfoamae = new DAMAE(abmf, age, true);

                    double youngScore = pmfyamae.getScore() - abmfyamae.getScore();
                    plot.setValue("Young" + age, factors, youngScore);

                    double oldScore = pmfoamae.getScore() - abmfoamae.getScore();
                    plot.setValue("Old" + age, factors, oldScore);
                }
            }

            plot.printData("0", "0.0000");
            plot.draw();

        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }
}
