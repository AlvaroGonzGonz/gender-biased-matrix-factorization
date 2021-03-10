import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.recommender.Recommender;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.PMF;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RPMF extends Recommender implements Iterable<RPMF>{
    protected static final double DEFAULT_GAMMA = 0.01;
    protected static final double DEFAULT_LAMBDA = 0.05;

    /** PMFs root */
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

    public RPMF(DataModel datamodel, int numFactors, int numIters, double lambda, double gamma, long seed){
        super(datamodel);

        this.numFactors = numFactors;
        this.numIters = numIters;
        this.lambda = lambda;
        this.gamma = gamma;

        Random rand = new Random(seed);

        recommender = new PMF(datamodel, this.numFactors, this.numIters, this.lambda, this.gamma, seed);

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

    public RPMF addChild(DataModel datamodel, int numFactors, int numIters, double lambda, double gamma, long seed) {
        RPMF childNode = new RPMF(datamodel, this.numFactors, this.numIters, this.lambda, this.gamma, seed);
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

    public void fit(){
        this.recommender.fit();
         for(RPMF child : children){
             child.fit();
         }
    }

    public double predict(int userIndex, int itemIndex){
        double result = this.recommender.predict(userIndex, itemIndex);
        for(RPMF child : children){
            result += child.predict(userIndex, itemIndex);
        }

        return result;
    }
}

