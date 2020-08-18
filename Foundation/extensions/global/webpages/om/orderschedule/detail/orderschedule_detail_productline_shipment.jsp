<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfc.util.*" %>

<table cellpadding="7px" cellSpacing="0" border="0" width="100%" class="anchor">
<%
		String DelMethodSentFromAnchor = getParameter("DeliveryMethod");
		String panelID=null;
		if (equals(DelMethodSentFromAnchor,"SHP"))	//the options should appear ONLY for Orderlines being shipped
		{	panelID="I02";
%>
			<tr>
				<td height="100%">
				<fieldset>
				<legend><b><yfc:i18n>Availability_Options</yfc:i18n></b></legend> 
					<table height="100%" width="100%" >
						<tr>
							<td>
								<jsp:include page="/om/orderschedule/detail/orderschedule_detail_availabilityoptions.jsp" flush="true" />
							</td>
						</tr>
					</table>
				</fieldset>
				</td>
			</tr>
<%	}	else {
			panelID="I04";
		}%>
<tr>
	<td height="100%" width="100%" >
       <table class="anchor" width="100%" cellpadding="3px">
        <%	boolean innerPanelExists = false;				
			ArrayList AvailabilityList = getLoopingElementList("xml:/Promise/SuggestedOption/Option/Interactions/@Interaction"); //looping over interactions
            for (int AvailabilityCounter = 0; AvailabilityCounter < AvailabilityList.size(); AvailabilityCounter++) 
			{	YFCElement singleInteractionElem = (YFCElement) AvailabilityList.get(AvailabilityCounter);
				String deliveryMethodInInteractionElement= singleInteractionElem.getAttribute("DeliveryMethod");
				if (equals(DelMethodSentFromAnchor,deliveryMethodInInteractionElement)) 
				{												
					YFCElement PromiseLinesElem = singleInteractionElem.createChild("PromiseLines");
					String InteractionNo= singleInteractionElem.getAttribute("InteractionNo");
					%>
					<yfc:loopXML binding="xml:/Promise/SuggestedOption/Option/PromiseLines/@PromiseLine" id="PromiseLine">
				<%	boolean PromiseLineHasBeenAdded=false;	%>
						<yfc:loopXML binding="xml:/PromiseLine/Assignments/@Assignment" id="Assignment">
				<%		if (equals(getValue("Assignment","xml:/Assignment/@InteractionNo"),InteractionNo) && PromiseLineHasBeenAdded==false) 
							{
								YFCElement PromiseLineElem = (YFCElement)pageContext.getAttribute("PromiseLine");
								YFCElement PromiseLineElemClone = PromiseLinesElem.createChild("PromiseLine");	//creating a clone as complete PromiseLine with all its children cannot be appended to Interaction. Only relevent Assignments need to be
								PromiseLineElemClone.setAttributes(PromiseLineElem.getAttributes());
							
								YFCElement AssignmentElem = (YFCElement)pageContext.getAttribute("Assignment");
								AssignmentElem.setAttribute("ShowAwaitingReservationFlag","N");
							%>
									<yfc:loopXML binding="xml:/Assignment/Procurements/@Procurement" id="Procurement">
										<% if (YFCCommon.equals(resolveValue("xml:/Procurement/@AwaitingReservationAcceptance"),"Y")) {
											AssignmentElem.setAttribute("ShowAwaitingReservationFlag","Y");
										} %>
									</yfc:loopXML> 								

							<%		
								PromiseLineElemClone.appendChild(AssignmentElem);
								PromiseLinesElem.appendChild(PromiseLineElemClone);
								PromiseLineHasBeenAdded=true;	//since only 1 Assignment within a PromiseLine can match the interaction number, once the PromiseLine has been added, then we need to move on to next PromiseLine
	//							System.out.println(singleInteractionElem.getString());
							}													
				%>
						</yfc:loopXML> 
					</yfc:loopXML> 
			<%	request.removeAttribute(YFCUIBackendConsts.YFC_CURRENT_INNER_PANEL_ID);
					request.setAttribute("Interaction", singleInteractionElem);
					String panelHeader= new String();
					String sDeliveryDate = getValue("Interaction", "xml:/Interaction/@DeliveryDate");

					panelHeader = getI18N("Expected_Delivery_Date") +" - " +sDeliveryDate ;
					panelHeader = getI18N("Shipment_#") +" " +(AvailabilityCounter + 1) +": " +panelHeader;
					innerPanelExists = true;
					%>
					<tr>
						<td>
						<jsp:include page="/yfc/innerpanel.jsp" flush="true">
							<jsp:param name="CurrentInnerPanelID" value="<%=panelID%>"/>
							<jsp:param name="Title" value='<%=panelHeader%>'/>
							<jsp:param name="DeliveryMethod" value="<%=DelMethodSentFromAnchor%>"/>
						</jsp:include>
						</td>
					</tr>
		<%	}
			}	%>
		</table>
	</td>
</tr>

<%	if (innerPanelExists) {	 %>
			<tr>
				<td height="100%">
				<fieldset>
				<legend><yfc:i18n>Legend</yfc:i18n></legend> 
					<table height="100%" width="100%" >
						<tr>
							<td align="right"><img class="nonclickablecolumnicon" <%=getImageOptions(request.getContextPath() + "/console/icons/reservations.gif", "Product_Availability_Date")%> />
							&nbsp;</td>
							<td class="protectedtext"><yfc:i18n>Product_Availability_Date</yfc:i18n></td>
					<%	if (equals(DelMethodSentFromAnchor,"SHP"))
							{	%>
								<td align="right"><img class="nonclickablecolumnicon" <%=getImageOptions(request.getContextPath() + "/console/icons/shippingcartons.gif", "Expected_Ship_Date")%> />
								&nbsp;</td>
								<td class="protectedtext"><yfc:i18n>Expected_Ship_Date</yfc:i18n></td>
					<%	}	%>
							<td align="right"><img class="nonclickablecolumnicon" <%=getImageOptions(request.getContextPath() + "/console/icons/shipments.gif", "Expected_Delivery_Date")%> />
							&nbsp;</td>
							<td class="protectedtext"><yfc:i18n>Expected_Delivery_Date</yfc:i18n></td>
							<td class="timelineInRangeDay" title='<%=getI18N("Line_Has_Constraints")%>' >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
							<td class="protectedtext"><yfc:i18n>Line_Has_Constraints</yfc:i18n></td>
							<td class="timelineNonWorkingDay" title='<%=getI18N("Non_Working_Day")%>' >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
							<td class="protectedtext"><yfc:i18n>Non_Working_Day</yfc:i18n></td>
						</tr>
					</table>
				</fieldset>
				</td>
			</tr>
<%	}	%>
</table>
