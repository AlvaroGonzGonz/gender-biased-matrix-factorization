import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.PMF;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.util.Range;
import es.upm.etsisi.cf4j.util.plot.LinePlot;

public class ABMFPMFComparison {
    private static final int[] NUM_FACTORS = Range.ofIntegers(2, 1, 14);
    private static final int NUM_ITERS = 500;
    private static final long RANDOM_SEED = 43L;

    public ABMFPMFComparison(){
    }

    public static void main(String[] args){
        try {
            DataModel datamodel = DataModel.load("gml1M");

            LinePlot plot = new LinePlot(NUM_FACTORS, "Number of latent factors", "MAE");

            //plot.addSeries("GeneralPMF");
            plot.addSeries("YoungPMF");
            plot.addSeries("OldPMF");

            // Evaluate PMF Recommender
            for (int factors : NUM_FACTORS) {
                Recommender pmf = new PMF(datamodel, factors, NUM_ITERS, RANDOM_SEED);
                pmf.fit();

                QualityMeasure yamae = new AMAE(pmf, new double[]{0.0, 0.449});
                QualityMeasure oamae = new AMAE(pmf, new double[]{0.45, 1.0});
                //QualityMeasure gamae = new AMAE(pmf);

                double youngScore = yamae.getScore();
                plot.setValue("YoungPMF", factors, youngScore);

                double oldScore = oamae.getScore();
                plot.setValue("OldPMF", factors, oldScore);

                //double generalScore = gamae.getScore();
                //plot.setValue("GeneralPMF", factors, generalScore);
            }

            //plot.addSeries("GeneralGBMF");
            plot.addSeries("YoungABMF");
            plot.addSeries("OldABMF");

            // Evaluate GBMF Recommender
            for (int factors : NUM_FACTORS) {
                Recommender abmf = new GBMF(datamodel, factors, NUM_ITERS, RANDOM_SEED);
                abmf.fit();

                QualityMeasure yamae = new AMAE(abmf, new double[]{0.0, 0.449});
                QualityMeasure oamae = new AMAE(abmf, new double[]{0.45, 1.0});
                //QualityMeasure gamae = new AMAE(abmf);

                double youngScore = yamae.getScore();
                plot.setValue("YoungABMF", factors, youngScore);

                double oldScore = oamae.getScore();
                plot.setValue("OldABMF", factors, oldScore);

                //double generalScore = gamae.getScore();
                //plot.setValue("GeneralABMF", factors, generalScore);
            }

            plot.printData("0", "0.0000");
            plot.draw();
        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }
}
