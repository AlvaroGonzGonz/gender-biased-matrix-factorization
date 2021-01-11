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

            grid.addParam("numIters", new int[] {25, 50, 75, 100, 200, 500});
            grid.addParam("numFactors", new int[] {2, 4, 6, 8, 10, 15});
            grid.addParam("lambda", new double[] {0.01, 0.02, 0.05, 0.10, 0.15});
            grid.addParam("gamma", new double[] {0.001, 0.005, 0.01, 0.03, 0.05, 0.07, 0.1});

            grid.addFixedParam("seed", 43L);

            Map<String, Object> qmparams = new HashMap<>();
            qmparams.put("gender", 0.0);

            GridSearch gs = new GridSearch(datamodel, grid, GBMF.class, GMAE.class, qmparams);
            gs.fit();
            gs.printResults(5);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
