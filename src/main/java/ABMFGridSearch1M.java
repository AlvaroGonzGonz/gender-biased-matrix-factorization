import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.util.optimization.GridSearch;
import es.upm.etsisi.cf4j.util.optimization.ParamsGrid;

import java.util.HashMap;
import java.util.Map;

public class ABMFGridSearch1M {
    public static void main(String[] args) {
        try {
            DataModel datamodel = DataModel.load("cml1M");

            ParamsGrid grid = new ParamsGrid();

            grid.addParam("numIters", new int[] {500});
            grid.addParam("numFactors", new int[] {2, 4, 6, 8, 10});
            grid.addParam("lambda", new double[] {0.05});
            grid.addParam("gamma", new double[] {0.002, 0.0025, 0.003});
            grid.addParam("etaf", new double[] {0.001, 0.0015, 0.002});
            grid.addParam("etam", new double[] {0.001, 0.002, 0.003});

            grid.addFixedParam("seed", 43L);

            Map<String, Object> qmparams = new HashMap<>();
            qmparams.put("age", new double[]{0.0, 1.0});

            GridSearch gs = new GridSearch(datamodel, grid, ABMF.class, AMAE.class, qmparams);
            gs.fit();
            gs.printResults(5);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
