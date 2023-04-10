<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="zh-CN">
<%@include file="/WEB-INF/include-head.jsp" %>
<link rel="stylesheet" href="css/pagination.css"/>
<script type="text/javascript" src="jquery/jquery.pagination.js"></script>
<script type="text/javascript">
    $(function () {
        //调用函数对页面导航条进行初始化
        initPagination();

        // 全选和全不选的反向操作
        $("#adminPageBody").on("click", ".itemBox", function () {

            // 获取当前已经选中的itemBox数量
            var checkedBoxCount = $(".itemBox:checked").length;

            //获取全部itemBox的数量
            var totalBoxCount = $(".itemBox").length;

            $("#summaryBox").prop("checked", checkedBoxCount == totalBoxCount);

        });

        // 给summaryBox按钮绑定单击响应函数
        $("#summaryBox").click(function () {

            //获取当前多选框的状态
            var currentStatus = this.checked;

            //用当前多选框的状态去设置其他多选框
            $(".itemBox").prop("checked", currentStatus);
        });

        //给批量删除按钮绑定单击响应函数
        $("#AdminBatchRemoveBtn").click(function () {


            var adminArray = [];

            //遍历选中的多选框
            $(".itemBox:checked").each(function () {

                //使用this引用当前遍历的多选框
                var adminId = this.id;

                //通过DOM操作获取名称
                var adminName = $(this).parent().next().text();

                adminArray.push({
                    "adminId": adminId,
                    "adminName": adminName
                });

                // 检查roleArray的长度
                if (adminArray.length == 0) {
                    layer.msg("请至少选中一个执行删除");
                    return;
                }

                //调用专门的函数
                showAdminConfirmModal(adminArray);

            });

        });


        // 点击删除模态框中确认删除的按钮执行删除
        $("#removeAdminBtn").click(function () {

            var requestBody = JSON.stringify(window.adminIdArray);

            $.ajax({
                "url": "admin/remove/by/admin/id/array.json",
                "type": "post",
                "data": requestBody,
                "contentType": "application/json;charset=UTF-8",
                "dataType": "json",
                "success": function (response) {
                    var result = response.result;

                    if (result == "SUCCESS") {
                        layer.msg("删除成功！")
                        //重新加载分页
                        generatePage();
                    }
                    if (result == "FAILED") {
                        layer.msg("删除失败！" + response.message)
                    }
                },
                "error": function (response) {
                    layer.msg(response.status + " " + response.statusText);
                }
            });

            //关闭模态框
            $("#adminConfirmModal").modal("hide");

            //清除之前多选框的勾选
            $("#summaryBox").prop("checked", false);

        });
    });

    function initPagination() {
        //获取总记录数
        var totalRecord = ${requestScope.pageInfo.total};

        //声明一个JSON对象存储Pagination要设置的属性
        var properties = {
            num_edge_entries: 3, //边缘页数
            num_display_entries: 5, //主体页数
            callback: pageSelectCallback,
            items_per_page:${requestScope.pageInfo.pageSize}, //每页显示项
            current_page:${requestScope.pageInfo.pageNum - 1}, //
            prev_text: "上一页",
            next_text: "下一页"
        };

        //生成页码横条
        $("#Pagination").pagination(totalRecord, properties);
    }

    function pageSelectCallback(pageIndex, jQuery) {
        //根据pageIndex计算pageNum
        var pageNum = pageIndex + 1;

        //跳转页面
        window.location.href = "admin/get/page.html?pageNum=" + pageNum +"&keyword=${param.keyword}";

        //
        return false;
    }

    // 声明专门的函数显示删除模态框
    function showAdminConfirmModal(adminArray) {

        // 打开模态框
        $("#adminConfirmModal").modal("show");

        // 清除旧数据
        $("#adminNameDiv").empty();

        // 全局变量的数组存储角色id
        window.adminIdArray = [];

        //
        for (var i = 0; i < adminArray.length; i++) {
            var admin = adminArray[i];
            var adminName = admin.adminArrayName;
            $("#adminNameDiv").append(adminName + "<br/>");

            var adminId = admin.adminId;

            window.adminIdArray.push(adminId);
        }


    }


</script>


<body>

<%@include file="/WEB-INF/include-nav.jsp" %>
<div class="container-fluid">
    <div class="row">
        <%@include file="/WEB-INF/include-sidebar.jsp" %>
        <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title"><i class="glyphicon glyphicon-th"></i> 数据列表</h3>
                </div>
                <div class="panel-body">
                    <form action="admin/get/page.html" method="post" class="form-inline" role="form" style="float:left;">
                        <div class="form-group has-feedback">
                            <div class="input-group">
                                <div class="input-group-addon">查询条件</div>
                                <input name="keyword" class="form-control has-success" type="text" placeholder="请输入查询条件">
                            </div>
                        </div>
                        <button type="submit" class="btn btn-warning"><i class="glyphicon glyphicon-search"></i> 查询
                        </button>
                    </form>
                    <button type="button" id="AdminBatchRemoveBtn" class="btn btn-danger" style="float:right;margin-left:10px;"><i
                            class=" glyphicon glyphicon-remove"></i> 删除
                    </button>

                    <a href="admin/to/add/page.html" class="btn btn-primary" style="float:right;"><i class="glyphicon glyphicon-plus"></i> 新增</a>
                    <br>
                    <hr style="clear:both;">
                    <div class="table-responsive">
                        <table class="table  table-bordered">
                            <thead>
                            <tr>
                                <th width="30">#</th>
                                <th width="30"><input id="summaryBox" type="checkbox"></th>
                                <th>账号</th>
                                <th>名称</th>
                                <th>邮箱地址</th>
                                <th width="100">操作</th>
                            </tr>
                            </thead>
                            <tbody id="adminPageBody">
                            <c:if test="${empty requestScope.pageInfo.list}">
                                <tr>
                                    <td colspan="6" align="center">
                                        抱歉！没有查询到你要的数据！
                                    </td>
                                </tr>
                            </c:if>
                            <c:if test="${!empty requestScope.pageInfo.list}">
                                <c:forEach items="${requestScope.pageInfo.list}" var="admin" varStatus="myStatus">
                                    <tr>
                                        <td>${myStatus.count}</td>
                                        <td><input id='" + ${admin.id} + "' class='itemBox' type="checkbox"></td>
                                        <td>${admin.loginAcct}</td>
                                        <td>${admin.userName}</td>
                                        <td>${admin.email}</td>
                                        <td>
                                            <a href="assign/to/assign/role/page.html?adminId=${admin.id}&pageNum=${requestScope.pageInfo.pageNum}&keyword=${param.keyword}"
                                               class="btn btn-success btn-xs"><i class=" glyphicon glyphicon-check"></i></a>
                                            <a href="admin/to/edit/page.html?adminId=${admin.id}&pageNum=${requestScope.pageInfo.pageNum}&keyword=${param.keyword}"
                                               class="btn btn-primary btn-xs"> <i class=" glyphicon glyphicon-pencil"></i> </a>
                                            <a href="admin/remove/${admin.id}/${requestScope.pageInfo.pageNum}/${param.keyword}.html"
                                               class="btn btn-danger btn-xs"> <i class=" glyphicon glyphicon-remove"></i> </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:if>


                            </tbody>
                            <tfoot>
                            <tr>
                                <td colspan="6" align="center">
                                    <div id="Pagination" class="pagination" >
                                        <!-- 这里显示分页 -->
                                    </div>
                                </td>
                            </tr>
                            </tfoot>
                        </table>
                    </div>
                </div>
            </div>
        </div>


    </div>
</div>


