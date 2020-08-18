<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/negotiationutils.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/negotiation.js"></script>

<%
    boolean isInitiator = isInitiatorOrganization((YFCElement) request.getAttribute("Negotiations"));

    String myLastResponseBinding;

    String canInitiatorRespond = getValue("Negotiations", "xml:/Negotiations/Negotiation/@CanInitiatorRespond");
    String canNegotiatorRespond = getValue("Negotiations", "xml:Negotiations/Negotiation/@CanNegotiatorRespond");

    boolean showResponseComboBox=false;
    boolean showSplitRowIcon=false;
    
    String itemId="";
    String productClass="";
    String unitOfMeasure="";
    String freightTerms="";
    String shipDate="";
    String deliveryDate="";
    String price="";

    YFCElement responseList = makeNegotiationHeaderResponseActionList("LINE", isInitiator, (YFCElement) request.getAttribute("NegotiationResponseList"));
    request.setAttribute("NegotiationLineResponseList", responseList);

    YFCElement negotiableLineAttrs = getNegotiableLineAttributes((YFCElement) request.getAttribute("Negotiations"));

    String itemIdSplit = negotiableLineAttrs.getAttribute("ItemID");
    String productClassSplit = negotiableLineAttrs.getAttribute("ProductClass");
    String unitOfMeasureSplit = negotiableLineAttrs.getAttribute("UnitOfMeasure");
    String freightTermsSplit = negotiableLineAttrs.getAttribute("FreightTerms");
    String shipDateSplit = negotiableLineAttrs.getAttribute("ShipDate");
    String deliveryDateSplit =  negotiableLineAttrs.getAttribute("DeliveryDate");
    String priceSplit =  negotiableLineAttrs.getAttribute("Price");
	String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("Negotiations", "xml:/Negotiations/Negotiation/@EnterpriseCode"));
%>

<table class="simpletable" cellspacing="0" width="100%">
    <thead>
        <tr>
            <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:<%=getUITableSize("xml:/Negotiations/Negotiation/NegotiationLines/NegotiationLine/@PrimeLineNo")%>"><yfc:i18n>Line</yfc:i18n></td>
            <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:<%=getUITableSize("xml:/Negotiations/Negotiation/NegotiationLines/NegotiationLine/@LastResponseActionDesc")%>"><yfc:i18n>Action_1</yfc:i18n></td>
            <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:<%=getUITableSize("xml:/Negotiations/Negotiation/@EnterpriseCode")%>"><yfc:i18n>Organization_Code</yfc:i18n></td>
            <td class="tablecolumnheader" nowrap="true" sortable="no"><yfc:i18n>Resp_#</yfc:i18n></td>
            <td class="tablecolumnheader" nowrap="true" sortable="no"><yfc:i18n>For_Resp_#</yfc:i18n></td>
            <td class="tablecolumnheader" nowrap="true"	sortable="no" style="width:<%=getUITableSize("xml:/Negotiations/Negotiation/NegotiationLines/NegotiationLine/@ItemID")%>"><yfc:i18n>Item_ID</yfc:i18n></td>
            <td class="tablecolumnheader" sortable="no" style="width:<%=getUITableSize("xml:/Negotiations/Negotiation/NegotiationLines/NegotiationLine/@ProductClass")%>"><yfc:i18n>PC</yfc:i18n></td>
            <td class="tablecolumnheader" sortable="no" style="width:<%=getUITableSize("xml:/Negotiations/Negotiation/NegotiationLines/NegotiationLine/@UnitOfMeasure")%>"><yfc:i18n>UOM</yfc:i18n></td>

            <% if (!equals(freightTermsSplit,"NULL")) { %>
                <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:<%=getUITableSize("xml:/Negotiations/Negotiation/NegotiationLines/NegotiationLine/@FreightTerms")%>"><yfc:i18n>Freight_Terms</yfc:i18n></td>
            <% } %>

            <% if (!equals(shipDateSplit,"NULL")) {%> 
                <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:<%=getUITableSize("xml:/Negotiations/Negotiation/NegotiationLines/NegotiationLine/@ShipDate")%>"><yfc:i18n>Ship_Date</yfc:i18n></td>
            <% } %>

            <% if (!equals(deliveryDateSplit,"NULL")) {%> 
                <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:<%=getUITableSize("xml:/Negotiations/Negotiation/NegotiationLines/NegotiationLine/@DeliveryDate")%>"><yfc:i18n>Delivery_Date</yfc:i18n></td>
            <% } %>

            <% if (!equals(priceSplit,"NULL")) { %>
                <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:<%=getUITableSize("xml:/Negotiations/Negotiation/NegotiationLines/NegotiationLine/@Price")%>"><yfc:i18n>Price</yfc:i18n></td>
            <% } %>
            
            <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:<%=getUITableSize("xml:/Negotiations/Negotiation/NegotiationLines/NegotiationLine/@Quantity")%>"><yfc:i18n>Quantity</yfc:i18n></td>
        </tr>
    </thead>

    <%
        // If at least one Negotiable Line Attribute is negotiable, show response combobox.
        if ( !equals(itemIdSplit,"NULL") || !equals(productClassSplit,"NULL") || !equals(unitOfMeasureSplit,"NULL") || !equals(freightTermsSplit,"NULL") || !equals(shipDateSplit,"NULL") || !equals(deliveryDateSplit,"NULL") || !equals(priceSplit,"NULL")) {
            showResponseComboBox=true;
        }
        else {
            showResponseComboBox=false;
        }

        // If at least one Negotiable Line Attribute has SplitLine="Y" show plus icon for splitting response rows.
        if ( equals(itemIdSplit,"Y") || equals(productClassSplit,"Y") || equals(unitOfMeasureSplit,"Y") || equals(freightTermsSplit,"Y") || equals(shipDateSplit,"Y") || equals(deliveryDateSplit,"Y") || equals(priceSplit,"Y")) {
            showSplitRowIcon=true;
        }
        else {
            showSplitRowIcon=false;
        }
    %>

	<tbody>
		<yfc:loopXML binding="xml:/Negotiations/Negotiation/NegotiationLines/@NegotiationLine" id="NegotiationLine">

            <%
                request.setAttribute("NegotiationLine", pageContext.getAttribute("NegotiationLine"));

                boolean isLastLineRespFromMyOrg = isLastLineResponseFromMyOrg((YFCElement) request.getAttribute("NegotiationLine"));
                String firstLineResponse=null;
                String secondLineResponse=null;
                String forResponse=null;
                String firstLineOrg=null;
                String secondLineOrg=null;

                if (isInitiator) {
                    firstLineResponse = "LastInitiator";
                    secondLineResponse = "LastNegotiator";

                    firstLineOrg = "InitiatorOrgCode";
                    secondLineOrg = "NegotiatorOrgCode";

                    forResponse = "LastNegotiator";
                }
                else {
                    firstLineResponse = "LastNegotiator";
                    secondLineResponse = "LastInitiator";

                    firstLineOrg = "NegotiatorOrgCode";
                    secondLineOrg = "InitiatorOrgCode";

                    forResponse = "LastInitiator";
                }
            %>

            <% boolean lineInNegotiation = false; %>

            <% // Show my last response. %>
            <yfc:loopXML binding='<%=buildBinding("xml:/NegotiationLine/", firstLineResponse, "LineResponse/LineTerms/@LineTermsDetail")%>' id="LineTermsDetail">
                <tr class="evenrow">
                    <% lineInNegotiation = true; %>

                    <% // Don't repeat the line number and action for additional Line Terms %>
                    <% if (LineTermsDetailCounter.intValue() > 1) { %>
                        <td class="tablecolumn"/>
                        <td class="tablecolumn"/>
                        <td class="tablecolumn"/>
                        <td class="numerictablecolumn"/>
                        <td class="numerictablecolumn"/>
                    <%} else {%>
                        <td class="tablecolumn">
                            <yfc:getXMLValue binding="xml:/NegotiationLine/@PrimeLineNo"/>
                        </td>
                        <td class="tablecolumn" nowrap="true">
							<%=displayOrderStatus("N",getValue("NegotiationLine",buildBinding("xml:/NegotiationLine/", firstLineResponse, "LineResponse/@ResponseActionDesc")),true)%>
                        </td>
                        <td class="tablecolumn">
                           <yfc:getXMLValue binding='<%=buildBinding("xml:/Negotiations/Negotiation/@", firstLineOrg, "")%>'/>
                        </td>
                        <td class="numerictablecolumn">
                           <yfc:getXMLValue binding='<%=buildBinding("xml:/NegotiationLine/", firstLineResponse, "LineResponse/@ResponseNo")%>'/>
                        </td>
                        <td class="numerictablecolumn">
                           <yfc:getXMLValue binding='<%=buildBinding("xml:/NegotiationLine/", firstLineResponse, "LineResponse/@ForResponseNo")%>'/>
                        </td>
                    <%}%>

                    <td class="tablecolumn"><yfc:getXMLValue binding="xml:/LineTermsDetail/@ItemID"/></td>
                    <td class="tablecolumn"><yfc:getXMLValue binding="xml:/LineTermsDetail/@ProductClass"/></td>
                    <td class="tablecolumn"><yfc:getXMLValue binding="xml:/LineTermsDetail/@UnitOfMeasure"/></td>

                    <% if (!equals(freightTermsSplit,"NULL")) { %>
                        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/LineTermsDetail/@FreightTerms"/></td>
                    <% } %>

                    <% if (!equals(shipDateSplit,"NULL")) {%>
                        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/LineTermsDetail/@ShipDate"/></td>
                    <% } %>

                    <% if (!equals(deliveryDateSplit,"NULL")) {%>
                        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/LineTermsDetail/@DeliveryDate"/></td>
                    <% } %>

                    <% if (!equals(priceSplit,"NULL")) { %>
                        <td class="numerictablecolumn"><yfc:getXMLValue binding="xml:/LineTermsDetail/@Price"/></td>
                    <% } %>

                    <td class="numerictablecolumn"><yfc:getXMLValue binding="xml:/LineTermsDetail/@Quantity"/></td>
                </tr>
                <%
                    itemId = getValue("LineTermsDetail", "xml:/LineTermsDetail/@ItemID");
                    productClass = getValue("LineTermsDetail", "xml:/LineTermsDetail/@ProductClass");
                    unitOfMeasure = getValue("LineTermsDetail", "xml:/LineTermsDetail/@UnitOfMeasure");
                    freightTerms = getValue("LineTermsDetail", "xml:/LineTermsDetail/@FreightTerms");
                    shipDate = getValue("LineTermsDetail", "xml:/LineTermsDetail/@ShipDate");
                    deliveryDate = getValue("LineTermsDetail", "xml:/LineTermsDetail/@DeliveryDate");
                    price = getValue("LineTermsDetail", "xml:/LineTermsDetail/@Price");
                %>
            </yfc:loopXML>

            <% // Show other party's response %>
            <yfc:loopXML binding='<%=buildBinding("xml:/NegotiationLine/", secondLineResponse, "LineResponse/LineTerms/@LineTermsDetail")%>' id="LineTermsDetail">
                <tr class="oddrow">
                    <% lineInNegotiation = true; %>

                    <% // Don't repeat the line number and action for additional Line Terms %>
                    <% if (LineTermsDetailCounter.intValue() > 1) { %>
                        <td class="tablecolumn"/>
                        <td class="tablecolumn"/>
                        <td class="tablecolumn"/>
                        <td class="numerictablecolumn"/>
                        <td class="numerictablecolumn"/>
                    <%} else {%>
                        <td class="tablecolumn">
                            <yfc:getXMLValue binding="xml:/NegotiationLine/@PrimeLineNo"/>
                        </td>
                        <td class="tablecolumn" nowrap="true">
							<%=displayOrderStatus("N",getValue("NegotiationLine",buildBinding("xml:/NegotiationLine/", secondLineResponse, "LineResponse/@ResponseActionDesc")),true)%>
                        </td>
                        <td class="tablecolumn">
                           <yfc:getXMLValue binding='<%=buildBinding("xml:/Negotiations/Negotiation/@", secondLineOrg, "")%>'/>
                        </td>
                        <td class="numerictablecolumn">
                           <yfc:getXMLValue binding='<%=buildBinding("xml:/NegotiationLine/", secondLineResponse, "LineResponse/@ResponseNo")%>'/>
                        </td>
                        <td class="numerictablecolumn">
                           <yfc:getXMLValue binding='<%=buildBinding("xml:/NegotiationLine/", secondLineResponse, "LineResponse/@ForResponseNo")%>'/>
                        </td>
                    <%}%>

                    <td class="tablecolumn"><yfc:getXMLValue binding="xml:/LineTermsDetail/@ItemID"/></td>
                    <td class="tablecolumn"><yfc:getXMLValue binding="xml:/LineTermsDetail/@ProductClass"/></td>
                    <td class="tablecolumn"><yfc:getXMLValue binding="xml:/LineTermsDetail/@UnitOfMeasure"/></td>

                    <% if (!equals(freightTermsSplit,"NULL")) { %>
                        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/LineTermsDetail/@FreightTerms"/></td>
                    <% } %>

                    <% if (!equals(shipDateSplit,"NULL")) {%>
                        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/LineTermsDetail/@ShipDate"/></td>
                    <% } %>

                    <% if (!equals(deliveryDateSplit,"NULL")) {%>
                        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/LineTermsDetail/@DeliveryDate"/></td>
                    <% } %>

                    <% if (!equals(priceSplit,"NULL")) { %>
                        <td class="numerictablecolumn"><yfc:getXMLValue binding="xml:/LineTermsDetail/@Price"/></td>
                    <% } %>

                    <td class="numerictablecolumn"><yfc:getXMLValue binding="xml:/LineTermsDetail/@Quantity"/></td>
                </tr>
                <%
                    itemId = getValue("LineTermsDetail", "xml:/LineTermsDetail/@ItemID");
                    productClass = getValue("LineTermsDetail", "xml:/LineTermsDetail/@ProductClass");
                    unitOfMeasure = getValue("LineTermsDetail", "xml:/LineTermsDetail/@UnitOfMeasure");
                    freightTerms = getValue("LineTermsDetail", "xml:/LineTermsDetail/@FreightTerms");
                    shipDate = getValue("LineTermsDetail", "xml:/LineTermsDetail/@ShipDate");
                    deliveryDate = getValue("LineTermsDetail", "xml:/LineTermsDetail/@DeliveryDate");
                    price = getValue("LineTermsDetail", "xml:/LineTermsDetail/@Price");
                %>
            </yfc:loopXML>

            <% // Show the new response checkbox if allowed %>

            <% // Create a row with editable inputs so a response can be created if allowed. %>

            <% if ((lineInNegotiation) && (showResponseComboBox)) {%>
            <% if ((isInitiator && canInitiatorRespond.equals("Y")) || (!isInitiator && canNegotiatorRespond.equals("Y"))) { %>
            
            <% /////// Response Action Combo Box Row ///////%>
            <tr class="evenrow">
                <td class="tablecolumn"/>
                <td class="tablecolumn">
                    <select id='<%="LineResponseInput" + NegotiationLineCounter%>' class="combobox" onchange="performNegotiationLineActionSelection(this.value, 'counterofferline', 'LineResponseInput', 'LineTermsDetailInput', '', '<%=NegotiationLineCounter %>');" <%=getComboOptions("xml:/old/Responses/Response/LineResponses/LineResponse_" + NegotiationLineCounter + "/@ResponseAction")%>>
                        <yfc:loopOptions binding="xml:NegotiationLineResponseList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
                    </select>
                </td>
				
				<td class="tablecolumn">
					<input type="hidden" name="ItemIDSplit" value="<%=HTMLEncode.htmlEscape(itemIdSplit)%>" />
					<input type="hidden" name="ProductClassSplit" value="<%=HTMLEncode.htmlEscape(productClassSplit)%>" />
					<input type="hidden" name="UnitOfMeasureSplit" value="<%=HTMLEncode.htmlEscape(unitOfMeasureSplit)%>" />
					<input type="hidden" name="FreightTermsSplit" value="<%=HTMLEncode.htmlEscape(freightTermsSplit)%>" />
					<input type="hidden" name="DeliveryDateSplit" value="<%=HTMLEncode.htmlEscape(deliveryDateSplit)%>" />
					<input type="hidden" name="ShipDateSplit" value="<%=HTMLEncode.htmlEscape(shipDateSplit)%>" />
					<input type="hidden" name="PriceSplit" value="<%=HTMLEncode.htmlEscape(priceSplit)%>" />
				</td>
                <td class="numerictablecolumn"/>
                <td class="numerictablecolumn"/>
                <td class="tablecolumn"/>
                <td class="tablecolumn"/>
				<td class="tablecolumn"/>

				<% if (!equals(freightTermsSplit,"NULL")) { %>
					<td class="tablecolumn"/>
				<% } %>

				<% if (!equals(shipDateSplit,"NULL")) {%>
					<td class="tablecolumn"/>
				<% } %>

				<% if (!equals(deliveryDateSplit,"NULL")) {%>
					<td class="tablecolumn"/>
				<% } %>

				<% if (!equals(priceSplit,"NULL")) { %>
					<td class="numerictablecolumn"/>
				<% } %>

				<td class="numerictablecolumn"/>

            </tr>


            <% /////// Response Row /////// %>

            <tr id='<%="counterofferline" + NegotiationLineCounter%>' style="display:none">

				<% String responseBinding = "xml:/old/Responses/Response/LineResponses/LineResponse_" + NegotiationLineCounter + "/"; %>
				<% String lineTermDtlsBinding = "xml:/old/Responses/Response/LineResponses/LineResponse_" + NegotiationLineCounter + "/LineTerms/LineTermsDetail_1/"; %>

                <% /////// Hidden Fields /////// %>

                <td class="tablecolumn" ShouldCopy="true">
                    <input id='<%="LineResponseInput" + NegotiationLineCounter%>' type="hidden" class="protectedinput" <%=getTextOptions( responseBinding + "@LineEntity", "xml:/NegotiationLine/@LineEntity")%>/>
                    <input id='<%="LineResponseInput" + NegotiationLineCounter%>' type="hidden" class="protectedinput" <%=getTextOptions( responseBinding + "@LineEntityKey", "xml:/NegotiationLine/@LineEntityKey")%>/>
                </td>
                <td class="tablecolumn" ShouldCopy="true">
                    <input id='<%="LineResponseInput" + NegotiationLineCounter%>' type="hidden" class="protectedinput" <%=getTextOptions( responseBinding + "@ForResponseNo", "xml:/NegotiationLine/" + forResponse + "LineResponse/@ResponseNo")%>/>
                </td>
                <td class="tablecolumn" ShouldCopy="true">
                    <input id='<%="LineResponseInput" + NegotiationLineCounter%>' type="hidden" class="protectedinput" <%=getTextOptions( responseBinding + "@ForOptionNo", "", "1")%>/>
                    <input id='<%="LineTermsDetailInput" + NegotiationLineCounter+"_OptionNo"%>' type="hidden" class="protectedinput" NewName="true" <%=getTextOptions( lineTermDtlsBinding + "@OptionNo", "", "1")%>/>
                </td>

				<td class="tablecolumn" ShouldCopy="true"></td>


				<td class="numerictablecolumn" ShouldCopy="true">
                    <% if (showSplitRowIcon) { %>
                        <img class="icon" IconName="addSplitLine" onclick="yfcSplitLine(this);" <%=getImageOptions(request.getContextPath() + "/console/icons/add.gif", "Add_/_Copy_Row")%>/>
                    <% } %>
                </td>

                <% /////// ItemID /////// %>
                <% if (!equals(itemIdSplit,"NULL")) { %>
					<td class="tablecolumn" nowrap="true" ShouldCopy="true">
						<input id='<%="LineTermsDetailInput" + NegotiationLineCounter+"_ItemID"%>' type="text" class="unprotectedinput" 
						NewName="true" <% if (equals(itemIdSplit,"N")) { %> NewClassName="protectedinput" NewContentEditable="false"<%}%>
						<%=getTextOptions(lineTermDtlsBinding + "@ItemID")%>/>
							<% if (equals(itemIdSplit,"Y")) { %>
								<img class="lookupicon" onclick="templateRowCallItemLookup(this,'ItemID','ProductClass','UnitOfMeasure','item','<%=extraParams%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_For_Item_ID") %>/>
							<% } %>
					</td>
                <% } else { %>
                    <td class="tablecolumn" nowrap="true" ShouldCopy="true">
                        <input id='<%="LineTermsDetailInput" + NegotiationLineCounter+"_ItemID"%>' type="hidden" class="protectedinput" NewName="true" <%=getTextOptions(lineTermDtlsBinding + "@ItemID","",itemId)%>/>
                    </td>
                <% } %>

                <% /////// ProductClass /////// %>
                <% if (!equals(productClassSplit,"NULL")) { %>
					<td class="tablecolumn" ShouldCopy="true">
						<select id='<%="LineTermsDetailInput" + NegotiationLineCounter+"_ProductClass"%>' class="combobox" NewName="true" 
						<% if (equals(productClassSplit,"N")) { %> NewDisabled="true"  <%}%>
						<%=getComboOptions(lineTermDtlsBinding + "@ProductClass")%>>
							<yfc:loopOptions binding="xml:ProductClassList:/CommonCodeList/@CommonCode" name="CodeValue" value="CodeValue"/>
						</select>
						<% if (equals(productClassSplit,"N")) { %>
							<input id='<%="LineTermsDetailInput" + NegotiationLineCounter+"_ProductClass"%>' type="hidden" class="protectedinput" NewName="true" <%=getTextOptions(lineTermDtlsBinding + "@ProductClass","",productClass)%>/>	
						<%}%>
					</td>
                <% } else { %>
                    <td class="tablecolumn" ShouldCopy="true">
                        <input id='<%="LineTermsDetailInput" + NegotiationLineCounter+"_ProductClass"%>' type="hidden" class="protectedinput" NewName="true" <%=getTextOptions(lineTermDtlsBinding + "@ProductClass","",productClass)%>/>
                    </td>
                <% } %>

                <% /////// UnitOfMeasure /////// %>
                <% if (!equals(unitOfMeasureSplit,"NULL")) { %>
                    <td class="tablecolumn" ShouldCopy="true">
						<select id='<%="LineTermsDetailInput" + NegotiationLineCounter+"_UnitOfMeasure"%>' class="combobox" NewName="true" <% if (equals(unitOfMeasureSplit,"N")) { %> NewDisabled="true"  <%}%>
							<%=getComboOptions(lineTermDtlsBinding + "@UnitOfMeasure")%>>
							<yfc:loopOptions binding="xml:UnitOfMeasureList:/ItemUOMMasterList/@ItemUOMMaster" name="UnitOfMeasure" value="UnitOfMeasure"/>
						</select>
						<% if (equals(unitOfMeasureSplit,"N")) { %>
							<input id='<%="LineTermsDetailInput" + NegotiationLineCounter+"_UnitOfMeasure"%>' type="hidden" class="protectedinput" NewName="true" <%=getTextOptions(lineTermDtlsBinding + "@UnitOfMeasure","",unitOfMeasure)%>/>	
						<%}%>
                    </td>
                <% } else { %>
                    <td class="tablecolumn" ShouldCopy="true">
                        <input id='<%="LineTermsDetailInput" + NegotiationLineCounter+"_UnitOfMeasure"%>' type="hidden" class="protectedinput" NewName="true" <%=getTextOptions(lineTermDtlsBinding + "@UnitOfMeasure","",unitOfMeasure)%>/>
                    </td>
                <% } %>

                <% /////// FreightTerms /////// %>
                <% if (!equals(freightTermsSplit,"NULL")) { %>
                    <td class="tablecolumn" ShouldCopy="true">
						<select id='<%="LineTermsDetailInput" + NegotiationLineCounter+"_FreightTerms"%>' class="combobox" NewName="true"
						<% if (equals(freightTermsSplit,"N")) { %> NewDisabled="true"  NewContentEditable="false" <%}%>
						<%=getComboOptions(lineTermDtlsBinding + "@FreightTerms")%>>
							<yfc:loopOptions binding="xml:FreightTermsList:/FreightTermsList/@FreightTerms" name="ShortDescription" value="FreightTerms" isLocalized="Y"/>
						</select>
						<% if (equals(freightTermsSplit,"N")) { %>
							<input id='<%="LineTermsDetailInput" + NegotiationLineCounter+"_FreightTerms"%>' type="hidden" class="protectedinput" NewName="true" <%=getTextOptions(lineTermDtlsBinding + "FreightTerms","",freightTerms)%>/>	
						<%}%>
                   </td>
                <% } %>

                <% /////// ShipDate /////// %>
                <% if (!equals(shipDateSplit,"NULL")) { %>
                    <td class="tablecolumn" ShouldCopy="true">
                        <input id='<%="LineTermsDetailInput" + NegotiationLineCounter+"_ShipDate"%>' type="text" class="unprotectedinput" 
						<% if (equals(shipDateSplit,"N")) { %> NewClassName="protectedinput"  NewContentEditable="false" <%}%>
						NewName="true" <%=getTextOptions(lineTermDtlsBinding + "@ShipDate")%>/>
                    </td>
                <% } %>

                <% /////// DeliveryDate /////// %>
                <% if (!equals(deliveryDateSplit,"NULL")) { %>
                    <td class="tablecolumn" ShouldCopy="true">
                        <input id='<%="LineTermsDetailInput" + NegotiationLineCounter+"_DeliveryDate"%>' type="text" class="unprotectedinput" 
						<% if (equals(deliveryDateSplit,"N")) { %> NewClassName="protectedinput"  NewContentEditable="false" <%}%>
						NewName="true" <%=getTextOptions(lineTermDtlsBinding + "@DeliveryDate")%>/>
                    </td>
                <% } %>

                <% /////// Price /////// %>
                <% if (!equals(priceSplit,"NULL")) { %>
                    <td class="numerictablecolumn" ShouldCopy="true">
                        <input id='<%="LineTermsDetailInput" + NegotiationLineCounter+"_Price"%>' type="text" class="numericunprotectedinput" <% if (equals(priceSplit,"N")) { %> NewClassName="protectedinput"  NewContentEditable="false" <%}%>
						NewName="true" <%=getTextOptions(lineTermDtlsBinding + "@Price")%>/>
                    </td>
                <% } %>

                <% /////// Quantity ///////%>
                <td class="numerictablecolumn" nowrap="true" ShouldCopy="true" >
                    <input id='<%="LineTermsDetailInput" + NegotiationLineCounter+"_Quantity"%>' type="text" class="numericunprotectedinput" NewName="true" <%=getTextOptions(lineTermDtlsBinding + "@Quantity")%>/>
                </td>
            </tr>

            <%}}%>
            <% if (lineInNegotiation) { %>
				<tr id='<%="separatorline" + NegotiationLineCounter%>' ><td colspan="13"><hr/></td></tr>
            <% } %>
    </yfc:loopXML>
	</tbody>
</table>
