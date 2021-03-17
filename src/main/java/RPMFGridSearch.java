import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.recommender.Recommender;

import java.util.Map;

public class RPMFGridSearch extends Recommender {
    private DataModel datamodel;

    private RPMF root;

    //Parameters for root
    private int NUM_FACTORS;
    private int NUM_ITERS;
    private double LAMBDA;
    private double GAMMA;

    //Parameters for child1
    private int NUM_FACTORS_1;
    private int NUM_ITERS_1;
    private double LAMBDA_1;
    private double GAMMA_1;

    //Parameters for child2
    private int NUM_FACTORS_2;
    private int NUM_ITERS_2;
    private double LAMBDA_2;
    private double GAMMA_2;

    //Parameters for child11
    private int NUM_FACTORS_11;
    private int NUM_ITERS_11;
    private double LAMBDA_11;
    private double GAMMA_11;

    //Parameters for child12
    private int NUM_FACTORS_12;
    private int NUM_ITERS_12;
    private double LAMBDA_12;
    private double GAMMA_12;

    //Parameters for child21
    private int NUM_FACTORS_21;
    private int NUM_ITERS_21;
    private double LAMBDA_21;
    private double GAMMA_21;

    //Parameters for child22
    private int NUM_FACTORS_22;
    private int NUM_ITERS_22;
    private double LAMBDA_22;
    private double GAMMA_22;

    private static final long SEED = 43L;

    public RPMFGridSearch(DataModel datamodel, Map<String, Object> params){
        super(datamodel);
        this.datamodel = datamodel;

        this.NUM_FACTORS = (int) params.get("NUM_FACTOR");
        this.NUM_ITERS = (int) params.get("NUM_ITER");
        this.LAMBDA = (double) params.get("LAMBDA");
        this.GAMMA = (double) params.get("GAMMA");

        this.NUM_FACTORS_1 = (int) params.get("NUM_FACTOR_1");
        this.NUM_ITERS_1 = (int) params.get("NUM_ITER_1");
        this.LAMBDA_1 = (double) params.get("LAMBDA_1");
        this.GAMMA_1 = (double) params.get("GAMMA_1");

        this.NUM_FACTORS_2 = (int) params.get("NUM_FACTOR_2");
        this.NUM_ITERS_2 = (int) params.get("NUM_ITER_2");
        this.LAMBDA_2 = (double) params.get("LAMBDA_2");
        this.GAMMA_2 = (double) params.get("GAMMA_2");

        this.NUM_FACTORS_11 = (int) params.get("NUM_FACTOR_11");
        this.NUM_ITERS_11 = (int) params.get("NUM_ITER_11");
        this.LAMBDA_11 = (double) params.get("LAMBDA_11");
        this.GAMMA_11 = (double) params.get("GAMMA_11");

        this.NUM_FACTORS_12 = (int) params.get("NUM_FACTOR_12");
        this.NUM_ITERS_12 = (int) params.get("NUM_ITER_12");
        this.LAMBDA_12 = (double) params.get("LAMBDA_12");
        this.GAMMA_12 = (double) params.get("GAMMA_12");

        this.NUM_FACTORS_21 = (int) params.get("NUM_FACTOR_21");
        this.NUM_ITERS_21 = (int) params.get("NUM_ITER_21");
        this.LAMBDA_21 = (double) params.get("LAMBDA_21");
        this.GAMMA_21 = (double) params.get("GAMMA_21");

        this.NUM_FACTORS_22 = (int) params.get("NUM_FACTOR_22");
        this.NUM_ITERS_22 = (int) params.get("NUM_ITER_22");
        this.LAMBDA_22 = (double) params.get("LAMBDA_22");
        this.GAMMA_22 = (double) params.get("GAMMA_22");

        root = new RPMF(datamodel, "RPMF/Group", 0, NUM_FACTORS, NUM_ITERS, LAMBDA, GAMMA, SEED);

    }

    public void fit(){
        try {
            root.fit();

            root.addChild(1, NUM_FACTORS_1, NUM_ITERS_1, LAMBDA_1, GAMMA_1, SEED);
            root.addChild(2, NUM_FACTORS_2, NUM_ITERS_2, LAMBDA_2, GAMMA_2, SEED);

            for (RPMF child : root.children) {
                child.fit();
            }

            int count = 1;
            for (RPMF child : root.children) {
                if (count == 1) {
                    child.addChild(1, NUM_FACTORS_11, NUM_ITERS_11, LAMBDA_11, GAMMA_11, SEED);
                    child.addChild(2, NUM_FACTORS_12, NUM_ITERS_12, LAMBDA_12, GAMMA_12, SEED);
                } else {
                    child.addChild(1, NUM_FACTORS_21, NUM_ITERS_21, LAMBDA_21, GAMMA_21, SEED);
                    child.addChild(2, NUM_FACTORS_22, NUM_ITERS_22, LAMBDA_22, GAMMA_22, SEED);
                }
                count++;

                for (RPMF grandchild : child.children) {
                    grandchild.fit();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public double predict(int userIndex, int itemIndex){
        return 0.0;
    }

    public double[] predict(TestUser testUser){
        return this.root.predict(testUser);
    }
}
