import java.io.*;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Tools {

    public static void makeExist(String filepath) {
        File file=new File(filepath);

        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static boolean checkExist(String filepath) {
        File file=new File(filepath);
        if (!file.exists()) {
            return false;
        }
        return true;
    }

    public static boolean checkValid(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                Paths.get(path);
            } catch (InvalidPathException | NullPointerException ex) {
                return false;
            }
            return true;
        }
        return true;
    }



    public static String md5HashCode32(String filePath) throws FileNotFoundException{
        FileInputStream fis = new FileInputStream(filePath);
        return md5HashCode32(fis);
    }


    public static String md5HashCode32(byte[] buffer){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(buffer,0,buffer.length);
            byte[] md5Bytes = md.digest();
            StringBuffer hexValue = new StringBuffer();
            for(int i =0;i<md5Bytes.length;i++){
                int val =((int)md5Bytes[i]) & 0xff;
                if(val<16){
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            return hexValue.toString();

        } catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            return "";
        }
    }

    public static String md5HashCode32(InputStream fis) {
        try {
            //Md5 converter could also change to SHA-1,SHA-256
            MessageDigest md = MessageDigest.getInstance("MD5");

            // slice the object file into blocks size 1M
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = fis.read(buffer, 0, 1024)) != -1) {
                md.update(buffer, 0, length);
            }
            fis.close();

            byte[] md5Bytes  = md.digest();
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }
            return hexValue.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    public static boolean findStringInFile(String path, String subString, Integer charset) throws IOException {

        String[] charsetNames = {"ASCII", "UTF-8", "UTF-16"};
        File file = new File(path);
        InputStreamReader read = new InputStreamReader(new FileInputStream(file), charsetNames[charset]);
        BufferedReader bufferedReader = new BufferedReader(read);
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            if (line.contains(subString)) {
                return true;
            }
        }
        return false;
    }

    public static double curLockersize(String lockerPath){
        // Calculate current locker size
        // MB in unit
        File curPath = new File(lockerPath);
        File[] files = curPath.listFiles();
        double totalSize = 0;
        for(File i: files){
            totalSize += i.length()/1e6;
        }
        return totalSize;
    }
}

