import es.upm.etsisi.cf4j.data.TestUser;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;

import java.util.Map;

public class EmbeddingsMAE extends QualityMeasure {

    private String group;

    public EmbeddingsMAE(Recommender recommender){
        super(recommender);
        this.group = "null";
    }

    public EmbeddingsMAE(Recommender recommender, String group) {
        super(recommender);
        this.group = group;
    }

    public EmbeddingsMAE(Recommender recommender, Map<String, Object> params) {
        this(recommender,
                (String) params.get("group"));
    }

    @Override
    public double getScore(TestUser testUser, double[] predictions) {

        if (this.group.equals("null") || testUser.getDataBank().getString("group").equals(this.group)) {
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

