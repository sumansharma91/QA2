<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<table class="view" width="100%">
    <tr>
        <td class="detaillabel" nowrap="true">
            <yfc:i18n>Ship_Node</yfc:i18n>
        </td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderRelease/@ShipNode"></yfc:getXMLValue></td>
    </tr>
    <tr>
        <td class="detaillabel" nowrap="true">
            <yfc:i18n>Delivery_Method</yfc:i18n>
        </td>
		<% 
		   if(!isVoid(resolveValue("xml:/OrderRelease/@DeliveryMethod"))){	
			   YFCElement commCodeElem = YFCDocument.createDocument("CommonCode").getDocumentElement();
			   commCodeElem.setAttribute("CodeType","DELIVERY_METHOD");
			   commCodeElem.setAttribute("CodeValue",resolveValue("xml:/OrderRelease/@DeliveryMethod"));
			   YFCElement templateElem = YFCDocument.parse("<CommonCode CodeName=\"\" CodeShortDescription=\"\" CodeType=\"\" CodeValue=\"\" CommonCodeKey=\"\" />").getDocumentElement();
		%>
			   <yfc:callAPI apiName="getCommonCodeList" inputElement="<%=commCodeElem%>" 
														templateElement="<%=templateElem%>" outputNamespace=""/>
			   <td class="protectedtext"><yfc:getXMLValueI18NDB binding="xml:/CommonCodeList/CommonCode/@CodeShortDescription" /></td>
		<% } else { %>
			<td class="protectedtext">&nbsp;</td>
		<% } %>
    </tr>
    <tr>
        <td class="detaillabel" nowrap="true">
            <yfc:i18n>Carrier_Service</yfc:i18n>
        </td>
        <td>
            <select <%=yfsGetComboOptions("xml:/OrderRelease/@ScacAndServiceKey", "xml:/OrderRelease/AllowedModifications")%> >
                <yfc:loopOptions binding="xml:/ScacAndServiceList/@ScacAndService" name="ScacAndServiceDesc" value="ScacAndServiceKey" selected="xml:/OrderRelease/@ScacAndServiceKey" isLocalized="Y"/>
            </select>
        </td>
    </tr>
</table>
