package pl.banachowski.psoir.processor;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;

/**
 * Created by IntelliJ IDEA.
 * Date: 3/19/13
 * Time: 12:32 PM
 * To change this template use File | Settings | File Templates.
 */
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