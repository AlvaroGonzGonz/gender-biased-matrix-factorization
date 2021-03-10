import es.upm.etsisi.cf4j.data.DataModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddData {
    public AddData(){
    }

    public static void main (String[] args) throws IOException {

        Map<String, Double> id2GenderIndex = new HashMap();
        Map<String, Integer> id2AgeIndex = new HashMap();
        Map<String, Double> id2Age2Index = new HashMap();
        Map<String, Double> id2NormAgeIndex = new HashMap();
        Map<String, Double> id2FairnessValue = new HashMap();

        double sum = 0.0;
        int count = 0;

        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;

        try {
            DataModel datamodel = DataModel.load("RPMF/Group2/ml-1m-2");
            // Apertura del fichero y creacion de BufferedReader para poder
            // hacer una lectura comoda (disponer del metodo readLine()).
            archivo = new File ("RPMF/Group1/users.dat");
            fr = new FileReader (archivo);
            br = new BufferedReader(fr);

            // Se lee cada linea y se divide en campos. Se introduce el id del
            // usuario y su género transformado a una double (siendo F = 0.0 y M = 1.0)
            // en una tabla Hash
            String linea;
            while((linea=br.readLine())!=null) {
                String[] parts = linea.split("::");

                if (parts[1].equals("M"))
                    id2GenderIndex.put(parts[0], 1.0);
                else if (parts[1].equals("F"))
                    id2GenderIndex.put(parts[0], 0.0);

                //System.out.println(parts[2]);
                id2AgeIndex.put(parts[0], Integer.parseInt(parts[2]));
                id2NormAgeIndex.put(parts[0], ((Double.parseDouble(parts[2]) - 1.0) / (56.0 - 1.0)));

                if(parts[2].equals("1")) {
                    id2FairnessValue.put(parts[0], 1.0);
                    id2Age2Index.put((parts[0]), 18.0);
                    sum += 18.0;
                }
                else if (parts[2].equals("18")) {
                    id2FairnessValue.put(parts[0], 0.5);
                    id2Age2Index.put((parts[0]), 21.0);
                    sum += 21.0;
                }
                else if(parts[2].equals("25")){
                    id2FairnessValue.put(parts[0], 0.25);
                    id2Age2Index.put((parts[0]), 29.5);
                    sum += 29.5;
                }
                else if(parts[2].equals("35")){
                    id2FairnessValue.put(parts[0], 0.1);
                    id2Age2Index.put((parts[0]), 39.5);
                    sum += 39.5;
                }
                else if(parts[2].equals("45")){
                    id2FairnessValue.put(parts[0], 0.25);
                    id2Age2Index.put((parts[0]), 47.0);
                    sum += 47.0;
                }
                else if(parts[2].equals("50")){
                    id2FairnessValue.put(parts[0], 0.5);
                    id2Age2Index.put((parts[0]), 52.5);
                    sum += 52.5;
                }
                else if(parts[2].equals("56")){
                    id2FairnessValue.put(parts[0], 1.0);
                    id2Age2Index.put((parts[0]), 56.0);
                    sum += 56.0;
                }

                count++;
            }

            // Para cada usuario de la tabla Hash se busca su índice
            // en el model de MovieLens y se le añade un campo gender
            // en su DataBank. Se realiza lo mismo para los usuarios de
            // test
            int index, testindex;
            for (String i : id2GenderIndex.keySet()) {
                index = datamodel.findUserIndex(i);
                if (index != -1) {
                    datamodel.getUser(index).getDataBank().setDouble("gender", (double) id2GenderIndex.get(i));
                    datamodel.getUser(index).getDataBank().setInt("age", (int) id2AgeIndex.get(i));
                    datamodel.getUser(index).getDataBank().setDouble("NormalizedAge", (double) id2NormAgeIndex.get(i));
                    datamodel.getUser(index).getDataBank().setDouble("MeanAge", (double) (sum/count));
                    datamodel.getUser(index).getDataBank().setDouble("Age", (double) id2Age2Index.get(i));
                }
                testindex = datamodel.findTestUserIndex(i);
                if (testindex != -1) {
                    datamodel.getTestUser(testindex).getDataBank().setDouble("gender", (double) id2GenderIndex.get(i));
                    datamodel.getTestUser(testindex).getDataBank().setInt("age", (int) id2AgeIndex.get(i));
                    datamodel.getTestUser(testindex).getDataBank().setDouble("NormalizedAge", (double) id2NormAgeIndex.get(i));
                    datamodel.getTestUser(testindex).getDataBank().setDouble("MeanAge", (double) (sum/count));
                    datamodel.getTestUser(testindex).getDataBank().setDouble("Age", (double) id2Age2Index.get(i));
                }
            }

            // Se guarda el nuevo modelo
            datamodel.save("RPMF/Group2/ml-1m-2");
        }
        catch(Exception e){
            e.printStackTrace();
        }finally {
            // En el finally cerramos el fichero, para asegurarnos
            // que se cierra tanto si todo va bien como si salta
            // una excepcion.
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
}
