import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.util.Range;
import org.tc33.jheatchart.HeatChart;

import java.io.File;

public class ABMFHeatMap {
    private static final int[] AGES = {1, 18, 25, 35, 45, 50, 56};
    private static final double[] C1 = Range.ofDoubles(0.1, 0.1, 30);
    private static final double[] C2 = Range.ofDoubles(0.05, 0.05, 19);
    private static final int FACTORS = 10;
    private static final int NUM_ITERS = 300;
    private static final long RANDOM_SEED = 43L;
    protected static final double DEFAULT_GAMMA = 0.001;
    protected static final double DEFAULT_LAMBDA = 0.05;

    public ABMFHeatMap(){
    }

    public static void main(String[] args){
        try {
            DataModel datamodel = DataModel.load("ml1M");

            double[][][] heatMaps = new double[AGES.length][C1.length][C2.length];

            for (int j = 0; j<C1.length; j++){
                for (int k = 0; k<C2.length; k++) {
                    Recommender abmf = new ABMF(datamodel, FACTORS, NUM_ITERS, DEFAULT_LAMBDA, DEFAULT_GAMMA, DEFAULT_GAMMA, DEFAULT_GAMMA, C1[j], C2[k], RANDOM_SEED);
                    abmf.fit();

                    for (int i = 0; i<AGES.length; i++) {
                        QualityMeasure abmfamae = new AMAE(abmf, AGES[i]);
                        heatMaps[i][j][k]= abmfamae.getScore();
                    }
                }
            }

            for (int i=0; i<AGES.length; i++){
                HeatChart map = new HeatChart(heatMaps[i]);

                map.setTitle("Heat Map for Age " + AGES[i]);
                map.setXAxisLabel("C1");
                map.setYAxisLabel("C2");

                map.saveToFile(new File("HeatMapAge" + AGES[i] + ".png"));
            }

        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }
}
