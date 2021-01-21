import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.PMF;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.util.Range;
import es.upm.etsisi.cf4j.util.plot.LinePlot;

public class DABMFPMFComparison {
    private static final int[] AGES = {1, 18, 25, 35, 45, 50, 56};
    private static final int[] NUM_FACTORS = Range.ofIntegers(2, 1, 2);
    private static final int NUM_ITERS = 500;
    private static final long RANDOM_SEED = 43L;

    public DABMFPMFComparison(){
    }

    public static void main(String[] args){
        try {
            DataModel datamodel = DataModel.load("gml1M");

            LinePlot plot = new LinePlot(NUM_FACTORS, "Number of latent factors", "MAE");

            for(int age: AGES) {
                //plot.addSeries("GeneralPMF");
                plot.addSeries("YoungPMF" + age);
                plot.addSeries("OldPMF" + age);

                // Evaluate PMF Recommender
                for (int factors : NUM_FACTORS) {
                    Recommender pmf = new PMF(datamodel, factors, NUM_ITERS, RANDOM_SEED);
                    pmf.fit();

                    QualityMeasure yamae = new DAMAE(pmf, age);
                    QualityMeasure oamae = new DAMAE(pmf, age, true);

                    double youngScore = yamae.getScore();
                    plot.setValue("YoungPMF" + age, factors, youngScore);

                    double oldScore = oamae.getScore();
                    plot.setValue("OldPMF" + age, factors, oldScore);
                }

                //plot.addSeries("GeneralGBMF");
                plot.addSeries("YoungABMF" + age);
                plot.addSeries("OldABMF" + age);

                // Evaluate GBMF Recommender
                for (int factors : NUM_FACTORS) {
                    Recommender abmf = new GBMF(datamodel, factors, NUM_ITERS, RANDOM_SEED);
                    abmf.fit();

                    QualityMeasure yamae = new DAMAE(abmf, age);
                    QualityMeasure oamae = new DAMAE(abmf, age, true);

                    double youngScore = yamae.getScore();
                    plot.setValue("YoungABMF" + age, factors, youngScore);

                    double oldScore = oamae.getScore();
                    plot.setValue("OldABMF" + age, factors, oldScore);
                }

                plot.printData("0", "0.0000");
                plot.draw();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }
}
