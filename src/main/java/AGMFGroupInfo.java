import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.recommender.Recommender;

public class AGMFGroupInfo {
    private static int numgroups = 2;

    public static void main (String[] args){
        try {
            DataModel datamodel = DataModel.load("ml-1m");

            AGMF agmf = new AGMF(datamodel, 7, 50, numgroups, 43L);
            agmf.fit();

            for (User user : datamodel.getUsers()){
                for (int i=0; i<numgroups; i++) {
                    System.out.print("|" + agmf.getGroups(user)[i] + "|");
                }
                System.out.println();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
