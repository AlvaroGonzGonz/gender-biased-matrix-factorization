import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.PMF;
import es.upm.etsisi.cf4j.util.plot.LinePlot;

public class AgeFairness {
    private static final int[] AGES = {18, 25, 35, 45, 50, 56};
    private static final int FACTORS = 8;
    private static final int NUM_ITERS = 300;
    private static final long RANDOM_SEED = 43L;

    public AgeFairness(){
    }

    public static void main(String[] args){
        try {

            DataModel datamodel = DataModel.load("ml-1m");

            LinePlot plot = new LinePlot(AGES, "User age", "MAE");

            plot.addSeries("PMF");
            plot.addSeries("nPMF");

            // Evaluate PMF Recommender
            //Recommender pmf = new PMF(datamodel, FACTORS, NUM_ITERS, 0.045, 0.01, RANDOM_SEED);
            //pmf.fit();

            // Evaluate nPMF Recommender
            Recommender npmf = new nPMF(datamodel, FACTORS, NUM_ITERS, 0.045, 0.01, RANDOM_SEED);
            npmf.fit();

            for (int age : AGES) {

                //QualityMeasure pmfmae = new AMAE(pmf, age);
                QualityMeasure npmfmae = new AMAE(npmf, age);

                //double ageScore = pmfmae.getScore();
                //plot.setValue("PMF", age, ageScore);

                double ageScore2 = npmfmae.getScore();
                plot.setValue("nPMF", age, ageScore2);
            }

            plot.printData("0", "0.0000");
            //plot.draw();

        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }
}
