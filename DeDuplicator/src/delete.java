import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;

public class delete {

    public void deleteFile(String sPath) {
        File file = new File(sPath);
        if (file.isFile() && file.exists()) {
            file.delete();
        }
    }

    public String Delete(HashMap<String,ArrayList<String>> file, HashMap<String, Integer> count, String filename){
        if (file.containsKey(filename)){
//            ArrayList<String> hash_file = new ArrayList<String>();
            ArrayList<String> hash_file = file.get(filename);
            for (String hash_file_name: hash_file){
                int count_number = count.get(hash_file_name);
                if (count_number - 1 == 0){
                    deleteFile(hash_file_name);
                    count.remove(hash_file_name);
                }
                else{
                    count.put(hash_file_name, count_number - 1);
                }
            }
            file.remove(filename);
            return "file is successfully deleted";
        }
        else{
            return "file is not in locker";
        }
    }
}
