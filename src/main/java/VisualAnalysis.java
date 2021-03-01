import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.PMF;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;


public class VisualAnalysis {
    private static final int NUM_ITERS = 500;
    private static final long RANDOM_SEED = 43L;
    private static final int FACTORS = 8;

    public VisualAnalysis(){
    }

    public static void main (String[] args){
        FileWriter fichero = null;
        PrintWriter pw = null;

        try{
            fichero = new FileWriter("userFactors");
            pw = new PrintWriter(fichero);

            DataModel datamodel = DataModel.load("ml1M");

            PMF pmf = new PMF(datamodel, FACTORS, NUM_ITERS, 0.045, 0.01, RANDOM_SEED);
            pmf.fit();

            for (User user : datamodel.getUsers()){
                double[] array = pmf.getUserFactors(user.getUserIndex());
                String[] linea = new String[FACTORS+2];
                linea[0] = user.getDataBank().getDouble("gender") == 1.0? "M" : "F";
                linea[1] = String.valueOf(user.getDataBank().getInt("age"));
                for (int i=0; i<FACTORS; i++){
                    linea[i+2] = String.valueOf(array[i]);
                }
                linea[linea.length-1] = linea[linea.length-1] + "::";
                pw.print(String.join("::", linea));
            }
        }
        catch(Exception e) {
        e.printStackTrace();
        } finally {
            try {
                if (null != fichero)
                    fichero.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
}
