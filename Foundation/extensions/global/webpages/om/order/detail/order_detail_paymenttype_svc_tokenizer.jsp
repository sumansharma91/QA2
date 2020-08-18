<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>

<%@ include file="/console/jsp/paymenttokenizerutils.jspf" %>
<%@include file="/yfc/util.jspf" %>

<script language="javascript">
	function doPostForParams(){
		window.document.forms.paramsFormToPost.submit();
		document.body.style.cursor='auto';
	}
</script>
<%=writeDocumentDomain()%>

<%
String ssdcsDataTypeDetail = HTMLEncode.htmlEscape(request.getParameter("ssdcsDataTypeDetail"));
String displayValue = HTMLEncode.htmlEscape(request.getParameter("ssdcsDisplayValue"));
String tokenValue = HTMLEncode.htmlEscape(request.getParameter("TokenValue"));
String ssdcsDataTypeDetailValue = "";
String ssdcsDisplayValue = "";
String ssdcsTokenValue = "";

if (ssdcsDataTypeDetail != null)
	ssdcsDataTypeDetailValue = ssdcsDataTypeDetail;
if (displayValue != null)
	ssdcsDisplayValue = displayValue;
if (tokenValue != null)
	ssdcsTokenValue = tokenValue;
%>
<%
	String url = YFSSystem.getProperty("yfs.ssdcs.url");
	String servlet = YFSSystem.getProperty("yfs.ssdcs.servlet");
	int port;
	try {
		URL ssdcsUrl = new URL(url);
		port = ssdcsUrl.getPort();
	} catch (java.net.MalformedURLException e) {
		port = request.getServerPort();
	}
	String portString = (port < 0) ? "" : (":" + port);
	String PaymentServerURL =  url + servlet;
	String locale_suffix = "";
	if (!equals(getBasicTheme(), getTheme())) {
		locale_suffix = "_" + getCurrentLocale();
	}
	String cssurl = request.getScheme() + "://" + request.getServerName() + portString + request.getContextPath() +"/css/"+getBasicTheme()+(Boolean.valueOf(HTMLEncode.htmlEscape(request.getParameter("setOverrideCss")))?"_ssdcs_override":"_ssdcs")+ locale_suffix +".css";

%>
<form method="post" name="paramsFormToPost"  action="<%=PaymentServerURL%>">
<input type="hidden" name="ssdcsAuthenticationToken" value="<%=ssdcsTokenValue%>"></input>
<input type="hidden" name="ssdcsCssUrl" value="<%=cssurl%>"></input>
<input type="hidden" name="ssdcsRedirectUrl" value="/jsp/ssdcs_tokenize_pan.jsp"></input>
<input type="hidden" name="ssdcsDataType" value="ssdcsStoredValueCardNumber"></input>
<input type="hidden" name="ssdcsDataTypeDetail" value="<%=ssdcsDataTypeDetailValue%>"></input>
<input type="hidden" name="ssdcsDisplayValue" value="<%=ssdcsDisplayValue%>"></input>
<input type="hidden" name="ssdcsTabIndex" value='2'></input>

<script language="JavaScript" type="text/javascript">
	window.attachEvent("onload", doPostForParams);
</script>
</form>
<input type="hidden" name="localResultCode" value='LOCAL'></input>
