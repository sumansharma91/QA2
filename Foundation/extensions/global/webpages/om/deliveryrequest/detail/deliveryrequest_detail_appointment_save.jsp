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
//System.out.println(root.getString());

String OrdHeadKey = getParameter("xml:/Order/@OrderHeaderKey");
String OrdLineKey = getParameter("xml:/Order/OrderLines/OrderLine/@OrderLineKey");

YFCDate currentApptStartDate = null;
YFCDate currentApptEndDate = null;
YFCDate DBApptStartDate = null;
YFCDate DBApptEndDate = null;
boolean callchangeOrderForRemoveAssociation = false;
boolean callchangeOrderForDateUpdate = false;
boolean callscheduleOrder = false;

YFCDocument multiAPIInputDoc =null;
YFCElement multiAPIInputElem =null; 

multiAPIInputDoc = YFCDocument.createDocument("MultiApi");
multiAPIInputElem = multiAPIInputDoc.getDocumentElement();

YFCElement orderElem = root.getChildElement("Order");
request.setAttribute("Order", orderElem);

if (!isVoid(getValue("Order","xml:/Order/OrderLines/OrderLine/@PromisedApptStartDate"))) {
	currentApptStartDate = new YFCDate(getValue("Order","xml:/Order/OrderLines/OrderLine/@PromisedApptStartDate"),getLocale());
//	System.out.println("currentApptStartDate = "+currentApptStartDate);
}
if (!isVoid(getValue("Order","xml:/Order/OrderLines/OrderLine/@PromisedApptEndDate"))) {
	currentApptEndDate = new YFCDate(getValue("Order","xml:/Order/OrderLines/OrderLine/@PromisedApptEndDate"),getLocale());
//	System.out.println("currentApptEndDate = "+currentApptEndDate);
}

String omitChkBox = getParameter("timelineEntityKey");

if (omitChkBox.length() == 0) {
//	System.out.println("00000000000");
} else{
//	System.out.println("11111111111");

	orderElem.setAttribute("Action","MODIFY");
	if (userHasOverridePermissions()) {
		orderElem.setAttribute("Override","Y");							
	} else{
		orderElem.setAttribute("Override","N");							
	}

	YFCElement OrderLinesElem = orderElem.getChildElement("OrderLines");
	orderElem.removeChild(OrderLinesElem);

	YFCElement productServiceAssocsElem = orderElem.getChildElement("ProductServiceAssocs");

	if(productServiceAssocsElem != null)	{
		//System.out.println(root.getString());
		YFCNodeList lstProductServiceAssoc = productServiceAssocsElem.getElementsByTagName("ProductServiceAssoc");
		if(lstProductServiceAssoc != null)
		{	for(int j = 0; j < lstProductServiceAssoc.getLength(); j++)
			{	//System.out.println("looping through ------------------------------------------------------------ "+j);
				YFCElement productServiceAssocElem = (YFCElement)lstProductServiceAssoc.item(j);
				productServiceAssocElem.setAttribute("Action","REMOVE");
				YFCElement serviceLineElem = root.getOwnerDocument().createElement("ServiceLine");
				productServiceAssocElem.appendChild(serviceLineElem);
				serviceLineElem.setAttribute("OrderLineKey",OrdLineKey);
			}
		}
//		System.out.println(orderElem.getString());
		callchangeOrderForRemoveAssociation = true;

		YFCElement firstAPIElement = multiAPIInputDoc.createElement("API");
		multiAPIInputElem.appendChild(firstAPIElement);

		firstAPIElement.setAttribute("Name","changeOrder");

		YFCElement firstAPIInputElement = multiAPIInputDoc.createElement("Input");
		firstAPIElement.appendChild(firstAPIInputElement);

		firstAPIInputElement.importNode(orderElem);

		YFCElement firstAPITemplateElement = multiAPIInputDoc.createElement("Template");
		firstAPIElement.appendChild(firstAPITemplateElement);

		YFCElement firstAPITemplateOrderElement = multiAPIInputDoc.createElement("Order");
		firstAPITemplateElement.appendChild(firstAPITemplateOrderElement);

		firstAPITemplateOrderElement.setAttribute("OrderHeaderKey","");

//		System.out.println("multiAPIInputElem = "+multiAPIInputElem.getString());

	}	else	{
		//System.out.println("ROOT IS NULL");
	}
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
//	System.out.println("Date's are equal "+callchangeOrderForDateUpdate);
//	Do NOT call change Order API if the date-time in the DB and the currently chosen date-time are the same

} else{
	callchangeOrderForDateUpdate = true;
//	System.out.println("Date's are NOT equal " +callchangeOrderForDateUpdate);

	YFCElement secondAPIElement = multiAPIInputDoc.createElement("API");
	multiAPIInputElem.appendChild(secondAPIElement);

	secondAPIElement.setAttribute("Name","changeOrder");

	YFCElement secondAPIInputElement = multiAPIInputDoc.createElement("Input");
	secondAPIElement.appendChild(secondAPIInputElement);

	YFCElement secondAPIOrderElement = multiAPIInputDoc.createElement("Order");
	secondAPIInputElement.appendChild(secondAPIOrderElement);

	secondAPIOrderElement.setAttribute("OrderHeaderKey",OrdHeadKey);
	secondAPIOrderElement.setAttribute("Action","MODIFY");

	String modReasonCode = getParameter("xml:/Order/@ModificationReasonCode");
	String modReasonText = getParameter("xml:/Order/@ModificationReasonText");

	secondAPIOrderElement.setAttribute("ModificationReasonCode",modReasonCode);
	secondAPIOrderElement.setAttribute("ModificationReasonText",modReasonText);
	if (userHasOverridePermissions()) {
		secondAPIOrderElement.setAttribute("Override","Y");							
	} else{
		secondAPIOrderElement.setAttribute("Override","N");							
	}

	YFCElement secondAPIOrderLinesElement = multiAPIInputDoc.createElement("OrderLines");
	secondAPIOrderElement.appendChild(secondAPIOrderLinesElement);

	YFCElement secondAPIOrderLineElement = multiAPIInputDoc.createElement("OrderLine");
	secondAPIOrderLinesElement.appendChild(secondAPIOrderLineElement);

	secondAPIOrderLineElement.setAttribute("OrderLineKey",OrdLineKey);
	secondAPIOrderLineElement.setAttribute("Action","MODIFY");
	secondAPIOrderLineElement.setAttribute("PromisedApptStartDate",currentApptStartDate.getString());
	secondAPIOrderLineElement.setAttribute("PromisedApptEndDate",currentApptEndDate.getString());
	secondAPIOrderLineElement.setAttribute("Timezone",getParameter("xml:/dummy/@TimeZone"));

    if(equals(getParameter("currentChosenCellsAppointmentStatus"), "OVERRIDDEN"))
        secondAPIOrderLineElement.setAttribute("OverrideCapacityCheck", "Y");

	YFCElement secondAPITemplateElement = multiAPIInputDoc.createElement("Template");
	secondAPIElement.appendChild(secondAPITemplateElement);

	YFCElement secondAPITemplateOrderElement = multiAPIInputDoc.createElement("Order");
	secondAPITemplateElement.appendChild(secondAPITemplateOrderElement);

	secondAPITemplateOrderElement.setAttribute("OrderHeaderKey","");

}

if (equals(getParameter("CannotSchedule"),"N"))  //Only if it is schedulable
{
	//System.out.println("Cannot Schedule = N");	
	if (equals(getParameter("xml:/Promise/@CanScheduleImmediately"),"Y")) //condition to check if Schedule Immediately checkBox is checked
	{	
		callscheduleOrder = true;

		YFCElement thirdAPIElement = multiAPIInputDoc.createElement("API");
		multiAPIInputElem.appendChild(thirdAPIElement);

		thirdAPIElement.setAttribute("Name","scheduleOrder");

		YFCElement thirdAPIInputElement = multiAPIInputDoc.createElement("Input");
		thirdAPIElement.appendChild(thirdAPIInputElement);

		YFCElement thirdAPIScheduleOrderElement = multiAPIInputDoc.createElement("ScheduleOrder");
		thirdAPIInputElement.appendChild(thirdAPIScheduleOrderElement);

		thirdAPIScheduleOrderElement.setAttribute("OrderHeaderKey",OrdHeadKey);
		thirdAPIScheduleOrderElement.setAttribute("OrderLineKey",OrdLineKey);
		thirdAPIScheduleOrderElement.setAttribute("IgnoreReleaseDate", "Y");	//	cr 36362
		if (equals(getParameter("xml:/Promise/@CanReleaseImmediately"),"Y"))
		{
			thirdAPIScheduleOrderElement.setAttribute("ScheduleAndRelease","Y");
		}

	}
} 

//	System.out.println("MultiApi Is >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//	System.out.println(multiAPIInputDoc.getString());

if (callchangeOrderForRemoveAssociation || callchangeOrderForDateUpdate || callscheduleOrder) 
{ %>
 		<yfc:callAPI apiName="multiApi" inputElement="<%=multiAPIInputElem%>" outputNamespace="MultiApiOutput"/>  
<%}	%>		
