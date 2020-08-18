<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@page import="com.yantra.yfc.dom.*"%>


<script language="javascript">
	
	function makeRefreshTrue() {
		window.returnValue = "Refresh";
	}

</script>


<% 
	
	YFCElement elem =  null;
	YFCElement oReceiptElem = null;
	YFCElement oReturnReleaseElem = null;
	YFCDocument oDoc =  YFCDocument.createDocument("Shipment");
	String sNode = resolveValue("xml:CurrentUser:/User/@Node");
	String sEnterpriseCode = resolveValue("xml:CurrentOrganization:/Organization/@OrganizationCode");
	if(isShipNodeUser()) {
        sEnterpriseCode = getValue("CurrentOrganization", "xml:CurrentOrganization:/Organization/@PrimaryEnterpriseKey");
    }
	String userID = resolveValue("xml:CurrentUser:/User/@Loginid");
	String sCurrentLocaleCode = getLocale().getLocaleCode();
	String sAppCode = resolveValue("xml:/CurrentEntity/@ApplicationCode");
	elem = (YFCElement)request.getAttribute("Shipment");
	
	if(elem != null) {
		elem.setAttribute("DirectFromMenu","N");
	}

	if(elem == null) {
		oReceiptElem = (YFCElement)request.getAttribute("Receipt");
		if(oReceiptElem != null) {
			elem = oDoc.getDocumentElement();
			elem.setAttributes(oReceiptElem.getAttributes());
			elem.setAttribute("DirectFromMenu","N");
		}
		oReturnReleaseElem = (YFCElement)request.getAttribute("OrderLineDetail");
		if(oReturnReleaseElem != null) {
			elem = oDoc.getDocumentElement();
			elem.setAttributes(oReturnReleaseElem.getAttributes());
			elem.setAttribute("DirectFromMenu","N");
		}
		if(elem == null) {
			if (isVoid(sNode)) {%>
				<script>
					alert(YFCMSG164);
					callDtlEntView('home');
				</script>
			<%}			
			elem = oDoc.getDocumentElement();
			elem.setAttribute("ReceivingNode",sNode);
			elem.setAttribute("EnterpriseCode",sEnterpriseCode);
			elem.setAttribute("DirectFromMenu","Y");
		}
	}

    if( elem != null )
		elem.setAttribute("ApplicationCode",sAppCode); 
	
    String javaCodebase = getJavaPluginCodebase();
    if( isVoid(javaCodebase) )
        javaCodebase = "http://java.sun.com/update/1.5.0/jinstall-1_5_0_11-windows-i586.cab#Version=1,5,0,11";
    String javaClassid = getJavaPluginClassidForHSDE();
    if( isVoid(javaClassid) ){
        javaClassid = "clsid:CAFEEFAC-0015-0000-0011-ABCDEFFEDCBA";
	}
	
	StringBuffer sbuf =  new StringBuffer();
	sbuf.append("yantrabundles.jar,platui.jar,platform_dv_ui.jar,platform_baseutils.jar,yscpui.jar,xml-apis.jar,xercesImpl.jar,wmsexui.jar,smcfsiconsbe.jar,yantraiconsbe.jar,comm.jar");
	StringBuffer jarList = addTokenToJarsIfRequired(sbuf);
	String jars = jarList.toString();
	
%>

<OBJECT classid = "<%=getJavaPluginClassidForHSDE()%>" 
   WIDTH = "100%" HEIGHT = "450" NAME = "ReceivingApplet"  ID="ReceivingApplet" 
   codebase = "<%=getJavaPluginCodebase()%>" >
		<PARAM NAME = CODE VALUE = "com.yantra.app.exui.YantraExuiApplet" >
		<PARAM NAME = CODEBASE  VALUE = "<%=request.getContextPath()%>/yfscommon" >
		<PARAM NAME = NAME VALUE  = "ReceivingApplet" >
		<PARAM NAME = ID VALUE = "ReceivingApplet" >
		<PARAM NAME ="cache_option" VALUE = "Plugin" >
		<PARAM NAME ="cache_archive" VALUE = "<%=jars%>" >
		<PARAM NAME="type" VALUE="<%=getJavaAppletType()%>">
		<PARAM NAME ="MAYSCRIPT" VALUE="true">
		<PARAM NAME="scriptable" VALUE="true">
		<PARAM NAME="TransactionClass" VALUE="com.yantra.wms.exui.ui.screens.WMSReceiveDtl">
		<PARAM NAME="ActionUrl" VALUE="<%=request.getContextPath()%>/console/exuigen.exui">
		<PARAM NAME="BundleList" VALUE="extn/extnbundle yscpapibundle ycpapibundle">
		<PARAM NAME="LocaleCode" VALUE="<%=sCurrentLocaleCode%>">
		<PARAM NAME="FormInput" VALUE='<%=elem%>'>
		<PARAM NAME="Debug" VALUE="N">
		<PARAM NAME="YWME134EX01" VALUE="com.yantra.wms.exui.ui.screens.TagEntry">
		<PARAM NAME="YWME134EX02" VALUE="com.yantra.wms.exui.ui.screens.ExpirationDateEntry">
		<PARAM NAME="YWME134EX03" VALUE="com.yantra.wms.exui.ui.screens.SerialEntry">
		<PARAM NAME="YWME134EX04" VALUE="com.yantra.wms.exui.ui.screens.DispositionCodeLookUp">
		<PARAM NAME="ContextPath" VALUE="<%=request.getContextPath()%>">
		<%
			Cookie[] cookies = request.getCookies();
			String cookieName = null;
			String cookieValue = null;
			String cookiePath = null;
			for(int i=0; i<cookies.length; i++) {
				Cookie cookie = cookies[i];
				cookieName = cookie.getName();
				cookieValue = cookie.getValue();
				cookiePath = cookie.getPath();
				if(cookiePath == null) {
					cookiePath = "/";
				}
		%>
		<PARAM NAME="CookieName<%=i+1%>" VALUE="<%=cookieName%>">
		<PARAM NAME="CookieValue<%=i+1%>" VALUE="<%=cookieValue%>">
		<PARAM NAME="CookiePath<%=i+1%>" VALUE="<%=cookiePath%>">
		<%
			}
		%>
</OBJECT>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/wmsim.js"></script>
<script language="javascript">

	function getShipmentKey() {
		var oObj = new Object();
		var oShipmentNo = new Object();
		var oShipmentKey = new Object();
		oObj.field1 = oShipmentNo;
		oObj.field2 = oShipmentKey;
		yfcShowSearchPopup('','poshipmentlookup',900,550,oObj,'poshipmentlookup');
		return oShipmentKey.value;
	}

	function getReturnShipmentKey() {
		var oObj = new Object();
		var oShipmentNo = new Object();
		var oShipmentKey = new Object();
		oObj.field1 = oShipmentNo;
		oObj.field2 = oShipmentKey;
		yfcShowSearchPopup('','returnshipmentlookup',900,550,oObj,'returnshipmentlookup');
		return oShipmentKey.value;
	}
	
	function getLocationId(obj) {
		var oObj = new Object();
		var oLocationId = new Object();
		oObj.field1 = oLocationId;
		yfcShowSearchPopupWithParams('','location',900,550,oObj,'location','xml:/Location/@Node='+obj);
		return oLocationId.value;
	}

	function getReceiptNo(obj) {
		var oObj = new Object();
		var oReceiptNo = new Object();
		oObj.field1 = oReceiptNo;
yfcShowSearchPopupWithParams('','receiptlookup',900,550,oObj,'receiptlookup','xml:/Receipt/@ShipmentKey='+obj);
		return oReceiptNo.value;
	}

	function getReturnReceiptNo(obj) {
		var oObj = new Object();
		var oReceiptNo = new Object();
		oObj.field1 = oReceiptNo;
yfcShowSearchPopupWithParams('','returnreceiptlookup',900,550,oObj,'returnreceiptlookup','xml:/Receipt/@ShipmentKey='+obj);
		return oReceiptNo.value;
	}
	
	function getReceiptNo() {
		var oObj = new Object();
		var oReceiptNo = new Object();
		oObj.field1 = oReceiptNo;
		yfcShowSearchPopupWithParams
		('','receiptlookup',900,550,oObj,'receiptlookup');
		return oReceiptNo.value;
	}

	function getReturnReceiptNo() {
		var oObj = new Object();
		var oReceiptNo = new Object();
		oObj.field1 = oReceiptNo;
		yfcShowSearchPopupWithParams
		('','returnreceiptlookup',900,550,oObj,'returnreceiptlookup');
		return oReceiptNo.value;
	}

	function getCOO() {
		var oObj = new Object();
		var oCOO = new Object();
		oObj.field1 = oCOO;
		yfcShowSearchPopup('','countryoforigin',900,550,oObj,'countryoforigin');
		return oCOO.value;
	}

	function getReceiptOrderLookup(){
		var oObj = new Object();
		var oOrderNo = new Object();
		oObj.field1 = oOrderNo;
		yfcShowSearchPopup('','polookup',900,550,oObj,'polookup');
		return oOrderNo.value;
	}

	function getDateFrmCalendar() {
		
		var myObject = new Object();
		myObject.datevalue = "";  
		
		var sFeatures = "dialogHeight:225px;dialogWidth:160px;dialogLeft:"  + 540 + "px;dialogTop:" + 100 + "px;scroll:no;resizable:no;help:no;status:no;edge:raised;unadorned:yes";
		var newDlg=window.sc.csrf.showModalDialog(contextPath+'/yfc/calendar.jsp', myObject, sFeatures);
		
		return myObject.datevalue;
	}


	function getTimeFrmTimer() {

		var myObject = new Object();
        myObject.timevalue = "";

		var sFeatures = "dialogHeight:185px;dialogWidth:165px;dialogLeft:"  + 540 + "px;dialogTop:" + 100 + "px;scroll:no;resizable:yes;help:no;status:no;edge:raised;unadorned:yes";
        var newDlg = window.sc.csrf.showModalDialog(contextPath+'/yfc/timelookup.jsp', myObject, sFeatures);
        
        return myObject.timevalue;
	}
		
</script>

<%!
// Method to get jars to download
	StringBuffer addTokenToJarsIfRequired(StringBuffer jarListFile) {
	   return YFCJarDownloadUtil.addTokenToJarsIfRequired(request, jarListFile);
	}
%>
