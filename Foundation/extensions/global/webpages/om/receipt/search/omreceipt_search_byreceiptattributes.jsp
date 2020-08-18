<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%> 
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<table class="view">
<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
  <jsp:param name="ShowDocumentType" value="true"/>
  <jsp:param name="DocumentTypeBinding" value="xml:/Receipt/Shipment/@DocumentType"/>
  <jsp:param name="RefreshOnDocumentType" value="true"/>
  <jsp:param name="EnterpriseCodeBinding" value="xml:/Receipt/Shipment/@EnterpriseCode"/>
  <jsp:param name="RefreshOnEnterpriseCode" value="true"/>
</jsp:include>
<yfc:callAPI apiID="AP2"/>
<tr> 
    <td>
        <input type="hidden" name="xml:yfcSearchCriteria:/Receipt/@ReceiptDateQryType" value="DATERANGE"/>
    </td>
</tr> 
<tr> 
	<td class="searchlabel" ><yfc:i18n>Receiving_Node</yfc:i18n></td>
</tr>
<tr>
	<td class="protectedtext" nowrap="true">
		<% if(isShipNodeUser()) {%>
			<%=getValue("CurrentUser","xml:/User/@Node")%>
			<input type="hidden" <%=getTextOptions("xml:/Receipt/@ReceivingNode", "xml:CurrentUser:/User/@Node")%>/>
		<%} else { %>
			<select name="xml:yfcSearchCriteria:/Receipt/@ReceivingNodeQryType" class="combobox" >
				<yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" 
					name="QueryTypeDesc" value="QueryType" selected="xml:yfcSearchCriteria:/Receipt/@ReceivingNodeQryType"/>
	        </select>
	 	    <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/@ReceivingNode")%>/>
			<% String extraParams = getExtraParamsForTargetBinding("xml:/ShipNode/Organization/EnterpriseOrgList/OrgEnterprise/@EnterpriseOrganizationKey", getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode")); %>
			<img class="lookupicon" onclick="callLookup(this,'nodelookup','<%=extraParams%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Receiving_Node") %>/>
		<%}%>
    </td>
</tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>Receiving_Dock</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/@ReceivingDock")%> />
    </td>
</tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>Receipt_#</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" <%=getComboOptions("xml:/Receipt/@ReceiptNoQryType")%>>
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/Receipt/@ReceiptNoQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/@ReceiptNo")%> />
    </td>
</tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>Buyer</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" <%=getComboOptions("xml:/Receipt/Shipment/@BuyerOrganizationCodeQryType")%>>
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/Receipt/Shipment/@BuyerOrganizationCodeQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/Shipment/@BuyerOrganizationCode")%> />
		<% String enterpriseCode = getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode");%>
		<% String sDocType = getValue("CommonFields", "xml:/CommonFields/@DocumentType");%>
        <img class="lookupicon" onclick="callLookupForOrder(this,'BUYER','<%=enterpriseCode%>','<%=sDocType%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
    </td>
</tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>Seller</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" <%=getComboOptions("xml:/Receipt/Shipment/@SellerOrganizationCodeQryType")%>>
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/Receipt/Shipment/@SellerOrganizationCodeQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/Shipment/@SellerOrganizationCode")%> />
        <img class="lookupicon" onclick="callLookupForOrder(this,'SELLER','<%=enterpriseCode%>','<%=sDocType%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
    </td>
</tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>Order_#</yfc:i18n></td>
</tr>    
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" <%=getComboOptions("xml:/Receipt/Shipment/@OrderNoQryType")%>>
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/Receipt/Shipment/@OrderNoQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/Shipment/@OrderNo")%> />
    </td>
</tr>
<tr>  
    <td class="searchlabel" ><yfc:i18n>Shipment_#</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" <%=getComboOptions("xml:/Receipt/Shipment/@ShipmentNoQryType")%>>
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/Receipt/Shipment/@ShipmentNoQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/Shipment/@ShipmentNo")%> />
    </td>
</tr>
<tr>
    <td class="searchlabel" >
        <yfc:i18n>Receipt_Date</yfc:i18n>
    </td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <input class="dateinput" type="text" <%=getTextOptions("xml:/Receipt/@FromReceiptDate")%> />
        <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
        <yfc:i18n>To</yfc:i18n>
        <input class="dateinput" type="text" <%=getTextOptions("xml:/Receipt/@ToReceiptDate")%> />
        <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
    </td>
</tr>
<tr>
    <td  class="searchlabel" ><yfc:i18n>Receipt_Status</yfc:i18n></td>
</tr>
<tr>
	<td class="searchcriteriacell">
		<select  class="combobox" <%=getComboOptions("xml:/Receipt/@Status")%>>
			<yfc:loopOptions binding="xml:Status:/StatusList/@Status" name="StatusName" value="Status" selected="xml:/Receipt/@Status" isLocalized="Y"/>
        </select>
	</td>
</tr>
<tr>
<td class="searchcriteriacell">
	<input type="checkbox" <%=getCheckBoxOptions("xml:/Receipt/@OpenReceiptFlag", "xml:/Receipt/@OpenReceiptFlag", "N")%> yfcCheckedValue='N' yfcUnCheckedValue=''><yfc:i18n>Closed_Receipts_Only</yfc:i18n></input>
</td>
</tr>
</table>
