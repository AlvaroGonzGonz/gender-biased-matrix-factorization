import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.util.Range;
import es.upm.etsisi.cf4j.util.plot.LinePlot;

public class GBMFGridSearch {
    private static final int[] NUM_FACTORS = Range.ofIntegers(2, 1, 14);
    private static final int NUM_ITERS = 500;
    private static final long RANDOM_SEED = 43L;

    public GBMFGridSearch(){
    }

    public static void main(String[] args){
        try {
            DataModel datamodel = DataModel.load("gml100k");

            LinePlot plot = new LinePlot(NUM_FACTORS, "lambda value", "GMAE");

            plot.addSeries("GeneralGBMF");
            plot.addSeries("FemaleGBMF");
            plot.addSeries("MaleGBMF");

            for (int factors : NUM_FACTORS) {
                Recommender gbmf = new GBMF(datamodel, factors, NUM_ITERS, 0.02, 0.001, RANDOM_SEED);
                gbmf.fit();

                QualityMeasure fbgmae = new GMAE(gbmf, 0.0);
                QualityMeasure mbgmae = new GMAE(gbmf, 1.0);
                QualityMeasure bgmae = new GMAE(gbmf);

                double femaleScore = fbgmae.getScore();
                plot.setValue("FemaleGBMF", factors, femaleScore);

                double maleScore = mbgmae.getScore();
                plot.setValue("MaleGBMF", factors, maleScore);

                double generalScore = bgmae.getScore();
                plot.setValue("GeneralGBMF", factors, generalScore);
            }

            plot.printData("0", "0.0000");
            plot.draw();

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
