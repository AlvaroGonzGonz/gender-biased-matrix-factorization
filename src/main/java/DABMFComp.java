import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.PMF;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.util.Range;
import es.upm.etsisi.cf4j.util.plot.LinePlot;

public class DABMFComp {
    private static final int[] AGES = {1, 18, 25, 35, 45, 50};
    private static final int[] NUM_FACTORS = Range.ofIntegers(2, 1, 14);
    private static final int NUM_ITERS = 500;
    private static final long RANDOM_SEED = 43L;

    public DABMFComp(){
    }

    public static void main(String[] args){
        try {
            DataModel datamodel = DataModel.load("ml1M");

            for (int age : AGES){
                LinePlot plot = new LinePlot(NUM_FACTORS, "Number of latent factors", "MAE");

                //plot.addSeries("GeneralPMF");
                plot.addSeries("YoungPMF");
                plot.addSeries("OldPMF");

                // Evaluate PMF Recommender
                for (int factors : NUM_FACTORS) {
                    Recommender pmf = new PMF(datamodel, factors, NUM_ITERS, RANDOM_SEED);
                    pmf.fit();

                    QualityMeasure ydamae = new DAMAE(pmf, age);
                    QualityMeasure odamae = new DAMAE(pmf, age, true);

                    double youngScore = ydamae.getScore();
                    plot.setValue("YoungPMF", factors, youngScore);

                    double oldScore = odamae.getScore();
                    plot.setValue("OldPMF", factors, oldScore);
                }

                //plot.addSeries("GeneralPMF");
                plot.addSeries("YoungDABMF");
                plot.addSeries("OldDABMF");

                // Evaluate PMF Recommender
                for (int factors : NUM_FACTORS) {
                    Recommender dabmf = new DABMF(datamodel, age, factors, NUM_ITERS, RANDOM_SEED);
                    dabmf.fit();

                    QualityMeasure ydamae = new DAMAE(dabmf, age);
                    QualityMeasure odamae = new DAMAE(dabmf, age, true);

                    double youngScore = ydamae.getScore();
                    plot.setValue("YoungDABMF", factors, youngScore);

                    double oldScore = odamae.getScore();
                    plot.setValue("OldDABMF", factors, oldScore);
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