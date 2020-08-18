<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@include file="/console/jsp/order.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script> 
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<table class="view">
<tr>
    <td>
        <input type="hidden" name="xml:/OrderLine/@StatusQryType" value="BETWEEN"/>
        <input type="hidden" name="xml:/OrderLine/Order/@DraftOrderFlag" value="N"/>

		<input type="hidden" name="xml:/OrderLine/Order/OrderHoldType/@Status" value=""/>
		<input type="hidden" name="xml:/OrderLine/Order/OrderHoldType/@StatusQryType" value="" />
    </td>
</tr>

    <jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
        <jsp:param name="RefreshOnDocumentType" value="true"/>
        <jsp:param name="RefreshOnEnterpriseCode" value="true"/>
        <jsp:param name="DocumentTypeBinding" value="xml:/OrderLine/Order/@DocumentType"/>
        <jsp:param name="EnterpriseCodeBinding" value="xml:/OrderLine/Order/@EnterpriseCode"/>
    </jsp:include>
    <% // Now call the APIs that are dependent on the common fields (Doc Type & Enterprise Code)
       // Statuses and Hold Reason Codes are refreshed. %>
    <yfc:callAPI apiID="AP2"/>
    <yfc:callAPI apiID="AP4"/>
	<%
	if(!isTrue("xml:/Rules/@RuleSetValue") )	{
	%>
		<yfc:callAPI apiID="AP6"/>

	<%
		YFCElement listElement = (YFCElement)request.getAttribute("HoldTypeList");

		YFCDocument document = listElement.getOwnerDocument();
		YFCElement newElement = document.createElement("HoldType");

		newElement.setAttribute("HoldType", " ");
		newElement.setAttribute("HoldTypeDescription", getI18N("All_Held_Orders"));

		YFCElement eFirst = listElement.getFirstChildElement();
		if(eFirst != null)	{
			listElement.insertBefore(newElement, eFirst);
		}	else	{
			listElement.appendChild(newElement);
		}
		request.setAttribute("defaultHoldType", newElement);
	}
        // Remove Statuses 'Draft Order Created' and 'Held' from the Status Search Combobox.
        prepareOrderSearchByStatusElement((YFCElement) request.getAttribute("StatusList"));
    %>

<tr>
    <td class="searchlabel" >
        <yfc:i18n>Order_#</yfc:i18n>
    </td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select name="xml:/OrderLine/Order/@OrderNoQryType" class="combobox">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
            value="QueryType" selected="xml:/OrderLine/Order/@OrderNoQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderLine/Order/@OrderNo")%>/>
    </td>
</tr>
<tr>
    <td class="searchlabel" >
        <yfc:i18n>Buyer</yfc:i18n>
    </td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select name="xml:/OrderLine/Order/@BuyerOrganizationCodeQryType" class="combobox">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
            value="QueryType" selected="xml:/OrderLine/Order/@BuyerOrganizationCodeQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderLine/Order/@BuyerOrganizationCode")%>/>
		<% String enterpriseCode = getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode");%>
        <img class="lookupicon" onclick="callLookupForOrder(this,'BUYER','<%=enterpriseCode%>','xml:/OrderLine/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
    </td>
</tr>
<tr>
    <td class="searchlabel" >
        <yfc:i18n>Seller</yfc:i18n>
    </td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select name="xml:/OrderLine/Order/@SellerOrganizationCodeQryType" class="combobox">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
            value="QueryType" selected="xml:/OrderLine/Order/@SellerOrganizationCodeQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderLine/Order/@SellerOrganizationCode")%>/>
        <img class="lookupicon" onclick="callLookupForOrder(this,'SELLER','<%=enterpriseCode%>','xml:/OrderLine/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
    </td>
</tr>
<tr>
    <td class="searchlabel" >
        <yfc:i18n>Buyer_Account_#</yfc:i18n>
    </td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select name="xml:/OrderLine/Order/PaymentMethod/@CustomerAccountNoQryType" class="combobox">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
            value="QueryType" selected="xml:/OrderLine/Order/PaymentMethod/@CustomerAccountNoQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderLine/Order/PaymentMethod/@CustomerAccountNo")%>/>
    </td>
</tr>
<tr>
    <td class="searchlabel" >
        <yfc:i18n>Order_Line_Status</yfc:i18n>
    </td>
</tr>
<tr>
    <td nowrap="true">
        <select name="xml:/OrderLine/@FromStatus" class="combobox">
            <yfc:loopOptions binding="xml:/StatusList/@Status" name="Description" value="Status" selected="xml:/OrderLine/@FromStatus" isLocalized="Y"/>
        </select>
        <span class="searchlabel" ><yfc:i18n>To</yfc:i18n></span>
    </td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select name="xml:/OrderLine/@ToStatus" class="combobox">
			<yfc:loopOptions binding="xml:/StatusList/@Status" name="Description" value="Status" selected="xml:/OrderLine/@ToStatus" isLocalized="Y"/>
        </select>
    </td>
</tr>
	<%	if(isTrue("xml:/Rules/@RuleSetValue") )	{	%>
<tr>
    <td class="searchcriteriacell" nowrap="true">
        <input type="checkbox" <%=getCheckBoxOptions("xml:/OrderLine/Order/@HoldFlag", "xml:/OrderLine/Order/@HoldFlag", "Y")%> yfcCheckedValue='Y' yfcUnCheckedValue=' '><yfc:i18n>Held_Orders</yfc:i18n></input>
    </td>
</tr>
<tr>
    <td class="searchlabel" >
        <yfc:i18n>Hold_Reason_Code</yfc:i18n>
    </td>
</tr>
<tr>
    <td class="searchcriteriacell" nowrap="true">
        <select name="xml:/OrderLine/Order/@HoldReasonCode" class="combobox">
            <yfc:loopOptions binding="xml:HoldReasonCodeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="xml:/OrderLine/Order/@HoldReasonCode" isLocalized="Y"/>
        </select>
    </td>
</tr>
	<%	}	else	{	%>
		<tr>
			<td class="searchcriteriacell">
				<input type="checkbox" <%=getCheckBoxOptions("xml:/OrderLine/Order/@HoldFlag", "xml:/OrderLine/Order/@HoldFlag", "Y")%> yfcCheckedValue='Y' yfcUnCheckedValue=' ' onclick="manageHoldOpts(this, 'xml:/OrderLine/Order/OrderHoldType/')" ><yfc:i18n>Held_Orders_With_Hold_Type</yfc:i18n></input>
			</td>
		</tr>
		<tr>
	        <td class="searchcriteriacell">
				<select resetName="<%=getI18N("All_Held_Orders")%>" onchange="resetObjName(this, 'xml:/OrderLine/Order/OrderHoldType/@HoldType')" name="xml:/OrderLine/Order/OrderHoldType/@HoldType" class="combobox" <%if(isTrue("xml:/OrderLine/Order/@HoldFlag") ) {%> ENABLED <%} else {%> disabled="true" <%}%> >
					<yfc:loopOptions binding="xml:/HoldTypeList/@HoldType" name="HoldTypeDescription" value="HoldType" suppressBlank="Y" selected="xml:/OrderLine/Order/OrderHoldType/@HoldType" isLocalized="Y"/>
				</select>

			</td>
		</tr>
	<%	}	%>
    
</table>
