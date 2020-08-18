<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/currencyutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script> 
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<table class="table" editable="false" width="100%" cellspacing="0">
    <thead> 
        <tr>
            <td sortable="no" class="checkboxheader">
                <input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>'/>
                <input type="hidden" name="xml:/WorkOrder/@Override" value="N"/>
                <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
            </td>
            <td class="tablecolumnheader"><yfc:i18n>Work_Order_#</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Ship_Node</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Service_Item_Group_ID</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Priority</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Segment_Type</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Segment_#</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Status</yfc:i18n></td>
        </tr>
    </thead>
    <tbody>
        <yfc:loopXML binding="xml:/WorkOrders/@WorkOrder" id="WorkOrder">
            <% if ((!equals("DS", resolveValue("xml:/WorkOrder/@ServiceItemGroupCode"))) && (!equals("PS", resolveValue("xml:/WorkOrder/@ServiceItemGroupCode")))) {
            %>
                <tr>
                    <yfc:makeXMLInput name="WorkOrderKey">
                        <yfc:makeXMLKey binding="xml:/WorkOrder/@WorkOrderKey" value="xml:/WorkOrder/@WorkOrderKey" />
                        <yfc:makeXMLKey binding="xml:/WorkOrder/@NodeKey" value="xml:/WorkOrder/@NodeKey" />
                        <yfc:makeXMLKey binding="xml:/WorkOrder/@EnterpriseCode" value="xml:/WorkOrder/@EnterpriseCode" />
                        <yfc:makeXMLKey binding="xml:/WorkOrder/@WorkOrderNo" value="xml:/WorkOrder/@WorkOrderNo" />
                    </yfc:makeXMLInput>                
                    <td class="checkboxcolumn">                     
                        <input type="checkbox" value='<%=getParameter("WorkOrderKey")%>' name="workOrderEntityKey" />
                    </td>
                    <td class="tablecolumn">
                        <% if(equals("Y", getValue("WorkOrder","xml:/WorkOrder/@isHistory") )){ %>
                            <yfc:getXMLValue binding="xml:/WorkOrder/@WorkOrderNo"/>
                        <%}else {%>
                            <a <%=getDetailHrefOptions("L01",getParameter("WorkOrderKey"),"")%>>
                                <yfc:getXMLValue binding="xml:/WorkOrder/@WorkOrderNo"/>
                            </a>
                        <%} %>                    
                    </td>
                    <td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrder/@NodeKey"/></td>
                    <td class="tablecolumn">
                        <%=getComboText("xml:ServiceItemGroupList:/CommonCodeList/@CommonCode", "CodeShortDescription", "CodeValue", "xml:/WorkOrder/@ServiceItemGroupCode", true)%>
                    </td>
                    <td class="tablecolumn">
                        <%=getComboText("xml:WorkOrderPriorityList:/CommonCodeList/@CommonCode", "CodeShortDescription", "CodeValue", "xml:/WorkOrder/@Priority",true)%>
                    </td>
                    <% 
                      if(!isVoid(resolveValue("xml:/WorkOrder/@SegmentType"))){	
                       YFCElement commCodeElem = YFCDocument.createDocument("CommonCode").getDocumentElement();
                       commCodeElem.setAttribute("CodeType","SEGMENT_TYPE");
                       commCodeElem.setAttribute("CodeValue",resolveValue("xml:/WorkOrder/@SegmentType"));
                       YFCElement templateElem = YFCDocument.parse("<CommonCode CodeName=\"\" CodeShortDescription=\"\" CodeType=\"\" CodeValue=\"\" CommonCodeKey=\"\" />").getDocumentElement();
                    %>
                        <yfc:callAPI apiName="getCommonCodeList" inputElement="<%=commCodeElem%>" 
                                                                    templateElement="<%=templateElem%>" outputNamespace=""/>
                        <td class="tablecolumn">
                            <yfc:getXMLValueI18NDB binding="xml:/CommonCodeList/CommonCode/@CodeShortDescription"/>
                        </td>
                    <% } else { %>
                        <td class="tablecolumn">&nbsp;</td>
                    <% } %>
                    <td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrder/@Segment"/></td>
                    <td class="tablecolumn"><%=getDBString(getValue("WorkOrder","xml:/WorkOrder/@StatusDescription"))%></td>
                </tr>
            <% } %>
        </yfc:loopXML>
   </tbody>
</table>
