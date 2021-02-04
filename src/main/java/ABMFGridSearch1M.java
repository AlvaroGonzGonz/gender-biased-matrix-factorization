import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.util.optimization.GridSearch;
import es.upm.etsisi.cf4j.util.optimization.ParamsGrid;

import java.util.HashMap;
import java.util.Map;

public class ABMFGridSearch1M {
    public static void main(String[] args) {
        try {
            DataModel datamodel = DataModel.load("ml1M");

            ParamsGrid grid = new ParamsGrid();

            grid.addParam("ntier", new int[] {1, 2, 3});
            grid.addParam("numIters", new int[] {500});
            grid.addParam("numFactors", new int[] {2, 4, 6, 8, 10});
            grid.addParam("lambda", new double[] {0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.07, 0.08});
            grid.addParam("gamma", new double[] {0.0015, 0.002, 0.0021, 0.0022, 0.0023, 0.0024, 0.0025, 0.0026, 0.0027, 0.003});
            grid.addParam("etaf", new double[] {0.001, 0.0012, 0.0014, 0.0015, 0.0017, 0.002, 0.0021, 0.0022, 0.0023, 0.0024, 0.0025});
            grid.addParam("etam", new double[] {0.001, 0.0012, 0.0014, 0.0015, 0.0017, 0.002, 0.0021, 0.0022, 0.0023, 0.0024, 0.0025, 0.0026, 0.0027, 0.003});

            grid.addFixedParam("seed", 43L);

            Map<String, Object> qmparams = new HashMap<>();
            qmparams.put("age", 0);

            GridSearch gs = new GridSearch(datamodel, grid, ABMF.class, AMAE.class, qmparams);
            gs.fit();
            gs.printResults(5);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
