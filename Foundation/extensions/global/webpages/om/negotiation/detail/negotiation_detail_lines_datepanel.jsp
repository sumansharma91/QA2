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

	YFCElement itemsElem = getNegotiationElementByItem((YFCElement) request.getAttribute("Negotiations"));

	pageContext.setAttribute("Items", itemsElem);

	long totalColumns = itemsElem.getLongAttribute("TotalColumns");

	long loopCounter = 0;

    String myLastResponseBinding;

	String canInitiatorRespond = getValue("Negotiations", "xml:/Negotiations/Negotiation/@CanInitiatorRespond");
	String canNegotiatorRespond = getValue("Negotiations", "xml:Negotiations/Negotiation/@CanNegotiatorRespond");

	String initiatorOrg = getValue("Negotiations", "xml:Negotiations/Negotiation/@InitiatorOrgCode");
	String negotiatorOrg = getValue("Negotiations", "xml:Negotiations/Negotiation/@NegotiatorOrgCode");

	String firstLineResponse=null;
	String secondLineResponse=null;
	String forResponse=null;
	String firstLineOrgCode=null;
	String secondLineOrgCode=null;

	if (isInitiator) {
		firstLineResponse = "LastInitiator";
		secondLineResponse = "LastNegotiator";

		firstLineOrgCode = initiatorOrg;
		secondLineOrgCode = negotiatorOrg;

		forResponse = "LastNegotiator";
	}
	else {
		firstLineResponse = "LastNegotiator";
		secondLineResponse = "LastInitiator";

		firstLineOrgCode = negotiatorOrg;
		secondLineOrgCode = initiatorOrg;

		forResponse = "LastInitiator";
	}

	boolean firstLineExists=false;
	boolean secondLineExists=false;
	
	String itemId="";
	String productClass="";
	String unitOfMeasure="";

	String primeLineNo="";
	String subLineNo="";

	YFCElement responseList = makeNegotiationHeaderResponseActionList("LINE", isInitiator, (YFCElement) request.getAttribute("NegotiationResponseList"));
	request.setAttribute("NegotiationLineResponseList", responseList);
%>

<yfc:loopXML binding="xml:/Items/@Item" id="Item">

	<table class="view" width="100%">
		<tr>
			<% /////// Response Action Combo Box ///////%>
            <% if ((isInitiator && canInitiatorRespond.equals("Y")) || (!isInitiator && canNegotiatorRespond.equals("Y"))) { %>

			   <td class="detaillabel"><yfc:i18n>Action_1</yfc:i18n></td>
				<td>
					<select class="combobox" onchange="performNegotiationLineActionSelection(this.value, 'counterofferline', 'LineResponseInput', 'LineTermsDetailInput', 'LineResponseAction', '<%=ItemCounter %>');" <%=getComboOptions("xml:/old/Responses/Response/LineResponses/LineResponse_" + ItemCounter + "/@ResponseAction")%>>
						<yfc:loopOptions binding="xml:NegotiationLineResponseList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
					</select>
				</td>

			<% } %>

			<td class="detaillabel" ><yfc:i18n>Item_ID</yfc:i18n></td>
			<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/@ItemID"/></td>
			<td class="detaillabel" ><yfc:i18n>Product_Class</yfc:i18n></td>
			<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/@ProductClass"/></td>
			<td class="detaillabel" ><yfc:i18n>Unit_Of_Measure</yfc:i18n></td>
			<td class="protectedtext"><yfc:getXMLValue binding="xml:/Item/@UnitOfMeasure"/></td>

			<%
				itemId = getValue("Item", "xml:/Item/@ItemID");
				productClass = getValue("Item", "xml:/Item/@ProductClass");
				unitOfMeasure = getValue("Item", "xml:/Item/@UnitOfMeasure");
			%>
		</tr>
	</table>

	<table class="table" cellspacing="0" width="100%">

		<% /////// Table Column Header ///////%>

		<% firstLineExists=false; %>
		<% secondLineExists=false; %>

		<thead>
			<td class="tablecolumnheader" nowrap="true" sortable="no"><yfc:i18n>Organization_Code</yfc:i18n></td>
			<yfc:loopXML binding="xml:/Item/NegotiationLines/@NegotiationLine" id="NegotiationLine">
				<td class="tablecolumnheader" nowrap="true" sortable="no">
					<yfc:getXMLValue binding="xml:/NegotiationLine/@ShipDate"/>
				</td>

				<yfc:hasXMLNode binding='<%=buildBinding("xml:/NegotiationLine/", firstLineResponse, "LineResponse/LineTerms/LineTermsDetail")%>'>
					<% firstLineExists=true; %>
				</yfc:hasXMLNode>

				<yfc:hasXMLNode binding='<%=buildBinding("xml:/NegotiationLine/", secondLineResponse, "LineResponse/LineTerms/LineTermsDetail")%>'>
					<% secondLineExists=true; %>
				</yfc:hasXMLNode>

			</yfc:loopXML>
		</thead>

		<tbody>

			<% /////// First Line ///////%>
			<% if (firstLineExists) { %>
				<tr>
					<td class="tablecolumn"><%=firstLineOrgCode%></td>
					<yfc:loopXML binding="xml:/Item/NegotiationLines/@NegotiationLine" id="NegotiationLine">
						<td class="numerictablecolumn">
						   <yfc:getXMLValue binding='<%=buildBinding("xml:/NegotiationLine/", firstLineResponse, "LineResponse/LineTerms/LineTermsDetail/@Quantity")%>'/>
						</td>
					</yfc:loopXML>
				</tr>
			<% } %>

			<% /////// Second Line ///////%>
			<% if (secondLineExists) { %>
				<tr>
					<td class="tablecolumn"><%=secondLineOrgCode%></td>
					<yfc:loopXML binding="xml:/Item/NegotiationLines/@NegotiationLine" id="NegotiationLine">
						<td class="numerictablecolumn">
						   <yfc:getXMLValue binding='<%=buildBinding("xml:/NegotiationLine/", secondLineResponse, "LineResponse/LineTerms/LineTermsDetail/@Quantity")%>'/>
						</td>
					</yfc:loopXML>
				</tr>
			<% } %>
 
			<% /////// Response Row ///////%>
			<% if ((isInitiator && canInitiatorRespond.equals("Y")) || (!isInitiator && canNegotiatorRespond.equals("Y"))) { %>
			<tr id='<%="counterofferline" + ItemCounter%>' style="display:none">
				<td class="tablecolumn"></td>

				<yfc:loopXML binding="xml:/Item/NegotiationLines/@NegotiationLine" id="NegotiationLine">
					<%
						primeLineNo = getValue("NegotiationLine", "xml:/NegotiationLine/@PrimeLineNo");
						subLineNo = getValue("NegotiationLine", "xml:/NegotiationLine/@SubLineNo");
					%>

					<td class="numerictablecolumn" nowrap="true" >
						<% String responseBinding = "xml:/old/Responses/Response/LineResponses/LineResponse_" + primeLineNo + subLineNo + "/"; %>
						<% String lineTermDtlsBinding = "xml:/old/Responses/Response/LineResponses/LineResponse_" +  primeLineNo + subLineNo + "/LineTerms/LineTermsDetail_1/"; %>
						<input id='<%="LineResponseInput" + ItemCounter%>' type="hidden" class="protectedinput" <%=getTextOptions( responseBinding + "@LineEntity", "xml:/NegotiationLine/@LineEntity")%>/>
						<input id='<%="LineResponseInput" + ItemCounter%>' type="hidden" class="protectedinput" <%=getTextOptions( responseBinding + "@LineEntityKey", "xml:/NegotiationLine/@LineEntityKey")%>/>
						<input id='<%="LineResponseAction" + ItemCounter%>' type="hidden" class="protectedinput" <%=getTextOptions( responseBinding + "@ResponseAction")%>/>
						<input id='<%="LineResponseInput" + ItemCounter%>' type="hidden" class="protectedinput" <%=getTextOptions( responseBinding + "@ForResponseNo", "xml:/NegotiationLine/" + forResponse + "LineResponse/@ResponseNo")%>/>
						<input id='<%="LineResponseInput" + ItemCounter%>' type="hidden" class="protectedinput" <%=getTextOptions( responseBinding + "@ForOptionNo", "", "1")%>/>
						<input id='<%="LineResponseInput" + ItemCounter%>' type="hidden" class="protectedinput" <%=getTextOptions( responseBinding + "@ApplicationAction", "", "CANCEL")%>/>
						<input id='<%="LineTermsDetailInput" + ItemCounter+"_OptionNo"%>' type="hidden" class="protectedinput" <%=getTextOptions( lineTermDtlsBinding + "@OptionNo", "", "1")%>/>
						<input id='<%="LineTermsDetailInput" + ItemCounter+"_ItemID"%>' type="hidden" class="protectedinput" <%=getTextOptions( lineTermDtlsBinding + "@ItemID", "", itemId)%>/>
						<input id='<%="LineTermsDetailInput" + ItemCounter+"_ProductClass"%>' type="hidden" class="protectedinput" <%=getTextOptions( lineTermDtlsBinding + "@ProductClass", "", productClass)%>/>
						<input id='<%="LineTermsDetailInput" + ItemCounter+"_UnitOfMeasure"%>' type="hidden" class="protectedinput" <%=getTextOptions( lineTermDtlsBinding + "@UnitOfMeasure", "", unitOfMeasure)%>/>
						<input id='<%="LineTermsDetailInput" + ItemCounter+"_Quantity"%>' type="text" class="numericunprotectedinput" <%=getTextOptions( lineTermDtlsBinding + "@Quantity")%>/>
					</td>
				</yfc:loopXML>
			</tr>
			<% } %>
			<tbody><tr><td colspan=<%=totalColumns+1%>><hr/></td></tr></tbody>
		</tbody>
	</table>
</yfc:loopXML>
