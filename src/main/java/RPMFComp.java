import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.qualityMeasure.prediction.MAE;

public class RPMFComp {
    private static final int NUM_FACTORS = 6;
    private static final int NUM_ITERS = 500;
    private static final double LAMBDA = 0.01;
    private static final double GAMMA = 0.045;
    private static final long SEED = 43L;

    public RPMFComp(){
    }

    public static void main(String[] args){
        try {
            DataModel datamodel = DataModel.load("ml-1m");

            RPMF rpmf = new RPMF(datamodel, "RPMF/Group", new double[]{0.0, 100.0}, NUM_FACTORS, NUM_ITERS, LAMBDA, GAMMA, SEED);
            rpmf.fit();
            rpmf.addChild(1, new double[]{0.0, 34.64}, NUM_FACTORS, NUM_ITERS, LAMBDA, GAMMA, SEED);
            rpmf.addChild(2, new double[]{34.64, 100.0}, NUM_FACTORS, NUM_ITERS, LAMBDA, GAMMA, SEED);

            double[][] aux = new double[][]{{0.0, 26.01},
                                            {26.01, 34.64},
                                            {34.64, 45.93},
                                            {45.93, 100.0}};
            int count = 0;

            for(RPMF child : rpmf.children) {
                child.fit();

                child.addChild(1, aux[count], NUM_FACTORS, NUM_ITERS, LAMBDA, GAMMA, SEED);
                count++;
                child.addChild(2, aux[count], NUM_FACTORS, NUM_ITERS, LAMBDA, GAMMA, SEED);
                count++;

                for(RPMF grandchild : child.children){
                    grandchild.fit();
                }
            }

            QualityMeasure rpmfmae = new MAE(rpmf);

            System.out.println(rpmfmae.getScore());

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
