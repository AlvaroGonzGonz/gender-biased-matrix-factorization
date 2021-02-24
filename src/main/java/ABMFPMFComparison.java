import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.qualityMeasure.prediction.MAE;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.PMF;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.util.Range;
import es.upm.etsisi.cf4j.util.plot.LinePlot;

public class ABMFPMFComparison {
    private static final int[] AGES = {1, 18, 25, 35, 45, 50, 56};
    private static final double[] ALPHA_VALUES = Range.ofDoubles(0.0, 0.2, 51);
    private static final int FACTORS = 10;
    private static final int NUM_ITERS = 300;
    private static final long RANDOM_SEED = 43L;

    public ABMFPMFComparison(){
    }

    public static void main(String[] args){
        try {
            DataModel datamodel = DataModel.load("ml1M");

            for (int age: AGES) {
                LinePlot plot = new LinePlot(ALPHA_VALUES, "Alpha", "MAE");

                plot.addSeries("PMFGeneral");
                plot.addSeries("PMF" + age);
                plot.addSeries("ABMF" + age);

                for (double alpha : ALPHA_VALUES) {

                    Recommender pmf = new PMF(datamodel, FACTORS, NUM_ITERS, RANDOM_SEED);
                    pmf.fit();

                    QualityMeasure pmfmae = new MAE(pmf);
                    plot.setValue("PMFGeneral", alpha, pmfmae.getScore());

                    QualityMeasure pmfdamae = new AMAE(pmf, age);
                    plot.setValue("PMF" + age, alpha, pmfdamae.getScore());

                    Recommender abmf = new ABMF(datamodel, FACTORS, NUM_ITERS, ABMF.DEFAULT_LAMBDA, ABMF.DEFAULT_GAMMA, ABMF.DEFAULT_GAMMA, ABMF.DEFAULT_GAMMA, 1.0, 0.5, alpha, RANDOM_SEED);
                    abmf.fit();

                    QualityMeasure abmfdamae = new AMAE(abmf, age);
                    plot.setValue("ABMF" + age, alpha, abmfdamae.getScore());
                }

                //plot.printData("0", "0.0000");
                plot.draw();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }
}
