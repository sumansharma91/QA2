<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<table class="view" width="100%">
    <tr>
        <td>
            <input type="hidden" name="xml:/OrderRelease/@ModificationReasonCode"/>
            <input type="hidden" name="xml:/OrderRelease/@ModificationReasonText"/>
            <input type="hidden" name="xml:/OrderRelease/@Override" value="N"/>

			<input type="hidden" name="xml:/Shipment/OrderReleases/OrderRelease/@AssociationAction"" value='Add' />
			<input type="hidden" name="xml:/Shipment/OrderReleases/OrderRelease/@OrderReleaseKey"" value='<%=resolveValue("xml:/OrderRelease/@OrderReleaseKey")%>' />
			<input type="hidden" name="xml:/Shipment/@OrderNo" value='<%=resolveValue("xml:/OrderRelease/Order/@OrderNo")%>' />
			<input type="hidden" name="xml:/Shipment/@ReleaseNo" value='<%=resolveValue("xml:/OrderRelease/@ReleaseNo")%>' />
			<input type="hidden" name="xml:/Shipment/@EnterpriseCode" value='<%=resolveValue("xml:/OrderRelease/Order/@EnterpriseCode")%>' />
        </td>
    </tr>
    <tr>
        <yfc:makeXMLInput name="statusKey">
            <yfc:makeXMLKey binding="xml:/OrderReleaseDetail/@OrderReleaseKey" value="xml:/OrderRelease/@OrderReleaseKey" ></yfc:makeXMLKey>
        </yfc:makeXMLInput>
        <yfc:makeXMLInput name="ReleaseKey">
            <yfc:makeXMLKey binding="xml:/OrderRelease/@OrderReleaseKey" value="xml:/OrderRelease/@OrderReleaseKey" ></yfc:makeXMLKey>
        </yfc:makeXMLInput>
        <yfc:makeXMLInput name="orderKey">
            <yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/OrderRelease/@OrderHeaderKey" ></yfc:makeXMLKey>
            <yfc:makeXMLKey binding="xml:/Order/@OrderNo" value="xml:/OrderRelease/Order/@OrderNo" ></yfc:makeXMLKey>
            <yfc:makeXMLKey binding="xml:/Order/@EnterpriseCode" value="xml:/OrderRelease/Order/@EnterpriseCode" ></yfc:makeXMLKey>
        </yfc:makeXMLInput>
        <input type="hidden" class="protectedinput" <%=getTextOptions("xml:/OrderRelease/@OrderReleaseKey")%> />
        <input type="hidden" class="protectedinput" <%=getTextOptions("xml:OpenReceiptList:/ReceiptList/Receipt/@ReceiptHeaderKey")%> />
        <input type="hidden" class="protectedinput" <%=getTextOptions("xml:OpenReceiptList:/ReceiptList/Receipt/@DocumentType")%> />
        <input type="hidden" class="protectedinput" <%=getTextOptions("xml:/OrderRelease/@OrderReleaseKey")%> />
		<input type="hidden" name="TempHeaderKey" value='<%=getValue("OrderRelease", "xml:/OrderRelease/@OrderReleaseKey")%>'/>
		<input type="hidden" name="TempBuyer" value='<%=getValue("OrderRelease", "xml:/OrderRelease/Order/@BuyerOrganizationCode")%>'/>
		<input type="hidden" name="TempSeller" value='<%=getValue("OrderRelease", "xml:/OrderRelease/Order/@SellerOrganizationCode")%>'/>
		<input type="hidden" name="TempCurrency" value='<%=getValue("OrderRelease", "xml:/OrderRelease/Order/PriceInfo/@Currency")%>'/>
		<input type="hidden" name="xml:/Order/@EnterpriseCode" value='<%=getValue("OrderRelease", "xml:/OrderRelease/Order/@EnterpriseCode")%>'/>
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
	            <a <%=getDetailHrefOptions("L01",getParameter("orderKey"),"")%>><yfc:getXMLValue binding="xml:/OrderRelease/Order/@OrderNo"></yfc:getXMLValue></a>&nbsp;
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
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderRelease/@OrderDate"></yfc:getXMLValue>&nbsp;</td>
    </tr>
    <tr>
        <td class="detaillabel" >
            <yfc:i18n>Order_Type</yfc:i18n>
        </td>
        <!--<td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderRelease/@OrderType"></yfc:getXMLValue>&nbsp;</td>-->
		<td class="protectedtext"><%=getComboText("xml:OrderTypeList:/CommonCodeList/@CommonCode" ,"CodeShortDescription" ,"CodeValue" ,"xml:/OrderRelease/@OrderType",true)%></td>
        <td class="detaillabel" >
            <yfc:i18n>Release_#</yfc:i18n>
        </td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderRelease/@ReleaseNo"></yfc:getXMLValue>&nbsp;</td>
		<input type="hidden" name="releaseKey" value="<%=getParameter("ReleaseKey")%>"/>
    </tr>
</table>
