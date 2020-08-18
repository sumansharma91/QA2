<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfc.date.*" %>
<%@ page import="com.yantra.yfc.util.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<%  
	boolean firstHighlightedDateSelected = false;
	String slotValue = "slotTD_1_1";
	boolean disableDateSelection = false;
	String disableDateSelectionAttrib = getParameter("DisableDateSelection");
	if(equals(disableDateSelectionAttrib, "Y"))
		disableDateSelection = true;

	//Set images and tooltips
	String availableClickToSelectImage = getImageOptions(request.getContextPath() + "/console/icons/add.gif", "Available_Click_To_Select");
	String availableClickToSelectPreferredImage = getImageOptions(request.getContextPath() + "/console/icons/addpreferred.gif", "Preferred_Click_To_Select");
	String requiresConfirmationImage = getImageOptions(request.getContextPath() + "/console/icons/help.gif", "Requires_Confirmation_Click_To_Select");
	String notAvailableClickToOverrideImage = getImageOptions(request.getContextPath() + "/console/icons/delete.gif", 	"Not_Available");
	String imageIconClass = "columnicon";
	String nonAvailableImageIconClass = "nonclickablecolumnicon";
	String notAvailableImage=getImageOptions(request.getContextPath() + "/console/icons/delete.gif", 	"Not_Available");

	String allowSlotsWithInsufficientCapacity = resolveValue("xml:/WorkOrderUI/@AllowInsufficientCapacitySlots");
	if(equals(allowSlotsWithInsufficientCapacity, "Y")){ 
		notAvailableClickToOverrideImage = getImageOptions(request.getContextPath() + "/console/icons/delete.gif", 	"Not_Available_Click_To_Override");	
		nonAvailableImageIconClass = "columnicon";
	}

	if(disableDateSelection){
		availableClickToSelectImage = getImageOptions(request.getContextPath() + "/console/icons/appointmentready.gif", "Available");
		availableClickToSelectPreferredImage = getImageOptions(request.getContextPath() + "/console/icons/appointmentready.gif", "Available");
		requiresConfirmationImage = getImageOptions(request.getContextPath() + "/console/icons/appointmentready.gif", "Available");
		notAvailableClickToOverrideImage = getImageOptions(request.getContextPath() + "/console/icons/delete.gif", 	"Not_Available");
		imageIconClass = "nonclickablecolumnicon";
		nonAvailableImageIconClass = "nonclickablecolumnicon";
	}

	boolean showPreferred = false;
	boolean regionServiced = false;
	boolean capacityAvailable = false;
	boolean greyBackground = false;
	String sEarliestDate = "";
	if(disableDateSelection){
		sEarliestDate = getValue("Promise", "xml:/Promise/SuggestedOption/Option/Interactions/Interaction/@EarliestDate");
	} else{
		sEarliestDate = getValue("Promise", "xml:/Promise/SuggestedOption/Option/PromiseServiceLines/PromiseServiceLine/@DeliveryStartSearchDate");
	}
	
	YDate earlDate = null;
	if(disableDateSelection){
		earlDate = YDate.newDate(sEarliestDate);
	} else {
		earlDate = new YDate(sEarliestDate, getLocale());
	}
	sEarliestDate = earlDate.getString();
	//System.out.println("sEarliestDate =================="+sEarliestDate);

	String sLatestDate = "";
	if(disableDateSelection){
		sLatestDate = getValue("Promise", "xml:/Promise/SuggestedOption/Option/Interactions/Interaction/@LastDate");
	} else{
		sLatestDate = getValue("Promise", "xml:/Promise/SuggestedOption/Option/PromiseServiceLines/PromiseServiceLine/@DeliveryEndSearchDate");
	}

//	System.out.println("sLatestDate=================="+sLatestDate);

	YDate latestDate = YDate.newDate(sLatestDate);

	YDate nonworkDate = null;
	YDate endApptDate = null;
    String formatedStartDateInUserTimeZone = null;
    String formatedEndDateInUserTimeZone = null;
	String currentApptDateTimeSlot = getParameter("displayAppointmentDate");
	String tempCurrentChosenCellsAppointmentStatus=" ";
	String RPtimezone = getValue("Promise", "xml:/Promise/SuggestedOption/Option/PromiseServiceLines/PromiseServiceLine/Slots/@TimeZone");
	if(isVoid(RPtimezone) )
		RPtimezone = YFCLocale.getDefaultLocale().getTimezone();

	String userTimeZone = getLocale().getTimezone();
//	System.out.println("RP Time Zone = "+RPtimezone);
//	System.out.println("User Time Zone = "+userTimeZone);
//	System.out.println("displayAppointmentDate = "+currentApptDateTimeSlot);
%>
<table class="table" width="100%" border="1" >
<tr>
	<td>
<%	if(disableDateSelection){ %>
		<div style="width:1200px;overflow-y:visible;overflow-x:scroll;" >
<% } else { %>
		<div style="width:1095px;overflow-y:visible;overflow-x:scroll;" >
<% } %>

		<table width="100%" class="table">
		<%
				YFCDocument timeLineInputDoc = YFCDocument.createDocument("TimeLineInput");
				YFCElement timeLineInputElem = timeLineInputDoc.getDocumentElement();
				timeLineInputElem.setAttribute("StartDate", sEarliestDate );
				//timeLineInputElem.setAttribute("EndDate",sLatestDate);
				timeLineInputElem.setAttribute("EndDate",sLatestDate);

			//	building list of columns that need to be shown in the timeline View
				YFCElement tableColumnsElement = timeLineInputDoc.createElement("TableColumns");
				timeLineInputElem.appendChild(tableColumnsElement);

			//	first column	
				YFCElement singleColumnElement1 = timeLineInputDoc.createElement("TableColumn");
				tableColumnsElement.appendChild(singleColumnElement1);
				singleColumnElement1.setAttribute("ColumnTitle",getI18N("Slot_(_StartTime_-_EndTime_)"));	//Title of column to be shown in timeline view. Required
				singleColumnElement1.setAttribute("TDType","text"); 

		//		System.out.println(timeLineInputDoc.getString());
				request.setAttribute("TimeLineInput", timeLineInputElem);
			
		%>

			<thead>
				<jsp:include page="/yfsjspcommon/timelineview_thead.jsp" flush="true" />	
			</thead>
			<tbody> 
			<yfc:loopXML name="Promise" binding="xml:/Promise/SuggestedOption/Option/PromiseServiceLines/PromiseServiceLine/Slots/@Slot" id="Slot"> 
			<%   //System.out.println("SlotCounter = "+SlotCounter);
					YFCElement slotElem = (YFCElement) pageContext.getAttribute("Slot");
					YTime startYTime = slotElem.getTimeAttribute("StartTime");
					YTime endYTime = slotElem.getTimeAttribute("EndTime");

//					System.out.println("startYTime = "+startYTime);
//					System.out.println("endYTime = "+endYTime);

					String displayShortStartTime = (startYTime == null ) ? " " : startYTime.getString("HH:mm");
					String displayShortEndTime = (endYTime == null ) ? " " : endYTime.getString("HH:mm");
					String serviceSlotDesc = resolveValue("xml:/Slot/@ServiceSlotDesc");
			%>

			<tr>
				 <td class="timelineCellRightBorder">
					<yfc:getXMLValue binding="xml:/Slot/@ServiceSlotDesc"/>
					&nbsp(<%=displayShortStartTime%>&nbsp-&nbsp<%=displayShortEndTime%>)
				</td>
			<%	earlDate =  YDate.newDate(sEarliestDate);
//					System.out.println("earlDate ----------------------------------"+earlDate);
					latestDate =  YDate.newDate(sLatestDate);
//					System.out.println("latestDate ----------------------------------"+latestDate);

					int colcounter = 0;
					while(earlDate.lte(latestDate, true)){
						boolean aptAvail = false;
						boolean workingday=true;
						boolean apptIsConfirmed=false;
						colcounter++;
						String TD_ID = "slotTD_"+SlotCounter+"_"+colcounter;
						boolean bThisTDIsHighlightedCurrently = false;

						if (startYTime.gt(endYTime)) {	 //if slot spans across 1 day then update endApptDate to next day
							endApptDate = YDate.newDate(earlDate,1);
						} else{
							endApptDate = earlDate;
						}
					
						YTimestamp startTimeStampInRPTimeZone = YTimestamp.newTimestamp(earlDate,startYTime);
						YTimestamp endTimeStampInRPTimeZone = YTimestamp.newTimestamp(endApptDate,endYTime);

						YTimestamp startTimeStampInInstallTimeZone = YFCTimeZone.getInstallTimestamp(startTimeStampInRPTimeZone, RPtimezone);
						YTimestamp endTimeStampInInstallTimeZone = YFCTimeZone.getInstallTimestamp(endTimeStampInRPTimeZone, RPtimezone);

						formatedStartDateInUserTimeZone = startTimeStampInInstallTimeZone.getTimestampString(getLocale());
						formatedEndDateInUserTimeZone = endTimeStampInInstallTimeZone.getTimestampString(getLocale());
//						System.out.println("formatedStartDateInUserTimeZone +++++++++++++"+formatedStartDateInUserTimeZone);
//						System.out.println("formatedEndDateInUserTimeZone +++++++++++++"+formatedEndDateInUserTimeZone);
						String appointmentWindowString = displayTimeWindow(formatedStartDateInUserTimeZone,formatedEndDateInUserTimeZone,RPtimezone);
//						System.out.println("appointmentWindowString ="+appointmentWindowString);

						ArrayList NonWorkingDayList = getLoopingElementList("xml:/Promise/SuggestedOption/Option/Interactions/Interaction/NonWorkingDays/@NonWorkingDay"); //looping over Non working days
						for (int NonWorkingDayCounter = 0; NonWorkingDayCounter < NonWorkingDayList.size(); NonWorkingDayCounter++) 
						{	YFCElement singleNonWorkingDayElem = (YFCElement) NonWorkingDayList.get(NonWorkingDayCounter);
							String snonworkingdate= singleNonWorkingDayElem.getAttribute("Date");//CHECK WITH KMD
							nonworkDate = YDate.newDate(snonworkingdate);
//							System.out.println("nonworkDate = "+nonworkDate);							
							if(earlDate.equals(nonworkDate))	{
								workingday = false;
								break;
							}
						}
						%>
			<%		if (workingday) {
							ArrayList AvailableDateList = getLoopingElementList("xml:/Slot/AvailableDates/@AvailableDate"); 
							for (int AvailableDateCounter = 0; AvailableDateCounter < AvailableDateList.size(); AvailableDateCounter++) 
							{	YFCElement singleAvailableDateElem = (YFCElement) AvailableDateList.get(AvailableDateCounter);
								String aptAvaildate= singleAvailableDateElem.getAttribute("Date");//CHECK WITH KMD
								YDate AvlDate = YDate.newDate(aptAvaildate);
								showPreferred= singleAvailableDateElem.getBooleanAttribute("Preferred");
								regionServiced= singleAvailableDateElem.getBooleanAttribute("RegionServiced");
								capacityAvailable= singleAvailableDateElem.getBooleanAttribute("CapacityAvailable");
//								System.out.println("AvlDate = "+AvlDate);							
								if(earlDate.equals(AvlDate))	{
									if(capacityAvailable){
										aptAvail = true;
									} else {
										aptAvail = false;
										if(!regionServiced){
											greyBackground = true;
										}

									}

									if (equals(singleAvailableDateElem.getAttribute("Confirmed"),"Y")) {
										apptIsConfirmed=true;
									}
									break;
								}	
							}
						}
			%>
				<%	//System.out.println("WorkingDay = "+workingday);
						if(workingday)
						{	%>
							<td  ID="<%=TD_ID%>"
								<%	//System.out.println("currentApptDateTimeSlot = "+currentApptDateTimeSlot);
										//System.out.println("bThisTDIsHighlightedCurrently = "+bThisTDIsHighlightedCurrently);
										if (currentApptDateTimeSlot.equals(appointmentWindowString)) {	 
												bThisTDIsHighlightedCurrently = true;	%>
												class="timelineCellRightBorderWithBackground" 
								<%	}	else	{
										if(!greyBackground){
								%>
											class="timelineCellRightBorder" 
								<%		} else { 
											greyBackground = false;
								%>
											class="timelineCellRightBorderRegionNotServiced" 
								<%		}
									}	
								%>								
								align="center" >	 
								
								 <img align="middle"   
								<%if(aptAvail){	%>
									<%if(isVoid(serviceSlotDesc)){%>
									class="nonclickablecolumnicon";
									<%}else{%>
									<% if (!bThisTDIsHighlightedCurrently && !firstHighlightedDateSelected) {
											slotValue = TD_ID;
											firstHighlightedDateSelected = true;
									   }
									%>
									class="<%=imageIconClass%>" 	
									<%}%>
								<%		if (apptIsConfirmed) {	
											if (bThisTDIsHighlightedCurrently) 
												tempCurrentChosenCellsAppointmentStatus="CONFIRMED";%>											
										<%	if(!disableDateSelection && !isVoid(serviceSlotDesc)){	%>		
												onclick="setSlotValues('<%=formatedStartDateInUserTimeZone%>','<%=formatedEndDateInUserTimeZone%>','<%=appointmentWindowString%>','CONFIRMED' );changeHighlightedTD('<%=SlotCounter%>','<%=colcounter%>');"
										<%	}  %>

										<%	if(showPreferred && !isVoid(serviceSlotDesc)){	%>		
											<%=availableClickToSelectPreferredImage%> 
										<%	} else { %>		
												<%if(isVoid(serviceSlotDesc)){%>
													<%=notAvailableImage%> 
												<%}else{%>
											<%=availableClickToSelectImage%> 
												<%}%>
										<%	} 
											showPreferred = false; //reset the preferred flag for next iteration
										%>		
								<%	} else	{	
												if (bThisTDIsHighlightedCurrently) 
													tempCurrentChosenCellsAppointmentStatus="REQUIRES_CONFIRMATION";	%>							
										<%	if(!disableDateSelection && !isVoid(serviceSlotDesc)){	%>				
										onclick="setSlotValues('<%=formatedStartDateInUserTimeZone%>','<%=formatedEndDateInUserTimeZone%>','<%=appointmentWindowString%>','REQUIRES_CONFIRMATION' );changeHighlightedTD('<%=SlotCounter%>','<%=colcounter%>');"
										<%	}	%>		
											<%=requiresConfirmationImage%> 
								<%	}	%>		
								<%}else{	
											if (bThisTDIsHighlightedCurrently) 
												tempCurrentChosenCellsAppointmentStatus="OVERRIDDEN";%>

									<%if(isVoid(serviceSlotDesc)){%>
										class="nonclickablecolumnicon";
									<%}else{%>
									class="<%=nonAvailableImageIconClass%>" 	
									<%}%>
								<%	if(!disableDateSelection && equals(allowSlotsWithInsufficientCapacity, "Y")&& !isVoid(serviceSlotDesc)) {	%>		
	
										onclick="setSlotValues('<%=formatedStartDateInUserTimeZone%>','<%=formatedEndDateInUserTimeZone%>','<%=appointmentWindowString%>','OVERRIDDEN' );changeHighlightedTD('<%=SlotCounter%>','<%=colcounter%>');"
								<%	}	%>		
									 <%=notAvailableClickToOverrideImage%> 
    								 <% if (!bThisTDIsHighlightedCurrently && !firstHighlightedDateSelected) {
											slotValue = TD_ID;
											firstHighlightedDateSelected = true;
									   }
									%>
								<%}	%>
								/>
					<%		if (bThisTDIsHighlightedCurrently) {	%>
									<input type="hidden" name="thisTDisHighlightedCurrently" value="<%=TD_ID%>" />	
					<%		} %>

							</td>
					<%	}	else	{	%>
							<td  class="timelineNonWorkingDay" title="<%=getI18N("Non_Working_Day")%>">
								&nbsp;
							</td>
					<%	}
						earlDate = earlDate.newDate(earlDate,1);
						//System.out.println("earlDate after update= "+earlDate);
						} 
						%>
			</tr>
			</yfc:loopXML>
			<input type="hidden" name="currentChosenCellsAppointmentStatus"  value="<%=tempCurrentChosenCellsAppointmentStatus%>" />
			<input type="hidden" name="highlitedTDNotFound" value="<%=slotValue%>" />		<%-- cr 36072 --%>
			<input type="hidden" name="xml:/Order/OrderLines/OrderLine/@IsAppointmentOverridden" />
		</table>
	</div>
	</td>
</tr>
</table>
