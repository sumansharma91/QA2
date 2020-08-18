<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<%
   String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("Order", "xml:/Order/@EnterpriseCode"));
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
            <yfc:makeXMLKey binding="xml:/Order/@OrderNo" value="xml:/Order/@OrderNo" ></yfc:makeXMLKey>
            <yfc:makeXMLKey binding="xml:/Order/@EnterpriseCode" value="xml:/Order/@EnterpriseCode" ></yfc:makeXMLKey>
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
            <input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Order/OrderLines/OrderLine/Item/@ItemID")%>/>
            <img class="lookupicon" onclick="callItemLookup('xml:/Order/OrderLines/OrderLine/Item/@ItemID','xml:/Order/OrderLines/OrderLine/Item/@ProductClass','xml:/Order/OrderLines/OrderLine/Item/@UnitOfMeasure','item','<%=extraParams%>')" name="search" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_For_Item_ID") %> />
        </td>
        <td class="detaillabel" >
            <yfc:i18n>Unit_Of_Measure</yfc:i18n>
        </td>
        <td >
            <select name="xml:/Order/OrderLines/OrderLine/Item/@UnitOfMeasure" class="combobox">
                <yfc:loopOptions binding="xml:UOMList:/CommonCodeList/@CommonCode" name="CodeValue"
                value="CodeValue" selected="xml:/Order/OrderLines/OrderLine/Item/@UnitOfMeasure"/>
            </select>
        </td>
        <td class="detaillabel" >
            <yfc:i18n>Product_Class</yfc:i18n>
        </td>
        <td>
            <select name="xml:/Order/OrderLines/OrderLine/Item/@ProductClass" class="combobox">
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
            <input class="numericunprotectedinput" type="text" <%=getTextOptions("xml:/Order/OrderLines/OrderLine/@OrderedQty")%>/>
        </td>
        <td class="detaillabel" >
            <yfc:i18n>Description</yfc:i18n>
        </td>
        <td colspan="3">
            <input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Order/OrderLines/OrderLine/Item/@ItemDesc")%>/>
        </td>
    </tr>
    <tr>
        <td class="detaillabel"  >
            <yfc:i18n>Ship_Node</yfc:i18n>
        </td>
        <td nowrap="true" >
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/OrderLines/OrderLine/@ShipNode")%> />
            <img class="lookupicon" onclick="callLookup(this,'shipnode')" name="search" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Ship_Node") %> />
        </td>
    </tr>
</table>
