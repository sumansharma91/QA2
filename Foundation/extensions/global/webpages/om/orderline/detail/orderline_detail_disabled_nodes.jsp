<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<table class="table" width="100%" editable="true" ID="OrderLineSourcingCntrlList">
<thead>
    <tr>
        <td class="tablecolumnheader"><yfc:i18n>Node</yfc:i18n></td>
        <td class="tablecolumnheader"><yfc:i18n>Suppress_Procurement</yfc:i18n></td>
        <td class="tablecolumnheader"><yfc:i18n>Suppress_Sourcing</yfc:i18n></td>
        <td class="tablecolumnheader"><yfc:i18n>Reason_Text</yfc:i18n></td>
    </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/OrderLine/OrderLineSourcingControls/@OrderLineSourcingCntrl" id="OrderLineSourcingCntrl">
        <tr>
            <td class="tablecolumn" >
				<yfc:getXMLValue binding="xml:/OrderLineSourcingCntrl/@Node"/>
			</td>
            <td class="tablecolumn" >
				<%=displayFlagAttribute(getValue("OrderLineSourcingCntrl","xml:/OrderLineSourcingCntrl/@SuppressProcurement"))%>
			</td>
            <td class="tablecolumn" >
				<%=displayFlagAttribute(getValue("OrderLineSourcingCntrl","xml:/OrderLineSourcingCntrl/@SuppressSourcing"))%>
			</td>
            <td class="tablecolumn" >
				<yfc:getXMLValue binding="xml:/OrderLineSourcingCntrl/@ReasonText"/>
			</td>
        </tr>
    </yfc:loopXML> 
</tbody>
</table>
