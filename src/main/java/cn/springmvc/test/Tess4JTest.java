package cn.springmvc.test;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;


public class Tess4JTest {
		//static Log logger = LogFactory.getLog(Tess4JTest.class);

		public static void main(String[] args) throws FileNotFoundException, TesseractException {
		     
			File imageFile = new File("E:/workspace/webmagic/src/main/java/cn/springmvc/test/eurotext.png");  
		   // InputStream in = new FileInputStream(imageFile);
			Tesseract instance = new Tesseract(); // JNA Interface Mapping
			instance.setTessVariable("tessedit_char_whitelist", "0123456789abcdefghijklmnopqrstuvwxyz");
			File tessDataFolder = LoadLibs.extractTessResources("tessdata"); // Maven build only; only English data bundled
			instance.setDatapath(tessDataFolder.getAbsolutePath());
			String result = instance.doOCR(imageFile);
			System.out.println(result);
		}
		
		 public static String OcrImage(InputStream instream){
		        String result=null;
		        try {
		        BufferedImage read = ImageIO.read(instream);
		   		//read = removeInterference(read);
		   		//read = grayImage(read);
		   		//read = erzhihuaImage(read);
		   		Tesseract instance = new Tesseract(); // JNA Interface Mapping
		   		instance.setTessVariable("tessedit_char_whitelist", "0123456789abcdefghijklmnopqrstuvwxyz");
		   		File tessDataFolder = LoadLibs.extractTessResources("tessdata"); // Maven build only; only English data bundled
		   		instance.setDatapath(tessDataFolder.getAbsolutePath());
		   		result = instance.doOCR(read);
		   		result = StringUtils.replace(StringUtils.trim(result), " ", "");
		   		} catch (Exception e) {
		   		e.printStackTrace();
		   	  }
		        return result;
		       }
		

}
