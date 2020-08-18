<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<table class="table" width="100%" editable="false">
<thead>
    <tr> 
		<td sortable="no" class="checkboxheader">
			<input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
		</td>
        <td class="tablecolumnheader">
            <yfc:i18n>Last_Name</yfc:i18n>
        </td>
        <td class="tablecolumnheader">
            <yfc:i18n>First_Name</yfc:i18n>
        </td>
        <td class="tablecolumnheader">
            <yfc:i18n>E-Mail</yfc:i18n>
        </td>
        <td class="tablecolumnheader">
            <yfc:i18n>Address_Line_1</yfc:i18n>
        </td>
        <td class="tablecolumnheader">
            <yfc:i18n>Address_Line_2</yfc:i18n>
        </td>
		<td class="tablecolumnheader">
            <yfc:i18n>City</yfc:i18n>
        </td>
        <td class="tablecolumnheader">
            <yfc:i18n>State</yfc:i18n>
        </td>
        <td class="tablecolumnheader">
            <yfc:i18n>Postal_Code</yfc:i18n>
        </td>
        <td class="tablecolumnheader">
            <yfc:i18n>Country</yfc:i18n>
        </td>
   </tr>
</thead>
    <tbody>
        <yfc:loopXML binding="xml:/PersonInfoList/@PersonInfo" id="PersonInfo">
            <tr>
                <yfc:makeXMLInput name="personInfoKey">
                    <yfc:makeXMLKey binding="xml:/PersonInfo/@PersonInfoKey" value="xml:/PersonInfo/@PersonInfoKey" />
                </yfc:makeXMLInput>                
                <td class="checkboxcolumn">                     
                    <input type="checkbox" value='<%=getParameter("personInfoKey")%>' name="EntityKey"/>
                </td>
                <td class="tablecolumn">
						<yfc:getXMLValue binding="xml:/PersonInfo/@LastName"/>
                </td>
                <td class="tablecolumn">
						<yfc:getXMLValue binding="xml:/PersonInfo/@FirstName"/>
                </td>
                <td class="tablecolumn">
						<yfc:getXMLValue binding="xml:/PersonInfo/@EMailID"/>
                </td>
                <td class="tablecolumn">
						<yfc:getXMLValue binding="xml:/PersonInfo/@AddressLine1"/>
                </td>
                <td class="tablecolumn">
						<yfc:getXMLValue binding="xml:/PersonInfo/@AddressLine2"/>
                </td>
                <td class="tablecolumn">
						<yfc:getXMLValue binding="xml:/PersonInfo/@City"/>
                </td>
                <td class="tablecolumn">
						<yfc:getXMLValue binding="xml:/PersonInfo/@State"/>
                </td>
                <td class="tablecolumn">
						<yfc:getXMLValue binding="xml:/PersonInfo/@ZipCode"/>
                </td>
                <td class="tablecolumn">
						<yfc:getXMLValue binding="xml:/PersonInfo/@Country"/>
                </td>
			</tr>
		</yfc:loopXML>
</tbody>
</table>
