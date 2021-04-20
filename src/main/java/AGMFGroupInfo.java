import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.recommender.Recommender;

public class AGMFGroupInfo {
    private static int numgroups = 7;
    private static final int[] AGES = {1, 18, 25, 35, 45, 50, 56};

    public static void main (String[] args){
        try {
            DataModel datamodel = DataModel.load("ml-1m");

            int[][] group = new int[numgroups][7];
            int[][] genero = new int[numgroups][2];

            AGMF agmf = new AGMF(datamodel, 9, 50, numgroups, 0.01, 0.08, 43L);
            agmf.fit();

            for (User user : datamodel.getUsers()){
                double[] grupos = agmf.getGroups(user);

                double max = Double.MIN_VALUE;
                int max_pos = 0;
                for(int i=0; i<grupos.length; i++){
                    if(grupos[i] > max){
                        max = grupos[i];
                        max_pos = i;
                    }
                }
                if(user.getDataBank().getInt("age") == 1){
                    group[max_pos][0]++;
                }else if(user.getDataBank().getInt("age") == 18){
                    group[max_pos][1]++;
                }else if(user.getDataBank().getInt("age") == 25){
                    group[max_pos][2]++;
                }else if(user.getDataBank().getInt("age") == 35){
                    group[max_pos][3]++;
                }else if(user.getDataBank().getInt("age") == 45){
                    group[max_pos][4]++;
                }else if(user.getDataBank().getInt("age") == 50){
                    group[max_pos][5]++;
                }else if(user.getDataBank().getInt("age") == 56){
                    group[max_pos][6]++;
                }

                if(user.getDataBank().getDouble("gender") == 0.0){
                    genero[max_pos][0]++;
                } else if(user.getDataBank().getDouble("gender") == 1.0){
                    genero[max_pos][1]++;
                }
            }

            System.out.println();
            for(int i=0; i<numgroups; i++){
                System.out.println("===================================================");
                System.out.println("El grupo " + (i+1) + " tiene la siguiente distribución:");
                for(int j=0; j<AGES.length; j++) {
                    System.out.println("\t-Edad " + AGES[j] + ": " + group[i][j] + " usuarios.");
                }
                System.out.println("De los cuales:");
                System.out.println("\t-" + genero[i][0] + " son mujeres.");
                System.out.println("\t-" + genero[i][1] + " son hombres.");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
