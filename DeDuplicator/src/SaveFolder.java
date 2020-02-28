import java.io.File;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.*;

public class SaveFolder {
    private HashMap<String, File> files = new HashMap<>();
    private HashMap<String, String> folderList = new HashMap<>();
    private static String pwd = System.getProperty("user.dir")+"/";

    public void saveFolder(String path, String[] argsin, MataData mataData) {
        final File folder = new File(path);
        HashMap<String, File> allFile = fileList(folder, folder.getName());

        CmdLine infom = new CmdLine();
        infom.readIn(argsin);

        mataData.store_dir(folder.getName(), folderList, infom.lockerPath);

        Iterator it = allFile.entrySet().iterator();
        while (it.hasNext()) {
            CmdLine info = new CmdLine();
            Map.Entry pair = (Map.Entry) it.next();
            String[] args = {argsin[0], pair.getKey().toString(), argsin[2], argsin[3]};
            info.readIn(args);
            String filepath = pair.getValue().toString();

            HashMap<String, ArrayList<String>> mata;
            HashMap<String,Integer> b_cnt;


            if (!info.isNewPath) {
                // Not a new locker;
                // Load matadata & block info
                mata = mataData.readmataf(info.lockerPath);
                b_cnt = mataData.readcntf(info.lockerPath);
                mataData.read_dir(info.lockerPath);
            }
            else{
                mata = new HashMap<>();
                b_cnt = new HashMap<>();
            }


            try {
                // File not saved in appointed locker
                if (!mata.containsKey(info.fileName)) {
                    File file = new File(filepath);
                    if(!file.isDirectory()) {
                        SplitFile sp = new SplitFile();
                        sp.splitFile(file, 128, info.lockerPath);
                        mataData.store(info.fileName, sp.hashCodes);
                    }
                    else{
                        ArrayList<String> tmp = new ArrayList<>(Arrays.asList("-1"));
                        mataData.store(info.fileName,tmp);
                    }
                    mataData.write2f(info.lockerPath);
//                    System.out.println("File saved!");
                }

                // File with same name already saved
                else {
                    System.out.println("[Error] A file with same name has already existed in the locker");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            it.remove();
        }
    }


    public void retrieveFolder(HashMap<String, HashMap<String, String>> folderinfo, String folderName, String locker, MataData mataData) throws IOException {
        HashMap<String, String> filelist = folderinfo.get(folderName);
        Iterator it = filelist.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            ExtractFile.ExtractFD(pair.getValue().toString(), pair.getKey().toString(), locker);
        }
        it.remove();
    }


    private HashMap<String, File> fileList(final File folder, String folderName) {
        if (folder.listFiles().length == 0) {
            String oriPath = folder.toString();
            String absPath = oriPath.replace(pwd, "/");
            folderList.put(absPath, folder.getName());
            files.put(folderName, folder);
        }
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                fileList(fileEntry, fileEntry.getName());
            } else {
                String oriPath = fileEntry.getAbsolutePath();
                String absPath = oriPath.replace(pwd, "/");
                folderList.put(absPath, fileEntry.getName());
                files.put(fileEntry.getName(), fileEntry);
            }
        }
        return files;
    }


    public boolean isFolder(String path) {
        try {
            File file = new File(path);
            boolean isValid = file.exists();
            boolean isDirectory = file.isDirectory();
            boolean isFile = file.isFile();
            return isDirectory && isValid && !isFile;
        } catch (InvalidPathException | NullPointerException ex) {
            return false;
        }
    }
}
