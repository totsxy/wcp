<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/view/conf/farmtag.tld" prefix="PF"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<base href="<PF:basePath/>" />
<title>${DOCE.doc.title}-<PF:ParameterValue
		key="config.sys.title" /></title>
<meta name="description" content="${DOCE.doc.docdescribe}" />
<meta name="author" content="${DOCE.doc.author}">
<meta name="keywords" content="${DOCE.doc.tagkey}">
<meta name="robots" content="index,fllow">
<jsp:include page="../atext/include-web.jsp"></jsp:include>
<script src="<PF:basePath/>text/lib/syntax-highlighter/brush.js"></script>
<link href="<PF:basePath/>text/lib/syntax-highlighter/shCore.css"
	rel="stylesheet" />
<link
	href="<PF:basePath/>text/lib/syntax-highlighter/shThemeDefault.css"
	rel="stylesheet" />
</head>
<c:set var="typeid" value="${DOCE.type.id}" scope="request"></c:set>
<body>
	<jsp:include page="../commons/head.jsp"></jsp:include>
	<div class="containerbox">
		<div class="container ">
			<div class="row" style="margin-top: 70px;">
				<div class="col-md-3  visible-lg visible-md">
					<jsp:include page="../know/commons/includeNavigationDoc.jsp"></jsp:include>
				</div>
				<div class="col-md-9">
					<div class="panel panel-default">
						<div class="panel-body"><jsp:include
								page="../know/commons/doc.jsp"></jsp:include></div>
					</div>
					<div style="margin-top: 20px;"></div>
					<div class="panel panel-default">
						<div class="panel-body">
							<jsp:include page="../know/commons/includeAuthInfo.jsp"></jsp:include>
							<jsp:include page="../know/commons/includeTagInfo.jsp"></jsp:include>
							<jsp:include page="../know/commons/includeDocVersions.jsp"></jsp:include>
							<jsp:include page="../know/commons/includeDocFiles.jsp"></jsp:include>
							<jsp:include page="../know/commons/includeTypeDoc.jsp"></jsp:include>
							<jsp:include page="../lucene/commons/includeLuceneResultMin.jsp"></jsp:include></div>
					</div>
				</div>
			</div>
			<div class="row column_box">
				<div class="col-md-12"></div>
			</div>
		</div>
	</div>
	<a name="markdocbottom"></a>
	<jsp:include page="../commons/footServer.jsp"></jsp:include>
	<jsp:include page="../commons/foot.jsp"></jsp:include>
	<script type="text/javascript">
		$(function() {
			$('img', '#docContentsId').addClass("img-thumbnail");
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