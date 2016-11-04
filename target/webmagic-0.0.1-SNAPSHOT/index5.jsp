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
		var jessionid = "56BDBE765D6152B0F4669F5F050E37C0.t7";  
		var data={segIndex:'0',depAirpCd:'SHA',arrAirpCd:'HND',depDate:'2016-08-14',retDate:'',kamno:'',adtCount:'1',cabinLevel:'',flightOrder:'0','jsessionid':jessionid};  
        
	    $.ajax({
            type: "POST",
            url: "./dh/getdh/3333",
            data: data,
            dataType: 'json',
            success: function(result) {
            	debugger;
                alert(result.data);
            }
        });
	});
	</script>
    
    
  </head>
  
  <body>	
  		<h2>你好</h2>
  </body>
</html>
