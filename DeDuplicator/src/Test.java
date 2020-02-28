import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Test {
    public static void test(String[] args) throws Exception {

        String testFile = System.getProperty("user.dir")+"/test.txt";
        String testFile2 = System.getProperty("user.dir")+"/test1.txt";
        String fileName = "test1.txt";

        CmdLine info = new CmdLine();
        SplitFile sp = new SplitFile();
        MataData mataData = new MataData();
        SaveFolder saveFolder = new SaveFolder();
        ImagDedup id = new ImagDedup();

        System.out.println("****************Test Tools****************\n");

        try{
            String hash32 = Tools.md5HashCode32(testFile);
            String hash32_1 = Tools.md5HashCode32(testFile2);
            if (hash32.compareTo(hash32_1)==0){
                System.out.println("Same File!");
            }
            else{
                System.out.println("Different File!");
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }

        String test1 = "./forTest";
        String test2 = "./forTest/anotherFolder";
        String result1 = Tools.checkExist(test1)? "Yes" : "No";
        String result2 = Tools.checkExist(testFile)? "Yes" : "No";
        System.out.println("Is '"+test1+"' exists?: " + result1);
        System.out.println("Is '"+testFile+"' exists?: " + result2);

        Tools.makeExist(test1);
        Tools.makeExist(test2);

        System.out.println("****************Test CmdLine****************\n");
        info.readIn(args);
        System.out.println(info.lockerPath);
        System.out.println(info.fileName);


        System.out.println("****************Test SplitFile****************\n");
        sp.splitFile(new File(testFile),1024,info.lockerPath);
        System.out.println(sp.hashCodes);


        System.out.println("****************Test MataData****************\n");
        mataData.store(fileName,sp.hashCodes);
        mataData.print_mata();
        mataData.print_cnt();
        mataData.write2f(info.lockerPath);
        mataData.readcntf(info.lockerPath);
        mataData.readmataf(info.lockerPath);
      
        System.out.println("****************Test Deletion****************\n");
        mataData.delete(fileName, info.lockerPath);
        mataData.write2f(info.lockerPath);
        mataData.readcntf(info.lockerPath);
        mataData.readmataf(info.lockerPath);

        System.out.println("****************Test SaveFolder****************\n");
        String[] arg = {"-addFile","forTest","-locker","testLocker"};

        Runtime runtime = Runtime.getRuntime();
        try{
            Process copy = runtime.exec("cp "+testFile+" "+test1);

            saveFolder.saveFolder(test1,arg,mataData);

            Process process = runtime.exec("rm -rf "+test1);
            System.out.println("Source folder deleted");

            saveFolder.retrieveFolder(mataData.read_dir(info.lockerPath),"forTest",info.lockerPath,mataData);
            System.out.println("Source folder retreived");


        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("****************Test AddImage****************\n");
        id.splitImage(new File("./test.bmp"), info.lockerPath);
        System.out.println(id.hashCodes);

    }
}
