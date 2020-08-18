<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>

<% // TO DO:
   // * Not sure if binding for leftover quantity is correct (it is currently bound to ApplicationAction)
   // * Not sure if binding for availability date is correct (it is currently bound to ApplicationReference)
%>

<table class="view" width="100%">
    <tr>
        <td class="detaillabel">
            <yfc:i18n>Reason_Code</yfc:i18n>
        </td>
        <td>
            <select class="combobox" <%=getComboOptions("xml:/Responses/Response/@ReasonCode")%>>
                <yfc:loopOptions binding="xml:RejectReasonList:/CommonCodeList/@CommonCode" 
                    name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
            </select>
        </td>
        <td class="detaillabel"><yfc:i18n>Leftover_Quantity</yfc:i18n></td>
        <td class="protectedtext">
            <input type="radio" <%=getRadioOptions("xml:/Responses/Response/@ApplicationAction")%>><yfc:i18n>Backorder</yfc:i18n>
            <input type="radio" <%=getRadioOptions("xml:/Responses/Response/@ApplicationAction")%>><yfc:i18n>Cancel</yfc:i18n>
        </td>
        <td class="detaillabel"><yfc:i18n>Availability_Date</yfc:i18n></td>
        <td class="protectedtext">
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Responses/Response/@ApplicationReference")%>/>
        </td>
    </tr>
    <tr>
        <td class="detaillabel"><yfc:i18n>Reason_Text</yfc:i18n></td>
        <td class="protectedtext" colspan="3">
            <textarea class="unprotectedtextareainput" rows="3" cols="100" <%=getTextAreaOptions("xml:/Responses/Response/@Notes")%>></textarea>
        </td>
    </tr>
</table>
