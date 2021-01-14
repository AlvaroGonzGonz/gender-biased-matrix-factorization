import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.User;

public class AgeStats {
    public AgeStats(){
    }

    public static void main (String[] args){
        try {
            DataModel datamodel = DataModel.load("gml1M");

        double userAge, mean, var;
        double count = 0.0;
        double sum = 0.0;
        double max = 0.0;
        double min = 100.0;

        for (User user : datamodel.getUsers()){

            userAge = user.getDataBank().getDouble("age");

            if(max < userAge) max = userAge;
            if(min > userAge) min = userAge;

            sum += userAge;
            count ++;
        }

        mean = sum / count;

        sum = 0.0;
        count = 0.0;

        for (User user : datamodel.getUsers()){

            userAge = user.getDataBank().getDouble("age");

            sum += Math.pow((userAge - mean), 2);
            count ++;
        }

        var = Math.sqrt(sum / count);

        System.out.println("Las edades de MovieLens1M se sitúan entre " + min + " años y " + max + " años.");
        System.out.println("La media de las edades es " + mean + " y su desviación típica es " + var);

        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
