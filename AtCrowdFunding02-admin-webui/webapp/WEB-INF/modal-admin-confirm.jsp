<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<<div class="modal fade" id="adminConfirmModal" tabindex="-1" role="dialog">
<div class="modal-dialog" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                    aria-hidden="true">&times;</span></button>
            <h4 class="modal-title">尚筹网系统弹窗</h4>
        </div>
        <div class="modal-body">
            <h4>请确认是否要删除下列角色：</h4>
            <div id="adminNameDiv" style="text-align: center"></div>
        </div>
        <div class="modal-footer">
            <button id="removeAdminBtn" type="button" class="btn btn-primary">确认删除</button>
        </div>
    </div><!-- /.modal-content -->
</div>
<!-- /.modal-dialog -->
</div><!-- /.modal -->