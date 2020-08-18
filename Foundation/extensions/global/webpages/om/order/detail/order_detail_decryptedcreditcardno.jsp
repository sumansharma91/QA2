<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<table class="view" width="100%">
    <tr>
        <td class="detaillabel" ><yfc:i18n>Credit_Card_#</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding='xml:/GetDecryptedCreditCardNumber/@DecryptedCCNo'/>&nbsp;</td
    </tr>
</table>
