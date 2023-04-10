<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>$Title$</title>
    <%--http://localhost:8080/AtCrowdFunding02_admin_webui/test/ssm.html--%>
    <base href="http://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/">
    <script type="text/javascript" src="jquery/jquery-2.1.1.min.js"></script>
    <script type="text/javascript" src="layer/layer.js"></script>
    <script type="text/javascript">

        $(function () {
            //准备好要发送服务器端的数据
            var array = [5,7,9];
            //将JSON数组转化为JSON字符串
            var requestBody = JSON.stringify(array);

            $("#btn3").click(function () {
                $.ajax({
                    "url": "send/array/three.html",         //请求目标资源
                    "type": "post",                   //请求方式
                    "data": requestBody,
                    "contentType":"application/json;charset=UTF-8",  //设置请求体的类型
                    "dataType": "text",               //如何对待服务器端返回的数据
                    "success": function (response) { //服务器端处理成功请求后调用的回调函数
                        alert(response);
                    },
                    "error": function (response) { //服务器端处理失败请求后调用的回调函数
                        alert(response);
                    }
                });
            });

            $("#btn2").click(function () {
                $.ajax({
                    "url": "send/array/two.html",         //请求目标资源
                    "type": "post",                   //请求方式
                    "data": {
                        "array[0]": 5,
                        "array[1]": 7,
                        "array[2]": 9
                    },
                    "dataType": "text",               //如何对待服务器端返回的数据
                    "success": function (response) { //服务器端处理成功请求后调用的回调函数
                        alert(response);
                    },
                    "error": function (response) { //服务器端处理失败请求后调用的回调函数
                        alert(response);
                    }
                });
            });
            $("#btn1").click(function () {
                $.ajax({
                    "url": "send/array/one.html",         //请求目标资源
                    "type": "post",                   //请求方式
                    "data": {
                        "array": [5, 7, 9]
                    },
                    "dataType": "text",               //如何对待服务器端返回的数据
                    "success": function (response) { //服务器端处理成功请求后调用的回调函数
                        alert(response);
                    },
                    "error": function (response) { //服务器端处理失败请求后调用的回调函数
                        alert(response);
                    }
                });
            });
            $("#btn4").click(function () {
                layer.msg("layer的弹框");
            });
        });

    </script>
</head>
<body>

<a href="admin/to/login/page.html">管理员登录</a>
<br/>

<a1>Test requestBody</a1>
<br/>
<br/>


<button id="btn1">send [5,7,9] one</button>
<br/>
<br/>

<button id="btn2">send [5,7,9] two</button>
<br/>
<br/>

<button id="btn3">send [5,7,9] three</button>
<br/>
<br/>

<button id="btn4">点我弹框</button>

</body>
</html>
