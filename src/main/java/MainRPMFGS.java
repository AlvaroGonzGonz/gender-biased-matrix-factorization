import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.qualityMeasure.prediction.MAE;
import es.upm.etsisi.cf4j.util.optimization.GridSearch;
import es.upm.etsisi.cf4j.util.optimization.ParamsGrid;

public class MainRPMFGS {
    public static void main(String[] args) {
        try {
            DataModel datamodel = DataModel.load("ml-1m");

            ParamsGrid grid = new ParamsGrid();

            grid.addParam("NUM_FACTORS", new int[] {10, 30, 50});
            grid.addParam("NUM_ITERS", new int[] {2, 4, 8});
            grid.addParam("LAMBDA", new double[] {0.03, 0.045, 0.06});
            grid.addParam("GAMMA", new double[] {0.001, 0.01, 0.03});

            grid.addParam("NUM_FACTORS_1", new int[] {10, 30, 50});
            grid.addParam("NUM_ITERS_1", new int[] {2, 4, 8});
            grid.addParam("LAMBDA_1", new double[] {0.03, 0.045, 0.06});
            grid.addParam("GAMMA_1", new double[] {0.001, 0.01, 0.03});

            grid.addParam("NUM_FACTORS_2", new int[] {10, 30, 50});
            grid.addParam("NUM_ITERS_2", new int[] {2, 4, 8});
            grid.addParam("LAMBDA_2", new double[] {0.03, 0.045, 0.06});
            grid.addParam("GAMMA_2", new double[] {0.001, 0.01, 0.03});

            grid.addParam("NUM_FACTORS_11", new int[] {10, 30, 50});
            grid.addParam("NUM_ITERS_11", new int[] {2, 4, 8});
            grid.addParam("LAMBDA_11", new double[] {0.03, 0.045, 0.06});
            grid.addParam("GAMMA_11", new double[] {0.001, 0.01, 0.03});

            grid.addParam("NUM_FACTORS_12", new int[] {10, 30, 50});
            grid.addParam("NUM_ITERS_12", new int[] {2, 4, 8});
            grid.addParam("LAMBDA_12", new double[] {0.03, 0.045, 0.06});
            grid.addParam("GAMMA_12", new double[] {0.001, 0.01, 0.03});

            grid.addParam("NUM_FACTORS_21", new int[] {10, 30, 50});
            grid.addParam("NUM_ITERS_21", new int[] {2, 4, 8});
            grid.addParam("LAMBDA_21", new double[] {0.03, 0.045, 0.06});
            grid.addParam("GAMMA_21", new double[] {0.001, 0.01, 0.03});

            grid.addParam("NUM_FACTORS_22", new int[] {10, 30, 50});
            grid.addParam("NUM_ITERS_22", new int[] {2, 4, 8});
            grid.addParam("LAMBDA_22", new double[] {0.03, 0.045, 0.06});
            grid.addParam("GAMMA_22", new double[] {0.001, 0.01, 0.03});

            grid.addFixedParam("seed", 43L);

            GridSearch gs = new GridSearch(datamodel, grid, RPMFGridSearch.class, MAE.class);
            gs.fit();
            gs.printResults(5);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
