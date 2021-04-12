import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.PMF;
import es.upm.etsisi.cf4j.util.plot.LinePlot;

public class RPMFComp {
    private static final int NUM_FACTORS = 2;
    private static final int NUM_ITERS = 300;
    private static final double LAMBDA = 0.1;
    private static final double GAMMA = 0.015;
    private static final long SEED = 43L;
    private static final int[] AGES = {1, 18, 25, 35, 45, 50, 56};

    public RPMFComp(){
    }

    public static void main(String[] args){
        try {
            DataModel datamodel = DataModel.load("ml-1m");

            LinePlot plot = new LinePlot(AGES, "User age", "MAE");

            RPMF rpmf = new RPMF(datamodel, "RPMF/Group", 0, 9, NUM_ITERS, 0.045, 0.01, SEED);
            rpmf.fit();

            plot.addSeries("RPMF");

            for(int age : AGES){
                QualityMeasure pmfmae = new AMAE(rpmf, age);
                plot.setValue("RPMF", age, pmfmae.getScore());
            }

            rpmf.addChild(1, NUM_FACTORS, NUM_ITERS, LAMBDA, GAMMA, SEED);
            rpmf.addChild(2, NUM_FACTORS, NUM_ITERS, LAMBDA, GAMMA, SEED);

            for(RPMF child : rpmf.children) {
                child.fit();
            }

            plot.addSeries("RPMF-children");

            for(int age : AGES){
                QualityMeasure pmfmae = new AMAE(rpmf, age);
                plot.setValue("RPMF-children", age, pmfmae.getScore());
            }

            for(RPMF child : rpmf.children) {
                child.addChild(1, NUM_FACTORS, NUM_ITERS, LAMBDA, GAMMA, SEED);
                child.addChild(2, NUM_FACTORS, NUM_ITERS, LAMBDA, GAMMA, SEED);

                for(RPMF grandchild : child.children){
                    grandchild.fit();
                }
            }

            plot.addSeries("RPMF-grandchildren");

            for(int age : AGES){
                QualityMeasure pmfmae = new AMAE(rpmf, age);
                plot.setValue("RPMF-grandchildren", age, pmfmae.getScore());
            }

            PMF pmf = new PMF(datamodel, 9, 100, 0.045, 0.01, SEED);
            pmf.fit();

            plot.addSeries("PMF");

            for(int age : AGES){
                QualityMeasure pmfmae = new AMAE(pmf, age);
                plot.setValue("PMF", age, pmfmae.getScore());
            }

            plot.printData("0", "0.0000");
            plot.draw();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
