package cn.springmvc.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.springmvc.login.Login;
import cn.springmvc.util.ExportExcel;
import cn.springmvc.util.FourExcelZip;
import cn.springmvc.util.HttpConnectionClient;
import cn.springmvc.util.PropertiesUtil;
import cn.springmvc.util.ZipUtil;
import cn.springmvc.vo.DhQueryVo;
import cn.springmvc.vo.DiscountFlightInfoVo;
import cn.springmvc.vo.ExcelVo;
import cn.springmvc.vo.FlightInfoVO;
import cn.springmvc.vo.HBMap;
import cn.springmvc.vo.HQQueryVo;

@Controller
@RequestMapping("/dh")
public class DhController {
	Log logger = LogFactory.getLog(DhController.class);
	
	//List<ExcelVo> resultListss=  new LinkedList<ExcelVo>();
	Map<String,String> codeMap =  HBMap.getMap();
	
	
	@ResponseBody
	@RequestMapping(value="/login",method={RequestMethod.POST })
	public Map<String, Object> login(HttpServletRequest request,  HttpServletResponse response,HQQueryVo vo){
		if(vo==null){
			logger.error("传递的vo为空");
		}
		String username = vo.getFrom();
		String password = vo.getArrive();
		Map<String,Object> map = new HashMap<String,Object>();  
		String jsessionid = "";
		try {
			jsessionid = Login.getLoginJessionId(username,password);
		} catch (Exception e) {
			e.printStackTrace(); 
		}
		map.put("jsessionid", jsessionid);
		map.put("code", "200");
	    return map;
	}
	
	
	
	@ResponseBody
	@RequestMapping(value="/autoDownload",method={RequestMethod.POST })
	public Map<String, Object> autoDownload(HttpServletRequest request,  HttpServletResponse response,HQQueryVo vo){
		 logger.error("test=======");
		if (vo == null){
			return null;
		}
		
		long timeOne=System.currentTimeMillis();
		
		String jsessionid = vo.getJsessionid();
		Date endTime  = vo.getEndtime();
		Date startTime  = vo.getStarttime();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String endDate = format.format(endTime);
		String startDate = format.format(startTime);
		
		SimpleDateFormat format1 = new SimpleDateFormat("MM.dd");
		String resultendDate = format1.format(endTime);
		String resultstartDate = format1.format(startTime);
		StringBuffer resultFilname = new StringBuffer();
		StringBuffer hbresultFilname = new StringBuffer();
		resultFilname.append(resultstartDate+"-"+resultendDate);
		String resultarrive = vo.getArrive();
		String[] resultarrives = resultarrive.split("/");
		if(resultarrives!=null&&resultarrives.length==1){
			resultFilname.append("_"+resultarrive);
			hbresultFilname.append(resultarrive);
		}else if(resultarrives!=null&&resultarrives.length>1){
			resultFilname.append("_"+resultarrive.replaceAll("/", "_"));
			hbresultFilname.append(resultarrive.replaceAll("/", "_"));
		}
		logger.info("==进入自动下载==");
		
		//获取天数
		List<String> listDays = getListDay(stringToCalendar(startDate),stringToCalendar(endDate));
		
		//获取出发抵达的组合数据
		String from = vo.getFrom();
		String arrive = vo.getArrive();
		List<HQQueryVo> zuheList = getzuhe(from,arrive);
		
		List<ExcelVo> allList = new LinkedList<ExcelVo>();
		for(HQQueryVo zh : zuheList){
			for(String flyDate : listDays){
				//httpclient请求数据
				List<ExcelVo> resultExcelList = getDomMsg(zh.getFrom(),zh.getArrive(),flyDate,jsessionid);
				allList.addAll(resultExcelList);
			    Thread thread = Thread.currentThread();
			    try {
					thread.sleep(1500);
				} catch (InterruptedException e) {
					logger.error("getDomMSg错误",e);
					e.printStackTrace();
				}//暂停1.5秒后程序继续执行
			}
		}
		logger.info("==获取数据完成==");
		//将list中重复的数据重新组装
		allList = getonlyList(allList);
		
		//去掉list www的
		allList = distinctAllList(allList);
		
		//时间组装成2016-09-09~2016-09-09
		allList = riqichongfu(allList);
		
		if(allList==null||allList.size()<=0){
			return null;
		}
		System.out.println("==进入下载==");
		
		/****
		 *  下载测试
		 */
		
		response.setContentType("application/x-excel");  
		response.setContentType("application/vnd.ms-excel;charset=UTF-8");
/*		String filename = "easternAirlines.xls";
		resultFilname*/
		response.setHeader("Content-disposition", "attachment; filename="
				+ resultFilname+".xls");
		response.setCharacterEncoding("utf-8");
		OutputStream os = null;
		String directory = "excel"+ZipUtil.getRandomString(4);
		String filePath = "D:\\zhanghan\\dhDownload\\"+directory +"\\";
		File iscunzai = new File(filePath);
		//如果文件夹不存在则创建    
		if  (!iscunzai.exists()  && !iscunzai.isDirectory())      
		{       
		    iscunzai.mkdir(); 
		}
		
		try {
			os = response.getOutputStream();
			//生成原来的xls
			OutputStream out = new FileOutputStream(filePath+" "+resultFilname+".xls");
		    String[] headers =  { "航路", "可售航班", "舱位", "价格", "旅行日期", "隔日中转"};  
			ExportExcel<ExcelVo> ex = new ExportExcel<ExcelVo>(); 
	        ex.exportExcel(headers, allList, out);
		} catch (Exception e) {
			   logger.error("==在外面生成总的excel=="+e);
		}
		
		try {
		
	        logger.info("==生成对应的excel==");
	        /** ========生成qunaer开始========**/
	        FourExcelZip.qunaerExcel(allList, filePath+" qunar_"+hbresultFilname+".xlsx");
	        /** ========生成qunaer结束========**/
	      
	        /** ========生成taobao开始========**/
	        FourExcelZip.taobaoExcel(allList, filePath+" taobao_"+hbresultFilname+".xlsx");
	        /** ========生成taobao结束========**/
	        
	        
	        /** ========生成tongcheng开始========**/
	        FourExcelZip.tongchengExcel(allList, filePath+" tongcheng_"+hbresultFilname+".xlsx");
	        /** ========生成tongcheng结束========**/
	        
	        /** ========生成ctrip开始========**/
	        FourExcelZip.ctripExcel(allList, filePath+" ctrip_"+hbresultFilname+".xlsx");
	        /** ========生成ctrip结束========**/
	        logger.info("==生成excel完成，开始下载zip==");
	        String zipPath = "D:\\zhanghan\\dhDownload\\zip\\";
	        //String dir = zipPath+resultFilname+".xls";
	        String zipFileName = resultFilname+".zip";
	        
	        try{
	        	//生产解压缩文件
	        	ZipUtil.zip(filePath, zipPath, zipFileName);
	        	
	        	
	        }catch(Exception e){
	            logger.error("生产解压缩文件错误",e);
	        	e.printStackTrace();
	        }
	        ZipUtil.downZip(response, zipPath+zipFileName, zipFileName);
	        logger.info("==下载zip完成==");
	        //JOptionPane.showMessageDialog(null, "导出成功!");  
	        logger.info("excel导出成功！");  
			
		} catch (IOException e) {
			logger.error("获取流失败",e);
			e.printStackTrace();
		}finally {
			try{
				if (os != null) {  
					os.close();  
				} 
			}catch(IOException e){
				logger.error("获取流失败",e);
				e.printStackTrace();
			}
			
		}
		
		//删除excel文件
    	ZipUtil.deleteFiles(filePath); 
		
    	logger.info("=====downloads======"+	allList.size());
		/****
		 *  下载测试
		 */
		
		Map<String,Object> map = new HashMap<String,Object>();  
	    map.put("result", "200");
	    map.put("jsessionid", vo.getJsessionid());
	    map.put("data", allList);
	/*    request.getSession().setAttribute("resultListss", allList);
	    request.getSession().setAttribute("resultFilname", resultFilname);*/
		long timeTwo=System.currentTimeMillis();
		//System.out.println("相隔"+(timeTwo-timeOne)+"秒");
		long minute=(timeTwo-timeOne)/(1000);//转化minute
		logger.info("相隔"+minute+"秒");
		map.put("timer", minute);
	    return map;
	}
	
	
	
	@ResponseBody
	@RequestMapping(value="/downloads",method={RequestMethod.GET })
	public void downloads(HttpServletRequest request,  HttpServletResponse response){
		
		Object rs = request.getSession().getAttribute("resultListss");
		List<ExcelVo> resultListss=  new LinkedList<ExcelVo>();
		if(rs!=null){
			resultListss = (List<ExcelVo>)rs;
		}
		response.setContentType("application/x-excel");  
		response.setContentType("application/vnd.ms-excel;charset=UTF-8");
		String filename = "easternAirlines.xls";
	    Object resfileName = request.getSession().getAttribute("resultFilname");
	    if(resfileName!=null){
	    	filename = resfileName.toString()+".xls";
	    }
		response.setHeader("Content-disposition", "attachment; filename="
				+ filename);
		response.setCharacterEncoding("utf-8");
		OutputStream os = null;
		try {
			os = response.getOutputStream();
		    String[] headers =  { "航路", "可售航班", "舱位", "价格", "旅行日期", "隔日中转"};  
			ExportExcel<ExcelVo> ex = new ExportExcel<ExcelVo>(); 
	        ex.exportExcel(headers, resultListss, os);
	        //JOptionPane.showMessageDialog(null, "导出成功!");  
            System.out.println("excel导出成功！");  
			
		} catch (IOException e) {
			System.out.println("获取流失败");
			e.printStackTrace();
		}finally {
			try{
				if (os != null) {  
					os.close();  
				} 
			}catch(IOException e){
				System.out.println("获取流失败");
				e.printStackTrace();
			}
			
		}
		
		System.out.println("=====downloads======"+	resultListss.size());
	}
	
	
	
	@ResponseBody
	@RequestMapping(value="/posthq",method={RequestMethod.POST })
	public Map<String, Object> posthq(HttpServletRequest request,  HttpServletResponse response,HQQueryVo vo){
		if (vo == null){
			return null;
		}
		
		long timeOne=System.currentTimeMillis();
		
		String jsessionid = vo.getJsessionid();
		Date endTime  = vo.getEndtime();
		Date startTime  = vo.getStarttime();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String endDate = format.format(endTime);
		String startDate = format.format(startTime);
		
		SimpleDateFormat format1 = new SimpleDateFormat("MM.dd");
		String resultendDate = format1.format(endTime);
		String resultstartDate = format1.format(startTime);
		StringBuffer resultFilname = new StringBuffer();
		resultFilname.append(resultstartDate+"-"+resultendDate);
		String resultarrive = vo.getArrive();
		String[] resultarrives = resultarrive.split("/");
		if(resultarrives!=null&&resultarrives.length==1){
			resultFilname.append("_"+resultarrive);
		}else if(resultarrives!=null&&resultarrives.length>1){
			resultFilname.append("_"+resultarrive.replaceAll("/", "_"));
		}
		
		
		//获取天数
		List<String> listDays = getListDay(stringToCalendar(startDate),stringToCalendar(endDate));
		
		//获取出发抵达的组合数据
		String from = vo.getFrom();
		String arrive = vo.getArrive();
		List<HQQueryVo> zuheList = getzuhe(from,arrive);
		
		List<ExcelVo> allList = new LinkedList<ExcelVo>();
		for(HQQueryVo zh : zuheList){
			for(String flyDate : listDays){
				//httpclient请求数据
				List<ExcelVo> resultExcelList = getDomMsg(zh.getFrom(),zh.getArrive(),flyDate,jsessionid);
				allList.addAll(resultExcelList);
			    Thread thread = Thread.currentThread();
			    try {
					thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}//暂停1.5秒后程序继续执行
			}
		}
		
		//将list中重复的数据重新组装
		allList = getonlyList(allList);
		
		//去掉list www的
		allList = distinctAllList(allList);
		
		//时间组装成2016-09-09~2016-09-09
		allList = riqichongfu(allList);
		
		
		Map<String,Object> map = new HashMap<String,Object>();  
	    map.put("result", "200");
	    map.put("jsessionid", vo.getJsessionid());
	    map.put("data", allList);
	    request.getSession().setAttribute("resultListss", allList);
	    request.getSession().setAttribute("resultFilname", resultFilname);
		long timeTwo=System.currentTimeMillis();
		//System.out.println("相隔"+(timeTwo-timeOne)+"秒");
		long minute=(timeTwo-timeOne)/(1000);//转化minute
		System.out.println("相隔"+minute+"秒");
		map.put("timer", minute);
	    return map;
	}
	
	
	public List<ExcelVo> distinctAllList(List<ExcelVo> list){
		List<ExcelVo> resultlists = new LinkedList<ExcelVo>();
		if(list==null||list.size()<=0){
			return  resultlists;
		}
		
		for(int i = 0 , len= list.size();i<len;++i){  
			  if("www".equals(list.get(i).getHanglu())){  
			       list.remove(i);  
			       --len;//减少一个  
			       --i;//
			 }
			}
		return list;
	}
	
	
	public List<ExcelVo> riqichongfu(List<ExcelVo> list){
		for(int i = 0 , len= list.size();i<len;++i){  
			if(list.get(i).getRiqi().indexOf("~")<0){
				list.get(i).setRiqi(list.get(i).getRiqi()+"~"+list.get(i).getRiqi());
				}
		 }
		return list;
	}
	
	
	
	//组装 List<ExcelVo> 组装重复的
	public List<ExcelVo> getonlyList(List<ExcelVo> list){
		List<ExcelVo> resultlists = new LinkedList<ExcelVo>();
		if(list==null||list.size()<=0){
			return resultlists;
		}
		
		for(int i=0;i<list.size();i++){
			ExcelVo li = list.get(i);
			for(int j=0;j<list.size();j++){
				ExcelVo lj = list.get(j);
				if(li.getChangwei().equals(lj.getChangwei())&&li.getHangban().equals(lj.getHangban())
						&&li.getHanglu().equals(lj.getHanglu())&&li.getJiage().equals(lj.getJiage())
						&&!li.getRiqi().equals(lj.getRiqi())){
					
					//日期格式 2016-08-15~2016-09-20	如果日期 格式没有~  比较日期的时间 然后叠加
					if(li.getRiqi().indexOf("~")>0){
						//还得日期是相邻的日期才会重叠在一起
						int m =li.getRiqi().indexOf("~");
						String enddate = li.getRiqi().substring(m+1);
						String lastdate = lj.getRiqi();
						//int diff = stringToCalendar(enddate).compareTo(stringToCalendar(lastdate));
						long diff = (stringToCalendar(enddate).getTimeInMillis()-stringToCalendar(lastdate).getTimeInMillis())/(1000*60*60*24);
						if(diff==-1){
							li.setRiqi(li.getRiqi().substring(0,m)+"~"+lastdate);
							//lj vo中的数据变换成www
							lj.setHanglu("www");
						}
					}else if(lj.getRiqi().indexOf("~")>0){
						//还得日期是相邻的日期才会重叠在一起
						int m =lj.getRiqi().indexOf("~");
						String enddate = lj.getRiqi().substring(m+1);
						String lastdate = li.getRiqi();
						long diff = (stringToCalendar(enddate).getTimeInMillis()-stringToCalendar(lastdate).getTimeInMillis())/(1000*60*60*24);
						//int diff = stringToCalendar(enddate).compareTo(stringToCalendar(lastdate));
						if(diff==-1){
							lj.setRiqi(lj.getRiqi().substring(0,m)+"~"+lastdate);
							//lj vo中的数据变换成www
							li.setHanglu("www");
						}
					}else{
						String ljdate = lj.getRiqi();
						String lidate = li.getRiqi();
						//int diff = stringToCalendar(ljdate).compareTo(stringToCalendar(lidate));
						long diff = (stringToCalendar(ljdate).getTimeInMillis()-stringToCalendar(lidate).getTimeInMillis())/(1000*60*60*24);
						if(diff==-1){
							lj.setRiqi(lj.getRiqi()+"~"+li.getRiqi());
							li.setHanglu("www");
						}else if(diff==1){
							li.setRiqi(li.getRiqi()+"~"+lj.getRiqi());
							lj.setHanglu("www");
						} 
						
					}
				}
			}
			
		}
		
		return list;
	}
	
	
	public static void main(String[] args) {
		String enddate ="2016-12-04";
		String lastdate ="2016-12-10";
		long diff = (stringToCalendars(enddate).getTimeInMillis()-stringToCalendars(lastdate).getTimeInMillis())/(1000*60*60*24);
		if(diff>-7){
			System.out.println("OK");
		}
		System.out.println(diff);
		
		 Calendar a = Calendar.getInstance(),
	              b = Calendar.getInstance();
	        a.set(2015, Calendar.MARCH, 31);
	        b.set(2015, Calendar.APRIL, 1);
	        long diffDays = (b.getTimeInMillis() - a.getTimeInMillis()) / (1000 * 60 * 60 * 24);
	        System.out.println(diffDays);
		
	}
	
	public static Calendar stringToCalendars(String str){
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}
	
	//笛卡尔积获取起飞抵达组合
	public List<HQQueryVo> getzuhe(String from,String arrive){
		List<HQQueryVo> resultList = new ArrayList<HQQueryVo>();
		if(StringUtils.isBlank(from)||StringUtils.isBlank(arrive)){
			return resultList;
		}
		String[] froms = from.split("/");
		String[] arrives = arrive.split("/");
		for(int i=0;i<froms.length;i++){
			for(int j=0;j<arrives.length;j++){
				HQQueryVo vo = new HQQueryVo();
				vo.setFrom(froms[i]);
				vo.setArrive(arrives[j]);
				resultList.add(vo);
			}
		}
		return resultList;
	}
	
	public List<ExcelVo> getDomMsg(String from, String arrive,String endDate,String jsessionid){
		List<ExcelVo> resultExcelList = new LinkedList<ExcelVo>();
		String url =null;
		String msg = "";
		HttpConnectionClient httpClient = new HttpConnectionClient();
		url = "http://781.ceair.com/bookingmanage/booking_bookodAjaxSearch.do?isIT=true";
		NameValuePair[] nvps = {new NameValuePair("routeType", "ODOW"),new NameValuePair("flightOrder", "0"),new NameValuePair("cabinLevel", "\"\""),new NameValuePair("adtCount", "1"),new NameValuePair("kamno", "\"\""),new NameValuePair("retDate", "\"\""),new NameValuePair("depDate", endDate),new NameValuePair("arrAirpCd", arrive),new NameValuePair("depAirpCd", from),new NameValuePair("segIndex", "0") };
		try{
			msg= httpClient.getContextByPostMethod3(url,nvps,"JSESSIONID="+jsessionid);
		}catch(Exception e){
			logger.error("通過接口去數據出錯：url=="+"from:"+from+"|arrive:"+arrive+"|endDate:"+endDate+"|jsessionid:"+jsessionid);
			logger.error("通過接口去數據出錯：结果=="+"resultExcelList:"+resultExcelList);
			try {
				
				msg= httpClient.getContextByPostMethod3(url,nvps,"JSESSIONID="+jsessionid);
				logger.error("前面失败第一次重试成功，：url=="+"from:"+from+"|arrive:"+arrive+"|endDate:"+endDate+"|jsessionid:"+jsessionid);
			} catch (Exception e2) {
				logger.error("==重试1==通過接口去數據出錯：url=="+"from:"+from+"|arrive:"+arrive+"|endDate:"+endDate+"|jsessionid:"+jsessionid);
				logger.error("==重试1==通過接口去數據出錯：结果=="+"resultExcelList:"+resultExcelList);
				
				try {
					msg= httpClient.getContextByPostMethod3(url,nvps,"JSESSIONID="+jsessionid);
					logger.error("前面失败第二次重试成功，：url=="+"from:"+from+"|arrive:"+arrive+"|endDate:"+endDate+"|jsessionid:"+jsessionid);
				} catch (Exception e3) {
					logger.error("==重试2==通過接口去數據出錯：url=="+"from:"+from+"|arrive:"+arrive+"|endDate:"+endDate+"|jsessionid:"+jsessionid);
					logger.error("==重试2==通過接口去數據出錯：结果=="+"resultExcelList:"+resultExcelList);
					
					try {
						msg= httpClient.getContextByPostMethod3(url,nvps,"JSESSIONID="+jsessionid);
						logger.error("前面失败第三次重试成功，：url=="+"from:"+from+"|arrive:"+arrive+"|endDate:"+endDate+"|jsessionid:"+jsessionid);
					} catch (Exception e4) {
						logger.error("==重试3==通過接口去數據出錯：url=="+"from:"+from+"|arrive:"+arrive+"|endDate:"+endDate+"|jsessionid:"+jsessionid);
						logger.error("==重试3==通過接口去數據出錯：结果=="+"resultExcelList:"+resultExcelList);
					}
				}
				
			}
		}
		//解析dom
		List<DiscountFlightInfoVo> resultList = parseDOM(msg,from,arrive,endDate);
		//转换要导出的数据
		resultExcelList = convertVo(resultList);
		return resultExcelList;
	}
	
	
	
	//将日期转换
	private  List<String> getListDay(Calendar startDay, Calendar endDay) {  
		  List<String> dateList = new ArrayList<String>();
		  dateList.add(calendarToString(startDay));
		  // 给出的日期开始日比终了日大则不执行打印  
		  if (startDay.compareTo(endDay) >= 0) {  
		   return dateList;  
		  }  
		  // 现在打印中的日期  
		  Calendar currentPrintDay = startDay;  
		  while (true) {  
		   // 日期加一  
		   currentPrintDay.add(Calendar.DATE, 1);
		   dateList.add(calendarToString(currentPrintDay));
		   // 日期加一后判断是否达到终了日，达到则终止打印  
		   if (currentPrintDay.compareTo(endDay) == 0) {
			   break;  
		   	}
		  } 
		  return dateList;
		}
	
	public String calendarToString(Calendar calendar){
		//Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(calendar.getTime());
		return dateStr;
	}
	
	public Calendar  stringToCalendar(String str){
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}
	
	//将dom数据解析成excle结构数据
	public List<ExcelVo> convertVo(List<DiscountFlightInfoVo> resultList){
		if(resultList==null||resultList.size()<=0){
			return new LinkedList<ExcelVo>();
		}
		
 		PropertiesUtil pu = new PropertiesUtil();
		String hb = pu.getProperty("fiter_class");
		String[] filtergb = {}; 
		if(StringUtils.isNotBlank(hb)){
			filtergb = hb.split("/");
		}
		
		List<ExcelVo> excelLists = new LinkedList<ExcelVo>();
		//将数据过滤组装
		for(int i=0;i<resultList.size();i++){
			ExcelVo vo = new ExcelVo();
			DiscountFlightInfoVo dvo = resultList.get(i);
			String firstchangwei = dvo.getFirstFlightSpace().substring(0,1);
			String secondchangwei = dvo.getSecondFlightSpace().substring(0,1);
			
			boolean flag = false;
			if(filtergb!=null&&filtergb.length>0){
				for(int k=0;k<filtergb.length;k++){
					if(filtergb[k].equals(firstchangwei)||filtergb[k].equals(secondchangwei)){
						flag = true;
						break;					
						}
				}
			}
			
			if(flag){
				continue;
			}
			//I舱 Z舱 排除
			/*if("I".equals(firstchangwei)||"I".equals(secondchangwei)||"Z".equals(firstchangwei)||"Z".equals(secondchangwei)){
				continue;
			}*/
			
			//中转机场不为同一个 排除
			String firstFlightArrivedAddress = dvo.getFirstFlightArrivedAddress();
			String secondFlightStartFlyAddress = dvo.getSecondFlightStartFlyAddress();
			if(!firstFlightArrivedAddress.equals(secondFlightStartFlyAddress)){
				continue;
			}
			
			//获取中转机场的代码
			String[] zhongzhuanAll = firstFlightArrivedAddress.split(" ");
			
			String zhongzhuancode = "";
			 for (Map.Entry<String, String> entry : codeMap.entrySet()) {
				 	if(zhongzhuanAll.length>1){
				 		
				 		if(zhongzhuanAll[0].equals(entry.getValue())){
				 			zhongzhuancode = entry.getKey();
				 			break;
				 		}else if(zhongzhuanAll[1].indexOf(entry.getValue())>=0){ 
				 			zhongzhuancode = entry.getKey(); 
				 			break; 
				 		}else{
				 			String fuzhacode = zhongzhuanAll[0]+zhongzhuanAll[1].substring(0,2);
				 			if(fuzhacode.equals(entry.getValue())){
				 				zhongzhuancode = entry.getKey();
					 			break;
				 			}
				 		}
				 	}else if(zhongzhuanAll.length==1){
				 		if(zhongzhuanAll[0].equals(entry.getValue())){
				 			zhongzhuancode = entry.getKey();
				 			break;
				 		}
				 	}
				}
			 
			 if(StringUtils.isBlank(zhongzhuancode)){
				 zhongzhuancode="中转code获取失败";
			 }
			
			
			String changwei = firstchangwei+"+"+secondchangwei;
			String hangban = dvo.getFirstFlightCode()+"/"+dvo.getSecondFlightCode();
			String hanglu = dvo.getFrom()+"-"+zhongzhuancode+"-"+dvo.getArrive();
			String jiage = dvo.getFirstFlightLastPrice();
			jiage = jiage.substring(0,jiage.indexOf("."));
			String riqi = dvo.getRiqi();
			String zhongzhuan = dvo.getFirstFlightISTomorrow().indexOf("隔日中转")>=0?"是":"否";
			vo.setChangwei(changwei);
			vo.setHangban(hangban);
			vo.setHanglu(hanglu);
			vo.setJiage(jiage);
			vo.setRiqi(riqi);
			vo.setZhongzhuan(zhongzhuan);
			excelLists.add(vo);
		}
		
		return excelLists;
	}
	
	
	@ResponseBody
	@RequestMapping(value="/getdhtejia/{jsessionid}",method={RequestMethod.GET })
	public Map<String, Object> getdhtejia(@PathVariable String jsessionid){
		
		String url = "";
		String msg = "";
		List<DiscountFlightInfoVo> resultList=  new LinkedList<DiscountFlightInfoVo>();
		try{
			HttpConnectionClient httpClient = new HttpConnectionClient();
			url = "http://781.ceair.com/bookingmanage/booking_bookodAjaxSearch.do?isIT=true";
			NameValuePair[] nvps = {new NameValuePair("routeType", "ODOW"),new NameValuePair("flightOrder", "0"),new NameValuePair("cabinLevel", "\"\""),new NameValuePair("adtCount", "1"),new NameValuePair("kamno", "\"\""),new NameValuePair("retDate", "\"\""),new NameValuePair("depDate", "2016-08-28"),new NameValuePair("arrAirpCd", "LAX"),new NameValuePair("depAirpCd", "PEK"),new NameValuePair("segIndex", "0") };
			//new NameValuePair("segIndex", "0"),new NameValuePair("depAirpCd", "PEK"),new NameValuePair("arrAirpCd", "LAX"),new NameValuePair("depDate", "2016-08-28"),new NameValuePair("retDate", "\"\""),new NameValuePair("kamno", "\"\""),new NameValuePair("adtCount", "1"),new NameValuePair("cabinLevel", "\"\""),new NameValuePair("flightOrder", "0"),new NameValuePair("routeType", "ODOW")
			//new NameValuePair("routeType", "ODOW"),new NameValuePair("flightOrder", "0"),new NameValuePair("cabinLevel", "\"\""),new NameValuePair("adtCount", "1"),new NameValuePair("kamno", "\"\""),new NameValuePair("retDate", "\"\""),new NameValuePair("depDate", "2016-08-28"),new NameValuePair("arrAirpCd", "LAX"),new NameValuePair("depAirpCd", "PEK"),new NameValuePair("segIndex", "0")
			msg= httpClient.getContextByPostMethod2(url,nvps,"JSESSIONID="+jsessionid);
			//System.out.println(msg);
			resultList = parseDOM(msg,"","","");
			
		}catch(Exception e){
			logger.error("通過接口去數據出錯：url"+url+e);
		}
		Map<String,Object> map = new HashMap<String,Object>();  
	    map.put("result", "200");
	    map.put("jsessionid", jsessionid);
	    map.put("data", resultList);
	  
	    return map;
	}
	
	
	
	public List<DiscountFlightInfoVo> parseDOM(String msg,String from, String arrive, String riqi){
		//字符串解析
		List<DiscountFlightInfoVo> resultList=  new LinkedList<DiscountFlightInfoVo>();
		Document doc = Jsoup.parse(msg);
		Elements odsearchresult_boxs = doc.getElementsByClass("odsearchresult_box");
		if(odsearchresult_boxs!=null&&odsearchresult_boxs.size()>0){
			for(int i=0;i<odsearchresult_boxs.size();i++){
				Element e = odsearchresult_boxs.get(i);
				DiscountFlightInfoVo  discInfoVo = new DiscountFlightInfoVo();
				discInfoVo.setFrom(from);
				discInfoVo.setArrive(arrive);
				discInfoVo.setRiqi(riqi);
				//1:获取tr
				Elements trs =  e.getElementsByTag("tr");
				Elements firstTds = null;
				Elements secondTds = null;
				Element firstTr =null;
				Element secondTr =null;
				if(trs!=null&&trs.size()==2){
					firstTr = trs.get(0);
					firstTds = firstTr.getElementsByTag("td");
					secondTr = trs.get(1);
					secondTds = secondTr.getElementsByTag("td");
				}
				
				//2：获取td
				if(firstTds!=null&&firstTds.size()>0){
					//first  1:MU5122 2:H舱  3:333 4:17:55  5:北京 首都机场  6:20:10  7: 上海 虹桥机场   8:隔日中转  9 剩余座位  10票面价： 12 最后价格
						discInfoVo.setFirstFlightCode(firstTds.get(1).text());
						discInfoVo.setFirstFlightSpace(firstTds.get(2).getElementsByTag("span").text());
						discInfoVo.setFirstFlightNum(firstTds.get(3).getElementsByTag("span").text());
						discInfoVo.setFirstFlightStartFlyTime(firstTds.get(4).getElementsByTag("strong").text());
						discInfoVo.setFirstFlightStartFlyAddress(firstTds.get(5).text());
						discInfoVo.setFirstFlightArrivedTime(firstTds.get(6).text());
						discInfoVo.setFirstFlightArrivedAddress(firstTds.get(7).text());
						discInfoVo.setFirstFlightISTomorrow(firstTds.get(8).getElementsByTag("span").get(0).text());
						discInfoVo.setFirstFlightLeftSeat(firstTds.get(9).getElementsByTag("span").get(0).text());
						discInfoVo.setFirstFlightSVGPrice(firstTds.get(10).getElementsByTag("strong").text());
						String priceHtml = firstTds.get(12).getElementsByTag("input").attr("onclick");
						int m = priceHtml.lastIndexOf(")");
						int n = priceHtml.lastIndexOf(",");
						String price = priceHtml.substring(n+2, m-1);
						discInfoVo.setFirstFlightLastPrice(price);
				}
				
				 
				if(secondTds!=null&&secondTds.size()>0){
					//second 1： MU583   2：N舱  3：773 4：13:00  5：上海 浦东机场   6： 10:05   7：洛杉矶 洛杉矶国际机场
						discInfoVo.setSecondFlightCode(secondTds.get(1).text());
						discInfoVo.setSecondFlightSpace(secondTds.get(2).getElementsByTag("span").text());
						discInfoVo.setSecondFlightNum(secondTds.get(3).getElementsByTag("span").text());
						discInfoVo.setSecondFlightStartFlyTime(secondTds.get(4).text());
						discInfoVo.setSecondFlightStartFlyAddress(secondTds.get(5).text());
						discInfoVo.setSecondFlightArrivedTime(secondTds.get(6).getElementsByTag("strong").text());
						discInfoVo.setSecondFlightArrivedAddress(secondTds.get(7).text());
				}
				
				resultList.add(discInfoVo);
			}
		}
		return resultList;
	}
	
	
	@ResponseBody
	@RequestMapping(value="/getdh/{jsessionid}",method={RequestMethod.GET })
	public Map<String, Object> getHqData(@PathVariable String jsessionid){
		
		String url = "";
		String msg = "";
		List<FlightInfoVO> resultList =  new LinkedList<FlightInfoVO>();
		try{
			HttpConnectionClient httpClient = new HttpConnectionClient();
			url = "http://781.ceair.com/bookingmanage/booking_bookAjaxSearch.do?isIT=true";
			NameValuePair[] nvps = { new NameValuePair("segIndex", "0"),new NameValuePair("depAirpCd", "SHA"),new NameValuePair("arrAirpCd", "HND"),new NameValuePair("depDate", "2016-08-28"),new NameValuePair("adtCount", "1"),new NameValuePair("flightOrder", "0")};
			msg= httpClient.getContextByPostMethod(url,nvps,"JSESSIONID="+jsessionid);
			System.out.println(msg);
			//字符串解析
			Document doc = Jsoup.parse(msg);
			Elements searchresult_boxs = doc.getElementsByClass("searchresult_box");
			if(searchresult_boxs!=null&&searchresult_boxs.size()>0){
				for(int i=0;i<searchresult_boxs.size();i++){
					Element e = searchresult_boxs.get(i);
					//1：现在没有从svg图片中获取最低价格，从页面元素中获取最低价格
					String lowestPrice = e.attr("lowestPrice");
					
					//2：获取航班飞行情况 航班 
					Elements lineEles =e.getElementsByClass("searchresult_line");
					Elements linetds = null;
					FlightInfoVO infoVo = new FlightInfoVO();
					if(lineEles!=null&&lineEles.size()>0){
						Element lineEle = lineEles.get(0);
						linetds = lineEle.getElementsByTag("td");
					}
					
					if(linetds!=null&&linetds.size()>0){
						//for(int j=0;j<linetds.size();j++){
							// 1:航班代码  2:燃油费机建费 3:起飞时间 4：起飞地点 5：到站时间 6：到站地点
							infoVo.setFlightCode(linetds.get(1).text());
							infoVo.setFlightFuelPay(linetds.get(2).text());
							infoVo.setFlightStartFlyTime(linetds.get(3).text());
							infoVo.setFlightStartFlyAddress(linetds.get(4).text());
							infoVo.setFlightArrivedTime(linetds.get(5).text());
							infoVo.setFlightArrivedAddress(linetds.get(6).text());
						//}
					}
					
					
					//3：R舱 N舱 剩余座位
					Elements nrEles =e.getElementsByClass("searchresult_nr");
					Elements nrtds = null;
					if(nrEles!=null&&nrEles.size()>0){
						Element nrEle = nrEles.get(0);
						nrtds = nrEle.getElementsByTag("td");
					}
					
					if(nrtds!=null&&nrtds.size()>0){
					//	for(int k=0;k<nrtds.size();k++){
							// 1:R N 航班  2:剩余座位 3:票面价格svg 4：代理返利 5：支付金额 
							infoVo.setFlightRN(nrtds.get(1).text());
							infoVo.setFlightLeftSeat(nrtds.get(2).text());
							infoVo.setFlightPrice(lowestPrice);
							infoVo.setFlightAgentPay(nrtds.get(4).text());
							infoVo.setFlightLastPay(nrtds.get(5).text());
					//	}
					}
					resultList.add(infoVo);
				}
			}
			
			
			
			
			//System.out.println(msg);
		}catch(Exception e){
			logger.error("通過接口去數據出錯：url"+url+e);
		}
		Map<String,Object> map = new HashMap<String,Object>();  
	    map.put("result", "200"); 
	    map.put("data", resultList);
	    return map;
	}
	
	
	@ResponseBody
	@RequestMapping(value="/getdh/{name}",method={RequestMethod.POST })
	public Map<String, Object> getHqDataBody(DhQueryVo vo,@PathVariable String name){
		
		String url = "";
		String msg = "";
		try{
			HttpConnectionClient httpClient = new HttpConnectionClient();
			url = "http://781.ceair.com/bookingmanage/booking_bookAjaxSearch.do?isIT=true&isRet=0";
			NameValuePair[] nvps = { new NameValuePair("segIndex", vo.getSegIndex()),new NameValuePair("depAirpCd", vo.getDepAirpCd()),new NameValuePair("arrAirpCd", vo.getArrAirpCd()),new NameValuePair("depDate", vo.getDepDate()),new NameValuePair("adtCount", vo.getAdtCount()),new NameValuePair("flightOrder", vo.getFlightOrder())};
			msg= httpClient.getContextByPostMethod(url,nvps,"JSESSIONID="+vo.getJsessionid());
		}catch(Exception e){
			logger.error("通過接口去數據出錯：url"+url+e);
		}
		Map<String,Object> map = new HashMap<String,Object>();  
	    map.put("result", "200"); 
	    map.put("data", msg);
	    return map;
	}
	
	
	//点击预订
	@ResponseBody
	@RequestMapping(value="/getdhyd/{jsessionid}",method={RequestMethod.GET })
	public Map<String, Object> getHqDatas(@PathVariable String jsessionid){
		
		String url = "";
		String msg = "";
		try{
			HttpConnectionClient httpClient = new HttpConnectionClient();
			url = "http://781.ceair.com/bookingmanage/booking_bookAjaxCheck.do?orderType=1";
			//NameValuePair[] nvps = { new NameValuePair("segIndex", "0"),new NameValuePair("depAirpCd", "SHA"),new NameValuePair("arrAirpCd", "HND"),new NameValuePair("depDate", "2016-08-27"),new NameValuePair("adtCount", "1"),new NameValuePair("flightOrder", "0")};
			NameValuePair[] nvps = { };
			msg= httpClient.getContextByPostMethod(url,nvps,"JSESSIONID="+jsessionid);
		}catch(Exception e){
			logger.error("通過接口去數據出錯：url"+url+e);
		}
		Map<String,Object> map = new HashMap<String,Object>();  
	    map.put("result", "200"); 
	    map.put("data", msg);
	    return map;
	}
	
	
	@ResponseBody
	@RequestMapping(value="/getPro",method={RequestMethod.POST })
	public Map<String, String>  getPro(HttpServletRequest request,  HttpServletResponse response){
		PropertiesUtil pro =  new PropertiesUtil();
		String prostr = pro.getProperty("fiter_class");
		System.out.println("prop:"+prostr);
		Map<String, String> map = new HashMap<String, String>();
		map.put("prop",prostr);
		return  map;
	}
	
	@ResponseBody
	@RequestMapping(value="/setPro",method={RequestMethod.POST })
	public Map<String, Object> setPro(HttpServletRequest request,  HttpServletResponse response,HQQueryVo vo){
		if (vo == null){
			return null;
		}
		PropertiesUtil pro =  new PropertiesUtil();
		pro.setProper("fiter_class", vo.getFrom());
		Map<String,Object> map = new HashMap<String,Object>();  
		String prostr = pro.getProperty("fiter_class");
		map.put("prop",prostr);
	    return map;
	}
	
}
