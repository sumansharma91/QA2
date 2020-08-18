<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.util.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/wmsim.js"></script>
<script language="javascript">
	function proceedClicked(obj){
			var tFlag=document.all("xml:/Receipt/@TempFlag");
			tFlag.value="Y";
			   yfcChangeDetailView(getCurrentViewId());
	}
</script>
<table width="100%" class="view">
<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
		<jsp:param name="ScreenType" value="detail"/>
		<jsp:param name="ShowDocumentType" value="true"/>
		<jsp:param name="DocumentTypeBinding" value="xml:/Receipt/@DocumentType"/>
		<jsp:param name="ShowNode" value="true"/>
		<jsp:param name="EnterpriseCodeBinding" value="xml:/Receipt/@EnterpriseCode"/>
		<jsp:param name="NodeBinding" value="xml:/Receipt/@ReceivingNode"/>
        <jsp:param name="RefreshOnNode" value="true"/>
		 <jsp:param name="RefreshOnEnterpriseCode" value="true"/>
        <jsp:param name="EnterpriseListForNodeField" value="true"/>
</jsp:include>
<%if(equals("Y",resolveValue("xml:/Receipt/@TempFlag")))
{
			YFCElement receiptInput = YFCDocument.parse("<Receipt ReceivingNode=\"" + resolveValue("xml:/Receipt/@ReceivingNode")  + "\" ReceiptNo=\"" + resolveValue("xml:/Receipt/@ReceiptNo")  + "\" OpenReceiptFlag=\"Y\" EnterpriseCode=\"" + resolveValue("xml:/Receipt/@EnterpriseCode")   +"\"><ReceiptLines><ReceiptLine  ItemID=\"" + resolveValue("xml:/Receipt/@ItemID")  + "\" ProductClass=\"" + resolveValue("xml:/Receipt/@ProductClass")  + "\" UnitOfMeasure=\"" + resolveValue("xml:/Receipt/@UnitOfMeasure")   + "\" CaseId=\"" + resolveValue("xml:/Receipt/@CaseId")  + "\" PalletId=\"" + resolveValue("xml:/Receipt/@PalletId") + "\" SerialNo=\"" + resolveValue("xml:/Receipt/@SerialNo")   +"\"/></ReceiptLines></Receipt>").getDocumentElement();
            YFCElement receiptTemplate = YFCDocument.parse("<ReceiptList TotalNumberOfRecords=\"\"><Receipt ReceiptHeaderKey=\"\"></Receipt></ReceiptList>").getDocumentElement();
            %>
	

            <yfc:callAPI apiName="getReceiptList" inputElement="<%=receiptInput%>" templateElement="<%=receiptTemplate%>" outputNamespace="ReceiptList"/>

		<yfc:makeXMLInput name="receiptKey">
			<yfc:makeXMLKey binding="xml:/Receipt/@ReceivingNode" value="xml:/Receipt/@ReceivingNode"/>
			<yfc:makeXMLKey binding="xml:/Receipt/@DocumentType" value="xml:/Receipt/@DocumentType"/>
			<yfc:makeXMLKey binding="xml:/Receipt/@EnterpriseCode" value="xml:/Receipt/@EnterpriseCode"/>
			<yfc:makeXMLKey binding="xml:/Receipt/@ReceiptNo" value="xml:/Receipt/@ReceiptNo"/>
			<yfc:makeXMLKey binding="xml:/Receipt/@ReceiptHeaderKey" value="xml:ReceiptList:/ReceiptList/Receipt/@ReceiptHeaderKey"/>
			<yfc:makeXMLKey binding="xml:/Receipt/@PalletId" value="xml:/Receipt/@PalletId"/>
			<yfc:makeXMLKey binding="xml:/Receipt/@CaseId" value="xml:/Receipt/@CaseId"/>
			<yfc:makeXMLKey binding="xml:/Receipt/@ItemID" value="xml:/Receipt/@ItemID"/>
			<yfc:makeXMLKey binding="xml:/Receipt/@ProductClass" value="xml:/Receipt/@ProductClass"/>
			<yfc:makeXMLKey binding="xml:/Receipt/@UnitOfMeasure" value="xml:/Receipt/@UnitOfMeasure"/>
			<yfc:makeXMLKey binding="xml:/Receipt/@SerialNo" value="xml:/Receipt/@SerialNo"/>
	</yfc:makeXMLInput>
    <input type="hidden" name="myEntityKey" value='<%=getParameter("receiptKey")%>' />


<%
if(equals("1",resolveValue("xml:ReceiptList:/ReceiptList/@TotalNumberOfRecords")))
{	%>
		<script language="javascript">
			var sVal = document.all("myEntityKey");
			 showDetailFor(sVal.value);  
		</script>
<%}else{%>
		<script language="javascript">
			alert(YFCMSG160);
		</script>
<%}
}
%>
<yfc:callAPI apiID="AP1"/>
<yfc:callAPI apiID="AP2"/>
<yfc:callAPI apiID="AP3"/>
<tr>
	<td class="detaillabel" >
        <yfc:i18n>Receipt_#</yfc:i18n>
    </td>
	<td>
			 <% String receiptParams = getExtraParamsForTargetBinding("xml:/Receipt/Shipment/@EnterpriseCode", getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode")); %>
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/@ReceiptNo")%> />  
	<%	
	String slookup="receiptlookup";
	String sAppCode = resolveValue("xml:/CurrentEntity/@ApplicationCode");
	if(equals("omr", sAppCode) ){
		slookup="returnreceiptlookup";
	}%>
	
            <img class="lookupicon" onclick="callLookup(this,'<%=slookup%>','<%=receiptParams%>&xml:/Receipt/@OpenReceiptFlag=Y')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Receipt") %> />
			<input type="hidden" <%=getTextOptions("xml:/Receipt/@ReceiptHeaderKey")%>/>
	</td>
    <td class="detaillabel" >
        <yfc:i18n>Pallet_ID</yfc:i18n>
    </td>
    <td nowrap="true">
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/@PalletId","xml:/Receipt/@PalletId")%> />
    </td>

	<td class="detaillabel" >
        <yfc:i18n>Case_ID</yfc:i18n>
    </td>
    <td nowrap="true">
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/@CaseId","xml:/Receipt/@CaseId")%> />
    </td>

</tr>
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Item_ID</yfc:i18n>
    </td>
    <td nowrap="true">
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/@ItemID","xml:/Receipt/@ItemID")%> />
		 <% String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode")); %>
		<img class="lookupicon" name="search"onclick="callItemLookup('xml:/Receipt/@ItemID','xml:/Receipt/@ProductClass','xml:/Receipt/@UnitOfMeasure','item','<%=extraParams%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item") %> />
    </td>
	<td class="detaillabel" >
        <yfc:i18n>Product_Class</yfc:i18n>
    </td>
    <td nowrap="true">
		<select name="xml:/Receipt/@ProductClass" class="combobox" >
			<yfc:loopOptions binding="xml:ProductClass:/CommonCodeList/@CommonCode" 
				name="CodeValue" value="CodeValue" selected="xml:/Receipt/@ProductClass" />
		</select>
    </td>

	<td class="detaillabel" >
        <yfc:i18n>Unit_Of_Measure</yfc:i18n>
    </td>
    <td nowrap="true">
        <select name="xml:/Receipt/@UnitOfMeasure" class="combobox">
            <yfc:loopOptions binding="xml:UnitOfMeasureList:/ItemUOMMasterList/@ItemUOMMaster" name="UnitOfMeasure" value="UnitOfMeasure" selected="xml:/Receipt/@UnitOfMeasure"/>
        </select>
    </td>
</tr>
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Serial_#</yfc:i18n>
    </td>
    <td nowrap="true">
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/@SerialNo","xml:/Receipt/@SerialNo")%> />
    </td>
	<td>
    </td>
    <td>
    </td>
	<td>
    </td>
    <td>
    </td>
</tr>
<tr>
    <td colspan="8" align="center">
		<input type="hidden" name="xml:/Receipt/@TempFlag"/>
        <input class="button" type="button" value="<%=getI18N("Proceed")%>" onclick="proceedClicked(this);" />
    </td>
</tr>
</table>
