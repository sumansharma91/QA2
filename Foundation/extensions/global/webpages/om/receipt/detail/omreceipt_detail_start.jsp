<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%> 
<%@include file="/console/jsp/modificationutils.jspf" %>
<%@page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<script language="javascript">
yfcDoNotPromptForChanges(true); 
</script>
<yfc:callAPI apiID="AP1"/>
<yfc:callAPI apiID="AP2"/>
<table class="view" width="100%">
<input type="hidden" name="xml:/Receipt/@ShipmentKey" value='<%=resolveValue("xml:/Shipment/@ShipmentKey")%>'/>
<input type="hidden" name="xml:/Receipt/@DocumentType" value='<%=resolveValue("xml:/Shipment/@DocumentType")%>'/>
<input type="hidden" name="xml:/Receipt/Shipment/@SCAC" value='<%=resolveValue("xml:/Shipment/@SCAC")%>'/>
<input type="hidden" name="xml:/Receipt/Shipment/@BolNo" value='<%=resolveValue("xml:/Shipment/@BolNo")%>'/>
<%
	String sNode = resolveValue ("xml:/Shipment/@ReceivingNode");
	String sShipmentKey = resolveValue("xml:/Shipment/@ShipmentKey");

	YFCElement getDockAppointmentListInput = YFCDocument.parse("<DockAppointment Node=\"" + sNode + "\" ShipmentKey=\"" + sShipmentKey + "\" AppointmentType=\"INBOUND\"/>").getDocumentElement();
    
	YFCElement getDockAppointmentListTemplate = YFCDocument.parse("<DockAppointmentList> <Dock LocationId=\"\"> <Dates> <Date> <UnavailableSlots> <UnavailableSlot> <DockAppointment IsReservedForInputCriteria=\"\"/> </UnavailableSlot> </UnavailableSlots> </Date> </Dates> </Dock> </DockAppointmentList>").getDocumentElement();
    %>
      <yfc:callAPI apiName="getDockAppointmentList" inputElement="<%=getDockAppointmentListInput%>" templateElement="<%=getDockAppointmentListTemplate%>" outputNamespace="getDockAppointmentList"/>
    <%
	
	YFCDocument newDoc = YFCDocument.createDocument ("AppointmentDetails");
	YFCElement newDocElem = newDoc.getDocumentElement ();
	newDocElem.setAttribute("LocationId","");
	String IsFound = "";

	YFCElement rootElem = (YFCElement)request.getAttribute ("getDockAppointmentList");
	if (!isVoid(rootElem)) {
		YFCNodeList dockList = (YFCNodeList)rootElem.getElementsByTagName("Dock");
		if (!isVoid(dockList)) {
			for(int i=0; i<dockList.getLength(); i++) {
				YFCElement dockElem = (YFCElement)dockList.item(i);
				if (!isVoid(dockElem)) {
					YFCElement datesElem = (YFCElement)dockElem.getChildElement("Dates");
					if (!isVoid(datesElem)) {
						YFCNodeList dateList = (YFCNodeList)datesElem.getElementsByTagName("Date");
						if (!isVoid(dateList)) {
							YFCElement dateElem = (YFCElement)dateList.item(0); // Taking the first element
							if (!isVoid(dateElem)) {
								YFCElement unavailbleSlotsElem = (YFCElement)dateElem.getChildElement("UnavailableSlots");
								if (!isVoid(unavailbleSlotsElem)) {
									YFCNodeList unavailbleSlotList = (YFCNodeList)datesElem.getElementsByTagName("UnavailableSlot");
									if (!isVoid(unavailbleSlotList)) {
										for(int j=0; j<unavailbleSlotList.getLength(); j++) {
											YFCElement unavailbleSlotElem = (YFCElement)unavailbleSlotList.item(j);
											if (!isVoid(unavailbleSlotElem)) {
												YFCElement dockAppointmentElem = (YFCElement)unavailbleSlotElem.getChildElement("DockAppointment");
												if (!isVoid(dockAppointmentElem)) {
													IsFound = dockAppointmentElem.getAttribute ("IsReservedForInputCriteria");
													if (!isVoid(IsFound) && !IsFound.equals("Y"))					
														continue;																											
												}
												if (!isVoid(IsFound) && IsFound.equals("Y"))
													break;
											}
										}
									}
								}
							}
						}
					}
					if (!isVoid(IsFound) && IsFound.equals("Y")) {
						newDocElem.setAttribute("LocationId", dockElem.getAttribute("LocationId"));
						break;
					}
				}
			}
		}
	}
	request.setAttribute ("AppointmentDetails", newDocElem);	
%>
	<tr>
		<td class="detaillabel">
			<yfc:i18n>Receipt_#</yfc:i18n>
        </td>
        <td>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/@ReceiptNo")%>/>
			<input type="hidden" name="xml:/Receipt/@Refresh" value="refresh" />
        </td>
    </tr>
<% if (equals("Y",getValue("ShipNode","xml:/ShipNodeList/ShipNode/@DcmIntegrationRealTime"))) { 		
	 String extraParamsForLocation = getExtraParamsForTargetBinding("xml:/Location/@Node", resolveValue("xml:/Shipment/@ReceivingNode"));
	 extraParamsForLocation = extraParamsForLocation + "&xml:/Location/@LocationType=DOCK";
 %>
	<tr> 
        <td class="detaillabel">
            <yfc:i18n>Receiving_Dock</yfc:i18n>
        </td>
        <td>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/@ReceivingDock","xml:/Receipt/@ReceivingDock","xml:AppointmentDetails:/AppointmentDetails/@LocationId")%>/>
			<img class="lookupicon" onclick="callLookup(this,'location','<%=extraParamsForLocation%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Location") %> />

        </td>
    </tr>
<% } %>
    <tr>
        <td class="detaillabel">
            <yfc:i18n>Receipt_Date</yfc:i18n>
        </td>
		<td>
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/@ReceiptDate", getTodayDate())%>/>
			<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
		</td>
    </tr>
	<tr>
        <td class="detaillabel">
            <yfc:i18n>Shipment_#</yfc:i18n>
        </td>
		     <td class="protectedtext">
                <yfc:getXMLValue binding="xml:/Shipment/@ShipmentNo"/>
                <input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Receipt/Shipment/@ShipmentNo","xml:/Shipment/@ShipmentNo")%> />
            </td>
        
    </tr>
	<tr>
        <td class="detaillabel">
            <yfc:i18n>Enterprise</yfc:i18n>
        </td>
		<td class="protectedtext">
                <yfc:getXMLValue binding="xml:/Shipment/@EnterpriseCode"/>
                <input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Receipt/Shipment/@EnterpriseCode","xml:/Shipment/@EnterpriseCode")%> />
		</td>
    </tr>
    <tr>
        <td class="detaillabel">
            <yfc:i18n>Expected_Delivery_Date</yfc:i18n>
        </td>
		    <td class="protectedtext">
                <yfc:getXMLValue binding="xml:/Shipment/@ExpectedDeliveryDate"/>
                <input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Receipt/Shipment/@ExpectedDeliveryDate","xml:/Shipment/@ExpectedDeliveryDate")%> />
            </td>
	</tr>
    <tr>
        <td class="detaillabel">
            <yfc:i18n>Receiving_Node</yfc:i18n>
        </td>
		    <td class="protectedtext">
                <yfc:getXMLValue binding="xml:/Shipment/@ReceivingNode"/>
                <input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Receipt/@ReceivingNode","xml:/Shipment/@ReceivingNode")%> />
            </td>
        
    </tr> 
	<tr>
        <td class="detaillabel">
            <yfc:i18n>Buyer</yfc:i18n>
        </td>
		    <td class="protectedtext">
                <yfc:getXMLValue binding="xml:/Shipment/@BuyerOrganizationCode"/>
                <input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Receipt/Shipment/@BuyerOrganizationCode","xml:/Shipment/@BuyerOrganizationCode")%> />
            </td>
        
    </tr> 
	<tr>
        <td class="detaillabel">
            <yfc:i18n>Seller</yfc:i18n>
        </td>
		    <td class="protectedtext">
                <yfc:getXMLValue binding="xml:/Shipment/@SellerOrganizationCode"/>
                <input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Receipt/Shipment/@SellerOrganizationCode","xml:/Shipment/@SellerOrganizationCode")%> />
            </td>
    </tr> 
		<tr>
        <td class="detaillabel">
            <yfc:i18n>No_Of_Expected_Pallets</yfc:i18n>
        </td>
		    <td>
               <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/@NumOfPallets","xml:/Shipment/@NumOfPallets")%> />
            </td>
    </tr>
		<tr>
        <td class="detaillabel">
            <yfc:i18n>No_Of_Expected_Cartons</yfc:i18n>
        </td>
		    <td>
                <input type="test" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/@NumOfCartons","xml:/Shipment/@NumOfCartons") %> />
            </td>
    </tr> 
</table>

