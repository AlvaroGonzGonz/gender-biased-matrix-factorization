import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;

import java.util.Map;

public class AMAE extends QualityMeasure {

    private double[] gap;

    public AMAE(Recommender recommender){
        super(recommender);
        this.gap = new double[]{Double.NaN, Double.NaN};
    }

    public AMAE(Recommender recommender, double[] gap) {
        super(recommender);
        this.gap = gap;
    }

    public AMAE(Recommender recommender, Map<String, Object> params) {
        this(recommender,
                (double[]) params.get("age"));
    }

    @Override
    public double getScore(TestUser testUser, double[] predictions) {

        if (Double.isNaN(this.gap[0]) || (this.gap[0] <= testUser.getDataBank().getDouble("age") && testUser.getDataBank().getDouble("age") < this.gap[1])) {
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