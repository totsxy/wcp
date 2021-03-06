<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/view/conf/farmtag.tld" prefix="PF"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<base href="<PF:basePath/>" />
<title>${DOCE.doc.title}-<PF:ParameterValue
		key="config.sys.title" /></title>
<meta name="description"
	content='<PF:ParameterValue key="config.sys.mate.description"/>'>
<meta name="keywords"
	content='<PF:ParameterValue key="config.sys.mate.keywords"/>'>
<meta name="author"
	content='<PF:ParameterValue key="config.sys.mate.author"/>'>
<meta name="robots" content="noindex,nofllow">
<jsp:include page="../atext/include-web.jsp"></jsp:include>
<script src="<PF:basePath/>text/lib/syntax-highlighter/brush.js"></script>
<link href="<PF:basePath/>text/lib/syntax-highlighter/shCore.css"
	rel="stylesheet" />
<link
	href="<PF:basePath/>text/lib/syntax-highlighter/shThemeDefault.css"
	rel="stylesheet" />
<script charset="utf-8"
	src="<PF:basePath/>text/lib/super-validate/validate.js"></script>
</head>
<c:set var="typeid" value="${DOCE.type.id}" scope="request"></c:set>
<body>
	<jsp:include page="../commons/head.jsp"></jsp:include>
	<div class="containerbox">
		<div class="container ">
			<div class="row" style="margin-top: 70px;">
				<div class="col-md-3  visible-lg visible-md">
					<jsp:include page="../know/commons/includeNavigationAudit.jsp"></jsp:include>
				</div>
				<div class="col-md-9">
					<div class="panel panel-default">
						<div class="panel-body">
							<jsp:include page="../know/commons/doc.jsp"></jsp:include></div>
					</div>
					<br />
					<c:if test="${TYPE=='AUDIT' }">
						<div class="panel panel-default">
							<div class="panel-body">
								<jsp:include page="../know/commons/includeAuditForm.jsp"></jsp:include>
							</div>
						</div>
					</c:if>
				</div>
				<div class="row column_box">
					<div class="col-md-12"></div>
				</div>
			</div>
		</div>
	</div>
	<a name="markdocbottom"></a>
	<jsp:include page="../commons/footServer.jsp"></jsp:include>
	<jsp:include page="../commons/foot.jsp"></jsp:include>
	<script type="text/javascript">
		$(function() {
			SyntaxHighlighter.config.clipboardSwf = basePath
					+ 'text/lib/syntax-highlighter/clipboard.swf';
			SyntaxHighlighter.config.strings = {
				expandSource : '????????????',
				viewSource : '????????????',
				copyToClipboard : '????????????',
				copyToClipboardConfirmation : '??????????????????',
				print : '??????',
				help : '?',
				alert : '??????????????????\n',
				noBrush : '??????????????????: ',
				brushNotHtmlScript : '??????????????????html-script??????',
				aboutDialog : '<div></div>'
			};
			SyntaxHighlighter.all();
		});
	</script>
</body>
</html>