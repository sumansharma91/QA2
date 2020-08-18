<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<table width="100%" class="view">
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Item_ID</yfc:i18n>
		<input type="hidden" <%=getTextOptions("xml:/OrderLine/@OrderLineKey")%> />
		<input type="hidden" <%=getTextOptions("xml:/OrderLine/@OrderHeaderKey")%> />
		<input type="hidden" <%=getTextOptions("xml:/OrderLine/@OrderedQty")%> />
    </td>
    <td class="protectedtext">
        <yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemID"/>
    </td>
    <td class="detaillabel" >
        <yfc:i18n>Product_Class</yfc:i18n>
    </td>
    <td class="protectedtext">
		<yfc:getXMLValue binding="xml:/OrderLine/Item/@ProductClass"/>
    </td>
    <td class="detaillabel" >
        <yfc:i18n>Unit_Of_Measure</yfc:i18n>
    </td>
    <td class="protectedtext">
        <yfc:getXMLValue binding="xml:/OrderLine/Item/@UnitOfMeasure"/>
    </td>
</tr>
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Description</yfc:i18n> 
    </td>
    <td class="protectedtext">
        <yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemDesc"/>
    </td>
	<td class="detaillabel" >
        <yfc:i18n>Quantity</yfc:i18n> 
    </td>
    <td class="protectedtext">
        <yfc:getXMLValue binding="xml:/OrderLine/@OrderedQty"/>
    </td>
		<td class="detaillabel" >
        <yfc:i18n>Unit_Price</yfc:i18n> 
    </td>
    <td class="protectedtext">
        <yfc:getXMLValue binding="xml:/OrderLine/LinePriceInfo/@UnitPrice"/>
    </td>
</tr>
</table>
