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

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import cn.springmvc.controller.MainController;
import cn.springmvc.vo.Book;
import cn.springmvc.vo.ExcelVo;

public class FourExcelZip {
	
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
               newRow.createCell((short) 0).setCellValue(vo.getChangwei());  
               newRow.createCell((short) 1).setCellValue(vo.getHanglu());  
               newRow.createCell((short) 2).setCellValue(vo.getHangban());  
               newRow.createCell((short) 3).setCellValue(vo.getJiage());  
               newRow.createCell((short) 4).setCellValue(vo.getRiqi());  
               newRow.createCell((short) 5).setCellValue(vo.getZhongzhuan());  
           } 
		
		
		
		
/*		// 5.得到Excel工作表指定行的单元格
		Cell cell = row.getCell((short) 1);
		// 6.得到单元格样式
		CellStyle cellStyle = cell.getCellStyle();*/

		/*for (int i = 0; i < trLength; i++) {
			// 得到Excel工作表的行
			Row row1 = sheet.getRow(i);
			for (int j = 0; j < tdLength; j++) {
				// 得到Excel工作表指定行的单元格
				Cell cell1 = row1.getCell(j);
				*//**  
				 * 为了处理：Excel异常Cannot get a text value from a numeric cell
				 * 将所有列中的内容都设置成String类型格式
				 *//*
				if (cell1 != null) {
					cell1.setCellType(Cell.CELL_TYPE_STRING);
				}

				if (j == 5 && i <= 10) {
					cell1.setCellValue("1000");
				}
				

				// 获得每一列中的值
				System.out.println("cell显示"+j+":"+cell1);
			}
		}*/
		// 将修改后的数据保存
		String filePath = outputFilePath+"qunar.xlsx";
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
