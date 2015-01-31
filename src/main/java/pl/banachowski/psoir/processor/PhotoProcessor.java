package pl.banachowski.psoir.processor;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;

public class PhotoProcessor {

    public static void  generateImage(InputStream inputImageStream,OutputStream outputStream, int scalabity){
        BufferedImage buffImg = null;
        try{
            buffImg = ImageIO.read(inputImageStream);
            buffImg = Scalr.resize(buffImg, Scalr.Method.SPEED, scalabity);
            ImageIO.write(buffImg, "jpeg", outputStream);

        }catch (Exception e){
        	e.printStackTrace();
            System.out.println("Exception in processing image : " + e);
        }finally {
            buffImg = null;

        }
    }
}