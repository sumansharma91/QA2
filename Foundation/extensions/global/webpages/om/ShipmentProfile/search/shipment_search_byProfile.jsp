<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file = "/yfsjspcommon/yfsutil.jspf"%>
<%@ page import = "com.yantra.yfs.ui.backend.*"%>

<script language = "javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js">
</script>

<script language = "javascript" src="<%=request.getContextPath()%>/console/scripts/om.js">
</script>

<script language = "javascript" src="<%=request.getContextPath()%>/console/scripts/dm.js">
</script>

<table class = "view">
    <jsp:include page = "/yfsjspcommon/common_fields.jsp" flush = "true">
        <jsp:param name = "ShowDocumentType" value = "false" />

        <jsp:param name = "EnterpriseCodeBinding" value = "xml:/Shipment/@EnterpriseCode" />

        <jsp:param name = "RefreshOnEnterpriseCode" value = "true" />

        <jsp:param name = "ScreenType" value = "search" />

        <jsp:param name = "ShowNode" value = "true" />

        <jsp:param name = "NodeBinding" value = "xml:/Shipment/@Node" />
    </jsp:include>

    <tr>
        <td class = "searchlabel">
            <yfc:i18n>
                Profile_ID
            </yfc:i18n>
        </td>
    </tr>
<% request.setAttribute(YFCUIBackendConsts.YFC_SHOW_MAX_RECORDS,"N");%>
    <tr>
        <td nowrap = "true" class = "searchcriteriacell">
            <select name = "xml:/Shipment/@ProfileIdQryType" class = "combobox">
                <yfc:loopOptions binding = "xml:/QueryTypeList/StringQueryTypes/@QueryType"
                    name = "QueryTypeDesc" value = "QueryType" selected = "xml:/Shipment/@ProfileIdQryType" />
            </select>

            <input type = "text" class = "unprotectedinput" <%=getTextOptions("xml:/Shipment/@ProfileId")%>/>
        </td>
    </tr>

    <tr>
        <td>
            <fieldset>
                <table class = "view" height = "100%" width = "100%">
                    <tr>
                        <td class = "searchlabel">
                            <yfc:i18n>
                                Consider_Profiles
                            </yfc:i18n>
                        </td>
                    </tr>

                    <tr>
                        <td class = "searchcriteriacell">
                            <input type = "radio"
                                <%= getRadioOptions("xml:/Shipment/@ConsiderShipmentsIncludedInwave", "xml:/Shipment/@ConsiderShipmentsIncludedInwave", "Y") %>>
                            <yfc:i18n>
                                Already_In_Wave
                            </yfc:i18n>

                            </input>
                        </td>
                    </tr>

                    <tr>
                        <td class = "searchcriteriacell">
                            <input type = "radio"
                                <%= getRadioOptions("xml:/Shipment/@ConsiderShipmentsIncludedInwave", "xml:/Shipment/@ConsiderShipmentsIncludedInwave", "N") %>>
                            <yfc:i18n>
                                Not_In_Wave
                            </yfc:i18n>

                            </input>
                        </td>
                    </tr>

                    <tr>
                        <td class = "searchcriteriacell">
                            <input type = "radio" name = "xml:/Shipment/@ConsiderShipmentsIncludedInwave" value = "A"<% 
							if (YFCCommon.isVoid(resolveValue("xml:/Shipment/@ConsiderShipmentsIncludedInwave"))) {%> CHECKED <%}%>>
                            <yfc:i18n>
                                All
                            </yfc:i18n>

                            </input>
                        </td>
                    </tr>
                </table>
            </fieldset>
        </td>
    </tr>

    <tr>
        <tr>
            <td>
                <yfc:i18n>
                    Show_Profiles
                </yfc:i18n>
            </td>
        </tr>

        <tr>
            <td class = "searchcriteriacell">
                <input type = "radio"<%= getRadioOptions("xml:/Shipment/@OrderBy", "xml:/Shipment/@OrderBy", "H")  %> CHECKED>
                <yfc:i18n>
                    More_No_Of_Shipments_First
                </yfc:i18n>

                </input>
            </td>
        </tr>

        <tr>
            <td class = "searchcriteriacell">
                <input type = "radio"<%= getRadioOptions("xml:/Shipment/@OrderBy", "xml:/Shipment/@OrderBy", "L") %>>
                <yfc:i18n>
                    Less_No_Of_Shipments_First
                </yfc:i18n>

                </input>
            </td>
        </tr>
</table>
