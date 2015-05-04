<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>通知消息编辑</title>
</head>
<body>
<form:form class="form-horizontal form-bordered form-label-stripped form-validation"
           action="${ctx}/admin/ad/advertising/edit" method="post" modelAttribute="entity"
           data-editrulesurl="${ctx}/admin/util/validate?clazz=${clazz}">
<form:hidden path="id"/>
<form:hidden path="version"/>
    <%--<div class="form-actions">--%>
    <%--<button class="btn green" type="submit" data-grid-reload="#grid-ad-advertising-index" data-post-dismiss="modal">--%>
    <%--保存--%>
    <%--</button>--%>
    <%--<button class="btn default" type="button" data-dismiss="modal">取消</button>--%>
    <%--</div>--%>
<script type="text/javascript">
    jQuery(document).ready(function () {
        $('#searchBtn').on('click', function () {
            var baName = $('#baName').val();
            if (baName == undefined || $.trim(baName) == "") {
                alert("贴吧名称不能为空.");
                return;
            }

            $("#admin-ad-list").ajaxJsonUrl(WEB_ROOT + "/admin/ad/advertising/adList?tieBaName=" + baName, function (data) {
                var $list = $(this);
                var index = 0;
                $.each(data, function (i, item) {
                    var el = '<label class="control-label">' + item.title + "</label>";
                    var checkbox = '<input type="checkbox" name="' + item.adId + '" value="' + item.adId + '">';
                    var row = '<div class="controls">' + checkbox + '</div>';
                    var item = $(el + row).appendTo($list);
                });
            });
            Util.assertNotBlank(baName, "调用的url参数不能为空");
        });
    });
</script>
<div class="form-body">
    <div class="row">
        <div class="col-md-12">
            <div class="form-group">
                <label class="control-label">贴吧名称</label>

                <div class="input-group m-bot15">
                      <span class="input-group-btn">
                        <button id="searchBtn" onclick="" type="button" class="btn btn-default"><i
                                class="fa fa-search"></i></button>
                      </span>
                    <form:input path="baName" class="form-control"/>
                </div>

            </div>
            <div class="form-group">
                <label class="control-label">选择广告</label>

                <div id="admin-ad-list" class="controls">
                    <input type="radio">
                    <span><label>为游戏而生！GTX970M战神Z7彩色版7499元“好色”首发！</label>
                    </span>
                    <span>
                        <input type="radio">
                    <label> 颜值当道，谁是你心中的女神？</label>

                    </span>
                     <span>
                         <input type="radio">
                    <label>全民欢乐抽奖，大奖豪礼送不停</label>

                    </span>
                </div>
            </div>
            <div class="form-group">
                <label class="control-label">广告ID</label>

                <div class="controls">
                    <form:input path="adId" class="form-control" readonly="true"/>
                </div>
            </div>
            <div class="form-group">
                <label class="control-label">广告标题</label>

                <div class="controls">
                    <form:input path="title" class="form-control" readonly="true"/>
                </div>
            </div>
            <div class="form-group">
                <label class="control-label">吧URL</label>

                <div class="controls">
                    <form:input path="adUrl" class="form-control" readonly="true"/>
                </div>
            </div>
            <div class="col-md-3">
                <div class="form-group">
                    <label class="control-label">开始时间</label>

                    <div class="controls">
                        <form:input path="fromDate" class="form-control" data-toggle="datetimepicker"/>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="form-group">
                    <label class="control-label">结束时间</label>

                    <div class="controls">
                        <form:input path="toDate" class="form-control" data-toggle="datetimepicker"/>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="form-group">
                    <label class="control-label">目标次数</label>

                    <div class="controls">
                        <form:input path="checkNum" class="form-control"/>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="form-group">
                    <label class="control-label">验证字符串</label>

                    <div class="controls ">
                        <form:input path="checkedStr" class="form-control"/>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="form-group">
                    <label class="control-label">启用状态</label>

                    <div class="controls controls-radiobuttons">
                        <form:radiobuttons path="enable" items="${applicationScope.cons.booleanLabelMap}"/>
                    </div>
                </div>
            </div>

        </div>
    </div>
    <div class="form-actions right">
        <button class="btn green" type="submit" data-grid-reload="#grid-ad-advertising-index" data-post-dismiss="modal">
            保存
        </button>
        <button class="btn default" type="button" data-dismiss="modal">取消</button>
    </div>
    </form:form>
</body>
</html>
