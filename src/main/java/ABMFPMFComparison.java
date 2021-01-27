import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.PMF;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.util.Range;
import es.upm.etsisi.cf4j.util.plot.LinePlot;

public class ABMFPMFComparison {
    private static final int[] TIERS = {1, 2, 3};
    private static final int[] AGES = {1, 18, 25, 35, 45, 50, 56};
    private static final int[] NUM_FACTORS = Range.ofIntegers(2, 1, 14);
    private static final int NUM_ITERS = 500;
    private static final long RANDOM_SEED = 43L;

    public ABMFPMFComparison(){
    }

    public static void main(String[] args){
        try {
            DataModel datamodel = DataModel.load("ml1M");

            for (int tier : TIERS) {

                LinePlot plot = new LinePlot(NUM_FACTORS, "Number of latent factors", "MAE");

                for (int age : AGES) {
                    plot.addSeries("Tier" + tier + "Age" + age);
                }

                // Evaluate PMF Recommender
                for (int factors : NUM_FACTORS) {
                    Recommender pmf = new PMF(datamodel, factors, NUM_ITERS, RANDOM_SEED);
                    pmf.fit();

                    Recommender abmf = new ABMF(datamodel, tier, factors, NUM_ITERS, RANDOM_SEED);
                    abmf.fit();

                    for (int age : AGES) {
                        QualityMeasure pmfamae = new AMAE(pmf, age);
                        QualityMeasure abmfamae = new AMAE(abmf, age);

                        plot.setValue("Age" + age, factors, pmfamae.getScore() - abmfamae.getScore());
                    }
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
