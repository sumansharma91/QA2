<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%> 
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>

<table class="view">
<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
  <jsp:param name="ShowDocumentType" value="true"/>
  <jsp:param name="DocumentTypeBinding" value="xml:/Receipt/Shipment/@DocumentType"/>
  <jsp:param name="EnterpriseCodeBinding" value="xml:/Receipt/Shipment/@EnterpriseCode"/>
  <jsp:param name="RefreshOnEnterpriseCode" value="true"/>
</jsp:include>
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
    <td>
		 <input type="hidden" <%=getTextOptions("xml:/Receipt/Shipment/@DocumentType", "xml:/CurrentEntity/@DocumentType")%> />
    </td>
</tr>
<tr>
	<td class="searchlabel" ><yfc:i18n>Pallet_ID</yfc:i18n></td>
</tr>
<tr>
    <td class="searchcriteriacell" nowrap="true">
		<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine/@PalletId") %> />
    </td>
</tr>

<tr>
    <td class="searchlabel" ><yfc:i18n>Case_ID</yfc:i18n></td>
</tr>
<tr>
    <td class="searchcriteriacell" nowrap="true">
		<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/ReceiptLines/ReceiptLine/@CaseId") %> />
    </td>
</tr>
</table>

