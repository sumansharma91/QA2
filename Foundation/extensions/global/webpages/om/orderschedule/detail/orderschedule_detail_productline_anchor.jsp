<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfc.util.*" %>
<%@ page import="java.net.URLDecoder"%>

<%	YFCElement root = getRequestDOM(); 
		//System.out.println("root while entering line availability = "+root.getString());
		String OrdHeadKey = getParameter("xml:/Order/@OrderHeaderKey");
		//System.out.println("root while entering line availability OrdHeadKey= "+OrdHeadKey);
		YFCDocument getPossibleSchedulesInputDoc =YFCDocument.createDocument("Promise");;
		YFCElement getPossibleSchedulesInputElem =getPossibleSchedulesInputDoc.getDocumentElement(); 

		getPossibleSchedulesInputElem.setAttribute("Mode","Inquire");
		getPossibleSchedulesInputElem.setAttribute("CheckInventory","Y");
		getPossibleSchedulesInputElem.setAttribute("OrderHeaderKey",OrdHeadKey);

		YFCElement promiseFromRootElem =  root.getChildElement("Promise");
		if (promiseFromRootElem !=null) {
			getPossibleSchedulesInputElem.setAttribute("OptimizationType",promiseFromRootElem.getAttribute("OptimizationType"));
			getPossibleSchedulesInputElem.setAttribute("DelayWindow",promiseFromRootElem.getAttribute("DelayWindow"));
//////////////////////////////////////////////////////// building Overrides Node if necessary and sending with input //////////////////////////
			YFCElement overrideFromRootElem =  promiseFromRootElem.getChildElement("Overrides");
			if (overrideFromRootElem !=null) {
				String distributionOverride = overrideFromRootElem.getAttribute("DistributionRuleId");	
				String scacAndServiceOverride = overrideFromRootElem.getAttribute("ScacAndServiceKey");
				if (!isVoid(distributionOverride) || !isVoid(scacAndServiceOverride)) {
					YFCElement getPossibleSchedulesOverridesElem = getPossibleSchedulesInputDoc.createElement("Overrides");
					getPossibleSchedulesInputElem.appendChild(getPossibleSchedulesOverridesElem);
					if (!isVoid(distributionOverride)) {
						getPossibleSchedulesOverridesElem.setAttribute("DistributionRuleId",distributionOverride);
					}
					if (!isVoid(scacAndServiceOverride)) {
						getPossibleSchedulesOverridesElem.setAttribute("ScacAndServiceKey",scacAndServiceOverride);
					}
				}
			}	
//////////////////////////////////////////////////////////	end overrides node ////////////////////////////////////////////////////////////////////	
		}
		// else api will do defaulting .
		//	cr 35576

		YFCElement getPossibleSchedulesOrderLinesElem = getPossibleSchedulesInputDoc.createElement("MultipleOrderLines");
		getPossibleSchedulesInputElem.appendChild(getPossibleSchedulesOrderLinesElem);

		String [] keys = request.getParameterValues("chkEntityKey");
        if ( keys != null ) {
            for ( int i = 0; i < keys.length; i++ ) { 
					YFCDocument entityKeyDoc = YFCDocument.parse(URLDecoder.decode(keys[i]));	
					YFCElement entityKeyElem = entityKeyDoc.getDocumentElement();					
					String OrdLinKey = entityKeyElem.getAttribute("OrderLineKey");	%>
					<input type="hidden" name="chkEntityKey" value="<%=HTMLEncode.htmlEscape(keys[i])%>" />
	<%			YFCElement getPossibleSchedulesOrderLineElem = getPossibleSchedulesInputDoc.createElement("OrderLine");
					getPossibleSchedulesOrderLinesElem.appendChild(getPossibleSchedulesOrderLineElem);
					getPossibleSchedulesOrderLineElem.setAttribute("OrderLineKey",OrdLinKey);
			}
        }
//		System.out.println("getPossibleSchedulesInputDoc = "+getPossibleSchedulesInputDoc.getString());

		String getPossSchedTemplateString = "<Promise Mode=\"\" CheckInventory=\"\" DelayWindow=\"\" MaximumRecords=\"\" OptimizationType=\"\" OrderHeaderKey=\"\" OrderLineKey=\"\" >" +
									"<Overrides ScacAndServiceKey=\"\" CarrierServiceCode=\"\" DistributionRuleId=\"\" />" + 
									"<SuggestedOption>" +
										"<Option>" + 
											"<PromiseLines>" +
												"<PromiseLine OrderScheduleKey=\"\" OrderLineKey=\"\" LineId=\"\" ItemID=\"\" ProductClass=\"\" ItemDesc=\"\" TagNumber=\"\" UnitOfMeasure=\"\" RequiredQty=\"\" DistributionRuleId=\"\" CarrierServiceCode=\"\" ReqStartDate=\"\" ReqEndDate=\"\">" +
													"<Assignments>" +
														"<Assignment InteractionNo=\"\" ProductAvailDate=\"\" DeliveryDate=\"\" ExternalNode=\"\" NodePriority=\"\" Quantity=\"\" ShipDate=\"\" ShipNode=\"\" CannotScheduleBeforeDate=\"\" CannotScheduleAfterDate=\"\"  AwaitingReservationAcceptance=\"\" />" + 
													"</Assignments>" +
												"</PromiseLine>" +
											"</PromiseLines>" +
											"<Interactions>" +
												"<Interaction Node=\"\" OrganizationCode=\"\" ExternalNode=\"\" ShipDate=\"\" DeliveryDate=\"\" NodePriority=\"\" Schedulable=\"\" InteractionNo=\"\" EarliestDate=\"\" LastDate=\"\" ItemGroupCode=\"\" DeliveryMethod=\"\" InteractionType=\"\" >" +
													"<PersonInfoShipTo/>" +
												"</Interaction>" +
											"</Interactions>" +
											"<NonWorkingDays>" +
												"<NonWorkingDay Date=\"\" />" +
											"</NonWorkingDays>" +
										"</Option>" +
										"<UnavailableLines>" + 
											"<UnavailableLine ItemGroupCode=\"\" ItemID=\"\" OrderLineKey=\"\" PrimeLineNo=\"\" ProductClass=\"\" ToScheduleQty=\"\" UnitOfMeasure=\"\">" + 
												"<Reasons>" + 
													"<Reason Text=\"\" />" + 
												"</Reasons>" + 
											"</UnavailableLine>" + 
										"</UnavailableLines>" + 
									"</SuggestedOption>" +
								"</Promise>" ;
		 YFCElement getPossibleSchedulesTemplateElem = YFCDocument.parse(getPossSchedTemplateString).getDocumentElement();
//		System.out.println("getPossibleSchedulesTemplateElem = "+getPossibleSchedulesTemplateElem.getString());	
%>

<yfc:callAPI apiName="getPossibleSchedules" inputElement="<%=getPossibleSchedulesInputElem%>" 	templateElement="<%=getPossibleSchedulesTemplateElem%>" inputNamespace="Promise" outputNamespace="Promise"/> 

<% 
boolean HasShipmentMethodInteraction=false;
boolean HasDeliveryMethodInteraction=false;
boolean HasUnavailableLines = false;
String shipInnerPanelHeader =null;
String deliveryInnerPanelHeader = null;
%>

<yfc:loopXML binding="xml:/Promise/SuggestedOption/Option/Interactions/@Interaction" id="Interaction">
<%		
	if (equals(getValue("Interaction","xml:/Interaction/@ItemGroupCode"),"PROD") && equals(getValue("Interaction","xml:/Interaction/@DeliveryMethod"),"SHP") ) {
		HasShipmentMethodInteraction=true;
		shipInnerPanelHeader =  getI18N("OrderLines_Being_Shipped");
	}
	if (equals(getValue("Interaction","xml:/Interaction/@ItemGroupCode"),"PROD") && equals(getValue("Interaction","xml:/Interaction/@DeliveryMethod"),"DEL") ) {
		HasDeliveryMethodInteraction=true;
		deliveryInnerPanelHeader =  getI18N("OrderLines_Being_Delivered");
	}
%>
</yfc:loopXML> 

<%	YFCElement promiseElement = (YFCElement) request.getAttribute("Promise");
		if (promiseElement != null) {
			YFCElement suggestedOptionElement = promiseElement.getChildElement("SuggestedOption");
			if (suggestedOptionElement != null)	{	
				YFCElement unavailableLinesElement = suggestedOptionElement.getChildElement("UnavailableLines");
				if (unavailableLinesElement != null)	{	
					YFCElement unavailableLineElement = unavailableLinesElement.getChildElement("UnavailableLine");
					if (unavailableLineElement != null)	{
						HasUnavailableLines=true;
						shipInnerPanelHeader =  getI18N("OrderLines_Being_Shipped");	//	cr 37002
					}
				}
			}
		}
%>

<table class="anchor" cellpadding="7px" cellSpacing="0">
<tr>
    <td >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
        </jsp:include>
    </td>
</tr>
<%	if (HasShipmentMethodInteraction || HasUnavailableLines)	{	%>
			<tr>
				<td >
					<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
						<jsp:param name="CurrentInnerPanelID" value="I03"/>
			            <jsp:param name="DeliveryMethod" value="SHP"/>
                        <jsp:param name="Title" value='<%=shipInnerPanelHeader%>'/>
					</jsp:include>
				</td>
			</tr>
<%	}	%>		
<%	if (HasDeliveryMethodInteraction)	{	%>
			<tr>
				<td >
					<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
						<jsp:param name="CurrentInnerPanelID" value="I03"/>
			            <jsp:param name="DeliveryMethod" value="DEL"/>
                        <jsp:param name="Title" value='<%=deliveryInnerPanelHeader%>'/>
					</jsp:include>
				</td>
			</tr>
<%	}	%>		
<%	if (HasUnavailableLines)	{	%>
			<tr>
				<td >
					<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
						<jsp:param name="CurrentInnerPanelID" value="I05"/>
						<jsp:param name="showProductClassColumn" value="true"/>
					</jsp:include>
				</td>
			</tr>
<%	}	%>		
<tr>
	<td >
		<input type="hidden" name="EKNOfIPClicked"/>
		<input type="hidden" name="xml:/Order/@OrderHeaderKey" value="<%=HTMLEncode.htmlEscape(OrdHeadKey)%>" />
	</td>
</tr>
</table>
