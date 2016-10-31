package cn.springmvc;

import java.io.File;

import cn.springmvc.util.PropertiesUtil;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;

public class TestTess4j {
	
	public static void main(String[] args) {
	 
		
		//System.setProperty("jna.library.path", "32".equals(System.getProperty("sun.arch.data.model")) ? "lib/win32-x86" : "lib/win32-x86-64");

		PropertiesUtil pu = new PropertiesUtil();
		String hb = pu.getProperty("user.dir");
		//System.getProperty("user.dir")
		File root = new File(hb);  
        ITesseract instance = new Tesseract();  
        //File tessDataFolder = LoadLibs.extractTessResources("tessdata"); // Maven build bundles English data
        //instance.setDatapath(tessDataFolder.getParent());
  
        try {  
            File[] files = root.listFiles();  
            for (File file : files) {
                String result = instance.doOCR(file);
                String fileName = file.toString().substring(file.toString().lastIndexOf("\\")+1);  
                System.out.println("图片名：" + fileName +" 识别结果："+result);  
            }  
        } catch (TesseractException e) {  
            System.err.println(e.getMessage());  
        }
		
		
	}

}
