import es.upm.etsisi.cf4j.data.*;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.PMF;

import java.io.*;
import java.util.*;

public class RPMF extends Recommender implements Iterable<RPMF>{
    protected static final double DEFAULT_GAMMA = 0.01;
    protected static final double DEFAULT_LAMBDA = 0.05;

    /** Path out */
    protected String pathout;

    /** Age range*/
    protected double[] ageRange;

    /** Recommender */
    protected PMF recommender;

    /** Parent RPMF */
    public RPMF parent;

    /** Children RPMF */
    public List<RPMF> children;

    /** Index */
    private List<RPMF> elementsIndex;

    /** Learning rate */
    protected final double gamma;

    /** Regularization parameter */
    protected final double lambda;

    /** Number of latent factors */
    protected final int numFactors;

    /** Number of iterations */
    protected final int numIters;

    /** Seed */
    protected final long seed;

    public RPMF(DataModel datamodel, String pathout, double[] ageRange, int numFactors, int numIters, double lambda, double gamma, long seed){
        super(datamodel);

        this.pathout = pathout;
        this.ageRange = ageRange;

        this.numFactors = numFactors;
        this.numIters = numIters;
        this.lambda = lambda;
        this.gamma = gamma;
        this.seed = seed;

        recommender = new PMF(datamodel, this.numFactors, this.numIters, this.lambda, this.gamma, this.seed);

        this.children = new LinkedList<RPMF>();
        this.elementsIndex = new LinkedList<RPMF>();
        this.elementsIndex.add(this);
    }

    public boolean isRoot() {
        return parent == null;
    }

    public boolean isLeaf() {
        return children.size() == 0;
    }

    public int getLevel() {
        if (this.isRoot())
            return 0;
        else
            return parent.getLevel() + 1;
    }

    public RPMF addChild(int childnumber, double[] ageRange, int numFactors, int numIters, double lambda, double gamma, long seed) throws Exception{
        String childpathout = pathout + childnumber;
        DataModel childdatamodel = DataModel.load(pathout + "/ml-1m-" + childnumber);
        RPMF childNode = new RPMF(childdatamodel, childpathout, ageRange, numFactors, numIters, lambda, gamma, seed);
        childNode.parent = this;
        this.children.add(childNode);
        this.registerChildForSearch(childNode);
        return childNode;
    }

    private void registerChildForSearch(RPMF node) {
        elementsIndex.add(node);
        if (parent != null)
            parent.registerChildForSearch(node);
    }

    public RPMF findTreeNode(Comparable<PMF> cmp) {
        for (RPMF element : this.elementsIndex) {
            PMF elData = element.recommender;
            if (cmp.compareTo(elData) == 0)
                return element;
        }

        return null;
    }

    @Override
    public Iterator<RPMF> iterator() {
        //Iterator<RPMF> iter = new NodeIter<RPMF>(this);
        //return iter;
        return null;
    }

    public void fit() {
        try {
            this.recommender.fit();
            /*if(this.getLevel()< 2) {
                this.generateErrors();
                this.generateDatamodel();
            }*/
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public double predict(int userId, int itemId){
        double result = 0.0;

        int userIndex = this.datamodel.findUserIndex(String.valueOf(userId));
        if(userIndex != -1) {
            if (this.datamodel.getUser(userIndex) != null) {
                int itemIndex = this.datamodel.findItemIndex(String.valueOf(itemId));

                result = this.recommender.predict(userIndex, itemIndex);
                for (RPMF child : children) {
                    result += child.predict(userId, itemId);
                }
            }
        }

        return result;
    }

    public double[] predict(TestUser testUser) {
        int userId = Integer.parseInt(testUser.getId());
        double[] predictions = new double[testUser.getNumberOfTestRatings()];
        for (int i = 0; i < predictions.length; i++) {
            int testItemIndex = testUser.getTestItemAt(i);
            TestItem testItem = this.datamodel.getTestItem(testItemIndex);
            int itemId = Integer.parseInt(testItem.getId());
            predictions[i] = this.predict(userId, itemId);
        }
        return predictions;
    }

    public void generateErrors() throws Exception{
        String linea;

        File archivoRatings = null;
        FileReader frR = null;
        BufferedReader brR = null;

        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;

        FileWriter fichero = null;
        PrintWriter pw = null;

        for(int i=1; i<3; i++) {
            Map<String, String> id2Index = new HashMap();

            archivo = new File (pathout + "/users.dat");
            fr = new FileReader (archivo);
            br = new BufferedReader(fr);

            while((linea=br.readLine())!=null){
                String[] parts = linea.split("::");
                id2Index.put(parts[0], parts[0]);
            }

            archivoRatings = new File (pathout + "/ratings.dat");
            frR = new FileReader (archivoRatings);
            brR = new BufferedReader(frR);

            fichero = new FileWriter(pathout + "/errors" + i + ".dat");
            pw = new PrintWriter(fichero);

            while((linea=brR.readLine())!=null){
                String[] parts = linea.split("::");
                if(id2Index.containsKey(parts[0])) {
                    double rating = Double.parseDouble(parts[2]);
                    rating = this.recommender.predict(this.datamodel.findUserIndex(parts[0]), this.datamodel.findItemIndex(parts[1])) - rating;
                    pw.println(parts[0] + "::" + parts[1] + "::" + String.valueOf(rating) + "::" + parts[3]);
                }
            }
        }
    }

    public void generateDatamodel() throws Exception{
        System.out.println(pathout);

        for(int i=1; i<3; i++) {
            DataSet dataset = new RandomSplitDataSet(pathout + "/errors" + i + ".dat", 0.2, 0.1, "::");
            DataModel datamodel = new DataModel(dataset);

            datamodel.save(pathout + "/ml-1m-" + i);
        }
    }
}

