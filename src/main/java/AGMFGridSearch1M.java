import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.qualityMeasure.prediction.MAE;
import es.upm.etsisi.cf4j.util.optimization.GridSearch;
import es.upm.etsisi.cf4j.util.optimization.ParamsGrid;

public class AGMFGridSearch1M {

    public static void main (String[] args){
        try{
            DataModel datamodel = DataModel.load("ml-1m");

            ParamsGrid params = new ParamsGrid();

            params.addParam("numFactors", new int[] {4, 6, 7, 9});
            params.addParam("numIters", new int[] {100});
            params.addParam("numGroups", new int[] {2, 4, 5, 7});
            params.addParam("lambda", new double[] {0.005, 0.01, 0.015});
            params.addParam("gamma", new double[] {0.08, 0.09, 0.1});

            params.addFixedParam("seed", 43L);

            GridSearch gs = new GridSearch(datamodel, params, AGMF.class, MAE.class);
            gs.fit();
            gs.printResults(5);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
