<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<table class="view" width="100%">
    <tr>
        <td class="detaillabel" >
            <yfc:i18n>Unit_Price</yfc:i18n>
        </td>
        <td>
            <input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/LinePriceInfo/@UnitPrice", "xml:/OrderLine/LinePriceInfo/@UnitPrice", "xml:/OrderLine/AllowedModifications")%>>
        </td>
        <td class="detaillabel" >
            <yfc:i18n>Customer_PO_#</yfc:i18n>
        </td>
        <td>
            <input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/@CustomerPONo", "xml:/OrderLine/@CustomerPONo", "xml:/OrderLine/AllowedModifications")%>>
        </td>
        <td class="detaillabel" >
            <yfc:i18n>Carrier_Service</yfc:i18n>
        </td>
        <td>
            <select <%=yfsGetComboOptions("xml:/Order/OrderLines/OrderLine/@ScacAndServiceKey", "xml:/OrderLine/@ScacAndServiceKey", "xml:/OrderLine/AllowedModifications")%> >
                <yfc:loopOptions binding="xml:/ScacAndServiceList/@ScacAndService" name="ScacAndServiceDesc" value="ScacAndServiceKey" selected="xml:/OrderLine/@ScacAndServiceKey" isLocalized="Y" targetBinding="xml:/Order/OrderLines/OrderLine/@ScacAndServiceKey"/>
            </select>
        </td>
    </tr>
	<yfc:callAPI apiID='AP5'/>
	<tr>
        <td class="detaillabel" ><yfc:i18n>Is_Returnable</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@IsReturnable"/></td>
        <td class="detaillabel" ><yfc:i18n>Return_Window_Days</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@ReturnWindow"/></td>
        <td class="detaillabel" ><yfc:i18n>Credit_WO_Receipt</yfc:i18n></td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@CreditWOReceipt"/></td>
	</tr>
	<tr>
    <td class="detaillabel" ><yfc:i18n>Invoiced_Qty</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderLine/@InvoicedQty"></yfc:getXMLValue>&nbsp;</td>
    <td class="detaillabel" ><yfc:i18n>Over_Receipt_Qty</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderLine/@OverReceiptQuantity"></yfc:getXMLValue>&nbsp;</td>
    <td class="detaillabel" ><yfc:i18n>Return_Reason</yfc:i18n></td>
	<td class="tablecolumn">
		<select <%=yfsGetComboOptions("xml:/Order/OrderLines/OrderLine/@ReturnReason", "xml:/OrderLine/@ReturnReason", "xml:/OrderLine/AllowedModifications")%>>
			<yfc:loopOptions binding="xml:ReasonCodeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="xml:/OrderLine/@ReturnReason" isLocalized="Y" targetBinding="xml:/Order/OrderLines/OrderLine/@ReturnReason"/>
		</select>
	</td>
	</tr>
	<tr>
        <td class="detaillabel" >
            <yfc:i18n>Shipped_Quantity</yfc:i18n>
        </td>
        <td class="protectedtext">
			<yfc:getXMLValue name="OrderLine" binding="xml:/OrderLine/OrderLineTranQuantity/@ShippedQuantity"/>
		</td>
        <td class="detaillabel" >
            <yfc:i18n>Received_Quantity</yfc:i18n>
        </td>
        <td class="protectedtext">
			<yfc:getXMLValue name="OrderLine" binding="xml:/OrderLine/OrderLineTranQuantity/@ReceivedQty"/>
		</td>
		<td class="detaillabel" >
			<yfc:i18n>Fulfillment_Type</yfc:i18n>
		</td>
		<td>
            <select class="combobox" <%=yfsGetComboOptions("xml:/Order/OrderLines/OrderLine/@FulfillmentType", "xml:/OrderLine/@FulfillmentType", "xml:/OrderLine/AllowedModifications")%> >
				<yfc:loopOptions binding="xml:FulfillmentTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="xml:/OrderLine/@FulfillmentType" isLocalized="Y" targetBinding="xml:/Order/OrderLines/OrderLine/@FulfillmentType"/>
			</select>
		</td>
	</tr>
	<tr>
	<yfc:hasXMLNode binding="xml:/OrderLine/@OrigOrderLineKey">
		<yfc:makeXMLInput name="origOrderLineKey">
			<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderLine/@OrigOrderLineKey"/>
		</yfc:makeXMLInput>
		<yfc:callAPI apiID='AP1'/>
		<tr>
		    <td class="detaillabel" ><yfc:i18n>Kit_Parent_Line</yfc:i18n></td>
			<td>
				<% if(showOrderLineNo("Order","Order")) {%>
					<a <%=getDetailHrefOptions("L01", getParameter("origOrderLineKey"), "")%>><yfc:getXMLValue binding="xml:OrigOrderLineDetails:/OrderLine/@PrimeLineNo"/></a>
				<%} else {%>
					<yfc:getXMLValue binding="xml:OrigOrderLineDetails:/OrderLine/@PrimeLineNo"/>
				<%}%>
			</td>
		</tr>
	</yfc:hasXMLNode>
	</tr>
	<tr>
		<td height="100%" colspan="6">
		<fieldset>
		<legend><b><yfc:i18n>Pickup_Attributes</yfc:i18n></b></legend> 
			<table height="100%" width="100%" >
				<tr>
					<td class="detaillabel" >
						<yfc:i18n>Pickup_Method</yfc:i18n>
					</td>
					<td>
						<%	//	Show only valid delivery options. 
							//  If nothing is allowed show only shipping
							YFCElement eDelMethod = (YFCElement)request.getAttribute("DeliveryMethodList");
							YFCElement eItemDetail = ((YFCElement)request.getAttribute("Item"));

							modifyDelMethodList(eDelMethod, eItemDetail);
						%>
						<select <%=yfsGetComboOptions("xml:/Order/OrderLines/OrderLine/@DeliveryMethod", "xml:/OrderLine/@DeliveryMethod", "xml:/OrderLine/AllowedModifications")%> >
							<yfc:loopOptions binding="xml:DeliveryMethodList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="xml:/OrderLine/@DeliveryMethod" suppressBlank="Y" isLocalized="Y" targetBinding="xml:/Order/OrderLines/OrderLine/@DeliveryMethod"/>
						</select>
					</td>
					<td class="detaillabel" >
						<yfc:i18n>Associated_Pickup_Line_#</yfc:i18n>
					</td>
					<yfc:makeXMLInput name="deliveryLineKey">
						<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderLine/DeliveryAssociations/DeliveryAssociation/ServiceLine/@OrderLineKey" ></yfc:makeXMLKey>
					</yfc:makeXMLInput>
					<td class="protectedtext">
				        <a <%=getDetailHrefOptions("L01",getParameter("deliveryLineKey"),"")%>><yfc:getXMLValue name="OrderLine" binding="xml:/OrderLine/DeliveryAssociations/DeliveryAssociation/ServiceLine/@PrimeLineNo"/></a>
					</td>
					<td class="detaillabel" >
						<yfc:i18n>Carrier_Service</yfc:i18n>
					</td>
					<td>
						<select <%=yfsGetComboOptions("xml:/Order/OrderLines/OrderLine/@ScacAndServiceKey", "xml:/OrderLine/@ScacAndServiceKey", "xml:/OrderLine/AllowedModifications")%> >
							<yfc:loopOptions binding="xml:/ScacAndServiceList/@ScacAndService" name="ScacAndServiceDesc" value="ScacAndServiceKey" selected="xml:/OrderLine/@ScacAndServiceKey" isLocalized="Y" targetBinding="xml:/Order/OrderLines/OrderLine/@ScacAndServiceKey"/>
						</select>
					</td>
				</tr>
				<tr>
					<td class="detaillabel" >
						<yfc:i18n>Carrier_Account_#</yfc:i18n>
					</td>
					<td>
						<input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/@CarrierAccountNo", "xml:/OrderLine/@CarrierAccountNo", "xml:/OrderLine/AllowedModifications")%>>
					</td>
					<td class="detaillabel" >
						<yfc:i18n>Shipping_Paid_By</yfc:i18n>
					</td>
					<td>
						<select <%=yfsGetComboOptions("xml:/Order/OrderLines/OrderLine/@DeliveryCode", "xml:/OrderLine/@DeliveryCode", "xml:/OrderLine/AllowedModifications")%> >
							<yfc:loopOptions binding="xml:DeliveryCodeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="xml:/OrderLine/@DeliveryCode" isLocalized="Y" targetBinding="xml:/Order/OrderLines/OrderLine/@DeliveryCode"/>
						</select>
					</td>
					<td class="detaillabel" >
						<yfc:i18n>Freight_Terms</yfc:i18n>
					</td>
					<td>
						<select <%=yfsGetComboOptions("xml:/Order/OrderLines/OrderLine/@FreightTerms", "xml:/OrderLine/@FreightTerms", "xml:/OrderLine/AllowedModifications")%> >
							<yfc:loopOptions binding="xml:FreightTermsList:/FreightTermsList/@FreightTerms" name="ShortDescription" value="FreightTerms" selected="xml:/OrderLine/@FreightTerms" isLocalized="Y" targetBinding="xml:/Order/OrderLines/OrderLine/@FreightTerms"/>
						</select>
					</td>
				</tr>
			</table>
		</fieldset>
		</td>
	</tr>
</table>
