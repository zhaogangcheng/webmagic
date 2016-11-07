package cn.springmvc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import cn.springmvc.controller.MainController;
import cn.springmvc.vo.Book;
import cn.springmvc.vo.ExcelVo;

public class FourExcelZip {
	
	
	public static void ctripExcel(List<ExcelVo> allList,String outputFilePath){
		String file = MainController.class.getClassLoader()
				.getResource("ctrip.xlsx").getPath();
		InputStream ins = null;
		Workbook wb = null;
	
		try {
			ins = new FileInputStream(new File(file));
			wb = WorkbookFactory.create(ins);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if(ins!=null){
				ins.close();	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 3.得到Excel工作表对象
		Sheet sheet = wb.getSheetAt(0);
		// 总行数
		int trLength = sheet.getLastRowNum();
		// 4.得到Excel工作表的行
		Row row = sheet.getRow(0);
		// 总列数
		int tdLength = row.getLastCellNum();
		 
	       for (int i = 0; i < allList.size(); i++)  
           {  
	    	   Row newRow = sheet.createRow((int) i + 1);  
	    	   ExcelVo vo = (ExcelVo) allList.get(i);  
               // 第四步，创建单元格，并设置值  
               //newRow.createCell((short) 0).setCellValue(vo.getChangwei());  //R+T  T+S
               //newRow.createCell((short) 1).setCellValue(vo.getHanglu());  //SHA-KMG-SIN
               //newRow.createCell((short) 2).setCellValue(vo.getHangban());  //MU5804/MU5093
               //newRow.createCell((short) 3).setCellValue(vo.getJiage());  //640
               //newRow.createCell((short) 4).setCellValue(vo.getRiqi());  //2016-11-17~2016-11-17
               //newRow.createCell((short) 5).setCellValue(vo.getZhongzhuan());  //否
	    	   //外部编号
	    	   newRow.createCell((short) 0).setCellValue("");
	    	   //文件编号
	    	   newRow.createCell((short) 1).setCellValue("西南-SGN");
	    	   //产品类型
	    	   newRow.createCell((short) 2).setCellValue("申请");
	    	   //开票航空公司
	    	   newRow.createCell((short) 3).setCellValue("MU");
	    	   
	    	   //录入方式
	    	   newRow.createCell((short) 4).setCellValue("机场");
	    	   
	    	   String hanglu = vo.getHanglu();
	    	   String firsthanglu  = "";
	    	   String middlehanglu  = "";
	    	   String lasthanglu  = "";
	    	   if(StringUtils.isNotBlank(hanglu)&&hanglu.indexOf("-")>0){
	    		  String hanglus[] = hanglu.split("\\-");
	    		  if (hanglus!=null&&hanglus.length==3){
	    			  firsthanglu = hanglus[0];
	    			  middlehanglu =  hanglus[1];
	    			  lasthanglu = hanglus[2];
	    		  }
	    	   }
	    	   
	    	   String hangban = vo.getHangban();
	    	   String firsthangban  = "";
	    	   String secondhangban  = "";
	    	   if(StringUtils.isNotBlank(hangban)&&hangban.indexOf("/")>0){
	    		   firsthangban = hangban.substring(0,hangban.indexOf("/"));
	    		   secondhangban = hangban.substring(hangban.indexOf("/")+1);
	    	   }
	    	   
	    	   //出发地
	    	   newRow.createCell((short) 5).setCellValue(firsthanglu);
	    	   //目的地
	    	   newRow.createCell((short) 6).setCellValue(lasthanglu);
	    	   //航路
	    	   String finalhanglu  = firsthanglu+"-"+firsthangban.substring(0,2)+"-"+middlehanglu+"-"+secondhangban.subSequence(0, 2)+"-"+lasthanglu;
	    	   newRow.createCell((short) 7).setCellValue(finalhanglu);
	    	   
	    	   
	    	   String changwei = vo.getChangwei();
	    	   String firstchangwei = "";
	    	   String secondchangwei = "";
	    	   if(StringUtils.isNotBlank(changwei)&&changwei.indexOf("+")>0){
	    		   String changweis[] = changwei.split("\\+");
	    		   if (changweis!=null&&changweis.length==2){
	    			   firstchangwei = changweis[0];
	    			   secondchangwei = changweis[1];
	    		   }
	    	   }
	    	   //舱位
	    	   newRow.createCell((short) 8).setCellValue(firstchangwei+"-"+secondchangwei);
	    	   
	    	   //票价基础
	    	   newRow.createCell((short) 9).setCellValue("");
	    	   
	    	   //可售航班
	    	   newRow.createCell((short) 10).setCellValue(vo.getHangban());
	    	   
	    	   //禁售航班
	    	   newRow.createCell((short) 11).setCellValue("");
	    	   //是否允许去程中途停留
	    	   newRow.createCell((short) 12).setCellValue("否");
	    	   
	    	   //去程班期
	    	   newRow.createCell((short) 13).setCellValue("1234567");
	    	   
	    	   //去程班期作用点
	    	   newRow.createCell((short) 14).setCellValue("");
	    	   
	    	   
	    	   //去程旅行日期 2016-10-12>2016-12-31
	    	   newRow.createCell((short) 15).setCellValue(vo.getRiqi().replaceAll("\\~", "\\>"));
	    	   //去程旅行日期作用点
	    	   newRow.createCell((short) 16).setCellValue("");
	    	   
	    	   //去程除外旅行日期
	    	   newRow.createCell((short) 17).setCellValue("");
	    	   
	    	   //销售日期
	    	   newRow.createCell((short) 18).setCellValue("");
	    	   //旅客资质
	    	   newRow.createCell((short) 19).setCellValue("普通成人");
	    	   //最小出行人数
	    	   newRow.createCell((short) 20).setCellValue("1");
	    	   //最大出行人数
	    	   newRow.createCell((short) 21).setCellValue("9");
	    	   //运价类型
	    	   newRow.createCell((short) 22).setCellValue("BSP");
	    	   //票种
	    	   newRow.createCell((short) 23).setCellValue("BSP电子票");
	    	   //销售票面价
	    	   newRow.createCell((short) 24).setCellValue(vo.getJiage());
	    	   //币种
	    	   newRow.createCell((short) 25).setCellValue("CNY");
	    	   //儿童价
	    	   newRow.createCell((short) 26).setCellValue("110%");
	    	   //无座婴儿价
	    	   newRow.createCell((short) 27).setCellValue("");
	    	   //返点
	    	   newRow.createCell((short) 28).setCellValue("0");
	    	   //留钱
	    	   newRow.createCell((short) 29).setCellValue("50");
	    	   //出票时限
	    	   newRow.createCell((short) 30).setCellValue("0-365,365,1");
	    	   //是否创建PNR
	    	   newRow.createCell((short) 31).setCellValue("是");
	    	   //报销凭证
	    	   newRow.createCell((short) 32).setCellValue("行程单");
	    	   //适用乘客国籍
	    	   newRow.createCell((short) 33).setCellValue("");
	    	   //除外乘客国籍
	    	   newRow.createCell((short) 34).setCellValue("");
	    	   //乘客年龄
	    	   newRow.createCell((short) 35).setCellValue("");
	    	   //去程可否更改
	    	   newRow.createCell((short) 36).setCellValue("否");
	    	   //去程改期费用
	    	   newRow.createCell((short) 37).setCellValue("");
	    	   //去程改期币种
	    	   newRow.createCell((short) 38).setCellValue("");
	    	   //全部未使用可否退票
	    	   newRow.createCell((short) 39).setCellValue("否");
	    	   //全部未使用退票费用
	    	   newRow.createCell((short) 40).setCellValue("");
	    	   //全部未使用退票币种
	    	   newRow.createCell((short) 41).setCellValue("");
	    	   //是否允许NOSHOW改期
	    	   newRow.createCell((short) 42).setCellValue("否");
	    	   //改期时航班起飞前多久算NOSHOW
	    	   newRow.createCell((short) 43).setCellValue("");
	    	   //去程NOSHOW改期费用
	    	   newRow.createCell((short) 44).setCellValue("");
	    	   //去程NOSHOW改期币种
	    	   newRow.createCell((short) 45).setCellValue("");
	    	   //是否允许NOSHOW退票
	    	   newRow.createCell((short) 46).setCellValue("否");
	    	   //退票时航班起飞前多久算NOSHOW
	    	   newRow.createCell((short) 47).setCellValue("");
	    	   //NOSHOW全部未使用退票费用
	    	   newRow.createCell((short) 48).setCellValue("");
	    	   //NOSHOW全部未使用退票币种
	    	   newRow.createCell((short) 49).setCellValue("");
	    	   //授权OFFICE号
	    	   newRow.createCell((short) 50).setCellValue("HAK166");
	    	   //出票备注
	    	   newRow.createCell((short) 51).setCellValue("");
	    	   //工作时间
	    	   newRow.createCell((short) 52).setCellValue("");
	    	   //去程行李额
	    	   newRow.createCell((short) 53).setCellValue("20公斤");
           }
		// 将修改后的数据保存
		String filePath = outputFilePath;
		OutputStream out;
		try {
			out = new FileOutputStream(filePath);
			wb.write(out);
			if(out!=null){
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	
	
	public static void tongchengExcel(List<ExcelVo> allList,String outputFilePath){
		String file = MainController.class.getClassLoader()
				.getResource("tongcheng.xlsx").getPath();
		InputStream ins = null;
		Workbook wb = null;
	
		try {
			ins = new FileInputStream(new File(file));
			wb = WorkbookFactory.create(ins);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if(ins!=null){
				ins.close();	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 3.得到Excel工作表对象
		Sheet sheet = wb.getSheetAt(0);
		// 总行数
		int trLength = sheet.getLastRowNum();
		// 4.得到Excel工作表的行
		Row row = sheet.getRow(0);
		// 总列数
		int tdLength = row.getLastCellNum();
		 
	       for (int i = 0; i < allList.size(); i++)  
           {  
	    	   Row newRow = sheet.createRow((int) i + 1);  
	    	   ExcelVo vo = (ExcelVo) allList.get(i);  
               // 第四步，创建单元格，并设置值  
               //newRow.createCell((short) 0).setCellValue(vo.getChangwei());  //R+T  T+S
               //newRow.createCell((short) 1).setCellValue(vo.getHanglu());  //SHA-KMG-SIN
               //newRow.createCell((short) 2).setCellValue(vo.getHangban());  //MU5804/MU5093
               //newRow.createCell((short) 3).setCellValue(vo.getJiage());  //640
               //newRow.createCell((short) 4).setCellValue(vo.getRiqi());  //2016-11-17~2016-11-17
               //newRow.createCell((short) 5).setCellValue(vo.getZhongzhuan());  //否
	    	   //运价代码
	    	   newRow.createCell((short) 0).setCellValue("");
	    	   //显示申请
	    	   newRow.createCell((short) 1).setCellValue("");
	    	   
	    	   String hangban = vo.getHangban();
	    	   String firsthangban  = "";
	    	   String secondhangban  = "";
	    	   String quchenghangsi = "";
	    	   if(StringUtils.isNotBlank(hangban)&&hangban.indexOf("/")>0){
	    		   firsthangban = hangban.substring(0,hangban.indexOf("/"));
	    		   secondhangban = hangban.substring(hangban.indexOf("/")+1);
	    	   }
	    	   if(StringUtils.isNotBlank(firsthangban)&&StringUtils.isNotBlank(secondhangban)){
	    		   if(firsthangban.substring(0,2).equals(secondhangban.substring(0,2))&&firsthangban.substring(0,2).equals("FM")){
	    			   quchenghangsi = "FM";
	    		   }else{
	    			   quchenghangsi = "MU";
	    		   }
	    	   }
	    	   //去程航司
	    	   newRow.createCell((short) 2).setCellValue(quchenghangsi);
	    	   
	    	   String hanglu = vo.getHanglu();
	    	   String firsthanglu  = "";
	    	   String middlehanglu  = "";
	    	   String lasthanglu  = "";
	    	   if(StringUtils.isNotBlank(hanglu)&&hanglu.indexOf("-")>0){
	    		  String hanglus[] = hanglu.split("\\-");
	    		  if (hanglus!=null&&hanglus.length==3){
	    			  firsthanglu = hanglus[0];
	    			  middlehanglu =  hanglus[1];
	    			  lasthanglu = hanglus[2];
	    		  }
	    	   }
	    	   //起飞机场
	    	   newRow.createCell((short) 3).setCellValue(firsthanglu);
	    	   
	    	   //中转机场
	    	   newRow.createCell((short) 4).setCellValue(middlehanglu);
	    	   //降落机场
	    	   newRow.createCell((short) 5).setCellValue(lasthanglu);
	    	   
	      	   
	    	   String changwei = vo.getChangwei();
	    	   String firstchangwei = "";
	    	   String secondchangwei = "";
	    	   if(StringUtils.isNotBlank(changwei)&&changwei.indexOf("+")>0){
	    		   String changweis[] = changwei.split("\\+");
	    		   if (changweis!=null&&changweis.length==2){
	    			   firstchangwei = changweis[0];
	    			   secondchangwei = changweis[1];
	    		   }
	    	   }
	    	   //去程舱位
	    	   newRow.createCell((short) 6).setCellValue(firstchangwei+"-"+secondchangwei);
	    	   //去程适用航班
	    	   newRow.createCell((short) 7).setCellValue(vo.getHangban());
	    	   //去程禁售航班
	    	   newRow.createCell((short) 8).setCellValue("");
	  
	    	   //去程班期
	    	   newRow.createCell((short) 9).setCellValue("1,2,3,4,5,6,7");
	    	   
	    	   //去程旅行日期
	    	   newRow.createCell((short) 10).setCellValue(vo.getRiqi());
	    	   //去程排除旅行日期
	    	   newRow.createCell((short) 11).setCellValue("");
	    	   //销售日期
	    	   newRow.createCell((short) 12).setCellValue(vo.getRiqi());
	    	   
	    	   //乘客类型
	    	   newRow.createCell((short) 13).setCellValue("成人");
	    	   
	    	   //是否创建PNR
	    	   newRow.createCell((short) 14).setCellValue("");
	    	   
	    	   //定舱见舱
	    	   newRow.createCell((short) 15).setCellValue("");
	    	   //座位数是否限制
	    	   newRow.createCell((short) 16).setCellValue("");
	    	   
	    	   //成人价格
	    	   String jiage = vo.getJiage();
	    	   String chengrenjiage = String.valueOf(Integer.parseInt(jiage)+50);
	    	   newRow.createCell((short) 17).setCellValue(chengrenjiage);
	    	   
	    	   //儿童价格
	    	   String ertongjiage = String.valueOf(Integer.parseInt(jiage)+200);
	    	   newRow.createCell((short) 18).setCellValue(ertongjiage);
	    	   
	    	   //退票规定
	    	   newRow.createCell((short) 19).setCellValue("不得退票");
	    	   //改期规定
	    	   newRow.createCell((short) 20).setCellValue("不得改期");
	    	   //行李额规定
	    	   newRow.createCell((short) 21).setCellValue("20KG");
	    	   //其他说明
	    	   newRow.createCell((short) 22).setCellValue("");
	    	   //运价备注
	    	   newRow.createCell((short) 23).setCellValue(jiage);
	    	   //提前出票时限
	    	   newRow.createCell((short) 24).setCellValue("");
	    	   //最晚出票时限
	    	   newRow.createCell((short) 25).setCellValue("");
	    	   
	    	   //最少N人起订
	    	   newRow.createCell((short) 26).setCellValue("");
	    	   //是否支持共享航班
	    	   newRow.createCell((short) 27).setCellValue("否");
           } 
		// 将修改后的数据保存
		String filePath = outputFilePath;
		OutputStream out;
		try {
			out = new FileOutputStream(filePath);
			wb.write(out);
			if(out!=null){
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	
	public static void taobaoExcel(List<ExcelVo> allList,String outputFilePath){
		String file = MainController.class.getClassLoader()
				.getResource("taobao.xlsx").getPath();
		InputStream ins = null;
		Workbook wb = null;
	
		try {
			ins = new FileInputStream(new File(file));
			wb = WorkbookFactory.create(ins);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if(ins!=null){
				ins.close();	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 3.得到Excel工作表对象
		Sheet sheet = wb.getSheetAt(0);
		// 总行数
		int trLength = sheet.getLastRowNum();
		// 4.得到Excel工作表的行
		Row row = sheet.getRow(0);
		// 总列数
		int tdLength = row.getLastCellNum();
		 
	       for (int i = 0; i < allList.size(); i++)  
           {  
	    	   Row newRow = sheet.createRow((int) i + 1);  
	    	   ExcelVo vo = (ExcelVo) allList.get(i);  
               // 第四步，创建单元格，并设置值  
               //newRow.createCell((short) 0).setCellValue(vo.getChangwei());  //R+T  T+S
               //newRow.createCell((short) 1).setCellValue(vo.getHanglu());  //SHA-KMG-SIN
               //newRow.createCell((short) 2).setCellValue(vo.getHangban());  //MU5804/MU5093
               //newRow.createCell((short) 3).setCellValue(vo.getJiage());  //640
               //newRow.createCell((short) 4).setCellValue(vo.getRiqi());  //2016-11-17~2016-11-17
               //newRow.createCell((short) 5).setCellValue(vo.getZhongzhuan());  //否
	    	   //外部政策ID
	    	   newRow.createCell((short) 0).setCellValue("");
	    	   //文件编号
	    	   newRow.createCell((short) 1).setCellValue("MU");
	    	   //出票航司
	    	   newRow.createCell((short) 2).setCellValue("MU");
	    	   
	    	   String hangban = vo.getHangban();
	    	   String firsthangban  = "";
	    	   String secondhangban  = "";
	    	   if(StringUtils.isNotBlank(hangban)&&hangban.indexOf("/")>0){
	    		   firsthangban = hangban.substring(0,hangban.indexOf("/"));
	    		   secondhangban = hangban.substring(hangban.indexOf("/")+1);
	    	   }
	    	   
	    	   //销售航司
	    	   newRow.createCell((short) 3).setCellValue(firsthangban.substring(0,2)+","+secondhangban.substring(0,2));
	    	   
	    	   //出发/目的地维护方式
	    	   newRow.createCell((short) 4).setCellValue("机场");
	    	   //航程种类
	    	   newRow.createCell((short) 5).setCellValue("中转");
	    	   
	    	   String hanglu = vo.getHanglu();
	    	   String firsthanglu  = "";
	    	   String middlehanglu  = "";
	    	   String lasthanglu  = "";
	    	   if(StringUtils.isNotBlank(hanglu)&&hanglu.indexOf("-")>0){
	    		  String hanglus[] = hanglu.split("\\-");
	    		  if (hanglus!=null&&hanglus.length==3){
	    			  firsthanglu = hanglus[0];
	    			  middlehanglu =  hanglus[1];
	    			  lasthanglu = hanglus[2];
	    		  }
	    	   }
	    	   //始发地
	    	   newRow.createCell((short) 6).setCellValue(firsthanglu);
	    	   //目的地
	    	   newRow.createCell((short) 7).setCellValue(lasthanglu);
	    	   //中转地
	    	   newRow.createCell((short) 8).setCellValue(middlehanglu);
	    	   
	    	   String changwei = vo.getChangwei();
	    	   String firstchangwei = "";
	    	   String secondchangwei = "";
	    	   if(StringUtils.isNotBlank(changwei)&&changwei.indexOf("+")>0){
	    		   String changweis[] = changwei.split("\\+");
	    		   if (changweis!=null&&changweis.length==2){
	    			   firstchangwei = changweis[0];
	    			   secondchangwei = changweis[1];
	    		   }
	    	   }
	    	   //舱位
	    	   newRow.createCell((short) 9).setCellValue(firstchangwei+","+secondchangwei);
	    	   
	    	   //适用航班号
	    	   newRow.createCell((short) 10).setCellValue(vo.getHangban());
	    	   //排除航班号
	    	   newRow.createCell((short) 11).setCellValue("");
	    	   //去程旅行有效期
	    	   newRow.createCell((short) 12).setCellValue(vo.getRiqi());
	    	   
	    	   //去程旅行排除时间段
	    	   newRow.createCell((short) 13).setCellValue("");
	    	   
	    	   //去程旅行日期作用点
	    	   newRow.createCell((short) 14).setCellValue("全部");
	    	   
	    	   //去程旅行排除日期作用点
	    	   newRow.createCell((short) 15).setCellValue("全部");
	    	   //去程班期限制
	    	   newRow.createCell((short) 16).setCellValue("1234567");
	    	   
	    	   //去程班期作用点
	    	   newRow.createCell((short) 17).setCellValue("全部");
	    	   
	    	   //销售日期
	    	   newRow.createCell((short) 18).setCellValue(vo.getRiqi());
	    	   //成人旅客身份
	    	   newRow.createCell((short) 19).setCellValue("普通");
	    	   //最小出行人数
	    	   newRow.createCell((short) 20).setCellValue("");
	    	   //最大出行人数
	    	   //String ertongpiaojia = String.valueOf(Integer.parseInt(jiage)+200);
	    	   newRow.createCell((short) 21).setCellValue("");
	    	   String jiage = vo.getJiage();
	    	   //销售票面价
	    	   newRow.createCell((short) 22).setCellValue(jiage);
	    	   
	    	   //儿童价
	    	   newRow.createCell((short) 23).setCellValue("120%");
	    	   //返点
	    	   newRow.createCell((short) 24).setCellValue("0.0");
	    	   //留钱
	    	   newRow.createCell((short) 25).setCellValue("50");
	    	   //提前出票时限
	    	   newRow.createCell((short) 26).setCellValue("");
	    	   //最晚出票时限
	    	   newRow.createCell((short) 27).setCellValue("");
	    	   //大客户编码
	    	   newRow.createCell((short) 28).setCellValue("");
	    	   //预订office
	    	   newRow.createCell((short) 29).setCellValue("HAK166");
	    	   //是否校验票面价
	    	   newRow.createCell((short) 30).setCellValue("否");
	    	   //退票规定
	    	   newRow.createCell((short) 31).setCellValue("不可退票;");
	    	   //改期规定
	    	   newRow.createCell((short) 32).setCellValue("不允许改期;");
	    	   //误机罚金说明
	    	   newRow.createCell((short) 33).setCellValue("无");
	    	   //行李额规定
	    	   newRow.createCell((short) 34).setCellValue("20KG");
	    	   //备注
	    	   newRow.createCell((short) 35).setCellValue("");
	    	   //商品类型
	    	   newRow.createCell((short) 36).setCellValue("普通");
	    	   //运价渠道
	    	   newRow.createCell((short) 37).setCellValue("listing");
	    	   //工作时间
	    	   newRow.createCell((short) 38).setCellValue("");
	    	   //代码共享适用类型
	    	   newRow.createCell((short) 39).setCellValue("不限制");
	    	   //购票须知
	    	   newRow.createCell((short) 40).setCellValue("");
	    	   //全部未使用可否退票
	    	   newRow.createCell((short) 41).setCellValue("否");
	    	   //全部未使用退票费用
	    	   newRow.createCell((short) 42).setCellValue("");
	    	   //全部未使用退票币种
	    	   newRow.createCell((short) 43).setCellValue("");
	    	   //全部未使用退票费用收取方式
	    	   newRow.createCell((short) 44).setCellValue("");
	    	   //部分未使用可否退票
	    	   newRow.createCell((short) 45).setCellValue("否");
	    	   //部分未使用退票费用
	    	   newRow.createCell((short) 46).setCellValue("");
	    	   //部分未使用退票币种
	    	   newRow.createCell((short) 47).setCellValue("");
	    	   //部分未使用退票费用收取方式
	    	   newRow.createCell((short) 48).setCellValue("");
	    	   //去程可否改期
	    	   newRow.createCell((short) 49).setCellValue("否");
	    	   //去程改期费用
	    	   newRow.createCell((short) 50).setCellValue("");
	    	   //去程改期币种
	    	   newRow.createCell((short) 51).setCellValue("");
	    	   //去程改期费用收取方式
	    	   newRow.createCell((short) 52).setCellValue("");
	    	   //NOSHOW是否有限制
	    	   newRow.createCell((short) 53).setCellValue("否");
	    	   //NOSHOW时限
	    	   newRow.createCell((short) 54).setCellValue("");
	    	   //NOSHOW时限单位
	    	   newRow.createCell((short) 55).setCellValue("");
	    	   //NOSHOW规则
	    	   newRow.createCell((short) 56).setCellValue("");
	    	   //NOSHOW金额
	    	   newRow.createCell((short) 57).setCellValue("");
	    	   //NOSHOW币种
	    	   newRow.createCell((short) 58).setCellValue("");
	    	   //小团儿童计数规则
	    	   newRow.createCell((short) 59).setCellValue("1个儿童计1个成人");
	    	   //国籍
	    	   newRow.createCell((short) 60).setCellValue("");
	    	   //除外国籍
	    	   newRow.createCell((short) 61).setCellValue("");
	    	   //年龄限制
	    	   newRow.createCell((short) 62).setCellValue("");
           } 
		// 将修改后的数据保存
		String filePath = outputFilePath;
		OutputStream out;
		try {
			out = new FileOutputStream(filePath);
			wb.write(out);
			if(out!=null){
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	
	
	public static void qunaerExcel(List<ExcelVo> allList,String outputFilePath){
		String file = MainController.class.getClassLoader()
				.getResource("qunar.xlsx").getPath();
		InputStream ins = null;
		Workbook wb = null;
	
		try {
			ins = new FileInputStream(new File(file));
			wb = WorkbookFactory.create(ins);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if(ins!=null){
				ins.close();	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 3.得到Excel工作表对象
		Sheet sheet = wb.getSheetAt(0);
		// 总行数
		int trLength = sheet.getLastRowNum();
		// 4.得到Excel工作表的行
		Row row = sheet.getRow(0);
		// 总列数
		int tdLength = row.getLastCellNum();
		 
	       for (int i = 0; i < allList.size(); i++)  
           {  
	    	   Row newRow = sheet.createRow((int) i + 1);  
	    	   ExcelVo vo = (ExcelVo) allList.get(i);  
               // 第四步，创建单元格，并设置值  
               //newRow.createCell((short) 0).setCellValue(vo.getChangwei());  //R+T  T+S
               //newRow.createCell((short) 1).setCellValue(vo.getHanglu());  //SHA-KMG-SIN
               //newRow.createCell((short) 2).setCellValue(vo.getHangban());  //MU5804/MU5093
               //newRow.createCell((short) 3).setCellValue(vo.getJiage());  //640
               //newRow.createCell((short) 4).setCellValue(vo.getRiqi());  //2016-11-17~2016-11-17
               //newRow.createCell((short) 5).setCellValue(vo.getZhongzhuan());  //否
	    	   String hangban = vo.getHangban();
	    	   String firsthangban  = "";
	    	   String secondhangban  = "";
	    	   if(StringUtils.isNotBlank(hangban)&&hangban.indexOf("/")>0){
	    		   firsthangban = hangban.substring(0,hangban.indexOf("/"));
	    		   secondhangban = hangban.substring(hangban.indexOf("/")+1);
	    	   }
	    	   //文件编号
	    	   newRow.createCell((short) 0).setCellValue("");
	    	   //外部政策ID
	    	   newRow.createCell((short) 1).setCellValue("");
	    	   //第一程航空公司
	    	   newRow.createCell((short) 2).setCellValue(firsthangban.substring(0,2));
	    	   //第二程航空公司
	    	   newRow.createCell((short) 3).setCellValue(secondhangban.substring(0,2));
	    	   
	    	   String hanglu = vo.getHanglu();
	    	   String firsthanglu  = "";
	    	   String middlehanglu  = "";
	    	   String lasthanglu  = "";
	    	   if(StringUtils.isNotBlank(hanglu)&&hanglu.indexOf("-")>0){
	    		  String hanglus[] = hanglu.split("\\-");
	    		  if (hanglus!=null&&hanglus.length==3){
	    			  firsthanglu = hanglus[0];
	    			  middlehanglu =  hanglus[1];
	    			  lasthanglu = hanglus[2];
	    		  }
	    	   }
	    	   
	    	   //出票航空公司
	    	   newRow.createCell((short) 4).setCellValue("1");
	    	   //起飞机场
	    	   newRow.createCell((short) 5).setCellValue(firsthanglu);
	    	   //中转机场
	    	   newRow.createCell((short) 6).setCellValue(middlehanglu);
	    	   //到达机场
	    	   newRow.createCell((short) 7).setCellValue(lasthanglu);
	    	   //第一程适用航班类型
	    	   newRow.createCell((short) 8).setCellValue("适用");
	    	   //第一程航班号
	    	   newRow.createCell((short) 9).setCellValue(firsthangban);
	    	   //第二程适用航班类型
	    	   newRow.createCell((short) 10).setCellValue("适用");
	    	   //第二程航班号
	    	   newRow.createCell((short) 11).setCellValue(secondhangban);
	    	   
	    	   String riqi = vo.getRiqi();
	    	   String startdate = "";
	    	   String enddate = "";
	    	   if(StringUtils.isNotBlank(riqi)&&riqi.indexOf("~")>0){
	    		   String riqis[] = riqi.split("\\~");
	    		   if (riqis!=null&&riqis.length==2){
	    			   startdate = riqis[0];
	    			   enddate = riqis[1];
	    		   }
	    	   }
	    	   //旅行开始日期
	    	   newRow.createCell((short) 12).setCellValue(startdate);
	    	   //旅行结束日期
	    	   newRow.createCell((short) 13).setCellValue(enddate);
	    	   //旅行排除日期
	    	   newRow.createCell((short) 14).setCellValue("");
	    	   //销售开始日期
	    	   newRow.createCell((short) 15).setCellValue("");
	    	   //销售结束日期
	    	   newRow.createCell((short) 16).setCellValue("");
	    	   //适用班期 1,2,3,4,5,6,7
	    	   newRow.createCell((short) 17).setCellValue("1,2,3,4,5,6,7");
	    	   
	    	   String changwei = vo.getChangwei();
	    	   String firstchangwei = "";
	    	   String secondchangwei = "";
	    	   if(StringUtils.isNotBlank(changwei)&&changwei.indexOf("+")>0){
	    		   String changweis[] = changwei.split("\\+");
	    		   if (changweis!=null&&changweis.length==2){
	    			   firstchangwei = changweis[0];
	    			   secondchangwei = changweis[1];
	    		   }
	    	   }
	    	   //第一程适用舱位
	    	   newRow.createCell((short) 18).setCellValue(firstchangwei);
	    	   //第二程适用舱位
	    	   newRow.createCell((short) 19).setCellValue(secondchangwei);
	    	   
	    	   //乘客类型
	    	   newRow.createCell((short) 20).setCellValue("普通成人");
	    	   
	    	   String jiage = vo.getJiage();
	    	   //销售票面价
	    	   newRow.createCell((short) 21).setCellValue(jiage);
	    	   //是否为XSFSD运价
	    	   newRow.createCell((short) 22).setCellValue("否");
	    	   //是否为含税总价
	    	   newRow.createCell((short) 23).setCellValue("否");
	    	   //儿童售票
	    	   String ertongpiaojia = String.valueOf(Integer.parseInt(jiage)+200);
	    	   newRow.createCell((short) 24).setCellValue(ertongpiaojia);
	    	   //返点
	    	   newRow.createCell((short) 25).setCellValue("");
	    	   //留钱
	    	   newRow.createCell((short) 26).setCellValue("50");
	    	   //提前出票时限
	    	   newRow.createCell((short) 27).setCellValue("");
	    	   //最晚出票时限
	    	   newRow.createCell((short) 28).setCellValue("");
	    	   //仅限联订
	    	   newRow.createCell((short) 29).setCellValue("否");
	    	   //是否校验AV状态
	    	   newRow.createCell((short) 30).setCellValue("是");
	    	   //是否创建PNR
	    	   newRow.createCell((short) 31).setCellValue("是");
	    	   //是否校验票面价
	    	   newRow.createCell((short) 32).setCellValue("否");
	    	   //是否支持共享航班
	    	   newRow.createCell((short) 33).setCellValue("是");
	    	   //是否显示为申请
	    	   newRow.createCell((short) 34).setCellValue("否");
	    	   //可售座位数
	    	   newRow.createCell((short) 35).setCellValue("");
	    	   //机票成本价
	    	   newRow.createCell((short) 36).setCellValue("");
	    	   String shuoming = "^-^一、不允许退票机票税费无法退还,签证须自理，若因签证等原因产生的损失自行承担，签证问题请咨询领馆或机场出入境。二、请确认好信息与原件一致(姓名、国籍、出生日期、性别、证件号码、有效期、签发地)。三、如无法搭乘当次航班须在航班起飞24小时前通知取消订票记录，否则视为误机，误机改期除了改期费还需额外支付误机费。四、改期受理时间：周一至周五9：00-17：00。五、该机票不提供报销凭证。六、特价机票最晚航班起飞前1个工作日出好，请耐心等待。一经支付则表示同意我司退改签等相关规定，未尽事宜,以我司规定为准。";
	    	   //其他说明
	    	   newRow.createCell((short) 37).setCellValue(shuoming);
	    	   //政策备注
	    	   newRow.createCell((short) 38).setCellValue("");
	    	   //退票规定
	    	   newRow.createCell((short) 39).setCellValue("*");
	    	   //部分退票费用
	    	   newRow.createCell((short) 40).setCellValue("*");
	    	   //改期规定
	    	   newRow.createCell((short) 41).setCellValue("*");
	    	   //部分改期费用
	    	   newRow.createCell((short) 42).setCellValue("*");
	    	   //是否允许签转
	    	   newRow.createCell((short) 43).setCellValue("否");
	    	   //NOSHOW规定时限
	    	   newRow.createCell((short) 44).setCellValue("");
	    	   //NOSHOW能否退票
	    	   newRow.createCell((short) 45).setCellValue("否");
	    	   //NOSHOW能否改期
	    	   newRow.createCell((short) 46).setCellValue("否");
	    	   //NOSHOW罚金
	    	   newRow.createCell((short) 47).setCellValue("");
	    	   //行李额规定
	    	   newRow.createCell((short) 48).setCellValue("-;-");
	    	   //乘客国籍适用类型
	    	   newRow.createCell((short) 49).setCellValue("全部");
	    	   //乘客国籍
	    	   newRow.createCell((short) 50).setCellValue("");
	    	   //乘客年龄
	    	   newRow.createCell((short) 51).setCellValue("");
	    	   //报销凭证类型
	    	   newRow.createCell((short) 52).setCellValue("");
	    	   //GDS来源
	    	   newRow.createCell((short) 53).setCellValue("");
	    	   //报销凭证类型
	    	   newRow.createCell((short) 54).setCellValue("10080m");
           } 
		// 将修改后的数据保存
		String filePath = outputFilePath;
		OutputStream out;
		try {
			out = new FileOutputStream(filePath);
			wb.write(out);
			if(out!=null){
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
		
}
