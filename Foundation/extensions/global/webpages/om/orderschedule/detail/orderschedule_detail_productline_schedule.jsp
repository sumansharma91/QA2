<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<%  //System.out.println("Inside the file++++++++++++++++++++++++++++++++"); 

/////////////////////////////////////////////////////////////	Initialization of variables	/////////////////////////////////////////////////////
String EKNOfIPClicked = getParameter("EKNOfIPClicked");
//System.out.println("EKNOfIPClicked = "+EKNOfIPClicked);
YFCElement root = getRequestDOM(); 
//System.out.println("root in schedule is +++++++++++++++++ " +root.getString());
String OrdHeadKey = getParameter("xml:/Order/@OrderHeaderKey");
String OrdLineKey = null;
String distributionRuleID = null;
String scacAndServiceKey = null;
YFCDocument multiAPIInputDoc = YFCDocument.createDocument("MultiApi");
YFCElement multiAPIInputElem = multiAPIInputDoc.getDocumentElement();
YFCElement clickedIPEntityKeyNameElem = root.getChildElement(EKNOfIPClicked);

/////////////////////////////////////////////////////////		Forming ChangeOrder XML	//////////////////////////////////////////////////////

YFCElement promiseElem = root.getChildElement("Promise");
if (promiseElem != null) 
{	//System.out.println("Inside Promise >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	YFCElement overridesElem = promiseElem.getChildElement("Overrides");
	if (overridesElem != null) 
	{	//System.out.println("Inside Overrides <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		scacAndServiceKey = overridesElem.getAttribute("ScacAndServiceKey");
		//System.out.println("scacAndServiceKey = "+scacAndServiceKey);

		if (!isVoid(scacAndServiceKey)) {
	
			YFCElement firstAPIElement = multiAPIInputDoc.createElement("API");
			multiAPIInputElem.appendChild(firstAPIElement);
			firstAPIElement.setAttribute("Name","changeOrder");

			YFCElement firstAPIInputElement = multiAPIInputDoc.createElement("Input");
			firstAPIElement.appendChild(firstAPIInputElement);

			YFCElement firstAPIOrderElement = multiAPIInputDoc.createElement("Order");
			firstAPIInputElement.appendChild(firstAPIOrderElement);
			firstAPIOrderElement.setAttribute("Action","MODIFY");
			if (userHasOverridePermissions()) {
				firstAPIOrderElement.setAttribute("Override","Y");							
			} else{
				firstAPIOrderElement.setAttribute("Override","N");							
			}

			firstAPIOrderElement.setAttribute("OrderHeaderKey",OrdHeadKey);

			YFCElement firstAPIOrderLinesElement = multiAPIInputDoc.createElement("OrderLines");
			firstAPIOrderElement.appendChild(firstAPIOrderLinesElement);

			if (clickedIPEntityKeyNameElem != null) 
			{	//System.out.println("Inside >>>>>>>>>>>>>>>>>>>>>>>>"+clickedIPEntityKeyNameElem);
				
				YFCNodeList lstPromiseLine = clickedIPEntityKeyNameElem.getElementsByTagName("PromiseLine");
				if(lstPromiseLine != null)
				{	for(int j = 0; j < lstPromiseLine.getLength(); j++)
					{
						YFCElement promiseLineElem = (YFCElement)lstPromiseLine.item(j);
						OrdLineKey = promiseLineElem.getAttribute("OrderLineKey");

						YFCElement firstAPIOrderLineElement = multiAPIInputDoc.createElement("OrderLine");
						firstAPIOrderLinesElement.appendChild(firstAPIOrderLineElement);
						firstAPIOrderLineElement.setAttribute("OrderLineKey",OrdLineKey);
						firstAPIOrderLineElement.setAttribute("Action","MODIFY");
						firstAPIOrderLineElement.setAttribute("ScacAndServiceKey",scacAndServiceKey);
					}
				}
			}
			YFCElement firstAPITemplateElement = multiAPIInputDoc.createElement("Template");
			firstAPIElement.appendChild(firstAPITemplateElement);
			YFCElement firstAPITemplateOrderElement = multiAPIInputDoc.createElement("Order");
			firstAPITemplateElement.appendChild(firstAPITemplateOrderElement);
			firstAPITemplateOrderElement.setAttribute("OrderHeaderKey","");
		}
	}	
}
//////////////////////////////////////////////////////	 End of Change Order XML	//////////////////////////////////////////////////////////

//////////////////////////////////////////////////////	 Forming Schedule Order Lines XML	///////////////////////////////////////////////////////

	YFCElement secondAPIElement = multiAPIInputDoc.createElement("API");
	multiAPIInputElem.appendChild(secondAPIElement);

	secondAPIElement.setAttribute("Name","scheduleOrderLines");

	YFCElement secondAPIInputElement = multiAPIInputDoc.createElement("Input");
	secondAPIElement.appendChild(secondAPIInputElement);

	YFCElement secondAPIPromiseElement = multiAPIInputDoc.createElement("Promise");
	secondAPIInputElement.appendChild(secondAPIPromiseElement);

	secondAPIPromiseElement.setAttribute("OrderHeaderKey",OrdHeadKey);
	secondAPIPromiseElement.setAttribute("CheckInventory","Y");

	YFCElement secondAPIPromiseLinesElement = multiAPIInputDoc.createElement("PromiseLines");
	secondAPIPromiseElement.appendChild(secondAPIPromiseLinesElement);

	if (clickedIPEntityKeyNameElem != null) 
	{	//System.out.println("Inside >>>>>>>>>>>>>>>>>>>>>>>>"+clickedIPEntityKeyNameElem);
		
		YFCNodeList lstPromiseLine = clickedIPEntityKeyNameElem.getElementsByTagName("PromiseLine");
		if(lstPromiseLine != null)
		{	for(int j = 0; j < lstPromiseLine.getLength(); j++)
			{
				YFCElement promiseLineElem = (YFCElement)lstPromiseLine.item(j);

				YFCElement secondAPIPromiseLineElement = multiAPIInputDoc.createElement("PromiseLine");
				secondAPIPromiseLinesElement.appendChild(secondAPIPromiseLineElement);
				secondAPIPromiseLineElement.setAttribute("OrderLineKey",promiseLineElem.getAttribute("OrderLineKey"));
				secondAPIPromiseLineElement.setAttribute("ShipNode",promiseLineElem.getAttribute("ShipNode"));
				secondAPIPromiseLineElement.setAttribute("DeliveryDate",promiseLineElem.getAttribute("DeliveryDate"));
				secondAPIPromiseLineElement.setAttribute("ShipDate",promiseLineElem.getAttribute("ShipDate"));
				secondAPIPromiseLineElement.setAttribute("Quantity",promiseLineElem.getAttribute("Quantity"));
			}
		}
	}
//////////////////////////////////////////////////////	 End of Schedule Order Lines XML	//////////////////////////////////////////////////////////

//	System.out.println("MultiApi Is >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//	System.out.println(multiAPIInputDoc.getString());
%>
<yfc:callAPI apiName="multiApi" inputElement="<%=multiAPIInputElem%>" outputNamespace="MultiApiOutput"/>  
