package cn.springmvc.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.springmvc.vo.Book;
import cn.springmvc.vo.Votest;

@Controller
@RequestMapping("/")
public class MainController {

	@RequestMapping("index1")
	public String index(){
		System.out.println("我到controller了======"+111);
		return "index";
	}
	
	@ResponseBody
	@RequestMapping(value="getHq",method={RequestMethod.POST })
	public Map<String, Object> getHqData(Votest votest){
		System.out.println("我到controller了getHqData()======"+111);
		Map<String,Object> map = new HashMap<String,Object>();  
	    map.put("result", "11"); 
	    map.put("data", votest);
	    return map;
	}
	
	@ResponseBody
	@RequestMapping(value="getHq2/{id}/{name}",method={RequestMethod.GET })
	public Map<String, Object> getHqData2(@PathVariable String id,@PathVariable String name){
		System.out.println("我到controller了getHqData()======"+111);
		Map<String,Object> map = new HashMap<String,Object>();  
	    map.put("result", id); 
	    map.put("data", name);
	    return map;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		String file = MainController.class.getClassLoader()
				.getResource("ctrip.xlsx").getPath();
		System.out.println(file);

		// excel模板路径
		//Workbook book = new XSSFWorkbook(new FileInputStream(file));
		InputStream ins = null;
		Workbook wb = null;
		ins = new FileInputStream(new File(file));
		try {
			wb = WorkbookFactory.create(ins);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
		ins.close();

		// 3.得到Excel工作表对象
		Sheet sheet = wb.getSheetAt(0);
		// 总行数
		int trLength = sheet.getLastRowNum();
		// 4.得到Excel工作表的行
		Row row = sheet.getRow(0);
		// 总列数
		int tdLength = row.getLastCellNum();
		
		List<Book> list = new ArrayList<Book>();
		Book b =  new Book();
		b.setAuthor("撒旦阿萨德");
		Book b1 =  new Book();
		b1.setAuthor("撒旦阿萨德ddddd");
		list.add(b1);
		list.add(b);
		
	       for (int i = 0; i < list.size(); i++)  
           {  
	    	   Row newRow = sheet.createRow((int) i + 1);  
               Book book = (Book) list.get(i);  
               // 第四步，创建单元格，并设置值  
               newRow.createCell((short) 0).setCellValue(i+book.getAuthor());  
               newRow.createCell((short) 1).setCellValue(i+book.getAuthor());  
               newRow.createCell((short) 2).setCellValue(i+book.getAuthor());  
               newRow.createCell((short) 3).setCellValue(i+book.getAuthor());  
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
		String filePath = "D:\\zhanghan\\dhDownload\\excel\\ctrip.xlsx";
		OutputStream out = new FileOutputStream(filePath);
		wb.write(out);
		if(out!=null)
		out.close();
	}
	
}
