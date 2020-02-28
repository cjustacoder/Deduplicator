import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CmdLine {

    public String opType;

    public String fileName;
    public String lockerPath;
    public int charset;
    public static boolean isNewPath = false;

    public boolean readIn(String[] input) {


        opType = input[0];
        fileName = input[1];
        boolean flag = false;

        if (input[2].compareTo("-locker") == 0) {
            lockerPath = System.getProperty("user.dir") + "/" + input[3];
            flag = manageFunc();
        }
        else {
            System.out.println("[Error] Unsupported type in args[2]");
        }

        if (opType.compareTo("-findSubString") == 0) {
            charset = Integer.parseInt(input[4]);
        }

        return flag;
    }

    public boolean validateInput(String[] input){
        List<String> supp = Arrays.asList("-addFile","-retrieveFile","-addImage","-retrieveImage","-deleteFile","-findSubString");
        try {
            // Unsupported operation type
            if(supp.indexOf(input[0])== -1){
                return false;
            }

            // Third one is not "-locker"
            if(input[2].compareTo("-locker")!=0){
                return false;
            }


            if (input.length != 4) {
                // all operation arguments are size 4 except find sub string
                if (input[0].compareTo("-findSubString") != 0) {
                    return false;
                }
                else{
                    // invalid fifth input for substring matching operation
                    List<String> fifth = Arrays.asList("0","1","2");
                    if(fifth.indexOf(input[4])==-1){
                        return false;
                    }
                }
            }
        }catch(Exception e){
            // invalid length for input
            return false;
        }
        return true;
    }

    private boolean manageFunc(){
        boolean flag = false;
        if (opType.compareTo("-addFile")==0) {
            if(!Tools.checkExist(lockerPath)){
                Tools.makeExist(lockerPath);
                isNewPath = true;
            }
            flag = true;
        }
        else if (opType.compareTo("-retrieveFile")==0) {
            if(!Tools.checkExist(lockerPath)){
                System.out.println("[Error] Appointed locker not found");
            }
            flag = true;
        }
        else if (opType.compareTo("-findSubString")==0){
            if(!Tools.checkExist(lockerPath)){
                System.out.println("[Error] Appointed locker not found");
            }
            flag = true;
        }

        else if (opType.compareTo("-addImage") == 0){
            if(!Tools.checkExist(lockerPath)){
                Tools.makeExist(lockerPath);
                isNewPath = true;
            }
            flag = true;
        }

        else if (opType.compareTo("-retrieveImage")==0) {
            if(!Tools.checkExist(lockerPath)){
                System.out.println("[Error] Appointed locker not found");
            }
            flag = true;
        }

        else if (opType.compareTo("-deleteFile") == 0) {
            if(!Tools.checkExist(lockerPath)){
                System.out.println("[Error] Appointed locker not found");
            }
            flag = true;
        }

        else {
            System.out.println("[Error] Operation not supported");
        }
        return flag;
    }
}