import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.qualityMeasure.prediction.MAE;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.util.Range;
import es.upm.etsisi.cf4j.util.plot.LinePlot;


public class Plot3D{
    private static final double[] ALPHA_VALUES = Range.ofDoubles(0.0, 0.1, 101);
    private static final double[] C2 = Range.ofDoubles(0.0, 0.1, 11);
    private static final int FACTORS = 10;
    private static final int NUM_ITERS = 30;
    private static final long RANDOM_SEED = 43L;

    public static void main(String[] args){
        try {
            double min = Double.MAX_VALUE;
            int alpha = 0, c2 = 0;
            DataModel datamodel = DataModel.load("ml1M");

            LinePlot plot = new LinePlot(C2, "Alpha", "MAE");

            for (int i=0; i<ALPHA_VALUES.length; i++){
                plot.addSeries("alpha=" + ALPHA_VALUES[i]);
                for (int j=0; j<C2.length; j++){
                    Recommender abmf = new ABMF(datamodel, FACTORS, NUM_ITERS, ABMF.DEFAULT_LAMBDA, ABMF.DEFAULT_GAMMA, ABMF.DEFAULT_GAMMA, ABMF.DEFAULT_GAMMA, 1.0, C2[j], ALPHA_VALUES[i],RANDOM_SEED);
                    abmf.fit();
                    QualityMeasure abmfmae = new MAE(abmf);

                    plot.setValue("alpha=" + ALPHA_VALUES[i], C2[j], abmfmae.getScore());

                    if(abmfmae.getScore() < min){
                        min = abmfmae.getScore();
                        alpha = i;
                        c2 = j;
                    }
                }
            }

            plot.printData("0", "0.0000");
            System.out.println("El menor error (" + min + ") se consigue en alpha=" + alpha + " y c2=" + c2);

        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }
}