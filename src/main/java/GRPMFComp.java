import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.util.plot.LinePlot;

public class GRPMFComp {
    private static final int NUM_FACTORS = 2;
    private static final int NUM_ITERS = 50;
    private static final double LAMBDA = 0.045;
    private static final double GAMMA = 0.01;
    private static final long SEED = 43L;
    private static final int[] GENDER = {0, 1};

    public GRPMFComp(){
    }

    public static void main(String[] args){
        try {
            DataModel datamodel = DataModel.load("ml-1m");

            LinePlot plot = new LinePlot(GENDER, "Gender", "MAE");

            RPMF rpmf = new RPMF(datamodel, "GRPMF/Group", 0, NUM_FACTORS, NUM_ITERS, LAMBDA, GAMMA, SEED);
            rpmf.fit();

            plot.addSeries("GRPMF");

            QualityMeasure pmfmae = new GMAE(rpmf, 0.0);
            plot.setValue("GRPMF", 0.0, pmfmae.getScore());

            pmfmae = new GMAE(rpmf, 1.0);
            plot.setValue("GRPMF", 1.0, pmfmae.getScore());

            rpmf.addChild(1, NUM_FACTORS, NUM_ITERS, LAMBDA, GAMMA, SEED);
            rpmf.addChild(2, NUM_FACTORS, NUM_ITERS, LAMBDA, GAMMA, SEED);

            for (RPMF child : rpmf.children) {
                child.fit();
            }

            plot.addSeries("GRPMF-children");

            pmfmae = new GMAE(rpmf, 0.0);
            plot.setValue("GRPMF-children", 0.0, pmfmae.getScore());

            pmfmae = new GMAE(rpmf, 1.0);
            plot.setValue("GRPMF-children", 1.0, pmfmae.getScore());

            plot.printData("0", "0.0000");
            plot.draw();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
