<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script> 

<%
    String driverDate = getValue("Order", "xml:/Order/@DriverDate");
%>

<table class="view" width="100%">
<tr>
    <td>
        <fieldset>
        <legend><yfc:i18n>Identification</yfc:i18n></legend> 
            <table class="view" width="100%">
                <tr>
                    <td class="detaillabel" ><yfc:i18n>Order_Name</yfc:i18n></td>
                    <td>
                        <input type="text" <%=yfsGetTextOptions("xml:/Order/@OrderName","xml:/Order/AllowedModifications")%>/>
                    </td>
                    <td class="detaillabel" ><yfc:i18n>Priority_Code</yfc:i18n></td>
                    <td>
                        <input type="text" <%=yfsGetTextOptions("xml:/Order/@PriorityCode","xml:/Order/AllowedModifications")%>/>
                    </td>
                    <td class="detaillabel" ><yfc:i18n>Currency</yfc:i18n></td>
                    <td>
						<select <%=yfsGetComboOptions("xml:/Order/PriceInfo/@Currency", "xml:/Order/AllowedModifications")%>>
							<yfc:loopOptions binding="xml:/CurrencyList/@Currency" name="CurrencyDescription"
							value="Currency" selected="xml:/Order/PriceInfo/@Currency" isLocalized="Y"/>
						</select>
					</td>
                </tr>
                <tr>
                    <td class="detaillabel" ><yfc:i18n>Division</yfc:i18n></td>
                    <td>
                        <input type="text" <%=yfsGetTextOptions("xml:/Order/@Division","xml:/Order/AllowedModifications")%>/>
                    </td>
                    <td class="detaillabel" ><yfc:i18n>Scheduling_Rule</yfc:i18n></td>
                    <td>
                        <select <%=yfsGetComboOptions("xml:/Order/@AllocationRuleID", "xml:/Order/AllowedModifications")%>>
                            <yfc:loopOptions binding="xml:AllocationRuleList:/AllocationRuleList/@AllocationRule" name="Description"
                            value="AllocationRuleId" selected="xml:/Order/@AllocationRuleID" isLocalized="Y"/>
                        </select>
                    </td>
					<td class="detaillabel" ><yfc:i18n>Customer_PO_#</yfc:i18n></td>
                    <td>
                        <input type="text" <%=yfsGetTextOptions("xml:/Order/@CustomerPONo","xml:/Order/AllowedModifications")%>/>
                    </td>
                </tr>
				<% if (!isVoid(driverDate)){ %>
					<tr>
						<td class="detaillabel" ><yfc:i18n>Requested_Cancel_Date</yfc:i18n></td>
						<td nowrap="true" >
							<input type="text" <%=yfsGetTextOptions("xml:/Order/@ReqCancelDate","xml:/Order/AllowedModifications")%>/>
							<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar","xml:/Order/@ReqCancelDate","xml:/Order/AllowedModifications") %> />
						</td>
						<% // Show ReqShipDate if ReqDeliveryDate is the driver date (determined by the "DriverDate" attribute output by getOrderDetails)
						   // because the Delivery Date field would be shown on the main order detail screen.
						   if (equals(driverDate, "02")) { %>
							<td class="detaillabel"><yfc:i18n>Requested_Ship_Date</yfc:i18n></td>
							<td nowrap="true">
								<input type="text" <%=yfsGetTextOptions("xml:/Order/@ReqShipDate","xml:/Order/AllowedModifications")%>/>
								<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar","xml:/Order/@ReqShipDate","xml:/Order/AllowedModifications") %> />
							</td>
						<% } else { %>
							<td class="detaillabel"><yfc:i18n>Requested_Delivery_Date</yfc:i18n></td>
							<td nowrap="true">
								<input type="text" <%=yfsGetTextOptions("xml:/Order/@ReqDeliveryDate","xml:/Order/AllowedModifications")%>/>
								<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar","xml:/Order/@ReqDeliveryDate","xml:/Order/AllowedModifications") %> />
							</td>
						<% } %>
                        <td class="detaillabel" ><yfc:i18n>Auto_Cancel_Date</yfc:i18n></td>
                        <td class="protectedtext"><%=resolveValue("xml:/Order/@AutoCancelDate")%></td>
					</tr>
                <% } %>
            </table>
        </fieldset>
    </td>
</tr>
<tr>
    <td>
        <fieldset>
        <legend><yfc:i18n>Shipping</yfc:i18n></legend>
            <table class="view" width="100%">
                <tr>
                    <td class="detaillabel" ><yfc:i18n>Freight_Terms</yfc:i18n></td>
                    <td>
						<select <%=yfsGetComboOptions("xml:/Order/@FreightTerms", "xml:/Order/@FreightTerms", "xml:/Order/AllowedModifications")%> >
			                <yfc:loopOptions binding="xml:FreightTermsList:/FreightTermsList/@FreightTerms" name="ShortDescription"
						    value="FreightTerms" selected="xml:/Order/@FreightTerms" isLocalized="Y"/>
				        </select>
                    </td>
                    <td class="detaillabel" ><yfc:i18n>Charge_Actual_Freight</yfc:i18n></td>
                    <td>
                        <input class="checkbox" type="checkbox" <%=yfsGetCheckBoxOptions("xml:/Order/@ChargeActualFreightFlag","xml:/Order/@ChargeActualFreightFlag","Y","xml:/Order/AllowedModifications")%>/>
                    </td>
                    <td class="detaillabel" ><yfc:i18n>Shipping_Paid_By</yfc:i18n></td>
                    <td>
                        <select <%=yfsGetComboOptions("xml:/Order/@DeliveryCode", "xml:/Order/AllowedModifications")%> >
                            <yfc:loopOptions binding="xml:DeliveryCodeList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
                            value="CodeValue" selected="xml:/Order/@DeliveryCode" isLocalized="Y"/>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td class="detaillabel" ><yfc:i18n>Carrier_Account_#</yfc:i18n></td>
                    <td>
                        <input type="text" <%=yfsGetTextOptions("xml:/Order/@CarrierAccountNo","xml:/Order/AllowedModifications")%>/>
                    </td>
                    <td class="detaillabel" ><yfc:i18n>Ship_Node</yfc:i18n></td>
                    <td>
                        <input type="text" <%=yfsGetTextOptions("xml:/Order/@ShipNode","xml:/Order/AllowedModifications")%>/>
						<img class="lookupicon" onclick="callLookup(this,'shipnode')" <%=yfsGetImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Ship_Node", "xml:/Order/@ShipNode", "xml:/Order/AllowedModifications")%>/>
                    </td>
                    <td class="detaillabel"><yfc:i18n>Receiving_Node</yfc:i18n></td>
                    <td>
                        <input type="text" <%=yfsGetTextOptions("xml:/Order/@ReceivingNode","xml:/Order/AllowedModifications")%>/>
						<img class="lookupicon" onclick="callLookup(this,'shipnode')" <%=yfsGetImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Receiving_Node", "xml:/Order/@ReceivingNode", "xml:/Order/AllowedModifications")%>/>
                    </td>
                </tr>
                <tr>
                    <td class="detaillabel"><yfc:i18n>Ship_To_ID</yfc:i18n></td>
                    <td class="protectedtext"><yfc:getXMLValue name="Order" binding="xml:/Order/@ShipToID"/></td>
                    <td class="detaillabel"><yfc:i18n>Bill_To_ID</yfc:i18n></td>
                    <td class="protectedtext"><yfc:getXMLValue name="Order" binding="xml:/Order/@BillToID"/></td>
					<td colspan="2">&nbsp;</td>
                </tr>
            </table>
        </fieldset>
    </td>
</tr>
<tr>
    <td>
        <fieldset>
        <legend><yfc:i18n>Financials</yfc:i18n></legend>
            <table class="view" width="100%">
                <tr>
                    <td  class="detaillabel" ><yfc:i18n>Terms_Code</yfc:i18n></td>
                    <td>
                        <select <%=yfsGetComboOptions("xml:/Order/@TermsCode", "xml:/Order/AllowedModifications")%> >
                            <yfc:loopOptions binding="xml:TermsCodeList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
                            value="CodeValue" selected="xml:/Order/@TermsCode" isLocalized="Y"/>
                        </select>
                    </td>
                    <td class="detaillabel" ><yfc:i18n>Price_Program</yfc:i18n></td>
                    <td>
                        <select <%=yfsGetComboOptions("xml:/Order/@PriceProgramKey", "xml:/Order/AllowedModifications")%>>
                            <yfc:loopOptions binding="xml:/PriceProgramList/@PriceProgram" name="PriceProgramDescription"
                            value="PriceProgramKey" selected="xml:/Order/@PriceProgramKey"/>
                        </select>
                    </td>
                    <td class="detaillabel" ><yfc:i18n>Taxpayer_ID</yfc:i18n></td>
                    <td>
                        <input type="text" <%=yfsGetTextOptions("xml:/Order/@TaxPayerId","xml:/Order/AllowedModifications")%>/>
                    </td>
                </tr>
                <tr>
                    <td class="detaillabel" ><yfc:i18n>Tax_Exempt</yfc:i18n></td>
                    <td>
                        <input class="checkbox" type="checkbox" <%=yfsGetCheckBoxOptions("xml:/Order/@TaxExemptFlag","xml:/Order/@TaxExemptFlag","Y","xml:/Order/AllowedModifications")%>/>
                    </td>
                    <td class="detaillabel" ><yfc:i18n>Tax_Exemption_Certificate</yfc:i18n></td>
                    <td>
                        <input type="text" <%=yfsGetTextOptions("xml:/Order/@TaxExemptionCertificate","xml:/Order/AllowedModifications")%>/>
                    </td>
                    <td class="detaillabel" ><yfc:i18n>Tax_Jurisdiction</yfc:i18n></td>
                    <td>
                        <input type="text" <%=yfsGetTextOptions("xml:/Order/@TaxJurisdiction","xml:/Order/AllowedModifications")%>/>
                    </td>
                </tr>
            </table>
        </fieldset>
    </td>
</tr>
<tr>
    <td>
        <fieldset>
        <legend><yfc:i18n>Other_Attributes</yfc:i18n></legend> 
            <table class="view" width="100%">
                <tr>                    
                    <td  class="detaillabel" ><yfc:i18n>Entered_By</yfc:i18n></td>
                    <td class="protectedtext"><yfc:getXMLValue name="Order" binding="xml:/Order/@EnteredBy"/></td>
                    <td class="detaillabel" ><yfc:i18n>Source</yfc:i18n></td>
                    <td class="protectedtext">
                        <select <%=yfsGetComboOptions("xml:/Order/@EntryType", "xml:/Order/AllowedModifications")%>>
                            <yfc:loopOptions binding="xml:SourceList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
                            value="CodeValue" selected="xml:/Order/@EntryType" isLocalized="Y"/>
                        </select>
                    </td>
                    <td  class="detaillabel" ><yfc:i18n>Vendor_ID</yfc:i18n></td>
                    <td>
                        <input type="text" <%=yfsGetTextOptions("xml:/Order/@VendorID","xml:/Order/AllowedModifications")%>/>
                    </td>
                </tr>
                <tr>
                    <td  class="detaillabel" ><yfc:i18n>Notification_Type</yfc:i18n></td>
                    <td>
                        <input type="text" <%=yfsGetTextOptions("xml:/Order/@NotificationType","xml:/Order/AllowedModifications")%>/>
                    </td>
                    <td class="detaillabel" ><yfc:i18n>Notification_Reference</yfc:i18n></td>
                    <td>
                        <input type="text" <%=yfsGetTextOptions("xml:/Order/@NotificationReference","xml:/Order/AllowedModifications")%>/>
                    </td>
                    <td/>
                </tr>
            </table>
        </fieldset>
    </td>
</tr>
</table>
