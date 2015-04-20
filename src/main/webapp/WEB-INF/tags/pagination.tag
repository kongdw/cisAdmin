<%@tag pageEncoding="UTF-8"%>
<%@ attribute name="page" type="org.springframework.data.domain.Page" required="true"%>
<%@ attribute name="target" type="java.lang.String" required="false"%>
<%@ attribute name="paginationSize" type="java.lang.Integer" required="false"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
    if (target == null || target == "") {
        target = "ajax-get-container";
    }
    request.setAttribute("target", target);
    
    int current = page.getNumber() + 1;
    if (paginationSize == null) {
        paginationSize = 5;
    }
    int begin = Math.max(1, current - paginationSize / 2);
    int end = Math.min(begin + (paginationSize - 1), page.getTotalPages());
    int prev = current - 1 > 0 ? current - 1 : 1;
    int next = current + 1 > page.getTotalPages() ? page.getTotalPages() : current + 1;

    request.setAttribute("current", current);
    request.setAttribute("begin", begin);
    request.setAttribute("end", end);
    request.setAttribute("prev", prev);
    request.setAttribute("next", next);
%>
<div class="row">
	<div class="col-md-5 col-sm-12">
		<div class="dataTables_info" id="sample_1_info">总计: ${page.totalElements} 条记录</div>
	</div>
	<div class="col-md-7 col-sm-12">
		<div class="dataTables_paginate paging_bootstrap" target="${target}">
			<ul class="pagination" style="visibility: visible;">
				<li class="first"><a title="首页" href="javascript:void(0)" page="${1}"><i class="fa fa-angle-left"></i></a></li>
				<li class="prev"><a title="上一页" href="javascript:void(0)" page="${prev}"><i class="fa fa-angle-left"></i></a></li>
				<c:forEach var="i" begin="${begin}" end="${end}">
					<c:choose>
						<c:when test="${i == current}">
							<li class="page active"><a href="javascript:void(0)">${i}</a></li>
						</c:when>
						<c:otherwise>
							<li class="page"><a href="javascript:void(0)" page="${i}">${i}</a></li>
						</c:otherwise>
					</c:choose>
				</c:forEach>
				<li class="next"><a title="下一页" href="javascript:void(0)" page="${next}"><i class="fa fa-angle-right"></i></a></li>
				<li class="last"><a title="末页" href="javascript:void(0)" page="${page.totalPages}"><i
						class="fa fa-angle-right"></i></a></li>
			</ul>
		</div>
	</div>
</div>