import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class AGMFGenerateRatings {

    public static void main (String[] args){
        Map<String, String> id2user;

        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;

        try {
            for (int i = 0; i < 7; i++) {
                id2user = new HashMap();

                archivo = new File("AGMF/usersGroup" + (i + 1));
                fr = new FileReader(archivo);
                br = new BufferedReader(fr);

                String linea;
                while ((linea = br.readLine()) != null){
                    id2user.put(linea.replaceAll("[\n]+$", ""), linea.replaceAll("[\n]+$", ""));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
