import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;

import java.util.Map;

public class AMAE extends QualityMeasure {

    private int age;

    public AMAE(Recommender recommender, int age) {
        super(recommender);
        this.age = age;
    }

    public AMAE(Recommender recommender, Map<String, Object> params) {
        this(recommender,
                (int) params.get("age"));
    }

    @Override
    public double getScore(TestUser testUser, double[] predictions) {

        if (this.age == testUser.getDataBank().getInt("age")) {
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
}