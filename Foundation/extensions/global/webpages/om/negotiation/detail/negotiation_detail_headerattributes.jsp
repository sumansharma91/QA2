<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/negotiationutils.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/negotiation.js"></script>

<%
    boolean isInitiator = isInitiatorOrganization((YFCElement) request.getAttribute("Negotiations"));
    boolean isLastHdrResponseFromMyOrg = isLastHeaderResponseFromMyOrg((YFCElement) request.getAttribute("Negotiations"), "Negotiation");

    String lastResponseBinding;
    String myLastResponseBinding;
	String myOffer="";
	String hisOffer="";

    if (isInitiator) {
        lastResponseBinding = "LastNegotiator";
        myLastResponseBinding = "LastInitiator";
		myOffer="Initiator";
		hisOffer="Negotiator";
    }
    else {
        lastResponseBinding = "LastInitiator";
        myLastResponseBinding = "LastNegotiator";
		myOffer="Negotiator";
		hisOffer="Initiator";
    }

	String canInitiatorRespond = getValue("Negotiations", "xml:/Negotiations/Negotiation/@CanInitiatorRespond");
	String canNegotiatorRespond = getValue("Negotiations", "xml:Negotiations/Negotiation/@CanNegotiatorRespond");

	boolean headerNegotiated=false;

	YFCElement responseList = makeNegotiationHeaderResponseActionList("HEADER", isInitiator, (YFCElement) request.getAttribute("NegotiationResponseList"));
	request.setAttribute("NegotiationHeaderResponseList", responseList);
%>

<table class="view" width="100%">
    <tr>
		<td class="detaillabel"><yfc:i18n>Last_Action_By</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/Negotiations/Negotiation/@LastResponseOrgCode"/></td>

		<yfc:hasXMLNode binding="xml:/Negotiations/Negotiation/NegotiatedHeaderResponse">
			<% headerNegotiated=true; %>
		</yfc:hasXMLNode>

		<% if (!(headerNegotiated)&&((isInitiator && equals(canInitiatorRespond,"Y")) || (!isInitiator && equals(canNegotiatorRespond,"Y")))) { %>
           <td class="detaillabel"><yfc:i18n>Action_1</yfc:i18n></td>
			<td>
                <select id="HeaderResponseInput" class="combobox" <%=getComboOptions("xml:/old/Responses/Response/HeaderResponses/HeaderResponse/@ResponseAction")%> onchange="performNegotiationHdrActionSelection( this.value, 'HeaderResponseInput', 'HeaderTermsInput')">
                    <yfc:loopOptions binding="xml:NegotiationHeaderResponseList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
                </select>
                <% if (isInitiator) { %>
					<input id="HeaderResponseInput" type="hidden" <%=getTextOptions("xml:/old/Responses/Response/HeaderResponses/HeaderResponse/@ForResponseNo","xml:/Negotiations/Negotiation/@LastNegotiatorResponseNo")%> />
		        <% } else { %>
					<input id="HeaderResponseInput" type="hidden" <%=getTextOptions("xml:/old/Responses/Response/HeaderResponses/HeaderResponse/@ForResponseNo","xml:/Negotiations/Negotiation/@LastInitiatorResponseNo")%> />
				<% } %>
            </td>
        <% } else { %>
	        <td></td>
	        <td></td>
		<% } %>
        <td></td><td></td>
    </tr>
	<% if (!(headerNegotiated)) { %>
    <tr>
        <td colspan="6">
            <table class="table">
                <thead>
                    <tr>
                        <td class="tablecolumnheader"><yfc:i18n>Attribute</yfc:i18n></td>
                        <td class="tablecolumnheader">
                                <yfc:i18n><%=myOffer%></yfc:i18n>
                        </td>
                        <td class="tablecolumnheader">
                                <yfc:i18n><%=hisOffer%></yfc:i18n>
                        </td>
                        <td class="tablecolumnheader" sortable="no"><yfc:i18n>Response</yfc:i18n></td>
                    </tr>
                </thead>
                <tbody>
					<yfc:loopXML binding="xml:/Negotiations/Negotiation/NegotiationRule/NegotiableHdrAttrs/@NegotiableHdrAttr" id="NegotiableHdrAttr">
						<tr>
							<% String attrName = getValue("NegotiableHdrAttr", "xml:/NegotiableHdrAttr/@AttributeName"); %>

		                    <% if (attrName.equals("FreightTerms")) { %>
								<td class="tablecolumn"><yfc:i18n>Freight_Terms</yfc:i18n></td>
							<% } else if (attrName.equals("PaymentTerms")) { %>
								<td class="tablecolumn"><yfc:i18n>Terms_Code</yfc:i18n></td>
							<% } else { %>
								<td class="tablecolumn"><yfc:i18n><%=attrName%></yfc:i18n></td>
							<% } %>

							<td class="tablecolumn">
								<yfc:getXMLValue binding='<%=buildBinding("xml:/Negotiations/Negotiation/", myLastResponseBinding, "HeaderResponse/HeaderTerms/@"+attrName)%>'/>
							</td>
							<td class="tablecolumn">
								<yfc:getXMLValue binding='<%=buildBinding("xml:/Negotiations/Negotiation/", lastResponseBinding, "HeaderResponse/HeaderTerms/@"+attrName)%>'/>
							</td>

							<% if (attrName.equals("FreightTerms")) { %>
								<td class="tablecolumn">
									<select style="display:none" id='<%="HeaderTermsInput" + NegotiableHdrAttrCounter %>' class="combobox" <%=getComboOptions("xml:/old/Responses/Response/HeaderResponses/HeaderResponse/HeaderTerms/@FreightTerms")%> >
										<yfc:loopOptions binding="xml:FreightTermsList:/FreightTermsList/@FreightTerms" name="ShortDescription" value="FreightTerms" isLocalized="Y"/>
									</select>
								</td>
							<% } else if (attrName.equals("PaymentTerms")) { %>
								<td class="tablecolumn">
									<select style="display:none" id='<%="HeaderTermsInput" + NegotiableHdrAttrCounter %>' class="combobox" <%=getComboOptions("xml:/old/Responses/Response/HeaderResponses/HeaderResponse/HeaderTerms/@PaymentTerms")%> >
										<yfc:loopOptions binding="xml:PaymentTermsList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
									</select>
								</td>
							<% } else { %>
								<td class="tablecolumn">
									<input id='<%="HeaderTermsInput" + NegotiableHdrAttrCounter %>' type="text" class="unprotectedinput" style="display:none" <%=getTextOptions("xml:/old/Responses/Response/HeaderResponses/HeaderResponse/HeaderTerms/@"+attrName)%>/>
								</td>
							<% } %>
						</tr>
					</yfc:loopXML>
				</tbody>
            </table>
        </td>
    </tr>
	<% } else { %>
    <tr>
        <td colspan="6">
            <table class="table">
                <thead>
                    <tr>
                        <td class="tablecolumnheader"><yfc:i18n>Attribute</yfc:i18n></td>
                        <td class="tablecolumnheader">
                                <yfc:i18n>Negotiated_Response</yfc:i18n>
                        </td>
                    </tr>
                </thead>
                <tbody>
					<yfc:loopXML binding="xml:/Negotiations/Negotiation/NegotiationRule/NegotiableHdrAttrs/@NegotiableHdrAttr" id="NegotiableHdrAttr">
						<tr>
							<% String attrName = getValue("NegotiableHdrAttr", "xml:/NegotiableHdrAttr/@AttributeName"); %>

							<% if (attrName.equals("FreightTerms")) { %>
								<td class="tablecolumn"><yfc:i18n>Freight_Terms</yfc:i18n></td>
		                    <% } else if (attrName.equals("PaymentTerms")) { %>
								<td class="tablecolumn"><yfc:i18n>Terms_Code</yfc:i18n></td>
							<% } else { %>
								<td class="tablecolumn"><yfc:i18n><%=attrName%></yfc:i18n></td>
							<% } %>

							<td class="tablecolumn">
								<yfc:getXMLValue binding='<%=buildBinding("xml:/Negotiations/Negotiation/NegotiatedHeaderResponse/HeaderTerms/@", attrName,"")%>'/>
							</td>
						</tr>
					</yfc:loopXML>
                </tbody>
            </table>
        </td>
    </tr>
	<% } %>
</table>
