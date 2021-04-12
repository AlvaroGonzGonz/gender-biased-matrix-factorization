import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.qualityMeasure.QualityMeasure;
import es.upm.etsisi.cf4j.qualityMeasure.prediction.MAE;
import es.upm.etsisi.cf4j.recommender.knn.UserKNN;
import es.upm.etsisi.cf4j.recommender.knn.userSimilarityMetric.*;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.BNMF;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.BiasedMF;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.NMF;
import es.upm.etsisi.cf4j.recommender.matrixFactorization.PMF;
import es.upm.etsisi.cf4j.util.plot.LinePlot;

public class AgeAnalysis {
    private static final int[] AGES = {1, 18, 25, 35, 45, 50, 56};
    private static final long SEED = 43L;

    public static void main(String[] args){
        DataModel datamodel;

        try {
            LinePlot plot = new LinePlot(AGES, "User age", "MAE");
            plot.addSeries("PMF");
            plot.addSeries("BiasedMF");
            plot.addSeries("BeMF");
            plot.addSeries("BNMF");
            plot.addSeries("NMF");
            plot.addSeries("JMSD");
            plot.addSeries("Cosine");
            plot.addSeries("PIP");
            plot.addSeries("Correlation");
            plot.addSeries("Singularities");

            for (int age : AGES) {
                datamodel = DataModel.load("ML-Analysis/Balanced/Age" + age + "/ml-1m");

                //PMF Recommender
                PMF pmf = new PMF(datamodel, 8, 100, 0.045, 0.01, SEED);
                pmf.fit();
                QualityMeasure pmfmae = new MAE(pmf);
                plot.setValue("PMF", age, pmfmae.getScore());

                //Biased Recommender
                BiasedMF biasedmf = new BiasedMF(datamodel, 6, 100, 0.055, 0.01, SEED);
                biasedmf.fit();
                QualityMeasure biasedmae = new MAE(biasedmf);
                plot.setValue("BiasedMF", age, biasedmae.getScore());

                //BeMF
                BeMF bemf = new BeMF(datamodel, 2, 100, 0.006, 0.16, new double[] {1.0, 2.0, 3.0, 4.0, 5.0}, SEED);
                bemf.fit();
                QualityMeasure bemfmae = new MAE(bemf);
                plot.setValue("BeMF", age, bemfmae.getScore());

                //BNMF Recommender
                BNMF bnmf = new BNMF(datamodel, 10, 100, 0.6, 5, 4, SEED);
                bnmf.fit();
                QualityMeasure bnmfmae = new MAE(bnmf);
                plot.setValue("BNMF", age, bnmfmae.getScore());

                //NMF Recommender
                NMF nmf = new NMF(datamodel, 2, 100, SEED);
                nmf.fit();
                QualityMeasure nmfmae = new MAE(nmf);
                plot.setValue("NMF", age, nmfmae.getScore());

                //kNN-JMSD Recommender
                UserKNN jmsd = new UserKNN(datamodel, 75, new JMSD(), UserKNN.AggregationApproach.DEVIATION_FROM_MEAN);
                jmsd.fit();
                QualityMeasure jmsdmae = new MAE(jmsd);
                plot.setValue("JMSD", age, jmsdmae.getScore());

                //kNN-Cosine Recommender
                UserKNN cosine = new UserKNN(datamodel, 75, new Cosine(), UserKNN.AggregationApproach.DEVIATION_FROM_MEAN);
                cosine.fit();
                QualityMeasure cosinemae = new MAE(cosine);
                plot.setValue("Cosine", age, cosinemae.getScore());

                //kNN-PIP Recommender
                UserKNN pip = new UserKNN(datamodel, 75, new PIP(), UserKNN.AggregationApproach.DEVIATION_FROM_MEAN);
                pip.fit();
                QualityMeasure pipmae = new MAE(pip);
                plot.setValue("PIP", age, pipmae.getScore());

                //kNN-Correlation Recommender
                UserKNN correlation = new UserKNN(datamodel, 75, new Correlation(), UserKNN.AggregationApproach.DEVIATION_FROM_MEAN);
                correlation.fit();
                QualityMeasure cormae = new MAE(correlation);
                plot.setValue("Correlation", age, cormae.getScore());

                //kNN-Singularities Recommender
                UserKNN singularities = new UserKNN(datamodel, 75, new Singularities(new double[] {4.0, 5.0}, new double[] {1.0, 2.0, 3.0}), UserKNN.AggregationApproach.DEVIATION_FROM_MEAN);
                singularities.fit();
                QualityMeasure singmae = new MAE(singularities);
                plot.setValue("Singularities", age, singmae.getScore());

            }

            plot.printData("0", "0.0000");

            //
            // =============== Edades en conjunto ========================
            //

            datamodel = DataModel.load("ML-Analysis/Balanced/ml-1m/ml-1m");
            plot = new LinePlot(AGES, "User age", "MAE");

            //PMF Recommender
            plot.addSeries("PMF");
            PMF pmf = new PMF(datamodel, 8, 100, 0.045, 0.01, SEED);
            pmf.fit();

            //Biased Recommender
            plot.addSeries("BiasedMF");
            BiasedMF biasedmf = new BiasedMF(datamodel, 6, 100, 0.055, 0.01, SEED);
            biasedmf.fit();

            //BeMF
            plot.addSeries("BeMF");
            BeMF bemf = new BeMF(datamodel, 2, 100, 0.006, 0.16, new double[] {1.0, 2.0, 3.0, 4.0, 5.0}, SEED);
            bemf.fit();

            //BNMF Recommender
            plot.addSeries("BNMF");
            BNMF bnmf = new BNMF(datamodel, 10, 100, 0.6, 5, 4, SEED);
            bnmf.fit();

            //NMF Recommender
            plot.addSeries("NMF");
            NMF nmf = new NMF(datamodel, 2, 100, SEED);
            nmf.fit();

            //kNN-JMSD Recommender
            plot.addSeries("JMSD");
            UserKNN jmsd = new UserKNN(datamodel, 75, new JMSD(), UserKNN.AggregationApproach.DEVIATION_FROM_MEAN);
            jmsd.fit();

            //kNN-Cosine Recommender
            plot.addSeries("Cosine");
            UserKNN cosine = new UserKNN(datamodel, 75, new Cosine(), UserKNN.AggregationApproach.DEVIATION_FROM_MEAN);
            cosine.fit();

            //kNN-PIP Recommender
            plot.addSeries("PIP");
            UserKNN pip = new UserKNN(datamodel, 75, new PIP(), UserKNN.AggregationApproach.DEVIATION_FROM_MEAN);
            pip.fit();

            //kNN-Correlation Recommender
            plot.addSeries("Correlation");
            UserKNN correlation = new UserKNN(datamodel, 75, new Correlation(), UserKNN.AggregationApproach.DEVIATION_FROM_MEAN);
            correlation.fit();

            //kNN-Singularities Recommender
            plot.addSeries("Singularities");
            UserKNN singularities = new UserKNN(datamodel, 75, new Singularities(new double[] {4.0, 5.0}, new double[] {1.0, 2.0, 3.0}), UserKNN.AggregationApproach.DEVIATION_FROM_MEAN);
            singularities.fit();

            QualityMeasure qm;
            for (int age : AGES){
                qm = new AMAE(pmf, age);
                plot.setValue("PMF", age, qm.getScore());

                qm = new AMAE(biasedmf, age);
                plot.setValue("BiasedMF", age, qm.getScore());

                qm = new AMAE(bemf, age);
                plot.setValue("BeMF", age, qm.getScore());

                qm = new AMAE(bnmf, age);
                plot.setValue("BNMF", age, qm.getScore());

                qm = new AMAE(nmf, age);
                plot.setValue("NMF", age, qm.getScore());

                qm = new AMAE(jmsd, age);
                plot.setValue("JMSD", age, qm.getScore());

                qm = new AMAE(cosine, age);
                plot.setValue("Cosine", age, qm.getScore());

                qm = new AMAE(pip, age);
                plot.setValue("PIP", age, qm.getScore());

                qm = new AMAE(correlation, age);
                plot.setValue("Correlation", age, qm.getScore());

                qm = new AMAE(singularities, age);
                plot.setValue("Singularities", age, qm.getScore());
            }

            plot.printData("0", "0.0000");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
