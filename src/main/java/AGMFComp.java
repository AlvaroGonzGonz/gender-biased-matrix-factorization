import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.qualityMeasure.prediction.MAE;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.PMF;
import es.upm.etsisi.cf4j.util.Range;
import es.upm.etsisi.cf4j.util.plot.LinePlot;

public class AGMFComp {
    private static final int[] NUM_FACTORS = Range.ofIntegers(2, 1, 14);
    private static final int NUM_ITERS = 50;
    private static final long RANDOM_SEED = 43L;

    public static void main (String[] args){
        try {
            DataModel datamodel = DataModel.load("ml-1m");

            LinePlot plot = new LinePlot(NUM_FACTORS, "Number of latent factors", "MAE");
            plot.addSeries("AGMF");
            plot.addSeries("PMF");

            for (int numFactors : NUM_FACTORS){
                Recommender agmf = new AGMF(datamodel, numFactors, NUM_ITERS, 7, RANDOM_SEED);
                agmf.fit();

                QualityMeasure agmf_mae = new MAE(agmf);
                plot.setValue("AGMF", numFactors, agmf_mae.getScore());

                Recommender pmf = new PMF(datamodel, numFactors, NUM_ITERS, 0.045, 0.01, RANDOM_SEED);
                pmf.fit();

                QualityMeasure pmf_mae = new MAE(pmf);
                plot.setValue("PMF", numFactors, pmf_mae.getScore());
            }

            plot.draw();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
