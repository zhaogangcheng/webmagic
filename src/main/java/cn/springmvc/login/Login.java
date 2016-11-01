package cn.springmvc.login;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;

import cn.springmvc.controller.DhController;
import cn.springmvc.util.HttpConnectionClient;
import cn.springmvc.vo.ExcelVo;

public class Login {
	
	  public static String img_path_url = "http://www.400gb.com/randcodeV2_login.php";
	  public static String image_save_path = "D:\\yzm\\vcode.png";
	
	public static void main(String strings[]) throws Exception   
    {  
		String imgcookie = downloadImage();
		
		String loginSession = getLoginSession(imgcookie);
		
		//现在获取 href="http://781.ceair.com/bookingmanage/booking_bookSearchInit.do
		String cookie781 = get781Cookie(loginSession);
		
		
		DhController dh = new DhController();
		List<ExcelVo> ls = dh.getDomMsg("SIN", "PIN", "2016-12-12", cookie781);
		String ss = "";
		
    }
	
	public static String getLoginSession(String imgcookie) throws Exception{
		
		HttpConnectionClient httpClient = new HttpConnectionClient();
		String url = "http://travel.ceair.com/log_f.do";
		String j_username = "hnkt";
		String j_password = "e10adc3949ba59abbe56e057f20f883e";
		String kaptcha = "1234";
		NameValuePair[] nvps = {new NameValuePair("j_username", j_username),new NameValuePair("j_password", j_password),new NameValuePair("kaptcha", kaptcha) };
		String loginCookie= httpClient.getContextByPostMethodLogin(url,nvps,imgcookie);
		System.out.println("loginCookie:"+loginCookie);
		
		return "";
	}
	
	
	 public  static String downloadImage() {  
		  String cookieResult= "";
		  HttpClient httpClient = new HttpClient();
		  //httpclient.getCookieStore().getCookies()
	        GetMethod getMethod = new GetMethod("http://travel.ceair.com/validateCode.vld?29");  
	            try {  
	                // 执行getMethod  
	            	Thread.sleep(500);
	                int statusCode = httpClient.executeMethod(getMethod);  
	                if (statusCode != HttpStatus.SC_OK) {  
	                    System.err.println("Method failed: "  
	                            + getMethod.getStatusLine());  
	                }  
	                // 读取内容  
	                Cookie[] cookies = httpClient.getState().getCookies();  
	                if(cookies!=null&&cookies.length==1){
	                	cookieResult = cookies[0].toExternalForm();
	                }else if(cookies!=null&&cookies.length==2){
	                	cookieResult = cookies[1].toExternalForm();
	                }else{
	                	cookieResult = "JSESSIONID=";
	                }
	               int m = cookieResult.indexOf("JSESSIONID=");
	               cookieResult = cookieResult.substring(m+11);
	                
	                System.out.println("cookieResult:"+cookieResult);
	            /*    for (int i = 0; i < cookies.length; i++) {  
	                    System.out.println(" - " + cookies[i].toExternalForm());  
	                }  
	                */
	                String picName = "D:\\yzm\\vcode.jpg";  
	                InputStream inputStream = getMethod.getResponseBodyAsStream();  
	                OutputStream outStream = new FileOutputStream(picName);  
	                IOUtils.copy(inputStream, outStream);  
	                outStream.close();  
	                System.out.println("OK!");   
	                
	                return cookieResult;
	            } catch (Exception e) {  
	                e.printStackTrace();  
	            } finally {  
	                // 释放连接  
	                getMethod.releaseConnection();  
	            }
	            return cookieResult;
	    }
	 
	 //781.ceair.com
	 public  static String get781Cookie(String loginsession) {  
		  String cookieResult= "";
		  HttpClient httpClient = new HttpClient();
		  //httpclient.getCookieStore().getCookies()
	        GetMethod getMethod = new GetMethod("http://781.ceair.com/bookingmanage/booking_bookSearchInit.do");  
	            try {  
	                // 执行getMethod  
	            	Thread.sleep(500);
	                int statusCode = httpClient.executeMethod(getMethod);  
	                if (statusCode != HttpStatus.SC_OK) {  
	                    System.err.println("Method failed: "  
	                            + getMethod.getStatusLine());  
	                }  
	                // 读取内容  
	                Cookie[] cookies = httpClient.getState().getCookies();  
	                if(cookies!=null&&cookies.length==1){
	                	cookieResult = cookies[0].toExternalForm();
	                }else if(cookies!=null&&cookies.length==2){
	                	cookieResult = cookies[1].toExternalForm();
	                }else if(cookies!=null&&cookies.length>2){
	                	cookieResult = cookies[3].toExternalForm();
	                }else{
	                	cookieResult = "JSESSIONID=";
	                }
	               int m = cookieResult.indexOf("JSESSIONID=");
	               cookieResult = cookieResult.substring(m+11);
	                
	                System.out.println("781cookieResult:"+cookieResult);
	              
	                
	                return cookieResult;
	            } catch (Exception e) {  
	                e.printStackTrace();  
	            } finally {  
	                // 释放连接  
	                getMethod.releaseConnection();  
	            }
	            return cookieResult;
	    }
	 
	 
	

}
