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

 public void graphicsGeneration(String word, String details,String source, String imgurl) //word�ǵ��ʣ�details�ǵ��ʽ���,source����Դ��imgurl����󱣴�ص�
 {

  int imageWidth = 500;// ͼƬ�Ŀ��

  int imageHeight = 400;// ͼƬ�ĸ߶�

  image = new BufferedImage(imageWidth, imageHeight,BufferedImage.TYPE_INT_RGB);//����һ��ͼ
  Graphics graphics = image.getGraphics();//��ͼ������GRAPHIC����л滭
  graphics.setColor(Color.white);//����ɫ����ɫ
  graphics.fillRect(0, 0, imageWidth, imageHeight);
  graphics.setColor(Color.black);//�л���ɫд����
  graphics.setFont(new Font("����",Font.BOLD,20));//��������
  graphics.drawString("���� : " + word, 50, 75);
  graphics.drawString("���� : " + details, 50, 150);
  graphics.drawString("������ : " + source, 50, 225);

  BufferedImage bimg = null;
  try {
   bimg = javax.imageio.ImageIO.read(new java.io.File(imgurl));//����һ���ļ����ļ���ַΪimgurl
  } catch (Exception e) {
  }

  if (bimg != null)//������ɳɹ�
   graphics.drawImage(bimg, 230, 0, null);//���ոյ�ͼ�񻭽�ȥ
  graphics.dispose();//�ر�GRAPHICS�࣬������Դ
  createImage(imgurl);
 
 
  //��������Ҫ��ͼƬ�ȵȶ����Ļ����õ��Ĵ���
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
