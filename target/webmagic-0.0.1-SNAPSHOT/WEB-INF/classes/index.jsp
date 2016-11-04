<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE html>
<html>
<head>
	<title>China Eastern Airlines</title>
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
	
<script>
  $(document).ready(function() {
    $("#datepickerstart").datepicker();
    $("#datepickerend").datepicker();
  });
 </script>
</head>
<body>
<h1>China Eastern Airlines</h1>
	<div class="container">
	
		<div class="left w3l">
		<h3>Check Flight</h3>
		<div style="position: relative;left: 45%;margin-bottom: 15px;"> <a style="color:red" target="_blank" href="properties.jsp">航班屏蔽配置</a> </div>
			<div class="register agileits">
				<form action="./dh/posthq" id="myformid" method="post">
					<input type="text" id="startplace" class="location" name="from" placeholder="出发城市:SHA/PEK/CAN" required="">
					<input type="text" id="endplace"  class="location" name="arrive" placeholder="到达城市:LAX/SIN/ICN/DAC" required="">
					<input type="datetime" id="datepickerstart" class="email" name="starttime" placeholder="出发日期（起）" required="">
					<input type="datetime" id="datepickerend" class="name" name="endtime" placeholder="出发日期（止）" required="">
					<input type="text" id="jsid" class="name" name="jsessionid" placeholder="jsessionid=DB04D1F7344CB65EDA351A6058B4E7AC.t7" required="">
					<input type="button"  value="Desperately searching" onclick="submitbtn()">
				</form>
				<form action="./dh/downloads" id="downloads" method="get">
					<input type="button"  value="DownLoad"  onclick="downloads()">
				</form>
			</div>
		</div>
		
		<!-- <div><a href="./dh/downloads">我要下载</a></div> -->
		
		<div class="left w3l" id="showfly"> </div>
		
		<div class="clear"></div>
		
	</div>
	<div class="footer agile">
			<p> &copy; 2016 China Eastern Airlines . All Rights Reserved | Design by zhanghan</p>
		</div>
</body>

<script>
	function submitbtn(){
		var startplace = $("#startplace").val();
		if(!startplace){
			alert("请填写出发地代码");
			return;
		}
		var endplace = $("#endplace").val();
		if(!endplace){
			alert("请填写目的地代码");
			return;
		}
		
		var datepickerstart = $("#datepickerstart").val();
		if(!datepickerstart){
			alert("请填写最早出发时间");
			return;
		}
		
		var datepickerend = $("#datepickerend").val();
		if(!datepickerend){
			alert("请填写最晚出发时间");
			return;
		}
		
		var jsid = $("#jsid").val();
		if(!jsid){
			alert("请填写用户Jsessionid");
			return;
		}
	var data={"from":startplace,"arrive":endplace,"starttime":datepickerstart,"endtime":datepickerend,"jsessionid":jsid};  
	    $.ajax({
            type: "POST",
            url: "./dh/posthq",
            data: data,
            dataType: 'json',
            beforeSend:function () {
                $.fn.jqLoading({ height: 100, width: 240, text: "玩命加载中，请耐心等待...." });
            },
            complete:function(){
            	  $.fn.jqLoading("destroy");
            },
            success: function(result) {
           	 if(result!=null&&result.data!=null&&result.data.length>0){
           		$("#showfly").html("<font color='red' size=6>总记录数:"+result.data.length+"条|耗时:"+result.timer+"秒</font>");
           		$("#jsid").val(result.jsessionid);
        		 for(var i=0;i<result.data.length;i++){
        			 var divshow = document.createElement("div");
        			 $(divshow).css('margin-top','10px'); 
        			 $(divshow).html('<table style="border: 1px dotted black;font-weight: 900;width: 100%;">'
 		 					+'<tr>'
 		 					+'<td>'+result.data[i].hanglu+'</td>'
 		 					+'<td>'+result.data[i].hangban+'</td>'
 		 					+'<td>'+result.data[i].changwei+'</td>'
 		 					+'<td>'+result.data[i].jiage+'</td>'
 		 					+'<td colspan=2>'+result.data[i].riqi+'</td>'
 		 					+'<td>'+result.data[i].zhongzhuan+'</td>'
 		 					+'</tr>'
 		 					+'</table>');
        			 $("#showfly")[0].appendChild(divshow);
        		 }
        		 alert("查询完成，可以下载啦");
        	 }
            }
        });
	}
	
	function downloads(){
		$("#downloads").submit();
		
	}

  
  
</script>


</html>
