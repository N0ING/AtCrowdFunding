// 声明专门的函数用来在分配 Auth 的模态框中显示 Auth 的树形结构数据
function fillAuthTree() {

    // 发送ajax请求查询Auth数据
    var ajaxReturn = $.ajax({
        "url": "assign/get/all/auth.json",
        "type": "post",
        "dataType": "json",
        "async": false
    });

    if (ajaxReturn.status != 200) {
        layer.msg("请求处理出错！响应状态码是：" + ajaxReturn.status + "说明是：" + ajaxReturn.statusText);
        return;
    }


    // 从响应结果中获取auth的JSON数据
    // 从服务器端查询到的 list 不需要组装成树形结构，这里我们交给zTree 去组装
    var authList = ajaxReturn.responseJSON.data;

    // 准备对 zTree 进行设置的 JSON 对象
    var setting = {
        "data": {
            "simpleData": {
                // 开启简单 JSON 功能
                "enable": true,
                // 使用 categoryId 属性关联父节点，不用默认的 pId 了
                "pIdKey": "categoryId"
            },
            "key": {
                // 使用 title 属性显示节点名称，不用默认的 name 作为属性名了
                "name": "title"
            }
        },
        "check": {
            "enable": true
        }
    };

    // 生成树形结构
    // <ul id="authTreeDemo" class="ztree"></ul>
    $.fn.zTree.init($("#authTreeDemo"), setting, authList)

    // 获取 zTreeObj 对象
    var zTreeObj = $.fn.zTree.getZTreeObj("authTreeDemo");
    // 调用 zTreeObj 对象的方法，把节点展开
    zTreeObj.expandAll(true);

    // 查询已分配的auth的id组成的数组
    ajaxReturn =  $.ajax({
        "url": "assign/get/assigned/auth/id/by/role/id.json",
        "type": "post",
        "data": {
            "roleId": window.roleId
        },
        "dataType": "json",
        "async": false
    });

    if (ajaxReturn.status != 200) {
        layer.msg("请求处理出错！响应状态码是：" + ajaxReturn.status + "说明是：" + ajaxReturn.statusText);
        return;
    }

    var authIdArray = ajaxReturn.responseJSON.data;
    // 根据 authIdArray 把树形结构中对应的节点勾选上
    // ①遍历 authIdArray
    for (var i = 0; i < authIdArray.length; i++) {
        var authId = authIdArray[i];
        // ②根据 id 查询树形结构中对应的节点
        var treeNode = zTreeObj.getNodeByParam("id", authId);
        // ③将 treeNode 设置为被勾选
        // checked 设置为 true 表示节点勾选
        var checked = true;
        // checkTypeFlag 设置为 false，表示不“联动”，不联动是为了避免把不该勾选的勾选上
        var checkTypeFlag = false;
        // 执行
        zTreeObj.checkNode(treeNode, checked, checkTypeFlag);
    }


}


// 声明专门的函数显示删除模态框
function showConfirmModal(roleArray) {

    // 打开模态框
    $("#confirmModal").modal("show");

    // 清除旧数据
    $("#roleNameDiv").empty();

    // 全局变量的数组存储角色id
    window.roleIdArray = [];

    //
    for (var i = 0; i < roleArray.length; i++) {
        var role = roleArray[i];
        var roleName = role.roleName;
        $("#roleNameDiv").append(roleName + "<br/>");

        var roleId = role.roleId;

        window.roleIdArray.push(roleId);
    }


}


//执行分页，生成页面数据，任何时候调用函数都可以重新加载页面
function generatePage() {

    //获取分页数据
    var pageInfo = getPageInfoRemote();

    //填充表格
    fillTableBody(pageInfo);

}

//远程访问服务器端程序获取pageInfo数据
function getPageInfoRemote() {
// 调用$.ajax()函数发送请求，并用ajaxResult接收函数返回值
    var ajaxResult = $.ajax({
        url: "role/get/page/info.json",
        type: "post",
        data: {
            "pageNum": window.pageNum,
            "pageSize": window.pageSize,
            "keyword": window.keyword
        },
        async: false,        //关闭异步模式，使用同步
        "dataType": "json"
    })
    console.log(ajaxResult);

    // 取得当前的响应状态码
    var statusCode = ajaxResult.status;

    // 判断当前状态码是不是200，不是200表示发生错误，通过layer提示错误消息
    if (statusCode != 200) {
        layer.msg("失败！状态码=" + statusCode + "错误信息=" + ajaxResult.statusText);
        return null;
    }

    // 响应状态码为200，进入下面的代码
    // 通过responseJSON取得handler中的返回值
    var resultEntity = ajaxResult.responseJSON;

    // 从resultEntity取得result属性
    var result = resultEntity.result;

    // 判断result是否是FAILED
    if (result == "FAILED") {
        // 显示失败的信息
        layer.msg(resultEntity.message);
        return null;
    }

    // result不是失败时，获取pageInfo
    var pageInfo = resultEntity.data;

    // 返回pageInfo
    return pageInfo;

}

//填充表格
function fillTableBody(pageInfo) {

    //清除tBody中的旧数据
    $("#rolePageBody").empty();

    // 这里清空是为了让没有搜索结果时不显示页码导航条
    $("#Pagination").empty();


    //判断pageInfo是否有效
    if (pageInfo == null || pageInfo == undefined || pageInfo.list == null || pageInfo.list.length == 0) {
        $("#rolePageBody").append("<tr><td colspan='4'>抱歉！没有查询到你搜索的数据！</td></tr>");
        return;
    }

    //使用pageInfo的list填充tBody
    for (var i = 0; i < pageInfo.list.length; i++) {

        var role = pageInfo.list[i];
        var roleId = role.id;
        var roleName = role.name;

        var numberTd = "<td>" + (i + 1) + "</td>";
        var checkboxTd = "<td><input id='" + roleId + "' class='itemBox' type='checkbox'></td>";
        var roleNameTd = "<td>" + roleName + "</td>"

        var checkBtn = "<button type='button' id='" + roleId + "' class='btn btn-success btn-xs checkBtn'><i class=' glyphicon glyphicon-check'></i></button>";
        //通过button的id属性把roleId的值传递到button的单击响应函数
        var pencilBtn = "<button type='button' id='" + roleId + "' class='btn btn-primary btn-xs pencilBtn'><i class=' glyphicon glyphicon-pencil'></i></button>";
        //通过button的id属性把roleId的值传递到button的单击响应函数
        var removeBtn = "<button type='button' id='" + roleId + "' class='btn btn-danger btn-xs removeBtn'><i class=' glyphicon glyphicon-remove'></i></button>";

        var buttonTd = "<td>" + checkBtn + " " + pencilBtn + " " + removeBtn + "</td>";
        var tr = "<tr>" + numberTd + checkboxTd + roleNameTd + buttonTd + "</tr>";

        $("#rolePageBody").append(tr);
    }
    generateNavigator(pageInfo);

}

//生成分页导航栏
function generateNavigator(pageInfo) {

    //获取总记录数
    var total = pageInfo.total;

    //声明相关属性
    var properties = {
        "num_edge_entries": 3,
        "num_display_entries": 5,
        "callback": paginationCallBack,
        "items_per_page": pageInfo.pageSize,
        "current_page": pageInfo.pageNum - 1,
        "prev_text": "上一页",
        "next_text": "下一页"
    }

    //调用Pagination（）函数
    $("#Pagination").pagination(total, properties);

}

//翻页时的回调函数
function paginationCallBack(pageIndex, jQuery) {

    //修改window的pageNum
    window.pageNum = pageIndex + 1;

    //调用分页函数
    generatePage();

    //取消页码超链接的按钮行为
    return false;
}