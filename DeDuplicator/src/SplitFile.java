import java.io.*;
import java.util.ArrayList;

public class SplitFile {

    public  ArrayList<String> hashCodes = new ArrayList<>();

    public void splitFile(File f,int sizeOfFiles,String lockerPath){

        // bytes as basic unit 1MB = 2^10 kb = 2^20 b
        // basic unit for sizeOfFiles is kb
        sizeOfFiles *= 1024;

        byte[] buffer = new byte[sizeOfFiles];

        try (FileInputStream fis = new FileInputStream(f);
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            int bytesAmount = 0;
            while ((bytesAmount = bis.read(buffer)) > 0) {

                // filename : filePartName
                //String filePartName = String.format("%s %03d", FileName, counter++);
                String filePartName = Tools.md5HashCode32(buffer);
                String directory = lockerPath + "/" + filePartName;
                File newFile =null;
                if(!Tools.checkExist(directory)){
                    newFile = new File(directory);
                }
                hashCodes.add(filePartName);

                if(newFile!=null){
                    FileOutputStream out = new FileOutputStream(newFile);
                    out.write(buffer, 0, bytesAmount);
                }
            }
        }catch(Exception e){

        }
    }
}