<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf"%>

<yfc:callAPI  apiID="AP1" />

<%	boolean hasAnInteraction = false;
		boolean hasUnavailableLines = false;
		YFCElement promiseElement = (YFCElement) request.getAttribute("Promise");
		if (promiseElement != null) { 
			YFCElement suggestedOptionElement = promiseElement.getChildElement("SuggestedOption");
			if (suggestedOptionElement != null)	{	
				YFCElement optionElement = suggestedOptionElement.getChildElement("Option");
				if (optionElement != null)	{	
					YFCElement interactionsElement = optionElement.getChildElement("Interactions");
					if (interactionsElement != null)	{	
						YFCElement interactionElement = interactionsElement.getChildElement("Interaction");
						if (interactionElement != null)	{ 
							if (!isVoid(interactionElement.getAttribute("EarliestDate")) && !isVoid(interactionElement.getAttribute("LastDate"))) {	
								hasAnInteraction = true;	 
							}
						} 
					}
				}
				YFCElement unavailableLinesElement = suggestedOptionElement.getChildElement("UnavailableLines");
				if (unavailableLinesElement != null)	{	
					YFCElement unavailableLineElement = unavailableLinesElement.getChildElement("UnavailableLine");
					if (unavailableLineElement != null)	{
						hasUnavailableLines = true;	
					}
				}
			}
		}	
%>		

<%
    /* Construct VisibleBinding for the Remove Appointment action. */
    YFCElement lineElement = (YFCElement) request.getAttribute("OrderLine");
    String showRemoveAppt = isVoid(lineElement.getAttribute("PromisedApptStartDate")) ? "N" : "Y";
    lineElement.setAttribute("enableRemoveAppointment", showRemoveAppt);
%>

<table class="anchor" cellpadding="7px" cellSpacing="0">
	<tr>
		<td colspan="3">
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value="I01"/>
			</jsp:include>
		</td>
	</tr>
<%	if (hasAnInteraction) {	%>
			<tr>
				<td height="100%" width="67%" colspan="2" >
					<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
						<jsp:param name="CurrentInnerPanelID" value="I02"/>
					</jsp:include>
				</td>
				<td height="100%" width="33%" colspan="1">
					<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
						<jsp:param name="CurrentInnerPanelID" value="I03"/>
						<jsp:param name="Path" value="xml:/Promise/SuggestedOption/Option/Interactions/Interaction/PersonInfoShipTo/"/>
						<jsp:param name="DataXML" value="Promise"/>
					</jsp:include>
				</td>
			</tr>
			<tr>
				<td colspan="3">
					<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
						<jsp:param name="CurrentInnerPanelID" value="I04"/>
					</jsp:include>
				</td>
			</tr>
<%	}	
		if (hasUnavailableLines) {	%>
			<tr>
				<td colspan="3" >
					<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
						<jsp:param name="CurrentInnerPanelID" value="I02"/>
					</jsp:include>
				</td>
			</tr>
			<tr>
				<td colspan="3">
					<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
						<jsp:param name="CurrentInnerPanelID" value="I06"/>
						<jsp:param name="showProductClassColumn" value="false"/>
					</jsp:include>
				</td>
			</tr>
<%	}	%>						
</table>
