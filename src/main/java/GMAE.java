import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;

import java.util.Map;

public class GMAE extends QualityMeasure {

    private double gender;

    public GMAE(Recommender recommender){
        super(recommender);
        this.gender = Double.NaN;
    }

    public GMAE(Recommender recommender, Double gender) {
        super(recommender);
        this.gender = gender;
    }

    public GMAE(Recommender recommender, Map<String, Object> params) {
        this(recommender,
                (double) params.get("gender"));
    }

    @Override
    public double getScore(TestUser testUser, double[] predictions) {

        if (Double.isNaN(this.gender) || testUser.getDataBank().getDouble("gender") == this.gender) {
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