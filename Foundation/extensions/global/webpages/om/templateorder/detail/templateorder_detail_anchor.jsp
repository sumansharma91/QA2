<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<table cellSpacing="0" class="anchor" cellpadding="7px">
<tr>
    <td>
        <yfc:makeXMLInput name="orderHeaderKey">
            <yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/Order/@OrderHeaderKey"/>
        </yfc:makeXMLInput>
        <input type="hidden" value='<%=getParameter("orderHeaderKey")%>' name="OrderEntityKey"/>
        <input type="hidden" <%=getTextOptions("xml:/Order/@OrderHeaderKey")%>/>
    </td>
</tr>
<tr>
    <td width="100%" colspan="4" >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
            <jsp:param name="ModifyView" value="true"/>
        </jsp:include>
    </td>
</tr>
<tr>
    <td addressip="true" width="67%"  height="100%" rowspan="2">
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
           <jsp:param name="CurrentInnerPanelID" value="I03"/>
           <jsp:param name="allowedBinding" value="xml:/Order/AllowedModifications"/>
           <jsp:param name="getBinding" value="xml:/Order"/>
           <jsp:param name="saveBinding" value="xml:/Order"/>
        </jsp:include>
    </td>
    <td addressip="true" width="33%"  height="100%">
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
           <jsp:param name="CurrentInnerPanelID" value="I02"/>
            <jsp:param name="Path" value="xml:/Order/PersonInfoShipTo"/>
            <jsp:param name="DataXML" value="Order"/>
            <jsp:param name="AllowedModValue" value='<%=getModificationAllowedValueWithPermission("ShipToAddress", "xml:/Order/AllowedModifications")%>'/>
        </jsp:include>
    </td>
</tr>
<tr>
    <td addressip="true"  width="33%"  height="100%">
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I04"/>
            <jsp:param name="Path" value="xml:/Order/PersonInfoBillTo"/>
            <jsp:param name="DataXML" value="Order"/>
            <jsp:param name="AllowedModValue" value='<%=getModificationAllowedValueWithPermission("BillToAddress", "xml:/Order/AllowedModifications")%>'/>
        </jsp:include>
    </td>
</tr>
</table>




