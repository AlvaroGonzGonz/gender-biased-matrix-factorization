import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.qualityMeasure.prediction.MAE;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.PMF;
import es.upm.etsisi.cf4j.util.plot.LinePlot;

public class AGMFConvergence {
    private static final int[] ITERS = {10, 20, 30, 40, 50, 100, 150, 200};

    public static void main (String[] args){
        try {
            DataModel datamodel = DataModel.load("ml-1m");

            LinePlot plot = new LinePlot(ITERS, "Iterations", "MAE");

            plot.addSeries("PMF");
            plot.addSeries("AGMF");

            for(int iters : ITERS) {
                AGMF agmf = new AGMF(datamodel, 9, iters, 7, 0.01, 0.08, 43L);
                agmf.fit();

                QualityMeasure agmf_mae = new MAE(agmf);
                plot.setValue("AGMF", iters, agmf_mae.getScore());

                Recommender pmf = new PMF(datamodel, 9, iters, 0.045, 0.01, 43L);
                pmf.fit();

                QualityMeasure pmf_mae = new MAE(pmf);
                plot.setValue("PMF", iters, pmf_mae.getScore());
            }

            plot.printData("0", "0.0000");
            plot.draw();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
