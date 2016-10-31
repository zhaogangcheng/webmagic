<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    <title>index.jsp</title>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">    
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
    
    
    <script src="<%=basePath%>/js/jquery/jquery-1.11.3.js"></script>
    <script>
	$(document).ready(function(){
		var data1={value1:'心想事成',value2:'万事如意',value3:'牛牛牛',value4:2009};  
           
	    $.ajax({
            type: "POST",
            url: "getHq",
            data: data1,
            dataType: 'json',
            success: function(result) {
            	debugger;
                alert(result.value1);
            }
        });
	});
	</script>
    
    
  </head>
  
  <body>	
  		<h2>你好</h2>
  </body>
</html>
