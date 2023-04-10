<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="zh-CN">
<%@include file="/WEB-INF/include-head.jsp" %>
<link rel="stylesheet" href="css/pagination.css"/>
<script type="text/javascript" src="jquery/jquery.pagination.js"></script>
<link rel="stylesheet" href="ztree/zTreeStyle.css"/>
<script type="text/javascript" src="ztree/jquery.ztree.all-3.5.min.js"></script>
<script type="text/javascript" src="crowd/my-role.js"></script>
<script type="text/javascript">
    $(function () {

        // 为分页数据初始化操作
        window.pageNum = 1;
        window.pageSize = 5;
        window.keyword = "";

        // 调用执行分页函数
        generatePage();

        // 给查询按钮绑定单击函数
        $("#searchBtn").click(function () {

            //获取文本框的keyword
            window.keyword = $("#keywordInput").val();

            //调用分页函数，刷新页面
            generatePage();
        });

        // 点击新增按钮弹出模态框
        $("#showModalBtn").click(function () {
            $("#addModal").modal("show");
        });
        // 给新增模态框中的保存按钮绑定单击响应函数
        $("#saveRoleBtn").click(function () {

            //获取用户在文本框中输入的角色名称
            var roleName = $.trim($("#addModal [name=roleName]").val());

            //发送Ajax请求
            $.ajax({
                "url": "role/save.json",
                "type": "post",
                "data": {
                    "name": roleName
                },
                "dataType": "json",
                "success": function (response) {
                    var result = response.result;

                    if (result == "SUCCESS") {
                        layer.msg("保存成功！")
                        //重新加载分页
                        window.pageNum = 999999;
                        generatePage();
                    }
                    if (result == "FAILED") {
                        layer.msg("保存失败！" + response.message)
                    }

                },
                "error": function (response) {
                    layer.msg(response.status + " " + response.statusText);
                }

            });

            //关闭模态框
            $("#addModal").modal("hide");

            //清理模态框
            $("#addModal [name=roleName]").val("");


        });

        // //给更新标签的按钮绑定单击响应函数  只有当前页绑定成功
        // $(".pencilBtn").click(function () {
        //
        // });
        // 给更新标签的按钮绑定单击响应函数 ,使用jQuery中的on函数
        $("#rolePageBody").on("click", ".pencilBtn", function () {
            //打开模态框
            $("#editModal").modal("show");
            //回现数据
            var roleName = $(this).parent().prev().text();
            //获取当前角色的ID
            window.roleId = this.id;

            $("#editModal [name=roleName]").val(roleName);
        });
        // 给更新按钮绑定单击响应函数
        $("#updateRoleBtn").click(function () {

            var roleName = $("#editModal [name=roleName]").val();

            $.ajax({
                "url": "role/update.json",
                "type": "post",
                "data": {
                    "id": window.roleId,
                    "name": roleName
                },
                "dataType": "json",
                "success": function (response) {
                    var result = response.result;

                    if (result == "SUCCESS") {
                        layer.msg("更新成功！")
                        //重新加载分页
                        generatePage();
                    }
                    if (result == "FAILED") {
                        layer.msg("更新失败！" + response.message)
                    }
                },
                "error": function (response) {
                    layer.msg(response.status + " " + response.statusText);
                }
            });
            //关闭模态框
            $("#editModal").modal("hide");
        });

        // 点击删除模态框中确认删除的按钮执行删除
        $("#removeRoleBtn").click(function () {

            var requestBody = JSON.stringify(window.roleIdArray);

            $.ajax({
                "url": "role/remove/by/role/id/array.json",
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
            $("#confirmModal").modal("hide");

            //清除之前多选框的勾选
            $("#summaryBox").prop("checked", false);

        });

        // 给单条删除按钮绑定单击响应函数
        $("#rolePageBody").on("click", ".removeBtn", function () {

            var roleName = $(this).parent().prev().text();

            //准备roleArray
            var roleArray = [{
                roleId: this.id,
                roleName: roleName
            }]

            //调用专门的函数调用模态框
            showConfirmModal(roleArray);

        });


        // 给summaryBox按钮绑定单击响应函数
        $("#summaryBox").click(function () {

            //获取当前多选框的状态
            var currentStatus = this.checked;

            //用当前多选框的状态去设置其他多选框
            $(".itemBox").prop("checked", currentStatus);
        });

        // 全选和全不选的反向操作
        $("#rolePageBody").on("click", ".itemBox", function () {

            // 获取当前已经选中的itemBox数量
            var checkedBoxCount = $(".itemBox:checked").length;

            //获取全部itemBox的数量
            var totalBoxCount = $(".itemBox").length;

            $("#summaryBox").prop("checked", checkedBoxCount == totalBoxCount);

        });

        // 给批量删除的按钮绑定单击响应函数
        $("#batchRemoveBtn").click(function () {

            var roleArray = [];

            //遍历选中的多选框
            $(".itemBox:checked").each(function () {

                //使用this引用当前遍历的多选框
                var roleId = this.id;

                //通过DOM操作获取名称
                var roleName = $(this).parent().next().text();

                roleArray.push({
                    "roleId": roleId,
                    "roleName": roleName
                });
            });

            // 检查roleArray的长度
            if (roleArray.length == 0) {
                layer.msg("请至少选中一个执行删除");
                return;
            }

            //调用专门的函数
            showConfirmModal(roleArray);
        });

        // 给角色修改权限按钮绑定单击响应函数
        $("#rolePageBody").on("click", ".checkBtn", function () {

            // 把角色id存入全局变量
            window.roleId = this.id;

            // 打开模态框
            $("#assignModal").modal("show");

            // 在模态框中装载Auth的树形结构
            fillAuthTree();
        });

        // 给分配权限模态框中的“分配”按钮绑定单击响应函数
        $("#assignBtn").click(function () {

            // ①收集树形结构的各个节点中被勾选的节点
            var authIdArray = [];

            // [2]获取 zTreeObj 对象
            var zTreeObj = $.fn.zTree.getZTreeObj("authTreeDemo");

            // [3]获取全部被勾选的节点
            var checkedNodes = zTreeObj.getCheckedNodes();

            // [4]遍历 checkedNodes
            for (var i = 0; i < checkedNodes.length; i++) {
                var checkedNode = checkedNodes[i];
                var authId = checkedNode.id;
                authIdArray.push(authId);
            }

            // ②发送请求执行分配
            var requestBody = {
                "authIdArray": authIdArray,
                //统一使用LIst<Integer>数组
                "roleId": [window.roleId]
            }
            requestBody = JSON.stringify(requestBody);

            $.ajax({
                "url": "assign/do/role/assign/auth.json",
                "type": "post",
                "data": requestBody,
                "contentType": "application/json;charset=UTF-8",
                "dataType": "json",
                "success": function (response) {
                    var result = response.result;
                    if (result == "SUCCESS") {
                        layer.msg("操作成功！");
                    }
                    if (result == "FAILED") {
                        layer.msg("操作失败！" + response.message);
                    }
                },
                "error": function (response) {
                    layer.msg(response.status + " " + response.statusText);
                }
            });

            $("#assignModal").modal("hide");
        });


    });
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
                    <form class="form-inline" role="form" style="float:left;">
                        <div class="form-group has-feedback">
                            <div class="input-group">
                                <div class="input-group-addon">查询条件</div>
                                <input id="keywordInput" class="form-control has-success" type="text"
                                       placeholder="请输入查询条件">
                            </div>
                        </div>
                        <button id="searchBtn" type="button" class="btn btn-warning"><i
                                class="glyphicon glyphicon-search"></i> 查询
                        </button>
                    </form>
                    <button type="button" id="batchRemoveBtn" class="btn btn-danger"
                            style="float:right;margin-left:10px;"><i class=" glyphicon glyphicon-remove"></i> 删除
                    </button>
                    <button type="button" id="showModalBtn" class="btn btn-primary" style="float:right;"><i
                            class="glyphicon glyphicon-plus"></i> 新增
                    </button>
                    <br>
                    <hr style="clear:both;">
                    <div class="table-responsive">
                        <table class="table  table-bordered">
                            <thead>
                            <tr>
                                <th width="30">#</th>
                                <th width="30"><input id="summaryBox" type="checkbox"></th>
                                <th>名称</th>
                                <th width="100">操作</th>
                            </tr>
                            </thead>

                            <tbody id="rolePageBody"></tbody>

                            <tfoot>
                            <tr>
                                <td colspan="6" align="center">
                                    <div id="Pagination" class="pagination">
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
<%@include file="/WEB-INF/modal-role-add.jsp" %>
<%@include file="/WEB-INF/modal-role-edit.jsp" %>
<%@include file="/WEB-INF/modal-role-confirm.jsp" %>
<%@include file="/WEB-INF/modal-role-assign-auth.jsp" %>
</body>
</html>

