<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<script language="javascript">
	
	function makeRefreshTrue() {
		window.returnValue = "Refresh";
        window.close();
	}

</script>
<yfc:callAPI apiID='AP1'/>
<% 
	String sCurrentLocaleCode = getLocale().getLocaleCode();

	// Get API output called from the view definition
    YFCElement questionsElem = (YFCElement) request.getAttribute("QuestionType");
    if (null == questionsElem) {
        questionsElem = YFCDocument.createDocument("QuestionType").getDocumentElement();
    }

    // Set the ModificationAllowedFlag on the input element based on which
    // AllowedModifications element exists in the request.
    String modificationFlag = "Y";
    if (!isVoid(resolveValue("xml:/Order/AllowedModifications/Modification/@ModificationType"))) {
        if (!isModificationAllowedWithModType("ANSWER_SET", "xml:/Order/AllowedModifications")) {
            modificationFlag = "N";
        }
    }
    else if (!isVoid(resolveValue("xml:/OrderLine/Order/AllowedModifications/Modification/@ModificationType"))) {
        if (!isModificationAllowedWithModType("ANSWER_SET", "xml:/OrderLine/Order/AllowedModifications")) {
            modificationFlag = "N";
        }
    }
    else if (!isVoid(resolveValue("xml:/WorkOrder/Order/AllowedModifications/Modification/@ModificationType"))) {
        if (!isModificationAllowedWithModType("ANSWER_SET", "xml:/WorkOrder/Order/AllowedModifications")) {
            modificationFlag = "N";
        }
    }
    if ((equals("Y", modificationFlag)) && (userHasOverridePermissions())) {
        modificationFlag = "O";
    }
    questionsElem.setAttribute("ModificationAllowedFlag", modificationFlag);

	StringBuffer sbuf =  new StringBuffer();
	sbuf.append("platui.jar,platform_dv_ui.jar,platform_baseutils.jar,yscpui.jar,xml-apis.jar,xercesImpl.jar,yompexui.jar,smcfsiconsbe.jar,yantraiconsbe.jar,comm.jar,resources.jar,icu4j-49_1.jar,icu4j-charset-49_1.jar,icu4j-localespi-49_1.jar");
	StringBuffer jarList = addTokenToJarsIfRequired(sbuf);
	String jars = jarList.toString();

    String javaCodebase = getJavaPluginCodebase();
    if( isVoid(javaCodebase) )
        javaCodebase = "http://java.sun.com/update/1.5.0/jinstall-1_5_0_11-windows-i586.cab#Version=1,5,0,11";
    String javaClassid = getJavaPluginClassid();
    if( isVoid(javaClassid) )
        javaClassid = "clsid:CAFEEFAC-0015-0000-0011-ABCDEFFEDCBA";
%>
<OBJECT classid = "<%=getJavaPluginClassidForHSDE()%>" 
   WIDTH = "100%" HEIGHT = "425" NAME = "OrderQuestionsApplet"  ID="OrderQuestionsApplet" 
   codebase = "<%=getJavaPluginCodebase()%>" >
		<PARAM NAME = CODE VALUE = "com.yantra.app.exui.YantraExuiApplet">
		<PARAM NAME = CODEBASE  VALUE = "<%=request.getContextPath()%>/yfscommon" >
		<PARAM NAME = NAME VALUE  = "OrderQuestionsApplet" >
		<PARAM NAME = ID VALUE = "OrderQuestionsApplet" >
		<PARAM NAME ="cache_option" VALUE = "Plugin" >
		<PARAM NAME ="cache_archive" VALUE = "<%=jars%>" >		
		<PARAM NAME="type" VALUE="<%=getJavaAppletType()%>">
		<PARAM NAME ="MAYSCRIPT" VALUE="true">
		<PARAM NAME="scriptable" VALUE="true">
		<PARAM NAME="TransactionClass" VALUE="com.yantra.omp.exui.ui.screens.OMPOrderQuestions">
		<PARAM NAME="ActionUrl" VALUE="<%=request.getContextPath()%>/console/order.exui">
		<PARAM NAME="BundleList" VALUE="extn/extnbundle yscpapibundle ycpapibundle">
		<PARAM NAME="LocaleCode" VALUE="<%=sCurrentLocaleCode%>">
		<PARAM NAME="FormInput" VALUE='<%=replaceString(replaceString(questionsElem.toString(),"&", "&amp;") ,"'","&apos;") %>'>
		<PARAM NAME="Debug" VALUE="Y">
		<PARAM NAME="YVSE001EX01" VALUE="com.yantra.wms.exui.ui.screens.TagEntry">
		<PARAM NAME="YVSE001EX02" VALUE="com.yantra.wms.exui.ui.screens.ExpirationDateEntry">
		<PARAM NAME="YVSE001EX03" VALUE="com.yantra.wms.exui.ui.screens.SerialEntry">
		<PARAM NAME="YVSE001EX04" VALUE="com.yantra.wms.exui.ui.screens.DispositionCodeLookUp">
		<PARAM NAME="ContextPath" VALUE="<%=request.getContextPath()%>">
		<%
			Cookie[] cookies = request.getCookies();
			String cookieName = null;
			String cookieValue = null;
			String cookiePath = null;
			StringBuffer cookieBuffer = new StringBuffer();
			for(int i=0; i<cookies.length; i++) {
				Cookie cookie = cookies[i];
				cookieName = cookie.getName();
				cookieValue = cookie.getValue();
				cookiePath = cookie.getPath();
				if(cookiePath == null) {
					cookiePath = "/";
				}
				cookieBuffer.append(" CookieName").append(i+1).append("=\"").append(cookieName).append("\"");
				cookieBuffer.append(" CookieValue").append(i+1).append("=\"").append(cookieValue).append("\"");
				cookieBuffer.append(" CookiePath").append(i+1).append("=\"").append(cookiePath).append("\"");

		%>
		<PARAM NAME="CookieName<%=i+1%>" VALUE="<%=cookieName%>">
		<PARAM NAME="CookieValue<%=i+1%>" VALUE="<%=cookieValue%>">
		<PARAM NAME="CookiePath<%=i+1%>" VALUE="<%=cookiePath%>">
		<%
			}
		%>
</OBJECT>
<script language="javascript">
	window.returnValue = "Refresh";        
</script>
