<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<table class="view" width="100%">
    <tr>
        <yfc:makeXMLInput name="orderKey">
            <yfc:makeXMLKey binding="xml:/OrderRelease/@OrderHeaderKey" value="xml:/Receipt/@OrderHeaderKey" ></yfc:makeXMLKey>
        </yfc:makeXMLInput>
        <yfc:makeXMLInput name="orderReleaseKey">
            <yfc:makeXMLKey binding="xml:/OrderReleaseDetail/@OrderReleaseKey" value="xml:/Receipt/@OrderReleaseKey" ></yfc:makeXMLKey>
        </yfc:makeXMLInput>
        <td class="detaillabel">
            <yfc:i18n>Receipt_#</yfc:i18n>
        </td>
        <td class="protectedtext">
            <yfc:getXMLValue binding="xml:/Receipt/@ReceiptNo"></yfc:getXMLValue>
        </td>
        <td class="detaillabel" >
            <yfc:i18n>Order_#</yfc:i18n>
        </td>
        <td class="protectedtext">
			<% if(showOrderNo("Receipt","Order")) {%>
	            <a <%=getDetailHrefOptions("L01",getParameter("orderKey"),"")%>>
					<yfc:getXMLValue binding="xml:/Receipt/Order/@OrderNo"></yfc:getXMLValue>
				</a>
			<%} else {%>
				<yfc:getXMLValue binding="xml:/Receipt/Order/@OrderNo"></yfc:getXMLValue>
			<%}%>
        </td>
        <td class="detaillabel" >
            <yfc:i18n>Release_#</yfc:i18n>
        </td>
        <td class="protectedtext">
            <a <%=getDetailHrefOptions("L02",getParameter("orderReleaseKey"),"")%>>
            <yfc:getXMLValue binding="xml:/Receipt/OrderRelease/@ReleaseNo"></yfc:getXMLValue></a>
        </td>
    </tr>
    <tr>
        <td class="detaillabel">
            <yfc:i18n>Receipt_Date</yfc:i18n>
        </td>
        <td class="protectedtext">
            <yfc:getXMLValue binding="xml:/Receipt/@ReceiptDate"></yfc:getXMLValue>
        </td>
        <td class="detaillabel">
            <yfc:i18n>Serial_#</yfc:i18n>
        </td>
        <td class="protectedtext">
            <yfc:getXMLValue binding="xml:CurrentReceiptLine:/ReceiptLine/@SerialNo"></yfc:getXMLValue>
        </td>  
        <td class="detaillabel">
            <yfc:i18n>Lot_#</yfc:i18n>
        </td>
        <td class="protectedtext">
            <yfc:getXMLValue binding="xml:CurrentReceiptLine:/ReceiptLine/@LotNumber"></yfc:getXMLValue>
        </td>  
    </tr>
    <tr>
        <td class="detaillabel">
            <yfc:i18n>Line_#</yfc:i18n>
        </td>
        <td class="protectedtext">
            <yfc:getXMLValue binding="xml:CurrentReceiptLine:/ReceiptLine/@ReceiptLineNo"></yfc:getXMLValue>
        </td>
        <td class="detaillabel">
            <yfc:i18n>Item_ID</yfc:i18n>
        </td>
        <td class="protectedtext">
            <yfc:getXMLValue binding="xml:CurrentReceiptLine:/ReceiptLine/OrderLine/Item/@ItemID"></yfc:getXMLValue>
        </td>
        <td class="detaillabel">
            <yfc:i18n>Product_Class</yfc:i18n>
        </td>
        <td class="protectedtext">
            <yfc:getXMLValue binding="xml:CurrentReceiptLine:/ReceiptLine/OrderLine/Item/@ProductClass"></yfc:getXMLValue>
        </td>
    </tr>
    <tr>
        <td class="detaillabel">
            <yfc:i18n>Unit_Of_Measure</yfc:i18n>
        </td>
        <td class="protectedtext">
            <yfc:getXMLValue binding="xml:CurrentReceiptLine:/ReceiptLine/OrderLine/Item/@UnitOfMeasure"></yfc:getXMLValue>
        </td>
        <td class="detaillabel"><yfc:i18n>Quantity</yfc:i18n></td>
        <td class="protectedtext" ><yfc:getXMLValue binding="xml:CurrentReceiptLine:/ReceiptLine/@Quantity"></yfc:getXMLValue></td>
        <td class="detaillabel">
            <yfc:i18n>Description</yfc:i18n>
        </td>
        <td class="protectedtext" >
            <yfc:getXMLValue binding="xml:CurrentReceiptLine:/ReceiptLine/OrderLine/Item/@ItemDesc"></yfc:getXMLValue>
        </td>
    </tr>
	<tr>
        <td class="detaillabel"><yfc:i18n>Disposition</yfc:i18n></td>
        <td class="protectedtext" >
			<!--<yfc:getXMLValue binding="xml:CurrentReceiptLine:/ReceiptLine/@DispositionCode"></yfc:getXMLValue>-->
			<%=getComboText("xml:/ReturnDispositionList/@ReturnDisposition" ,"Description" ,"DispositionCode" ,"xml:CurrentReceiptLine:/ReceiptLine/@DispositionCode",true)%>
		</td>
        <td class="detaillabel"><yfc:i18n>Comments</yfc:i18n></td>
        <td class="protectedtext" ><yfc:getXMLValue binding="xml:CurrentReceiptLine:/ReceiptLine/@InspectionComments"></yfc:getXMLValue></td>
        <td class="detaillabel"><yfc:i18n>Ship_By_Date</yfc:i18n></td>
        <td class="protectedtext" ><yfc:getXMLValue binding="xml:CurrentReceiptLine:/ReceiptLine/@ShipByDate"></yfc:getXMLValue></td>
	</tr>
</table>
