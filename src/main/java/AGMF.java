import es.upm.etsisi.cf4j.util.process.Parallelizer;
import es.upm.etsisi.cf4j.util.process.Partible;
import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.Item;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.util.Maths;

import java.util.Map;
import java.util.Random;

/**
 * Implements Mnih, A., &amp; Salakhutdinov, R. R. (2008). Probabilistic matrix factorization. In
 * Advances in neural information processing systems (pp. 1257-1264).
 */
public class AGMF extends Recommender {

    protected static final double DEFAULT_GAMMA = 0.01;
    protected static final double DEFAULT_LAMBDA = 0.05;

    /** User factors */
    protected final double[][][] p;

    /** Item factors */
    protected final double[][][] q;

    /** Group pertenency */
    protected final double[][] w;

    /** Learning rate */
    protected final double gamma_p;
    protected final double gamma_q;
    protected final double gamma_w;

    /** Regularization parameter */
    protected final double lambda_p;
    protected final double lambda_q;
    protected final double lambda_w;

    /** Number of latent factors */
    protected final int numFactors;

    /** Number of iterations */
    protected final int numIters;

    /** Number of groups */
    protected final int numGroups;

    /**
     * Model constructor from a Map containing the model's hyper-parameters values. Map object must
     * contains the following keys:
     *
     * <ul>
     *   <li><b>numFactors</b>: int value with the number of latent factors.
     *   <li><b>numIters:</b>: int value with the number of iterations.
     *   <li><b><em>gamma</em></b> (optional): double value with the learning rate hyper-parameter. If
     *       missing, it is set to 0.01.
     *   <li><b><em>lambda</em></b> (optional): double value with the regularization hyper-parameter.
     *       If missing, it is set to 0.05.
     *   <li><b><em>seed</em></b> (optional): random seed for random numbers generation. If missing,
     *       random value is used.
     * </ul>
     *
     * @param datamodel DataModel instance
     * @param params Model's hyper-parameters values
     */
    public AGMF(DataModel datamodel, Map<String, Object> params) {
        this(
                datamodel,
                (int) params.get("numFactors"),
                (int) params.get("numIters"),
                (int) params.get("numGroups"),
                params.containsKey("lambda_p") ? (double) params.get("lambda_p") : DEFAULT_LAMBDA,
                params.containsKey("lambda_q") ? (double) params.get("lambda_q") : DEFAULT_LAMBDA,
                params.containsKey("lambda_w") ? (double) params.get("lambda_w") : DEFAULT_LAMBDA,
                params.containsKey("gamma_p") ? (double) params.get("gamma_p") : DEFAULT_GAMMA,
                params.containsKey("gamma_q") ? (double) params.get("gamma_q") : DEFAULT_GAMMA,
                params.containsKey("gamma_w") ? (double) params.get("gamma_w") : DEFAULT_GAMMA,
                params.containsKey("seed") ? (long) params.get("seed") : System.currentTimeMillis());
    }

    /**
     * Model constructor
     *
     * @param datamodel DataModel instance
     * @param numFactors Number of factors
     * @param numIters Number of iterations
     */
    public AGMF(DataModel datamodel, int numFactors, int numIters, int numGroups) {
        this(datamodel, numFactors, numIters, numGroups, DEFAULT_LAMBDA);
    }

    /**
     * Model constructor
     *
     * @param datamodel DataModel instance
     * @param numFactors Number of factors
     * @param numIters Number of iterations
     * @param seed Seed for random numbers generation
     */
    public AGMF(DataModel datamodel, int numFactors, int numIters, int numGroups, long seed) {
        this(datamodel, numFactors, numIters, numGroups, DEFAULT_LAMBDA, DEFAULT_LAMBDA, DEFAULT_LAMBDA, DEFAULT_GAMMA, DEFAULT_GAMMA, DEFAULT_GAMMA, seed);
    }

    /**
     * Model constructor
     *
     * @param datamodel DataModel instance
     * @param numFactors Number of factors
     * @param numIters Number of iterations
     * @param lambda Regularization parameter
     */
    public AGMF(DataModel datamodel, int numFactors, int numIters, int numGroups, double lambda) {
        this(datamodel, numFactors, numIters, numGroups, lambda, lambda, lambda, DEFAULT_GAMMA, DEFAULT_GAMMA, DEFAULT_GAMMA, System.currentTimeMillis());
    }

    /**
     * Model constructor
     *
     * @param datamodel DataModel instance
     * @param numFactors Number of factors
     * @param numIters Number of iterations
     * @param lambda Regularization parameter
     * @param seed Seed for random numbers generation
     */
    public AGMF(DataModel datamodel, int numFactors, int numIters, int numGroups, double lambda, long seed) {
        this(datamodel, numFactors, numIters, numGroups, lambda, lambda, lambda, DEFAULT_GAMMA, DEFAULT_GAMMA, DEFAULT_GAMMA, seed);
    }

    /**
     * Model constructor
     * @param datamodel DataModel instance
     * @param numFactors Number of factors
     * @param numIters Number of iterations
     * @param numGroups Number of Groups
     * @param lambda Regularzation parameter
     * @param gamma Learning rate parameter
     * @seed Seed for random numbers generation
     */
    public AGMF(DataModel datamodel, int numFactors, int numIters, int numGroups, double lambda, double gamma, long seed){
        this(datamodel, numFactors, numIters, numGroups, lambda, lambda, lambda, gamma, gamma, gamma, seed);
    }

    /**
     * Model constructor
     *
     * @param datamodel DataModel instance
     * @param numFactors Number of factors
     * @param numIters Number of iterations
     * @param numGroups Number of groups
     * @param lambda_p Regularization parameter
     * @param lambda_q Regularization parameter
     * @param lambda_w Regularization parameter
     * @param gamma_p Learning rate parameter
     * @param gamma_q Learning rate parameter
     * @param gamma_w Learning rate parameter
     * @param seed Seed for random numbers generation
     */
    public AGMF(
            DataModel datamodel, int numFactors, int numIters, int numGroups, double lambda_p, double lambda_q, double lambda_w, double gamma_p, double gamma_q, double gamma_w, long seed) {
        super(datamodel);

        this.numFactors = numFactors;
        this.numIters = numIters;
        this.numGroups = numGroups;
        this.lambda_p = lambda_p;
        this.lambda_q = lambda_q;
        this.lambda_w = lambda_w;
        this.gamma_p = gamma_p;
        this.gamma_q = gamma_q;
        this.gamma_w = gamma_w;

        Random rand = new Random(seed);

        // Users initialization
        this.p = new double[datamodel.getNumberOfUsers()][numGroups][numFactors];
        for (int u = 0; u < datamodel.getNumberOfUsers(); u++) {
            for (int g = 0; g < numGroups; g++) {
                for (int k = 0; k < numFactors; k++) {
                    this.p[u][g][k] = rand.nextDouble() * 2 - 1;
                }
            }
        }

        // Items initialization
        this.q = new double[datamodel.getNumberOfItems()][numGroups][numFactors];
        for (int i = 0; i < datamodel.getNumberOfItems(); i++) {
            for (int g = 0; g < numGroups; g++) {
                for (int k = 0; k < numFactors; k++) {
                    this.q[i][g][k] = rand.nextDouble() * 2 - 1;
                }
            }
        }

        this.w = new double[datamodel.getNumberOfUsers()][numGroups];
        for (int u = 0; u < datamodel.getNumberOfUsers(); u++){
            for (int g = 0; g < numGroups; g++){
                this.w[u][g] = rand.nextDouble() * 2 - 1;
            }
        }
    }

    /**
     * Get the number of factors of the model
     *
     * @return Number of factors
     */
    public int getNumFactors() {
        return this.numFactors;
    }

    /**
     * Get the number of iterations
     *
     * @return Number of iterations
     */
    public int getNumIters() {
        return this.numIters;
    }

    public double[] getGroups(User user){return w[user.getUserIndex()]; }

    /**
     * Get the regularization parameter of the model
     *
     * @return Lambda_p
     */

    public double getLambda_p() {
        return this.lambda_p;
    }

    /**
     * Get the regularization parameter of the model
     *
     * @return Lambda_q
     */
    public double getLambda_q() {
        return this.lambda_q;
    }

    /**
     * Get the regularization parameter of the model
     *
     * @return Lambda_w
     */
    public double getLambda_w() {
        return this.lambda_w;
    }

    /**
     * Get the learning rate parameter of the model
     *
     * @return Gamma_p
     */
    public double getGamma_p() {
        return this.gamma_p;
    }

    /**
     * Get the learning rate parameter of the model
     *
     * @return Gamma_q
     */
    public double getGamma_q() {
        return this.gamma_q;
    }

    /**
     * Get the learning rate parameter of the model
     *
     * @return Gamma_w
     */
    public double getGamma_w() {
        return this.gamma_w;
    }

    /**
     * Get the latent factors vector of a user (pu)
     *
     * @param userIndex User
     * @return Latent factors vector
     */
    public double[] getUserFactors(int userIndex, int group) {
        return this.p[userIndex][group];
    }

    /**
     * Get the latent factors vector of an item (qi)
     *
     * @param itemIndex User
     * @return Latent factors vector
     */
    public double[] getItemFactors(int itemIndex, int group) {
        return this.q[itemIndex][group];
    }

    public double softmax(int userIndex, int group){
        double sum = 0.0;
        for(int g = 0; g < numGroups; g++){
            sum += Math.exp(w[userIndex][g]);
        }

        return (Math.exp(w[userIndex][group])/sum);
    }

    @Override
    public void fit() {
        System.out.println("\nFitting " + this.toString());

        for (int iter = 1; iter <= this.numIters; iter++) {
            Parallelizer.exec(this.datamodel.getUsers(), new UpdateUsersFactors());
            Parallelizer.exec(this.datamodel.getItems(), new UpdateItemsFactors());

            if ((iter % 10) == 0) System.out.print(".");
            if ((iter % 100) == 0) System.out.println(iter + " iterations");
        }
    }

    @Override
    public double predict(int userIndex, int itemIndex) {
        double result = 0.0;
        for (int g = 0; g < this.numGroups; g++){
            for(int k = 0; k < this.numFactors; k++) {
                result += this.softmax(userIndex, g) * this.p[userIndex][g][k] * this.q[itemIndex][g][k];
            }

        }
        return result;
    }

    @Override
    public String toString() {
        return "AGMF("
                + "numFactors="
                + this.numFactors
                + "; "
                + "numIters="
                + this.numIters
                + "; "
                + "numGroups="
                + this.numGroups
                + "; "
                + "gamma_p="
                + this.gamma_p
                + "; "
                + "gamma_q="
                + this.gamma_q
                + "; "
                + "gamma_w="
                + this.gamma_w
                + "; "
                + "lambda_p="
                + this.lambda_p
                + "; "
                + "lambda_q="
                + this.lambda_q
                + "; "
                + "lambda_w="
                + this.lambda_w
                + ")";
    }

    /** Auxiliary inner class to parallelize user factors computation */
    private class UpdateUsersFactors implements Partible<User> {

        @Override
        public void beforeRun() {
        }

        @Override
        public void run(User user) {
            int userIndex = user.getUserIndex();

            for (int pos = 0; pos < user.getNumberOfRatings(); pos++) {
                int itemIndex = user.getItemAt(pos);
                double error = user.getRatingAt(pos) - predict(userIndex, itemIndex);
                double[] gradient_w = new double[numGroups];
                for(int i=0; i<gradient_w.length; i++)
                    gradient_w[i] = 0.0;
                for (int g = 0; g < numGroups; g++) {
                    for (int k = 0; k < numFactors; k++) {
                        p[userIndex][g][k] += gamma_p * (this.softmax(userIndex, g) * error * q[itemIndex][g][k] - lambda_p * p[userIndex][g][k]);
                    }

                    for (int k = 0; k < numGroups; k++) {
                        if (g != k) {
                            gradient_w[g] += (-this.softmax(userIndex, g)) * this.softmax(userIndex, k) * Math.pow(error, 2);
                        } else {
                            gradient_w[g] += (1 - this.softmax(userIndex, g)) * this.softmax(userIndex, g) * Math.pow(error, 2);
                        }
                    }
                }
                for(int g=0; g<numGroups; g++){
                    w[userIndex][g] -= gamma_w * (gradient_w[g] + lambda_w * w[userIndex][g]);
                }
            }
        }

        @Override
        public void afterRun() {}

        public double softmax(int userIndex, int group){
            double result;
            double sum = 0.0;
            for(int g = 0; g < numGroups; g++){
                sum += Math.exp(w[userIndex][g]);
            }

            if(Double.isNaN(sum)) {
                result = 0.0;
            } else {
                result = Math.exp(w[userIndex][group])/sum;
            }


            return result;
        }
    }

    /** Auxiliary inner class to parallelize item factors computation */
    private class UpdateItemsFactors implements Partible<Item> {

        @Override
        public void beforeRun() {}

        @Override
        public void run(Item item) {
            int itemIndex = item.getItemIndex();
            for (int pos = 0; pos < item.getNumberOfRatings(); pos++) {
                int userIndex = item.getUserAt(pos);
                double error = item.getRatingAt(pos) - predict(userIndex, itemIndex);
                for (int g = 0; g < numGroups; g++) {
                    for (int k = 0; k < numFactors; k++) {
                        q[itemIndex][g][k] += gamma_q * (this.softmax(userIndex, g) * error * p[userIndex][g][k] - lambda_q * q[itemIndex][g][k]);
                    }
                }
            }
        }

        @Override
        public void afterRun() {}

        public double softmax(int userIndex, int group){
            double sum = 0.0;
            for(int g = 0; g < numGroups; g++){
                sum += Math.exp(w[userIndex][g]);
            }

            return (Math.exp(w[userIndex][group])/sum);
        }
    }
}