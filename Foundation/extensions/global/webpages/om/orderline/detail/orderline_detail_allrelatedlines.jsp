<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@include file="/console/jsp/order.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.util.*" %>

<%
	String sDocumentType = getValue("DocumentType", "xml:/DocumentType/@DocumentType");
	 String sOrderNoColumnHeader = getI18N(sDocumentType+"_Order_#") ;
	 if(YFCUtils.equals(sOrderNoColumnHeader,sDocumentType+"_Order_#"))
	 {
		 //no entry exists in ycpbundle.properties.use default value
		sOrderNoColumnHeader = getI18N("0001_Order_#") ;
	 }
     boolean IsReturnService = false;
     if(equals("true", getParameter("IsReturnService")) || equals("Y", getParameter("IsReturnService") ) )      {           
         IsReturnService = true;
     }
%>

<%  if(IsReturnService)         {           %>
    <yfc:callAPI apiID="AP5"/>
<%  }   %>

<table class="table" width="100%">
    <thead>
        <tr>
            <td class="checkboxheader" sortable="no">
                <input type="checkbox" value="checkbox" name="checkbox" onclick="doCheckAll(this);"/>
            </td>
            <td class="tablecolumnheader"><%=sOrderNoColumnHeader%></td>
            <td class="tablecolumnheader"><yfc:i18n>Line_#</yfc:i18n></td>            
            <td class="tablecolumnheader"><yfc:i18n>Date</yfc:i18n></td>            
            <td class="tablecolumnheader"><yfc:i18n>Relationship</yfc:i18n></td>
            <%  if(IsReturnService)         {           %>
                <td class="tablecolumnheader"><yfc:i18n>Reason_Code</yfc:i18n></td>
            <%  }   %>
            <td class="tablecolumnheader"><yfc:i18n>Quantity</yfc:i18n></td>
            <%  if(IsReturnService)         {           %>
                <td class="tablecolumnheader"><yfc:i18n>Amount</yfc:i18n></td>
            <%  }   %>
            <td class="tablecolumnheader"><yfc:i18n>Status</yfc:i18n></td>
        </tr>
    </thead>
    <tbody>
        <yfc:loopXML binding="xml:/DocumentType/@OrderLine" id="OrderLine">
        <tr>
            <yfc:makeXMLInput name="lineKey">
                <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderLine/@OrderLineKey" />
				<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderHeaderKey" value="xml:/OrderLine/Order/@OrderHeaderKey" />
            </yfc:makeXMLInput>
            <yfc:makeXMLInput name="orderKey">
                <yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/OrderLine/Order/@OrderHeaderKey" />
            </yfc:makeXMLInput>
            <yfc:makeXMLInput name="statusKey">
                <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderLine/@OrderLineKey" />
            </yfc:makeXMLInput>
            <td class="checkboxcolumn">
                <input type="checkbox" value='<%=getParameter("lineKey")%>' name="chkRelatedKey" yfsTargetEntity="<%=getEntityIDForLink("L02", getValue("OrderLine", "xml:/OrderLine/Order/@DocumentType"))%>"
				<% if( (!showOrderNo("OrderLine","Order")) || (!showOrderLineNo("OrderLine","Order")) || (isVoid(getEntityIDForLink("L02", getValue("OrderLine", "xml:/OrderLine/Order/@DocumentType")))) ) {%> disabled="true" <%}%>
				/>
            </td>
            <td class="tablecolumn">
				<% if(showOrderNo("OrderLine","Order")) {%>	
		            <a <%=getDetailHrefOptions("L01", getValue("OrderLine", "xml:/OrderLine/Order/@DocumentType"), getParameter("orderKey"), "")%>>
						<yfc:getXMLValue binding="xml:/OrderLine/Order/@OrderNo"/></a>
				<%} else {%>
					<yfc:getXMLValue binding="xml:/OrderLine/Order/@OrderNo"/>
				<%}%>
            </td>
            <td class="tablecolumn" sortValue="<%=getNumericValue("xml:/OrderLine/@PrimeLineNo")%>">
				<% if(showOrderLineNo("OrderLine","Order")) {%>
	                <a <%=getDetailHrefOptions("L02", getValue("OrderLine", "xml:/OrderLine/Order/@DocumentType"), getParameter("lineKey"), "")%>>
						<yfc:getXMLValue binding="xml:/OrderLine/@PrimeLineNo"/></a>
				<%} else {%>
					<yfc:getXMLValue binding="xml:/OrderLine/@PrimeLineNo"/>
				<%}%>
            </td>
            <td class="tablecolumn">
                <yfc:i18n><yfc:getXMLValue binding="xml:/OrderLine/@Createts"/></yfc:i18n>
            </td>
            <td class="tablecolumn">
                <yfc:i18n><yfc:getXMLValue binding="xml:/OrderLine/@Relationship"/></yfc:i18n>
            </td>
            <%  if(IsReturnService)         {           %>
                <td class="tablecolumn" nowrap="true" >
                    <%=getComboText("xml:ReasonCodeList:/CommonCodeList/@CommonCode", "CodeShortDescription", "CodeValue", "xml:/OrderLine/@ReturnReason",true)%>
                </td>
            <%  }   %>

            <td class="tablecolumn">
                <yfc:getXMLValue binding="xml:/OrderLine/@OrderedQty"/>
            </td>
            
            <%  if(IsReturnService)         {           %>
                <td class="numerictablecolumn" nowrap="true" sortValue="<%=getNumericValue("xml:OrderLine:/OrderLine/LineOverallTotals/@LineTotal")%>">
                    <% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%>&nbsp;<yfc:getXMLValue binding="xml:/OrderLine/LineOverallTotals/@LineTotal"/>&nbsp;<%=curr0[1]%>
                </td>
            <%  }   %>

            <td class="tablecolumn">
                <a <%=getDetailHrefOptions("L03", getValue("OrderLine", "xml:/OrderLine/Order/@DocumentType"), getParameter("statusKey"),"ShowReleaseNo=Y")%>>
                <%=displayOrderStatus(getValue("OrderLine","xml:/OrderLine/@MultipleStatusesExist"),getValue("OrderLine","xml:/OrderLine/@MaxLineStatusDesc"),true)%></a>
            </td>
        </tr>
        </yfc:loopXML>
   </tbody>
</table>
