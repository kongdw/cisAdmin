<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>广告配置</title>
</head>
<body>
	<div class="row search-form-default">
		<div class="col-md-12">
			<form method="get" class="form-inline form-validation form-search form-search-init control-label-sm"
				data-grid-search="#grid-ad-advertising-index">
				<div class="form-group">
					<div class="controls controls-clearfix">
						<input type="text" name="search['CN_adId_OR_title']" class="form-control input-large" placeholder="标题，广告ID...">
					</div>
				</div>
				<div class="form-group search-group-btn">
					<button class="btn green" type="submmit">
						<i class="m-icon-swapright m-icon-white"></i>&nbsp; 查&nbsp;询
					</button>
					<button class="btn default" type="reset">
						<i class="fa fa-undo"></i>&nbsp; 重&nbsp;置
					</button>
				</div>
			</form>
		</div>
	</div>
	<div class="row">
		<div class="col-md-12">
			<table id="grid-ad-advertising-index"></table>
		</div>
	</div>

	<script type="text/javascript">
        $(function() {
            $("#grid-ad-advertising-index").data("gridOptions", {
                url : WEB_ROOT + '/admin/ad/advertising/list',
                colModel : [ {
                    label : '广告ID',
                    name : 'adId',
                    width : 50,
                    editable : false,
                    align : 'left'
                }, {
                    label : '贴吧名称',
                    name : 'baName',
                    width : 50,
                    editable : false,
                    align : 'left'
                }, {
                    label : '广告标题',
                    name : 'titleAbstract',
                    index: 'title',
                    width : 150,
                    editable : false,
                    align : 'left'
                }, {
                    label : '吧URL',
                    name : 'adUrlAbstract',
                    index: 'adUrl',
//                    edittype : 'textarea',
                    editable : false,
                    width : 80
                } ,{
                    label : '启用停用',
                    name : 'enable',
                    formatter : 'checkbox',
                    editable : true,
                    width : 40
                }, {
                    label : '目标次数',
                    name : 'checkNum',
                    editable : true,
                    width : 40
                }, {
                    label : '已完成次数',
                    name : 'checkedNum',
                    editable : false,
                    width : 40
                }, {
                    label : '开始时间',
                    name : 'fromDate',
                    formatter : 'timestamp',
                    editable : true,
                    editoptions : {
                        time : true
                    },
                    formatoptions : {
                        srcformat : 'Y-m-d H:i',
                        newformat : 'Y-m-d H:i'
                    },
                    align : 'center'
                }, {
                    label : '截止时间',
                    name : 'toDate',
                    formatter : 'timestamp',
                    editable : true,
                    editoptions : {
                        time : true
                    },
                    formatoptions : {
                        srcformat : 'Y-m-d H:i',
                        newformat : 'Y-m-d H:i'
                    },
                    align : 'center'
                }],
                fullediturl : WEB_ROOT + "/admin/ad/advertising/edit",
                editurl : WEB_ROOT + '/admin/ad/advertising/edit',
                editrulesurl : WEB_ROOT + '/admin/util/validate?clazz=${clazz}',
                delurl : WEB_ROOT + '/admin/ad/advertising/delete'
            });
        });
    </script>
</body>
</html>