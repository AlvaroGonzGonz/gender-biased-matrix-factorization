import es.upm.etsisi.cf4j.util.process.Parallelizer;
import es.upm.etsisi.cf4j.util.process.Partible;
import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.Item;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.recommender.Recommender;


import java.util.Map;
import java.util.Random;


public class RDABMF extends Recommender {

    protected static final double DEFAULT_GAMMA = 0.001;
    protected static final double DEFAULT_LAMBDA = 0.05;

    /** User factors */
    protected final double[][] p;

    /** Gender Matrix */
    protected final double[][] g;

    /** Female Item factors */
    protected final double[][] qf;

    /** Male Item factors */
    protected final double[][] qm;

    /** Learning rate */
    protected final double gamma;

    /** Learning rate for qf and qm*/
    protected final double etaf;
    protected final double etam;

    /** Regularization parameter */
    protected final double lambda;

    /** Number of latent factors */
    protected final int numFactors;

    /** Number of iterations */
    protected final int numIters;

    /**
     * Model constructor from a Map containing the model's hyper-parameters values. Map object must
     * contains the following keys:
     *
     * <ul>
     *   <li><b>numFactors</b>: int value with the number of latent factors.
     *   <li><b>numIters:</b>: int value with the number of iterations.
     *   <li><b>Age:</b>: int value with the maximum age for young group.
     *   <li><b><em>gamma</em></b> (optional): double value with the learning rate hyper-parameter. If
     *       missing, it is set to 0.01.
     *   <li><b><em>etaf</em></b> (optional): double value with the learning rate hyper-parameter for
     *       feminine users. If missing, it is set to 0.01.
     *   <li><b><em>etam</em></b> (optional): double value with the learning rate hyper-parameter for
     *       masculine users. If missing, it is set to 0.01.
     *   <li><b><em>lambda</em></b> (optional): double value with the regularization hyper-parameter.
     *       If missing, it is set to 0.05.
     *   <li><b><em>seed</em></b> (optional): random seed for random numbers generation. If missing,
     *       random value is used.
     * </ul>
     *
     * @param datamodel DataModel instance
     * @param params Model's hyper-parameters values
     */
    public RDABMF(DataModel datamodel, Map<String, Object> params) {
        this(
                datamodel,
                (int) params.get("numFactors"),
                (int) params.get("numIters"),
                params.containsKey("lambda") ? (double) params.get("lambda") : DEFAULT_LAMBDA,
                params.containsKey("gamma") ? (double) params.get("gamma") : DEFAULT_GAMMA,
                params.containsKey("etaf") ? (double) params.get("etaf") : DEFAULT_GAMMA,
                params.containsKey("etam") ? (double) params.get("etam") : DEFAULT_GAMMA,
                params.containsKey("seed") ? (long) params.get("seed") : System.currentTimeMillis());
    }

    /**
     * Model constructor
     *
     * @param datamodel DataModel instance
     * @param numFactors Number of factors
     * @param numIters Number of iterations
     */
    public RDABMF(DataModel datamodel, int numFactors, int numIters) {
        this(datamodel, numFactors, numIters, DEFAULT_LAMBDA);
    }

    /**
     * Model constructor
     *
     * @param datamodel DataModel instance
     * @param numFactors Number of factors
     * @param numIters Number of iterations
     * @param seed Seed for random numbers generation
     */
    public RDABMF(DataModel datamodel, int numFactors, int numIters, long seed) {
        this(datamodel, numFactors, numIters, DEFAULT_LAMBDA, DEFAULT_GAMMA, DEFAULT_GAMMA, DEFAULT_GAMMA, seed);
    }

    /**
     * Model constructor
     *
     * @param datamodel DataModel instance
     * @param numFactors Number of factors
     * @param numIters Number of iterations
     * @param lambda Regularization parameter
     */
    public RDABMF(DataModel datamodel, int numFactors, int numIters, double lambda) {
        this(datamodel, numFactors, numIters, lambda, DEFAULT_GAMMA, DEFAULT_GAMMA, DEFAULT_GAMMA, System.currentTimeMillis());
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
    public RDABMF(DataModel datamodel, int numFactors, int numIters, double lambda, long seed) {
        this(datamodel, numFactors, numIters, lambda, DEFAULT_GAMMA, DEFAULT_GAMMA, DEFAULT_GAMMA, seed);
    }

    /**
     * Model constructor
     *
     * @param datamodel DataModel instance
     * @param numFactors Number of factors
     * @param numIters Number of iterations
     * @param lambda Regularization parameter
     * @param gamma Learning rate parameter
     * @param etaf Learning rate parameter
     * @param etam Learning rate parameter
     * @param seed Seed for random numbers generation
     */
    public RDABMF(
            DataModel datamodel, int numFactors, int numIters, double lambda, double gamma, double etaf, double etam, long seed) {
        super(datamodel);

        this.numFactors = numFactors;
        this.numIters = numIters;
        this.lambda = lambda;
        this.gamma = gamma;
        this.etaf = etaf;
        this.etam = etam;

        Random rand = new Random(seed);

        // Users initialization
        this.p = new double[datamodel.getNumberOfUsers()][numFactors];
        for (int u = 0; u < datamodel.getNumberOfUsers(); u++) {
            for (int k = 0; k < numFactors; k++) {
                this.p[u][k] = rand.nextDouble() * 2 - 1;
            }
        }

        //Age initialization
        this.g = new double[datamodel.getNumberOfUsers()][datamodel.getNumberOfUsers()];
        for (int g = 0; g < datamodel.getNumberOfUsers(); g++){
            for (int k = 0; k < datamodel.getNumberOfUsers(); k++){
                if (g == k)
                    this.g[g][k] = datamodel.getUser(k).getDataBank().getInt("age");
                else this.g[g][k] = 0.0;
            }
        }

        // Items initialization
        this.qf = new double[datamodel.getNumberOfItems()][numFactors];
        for (int i = 0; i < datamodel.getNumberOfItems(); i++) {
            for (int k = 0; k < numFactors; k++) {
                this.qf[i][k] = rand.nextDouble() * 2 - 1;
            }
        }

        this.qm = new double[datamodel.getNumberOfItems()][numFactors];
        for (int i = 0; i < datamodel.getNumberOfItems(); i++) {
            for (int k = 0; k < numFactors; k++) {
                this.qm[i][k] = rand.nextDouble() * 2 - 1;
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

    /**
     * Get the regularization parameter of the model
     *
     * @return Lambda
     */
    public double getLambda() {
        return this.lambda;
    }

    /**
     * Get the learning rate parameter of the model
     *
     * @return Gamma
     */
    public double getGamma() {
        return this.gamma;
    }

    /**
     * Get the learning rate parameter of the model
     *
     * @return Etaf
     */
    public double getEtaf() {
        return this.etaf;
    }

    /**
     * Get the learning rate parameter of the model
     *
     * @return Etam
     */
    public double getEtam() {
        return this.etam;
    }

    /**
     * Get the latent factors vector of a user (pu)
     *
     * @param userIndex User
     * @return Latent factors vector
     */
    public double[] getUserFactors(int userIndex) {
        return this.p[userIndex];
    }

    /**
     * Get the latent factors vector of gender (gu)
     *
     * @param userIndex User
     * @return Latent factors vector
     */
    public double[] getGenderFactors(int userIndex) {
        return this.g[userIndex];
    }

    /**
     * Get the latent factors vector of an item (qi)
     * for female users
     * @param itemIndex User
     * @return Latent factors vector
     */
    public double[] getFemaleItemFactors(int itemIndex) {
        return this.qf[itemIndex];
    }

    /**
     * Get the latent factors vector of an item (qi)
     * for male users
     * @param itemIndex User
     * @return Latent factors vector
     */
    public double[] getMaleItemFactors(int itemIndex) {
        return this.qm[itemIndex];
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
        double result = 0;

        for (int k=0; k < this.getNumFactors(); k++){
            result += this.p[userIndex][k]
                    * (this.g[userIndex][userIndex] * this.qf[itemIndex][k]
                    + (1.0 - this.g[userIndex][userIndex]) * this.qm[itemIndex][k]);
        }

        return result;
    }

    @Override
    public String toString() {
        return "RDABMF("
                + "numFactors="
                + this.numFactors
                + "; "
                + "numIters="
                + this.numIters
                + "; "
                + "gamma="
                + this.gamma
                + "; "
                + "lambda="
                + this.lambda
                + "; "
                + "etaf="
                + this.etaf
                + "; "
                + "etam="
                + this.etam
                + ")";
    }

    /** Auxiliary inner class to parallelize user factors computation */
    private class UpdateUsersFactors implements Partible<User> {

        @Override
        public void beforeRun() {}

        @Override
        public void run(User user) {
            int userIndex = user.getUserIndex();
            for (int pos = 0; pos < user.getNumberOfRatings(); pos++) {
                int itemIndex = user.getItemAt(pos);
                double error = user.getRatingAt(pos) - predict(userIndex, itemIndex);
                for (int k = 0; k < numFactors; k++) {
                    p[userIndex][k] += gamma * (error * ((g[userIndex][userIndex] * qf[itemIndex][k]) + ((1.0 - g[userIndex][userIndex]) * qm[itemIndex][k])) - lambda * p[userIndex][k]);
                }
            }
        }

        @Override
        public void afterRun() {}
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
                for (int k = 0; k < numFactors; k++) {
                    qf[itemIndex][k] += etaf * (error * g[userIndex][userIndex] * p[userIndex][k] - lambda * qf[itemIndex][k]);
                    qm[itemIndex][k] += etam * (error * (1.0 - g[userIndex][userIndex]) * p[userIndex][k] - lambda * qm[itemIndex][k]);
                }
            }
        }

        @Override
        public void afterRun() {}
    }
}

