import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.PMF;
import es.upm.etsisi.cf4j.util.Range;
import es.upm.etsisi.cf4j.util.plot.LinePlot;

public class EmbeddingsMovieLens {
    private static final int[] GROUP = Range.ofIntegers(0, 1, 10);

    public EmbeddingsMovieLens(){
    }

    public static void main(String[] args){

        try {
            String path = "data/ml1M_embeddings_group_";
            LinePlot plot = new LinePlot(GROUP, "Embedding group", "MAE");

            plot.addSeries("minority group MAE");
            plot.addSeries("Majority group MAE");

            for (int grupo : GROUP) {
                DataModel datamodel = DataModel.load(path + grupo);

                Recommender pmf = new PMF(datamodel, 4, 300, 0.045, 0.002, 43L);
                pmf.fit();

                QualityMeasure maem = new EmbeddingsMAE(pmf, "m");
                QualityMeasure maeM = new EmbeddingsMAE(pmf, "M");

                plot.setValue("minority group MAE", grupo, maem.getScore());
                plot.setValue("Majority group MAE", grupo, maeM.getScore());

            }

            plot.printData("0", "0.0000");

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}