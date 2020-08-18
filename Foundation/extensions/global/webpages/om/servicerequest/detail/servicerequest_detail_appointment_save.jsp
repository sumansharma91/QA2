<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<% // System.out.println("Inside the file"); 
YFCElement root = getRequestDOM(); 
//	System.out.println(root.getString());

String OrdHeadKey = getParameter("xml:/Order/@OrderHeaderKey");
String OrdLineKey = getParameter("xml:/Order/OrderLines/OrderLine/@OrderLineKey");

YFCDate currentApptStartDate = null;
YFCDate currentApptEndDate = null;
YFCDate DBApptStartDate = null;
YFCDate DBApptEndDate = null;
boolean callchangeOrder = false;
boolean callscheduleOrderLines = false;

YFCDocument multiAPIInputDoc =null;
YFCElement multiAPIInputElem =null; 

multiAPIInputDoc = YFCDocument.createDocument("MultiApi");
multiAPIInputElem = multiAPIInputDoc.getDocumentElement();

YFCElement rootOrderElem = root.getChildElement("Order");
request.setAttribute("Order", rootOrderElem);

if (!isVoid(getValue("Order","xml:/Order/OrderLines/OrderLine/@PromisedApptStartDate"))) {
	currentApptStartDate = new YFCDate(getValue("Order","xml:/Order/OrderLines/OrderLine/@PromisedApptStartDate"),getLocale());
//	System.out.println("currentApptStartDate = "+currentApptStartDate);
}
if (!isVoid(getValue("Order","xml:/Order/OrderLines/OrderLine/@PromisedApptEndDate"))) {
	currentApptEndDate = new YFCDate(getValue("Order","xml:/Order/OrderLines/OrderLine/@PromisedApptEndDate"),getLocale());
//	System.out.println("currentApptEndDate = "+currentApptEndDate);
}

if (!isVoid(getParameter("xml:/dummy/@DBStartDate"))) {
	DBApptStartDate = new YFCDate(getParameter("xml:/dummy/@DBStartDate"),getLocale());
//	System.out.println("DBApptStartDate = "+DBApptStartDate);
}
if (!isVoid(getParameter("xml:/dummy/@DBEndDate"))) {
	DBApptEndDate = new YFCDate(getParameter("xml:/dummy/@DBEndDate"),getLocale());
//	System.out.println("DBApptEndDate = "+DBApptEndDate);
}


if (currentApptStartDate == null || currentApptEndDate == null || (currentApptStartDate.equals(DBApptStartDate) && currentApptEndDate.equals(DBApptEndDate))) 
{
//	System.out.println("Date's are equal "+callchangeOrder);
//	Do NOT call change Order API if the date-time in the DB and the currently chosen date-time are the same or if blank values are passed in 

} else{
	callchangeOrder = true;
//	System.out.println("Date's are NOT equal " +callchangeOrder);

	YFCElement firstAPIElement = multiAPIInputDoc.createElement("API");
	multiAPIInputElem.appendChild(firstAPIElement);

	firstAPIElement.setAttribute("Name","changeOrder");

	YFCElement firstAPIInputElement = multiAPIInputDoc.createElement("Input");
	firstAPIElement.appendChild(firstAPIInputElement);

	YFCElement firstAPIOrderElement = multiAPIInputDoc.createElement("Order");
	firstAPIInputElement.appendChild(firstAPIOrderElement);

	firstAPIOrderElement.setAttribute("OrderHeaderKey",OrdHeadKey);
	firstAPIOrderElement.setAttribute("Action","MODIFY");

	String modReasonCode = getParameter("xml:/Order/@ModificationReasonCode");
	String modReasonText = getParameter("xml:/Order/@ModificationReasonText");

	firstAPIOrderElement.setAttribute("ModificationReasonCode",modReasonCode);
	firstAPIOrderElement.setAttribute("ModificationReasonText",modReasonText);
	if (userHasOverridePermissions()) {
		firstAPIOrderElement.setAttribute("Override","Y");							
	} else{
		firstAPIOrderElement.setAttribute("Override","N");							
	}

	YFCElement firstAPIOrderLinesElement = multiAPIInputDoc.createElement("OrderLines");
	firstAPIOrderElement.appendChild(firstAPIOrderLinesElement);

	YFCElement firstAPIOrderLineElement = multiAPIInputDoc.createElement("OrderLine");
	firstAPIOrderLinesElement.appendChild(firstAPIOrderLineElement);

	firstAPIOrderLineElement.setAttribute("OrderLineKey",OrdLineKey);
	firstAPIOrderLineElement.setAttribute("Action","MODIFY");
	firstAPIOrderLineElement.setAttribute("PromisedApptStartDate",currentApptStartDate.getString());
	firstAPIOrderLineElement.setAttribute("PromisedApptEndDate",currentApptEndDate.getString());
	firstAPIOrderLineElement.setAttribute("Timezone",getParameter("xml:/dummy/@TimeZone"));
	//System.out.println("timezone:" + getParameter("xml:/dummy/@TimeZone") );

	String sCurrentChosenCellsAppointmentStatus = getParameter("currentChosenCellsAppointmentStatus");
    if(equals(sCurrentChosenCellsAppointmentStatus,"OVERRIDDEN"))
        firstAPIOrderLineElement.setAttribute("OverrideCapacityCheck", "Y");

	YFCElement firstAPITemplateElement = multiAPIInputDoc.createElement("Template");
	firstAPIElement.appendChild(firstAPITemplateElement);

	YFCElement firstAPITemplateOrderElement = multiAPIInputDoc.createElement("Order");
	firstAPITemplateElement.appendChild(firstAPITemplateOrderElement);

	firstAPITemplateOrderElement.setAttribute("OrderHeaderKey","");

}

if (equals(getParameter("CannotSchedule"),"N"))  //Only if it is schedulable
{
	//System.out.println("Cannot Schedule = N");	
	if (equals(getParameter("xml:/Promise/@CanScheduleImmediately"),"Y")) //condition to check if Schedule Immediately checkBox is checked
	{	
		callscheduleOrderLines = true;

		YFCElement secondAPIElement = multiAPIInputDoc.createElement("API");
		multiAPIInputElem.appendChild(secondAPIElement);

		secondAPIElement.setAttribute("Name","scheduleOrder");

		YFCElement secondAPIInputElement = multiAPIInputDoc.createElement("Input");
		secondAPIElement.appendChild(secondAPIInputElement);

		YFCElement secondAPIScheduleOrderElement = multiAPIInputDoc.createElement("ScheduleOrder");
		secondAPIInputElement.appendChild(secondAPIScheduleOrderElement);

		secondAPIScheduleOrderElement.setAttribute("OrderHeaderKey",OrdHeadKey);
		secondAPIScheduleOrderElement.setAttribute("OrderLineKey",OrdLineKey);
		if (equals(getParameter("xml:/Promise/@CanReleaseImmediately"),"Y"))
		{
			secondAPIScheduleOrderElement.setAttribute("ScheduleAndRelease","Y");
		}
	}
} 

//	System.out.println("MultiApi Is >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//	System.out.println(multiAPIInputDoc.getString());

if (callchangeOrder || callscheduleOrderLines) 
	{ %>
 		<yfc:callAPI apiName="multiApi" inputElement="<%=multiAPIInputElem%>" outputNamespace="MultiApiOutput"/>  
<%}

	%>		
