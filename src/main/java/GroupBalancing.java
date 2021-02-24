import es.upm.etsisi.cf4j.data.DataModel;
import es.upm.etsisi.cf4j.data.User;

public class GroupBalancing {

    public GroupBalancing(){
    }

    public static void main (String[] args){
        try{
            DataModel datamodel = DataModel.load("ml1M");

            int age1=0, age18=0, age25=0, age35=0, age45=0, age50=0, age56=0;

            for (User user : datamodel.getUsers()){
                if(user.getDataBank().getInt("age") == 1)
                    age1++;
                else if(user.getDataBank().getInt("age") == 18)
                    age18++;
                else if(user.getDataBank().getInt("age") == 25)
                    age25++;
                else if(user.getDataBank().getInt("age") == 35)
                    age35++;
                else if(user.getDataBank().getInt("age") == 45)
                    age45++;
                else if(user.getDataBank().getInt("age") == 50)
                    age50++;
                else if(user.getDataBank().getInt("age") == 56)
                    age56++;
            }

            System.out.println("El número de usuarios con edad 1 es: " + age1);
            System.out.println("El número de usuarios con edad 18 es: " + age18);
            System.out.println("El número de usuarios con edad 25 es: " + age25);
            System.out.println("El número de usuarios con edad 35 es: " + age35);
            System.out.println("El número de usuarios con edad 45 es: " + age45);
            System.out.println("El número de usuarios con edad 50 es: " + age50);
            System.out.println("El número de usuarios con edad 56 es: " + age56);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
