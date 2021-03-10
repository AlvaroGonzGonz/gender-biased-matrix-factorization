import es.upm.etsisi.cf4j.data.DataModel;

public class RPMFComp {
    private static final int NUM_FACTORS = 8;
    private static final int NUM_ITERS = 30;
    private static final double LAMBDA = 0.01;
    private static final double GAMMA = 0.045;
    private static final long SEED = 43L;

    public RPMFComp(){
    }

    public static void main(String[] args){
        try {
            DataModel datamodel = DataModel.load("ml-1m");

            RPMF rpmf = new RPMF(datamodel, "RPMF/Group", NUM_FACTORS, NUM_ITERS, LAMBDA, GAMMA, SEED);
            rpmf.fit();
            for(int i=0; i<2; i++) {
                rpmf.addChild(i+1, NUM_FACTORS, NUM_ITERS, LAMBDA, GAMMA, SEED);
            }

            for(RPMF child : rpmf.children) {
                child.fit();
                for (int i=1; i < 3; i++) {
                    child.addChild(i, NUM_FACTORS, NUM_ITERS, LAMBDA, GAMMA, SEED);
                }
                for(RPMF grandchild : child.children){
                    grandchild.fit();
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
