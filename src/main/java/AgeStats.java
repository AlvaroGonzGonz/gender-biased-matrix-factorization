import com.tdunning.math.stats.Histogram;
import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.util.plot.HistogramPlot;

public class AgeStats {
    public AgeStats(){
    }

    public static void main (String[] args){
        try {
            DataModel datamodel = DataModel.load("gml1M");

            HistogramPlot plot = new HistogramPlot("Age", 57);

            double userAge, mean, var;
            double count = 0.0;
            double sum = 0.0;
            double max = 0.0;
            double min = 100.0;
            int count56 = 0;

            for (User user : datamodel.getUsers()){
                userAge = (int) 100.0 * user.getDataBank().getDouble("age");

                plot.addValue(userAge);

                if(max < userAge) max = userAge;
                if(min > userAge) min = userAge;

                sum += userAge;
                count ++;
            }

            mean = sum / count;

            sum = 0.0;
            count = 0.0;

            for (User user : datamodel.getUsers()){

                userAge = (int) 100.0 * user.getDataBank().getDouble("age");

                sum += Math.pow((userAge - mean), 2);
                count ++;
            }

            var = Math.sqrt(sum / count);

            System.out.format("Las edades de MovieLens1M se sitúan entre %d años y %d años. ", (int) min, (int) max);
            System.out.format("La media de las edades es %.2f y su desviación típica es %.2f. ", mean, var);
            System.out.println(count56);

            plot.draw();

        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
