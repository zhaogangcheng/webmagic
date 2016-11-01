package cn.springmvc.login;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import cn.springmvc.util.HttpConnectionClient;

public class Login {
	
	public static void main(String strings[]) throws Exception   
    {  
		 
		HttpConnectionClient httpClient = new HttpConnectionClient();
		String url = "http://travel.ceair.com/log_f.do";
		NameValuePair[] nvps = {new NameValuePair("j_username", "hnkt"),new NameValuePair("j_password", "e10adc3949ba59abbe56e057f20f883e"),new NameValuePair("kaptcha", "n3m5") };
		String msg= httpClient.getContextByPostMethodLogin(url,nvps);
		
    }
	

}
