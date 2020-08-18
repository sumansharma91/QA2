<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript">
    function window.onload() {
        if (!yfcBodyOnLoad() && (!document.all('YFCDetailError'))) {
            return;
        }       
	
        <% 
			String sOrderHeaderKey = resolveValue("xml:CreatedFromTemplate:/Order/@OrderHeaderKey");
			if (!isVoid(sOrderHeaderKey)) { 
			YFCDocument orderDoc = YFCDocument.createDocument("Order");
		   	orderDoc.getDocumentElement().setAttribute("OrderHeaderKey", resolveValue("xml:CreatedFromTemplate:/Order/@OrderHeaderKey"));
		%>
			entityType = "<%=getEntityIDForLink("L01", getValue("CreatedFromTemplate", "xml:/Order/@DocumentType"))%>";
			showDetailFor('<%=orderDoc.getDocumentElement().getString(false)%>');
		<%}%>        
    }
</script> 

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<%  
    String modifyView = request.getParameter("ModifyView");
    modifyView = modifyView == null ? "" : modifyView;
%>

<table class="view" width="100%">
    <yfc:makeXMLInput name="orderKey">
        <yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/Order/@OrderHeaderKey"/>
    </yfc:makeXMLInput>

	<input type="hidden" name="xml:/CopyOrderQ/@CopyFromOrderHeaderKey23" value="<%=getValue("Order","xml:/Order/@OrderHeaderKey")%>" />
    <tr>
        <td>
            <input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>'/>
            <input type="hidden" name="xml:/Order/@ModificationReasonCode"/>
            <input type="hidden" name="xml:/Order/@ModificationReasonText"/>
            <input type="hidden" name="xml:/Order/@Override" value="N"/>
			<input type="hidden" name="xml:/Order/@EnterpriseCode" value='<%=getValue("Order", "xml:/Order/@EnterpriseCode")%>'/>
			<input type="hidden" name="xml:/Order/@DocumentType" value='<%=getValue("Order", "xml:/Order/@DocumentType")%>'/>
        </td>
    </tr>
    <tr>
        <td class="detaillabel" ><yfc:i18n>Enterprise</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@EnterpriseCode"/></td>
        <td class="detaillabel" ><yfc:i18n>Buyer</yfc:i18n></td>
		<%if (isModificationAllowed("xml:/Order/@BuyerOrganizationCode","xml:/Order/AllowedModifications") && !isVoid(modifyView)) { %>
        <td nowrap="true" >
            <input type="text" <%=yfsGetTextOptions("xml:/Order/@BuyerOrganizationCode", "xml:/Order/AllowedModifications")%>/>
            <img class="lookupicon" onclick="callLookupForOrder(this,'BUYER','xml:/Order/@EnterpriseCode','xml:/Order/@DocumentType')" <%=yfsGetImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization", "xml:/Order/@BuyerOrganizationCode", "xml:/Order/AllowedModifications")%>/>
        </td>
		<%} else {%>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@BuyerOrganizationCode"/></td>
		<%}%>
        <td class="detaillabel" ><yfc:i18n>Seller</yfc:i18n></td>
			<%if (isModificationAllowed("xml:/Order/@SellerOrganizationCode","xml:/Order/AllowedModifications") && !isVoid(modifyView)) { %>
			<td nowrap="true">
				<input type="text" <%=yfsGetTextOptions("xml:/Order/@SellerOrganizationCode", "xml:/Order/AllowedModifications")%>/>
				<img class="lookupicon" name="search" onclick="callLookupForOrder(this,'SELLER','xml:/Order/@EnterpriseCode','xml:/Order/@DocumentType')" <%=yfsGetImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization", "xml:/Order/@SellerOrganizationCode", "xml:/Order/AllowedModifications")%>/>
			</td>
		<% } else { %>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@SellerOrganizationCode"/></td>
		<%}%>
    </tr>
    <tr>
        <td class="detaillabel" ><yfc:i18n>Order_#</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@OrderNo"/></td>
        <td class="detaillabel" ><yfc:i18n>Order_Date</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@OrderDate"/></td>
        <td class="detaillabel" ><yfc:i18n>Carrier_Service</yfc:i18n></td>
        <td>
            <select <% if (isVoid(modifyView)) {%> <%=getProtectedComboOptions()%> <%}%> <%=yfsGetComboOptions("xml:/Order/@ScacAndServiceKey", "xml:/Order/AllowedModifications")%>>
                <yfc:loopOptions binding="xml:/ScacAndServiceList/@ScacAndService" name="ScacAndServiceDesc" value="ScacAndServiceKey" selected="xml:/Order/@ScacAndServiceKey" isLocalized="Y"/>
            </select>
        </td>
    </tr>
    <tr>
	<%  
		if(equals("true", modifyView))
		{
	%>
		<td class="detaillabel" >
			<yfc:i18n>Document_Type</yfc:i18n>
		</td>
		<td class="protectedtext">
			<yfc:getXMLValueI18NDB binding="xml:DocumentParamsList:/DocumentParamsList/DocumentParams/@Description"></yfc:getXMLValueI18NDB>
		</td>
	<%  }	%>

        <td class="detaillabel"><yfc:i18n>Order_Type</yfc:i18n></td>
        <td class="protectedtext">
            <select <% if (isVoid(modifyView)) {%> <%=getProtectedComboOptions()%> <%}%> <%=yfsGetComboOptions("xml:/Order/@OrderType", "xml:/Order/AllowedModifications")%>>
                <yfc:loopOptions binding="xml:OrderTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
                value="CodeValue" selected="xml:/Order/@OrderType" isLocalized="Y"/>
            </select>
        </td>
        <td class="detaillabel" ><yfc:i18n>Default_Template</yfc:i18n></td>
        <td>
            <input type="checkbox" <% if (isVoid(modifyView)) {%> <%=getProtectedComboOptions()%> <%}%> <%=yfsGetCheckBoxOptions("xml:/Order/@DefaultTemplate","xml:/Order/@DefaultTemplate","Y","xml:/Order/AllowedModifications")%>/>
        </td>
        <td/><td/>
    </tr>
</table>
