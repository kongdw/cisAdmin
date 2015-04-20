<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>定时任务控制</title>
</head>
<body>
	<div class="row">
		<div class="col-md-12">
			<table id="grid-schedule-triggers" data-grid="table"></table>
		</div>
	</div>
	<script type="text/javascript">
        $(function() {
            $("#grid-schedule-triggers").data("gridOptions", {
                url : WEB_ROOT + '/admin/schedule/quartz-trigger/list',
                colNames : [ '任务名称', 'CRON表达式', '当前状态', '上次触发时间', '下次触发时间', '集群运行模式' ],
                colModel : [ {
                    name : 'jobName',
                    width : 240,
                    align : 'left'
                }, {
                    name : 'cronExpression',
                    width : 100,
                    align : 'right'
                }, {
                    name : 'stateLabel',
                    width : 60,
                    align : 'center'
                }, {
                    name : 'previousFireTime',
                    sorttype : 'date',
                    align : 'center'
                }, {
                    name : 'nextFireTime',
                    sorttype : 'date',
                    align : 'center'
                }, {
                    name : 'runWithinCluster',
                    formatter : 'checkbox'
                } ],
                rowNum : -1,
                loadonce : true,
                addable : false,
                loadonce : true,
                operations : function(items) {
                    var $grid = $(this);
                    var $resume = $('<li data-position="multi" data-toolbar="show" data-text="show"><a><i class="fa fa-play"></i> 启动</a></li>');
                    $resume.children("a").bind("click", function(e) {
                        e.preventDefault();
                        var ids = $grid.getAtLeastOneSelectedItem();
                        if (ids) {
                            $grid.ajaxPostURL({
                                url : WEB_ROOT + '/admin/schedule/quartz-trigger/state',
                                success : function() {
                                    $grid.refresh();
                                },
                                confirmMsg : "确认 启动 所选任务？",
                                data : {
                                    ids : ids.join(","),
                                    state : 'resume'
                                }
                            })
                        }
                    });
                    items.push($resume);
                    var $pause = $('<li data-position="multi" data-toolbar="show" data-text="show"><a><i class="fa fa-pause"></i> 暂停</a></li>');
                    $pause.children("a").bind("click", function(e) {
                        e.preventDefault();
                        var ids = $grid.getAtLeastOneSelectedItem();
                        if (ids) {
                            $grid.ajaxPostURL({
                                url : WEB_ROOT + '/admin/schedule/quartz-trigger/state',
                                success : function() {
                                    $grid.refresh();
                                },
                                confirmMsg : "确认 暂停 所选任务？",
                                data : {
                                    ids : ids.join(","),
                                    state : 'pause'
                                }
                            })
                        }
                    });
                    items.push($pause);
                    var $run = $('<li data-position="multi" data-toolbar="show" data-text="show"><a><i class="fa fa-bolt"></i> 立即执行</a></li>');
                    $run.children("a").bind("click", function(e) {
                        e.preventDefault();
                        var ids = $grid.getAtLeastOneSelectedItem();
                        if (ids) {
                            $grid.ajaxPostURL({
                                url : WEB_ROOT + '/admin/schedule/quartz-trigger/run',
                                success : function() {
                                    $grid.refresh();
                                },
                                confirmMsg : "确认 立即执行 所选任务？",
                                data : {
                                    ids : ids.join(",")
                                }
                            })
                        }
                    });
                    items.push($run);
                }
            });
        });
    </script>
</body>
</html>