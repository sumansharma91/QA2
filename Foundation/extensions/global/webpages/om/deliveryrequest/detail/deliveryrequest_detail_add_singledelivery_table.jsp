<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<script language="javascript">
	document.body.attachEvent("onunload", addDeliveryProcessOptionRecordsCall);
	<%	
		String sDelItem = resolveValue("xml:/DeliveryServiceGroup/DeliveryServices/Item/@ItemID");
		if ((isModificationAllowedWithModType("ADD_LINE", "xml:/Order/AllowedModifications")) &&
			!isVoid(sDelItem) ) 
		{%>
				yfcDoNotPromptForChanges(true);
	<%	}%>
</script> 

<table  editable="false" width="100%" class="table" cellspacing="0" cellpadding="0">
	<thead>
		<tr>
			<td class="tablecolumnheader" sortable="no" style="width:<%=getUITableSize("xml:/OrderLines/OrderLine/@PrimeLineNo")%>">
				<yfc:i18n>Line</yfc:i18n>
				<input type="hidden" name="hiddenItemCounter" value="1" />
			</td>
			<td class="tablecolumnheader" sortable="no" style="width:<%=getUITableSize("xml:/OrderLines/OrderLine/Item/@ItemID")%>">
				<yfc:i18n>Item_ID</yfc:i18n>
			</td>
			<td class="tablecolumnheader" sortable="no" style="width:<%=getUITableSize("xml:/OrderLines/OrderLine/Item/@ProductClass")%>">
				<yfc:i18n>PC</yfc:i18n>
			</td>
			<td class="tablecolumnheader" sortable="no" style="width:<%=getUITableSize("xml:/OrderLines/OrderLine/Item/@UnitOfMeasure")%>">
				<yfc:i18n>UOM</yfc:i18n>
			</td>
			<td class="tablecolumnheader" sortable="no" style="width:<%=getUITableSize("xml:/OrderLines/OrderLine/Item/@ItemShortDesc")%>">
				<yfc:i18n>Description</yfc:i18n>
			</td>
		<%	int noOfDeliveryServices = 0;	%>
			<yfc:loopXML binding="xml:/DeliveryServiceGroup/DeliveryServices/@Item" id="Item">
		<%		noOfDeliveryServices = ItemCounter.intValue();	%>
			</yfc:loopXML>			
		<%	int noOfOrderLines = 0;	%>
			<yfc:loopXML binding="xml:/DeliveryServiceGroup/OrderLines/@OrderLine" id="OrderLine">
		<%		noOfOrderLines = OrderLineCounter.intValue();	%>
			</yfc:loopXML>		
		<%	String checkedDeliveryItem=null;	

			if(!isVoid(sDelItem)) {
		%>
			<yfc:loopXML binding="xml:/DeliveryServiceGroup/DeliveryServices/@Item" id="Item">
				<td class="tablecolumnheader" sortable="no" style="width:<%=getUITableSize("xml:/OrderLines/OrderLine/Item/@ItemShortDesc")%>">
				<%	if (noOfDeliveryServices>1 && isModificationAllowedWithModType("ADD_LINE", "xml:/Order/AllowedModifications")) {		%>
					<%	 if (ItemCounter.intValue()==1) { 	
								checkedDeliveryItem = getValue("Item","xml:/Item/@ItemID");	%>
								<input type="hidden" name="xml:/OrderLine/Item/@UnitOfMeasure"  value="<%=resolveValue("xml:/Item/@UnitOfMeasure")%>" />	
					<%	}	%>	
							<input type="radio"  <%=yfsGetRadioOptions("xml:/OrderLine/Item/@ItemID", checkedDeliveryItem, "xml:/Item/@ItemID", "xml:/Order/AllowedModifications")%>
							onclick="addDeliveryChangeElements('<%=ItemCounter.intValue()%>','<%=noOfDeliveryServices%>','<%=noOfOrderLines%>','chkboxColumn_','chkEntityDSOPT','RadioID_','<%=resolveValue("xml:/Item/@UnitOfMeasure")%>');addDeliveryOptionDisplay('<%=ItemCounter.intValue()%>','optionSet_','<%=noOfDeliveryServices%>')" >
				<%	}	else{	 %>
								<input type="hidden" name="xml:/OrderLine/Item/@ItemID"  value="<%=resolveValue("xml:/Item/@ItemID")%>" />	<input type="hidden" name="xml:/OrderLine/Item/@UnitOfMeasure"  value="<%=resolveValue("xml:/Item/@UnitOfMeasure")%>" />						
				<%	}
					String sItemBinding = null;
					if(isVoid(resolveValue("xml:/Item/PrimaryInformation/@Description") ) )
						sItemBinding = "xml:/Item/@ItemID";
					else
						sItemBinding = "xml:/Item/PrimaryInformation/@ShortDescription";
				%>
					<yfc:getXMLValue  binding='<%=sItemBinding%>' />
				<%	if (equals(resolveValue("xml:/Item/@PriceBasedOnProductAssociation"),"N"))	{	%>
							<yfc:i18n>Price___</yfc:i18n>
							<% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%>&nbsp;<yfc:getXMLValue  binding="xml:/Item/@Price" />&nbsp;<%=curr0[1]%>						
				<%	}	else	{	%>
								<yfc:i18n>Price___</yfc:i18n><yfc:i18n>TBD</yfc:i18n>
				<%	}%>
				</td>
			</yfc:loopXML>
			<%	}%>
		</tr>
	</thead>
	<tbody>
			<yfc:loopXML binding="xml:/DeliveryServiceGroup/OrderLines/@OrderLine" id="OrderLine">
				<tr>
					<yfc:makeXMLInput name="orderLineKey">
						<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderLine/@OrderLineKey"/>
						<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderHeaderKey" value="xml:/Order/@OrderHeaderKey"/>
					</yfc:makeXMLInput>
					<td class="tablecolumn">
						<a <%=getDetailHrefOptions("L01", getParameter("orderLineKey"),"")%>><yfc:getXMLValue binding="xml:/OrderLine/@PrimeLineNo"/></a>
					</td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemID"/></td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ProductClass"/></td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@UnitOfMeasure"/></td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemShortDesc"/></td>
					<yfc:loopXML binding="xml:/DeliveryServiceGroup/DeliveryServices/@Item" id="Item">
					<%	//Loops through Delivery Item columns and checks if a particular Delivery Item ID is valid for a particular Product Item ID, and if so, paints a check box in that row-column intersection
						boolean hasCheckBox=false;
					%>
						<yfc:loopXML binding="xml:/OrderLine/ValidDeliveryServices/@ValidDeliveryServiceItem" id="ValidDeliveryServiceItem">
						<%	/*System.out.println("OrderLineCounter = "+OrderLineCounter);
								System.out.println("ValidDeliveryServiceItemCounter = "+ValidDeliveryServiceItemCounter);
								System.out.println("DeliveryServiceCounter = "+DeliveryServiceCounter);
								System.out.println("DeliveryServiceItem = "+getValue("Item","xml:/Item/@ItemID"));
								System.out.println("DeliveryItem = "+getValue("ValidDeliveryServiceItem","xml:/ValidDeliveryServiceItem/@ItemID")); */

								if (equals(getValue("Item","xml:/Item/@ItemID"),getValue("ValidDeliveryServiceItem","xml:/ValidDeliveryServiceItem/@ItemID")) && equals(getValue("Item","xml:/Item/@UnitOfMeasure"),getValue("ValidDeliveryServiceItem","xml:/ValidDeliveryServiceItem/@UnitOfMeasure"))) 
									hasCheckBox = true;	%>
						</yfc:loopXML>
				<%	if (!isVoid(sDelItem) )  {
						if (hasCheckBox) 
							{	 	String checkBoxKeyValue=getValue("OrderLine","xml:/OrderLine/@OrderLineKey");
									String checkBoxKeyName=	"xml:/OrderLine/ProductAssociations/ProductAssociation_"+OrderLineCounter+"/ProductLine/@OrderLineKey" ; 
																		%>
								<td class="checkboxcolumn" >
									<input ID="chkboxColumn_<%=ItemCounter%>_<%=OrderLineCounter%>" type="checkbox" yfcSuppressInitCheckBox="true"  <%=yfsGetCheckBoxOptions(checkBoxKeyName,"",checkBoxKeyValue,"xml:/Order/AllowedModifications")%> 
									<%	
										String hiddenItemCounterVal = request.getParameter("hiddenItemCounter");
										if (isVoid(hiddenItemCounterVal) || !hasErrorOccurred()) {
											hiddenItemCounterVal = "1";
										}

										if (equals(ItemCounter.toString(),hiddenItemCounterVal) )	{ 
											if (isModificationAllowedWithModType("ADD_LINE", "xml:/Order/AllowedModifications"))	{
												if (!hasErrorOccurred()) {%>
													CHECKED
									<%			}
											}
										} else{ %>
											DISABLED									
									<%	}%>
									/>
								</td>
					<%	} else {		%>
								<td class="tablecolumn" ID="chkboxColumn_<%=ItemCounter%>_<%=OrderLineCounter%>" >
								</td>
					<%	}	
					}%>

					</yfc:loopXML>
				</tr>
			</yfc:loopXML>
		</tbody>
</table>
<table  editable="false" width="100%" class="table" cellspacing="0" cellpadding="0">
	<tr>
		<td>
			<%	ArrayList DeliveryServiceList = getLoopingElementList("xml:/DeliveryServiceGroup/DeliveryServices/@Item"); //looping over Item to [paint options
			for (int DeliveryServiceCounter = 0; DeliveryServiceCounter < DeliveryServiceList.size(); DeliveryServiceCounter++) 
			{
					YFCElement singleDeliveryServiceElem = (YFCElement) DeliveryServiceList.get(DeliveryServiceCounter);
					//YFCElement ItemElem = singleDeliveryServiceElem.getChildElement("Item");
					YFCElement ItemOptionsElem = singleDeliveryServiceElem.getChildElement("ItemOptions");
					if (ItemOptionsElem != null) 
					{	//System.out.println("ItemOptionsElem = " +ItemOptionsElem.getString());
						YFCElement ItemOptionElem = ItemOptionsElem.getChildElement("ItemOption");
						if (ItemOptionElem != null)
						{
								request.setAttribute("ItemOptions", ItemOptionsElem);	%>
								<table class="view" width="100%">
									<tr id='<%="optionSet_" + (DeliveryServiceCounter+1)%>' 
									<%	if (DeliveryServiceCounter != 0)  {	%>
												style="display:none"
									<%	}	%>>
									<% //System.out.println("trname = optionSet_" + (DeliveryServiceCounter+1));%>
										<td >
											<jsp:include page="/om/deliveryrequest/detail/deliveryrequest_detail_deliveryitemoptions.jsp" flush="true">
												<jsp:param name="optionSetBelongingToLine" value='<%=String.valueOf(DeliveryServiceCounter+1)%>'/>
											</jsp:include>
										</td>
									</tr>
								</table>
			<%		}
					}	
								
			}	%>
		</td>
	</tr>
</table>
					

