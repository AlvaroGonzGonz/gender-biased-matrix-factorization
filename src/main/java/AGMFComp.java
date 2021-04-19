import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.qualityMeasure.prediction.MAE;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.PMF;
import es.upm.etsisi.cf4j.util.Range;
import es.upm.etsisi.cf4j.util.plot.LinePlot;

public class AGMFComp {
    private static final int[] AGES = {1, 18, 25, 35, 45, 50, 56};
    private static final int NUM_ITERS = 100;
    private static final long RANDOM_SEED = 43L;

    public static void main (String[] args){
        try {
            DataModel datamodel = DataModel.load("ml-1m");

            LinePlot plot = new LinePlot(AGES, "Age group", "MAE");

            plot.addSeries("PMF");
            plot.addSeries("AGMF");

            Recommender agmf = new AGMF(datamodel, 4, NUM_ITERS, 7, 0.01, 0.09, RANDOM_SEED);
            agmf.fit();

            Recommender pmf = new PMF(datamodel, 7, NUM_ITERS, 0.045, 0.01, RANDOM_SEED);
            pmf.fit();

            for (int age : AGES) {
                QualityMeasure agmf_mae = new AMAE(agmf, age);
                plot.setValue("AGMF", age, agmf_mae.getScore());

                QualityMeasure pmf_mae = new AMAE(pmf, age);
                plot.setValue("PMF", age, pmf_mae.getScore());
            }

            plot.printData("0", "0.0000");
            plot.draw();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
