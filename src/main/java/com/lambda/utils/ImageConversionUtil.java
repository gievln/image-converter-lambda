package com.lambda.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageConversionUtil {
    public static File createThumbnail(File inputImgFile, int width, int height){
        File outputFile=null;
        try {
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            img.createGraphics().drawImage(ImageIO.read(inputImgFile).getScaledInstance(width, height, Image.SCALE_SMOOTH),0,0,null);
            outputFile=new File(inputImgFile.getParentFile()+File.separator+"thumnail_"+inputImgFile.getName());
            ImageIO.write(img, "jpg", outputFile);
            return outputFile;
        } catch (IOException e) {
            System.out.println("Exception while generating thumbnail "+e.getMessage());
            return null;
        }
    }
}
