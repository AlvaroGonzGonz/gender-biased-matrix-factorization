import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.util.Range;
import org.tc33.jheatchart.HeatChart;

import java.awt.*;
import java.io.File;
import java.util.Arrays;

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

                //Heat Map image
                HeatChart map = new HeatChart(heatMaps[i]);

                map.setTitle("Heat Map for Age " + AGES[i]);

                map.setXAxisLabel("C2");
                map.setXValues(C2[0], C2[1]-C2[0]);
                map.setYAxisLabel("C1");
                map.setYValues(C1[0], C1[1]-C1[0]);

                map.setHighValueColour(new Color(255, 0 ,0));
                map.setLowValueColour(new Color(0, 0, 255));

                map.saveToFile(new File("Graphics/HeatMaps/HeatMapAge" + AGES[i] + ".png"));

                //Gradient image
                double[][] gradient = getGradient(heatMaps[i]);

                map = new HeatChart(gradient);

                map.setTitle("Gradient for Age" + AGES[i]);

                map.setYAxisLabel("Values");
                map.setYValues(gradient[0][0], gradient[gradient.length-1][0]);

                map.setHighValueColour(new Color(255, 0 ,0));
                map.setLowValueColour(new Color(0, 0, 255));

                map.saveToFile(new File("Graphics/HeatMaps/HeatMapAge" + AGES[i] + "Gradient.png"));
            }

            for (int i=0; i<AGES.length; i++) {
                double error = Double.MAX_VALUE;
                double c1 = 0d, c2 = 0d;
                for(int j=0; j<heatMaps[0].length; j++) {
                    for (int k=0; k < heatMaps[0][0].length; k++) {
                        if(heatMaps[i][j][k] < error){
                            error = heatMaps[i][j][k];
                            c1 = C1[j];
                            c2 = C2[k];
                        }
                    }
                }

                System.out.print("\nPara Edad " + AGES[i] + " el menor error es: " + error + " con C1 = " + c1 + " y C2 = " + c2);
            }

        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }

    private static double[][] getGradient(double[][] matrix){

        Double[] matrix2array = new Double[matrix.length* matrix[0].length];
        for(int i=0; i< matrix.length; i++){
            for(int j=0; j<matrix[0].length; j++){
                matrix2array[i*matrix[0].length+j] = matrix[i][j];
            }
        }

        Object[] unique = Arrays.stream(matrix2array).distinct().sorted().toArray();
        double[] array = new double[unique.length];

        for(int i=0; i< unique.length; i++)
            array[i] = Double.parseDouble(unique[i].toString());

        double[][] gradient = new double[30][1];

        int count = 0;
        for(int i=0; i< array.length; i++){
            if((i+1)%19 == 0) {
                gradient[count][0] = array[i];
                count++;
            }
        }

        return gradient;
    }
}
