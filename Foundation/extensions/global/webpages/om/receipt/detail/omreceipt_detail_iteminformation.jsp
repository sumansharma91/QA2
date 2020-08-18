<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<table class="view" width="100%">
<tr>
    <td class="detaillabel" ><yfc:i18n>ItemID</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@ItemID"/></td>
	<td class="detaillabel" ><yfc:i18n>Item_Description</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@ItemDesc"/></td>
	<td class="detaillabel" ><yfc:i18n></yfc:i18n></td>
    <td class="protectedtext"></td>
</tr>
<tr>
	<td class="detaillabel" ><yfc:i18n>Product_Class</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@ProductClass"/></td>
    <td class="detaillabel" ><yfc:i18n>Unit_Of_Measure</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@UnitOfMeasure"/></td>
	<td class="detaillabel" ><yfc:i18n>COO</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@CountryOfOrigin"/> 
	</td>
</tr>    
</table> 
 
