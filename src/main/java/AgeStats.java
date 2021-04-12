import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.User;
import es.upm.etsisi.cf4j.recommender.knn.userSimilarityMetric.*;

public class AgeStats {
    private static final int[] AGES = {1, 18, 25, 35, 45, 50, 56};

    public AgeStats(){
    }

    public static void main (String[] args){
        try {
            DataModel datamodel = DataModel.load("ml-1mnt");

            int age1=0, age18=0, age25=0, age35=0, age45=0, age50=0, age56=0;
            int rage1=0, rage18=0, rage25=0, rage35=0, rage45=0, rage50=0, rage56=0;
            double mvn1, mvn18, mvn25, mvn35, mvn45, mvn50, mvn56;
            double mv1=0, mv18=0, mv25=0, mv35=0, mv45=0, mv50=0, mv56=0;
            double std1=0, std18=0, std25=0, std35=0, std45=0, std50=0, std56=0;
            int age1_r1=0, age1_r2=0, age1_r3=0, age1_r4=0, age1_r5=0;
            int age18_r1=0, age18_r2=0, age18_r3=0, age18_r4=0, age18_r5=0;
            int age25_r1=0, age25_r2=0, age25_r3=0, age25_r4=0, age25_r5=0;
            int age35_r1=0, age35_r2=0, age35_r3=0, age35_r4=0, age35_r5=0;
            int age45_r1=0, age45_r2=0, age45_r3=0, age45_r4=0, age45_r5=0;
            int age50_r1=0, age50_r2=0, age50_r3=0, age50_r4=0, age50_r5=0;
            int age56_r1=0, age56_r2=0, age56_r3=0, age56_r4=0, age56_r5=0;

            for (User user : datamodel.getUsers()){
                int userAge = user.getDataBank().getInt("age");

                if (userAge == 1){
                    age1++;
                } else if (userAge == 18){
                    age18++;
                } else if (userAge == 25){
                    age25++;
                } else if (userAge == 35){
                    age35++;
                } else if (userAge == 45){
                    age45++;
                } else if (userAge == 50){
                    age50++;
                } else if (userAge == 56){
                    age56++;
                }

                for(int i=0; i<user.getNumberOfRatings(); i++){
                    double rating = user.getRatingAt(i);

                    if (userAge == 1){
                        mv1+=rating;
                        rage1++;
                        if (rating == 1.0){
                            age1_r1++;
                        } else if (rating == 2.0){
                            age1_r2++;
                        } else if (rating == 3.0) {
                            age1_r3++;
                        }else if (rating == 4.0){
                            age1_r4++;
                        }else if (rating == 5.0){
                            age1_r5++;
                        }
                    } else if (userAge == 18){
                        mv18+=rating;
                        rage18++;

                        if (rating == 1.0){
                            age18_r1++;
                        } else if (rating == 2.0){
                            age18_r2++;
                        } else if (rating == 3.0) {
                            age18_r3++;
                        }else if (rating == 4.0){
                            age18_r4++;
                        }else if (rating == 5.0){
                            age18_r5++;
                        }
                    } else if (userAge == 25){
                        mv25+=rating;
                        rage25++;

                        if (rating == 1.0){
                            age25_r1++;
                        } else if (rating == 2.0){
                            age25_r2++;
                        } else if (rating == 3.0) {
                            age25_r3++;
                        }else if (rating == 4.0){
                            age25_r4++;
                        }else if (rating == 5.0){
                            age25_r5++;
                        }
                    } else if (userAge == 35){
                        mv35+=rating;
                        rage35++;

                        if (rating == 1.0){
                            age35_r1++;
                        } else if (rating == 2.0){
                            age35_r2++;
                        } else if (rating == 3.0) {
                            age35_r3++;
                        }else if (rating == 4.0){
                            age35_r4++;
                        }else if (rating == 5.0){
                            age35_r5++;
                        }
                    } else if (userAge == 45){
                        mv45+=rating;
                        rage45++;

                        if (rating == 1.0){
                            age45_r1++;
                        } else if (rating == 2.0){
                            age45_r2++;
                        } else if (rating == 3.0) {
                            age45_r3++;
                        }else if (rating == 4.0){
                            age45_r4++;
                        }else if (rating == 5.0){
                            age45_r5++;
                        }
                    } else if (userAge == 50){
                        mv50+=rating;
                        rage50++;

                        if (rating == 1.0){
                            age50_r1++;
                        } else if (rating == 2.0){
                            age50_r2++;
                        } else if (rating == 3.0) {
                            age50_r3++;
                        }else if (rating == 4.0){
                            age50_r4++;
                        }else if (rating == 5.0){
                            age50_r5++;
                        }
                    } else if (userAge == 56){
                        mv56+=rating;
                        rage56++;

                        if (rating == 1.0){
                            age56_r1++;
                        } else if (rating == 2.0){
                            age56_r2++;
                        } else if (rating == 3.0) {
                            age56_r3++;
                        }else if (rating == 4.0){
                            age56_r4++;
                        }else if (rating == 5.0){
                            age56_r5++;
                        }
                    }
                }
            }

            mvn1 = (1.0 * rage1) / age1;
            mvn18 = (1.0 * rage18) / age18;
            mvn25 = (1.0 * rage25) / age25;
            mvn35 = (1.0 * rage35) / age35;
            mvn45 = (1.0 * rage45) / age45;
            mvn50 = (1.0 * rage50) / age50;
            mvn56 = (1.0 * rage56) / age56;

            mv1 = mv1 / rage1;
            mv18 = mv18 / rage18;
            mv25 = mv25 / rage25;
            mv35 = mv35 / rage35;
            mv45 = mv45 / rage45;
            mv50 = mv50 / rage50;
            mv56 = mv56 / rage56;

            for (User user : datamodel.getUsers()){
                int userAge = user.getDataBank().getInt("age");

                for(int i=0; i<user.getNumberOfRatings(); i++) {
                    double rating = user.getRatingAt(i);
                    if (userAge == 1){
                        std1 += Math.pow((rating - mv1),2);
                    }else if (userAge == 18){
                        std18 += Math.pow((rating - mv18),2);
                    }else if (userAge == 25){
                        std25 += Math.pow((rating - mv25),2);
                    }else if (userAge == 35){
                        std35 += Math.pow((rating - mv35),2);
                    }else if (userAge == 45){
                        std45 += Math.pow((rating - mv45),2);
                    }else if (userAge == 50){
                        std50 += Math.pow((rating - mv50),2);
                    }else if (userAge == 56){
                        std56 += Math.pow((rating - mv56),2);
                    }
                }
            }
            std1 = Math.sqrt(std1/rage1);
            std18 = Math.sqrt(std18/rage18);
            std25 = Math.sqrt(std25/rage25);
            std35 = Math.sqrt(std35/rage35);
            std45 = Math.sqrt(std45/rage45);
            std50 = Math.sqrt(std50/rage50);
            std56 = Math.sqrt(std56/rage56);


            double jmsd_r=0, cosine_r=0, pip_r=0, correlation_r=0, singularities_r=0, result=0;

            datamodel = DataModel.load("ML-Analysis/Standard/Age1/ml-1m");

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
            System.out.println("==========GRUPO 1============");
            System.out.println("Número de usuarios: " + age1);
            System.out.println("Número de votos medio: " + mvn1);
            System.out.println("Cohesión: ");
            System.out.println("\t-JMSD: " + (jmsd_r/((age1*age1)-age1)));
            System.out.println("\t-Cosine: " + (cosine_r/((age1*age1)-age1)));
            System.out.println("\t-PIP: " + (pip_r/((age1*age1)-age1)));
            System.out.println("\t-Correlation: " + (correlation_r/((age1*age1)-age1)));
            System.out.println("\t-Singularities: " + (singularities_r/((age1*age1)-age1)));
            System.out.println("Votación media: " + mv1);
            System.out.println("Desviación típica: " + std1);
            System.out.println("Número de votos 1.0: " + age1_r1);
            System.out.println("Número de votos 2.0: " + age1_r2);
            System.out.println("Número de votos 3.0: " + age1_r3);
            System.out.println("Número de votos 4.0: " + age1_r4);
            System.out.println("Número de votos 5.0: " + age1_r5);
            System.out.println("=============================");

            jmsd_r=0;
            cosine_r=0;
            pip_r=0;
            correlation_r=0;
            singularities_r=0;

            datamodel = DataModel.load("ML-Analysis/Standard/Age18/ml-1m");

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
            System.out.println("==========GRUPO 18===========");
            System.out.println("Número de usuarios: " + age18);
            System.out.println("Número de votos medio: " + mvn18);
            System.out.println("Cohesión: ");
            System.out.println("\t-JMSD: " + (jmsd_r/((age18*age18)-age18)));
            System.out.println("\t-Cosine: " + (cosine_r/((age18*age18)-age18)));
            System.out.println("\t-PIP: " + (pip_r/((age18*age18)-age18)));
            System.out.println("\t-Correlation: " + (correlation_r/((age18*age18)-age18)));
            System.out.println("\t-Singularities: " + (singularities_r/((age18*age18)-age18)));
            System.out.println("Votación media: " + mv18);
            System.out.println("Desviación típica: " + std18);
            System.out.println("Número de votos 1.0: " + age18_r1);
            System.out.println("Número de votos 2.0: " + age18_r2);
            System.out.println("Número de votos 3.0: " + age18_r3);
            System.out.println("Número de votos 4.0: " + age18_r4);
            System.out.println("Número de votos 5.0: " + age18_r5);
            System.out.println("=============================");

            jmsd_r=0;
            cosine_r=0;
            pip_r=0;
            correlation_r=0;
            singularities_r=0;

            datamodel = DataModel.load("ML-Analysis/Standard/Age25/ml-1m");

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
            System.out.println("==========GRUPO 25===========");
            System.out.println("Número de usuarios: " + age25);
            System.out.println("Número de votos medio: " + mvn25);
            System.out.println("Cohesión: ");
            System.out.println("\t-JMSD: " + (jmsd_r/((age25*age25)-age25)));
            System.out.println("\t-Cosine: " + (cosine_r/((age25*age25)-age25)));
            System.out.println("\t-PIP: " + (pip_r/((age25*age25)-age25)));
            System.out.println("\t-Correlation: " + (correlation_r/((age25*age25)-age25)));
            System.out.println("\t-Singularities: " + (singularities_r/((age25*age25)-age25)));
            System.out.println("Votación media: " + mv25);
            System.out.println("Desviación típica: " + std25);
            System.out.println("Número de votos 1.0: " + age25_r1);
            System.out.println("Número de votos 2.0: " + age25_r2);
            System.out.println("Número de votos 3.0: " + age25_r3);
            System.out.println("Número de votos 4.0: " + age25_r4);
            System.out.println("Número de votos 5.0: " + age25_r5);
            System.out.println("=============================");

            jmsd_r=0;
            cosine_r=0;
            pip_r=0;
            correlation_r=0;
            singularities_r=0;

            datamodel = DataModel.load("ML-Analysis/Standard/Age35/ml-1m");

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
            System.out.println("==========GRUPO 35===========");
            System.out.println("Número de usuarios: " + age35);
            System.out.println("Número de votos medio: " + mvn35);
            System.out.println("Cohesión: ");
            System.out.println("\t-JMSD: " + (jmsd_r/((age35*age35)-age35)));
            System.out.println("\t-Cosine: " + (cosine_r/((age35*age35)-age35)));
            System.out.println("\t-PIP: " + (pip_r/((age35*age35)-age35)));
            System.out.println("\t-Correlation: " + (correlation_r/((age35*age35)-age35)));
            System.out.println("\t-Singularities: " + (singularities_r/((age35*age35)-age35)));
            System.out.println("Votación media: " + mv35);
            System.out.println("Desviación típica: " + std35);
            System.out.println("Número de votos 1.0: " + age35_r1);
            System.out.println("Número de votos 2.0: " + age35_r2);
            System.out.println("Número de votos 3.0: " + age35_r3);
            System.out.println("Número de votos 4.0: " + age35_r4);
            System.out.println("Número de votos 5.0: " + age35_r5);
            System.out.println("=============================");

            jmsd_r=0;
            cosine_r=0;
            pip_r=0;
            correlation_r=0;
            singularities_r=0;

            datamodel = DataModel.load("ML-Analysis/Standard/Age45/ml-1m");

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
            System.out.println("==========GRUPO 45===========");
            System.out.println("Número de usuarios: " + age45);
            System.out.println("Número de votos medio: " + mvn45);
            System.out.println("Cohesión: ");
            System.out.println("\t-JMSD: " + (jmsd_r/((age45*age45)-age45)));
            System.out.println("\t-Cosine: " + (cosine_r/((age45*age45)-age45)));
            System.out.println("\t-PIP: " + (pip_r/((age45*age45)-age45)));
            System.out.println("\t-Correlation: " + (correlation_r/((age45*age45)-age45)));
            System.out.println("\t-Singularities: " + (singularities_r/((age45*age45)-age45)));
            System.out.println("Votación media: " + mv45);
            System.out.println("Desviación típica: " + std45);
            System.out.println("Número de votos 1.0: " + age45_r1);
            System.out.println("Número de votos 2.0: " + age45_r2);
            System.out.println("Número de votos 3.0: " + age45_r3);
            System.out.println("Número de votos 4.0: " + age45_r4);
            System.out.println("Número de votos 5.0: " + age45_r5);
            System.out.println("=============================");

            jmsd_r=0;
            cosine_r=0;
            pip_r=0;
            correlation_r=0;
            singularities_r=0;

            datamodel = DataModel.load("ML-Analysis/Standard/Age50/ml-1m");

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
            System.out.println("==========GRUPO 50===========");
            System.out.println("Número de usuarios: " + age50);
            System.out.println("Número de votos medio: " + mvn50);
            System.out.println("Cohesión: ");
            System.out.println("\t-JMSD: " + (jmsd_r/((age50*age50)-age50)));
            System.out.println("\t-Cosine: " + (cosine_r/((age50*age50)-age50)));
            System.out.println("\t-PIP: " + (pip_r/((age50*age50)-age50)));
            System.out.println("\t-Correlation: " + (correlation_r/((age50*age50)-age50)));
            System.out.println("\t-Singularities: " + (singularities_r/((age50*age50)-age50)));
            System.out.println("Votación media: " + mv50);
            System.out.println("Desviación típica: " + std50);
            System.out.println("Número de votos 1.0: " + age50_r1);
            System.out.println("Número de votos 2.0: " + age50_r2);
            System.out.println("Número de votos 3.0: " + age50_r3);
            System.out.println("Número de votos 4.0: " + age50_r4);
            System.out.println("Número de votos 5.0: " + age50_r5);
            System.out.println("=============================");

            jmsd_r=0;
            cosine_r=0;
            pip_r=0;
            correlation_r=0;
            singularities_r=0;

            datamodel = DataModel.load("ML-Analysis/Standard/Age56/ml-1m");

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
            System.out.println("==========GRUPO 56===========");
            System.out.println("Número de usuarios: " + age56);
            System.out.println("Número de votos medio: " + mvn56);
            System.out.println("Cohesión: ");
            System.out.println("\t-JMSD: " + (jmsd_r/((age56*age56)-age56)));
            System.out.println("\t-Cosine: " + (cosine_r/((age56*age56)-age56)));
            System.out.println("\t-PIP: " + (pip_r/((age56*age56)-age56)));
            System.out.println("\t-Correlation: " + (correlation_r/((age56*age56)-age56)));
            System.out.println("\t-Singularities: " + (singularities_r/((age56*age56)-age56)));
            System.out.println("Votación media: " + mv56);
            System.out.println("Desviación típica: " + std56);
            System.out.println("Número de votos 1.0: " + age56_r1);
            System.out.println("Número de votos 2.0: " + age56_r2);
            System.out.println("Número de votos 3.0: " + age56_r3);
            System.out.println("Número de votos 4.0: " + age56_r4);
            System.out.println("Número de votos 5.0: " + age56_r5);
            System.out.println("=============================");

        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
