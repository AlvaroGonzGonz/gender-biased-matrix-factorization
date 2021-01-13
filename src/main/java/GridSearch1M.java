import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.util.optimization.GridSearch;
import es.upm.etsisi.cf4j.util.optimization.ParamsGrid;

import java.util.HashMap;
import java.util.Map;

public class GridSearch1M {
    public static void main(String[] args) {
        try {
            DataModel datamodel = DataModel.load("gml1M");

            ParamsGrid grid = new ParamsGrid();

            grid.addParam("numIters", new int[] {500});
            grid.addParam("numFactors", new int[] {4});
            grid.addParam("lambda", new double[] {0.025, 0.026, 0.027, 0.028, 0.029, 0.03});
            grid.addParam("gamma", new double[] {0.0014, 0.0015, 0.0016});
            grid.addParam("etaf", new double[] {0.0015, 0.002, 0.0025});
            grid.addParam("etam", new double[] {0.0006, 0.00065, 0.0007});

            grid.addFixedParam("seed", 43L);

            //Map<String, Object> qmparams = new HashMap<>();
            //qmparams.put("gender", 0.0);

            GridSearch gs = new GridSearch(datamodel, grid, GBMF.class, GMAE.class);
            gs.fit();
            gs.printResults(5);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
