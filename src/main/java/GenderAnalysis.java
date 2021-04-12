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

public class GenderAnalysis {
    private static final String[] GENDERS = {"Female", "Male"};
    private static final int[] aux = {0, 1};
    private static final long SEED = 43L;

    public static void main(String[] args){
        DataModel datamodel;

        try {
            LinePlot plot = new LinePlot(aux, "User age", "MAE");
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

            for (String gender : GENDERS) {
                datamodel = DataModel.load("ML-Analysis/Gender/Balanced/" + gender + "/ml-1m");

                int gn=0;
                if(gender.equals("Female"))
                    gn=0;
                else if(gender.equals("Male"))
                    gn=1;

                //PMF Recommender
                PMF pmf = new PMF(datamodel, 8, 100, 0.045, 0.01, SEED);
                pmf.fit();
                QualityMeasure pmfmae = new MAE(pmf);
                plot.setValue("PMF", gn, pmfmae.getScore());

                //Biased Recommender
                BiasedMF biasedmf = new BiasedMF(datamodel, 6, 100, 0.055, 0.01, SEED);
                biasedmf.fit();
                QualityMeasure biasedmae = new MAE(biasedmf);
                plot.setValue("BiasedMF", gn, biasedmae.getScore());

                //BeMF
                BeMF bemf = new BeMF(datamodel, 2, 100, 0.006, 0.16, new double[] {1.0, 2.0, 3.0, 4.0, 5.0}, SEED);
                bemf.fit();
                QualityMeasure bemfmae = new MAE(bemf);
                plot.setValue("BeMF", gn, bemfmae.getScore());

                //BNMF Recommender
                BNMF bnmf = new BNMF(datamodel, 10, 100, 0.6, 5, 4, SEED);
                bnmf.fit();
                QualityMeasure bnmfmae = new MAE(bnmf);
                plot.setValue("BNMF", gn, bnmfmae.getScore());

                //NMF Recommender
                NMF nmf = new NMF(datamodel, 2, 100, SEED);
                nmf.fit();
                QualityMeasure nmfmae = new MAE(nmf);
                plot.setValue("NMF", gn, nmfmae.getScore());

                //kNN-JMSD Recommender
                UserKNN jmsd = new UserKNN(datamodel, 75, new JMSD(), UserKNN.AggregationApproach.DEVIATION_FROM_MEAN);
                jmsd.fit();
                QualityMeasure jmsdmae = new MAE(jmsd);
                plot.setValue("JMSD", gn, jmsdmae.getScore());

                //kNN-Cosine Recommender
                UserKNN cosine = new UserKNN(datamodel, 75, new Cosine(), UserKNN.AggregationApproach.DEVIATION_FROM_MEAN);
                cosine.fit();
                QualityMeasure cosinemae = new MAE(cosine);
                plot.setValue("Cosine", gn, cosinemae.getScore());

                //kNN-PIP Recommender
                UserKNN pip = new UserKNN(datamodel, 75, new PIP(), UserKNN.AggregationApproach.DEVIATION_FROM_MEAN);
                pip.fit();
                QualityMeasure pipmae = new MAE(pip);
                plot.setValue("PIP", gn, pipmae.getScore());

                //kNN-Correlation Recommender
                UserKNN correlation = new UserKNN(datamodel, 75, new Correlation(), UserKNN.AggregationApproach.DEVIATION_FROM_MEAN);
                correlation.fit();
                QualityMeasure cormae = new MAE(correlation);
                plot.setValue("Correlation", gn, cormae.getScore());

                //kNN-Singularities Recommender
                UserKNN singularities = new UserKNN(datamodel, 75, new Singularities(new double[] {4.0, 5.0}, new double[] {1.0, 2.0, 3.0}), UserKNN.AggregationApproach.DEVIATION_FROM_MEAN);
                singularities.fit();
                QualityMeasure singmae = new MAE(singularities);
                plot.setValue("Singularities", gn, singmae.getScore());

            }

            plot.printData("0", "0.0000");

            //
            // =============== Géneros en conjunto ========================
            //

            datamodel = DataModel.load("ML-Analysis/Gender/Balanced/GenderBalanced/ml-1m");
            plot = new LinePlot(aux, "User age", "MAE");

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
            for (String gender : GENDERS){
                double gn=0.0;
                if(gender.equals("Female"))
                    gn=0.0;
                else if(gender.equals("Male"))
                    gn=1.0;

                qm = new GMAE(pmf, gn);
                plot.setValue("PMF", (int) gn, qm.getScore());

                qm = new GMAE(biasedmf, gn);
                plot.setValue("BiasedMF", (int) gn, qm.getScore());

                qm = new GMAE(bemf, gn);
                plot.setValue("BeMF", (int) gn, qm.getScore());

                qm = new GMAE(bnmf, gn);
                plot.setValue("BNMF", (int) gn, qm.getScore());

                qm = new GMAE(nmf, gn);
                plot.setValue("NMF", (int) gn, qm.getScore());

                qm = new GMAE(jmsd, gn);
                plot.setValue("JMSD", (int) gn, qm.getScore());

                qm = new GMAE(cosine, gn);
                plot.setValue("Cosine", (int) gn, qm.getScore());

                qm = new GMAE(pip, gn);
                plot.setValue("PIP", (int) gn, qm.getScore());

                qm = new GMAE(correlation, gn);
                plot.setValue("Correlation", (int) gn, qm.getScore());

                qm = new GMAE(singularities, gn);
                plot.setValue("Singularities", (int) gn, qm.getScore());
            }

            plot.printData("0", "0.0000");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}