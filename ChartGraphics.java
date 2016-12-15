import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

public class ChartGraphics {
  private BufferedImage image;

  private void createImage(String fileLocation) {
    try {
      FileOutputStream fos = new FileOutputStream(fileLocation);
      BufferedOutputStream bos = new BufferedOutputStream(fos);
      JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fos);
      encoder.encode(image);
      bos.close();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void graphicsGeneration(String word, String details, String source, String imgurl) //word