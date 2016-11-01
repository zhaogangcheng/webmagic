package cn.springmvc.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;


public class HttpConnectionClient {
	/**
	 * 链接超时
	 */
	private static int DEFAULT_CONNECTION_TIMEOUT = 60000 * 10;
	/**
	 * 传输超时
	 */
	private static int DEFAULT_SO_TIMEOUT = 1000 * 20;
	/**
	 * 最大连接数
	 */
	private static int DEFAULT_CONNECTIONS_MAX_TOTAL = 200;
	/**
	 * 每host最大连接数
	 */
	private static int DEFAULT_CONNECTIONS_MAX_PERHOST = 50;
	// 初始化用到的同步锁
	private final ReentrantLock lock = new ReentrantLock();
	
	private static Logger logger = Logger.getLogger(HttpConnectionClient.class);

	private MultiThreadedHttpConnectionManager connectionManager = null;
	private HttpClient httpClient = null;

	private int connectionTimeOut = DEFAULT_CONNECTION_TIMEOUT;
	private int soTimeOut = DEFAULT_SO_TIMEOUT;
	private int connectionMaxTotal = DEFAULT_CONNECTIONS_MAX_TOTAL;
	private int connectionMaxPerHost = DEFAULT_CONNECTIONS_MAX_PERHOST;
	private String codeing = "UTF-8";

	private HttpClient getHttpClient() {
		lock.lock();
		try {
			if (connectionManager == null) {
				connectionManager = new MultiThreadedHttpConnectionManager();
				configure();
			}
			if (httpClient == null) {
				httpClient = new HttpClient(connectionManager);
			}

		} finally {
			lock.unlock();
		}
		return httpClient;
	}

	public HttpClient getClient() {
		return getHttpClient();
	}

	/**
	 * 配置connectionmanager
	 */
	private void configure() {
		HttpConnectionManagerParams params = connectionManager.getParams();
		params.setConnectionTimeout(connectionTimeOut);
		params.setMaxTotalConnections(connectionMaxTotal);
		params.setDefaultMaxConnectionsPerHost(connectionMaxPerHost);
		params.setSoTimeout(soTimeOut);
	}

	/**
	 * 返回http网页内容，使用Get方法提交
	 * 
	 * @param url
	 * @param returnCharset 返回的编码格式
	 * @return
	 * @throws Exception
	 */

	public String getContextByGetMethod(String url, String returnCharset) {
		HttpClient client = getHttpClient();
		// 设置编码
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
				codeing);
		GetMethod gm = new GetMethod(url);
		String result = "";
		try {
			client.executeMethod(gm);

			if (gm.getStatusCode() >= 400) {
				throw new HttpException("GET-"+url+ " Connection Error!return Status :"
						+ gm.getStatusCode());
			}
//			result = new String(gm.getResponseBody(), "gbk");
//			System.out.println("result gbk:        "+result);
			result = new String(gm.getResponseBody(), returnCharset);
//			System.out.println("result utf-8:        "+result);
		} catch (Exception e) {
			logger.error("请求url失败：" + url ,e);
		} finally {
			gm.releaseConnection();
		}
		return result;
	}
	
	/**
	 * 返回输入流
	 * @param url
	 * @param returnCharset
	 * @return
	 * @throws Exception
	 */
	public InputStream getInputStreamByGetMethod(String url, String returnCharset) throws Exception {
		HttpClient client = getHttpClient();
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, codeing);
		GetMethod gm = new GetMethod(url);
		client.executeMethod(gm);
		if (gm.getStatusCode() >= 400) {
			throw new HttpException("GET-"+url+ " Connection Error!return Status :"
					+ gm.getStatusCode());
		}
		return gm.getResponseBodyAsStream();
	}
	
	/**
	 * 返回流的方式
	 * @param url
	 * @param returnCharset
	 * @return
	 */
	public String getStreamByGetMethod(String url, String returnCharset){
		HttpClient client = getHttpClient();
		// 设置编码
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
				codeing);
		GetMethod gm = new GetMethod(url);
		try {
			client.executeMethod(gm);

			if (gm.getStatusCode() >= 400) {
				throw new HttpException("GET-"+url+ " Connection Error!return Status :"
						+ gm.getStatusCode());
			}
			InputStream is = gm.getResponseBodyAsStream();
			byte[] bytes = new byte[1024];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			int count = 0;
			while ((count = is.read(bytes)) != -1)	baos.write(bytes, 0, count);;
			if (bytes.length > 0) {
				return new String(baos.toByteArray(),returnCharset);
			}
		} catch (Exception e) {
			logger.error("请求url失败："+ url ,e);
		} finally {
			gm.releaseConnection();
		}
		return "";
	}
	
	public String getStreamByGetMethod(String url) {
		return getStreamByGetMethod(url, "utf-8");
	}
	
	public String getContextByGetMethod(String url) {
		return getContextByGetMethod(url, "utf-8");
	}
	
	/**
	 * http get请求，查询参数由queryParamMap拼成
	 * @param url 仅包含get请求路径的url
	 * @param queryParamMap 存放查询参数的键值对
	 * @return get请求结果
	 */
	public String getContextByGetMethod(String url, Map<String, String> queryParamMap){
		if(queryParamMap==null || queryParamMap.size()==0){
			return getContextByGetMethod(url);
		}
		StringBuilder sb = new StringBuilder();
		String key = null;
		boolean first = true;
		Iterator<String> iterator = queryParamMap.keySet().iterator();
		while(iterator.hasNext()){
			if(first){
				sb.append(url);
				sb.append("?");
				first = false;
			}else{
				sb.append("&");
			}
			key = iterator.next();
			sb.append(key);
			sb.append("=");
			try{
				sb.append(URLEncoder.encode(queryParamMap.get(key), "utf-8"));
			}catch(Exception e){
				sb.append(queryParamMap.get(key));
			}
		}
		return getContextByGetMethod(sb.toString());
	}

	/**
	 * 返回http网页内容，使用Post方法提交
	 * 
	 * @param url
	 *            访问地址
	 * @param param
	 *            参数，键值对列表
	 * @return
	 * @throws Exception
	 */
	public int getContextByPostMethod(String url, NameValuePair[] nvps) throws Exception {
		HttpClient client = getHttpClient();
		// 设置编码
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
				codeing);
		PostMethod post = null;
		int result = 0;
		try {
			// 设置提交地址
			URL u = new URL(url);
			client.getHostConfiguration().setHost(u.getHost(),
					u.getPort() == -1 ? u.getDefaultPort() : u.getPort(),
					u.getProtocol());
			post = new PostMethod(u.getPath());
			// 提交数据
			post.setRequestBody(nvps);
			client.executeMethod(post);
			result = post.getStatusCode();
			if (post.getStatusCode() >= 400) {
				throw new HttpException("POST-"+url+" Connection Error!return Status :"
						+ post.getStatusCode());
			}
		} catch (Exception e) {
			logger.error("请求url失败："+ url ,e);
			throw e;
		} finally {
			if (post != null)
				post.releaseConnection();
		}
		return result;
	}
	

	/**
	 * 返回http网页内容，使用Post方法提交
	 * 
	 * @param url
	 *            访问地址
	 * @param param
	 *            参数，键值对列表
	 * @return
	 * @throws Exception
	 */
	public String getContextByPostMethod3(String url, NameValuePair[] nvps, String jSessionid) throws Exception {
		HttpClient client = getHttpClient();
		// 设置编码
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
				codeing);
		PostMethod post = null;
		try {
			// 设置提交地址
			URL u = new URL(url);
			client.getHostConfiguration().setHost(u.getHost(),
					u.getPort() == -1 ? u.getDefaultPort() : u.getPort(),
					u.getProtocol());
			post = new PostMethod(url);
			// 提交数据
			post.addParameters(nvps);
			Header header =  new Header("Cookie",jSessionid);
			//post.setRequestHeader("Cookie", "JSESSIONID=D9109693F0DFFA04C28354AAEBA26E91.t9; __COOKIE_SSO_KEY=SK_25927_pKq0XRW9PYDlbJZGgUBCHWNRLVYc1P52; uid=112");
			//post.setRequestHeader("X-Requested-With", "XMLHttpRequest");
			//post.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8"); 
			//post.setRequestHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36");
			//post.setRequestHeader("Accept","text/html, */*; q=0.01");
			   //post.setRequestHeader("Accept-Encoding","gzip, deflate");
			//post.setRequestHeader("Accept-Language","zh-CN,zh;q=0.8");
			//post.setRequestHeader("Connection","keep-alive");
			//post.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
			//post.setRequestHeader("Origin","http://781.ceair.com");
			//post.setRequestHeader("Referer","http://781.ceair.com/bookingmanage/booking_bookSearchInternationalInit.do");
			post.getParams().setParameter("Cookie","JSESSIONID=6A49D7F1094C0E0FFC3C9FAC8056A474.t9; __COOKIE_SSO_KEY=SK_25927_3JYiZMtSx5Y0vuu9lokFgJyQTnzMUSMh; uid=112");
			post.getParams().setParameter(HttpMethodParams.USER_AGENT,"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36");
			post.getParams().setParameter("X-Requested-With", "XMLHttpRequest");
			post.getParams().setParameter("Content-Type","application/x-www-form-urlencoded; charset=UTF-8"); 
			//post.getParams().setParameter("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36");
			post.getParams().setParameter("Accept","text/html, */*; q=0.01");
			post.getParams().setParameter("Accept-Language","zh-CN,zh;q=0.8");
			post.getParams().setParameter("Connection","keep-alive");
			post.getParams().setParameter("Content-Type","application/x-www-form-urlencoded");
			post.getParams().setParameter("Origin","http://781.ceair.com");
			post.getParams().setParameter("Referer","http://781.ceair.com/bookingmanage/booking_bookSearchInternationalInit.do");
			post.addRequestHeader(header);
			post.setRequestBody(nvps);
			
			int statusCode = client.executeMethod(post);
			int flag = 0;
			if (statusCode != HttpStatus.SC_OK) {
				while(flag<3&&statusCode!= HttpStatus.SC_OK){
					++flag;
					System.out.println("============post请求出错=====重试第多少次:"+flag);
					statusCode = client.executeMethod(post);
					
				}
				
		/*		throw new HttpException("POST-" + url
						+ " Connection Error!return Status :"
						+ post.getStatusCode());*/
			}
		return	post.getResponseBodyAsString();
		} catch (Exception e) {
			logger.error("请求url失败："+ url ,e);
			throw e; 
		} finally {
			if (post != null)
				post.releaseConnection();
		}
	}
	
	
	/**
	 * 返回http网页内容，使用Post方法提交
	 * 
	 * @param url
	 *            访问地址
	 * @param param
	 *            参数，键值对列表
	 * @return
	 * @throws Exception
	 */
	public String getContextByPostMethodLogin(String url, NameValuePair[] nvps, String cookie) throws Exception {
		HttpClient client = getHttpClient();
		// 设置编码
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
				codeing); 
		PostMethod post = null;
		try {
			// 设置提交地址
			URL u = new URL(url);
			client.getHostConfiguration().setHost(u.getHost(),
					u.getPort() == -1 ? u.getDefaultPort() : u.getPort(),
					u.getProtocol());
			post = new PostMethod(url);
			// 提交数据
			post.addParameters(nvps);
			
			//Accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8
			/*Accept-Encoding:gzip, deflate
			Accept-Language:zh-CN,zh;q=0.8
			Cache-Control:max-age=0
			Connection:keep-alive
			Content-Length:72
			Content-Type:application/x-www-form-urlencoded
			Cookie:CNZZDATA1000327295=1904323841-1477879351-%7C1477982590; uid=112; JSESSIONID=54A1C94EE13D505784C057D0D2990831.t1
			Host:travel.ceair.com
			Origin:http://travel.ceair.com
			Referer:http://travel.ceair.com/
			User-Agent:Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36*/
			
			Header header =  new Header("Cookie",cookie);
			//post.getParams().setParameter("Cookie","JSESSIONID=6A49D7F1094C0E0FFC3C9FAC8056A474.t9; __COOKIE_SSO_KEY=SK_25927_3JYiZMtSx5Y0vuu9lokFgJyQTnzMUSMh; uid=112");
			post.getParams().setParameter(HttpMethodParams.USER_AGENT,"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36");
			post.getParams().setParameter("X-Requested-With", "XMLHttpRequest");
			post.getParams().setParameter("Content-Type","application/x-www-form-urlencoded; charset=UTF-8"); 
			//post.getParams().setParameter("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36");
			post.getParams().setParameter("Accept","text/html, */*; q=0.01");
			post.getParams().setParameter("Accept-Language","zh-CN,zh;q=0.8");
			post.getParams().setParameter("Connection","keep-alive");
			post.getParams().setParameter("Content-Type","application/x-www-form-urlencoded");
			post.getParams().setParameter("Origin","http://travel.ceair.com");
			post.getParams().setParameter("Referer","http://travel.ceair.com/");
			post.addRequestHeader(header);
			post.setRequestBody(nvps);
			
			int statusCode = client.executeMethod(post);
			//String result = post.getResponseBodyAsString();
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
			logger.error("请求url失败："+ url ,e);
			throw e;
		} finally {
			if (post != null)
				post.releaseConnection();
		}
	}
	
	/**
	 * 返回http网页内容，使用Post方法提交
	 * 
	 * @param url
	 *            访问地址
	 * @param param
	 *            参数，键值对列表
	 * @return
	 * @throws Exception
	 */
	public String getContextByPostMethod2(String url, NameValuePair[] nvps, String jSessionid) throws Exception {
		HttpClient client = getHttpClient();
		// 设置编码
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
				codeing);
		PostMethod post = null;
		try {
			// 设置提交地址
			URL u = new URL(url);
			client.getHostConfiguration().setHost(u.getHost(),
					u.getPort() == -1 ? u.getDefaultPort() : u.getPort(),
					u.getProtocol());
			post = new PostMethod(url);
			// 提交数据
			post.addParameters(nvps);
			Header header =  new Header("Cookie","JSESSIONID=6A49D7F1094C0E0FFC3C9FAC8056A474.t9; __COOKIE_SSO_KEY=SK_25927_3JYiZMtSx5Y0vuu9lokFgJyQTnzMUSMh; uid=112");
			//post.setRequestHeader("Cookie", "JSESSIONID=D9109693F0DFFA04C28354AAEBA26E91.t9; __COOKIE_SSO_KEY=SK_25927_pKq0XRW9PYDlbJZGgUBCHWNRLVYc1P52; uid=112");
			//post.setRequestHeader("X-Requested-With", "XMLHttpRequest");
			//post.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8"); 
			//post.setRequestHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36");
			//post.setRequestHeader("Accept","text/html, */*; q=0.01");
			   //post.setRequestHeader("Accept-Encoding","gzip, deflate");
			//post.setRequestHeader("Accept-Language","zh-CN,zh;q=0.8");
			//post.setRequestHeader("Connection","keep-alive");
			//post.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
			//post.setRequestHeader("Origin","http://781.ceair.com");
			//post.setRequestHeader("Referer","http://781.ceair.com/bookingmanage/booking_bookSearchInternationalInit.do");
			post.getParams().setParameter("Cookie","JSESSIONID=6A49D7F1094C0E0FFC3C9FAC8056A474.t9; __COOKIE_SSO_KEY=SK_25927_3JYiZMtSx5Y0vuu9lokFgJyQTnzMUSMh; uid=112");
			post.getParams().setParameter(HttpMethodParams.USER_AGENT,"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36");
			post.getParams().setParameter("X-Requested-With", "XMLHttpRequest");
			post.getParams().setParameter("Content-Type","application/x-www-form-urlencoded; charset=UTF-8"); 
			//post.getParams().setParameter("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36");
			post.getParams().setParameter("Accept","text/html, */*; q=0.01");
			post.getParams().setParameter("Accept-Language","zh-CN,zh;q=0.8");
			post.getParams().setParameter("Connection","keep-alive");
			post.getParams().setParameter("Content-Type","application/x-www-form-urlencoded");
			post.getParams().setParameter("Origin","http://781.ceair.com");
			post.getParams().setParameter("Referer","http://781.ceair.com/bookingmanage/booking_bookSearchInternationalInit.do");
			post.addRequestHeader(header);
			post.setRequestBody(nvps);
			
			int statusCode = client.executeMethod(post);
			if (statusCode != HttpStatus.SC_OK) {
				throw new HttpException("POST-" + url
						+ " Connection Error!return Status :"
						+ post.getStatusCode());
			}
		return	post.getResponseBodyAsString();
		} catch (Exception e) {
			logger.error("请求url失败："+ url ,e);
			throw e;
		} finally {
			if (post != null)
				post.releaseConnection();
		}
	}
	
	/**
	 * 返回http网页内容，使用Post方法提交
	 * 
	 * @param url
	 *            访问地址
	 * @param param
	 *            参数，键值对列表
	 * @return
	 * @throws Exception
	 */
	public String getContextByPostMethod(String url, NameValuePair[] nvps, String jSessionid) throws Exception {
		HttpClient client = getHttpClient();
		// 设置编码
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
				codeing);
		PostMethod post = null;
		try {
			// 设置提交地址
			URL u = new URL(url);
			client.getHostConfiguration().setHost(u.getHost(),
					u.getPort() == -1 ? u.getDefaultPort() : u.getPort(),
					u.getProtocol());
			post = new PostMethod(url);

			// 提交数据
			post.setRequestBody(nvps);
			post.setRequestHeader("Cookie", jSessionid);
			//post.setRequestHeader("X-Requested-With", "XMLHttpRequest");
			post.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8"); 
			post.setRequestHeader("User-Agent","Apache-HttpClient/4.3.1 (java 1.5)");
			//post.setRequestHeader("Transfer-Encoding", "chunked");
			int statusCode = client.executeMethod(post);
			if (statusCode != HttpStatus.SC_OK) {
				throw new HttpException("POST-" + url
						+ " Connection Error!return Status :"
						+ post.getStatusCode());
			}
		return	post.getResponseBodyAsString();
		} catch (Exception e) {
			logger.error("请求url失败："+ url ,e);
			throw e;
		} finally {
			if (post != null)
				post.releaseConnection();
		}
	}
	
	
	public String getContextByPostMethod(String url, String charset,
			Map<String, String> params) throws Exception {
		HttpClient client = getHttpClient();
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
				charset);

		PostMethod post = new PostMethod(url);
		for (String key : params.keySet()) {
			post.setParameter(key, params.get(key));
		}

		try {
			int statusCode = client.executeMethod(post);
			if (statusCode != HttpStatus.SC_OK) {
				throw new HttpException("POST-" + url
						+ " Connection Error!return Status :"
						+ post.getStatusCode());
			}
			return post.getResponseBodyAsString();
		} catch (Exception e) {
			logger.error("HTTP的POST请求"+url+"调用失败", e);
			throw new Exception("HTTP的POST请求"+url+"调用失败");
		} finally {
			post.releaseConnection();
			client.getHttpConnectionManager().closeIdleConnections(0);
		}
	}

	/**
	 * 返回http网页内容，使用Post方法提交
	 * @param url	地址
	 * @param charsetStr	字符集
	 * @param map	需要传递的对象列表
	 * @return	返回内容
	 * @throws Exception
	 */
	public String getContextByPostMethod(String url, String charsetStr,HashMap map) throws Exception {
	
		HttpClient client = getHttpClient();
		// 设置编码
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
				charsetStr);
		PostMethod post = null;
		String result = "";
		try {
			// 设置提交地址
			URL u = new URL(url);
			client.getHostConfiguration().setHost(u.getHost(),
					u.getPort() == -1 ? u.getDefaultPort() : u.getPort(),
					u.getProtocol());
			post = new PostMethod(u.getPath());			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try
			{
			   ObjectOutputStream oos = new ObjectOutputStream(baos);
			   oos.writeObject(map);
			}
			catch (Throwable e)
			{
				logger.error("构建ObjectOutputStream异常：",e);
				throw new Exception("POST-"+url+" Create ObjectOutputStream Error。");
			}
			byte[] data = baos.toByteArray();
			ByteArrayInputStream bis = new ByteArrayInputStream(data);
			post.setRequestBody(bis);
			
			// 提交数据
			client.executeMethod(post);		
			if (post.getStatusCode() >= 400) {
				throw new HttpException("POST-"+url+" Connection Error!return Status :"
						+ post.getStatusCode());
			}
			result = post.getResponseBodyAsString();
		} catch (Exception e) {
			logger.error("请求url失败："+ url ,e);
			throw e;
		} finally {
			if (post != null)
				post.releaseConnection();
		}
		return result;
	}

	/**
	 * 执行DELETE方法
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public int doDelete(String url) {
		HttpClient client = getHttpClient();
		// 设置编码
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
				codeing);
		DeleteMethod dm = new DeleteMethod(url);
		int result = 0;
		try {
			client.executeMethod(dm);
			result = dm.getStatusCode();
			if (dm.getStatusCode() >= 400) {
				throw new HttpException("DELETE-"+url+" Connection Error!return Status :"
						+ dm.getStatusCode());
			}
		} catch (Exception e) {
			logger.error("请求url失败："+ url ,e);
		} finally {
			dm.releaseConnection();
		}
		return result;
	}

	public int getConnectionTimeOut() {
		return connectionTimeOut;
	}

	public void setConnectionTimeOut(int connectionTimeOut) {
		this.connectionTimeOut = connectionTimeOut;
	}

	public int getConnectionMaxTotal() {
		return connectionMaxTotal;
	}

	public void setConnectionMaxTotal(int connectionMaxTotal) {
		this.connectionMaxTotal = connectionMaxTotal;
	}

	public int getConnectionMaxPerHost() {
		return connectionMaxPerHost;
	}

	public void setConnectionMaxPerHost(int connectionMaxPerHost) {
		this.connectionMaxPerHost = connectionMaxPerHost;
	}

	/**
	 * @param codeing
	 *            the codeing to set
	 */
	public void setCodeing(String codeing) {
		this.codeing = codeing;
	}

	/**
	 * @return the codeing
	 */
	public String getCodeing() {
		return codeing;
	}

	public void setSoTimeOut(int soTimeOut) {
		this.soTimeOut = soTimeOut;
	}

	public int getSoTimeOut() {
		return soTimeOut;
	}
}
