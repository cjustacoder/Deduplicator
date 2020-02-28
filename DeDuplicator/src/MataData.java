import java.util.*;
import java.io.*;

public class MataData {
//    matadata  [filename: [blockmd5_1, _2, _3...]]
    private HashMap<String, ArrayList<String>> matadata = new HashMap<>();
//    blockcount [blockmd5: # of this block]
    private HashMap<String, Integer> block_cnt= new HashMap<>();
//    directory_path_record [dir_name: [filepath: filename]]
    private HashMap<String, HashMap<String, String>> dir_path_rec = new HashMap<>();

    /*  store a dir info into dir_path_rec
    input:  dir_name: String, the name of dir
            fandpath: HashMap<String, String>, [filepath: filename]
    */
    public void store_dir(String dir_name, HashMap<String, String> fandpath, String lockerpath) {
        HashMap<String, String> temp_path = new HashMap<>();
        for(String currentKey : fandpath.keySet()) {
            temp_path.put(currentKey,fandpath.get(currentKey));
        }
        dir_path_rec.put(dir_name, temp_path);
        // writing dir_path_rec to file
        String dirname = lockerpath + "/dir_rec";
        try{
            FileOutputStream dir_rec =
                new FileOutputStream(dirname);
            ObjectOutputStream dirStream = new ObjectOutputStream(dir_rec);
            dirStream.writeObject(dir_path_rec);
            dirStream.close();
            dir_rec.close();
//            System.out.println("Serialized dir_record is saved in " + dirname);
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
    /*
    * pretty printing for testing
    * */

    /*
    * regular operation for storing a file
    * input:    fname: String, filename
    *           fmd5: ArrayList<String> a list of md5(string) of this file
    */
    public void store(String fname, ArrayList<String> fmd5) {
        matadata.put(fname, fmd5);
        for (String md5 : fmd5) {
            if (block_cnt.containsKey(md5))
                block_cnt.put(md5, block_cnt.get(md5) + 1);
            else
                block_cnt.put(md5, 1);
        }
    }


    public void deleteFile(String sPath) {
        File file = new File(sPath);
        if (file.isFile() && file.exists()) {
            file.delete();
        }
    }

    public void delete(String filename, String locker_path) {
//        if (matadata.containsKey(filename)){
//            ArrayList<String> hash_file = new ArrayList<String>();
        ArrayList<String> hash_file = matadata.get(filename);
        for (String hash_file_name : hash_file) {
            int count_number = block_cnt.get(hash_file_name);
            if (count_number - 1 == 0) {
                deleteFile(locker_path + "/" + hash_file_name);
                block_cnt.remove(hash_file_name);
            } else {
                block_cnt.put(hash_file_name, count_number - 1);
            }
        }
        matadata.remove(filename);
//        if (matadata.isEmpty()) {
//            System.out.println("matadata empty");
//        }
//        if (block_cnt.isEmpty()) {
//            System.out.println("block_cnt empty");
//        }
    }

    public String print_mata() {
        System.out.println("==========matadata============");
        String output = "======matadata======\n";
        for (Map.Entry<String, ArrayList<String>> entry : matadata.entrySet()) {
            System.out.print("filename: "+ entry.getKey() + "\tcontent(in Md5 Hash):");
            output = output + "filename: "+ entry.getKey() + " content(in Md5 Hash):";
            ArrayList<String> con = matadata.get(entry.getKey());
            for (String md5: con) {
                output = output + md5;
                System.out.print(md5);
                output = output + ", ";
                System.out.print(", ");
            }
            System.out.println();
            output = output + "\n";
        }
        return output;
    }

    /*
    pretty printing for cnt_block for testing
    * */
    public String print_cnt() {
        System.out.println("==========block_count============");
        String output = "======block table======\n";
        for (Map.Entry<String, Integer> entry : block_cnt.entrySet()) {
            System.out.println("Block_Hash: " + entry.getKey() + ", number: " + entry.getValue());
            output = output + "Block_Hash: " + entry.getKey() + ", number: " + entry.getValue() + "\n";
        }
        return output;
    }

    /*
    * pretty print for matadata for testing
    * */
    public String print_mata_sta(String lockername) {
        String output = "======"+lockername+" matadata======\n";
        for (Map.Entry<String, ArrayList<String>> entry : matadata.entrySet()) {
            output = output + "filename: "+ entry.getKey() + " content(in Md5 Hash):";
            ArrayList<String> con = matadata.get(entry.getKey());
            for (String md5: con) {
                output = output + md5;
                output = output + ", ";
            }
            output = output + "\n";
        }
        output += "================================" + "\n";
        return output;
    }

    public String print_static(String lockername) {
        String output = "LockerName: " + lockername+"\n";
        output += "Number of Files: " + matadata.size() + "\n";
        output += "Number of Blocks(stored): " + block_cnt.size() + "\n";
        Integer sum = 0;
        for (Integer i : block_cnt.values()) {
            sum += i;
        }
        output += "Number of Blocks Deduplicated: " + (sum - block_cnt.size()) + "\n";
        return output;
    }

    /*
    pretty printing for cnt_block for testing
    * */

    /*
    * write the infomation table in to stream file, for nonvolatile and portable purpose
    * input: String, the lockerpath*/
    public void write2f(String lockerPath) {
        String mfname = lockerPath + "/mata";
        String bfname = lockerPath + "/b_cnt";
        String dirname = lockerPath + "/dir_rec";
        try {
            // writing mata to file
            FileOutputStream mata =
                    new FileOutputStream(mfname);
            ObjectOutputStream mataStream = new ObjectOutputStream(mata);
            mataStream.writeObject(matadata);
            mataStream.close();
            mata.close();
//            System.out.println("Serialized MataData is saved in " + mfname);
            // writing block_cnt to file
            FileOutputStream cnt_list =
                    new FileOutputStream(bfname);
            ObjectOutputStream cntStream = new ObjectOutputStream(cnt_list);
            cntStream.writeObject(block_cnt);
            cntStream.close();
            cnt_list.close();
//            System.out.println("Serialized BlockList is saved in " + bfname);
            // writing dir_path_rec to file
//            FileOutputStream dir_rec =
//                    new FileOutputStream(dirname);
//            ObjectOutputStream dirStream = new ObjectOutputStream(dir_rec);
//            dirStream.writeObject(dir_path_rec);
//            dirStream.close();
//            dir_rec.close();
//            System.out.println("Serialized dir_record is saved in " + dirname);
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /*
    * reading(recover) a matadata from a stream file
    * input: lockerPath: String, the path of locker
    * output: matadata, replace the matadata in this object*/
    public HashMap<String, ArrayList<String>> readmataf(String lockerPath) {
        String mataname = lockerPath + "/mata";
        HashMap<String, ArrayList<String>> map = null;
        try
        {
            FileInputStream fis = new FileInputStream(mataname);
            ObjectInputStream ois = new ObjectInputStream(fis);
            map = (HashMap) ois.readObject();
            ois.close();
            fis.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } catch(ClassNotFoundException c) {
            System.out.println("Class not found");
            c.printStackTrace();
        }
//----------------------pretty print for testing---------------
//        System.out.println("Deserialized HashMap..");
        // Display content using Iterator
        Set set = map.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
            Map.Entry mentry = (Map.Entry)iterator.next();
            System.out.print("key: "+ mentry.getKey() + " & Value: ");
            System.out.println(mentry.getValue());
        }
        matadata = map;
        return map;
    }

    /*
     * reading(recover) a block_cnt from a stream file
     * input: lockerPath: String, the path of locker
     * output: block_cnt, replace the block_cnt in this object*/
    public HashMap<String, Integer> readcntf(String lockerPath) {
        String cntfname = lockerPath + "/b_cnt";
        HashMap<String, Integer> map = null;
        try
        {
            FileInputStream fis = new FileInputStream(cntfname);
            ObjectInputStream ois = new ObjectInputStream(fis);
            map = (HashMap) ois.readObject();
            ois.close();
            fis.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } catch(ClassNotFoundException c) {
            System.out.println("Class not found");
            c.printStackTrace();
        }
//----------------------pretty print for Test---------------
//        System.out.println("Deserialized HashMap..");
        // Display content using Iterator
        Set set = map.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
            Map.Entry mentry = (Map.Entry)iterator.next();
            System.out.print("key: "+ mentry.getKey() + " & Value: ");
            System.out.println(mentry.getValue());
        }
        block_cnt = map;
        return map;
    }

    /*
     * reading(recover) a dir_path_rec from a stream file
     * input: lockerPath: String, the path of locker
     * output: dir_path_rec, replace the dir_path_rec in this object*/
    public HashMap<String, HashMap<String, String>> read_dir(String lockerPath) {
        String dirname = lockerPath + "/dir_rec";
        HashMap<String, HashMap<String, String>> map = null;
        try
        {
            FileInputStream fis = new FileInputStream(dirname);
            ObjectInputStream ois = new ObjectInputStream(fis);
            map = (HashMap) ois.readObject();
            ois.close();
            fis.close();
        } catch(IOException ioe) {
            System.out.println("[Warning] dir_rec not exist");
        } catch(ClassNotFoundException c) {
            System.out.println("Class not found");
            c.printStackTrace();
        }
//----------------------pretty print for testing---------------
//        System.out.println("Deserialized HashMap..");
//        // Display content using Iterator
//        Set set = map.entrySet();
//        Iterator iterator = set.iterator();
//        while(iterator.hasNext()) {
//            Map.Entry mentry = (Map.Entry)iterator.next();
//            System.out.print("key: "+ mentry.getKey() + " & Value: ");
//            System.out.println(mentry.getValue());
//        }
        if(!(map==null))
            dir_path_rec = map;
        return map;
    }

//    deletion
    

}
