<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/negotiationutils.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/negotiation.js"></script>

<%
    // TO DO: 
    // * The XML used by this screen currently comes from a static data XML file: getNegotiationDetailsView2.xml.
    //   The XML structure in this file has been grouped according to the requirements for this screen. Code should
    //   be written that forms this grouping from the output of the getNegotiationDetails API.

    boolean isInitiator = isInitiatorOrganization((YFCElement) request.getAttribute("Negotiations"));
    boolean isLastHdrResponseFromMyOrg = isLastHeaderResponseFromMyOrg((YFCElement) request.getAttribute("Negotiations"), "Negotiation");

    String lastResponseBinding;
    String myLastResponseBinding;
    if (isInitiator) {
        lastResponseBinding = "LastNegotiator";
        myLastResponseBinding = "LastInitiator";
    }
    else {
        lastResponseBinding = "LastInitiator";
        myLastResponseBinding = "LastNegotiator";
    }
%>

<table class="view" width="100%">
    <tr>
        <td class="detaillabel"><yfc:i18n>Item_ID</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/NegotiationItem/@ItemID"/></td>
        <td class="detaillabel"><yfc:i18n>Product_Class</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/NegotiationItem/@ProductClass"/></td>
        <td class="detaillabel"><yfc:i18n>UOM</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/NegotiationItem/@UnitOfMeasure"/></td>
    </tr>
    <tr>
        <td class="detaillabel"><yfc:i18n>Price</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/NegotiationItem/@Price"/></td>
        <td class="detaillabel"><yfc:i18n>Freight_Terms</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/NegotiationItem/@FreightTerms"/></td>
        <% if (!isLastHdrResponseFromMyOrg) { %>
            <td class="detaillabel"><yfc:i18n>Action</yfc:i18n></td>
            <td>
                <select class="combobox" <%=getComboOptions("xml:/Responses/Response/HeaderResponses/HeaderResponse/@ResponseAction")				%>
                        onchange="performNegotiationActionSelection(this.value, 'counterofferrow_1')">
                    <yfc:loopOptions binding="xml:NegotiationResponseList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
                </select>
            </td>
        <% } %>
    </tr>
    <tr>
        <td colspan="6">
            <table class="table">
                <thead>
                    <tr>
                        <td class="tablecolumnheader">&nbsp;</td>
                        <yfc:loopXML binding="xml:/NegotiationItem/@NegotiationLine" id="NegotiationLine">
                            <td class="tablecolumnheader"><yfc:getXMLValue binding="xml:/NegotiationLine/@ShipDate"/></td>
                        </yfc:loopXML>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td class="tablecolumn">
                            <% if (isLastHdrResponseFromMyOrg) { %>
                                <yfc:i18n>Current_Offer</yfc:i18n>
                            <%} else {%>
                                <yfc:i18n>Previous_Offer</yfc:i18n>
                            <%}%>
                        </td>
                        <yfc:loopXML binding="xml:/NegotiationItem/@NegotiationLine" id="NegotiationLine">
                            <td class="numerictablecolumn"><yfc:getXMLValue binding='<%=buildBinding("xml:/NegotiationLine/", myLastResponseBinding, "LineResponse/@Quantity")%>'/></td>
                        </yfc:loopXML>
                    </tr>
                    <tr>
                        <td class="tablecolumn">
                            <% if (isLastHdrResponseFromMyOrg) { %>
                                <yfc:i18n>Previous_Response</yfc:i18n>
                            <%} else {%>
                                <yfc:i18n>Current_Response</yfc:i18n>
                            <%}%>
                        </td>
                        <yfc:loopXML binding="xml:/NegotiationItem/@NegotiationLine" id="NegotiationLine">
                            <td class="numerictablecolumn"><yfc:getXMLValue binding='<%=buildBinding("xml:/NegotiationLine/", lastResponseBinding, "LineResponse/@Quantity")%>'/></td>
                        </yfc:loopXML>
                    </tr>
                    <tr id="counterofferrow_1" style="display:none">
                        <td class="tablecolumn">
                            <yfc:i18n>Counter_Offer</yfc:i18n>
                        </td>
                        <yfc:loopXML binding="xml:/NegotiationItem/@NegotiationLine" id="NegotiationLine">
                            <td class="numerictablecolumn">
                                <% String responseBinding = "xml:/Responses/Response/LineResponses/LineResponse_" + NegotiationLineCounter + "/LineTerms/LineTermsDetail_1/"; %>
                                <input type="text" class="numericunprotectedinput" <%=getTextOptions(responseBinding + "@Quantity")%>/>
                            </td>
                        </yfc:loopXML>
                    </tr>
                </tbody>
            </table>
        </td>
    </tr>
</table>
