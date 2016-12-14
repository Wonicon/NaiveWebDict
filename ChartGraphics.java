package image;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class ChartGraphics {

 BufferedImage image;

 void createImage(String fileLocation) {
  try {
   FileOutputStream fos = new FileOutputStream(fileLocation);
   BufferedOutputStream bos = new BufferedOutputStream(fos);
   JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(bos);
   encoder.encode(image);
   bos.close();
  } catch (Exception e) {
   e.printStackTrace();
  }
 }

 public void graphicsGeneration(String word, String details,String source, String imgurl) //word是单词，details是单词解释,source是来源，imgurl是最后保存地点
 {

  int imageWidth = 500;// 图片的宽度

  int imageHeight = 400;// 图片的高度

  image = new BufferedImage(imageWidth, imageHeight,BufferedImage.TYPE_INT_RGB);//创建一个图
  Graphics graphics = image.getGraphics();//将图交付给GRAPHIC类进行绘画
  graphics.setColor(Color.white);//画底色（白色
  graphics.fillRect(0, 0, imageWidth, imageHeight);
  graphics.setColor(Color.black);//切换颜色写字体
  graphics.setFont(new Font("宋体",Font.BOLD,20));//输入字体
  graphics.drawString("单词 : " + word, 50, 75);
  graphics.drawString("解释 : " + details, 50, 150);
  graphics.drawString("赠送者 : " + source, 50, 225);

  BufferedImage bimg = null;
  try {
   bimg = javax.imageio.ImageIO.read(new java.io.File(imgurl));//生成一个文件，文件地址为imgurl
  } catch (Exception e) {
  }

  if (bimg != null)//如果生成成功
   graphics.drawImage(bimg, 230, 0, null);//将刚刚的图像画进去
  graphics.dispose();//关闭GRAPHICS类，回收资源
  createImage(imgurl);
 
 
  //这边是如果要加图片等等东西的话会用到的代码
 }
 public static void main(String[] args) {
  ChartGraphics cg = new ChartGraphics();
  try {
   cg.graphicsGeneration("ewew", "1", "12", "D://2.jpg");
  } catch (Exception e) {
   e.printStackTrace();
  }
 }
}
