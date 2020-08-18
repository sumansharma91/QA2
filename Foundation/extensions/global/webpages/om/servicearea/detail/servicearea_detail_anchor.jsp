<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<% 
	boolean itemExists = true;
	YFCElement inputElem = ((YFCElement) request.getAttribute("yfcSearchCriteria"));
	if(inputElem != null){
		YFCElement promiseServiceLinesElem = inputElem.getChildElement("PromiseServiceLines");
		if(promiseServiceLinesElem != null){
			YFCElement promiseServiceLineElem = promiseServiceLinesElem.getChildElement("PromiseServiceLine");
			if(promiseServiceLineElem != null){
				if(isVoid(promiseServiceLineElem.getAttribute("ItemID"))){
					itemExists = false;
				}
			}
		}
	}
	if(itemExists){
%>

<yfc:callAPI  apiID="AP1" />

<%	boolean hasAnInteraction = false;
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
		}
	}	
%>		

<%		if (hasAnInteraction) {	%>
			<table class="anchor" width="100%" cellSpacing="0" cellpadding="7px">
				<tr>
					<td >
						<jsp:include page="/yfc/innerpanel.jsp" flush='true' >
							<jsp:param name="DisableDateSelection" value="Y"/>
							<jsp:param name="CurrentInnerPanelID" value="I01" />
						</jsp:include>
					</td>
				</tr>
			</table>
<%	
		} else {
%>
			<table class="view" width="100%" height="100%" cellSpacing="0">
				<tr>
					<td class="detailcenterlabel">
					<yfc:i18n>The_Service_Type_Is_Not_Available_For_The_Selected_Postal_Code</yfc:i18n>
					</td>
				</tr>
			</table>
<%
		}
%>		
<%
	} else {
%>
		<table class="view" width="100%" height="100%" cellSpacing="0" >
			<tr>
				<td class="detailcenterlabel">
				<yfc:i18n>Service_Type_Is_Mandatory</yfc:i18n>
				</td>
			</tr>
		</table>
<%
	}
%>
