<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<table class="table" cellspacing="0" width="100%">
    <thead>
        <tr>
            <td class="tablecolumnheader" nowrap="true"><yfc:i18n>Source_From_Organization</yfc:i18n></td>
            <td class="tablecolumnheader" nowrap="true"><yfc:i18n>Ship_Node</yfc:i18n></td>
            <td class="tablecolumnheader" nowrap="true"><yfc:i18n>Expected_Ship_Date</yfc:i18n></td>
            <td class="tablecolumnheader" nowrap="true"><yfc:i18n>Expected_Delivery_Date</yfc:i18n></td>
            <td class="tablecolumnheader" nowrap="true"><yfc:i18n>Quantity</yfc:i18n></td>            
        </tr>
    </thead>
    <tbody>
        <yfc:loopXML name="Options" binding="xml:/Options/@Option" id="Option"> 
            <yfc:loopXML name="Option" binding="xml:/Option/OptionLines/@OptionLine" id="OptionLine"> 
                <tr>
                    <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OptionLine/@ItemID"/></td>
                    <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OptionLine/@ProductClass"/></td>
                    <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OptionLine/@UnitOfMeasure"/></td>
                    <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OptionLine/@ItemDesc"/></td>
                    <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OptionLine/@ItemDesc"/></td>
                </tr>
            </yfc:loopXML>
        </yfc:loopXML>
    </tbody>
</table>
