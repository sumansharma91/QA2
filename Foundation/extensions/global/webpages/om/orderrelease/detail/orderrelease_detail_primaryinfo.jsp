<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<table class="view" width="100%">
    <tr>
        <td>
            <input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>'/>
            <input type="hidden" name="xml:/OrderRelease/@ModificationReasonCode"/>
            <input type="hidden" name="xml:/OrderRelease/@ModificationReasonText"/>
            <input type="hidden" name="xml:/OrderRelease/@Override" value="N"/>
            <input type="hidden" <%=getTextOptions("xml:/Shipment/OrderReleases/OrderRelease/@OrderReleaseKey","xml:/OrderRelease/@OrderReleaseKey")%> />
            <input type="hidden" <%=getTextOptions("xml:/Shipment/@DocumentType","xml:/OrderRelease/Order/@DocumentType")%> />	<%-- cr 36811--%>
			<yfc:makeXMLInput name="shipmentKey">
				<yfc:makeXMLKey binding="xml:/Shipment/@ShipmentKey" value="xml:CreateShipment:/Shipment/@ShipmentKey" />
			</yfc:makeXMLInput>
			<input type="hidden" name="hidShipmentEntityKey" value='<%=getParameter("shipmentKey")%>' />
			<input type="hidden" <%=getTextOptions("hidShipmentKey","xml:CreateShipment:/Shipment/@ShipmentKey")%> />
        </td>
    </tr>
    <tr>
        <yfc:makeXMLInput name="statusKey">
            <yfc:makeXMLKey binding="xml:/OrderReleaseDetail/@OrderReleaseKey" value="xml:/OrderRelease/@OrderReleaseKey" ></yfc:makeXMLKey>
        </yfc:makeXMLInput>
        <yfc:makeXMLInput name="orderKey">
            <yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/OrderRelease/@OrderHeaderKey" ></yfc:makeXMLKey>
        </yfc:makeXMLInput>
        <input type="hidden" class="protectedinput" <%=getTextOptions("xml:/OrderRelease/@OrderReleaseKey")%> />
        <td class="detaillabel" >
            <yfc:i18n>Enterprise</yfc:i18n>
        </td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderRelease/Order/@EnterpriseCode"></yfc:getXMLValue>&nbsp;</td>
        <td class="detaillabel" >
            <yfc:i18n>Buyer</yfc:i18n>
        </td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderRelease/Order/@BuyerOrganizationCode"></yfc:getXMLValue>&nbsp;</td>
        <td class="detaillabel" >
            <yfc:i18n>Seller</yfc:i18n>
        </td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderRelease/Order/@SellerOrganizationCode"></yfc:getXMLValue>&nbsp;</td>
    </tr>
    <tr>
        <td class="detaillabel" >
            <yfc:i18n>Order_#</yfc:i18n>
        </td>
        <td class="protectedtext">
			<% if(showOrderNo("OrderRelease","Order")) {%>
	            <a <%=getDetailHrefOptions("L01",getParameter("orderKey"),"")%>>
					<yfc:getXMLValue binding="xml:/OrderRelease/Order/@OrderNo"></yfc:getXMLValue>
				</a>&nbsp;
			<%} else {%>
				<yfc:getXMLValue binding="xml:/OrderRelease/Order/@OrderNo"></yfc:getXMLValue>
			<%}%>
        </td>
        <td class="detaillabel" >
            <yfc:i18n>Status</yfc:i18n>
        </td>
        <td class="protectedtext">
            <a <%=getDetailHrefOptions("L02",getParameter("statusKey"),"ShowReleaseNo=N")%>>
            <%=displayOrderStatus(getValue("OrderRelease","xml:/OrderRelease/@MultipleStatusesExist"),getValue("OrderRelease","xml:/OrderRelease/@MaxOrderReleaseStatusDesc"),true)%></a>&nbsp;
        </td>
        <td class="detaillabel" >
            <yfc:i18n>Created_On</yfc:i18n>
        </td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderRelease/@Createts"></yfc:getXMLValue>&nbsp;</td>
    </tr>
    <tr>
        <td class="detaillabel" >
            <yfc:i18n>Order_Type</yfc:i18n>
        </td>
        <td class="protectedtext"><%=getComboText("xml:OrderTypeList:/CommonCodeList/@CommonCode","CodeShortDescription","CodeValue","xml:/OrderRelease/@OrderType",true)%></td>    
        <td class="detaillabel" >
            <yfc:i18n>Release_#</yfc:i18n>
        </td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderRelease/@ReleaseNo"></yfc:getXMLValue>&nbsp;</td>
        <td class="detaillabel" >
            <yfc:i18n>Ship_Advice_#</yfc:i18n>
        </td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderRelease/@ShipAdviceNo"></yfc:getXMLValue>&nbsp;</td>
    </tr>
<tr>
	<%  
	    String modifyView = request.getParameter("ModifyView");
		if(equals("true", modifyView) )
		{
	%>
		<td class="detaillabel" >
			<yfc:i18n>Document_Type</yfc:i18n>
		</td>
		<td class="protectedtext">
			<yfc:getXMLValueI18NDB binding="xml:DocumentParamsList:/DocumentParamsList/DocumentParams/@Description" />
		</td>
	<%  }	%>
</tr>
</table>

