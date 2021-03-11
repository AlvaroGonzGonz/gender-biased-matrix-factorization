import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.qualityMeasure.prediction.MAE;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.PMF;

public class Prueba {
    private static final int NUM_FACTORS = 9;
    private static final int NUM_ITERS = 50;
    private static final double LAMBDA = 0.045;
    private static final double GAMMA = 0.01;
    private static final long SEED = 43L;
    private static final int[] AGES = {1, 18, 25, 35, 45, 50, 56};

    public Prueba(){
    }

    public static void main(String[] args){
        try{
            DataModel datamodel = DataModel.load("RPMF/Group/ml-1m-2");

            PMF pmf = new PMF(datamodel, NUM_FACTORS, NUM_ITERS, LAMBDA, GAMMA, SEED);
            pmf.fit();

            QualityMeasure qmae = new MAE(pmf);
            System.out.println("\nEl MAE es " + qmae.getScore());

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
