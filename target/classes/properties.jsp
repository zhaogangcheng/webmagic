<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
	<link rel="stylesheet" href="styles/style.css">
	<link href='//fonts.googleapis.com/css?family=Yanone+Kaffeesatz:400,700,300,200' rel='stylesheet' type='text/css'>
	<link href='//fonts.googleapis.com/css?family=Open+Sans' rel='stylesheet' type='text/css'>
<!-- 	<link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css" rel="stylesheet" type="text/css"/>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.4/jquery.min.js"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js"></script> -->
    <script src="<%=basePath%>/js/jquery/jquery-1.3.2.min.js"></script>
    <script src="<%=basePath%>/js/jquery/jquery-ui-1.7.2.custom.min.js"></script>
     <script src="<%=basePath%>/js/jquery/jquery-ui-jqLoding.js"></script>
    <link href="<%=basePath%>/js/jquery/jquery-ui-1.7.2.custom.css" rel="stylesheet" type="text/css"/>

	<!-- For-Mobile-Apps-and-Meta-Tags -->
		<meta name="viewport" content="width=device-width, initial-scale=1" />
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta name="keywords" content="Impressive Multiple Form  Responsive, Login Form Web Template, Flat Pricing Tables, Flat Drop-Downs, Sign-Up Web Templates, Flat Web Templates, Login Sign-up Responsive Web Template, Smartphone Compatible Web Template, Free Web Designs for Nokia, Samsung, LG, Sony Ericsson, Motorola Web Design" />
		<script type="application/x-javascript"> addEventListener("load", function() { setTimeout(hideURLbar, 0); }, false); function hideURLbar(){ window.scrollTo(0,1); } </script>
	<!-- //For-Mobile-Apps-and-Meta-Tags -->
	
    <base href="<%=basePath%>">
    <title>航班屏蔽配置</title>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">    
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
    <script src="<%=basePath%>/js/jquery/jquery-1.11.3.js"></script>
    <script>
	$(document).ready(function(){
	    $.ajax({
            type: "POST",
            url: "./dh/getPro",
           // data: data,
            dataType: 'json',
            success: function(result) {
                $("#startplace").val(result.prop); 
            }
        });
	});
	</script>
	
	<script>
	function submitbtn(){
		var startplace = $("#startplace").val();
		if(!startplace){
			alert("请填写需要禁用的航班配置");
			return;
		}
	
	var data={"from":startplace};  
	    $.ajax({
            type: "POST",
            url: "./dh/setPro",
            data: data,
            dataType: 'json',
            success: function(result) {
            	$("#startplace").val(result.prop);
            	alert("修改航班配置成功");
            }
        });
	}
	</script>
    
  </head>
  
  <body>	
  <div class="container">
  		<div class="left w3l">
		<h3>航班屏蔽配置</h3>
			<div class="register agileits">
				<form action="./dh/setPro" id="myformid" method="post">
					<input type="text" id="startplace" class="location" name="from" placeholder="格式:F/C/J/I/Z" required="" style="width: 100%;">
					<input type="button"  value="我要修改" onclick="submitbtn()">
				</form>
			</div>
		</div>
  </div>
  </body>
</html>
