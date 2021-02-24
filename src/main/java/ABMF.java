import es.upm.etsisi.cf4j.util.process.Parallelizer;
import es.upm.etsisi.cf4j.util.process.Partible;
import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.Item;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.recommender.Recommender;


import java.util.Map;
import java.util.Random;


public class ABMF extends Recommender {

    protected static final double DEFAULT_GAMMA = 0.001;
    protected static final double DEFAULT_LAMBDA = 0.05;
    protected static final double DEFAULT_ALPHA = 0;

    protected static final double DEFAULT_C1 = 0.01;
    protected static final double DEFAULT_C2 = 0.01;

    /** User factors */
    protected final double[][] p;

    /** Sigmoid Matrix */
    protected final double[][] s_u;

    /** Female Item factors */
    protected final double[][] qy;

    /** Male Item factors */
    protected final double[][] qo;

    /** Learning rate */
    protected final double gamma;

    /** Learning rate for feminine and masculine*/
    protected final double etay;
    protected final double etao;

    /** Regularization parameter */
    protected final double lambda;

    /** Number of latent factors */
    protected final int numFactors;

    /** Number of iterations */
    protected final int numIters;

    /** Sigmoid parameters */
    protected final double c1;
    protected final double c2;

    /** Age parameter */
    protected final double alpha;

    /**
     * Model constructor from a Map containing the model's hyper-parameters values. Map object must
     * contains the following keys:
     *
     * <ul>
     *   <li><b>numFactors</b>: int value with the number of latent factors.
     *   <li><b>numIters:</b>: int value with the number of iterations.
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
    public ABMF(DataModel datamodel, Map<String, Object> params) {
        this(
                datamodel,
                (int) params.get("numFactors"),
                (int) params.get("numIters"),
                params.containsKey("lambda") ? (double) params.get("lambda") : DEFAULT_LAMBDA,
                params.containsKey("gamma") ? (double) params.get("gamma") : DEFAULT_GAMMA,
                params.containsKey("etaf") ? (double) params.get("etaf") : DEFAULT_GAMMA,
                params.containsKey("etam") ? (double) params.get("etam") : DEFAULT_GAMMA,
                params.containsKey("c1") ? (double) params.get("c1") : DEFAULT_C1,
                params.containsKey("c2") ? (double) params.get("c2") : DEFAULT_C2,
                params.containsKey("alpha") ? (double) params.get("alpha") : DEFAULT_ALPHA,
                params.containsKey("seed") ? (long) params.get("seed") : System.currentTimeMillis());
    }

    /**
     * Model constructor
     *
     * @param datamodel DataModel instance
     * @param numFactors Number of factors
     * @param numIters Number of iterations
     */
    public ABMF(DataModel datamodel, int numFactors, int numIters) {
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
    public ABMF(DataModel datamodel, int numFactors, int numIters, long seed) {
        this(datamodel, numFactors, numIters, DEFAULT_LAMBDA, DEFAULT_GAMMA, DEFAULT_GAMMA, DEFAULT_GAMMA, DEFAULT_C1, DEFAULT_C2, DEFAULT_ALPHA, seed);
    }

    /**
     * Model constructor
     *
     * @param datamodel DataModel instance
     * @param numFactors Number of factors
     * @param numIters Number of iterations
     * @param lambda Regularization parameter
     */
    public ABMF(DataModel datamodel, int numFactors, int numIters, double lambda) {
        this(datamodel, numFactors, numIters, lambda, DEFAULT_GAMMA, DEFAULT_GAMMA, DEFAULT_GAMMA, DEFAULT_C1, DEFAULT_C2, DEFAULT_ALPHA, System.currentTimeMillis());
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
    public ABMF(DataModel datamodel, int numFactors, int numIters, double lambda, long seed) {
        this(datamodel, numFactors, numIters, lambda, DEFAULT_GAMMA, DEFAULT_GAMMA, DEFAULT_GAMMA, DEFAULT_C1, DEFAULT_C2, DEFAULT_ALPHA, seed);
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
    public ABMF(
            DataModel datamodel, int numFactors, int numIters, double lambda, double gamma, double etaf, double etam, double c1, double c2, double alpha, long seed) {
        super(datamodel);

        this.numFactors = numFactors;
        this.numIters = numIters;
        this.lambda = lambda;
        this.gamma = gamma;
        this.etay = etaf;
        this.etao = etam;
        this.c1 = c1;
        this.c2 = c2;
        this.alpha = alpha;

        Random rand = new Random(seed);

        // Users initialization
        this.p = new double[datamodel.getNumberOfUsers()][numFactors];
        for (int u = 0; u < datamodel.getNumberOfUsers(); u++) {
            for (int k = 0; k < numFactors; k++) {
                this.p[u][k] = rand.nextDouble() * 2 - 1;
            }
        }

        //Age initialization
        this.s_u = new double[datamodel.getNumberOfUsers()][datamodel.getNumberOfUsers()];
        for (int g = 0; g < datamodel.getNumberOfUsers(); g++){
            for (int k = 0; k < datamodel.getNumberOfUsers(); k++){
                if (g == k)
                    this.s_u[g][k] = 2.0 * ( 1.0 - (1.0 /( 1 + Math.exp( - this.c1 * (datamodel.getUser(k).getDataBank().getDouble("NormalizedAge") - this.c2)))));
                else this.s_u[g][k] = 0.0;
            }
        }

        // Items initialization
        this.qy = new double[datamodel.getNumberOfItems()][numFactors];
        for (int i = 0; i < datamodel.getNumberOfItems(); i++) {
            for (int k = 0; k < numFactors; k++) {
                this.qy[i][k] = rand.nextDouble() * 2 - 1;
            }
        }

        this.qo = new double[datamodel.getNumberOfItems()][numFactors];
        for (int i = 0; i < datamodel.getNumberOfItems(); i++) {
            for (int k = 0; k < numFactors; k++) {
                this.qo[i][k] = rand.nextDouble() * 2 - 1;
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
    public double getEtay() {
        return this.etay;
    }

    /**
     * Get the learning rate parameter of the model
     *
     * @return Etam
     */
    public double getEtao() {
        return this.etao;
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
     * Get the latent factors vector of s_u
     *
     * @param userIndex User
     * @return Latent factors vector
     */
    public double[] getSuFactors(int userIndex) {
        return this.s_u[userIndex];
    }

    /**
     * Get the latent factors vector of an item (qi)
     * for female users
     * @param itemIndex User
     * @return Latent factors vector
     */
    public double[] getFemaleItemFactors(int itemIndex) {
        return this.qy[itemIndex];
    }

    /**
     * Get the latent factors vector of an item (qi)
     * for male users
     * @param itemIndex User
     * @return Latent factors vector
     */
    public double[] getMaleItemFactors(int itemIndex) {
        return this.qo[itemIndex];
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

        double wy = Math.pow(s_u[userIndex][userIndex], alpha);
        double wo = Math.pow(1.0 - s_u[userIndex][userIndex], alpha);

        for (int k=0; k < this.getNumFactors(); k++){
            result += this.p[userIndex][k]
                    * ((wy * qy[itemIndex][k] + wo * qo[itemIndex][k])/(wy + wo));
        }

        return result;
    }

    @Override
    public String toString() {
        return "ABMF("
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
                + this.etay
                + "; "
                + "etam="
                + this.etao
                + "; "
                + "c1="
                + this.c1
                + "; "
                + "c2="
                + this.c2
                + "; "
                + "alpha="
                + this.alpha
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
                double wy = Math.pow(s_u[userIndex][userIndex], alpha);
                double wo = Math.pow(1.0 - s_u[userIndex][userIndex], alpha);
                for (int k = 0; k < numFactors; k++) {
                    p[userIndex][k] += gamma * (((wy * qy[itemIndex][k] + wo * qo[itemIndex][k])/(wy + wo)) * error - lambda * p[userIndex][k]);
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
                double wy = Math.pow(s_u[userIndex][userIndex], alpha);
                double wo = Math.pow(1.0 - s_u[userIndex][userIndex], alpha);
                for (int k = 0; k < numFactors; k++) {
                    qy[itemIndex][k] += etay * (error * (wy/(wy + wo)) * p[userIndex][k] - lambda * qy[itemIndex][k]);
                    qo[itemIndex][k] += etao * (error * (wo/(wy + wo)) * p[userIndex][k] - lambda * qo[itemIndex][k]);
                }
            }
        }

        @Override
        public void afterRun() {}
    }
}