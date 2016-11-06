package cn.springmvc.login;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;

import cn.springmvc.ValidateCode;
import cn.springmvc.controller.DhController;
import cn.springmvc.vo.ExcelVo;

public class Login {
	
	
	public static void main(String strings[]) throws Exception   
    {  
		
		 HttpClient httpClient = new HttpClient();
		//获取图片下载 、同时获取图片的cookie
		String imgcookie = downloadImage(httpClient);
		
		//识别验证码
		String kaptcha = identifyImg(); 
		
		//利用图片的cookie去模拟登录
		String loginSession = getLoginSession(httpClient,imgcookie,kaptcha);
		
		
		//利用登录的cookie去做home的操作 http://travel.ceair.com/home_f.do
		String homeSession = getHomeSession(httpClient,loginSession);
		
		//现在获取 href="http://781.ceair.com/bookingmanage/booking_bookSearchInit.do
		String cookie781 = get781Cookie(httpClient,homeSession);
		
		if("error".equals(cookie781)){
			System.out.println("验证码错误");
			return;
		}
		
		
		DhController dh = new DhController();
		List<ExcelVo> ls = dh.getDomMsg("SIN", "PEK", "2016-12-12", cookie781);
		String ss = "";
		String sdsds= "";
		
    }
	
	  public static String identifyImg(){
              
            String result = "";
			try {
				result = ValidateCode.OcrImage(new FileInputStream(new File("D:\\zhanghan\\yzm\\yzm.jpg")));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
              System.out.println("验证码:"+result);
              return result;
 	}
	
	public static String getLoginSession( HttpClient httpClient, String imgcookie,String kaptcha) throws Exception{
		
		String url = "http://travel.ceair.com/log_f.do";
		String j_username = "hnkt";
		String j_password = "e10adc3949ba59abbe56e057f20f883e";
		NameValuePair[] nvps = {new NameValuePair("j_username", j_username),new NameValuePair("j_password", j_password),new NameValuePair("kaptcha", kaptcha) };
		//String loginCookie= httpClient.getContextByPostMethodLogin(url,nvps,imgcookie);
		// 设置编码
		httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"UTF-8"); 
		PostMethod post = null;
		try {
			// 设置提交地址
			URL u = new URL(url);
			httpClient.getHostConfiguration().setHost(u.getHost(),
					u.getPort() == -1 ? u.getDefaultPort() : u.getPort(),
					u.getProtocol());
			post = new PostMethod(url);
			// 提交数据
			post.addParameters(nvps);
			Header header =  new Header("Cookie",imgcookie);
			//post.getParams().setParameter("Cookie","JSESSIONID=6A49D7F1094C0E0FFC3C9FAC8056A474.t9; __COOKIE_SSO_KEY=SK_25927_3JYiZMtSx5Y0vuu9lokFgJyQTnzMUSMh; uid=112");
			post.getParams().setParameter(HttpMethodParams.USER_AGENT,"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36");
			//post.getParams().setParameter("X-Requested-With", "XMLHttpRequest");
			post.getParams().setParameter("Content-Type","application/x-www-form-urlencoded; charset=UTF-8"); 
			//post.getParams().setParameter("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36");
			post.getParams().setParameter("Accept","text/html, */*; q=0.01");
			post.getParams().setParameter("Accept-Language","zh-CN,zh;q=0.8");
			post.getParams().setParameter("Connection","keep-alive");
			post.getParams().setParameter("Content-Type","application/x-www-form-urlencoded");
			//post.getParams().setParameter("Origin","http://travel.ceair.com");
			post.getParams().setParameter("Referer","http://travel.ceair.com/log_f.do");
			post.addRequestHeader(header); 
			post.setRequestBody(nvps);
			
			int statusCode = httpClient.executeMethod(post);
			String resultBody = post.getResponseBodyAsString();
			String result ="";
			Cookie[] cookies = httpClient.getState().getCookies();
			if(cookies!=null&&cookies.length==1){
				result = cookies[0].toExternalForm();
             }else if(cookies!=null&&cookies.length==2){
            	 result = cookies[1].toExternalForm();
             }else{
            	 result = "JSESSIONID=";
             }
            int m = result.indexOf("JSESSIONID=");
            result = result.substring(m+11);
			
		return	result;
		} catch (Exception e) {
			throw e;
		} finally {
			if (post != null)
				post.releaseConnection();
		}
	}
	
	
public static String getHomeSession( HttpClient httpClient, String loginsession) throws Exception{
		
		  String url = "http://travel.ceair.com/home_f.do";
		  String cookieResult= "";
		  String host = "travel.ceair.com";
	      httpClient.getHostConfiguration().setHost(host, 80, "http"); 
		  //httpclient.getCookieStore().getCookies()
	        GetMethod getMethod = new GetMethod(url);  
	        getMethod.setRequestHeader("Referer", "http://travel.ceair.com/log_f.do");
	        getMethod.getParams().setParameter("Referer", "http://travel.ceair.com/log_f.do");
	        getMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36");
	        getMethod.getParams().setParameter("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36");
	        getMethod.setRequestHeader("Cookie", "JSESSIONID="+loginsession+";uid=112;");
	        getMethod.getParams().setParameter("Cookie", "JSESSIONID="+loginsession+";uid=112;");
	        //"Content-Type","application/x-www-form-urlencoded"
	        
	        try {  
	                // 执行getMethod  
	            	Thread.sleep(500);
	              int statusCode = httpClient.executeMethod(getMethod);  
	                
	              String result =   getMethod.getResponseBodyAsString();
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
	                	cookieResult = cookies[2].toExternalForm();
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
	
	
	 public  static String downloadImage( HttpClient httpClient) {  
		  String cookieResult= "";
		 
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
	                String picName = "D:\\zhanghan\\yzm\\yzm.jpg";  
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
	 public  static String get781Cookie(HttpClient httpClient, String loginsession) {  
		  String cookieResult= "";
		  String host = "781.ceair.com";
	      httpClient.getHostConfiguration().setHost(host, 80, "http"); 
		  //httpclient.getCookieStore().getCookies()
	        GetMethod getMethod = new GetMethod("http://781.ceair.com/bookingmanage/booking_bookSearchInit.do");  
	        getMethod.setRequestHeader("Referer", "http://travel.ceair.com/home_f.do");
	        getMethod.getParams().setParameter("Referer", "http://travel.ceair.com/home_f.do");
	        getMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36");
	        getMethod.getParams().setParameter("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36");
	        getMethod.setRequestHeader("Cookie", "JSESSIONID="+loginsession+";uid=112;");
	        getMethod.getParams().setParameter("Cookie", "JSESSIONID="+loginsession+";uid=112;");
	        //"Content-Type","application/x-www-form-urlencoded"
	        
	        try {  
	                // 执行getMethod  
	            	Thread.sleep(500);
	                int statusCode = httpClient.executeMethod(getMethod);  
	                
	              String result =   getMethod.getResponseBodyAsString();
	                if (statusCode != HttpStatus.SC_OK) {  
	                    System.err.println("Method failed: "  
	                            + getMethod.getStatusLine());  
	                }  
	                // 读取内容  
	                Cookie[] cookies = httpClient.getState().getCookies();  
	                
	                if(cookies.length<5){
	                	return "error";
	                }
	                
	                if(cookies!=null&&cookies.length==1){
	                	cookieResult = cookies[0].toExternalForm();
	                }else if(cookies!=null&&cookies.length==2){
	                	cookieResult = cookies[1].toExternalForm();
	                }else if(cookies!=null&&cookies.length>=5){
	                	cookieResult = cookies[4].toExternalForm();
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
