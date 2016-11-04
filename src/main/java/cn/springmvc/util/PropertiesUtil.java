package cn.springmvc.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

	private static  Properties prop = new Properties();
	private final static String file = PropertiesUtil.class.getClassLoader().getResource("filter.properties").getPath();
	
	static{
		try {
		prop.load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
		e.printStackTrace();
		} catch (IOException e) {
		e.printStackTrace();
		}
	}

	public static String getProperty(String key){
	return prop.getProperty(key);
	}

	public static void setProper(String key,String value){
	/**
	* 将文件加载到内存中，在内存中修改key对应的value值，再将文件保存
	*/
	try {
	prop.setProperty(key, value);
	FileOutputStream fos = new FileOutputStream(file);
	prop.store(fos, null);
	fos.close();
	} catch (FileNotFoundException e) {
	e.printStackTrace();
	} catch (IOException e) {
	e.printStackTrace();
	}
}

}