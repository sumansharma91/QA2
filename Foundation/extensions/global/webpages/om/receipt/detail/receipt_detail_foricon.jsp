<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%> 
<%@include file="/console/jsp/currencyutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<%	if (getNumericValue(getValue("ReceiptList","xml:/ReceiptList/@TotalNumberOfRecords")) == 0) { %>
		<yfc:callAPI apiID="AP3"/>
<%	}	%>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script> 
<table class="table" editable="false" width="100%" cellspacing="0">
    <thead> 
        <tr>
            <td sortable="no" class="checkboxheader">
                <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
            </td>
            <td class="tablecolumnheader"><yfc:i18n>Receipt_#</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Shipment_#</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Order_#</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Enterprise</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Buyer</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Seller</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Receipt_Start_Date</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Receiving_Node</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Receipt_Open</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Receipt_Status</yfc:i18n></td>
        </tr>
    </thead> 
    <tbody> 
	    <yfc:loopXML binding="xml:ReceiptList:/ReceiptList/@Receipt" id="Receipt" >
            <tr>
				<yfc:makeXMLInput name="receiptKey">
					<yfc:makeXMLKey binding="xml:/Receipt/@ReceiptHeaderKey" value="xml:/Receipt/@ReceiptHeaderKey"></yfc:makeXMLKey>
					<yfc:makeXMLKey binding="xml:/Receipt/@ReceiptNo" value="xml:/Receipt/@ReceiptNo"></yfc:makeXMLKey>
					<yfc:makeXMLKey binding="xml:/Receipt/@ReceivingNode" value="xml:/Receipt/@ReceivingNode"></yfc:makeXMLKey>
					<yfc:makeXMLKey binding="xml:/Receipt/@ReceivingDock" value="xml:/Receipt/@ReceivingDock"></yfc:makeXMLKey>
					<yfc:makeXMLKey binding="xml:/Receipt/@DocumentType" value="xml:/Receipt/Shipment/@DocumentType"></yfc:makeXMLKey>
				</yfc:makeXMLInput>
	            <td class="checkboxcolumn">
					<input type="checkbox" value='<%=getParameter("receiptKey")%>' name="chkEntityKey"/>
				</td>
	            <td class="tablecolumn">
				 <a <%=getDetailHrefOptions("L01", getParameter("receiptKey"), "")%>>
				 		<yfc:getXMLValue binding="xml:/Receipt/@ReceiptNo"/>
				 </a>	
				</td>
	            <td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/Receipt/Shipment/@ShipmentNo"/>
				</td>
	            <td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/Receipt/Shipment/@OrderNo"/>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/Receipt/Shipment/@EnterpriseCode"/>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/Receipt/Shipment/@BuyerOrganizationCode"/>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/Receipt/Shipment/@SellerOrganizationCode"/>
				</td>
	            <td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/Receipt/@ReceiptDate"/>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/Receipt/@ReceivingNode"/>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/Receipt/@OpenReceiptFlag"/>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValueI18NDB binding="xml:/Receipt/Status/@StatusName"/>
				</td>
            </tr>
        </yfc:loopXML>
   </tbody>
</table>
