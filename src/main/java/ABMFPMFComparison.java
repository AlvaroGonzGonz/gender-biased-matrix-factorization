import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.PMF;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.util.Range;
import es.upm.etsisi.cf4j.util.plot.LinePlot;

public class ABMFPMFComparison {
    private static final int[] TIERS = {1, 2, 3};
    private static final int[] AGES = {1, 18, 25, 35, 45, 50, 56};
    private static final int FACTORS = 10;
    private static final int NUM_ITERS = 300;
    private static final long RANDOM_SEED = 43L;

    public ABMFPMFComparison(){
    }

    public static void main(String[] args){
        try {
            DataModel datamodel = DataModel.load("ml1M");

            LinePlot plot = new LinePlot(AGES, "Number of latent factors", "MAE");

            plot.addSeries("PMF");

            for (int tier: TIERS) {
                plot.addSeries("Tier" + tier);
            }

            for (int age : AGES) {

                Recommender pmf = new PMF(datamodel, FACTORS, NUM_ITERS, RANDOM_SEED);
                pmf.fit();

                QualityMeasure pmfamae = new AMAE(pmf, age);
                plot.setValue("PMF", age, pmfamae.getScore());

                for (int tier: TIERS){

                    Recommender abmf = new ABMF(datamodel, tier, FACTORS, NUM_ITERS, RANDOM_SEED);
                    abmf.fit();

                    QualityMeasure abmfamae = new AMAE(abmf, age);

                    plot.setValue("Tier" + tier, age, abmfamae.getScore());
                }
            }

            plot.printData("0", "0.0000");
        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }
}
