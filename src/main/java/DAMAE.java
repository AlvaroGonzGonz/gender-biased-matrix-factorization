import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;

public class DAMAE extends QualityMeasure {

    private int age;
    private boolean older;

    public DAMAE(Recommender recommender, int age){
        super(recommender);
        this.age = age;
        this.older = false;
    }

    public DAMAE(Recommender recommender, int age, boolean older){
        super(recommender);
        this.age = age;
        this.older = older;
    }

    @Override
    public double getScore(TestUser testUser, double[] predictions) {

        if(!older) {
            if (testUser.getDataBank().getInt("age") <= this.age) {
                double sum = 0d;
                int count = 0;

                for (int pos = 0; pos < testUser.getNumberOfTestRatings(); pos++) {
                    if (!Double.isNaN(predictions[pos])) {
                        sum += Math.abs(predictions[pos] - testUser.getTestRatingAt(pos));
                        count++;
                    }
                }

                return (count == 0) ? Double.NaN : (sum / count);
            }

            else return Double.NaN;
        } else if(older){
            if (testUser.getDataBank().getInt("age") > this.age) {
                double sum = 0d;
                int count = 0;

                for (int pos = 0; pos < testUser.getNumberOfTestRatings(); pos++) {
                    if (!Double.isNaN(predictions[pos])) {
                        sum += Math.abs(predictions[pos] - testUser.getTestRatingAt(pos));
                        count++;
                    }
                }

                return (count == 0) ? Double.NaN : (sum / count);
            }

            else return Double.NaN;
        }

        else return Double.NaN;
    }
}
