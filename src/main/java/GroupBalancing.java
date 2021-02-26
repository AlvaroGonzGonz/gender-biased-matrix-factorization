import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.PMF;
import es.upm.etsisi.cf4j.util.Range;
import es.upm.etsisi.cf4j.util.plot.LinePlot;

public class GroupBalancing {
    private static final int[] AGES = {1, 18, 25, 35, 45, 50, 56};
    private static final int[] NUM_FACTORS = Range.ofIntegers(2, 1, 14);
    private static final int NUM_ITERS = 500;
    private static final long RANDOM_SEED = 43L;

    public GroupBalancing(){
    }

    public static void main (String[] args){
        try{
            DataModel datamodel = DataModel.load("ml-1m-subsampling");

            LinePlot plot = new LinePlot(NUM_FACTORS, "Number of factors", "MAE");

            for( int age : AGES){
                plot.addSeries("Age" + age);
            }

            for (int FACTORS : NUM_FACTORS){
                Recommender pmf = new PMF(datamodel, FACTORS, NUM_ITERS, 0.045, 0.01, RANDOM_SEED);
                pmf.fit();

                for( int age : AGES){
                    QualityMeasure pmfamae = new AMAE(pmf, age);
                    plot.setValue("Age" + age, FACTORS, pmfamae.getScore());
                }

            }

            plot.printData("0", "0.0000");
            plot.draw();

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
