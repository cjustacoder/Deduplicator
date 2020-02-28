import java.nio.file.InvalidPathException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;

public class dedup {
    private static String pwd = System.getProperty("user.dir")+"/";

    public static void main(String[] args) throws Exception {

        boolean DEBUG = false;

        if(!DEBUG) {
            // Main function starts here
            // Initialize objects
            CmdLine info = new CmdLine();
            SplitFile sp = new SplitFile();
            MataData mataData = new MataData();
            SaveFolder sf = new SaveFolder();
            ImagDedup id = new ImagDedup();

            HashMap<String, ArrayList<String>> mata = new HashMap<>();
            HashMap<String, Integer> b_cnt = new HashMap<>();
            HashMap<String, HashMap<String, String>> dir = new HashMap<>();


            if(info.validateInput(args)) {

                MainFrame gui = new MainFrame("DeDuplicator");
                gui.write2progress("Checking if Locker existed...");

                if (info.readIn(args)) {
                    gui.write2progress("locker find");
                } else {
                    gui.write2progress("Appointed locker not found");
                }

                // split to get lockername
                String[] tempString = info.lockerPath.split("/");
                String lockername = tempString[tempString.length - 1];
                String target = pwd + info.fileName;
                DecimalFormat df = new DecimalFormat("#.##"); // define a decimal print format


                if (!info.isNewPath) {
                    // Not a new locker;
                    // Load matadata & block info
                    mata = mataData.readmataf(info.lockerPath);
                    b_cnt = mataData.readcntf(info.lockerPath);
                    dir = mataData.read_dir(info.lockerPath);
                    gui.write2status(mataData.print_mata_sta(lockername));
                    gui.write2status("Locker: " + lockername);
                    gui.write2status("lockersize: " +
                            df.format(Tools.curLockersize(info.lockerPath)) + "MB");
                    gui.write2status(mataData.print_static(lockername));

                } else {
                    System.out.println("A new locker!");
                }

                // Add targetFile into locker
                double lockersize = Tools.curLockersize(info.lockerPath);
                double filesize = 0;

                // Add files or non-bmp pictures
                if ((info.opType.compareTo("-addFile") == 0) || ((info.opType.compareTo("-addImage") == 0) && (info.fileName.indexOf("bmp") == -1))){                    // Target file not found at pwd
                    if (!Tools.checkExist(info.fileName)) {
                        Runtime runtime = Runtime.getRuntime();
                        runtime.exec("rm -rf " + lockername);
                        gui.write2progress("[Error] Target file not found at: " + pwd);
                    } else {
                        if (sf.isFolder(target)) {
                            try {
                                gui.write2progress("saving folder ...");
                                long start = System.currentTimeMillis();
                                sf.saveFolder(target, args, mataData);
                                double end = ((double) System.currentTimeMillis() - (double) start) / 1000;
                                System.out.println("saving folder time: "+end);
                                gui.write2progress("Folder saved successfully!");
                                gui.delStatus();
                                gui.write2status(mataData.print_mata_sta(lockername));
                                gui.write2status("Locker: " + lockername);
                                gui.write2status("lockersize: " +
                                        df.format(Tools.curLockersize(info.lockerPath)) + "MB");
                                gui.write2status(mataData.print_static(lockername));
                            } catch (InvalidPathException | NullPointerException ex) {
                                System.out.println("[Error] Target file " + info.fileName + " not found at current path: " + pwd);
                                gui.write2progress("[Error] Target file " + info.fileName + " not found at current path");
                            }
                        } else {
                            try {
                                // File not saved in appointed locker
                                if (!mata.containsKey(info.fileName)) {
                                    File file = new File(target);
                                    double inputFileSize = file.length() / 1e6;
                                    filesize = inputFileSize;
                                    System.out.println("Target file size: " + inputFileSize + "MB");
                                    gui.write2progress(info.fileName + " size: " + inputFileSize + "MB");

                                    gui.write2progress("Splitting file into blocks...");
                                    sp.splitFile(file, 128, info.lockerPath);
                                    mataData.store(info.fileName, sp.hashCodes);
                                    gui.write2progress(info.fileName + " has been split into " + sp.hashCodes.size() + " blocks.");
                                    mataData.write2f(info.lockerPath);
//                                    System.out.println("File saved!");
                                    gui.write2progress("File" + info.fileName + " saved!");
                                    // refresh status testArea
                                    gui.delStatus();
                                    gui.write2status(mataData.print_mata_sta(lockername));
                                    gui.write2status("Locker: " + lockername);
                                    gui.write2status("lockersize: " +
                                            df.format(Tools.curLockersize(info.lockerPath)) + "MB");
                                    gui.write2status(mataData.print_static(lockername));
                                }

                                // File with same name already saved
                                else {
                                    System.out.println("[Error] A file with same name has already existed in the locker");
                                    gui.write2progress("[Error] A file with same name has already existed in the locker");
                                }
                            } catch (NullPointerException e) {
                                System.out.println("[Error] Target folder " + info.fileName + " not found at current path: " + pwd);
                                gui.write2progress("[Error] Target folder " + info.fileName + " not found at current path.");
                                //e.printStackTrace();
                            }
                        }


                        System.out.println("Current locker size: " + df.format(Tools.curLockersize(info.lockerPath)) + "MB");

                        gui.write2progress("File size after deduplication: " + df.format(Tools.curLockersize(info.lockerPath) - lockersize) + "MB");
                        gui.write2progress("Deduplication rate: " + df.format(100 - 100 * (Tools.curLockersize(info.lockerPath) - lockersize) / filesize) + "%");
                    }
                }

                // Retrieve targetFile
                else if (info.opType.compareTo("-retrieveFile") == 0) {
                    try {
                        if (mataData.read_dir(info.lockerPath).containsKey(info.fileName)) {
                            gui.write2progress(info.fileName + " find in " + info.lockerPath +
                                    " retrieving...");
                            long start = System.currentTimeMillis();
                            sf.retrieveFolder(dir, info.fileName, info.lockerPath, mataData);
                            double end = ((double) System.currentTimeMillis() - (double) start) / 1000;
                        } else {
                            try {
                                gui.write2progress(info.fileName + " retrieving...");
                                ExtractFile.Extract(info.fileName, info.lockerPath, mata);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (FileNotFoundException | NullPointerException ee) {
                        try {
                            gui.write2progress(info.fileName + " retrieving...");
                            ExtractFile.Extract(info.fileName, info.lockerPath, mata);
                        } catch (IOException e) {
                            //e.printStackTrace();
                        }
                        gui.write2progress(info.fileName + " has retrieved from " + lockername);
                    }
                    gui.delStatus();
                    gui.write2status(mataData.print_mata_sta(lockername));
                    gui.write2status("Locker: " + lockername);
                    gui.write2status("lockersize: " +
                            df.format(Tools.curLockersize(info.lockerPath)) + "MB");
                    gui.write2status(mataData.print_static(lockername));

                }

                // Delete targetFile
                else if (info.opType.compareTo("-deleteFile") == 0) {
                    try {
                        // File not saved in appointed locker
                        if (mata.containsKey(info.fileName)) {
                            mataData.delete(info.fileName, info.lockerPath);
                            mataData.write2f(info.lockerPath);
                            System.out.println("File "+info.fileName+" deleted!");
                            gui.write2progress("File "+info.fileName+" deleted!");
                            gui.delStatus();
                            if(mata.size()==0){
                                Runtime runtime = Runtime.getRuntime();
                                runtime.exec("rm -rf "+lockername);
                                gui.write2status("Locker deleted automatically since there's no file in locker anymore");
                            }else {
                                gui.write2status(mataData.print_mata_sta(lockername));
                                gui.write2status("Locker: " + lockername);
                                gui.write2status("lockersize: " +
                                        df.format(Tools.curLockersize(info.lockerPath)) + "MB");
                                gui.write2status(mataData.print_static(lockername));
                            }
                        }

                        // File with same name already saved
                        else {
                            System.out.println("[Error] This file does not exist in the locker");
                            gui.write2progress("[Error] This file does not exist in the locker");
                        }
                    } catch (NullPointerException ee) {
                        ee.printStackTrace();
                    }
                }

                // Substring match
                else if (info.opType.compareTo("-findSubString") == 0) {

                    ArrayList<String> subFiles = new ArrayList<>();
                    System.out.println("\nCurrent files saved in the locker with substring '" + info.fileName + "':");
                    gui.write2searchRes("Current files saved in the locker with substring '"
                            + info.fileName + "':");
                    long start = System.currentTimeMillis();
                    for (String slice : b_cnt.keySet()) {
                        if (slice.compareTo("-1") != 0) {
                            String curPath = info.lockerPath + "/" + slice;
                            try {
                                if (Tools.findStringInFile(curPath, info.fileName, info.charset)) {
                                    subFiles.add(slice);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    for (String file : mata.keySet()) {
                        ArrayList<String> tmp = mata.get(file);
                        for (String md5 : subFiles) {
                            if (md5.compareTo("-1") == 0) {
                                break;
                            }
                            if (tmp.indexOf(md5) != -1) {
                                System.out.println(file);
                                gui.write2searchRes(file);
                                break;
                            }
                        }
                    }
                    double end = ((double) System.currentTimeMillis() - (double) start) / 1000;
                    System.out.println("\n------------------------------------------------------");
                    System.out.println("Search finished in " + end + "s");
                    gui.write2searchRes("\n------------------------------------------------------");
                    gui.write2searchRes("Search finished in " + end + "s");
                }

                //Add bmp images
                else if (info.opType.compareTo("-addImage") == 0) {
                    if (!Tools.checkValid(target)) {
                        System.out.println("[Error] Target file " + info.fileName + " not found at current path: " + pwd);
                    }
                    try {
                        // File not saved in appointed locker
                        if (!mata.containsKey(info.fileName)) {

                            File file = new File(target);

                            double inputFileSize = file.length() / 1e6;
                            System.out.println("Target file size: " + inputFileSize + "MB");

                            gui.write2progress("Splitting image into blocks...");
                            id.splitImage(file, info.lockerPath);
                            mataData.store(info.fileName, id.hashCodes);
                            mataData.write2f(info.lockerPath);
//                            System.out.println("File saved!");
                        }

                        // File with same name already saved
                        else {
                            System.out.println("[Error] A file with same name has already existed in the locker");
                            gui.write2progress(info.fileName + " has already in " + lockername);
                        }
                    } catch (Exception e) {
                        System.out.println("[Error] Target folder " + info.fileName + " not found at current path: " + pwd);
                        //e.printStackTrace();
                    }

                } else if (info.opType.compareTo("-retrieveImage") == 0) {
                    boolean flagRetrive = false;
//                gui.write2progress("Retrieving "+info.fileName+" ...");
                    try {

                        try {
                            id.ByteToFile(info.fileName, info.lockerPath, mata);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    } catch (NullPointerException ee) {
                        try {
                            ExtractFile.Extract(info.fileName, info.lockerPath, mata);
                        } catch (IOException e) {
                            //e.printStackTrace();
                        }
                    }
                    if (flagRetrive) {
                        gui.write2progress(info.fileName + " has retrieved successfully");
                    }
                }

            }
            // Unsupported operations
            // log print hints
            else{
                System.out.println("[Error] Unsupported operation type. Current operations examples:");
                System.out.println("    -addFile [filename] -locker [lockername]");
                System.out.println("    -retrieveFile [filename] -locker [lockername]");
                System.out.println("    -findSubString [substring] -locker [lockername]");
                System.out.println("    -addImage [filename] -locker [lockername]");
                System.out.println("    -retrieveImage [filename] -locker [lockername]");
            }


        }

        else{
            // Test cases start here
            System.out.println("===================Running Test Cases===================");
            String[] arg = {"-addFile","test.txt","-locker","testLocker"};
            Test.test(arg);

        }
    }
}
