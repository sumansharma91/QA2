<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<table class="table" width="100%">
<thead>
    <tr>
        <td class="checkboxheader" sortable="no">
            <input type="checkbox" value="checkbox" name="checkbox" onclick="doCheckAll(this);"/>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Negotiations/Negotiation/@NegotiationNo")%>"><yfc:i18n>Negotiation_#</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Negotiations/Negotiation/@InitiatorOrgCode")%>"><yfc:i18n>Initiator</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Negotiations/Negotiation/@NegotiatorOrgCode")%>"><yfc:i18n>Negotiator</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Negotiations/Negotiation/@Status")%>"><yfc:i18n>Status</yfc:i18n></td>
    </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/Negotiations/@Negotiation" id="Negotiation">
        <tr>
            <yfc:makeXMLInput name="negotiationKey">
                <yfc:makeXMLKey binding="xml:/Negotiation/@NegotiationHeaderKey" value="xml:/Negotiation/@NegotiationHeaderKey" />
            </yfc:makeXMLInput>
            <td class="checkboxcolumn"><input type="checkbox" value='<%=getParameter("negotiationKey")%>' name="chkEntityKey"/></td>
            <td class="tablecolumn">
                <a <%=getDetailHrefOptions("L01",getParameter("negotiationKey"),"")%>>
                <yfc:getXMLValue binding="xml:/Negotiation/@NegotiationNo"/></a>
            </td>
            <td class="tablecolumn">
                <yfc:getXMLValue binding="xml:/Negotiation/@InitiatorOrgCode"/>
            </td>
            <td class="tablecolumn">
                <yfc:getXMLValue binding="xml:/Negotiation/@NegotiatorOrgCode"/>
            </td>
            <td class="tablecolumn">
				<%=displayOrderStatus("N", getValue("Negotiation","xml:/Negotiation/@StatusDescription"),true)%>
            </td>
        </tr>
    </yfc:loopXML> 
</tbody>
</table>
