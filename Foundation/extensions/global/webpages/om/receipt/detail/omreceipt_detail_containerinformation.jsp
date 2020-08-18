<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%> 
<table class="table">
<thead>
    <tr>  
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@ItemID")%>">
            <yfc:i18n>Item_ID</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/OrderLine/Item/@ItemShortDesc")%>">
            <yfc:i18n>Item_Description</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@ProductClass")%>">
            <yfc:i18n>PC</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@UnitOfMeasure")%>">
            <yfc:i18n>UOM</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@DispositionCode")%>">
            <yfc:i18n>Disposition_Code</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@OrderNo")%>">
            <yfc:i18n>Order_#</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@PrimeLineNo")%>">
            <yfc:i18n>Line_#</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@ReleaseNo")%>">
            <yfc:i18n>Release_#</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@ShipByDate")%>">
            <yfc:i18n>Ship_By_Date</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@CountryOfOrigin")%>">
            <yfc:i18n>COO</yfc:i18n> 
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@SerialNo")%>">
            <yfc:i18n>Serial_#</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@TagNumber")%>">
            <yfc:i18n>Tag</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@Quantity")%>">
            <yfc:i18n>Quantity</yfc:i18n>
        </td>
    </tr>
</thead> 
<tbody>
    <yfc:loopXML binding="xml:ReceiptLineList:/ReceiptLineList/@ReceiptLine" id="ReceiptLine"> 
	
	<%if(getNumericValue("xml:/ReceiptLine/@Quantity")>0){%>
	<tr> 
	 <yfc:makeXMLInput name="tagKey">
        <yfc:makeXMLKey binding="xml:/Receipt/@ReceiptNo" value="xml:/Receipt/@ReceiptNo"/>
        <yfc:makeXMLKey binding="xml:/Receipt/Shipment/@ShipmentNo" value="xml:/Receipt/@ShipmentNo"/>
		<yfc:makeXMLKey binding="xml:/Receipt/@ReceivingNode" value="xml:/Receipt/@ReceivingNode"/>
        <yfc:makeXMLKey binding="xml:/Receipt/@ReceivingDock" value="xml:/Receipt/@ReceivingDock"/>
        <yfc:makeXMLKey binding="xml:/Receipt/@Status" value="xml:/Receipt/@Status"/>
        <yfc:makeXMLKey binding="xml:/Receipt/@ShortDescription" value="xml:/ReceiptLine/InventoryItem/Item/PrimaryInformation/@ShortDescription"/>
        <yfc:makeXMLKey binding="xml:/Receipt/@OrderNo" value="xml:/ReceiptLine/@OrderNo"/>
        <yfc:makeXMLKey binding="xml:/Receipt/@PrimeLineNo" value="xml:/ReceiptLine/@PrimeLineNo"/>
        <yfc:makeXMLKey binding="xml:/Receipt/@ReleaseNo" value="xml:/ReceiptLine/@ReleaseNo"/>
		<yfc:makeXMLKey binding="xml:/Receipt/@ItemID" value="xml:/ReceiptLine/@ItemID"/>
		<yfc:makeXMLKey binding="xml:/Receipt/@ProductClass" value="xml:/ReceiptLine/@ProductClass"/>
		<yfc:makeXMLKey binding="xml:/Receipt/@UnitOfMeasure" value="xml:/ReceiptLine/@UnitOfMeasure"/>
		<yfc:makeXMLKey binding="xml:/Receipt/@EnterpriseCode" value="xml:/ReceiptLine/@EnterpriseCode"/>

        <yfc:makeXMLKey binding="xml:/Receipt/Tag/@LotAttribute1" value="xml:/ReceiptLine/@LotAttribute1"/>
		<yfc:makeXMLKey binding="xml:/Receipt/Tag/@LotAttribute2" value="xml:/ReceiptLine/@LotAttribute2"/>
		<yfc:makeXMLKey binding="xml:/Receipt/Tag/@LotAttribute3" value="xml:/ReceiptLine/@LotAttribute3"/>
		<yfc:makeXMLKey binding="xml:/Receipt/Tag/@LotExpirationDate" value="xml:/ReceiptLine/@LotExpirationDate"/>
		<yfc:makeXMLKey binding="xml:/Receipt/Tag/@LotKeyReference" value="xml:/ReceiptLine/@LotKeyReference"/>
		<yfc:makeXMLKey binding="xml:/Receipt/Tag/@LotNumber" value="xml:/ReceiptLine/@LotNumber"/>
		<yfc:makeXMLKey binding="xml:/Receipt/Tag/@ManufacturingDate" value="xml:/ReceiptLine/@ManufacturingDate"/>
		<yfc:makeXMLKey binding="xml:/Receipt/Tag/@RevisionNo" value="xml:/ReceiptLine/@RevisionNo"/>
        <yfc:makeXMLKey binding="xml:/Receipt/Tag/@BatchNo" value="xml:/ReceiptLine/@BatchNo"/>
		<yfc:makeXMLKey binding="xml:/Receipt/@ReceiptLineKey" value="xml:/ReceiptLine/@ReceiptLineKey"/>		
	 </yfc:makeXMLInput>
		<yfc:makeXMLInput name="receiptKey">
					<yfc:makeXMLKey binding="xml:/Receipt/@ReceiptHeaderKey" value="xml:/Receipt/@ReceiptHeaderKey" />
		</yfc:makeXMLInput>
	   	<td class="tablecolumn">
            <yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@ItemID"/>
        </td>
		<td class="tablecolumn">
            <yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/OrderLine/Item/@ItemShortDesc"/>
        </td>
        <td class="tablecolumn">
            <yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@ProductClass"/>
        </td>
        <td class="tablecolumn">
            <yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@UnitOfMeasure"/>
        </td>
		<td class="tablecolumn">
            <yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@DispositionCode"/>
        </td>
	    <td class="tablecolumn">
	           <yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@OrderNo"/>
        </td>
	    <td class="tablecolumn">
	           <yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@PrimeLineNo"/>
        </td>
		<td class="tablecolumn">
            <yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@ReleaseNo"/>
        </td>
       	<td class="tablecolumn">
            <yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@ShipByDate"/>
        </td>
		<td class="tablecolumn">
            <yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@CountryOfOrigin"/>
        </td>
		<td class="tablecolumn">
			<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@SerialNo"/>
        </td>
		<td class="tablecolumn">
			<a <%=getDetailHrefOptions("L01", getParameter("tagKey"),"")%> >
				<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@TagNumber"/>
			</a>
        </td>
        <td class="numerictablecolumn">
            <yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@Quantity"/>
        </td>
	 </tr>
	 <%}%>
	   </yfc:loopXML> 

</tbody>
</table>
