<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<%
	String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("Order", "xml:/Order/@EnterpriseCode"));
	String driverDate = getValue("Order", "xml:/Order/@DriverDate");

%>

<table class="view" width="100%">
    <tr>
        <td>
            <input type="hidden" name="xml:/Order/@ModificationReasonCode"/>
            <input type="hidden" name="xml:/Order/@ModificationReasonText"/>
            <input type="hidden" name="xml:/Order/@Override" value="N"/>
            <input type="hidden" name="hiddenDraftOrderFlag" value='<%=getValue("OrderLine", "xml:/Order/@DraftOrderFlag")%>'/>

            <input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Order/@OrderHeaderKey","xml:/Order/@OrderHeaderKey")%>/>
            <input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Order/OrderLines/OrderLine/@KitCode","LK")%>/>
        </td>
    </tr>
    <tr>
        <yfc:makeXMLInput name="orderKey">
            <yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/Order/@OrderHeaderKey" ></yfc:makeXMLKey>
        </yfc:makeXMLInput>
        <td class="detaillabel" >
            <yfc:i18n>Order_#</yfc:i18n>
        </td>
        <td class="protectedtext">
			<% if(showOrderNo("Order","Order")) {%>
	            <a <%=getDetailHrefOptions("L01",getParameter("orderKey"),"")%>>
					<yfc:getXMLValue binding="xml:/Order/@OrderNo"></yfc:getXMLValue>
				</a>
			<%} else {%>
				<yfc:getXMLValue binding="xml:/Order/@OrderNo"></yfc:getXMLValue>
			<%}%>
		    <input type="hidden" class="protectedinput" <%=getTextOptions("xml:/Order/@OrderNo","xml:/Order/@OrderNo")%> />
        </td>
        <td colspan="4">&nbsp;</td>
    </tr>
    <tr>
        <td class="detaillabel" >
            <yfc:i18n>Item_ID</yfc:i18n>
        </td>
        <td nowrap="true" >
            <input class="unprotectedinput" type="text" <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine/Item/@ItemID","xml:/Order/AllowedModifications","ADD_LINE","text" )%> />
           <img class="lookupicon" onclick="callItemLookup('xml:/Order/OrderLines/OrderLine/Item/@ItemID','xml:/Order/OrderLines/OrderLine/Item/@ProductClass','xml:/Order/OrderLines/OrderLine/OrderLineTranQuantity/@TransactionalUOM','item','<%=extraParams%>')" name="search" <%=yfsGetImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item","xml:/KitLine/@AddLine","xml:/Order/AllowedModifications") %> />
        </td>
        <td class="detaillabel" >
            <yfc:i18n>Unit_Of_Measure</yfc:i18n>
        </td>
        <td >
            <select <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine/OrderLineTranQuantity/@TransactionalUOM","xml:/Order/AllowedModifications","ADD_LINE","combo" )%>>
                <yfc:loopOptions binding="xml:UOMList:/ItemUOMMasterList/@ItemUOMMaster" name="UnitOfMeasure"
                value="UnitOfMeasure" selected="xml:/Order/OrderLines/OrderLine/OrderLineTranQuantity/@TransactionalUOM"/>
            </select>
        </td>
        <td class="detaillabel" >
            <yfc:i18n>Product_Class</yfc:i18n>
        </td>
        <td>
            <select <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine/Item/@ProductClass","xml:/Order/AllowedModifications","ADD_LINE","combo" )%>>
                <yfc:loopOptions binding="xml:ProductClassList:/CommonCodeList/@CommonCode" name="CodeValue"
                value="CodeValue" selected="xml:/Order/OrderLines/OrderLine/Item/@ProductClass"/>
            </select>
        </td>
    </tr>
    <tr>
        <td  class="detaillabel" >
            <yfc:i18n>Line_Quantity</yfc:i18n>
        </td>
        <td>
            <input class="numericunprotectedinput" type="text" <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine/OrderLineTranQuantity/@OrderedQty","xml:/Order/AllowedModifications","ADD_LINE","text" )%>/>
        </td>
        <td class="detaillabel" >
            <yfc:i18n>Description</yfc:i18n>
        </td>
        <td colspan="3">
            <input class="unprotectedinput" type="text" 
<%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine/Item/@ItemDesc","xml:/Order/AllowedModifications","ADD_LINE","text")%>/>
        </td>
    </tr>
    <tr>
        <td  class="detaillabel" >
            <yfc:i18n>Receiving_Node</yfc:i18n>
        </td>
        <td nowrap="true" >
            <input type="text" class="unprotectedinput" 
<%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine/@ReceivingNode","xml:/Order/AllowedModifications","ADD_LINE","text")%>/>
            <img class="lookupicon" onclick="callLookup(this,'shipnode')" name="search" <%=yfsGetImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Receiving_Node","xml:/KitLine/@AddLine","xml:/Order/AllowedModifications") %>/>
        </td>
        <td class="detaillabel"  >
            <yfc:i18n>Ship_Node</yfc:i18n>
        </td>
        <td nowrap="true" >
            <input type="text" class="unprotectedinput" <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine/@ShipNode","xml:/Order/AllowedModifications","ADD_LINE","text")%>/>
            <img class="lookupicon" onclick="callLookup(this,'shipnode')" name="search" <%=yfsGetImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Ship_Node","xml:/KitLine/@AddLine","xml:/Order/AllowedModifications") %>/>
        </td>
        <% if (equals(driverDate, "02")) { %>
            <td class="detaillabel" >
                <yfc:i18n>Requested_Delivery_Date</yfc:i18n>
            </td>
            <td nowrap="true" >
                <input type="text" class="unprotectedinput" <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine/@ReqDeliveryDate","xml:/Order/AllowedModifications","ADD_LINE","text")%>/>
                <img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar","xml:/KitLine/@AddLine","xml:/Order/AllowedModifications") %>/>
            </td>
        <% } else { %>
            <td class="detaillabel" >
                <yfc:i18n>Requested_Ship_Date</yfc:i18n>
            </td>
            <td nowrap="true" >
                <input type="text" class="unprotectedinput" <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine/@ReqShipDate","xml:/Order/AllowedModifications","ADD_LINE","text")%>/>
                <img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar","xml:/KitLine/@AddLine","xml:/Order/AllowedModifications") %>/>
            </td>
        <% } %>
    </tr>
</table>
