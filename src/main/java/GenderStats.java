import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.recommender.knn.userSimilarityMetric.*;

public class GenderStats {
    private static final int[] AGES = {1, 18, 25, 35, 45, 50, 56};

    public GenderStats(){
    }

    public static void main (String[] args){
        try {
            DataModel datamodel = DataModel.load("ML-Analysis/Gender/Balanced/GenderBalanced/ml-1mnt");

            int genderF=0, genderM=0;
            int rGenderF=0, rGenderM=0;
            double mvnF, mvnM;
            double mvF=0, mvM=0;
            double stdF=0, stdM=0;
            int ageF_r1=0, ageF_r2=0, ageF_r3=0, ageF_r4=0, ageF_r5=0;
            int ageM_r1=0, ageM_r2=0, ageM_r3=0, ageM_r4=0, ageM_r5=0;

            for (User user : datamodel.getUsers()){
                double userGender = user.getDataBank().getDouble("gender");

                if (userGender == 0.0){
                    genderF++;
                } else if (userGender == 1.0){
                    genderM++;
                }

                for(int i=0; i<user.getNumberOfRatings(); i++){
                    double rating = user.getRatingAt(i);

                    if (userGender == 0.0){
                        mvF+=rating;
                        rGenderF++;
                        if (rating == 1.0){
                            ageF_r1++;
                        } else if (rating == 2.0){
                            ageF_r2++;
                        } else if (rating == 3.0) {
                            ageF_r3++;
                        }else if (rating == 4.0){
                            ageF_r4++;
                        }else if (rating == 5.0){
                            ageF_r5++;
                        }
                    } else if (userGender == 1.0){
                        mvM+=rating;
                        rGenderM++;

                        if (rating == 1.0){
                            ageM_r1++;
                        } else if (rating == 2.0){
                            ageM_r2++;
                        } else if (rating == 3.0) {
                            ageM_r3++;
                        }else if (rating == 4.0){
                            ageM_r4++;
                        }else if (rating == 5.0){
                            ageM_r5++;
                        }
                    }
                }
            }

            mvnF = (1.0 * rGenderF) / genderF;
            mvnM = (1.0 * rGenderM) / genderM;

            mvF = mvF / rGenderF;
            mvM = mvM / rGenderM;

            for (User user : datamodel.getUsers()){
                double userAge = user.getDataBank().getDouble("gender");

                for(int i=0; i<user.getNumberOfRatings(); i++) {
                    double rating = user.getRatingAt(i);
                    if (userAge == 0.0){
                        stdF += Math.pow((rating - mvF),2);
                    }else if (userAge == 1.0){
                        stdM += Math.pow((rating - mvM),2);
                    }
                }
            }
            stdF = Math.sqrt(stdF/rGenderF);
            stdM = Math.sqrt(stdM/rGenderM);


            double jmsd_r=0, cosine_r=0, pip_r=0, correlation_r=0, singularities_r=0, result=0;

            datamodel = DataModel.load("ML-Analysis/Gender/Balanced/Female/ml-1mnt");

            UserSimilarityMetric jmsd = new JMSD(), cosine = new Cosine(), pip = new PIP(),
                    correlation = new Correlation(),
                    singularities = new Singularities(new double[] {4.0, 5.0}, new double[] {1.0, 2.0, 3.0});

            jmsd.setDatamodel(datamodel);
            jmsd.beforeRun();
            cosine.setDatamodel(datamodel);
            cosine.beforeRun();
            pip.setDatamodel(datamodel);
            pip.beforeRun();
            correlation.setDatamodel(datamodel);
            correlation.beforeRun();
            singularities.setDatamodel(datamodel);
            singularities.beforeRun();

            for(User user: datamodel.getUsers()){
                for(User otherUser: datamodel.getUsers()){
                    result = jmsd.similarity(user, otherUser);
                    if(result != Double.NEGATIVE_INFINITY)
                        jmsd_r += result;

                    result = cosine.similarity(user, otherUser);
                    if(result != Double.NEGATIVE_INFINITY)
                        cosine_r += result;

                    result = pip.similarity(user, otherUser);
                    if(result != Double.NEGATIVE_INFINITY)
                        pip_r += result;

                    result = correlation.similarity(user, otherUser);
                    if(result != Double.NEGATIVE_INFINITY)
                        correlation_r += result;

                    result = singularities.similarity(user, otherUser);
                    if(result != Double.NEGATIVE_INFINITY)
                        singularities_r += result;
                }
            }
            System.out.println("==========GRUPO F============");
            System.out.println("Número de usuarios: " + genderF);
            System.out.println("Número de votos medio: " + mvnF);
            System.out.println("Cohesión: ");
            System.out.println("\t-JMSD: " + (jmsd_r/((genderF*genderF)-genderF)));
            System.out.println("\t-Cosine: " + (cosine_r/((genderF*genderF)-genderF)));
            System.out.println("\t-PIP: " + (pip_r/((genderF*genderF)-genderF)));
            System.out.println("\t-Correlation: " + (correlation_r/((genderF*genderF)-genderF)));
            System.out.println("\t-Singularities: " + (singularities_r/((genderF*genderF)-genderF)));
            System.out.println("Votación media: " + mvF);
            System.out.println("Desviación típica: " + stdF);
            System.out.println("Número de votos 1.0: " + ageF_r1);
            System.out.println("Número de votos 2.0: " + ageF_r2);
            System.out.println("Número de votos 3.0: " + ageF_r3);
            System.out.println("Número de votos 4.0: " + ageF_r4);
            System.out.println("Número de votos 5.0: " + ageF_r5);
            System.out.println("=============================");

            jmsd_r=0;
            cosine_r=0;
            pip_r=0;
            correlation_r=0;
            singularities_r=0;

            datamodel = DataModel.load("ML-Analysis/Gender/Balanced/Male/ml-1mnt");

            jmsd = new JMSD();
            cosine = new Cosine();
            pip = new PIP();
            correlation = new Correlation();
            singularities = new Singularities(new double[] {4.0, 5.0}, new double[] {1.0, 2.0, 3.0});

            jmsd.setDatamodel(datamodel);
            jmsd.beforeRun();
            cosine.setDatamodel(datamodel);
            cosine.beforeRun();
            pip.setDatamodel(datamodel);
            pip.beforeRun();
            correlation.setDatamodel(datamodel);
            correlation.beforeRun();
            singularities.setDatamodel(datamodel);
            singularities.beforeRun();

            for(User user: datamodel.getUsers()){
                for(User otherUser: datamodel.getUsers()){
                    result = jmsd.similarity(user, otherUser);
                    if(result != Double.NEGATIVE_INFINITY)
                        jmsd_r += result;

                    result = cosine.similarity(user, otherUser);
                    if(result != Double.NEGATIVE_INFINITY)
                        cosine_r += result;

                    result = pip.similarity(user, otherUser);
                    if(result != Double.NEGATIVE_INFINITY)
                        pip_r += result;

                    result = correlation.similarity(user, otherUser);
                    if(result != Double.NEGATIVE_INFINITY)
                        correlation_r += result;

                    result = singularities.similarity(user, otherUser);
                    if(result != Double.NEGATIVE_INFINITY)
                        singularities_r += result;
                }
            }
            System.out.println("==========GRUPO M===========");
            System.out.println("Número de usuarios: " + genderM);
            System.out.println("Número de votos medio: " + mvnM);
            System.out.println("Cohesión: ");
            System.out.println("\t-JMSD: " + (jmsd_r/((genderM*genderM)-genderM)));
            System.out.println("\t-Cosine: " + (cosine_r/((genderM*genderM)-genderM)));
            System.out.println("\t-PIP: " + (pip_r/((genderM*genderM)-genderM)));
            System.out.println("\t-Correlation: " + (correlation_r/((genderM*genderM)-genderM)));
            System.out.println("\t-Singularities: " + (singularities_r/((genderM*genderM)-genderM)));
            System.out.println("Votación media: " + mvM);
            System.out.println("Desviación típica: " + stdM);
            System.out.println("Número de votos 1.0: " + ageM_r1);
            System.out.println("Número de votos 2.0: " + ageM_r2);
            System.out.println("Número de votos 3.0: " + ageM_r3);
            System.out.println("Número de votos 4.0: " + ageM_r4);
            System.out.println("Número de votos 5.0: " + ageM_r5);
            System.out.println("=============================");

        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}