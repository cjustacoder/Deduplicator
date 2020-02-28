import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;

public class ImagDedup {
    public ArrayList<String> hashCodes = new ArrayList<>();

    public void splitImage(File file, String lockerPath) throws Exception {
//        String originalImg = "C:\\Users\\zhoub\\OneDrive\\Desktop\\New folder\\test.bmp";

        //Read image
//        File file = new File(originalImg);
        FileInputStream fis = new FileInputStream(file);
        BufferedImage image = ImageIO.read(fis);

        //Split into 4*4(16) thumbnails
        int rows = 4;
        int cols = 4;
        int chunks = rows * cols;

        // Calculate the width and height of each thumbnail
        int chunkWidth = image.getWidth() / cols;
        int chunkHeight = image.getHeight() / rows;
        int WidthSide = image.getWidth() % chunkWidth;
        int HeightSide = image.getHeight() % chunkHeight;

        int mychunkWidth, mychunkHeight;

//        System.out.println(chunkHeight);
//        System.out.println(image.getHeight());
//        System.out.println(chunkWidth);
//        System.out.println(image.getWidth());

        int count = 0;
        BufferedImage imgs[] = new BufferedImage[chunks];
        for (int x = 0; x < rows; x++) {
            if(x == rows-1){
                mychunkHeight = chunkHeight + HeightSide;
            }
            else{
                mychunkHeight = chunkHeight;
            }
            for (int y = 0; y < cols; y++) {
                if(y == cols-1){
                    mychunkWidth = chunkWidth + WidthSide;
                }
                else {
                    mychunkWidth = chunkWidth;
                }
                //Set the size and type of the thumbnail
//                System.out.println("w "+ mychunkWidth+" h"+ mychunkHeight);
                imgs[count] = new BufferedImage(mychunkWidth, mychunkHeight, image.getType());

                //Write image content
                Graphics2D gr = imgs[count++].createGraphics();
                gr.drawImage(image, 0, 0, mychunkWidth, mychunkHeight,
                        chunkWidth * y, chunkHeight * x,
                        chunkWidth * y + mychunkWidth,
                        chunkHeight * x + mychunkHeight, null);
                gr.dispose();
            }
        }

        //Output cut images
        for (int i = 0; i < imgs.length; i++) {
//            String filePartName = Tools.md5HashCode32(imgs[i]);
//            ImageIO.write(imgs[i], "bmp", new File(lockerPath + "/" + filePartName + ".bmp"));
            fileToByte(imgs[i], lockerPath);
        }

        System.out.println("Cut complete!");
    }


    public void fileToByte(BufferedImage bi, String lockerPath) throws Exception {
        byte[] bytes;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bi, "bmp", baos);
            bytes = baos.toByteArray();
//            System.err.println("Byte length " + bytes.length);
            String filePartName = Tools.md5HashCode32(bytes);
            String directory = lockerPath + "/" + filePartName;
            hashCodes.add(filePartName);
            if(!Tools.checkExist(directory)){
                FileOutputStream fos = new FileOutputStream(directory);
                fos.write(bytes);
                fos.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            baos.close();
        }
    }



    public static void mergeImage(BufferedImage[] buffImages, String filename, String lockerPath) throws IOException {

        int rows = 4;
        int cols = 4;
        int chunks = rows * cols;

        int chunkWidth, chunkHeight;
        int WidthSide, HeightSide;
        int type;

//        //Read cut images
//        File[] imgFiles = new File[chunks];
//        for (int i = 0; i < chunks; i++) {
//            imgFiles[i] = new File("C:\\Users\\zhoub\\OneDrive\\Desktop\\New folder\\img_" + i + ".bmp");
//        }
//
//        //Crate BufferedImage
//        BufferedImage[] buffImages = new BufferedImage[chunks];
//        for (int i = 0; i < chunks; i++) {
//            buffImages[i] = ImageIO.read(imgFiles[i]);
//        }
        type = buffImages[0].getType();
        chunkWidth = buffImages[0].getWidth();
        chunkHeight = buffImages[0].getHeight();
        WidthSide = buffImages[chunks-1].getWidth() - chunkWidth;
        HeightSide = buffImages[chunks-1].getHeight() - chunkHeight;
//        System.out.println(WidthSide);
//        System.out.println(HeightSide);

        //Set the size and type of the stitched image
        BufferedImage finalImg = new BufferedImage(chunkWidth * cols + WidthSide, chunkHeight * rows + HeightSide, type);
//        System.out.println(finalImg.getWidth());
//        System.out.println(finalImg.getHeight());

        //Write image content
        int num = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                finalImg.createGraphics().drawImage(buffImages[num], chunkWidth * j, chunkHeight * i, null);
                num++;
            }
        }

        //Output the stitched image
        ImageIO.write(finalImg, "bmp", new File(filename));
        InputStream is=(InputStream) ImageIO.createImageInputStream(finalImg);

        System.out.println("Stitching complete!");
    }



    static void ByteToFile(String filename, String lockerPath, HashMap<String, ArrayList<String>> mata)throws Exception{
        if (mata.containsKey(filename)){
            BufferedImage[] buffImages = new BufferedImage[16];
            ArrayList<String> fileList = mata.get(filename);
            int i = 0;
            for(String fil: fileList){
                byte[] myByteArray = toByteArray(lockerPath + "/" + fil);
                ByteArrayInputStream bais = new ByteArrayInputStream(myByteArray);
                BufferedImage bi1;
                bi1 = ImageIO.read(bais);
//                try {
//                    File w2 = new File(fil);
//                    ImageIO.write(bi1, "bmp", w2);
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }finally{
//                    bais.close();
//                }
                buffImages[i++] = bi1;
                bais.close();
            }
            mergeImage(buffImages, filename, lockerPath);
        }
        else{
            System.out.println("File is not found in locker");
        }


    }


    public static byte[] toByteArray(String filename)throws IOException{

        File f = new File(filename);
        if(!f.exists()){
            throw new FileNotFoundException(filename);
        }

        FileChannel channel = null;
        FileInputStream fs = null;
        try{
            fs = new FileInputStream(f);
            channel = fs.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int)channel.size());
            while((channel.read(byteBuffer)) > 0){
                // do nothing
//              System.out.println("reading");
            }
            return byteBuffer.array();
        }catch (IOException e) {
            e.printStackTrace();
            throw e;
        }finally{
            try{
                channel.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
            try{
                fs.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
