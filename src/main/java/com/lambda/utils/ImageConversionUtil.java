package com.lambda.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImageConversionUtil {

    public static InputStream createThumbnail(InputStream inputImgStream, int width, int height) throws IOException {
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            img.createGraphics().drawImage(ImageIO.read(inputImgStream).getScaledInstance(
                    width, height, Image.SCALE_SMOOTH),0,0,null);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(img, "jpeg", os);
            return new ByteArrayInputStream(os.toByteArray());
    }
}
