package cn.springmvc.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

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
	
}
