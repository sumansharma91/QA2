<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<table class="view" width="100%">
    <tr>
		<td class="detaillabel" >
			<yfc:i18n>Receipt_#</yfc:i18n>
		</td>
        <td class="protectedtext">
			<yfc:getXMLValue binding="xml:/Receipts/Receipt/@ReceiptNo"/>
		</td>
        <td class="detaillabel" >
			<yfc:i18n>Receiving_Node</yfc:i18n>
		</td>
        <td class="protectedtext">
			<yfc:getXMLValue binding="xml:/Receipts/Receipt/@ReceivingNode"/>
		</td>
        <td class="detaillabel" >
			<yfc:i18n>Ship_Node</yfc:i18n>
		</td>
        <td class="protectedtext">
			<yfc:getXMLValue binding="xml:/Receipts/Receipt/@ShipNode"/>
		</td>
	</tr>
	<tr>
        <td class="detaillabel" >
			<yfc:i18n>Enterprise</yfc:i18n>
		</td>
        <td class="protectedtext">
			<yfc:getXMLValue binding="xml:/Receipts/Receipt/@EnterpriseCode"/>
		</td>
        <td class="detaillabel" >
			<yfc:i18n>Seller_Organization_Code</yfc:i18n>
		</td>
        <td class="protectedtext">
			<yfc:getXMLValue binding="xml:/Receipts/Receipt/@SellerOrganizationCode"/>
		</td>
        <td class="detaillabel" >
			<yfc:i18n>Buyer_Organization_Code</yfc:i18n>
		</td>
        <td class="protectedtext">
			<yfc:getXMLValue binding="xml:/Receipts/Receipt/@BuyerOrganizationCode"/>
		</td>
	</tr>
	<tr>
        <td class="detaillabel" >
			<yfc:i18n>Status</yfc:i18n>
		</td>
        <td class="protectedtext">
			<yfc:getXMLValue binding="xml:/Receipts/Receipt/@Status"/>
		</td>
			<td class="detaillabel" >
			<yfc:i18n>Shipment_#</yfc:i18n>
		</td>
        <td class="protectedtext">
			<yfc:getXMLValue binding="xml:/Receipts/Receipt/@ShipmentNo"/>
		</td>
        <td class="detaillabel" >
			<yfc:i18n>Expected_Shipment_Date</yfc:i18n>
		</td>
        <td class="protectedtext">
			<yfc:getXMLValue binding="xml:/Receipts/Receipt/@ExpectedShipmentDate"/>
		</td>
	</tr>
	<tr>
        <td class="detaillabel" >
			<yfc:i18n>Pick_Ticket_#</yfc:i18n>
		</td>
        <td class="protectedtext">
            <yfc:getXMLValue binding="xml:/Receipts/Receipt/@PickTicketNo"/>
            <input type="hidden" <%=getTextOptions( "xml:/Receipts/Receipt/@PickTicketNo", "xml:/Receipts/Receipt/@PickTicketNo")%>/>
        </td>
		</td>
        <td class="detaillabel" >
			<yfc:i18n>Carrier</yfc:i18n>
		</td>
        <td class="protectedtext">
			<yfc:getXMLValue binding="xml:/Receipts/Receipt/@Carrier"/>
		</td>
        <td class="detaillabel" >
			<yfc:i18n>Carrier_Type</yfc:i18n>
		</td>
        <td class="protectedtext">
			<yfc:getXMLValue binding="xml:/Receipts/Receipt/@CarrierType"/>
		</td>
	</tr>
	<tr>
        <td class="detaillabel" >
			<yfc:i18n>Bol_#</yfc:i18n>
		</td>
        <td class="protectedtext">
			<yfc:getXMLValue binding="xml:/Receipts/Receipt/@BolNo"/>
		</td>
        <td class="detaillabel" >
			<yfc:i18n>Trailer_#</yfc:i18n>
		</td>
        <td class="protectedtext">
			<yfc:getXMLValue binding="xml:/Receipts/Receipt/@TrailerNo"/>
		</td>
        <td class="detaillabel" >
			<yfc:i18n>Tracking_#</yfc:i18n>
		</td>
        <td class="protectedtext">
            <yfc:getXMLValue binding="xml:/Receipts/Receipt/@TrackingNo"/>
            <input type="hidden" <%=getTextOptions( "xml:/Receipts/Receipt/@TrackingNo", "xml:/Receipts/Receipt/@TrackingNo")%>/>
		</td>
	</tr>
</table>
