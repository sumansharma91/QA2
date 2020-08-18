<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/inventory.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript">
yfcDoNotPromptForChanges(true); 
</script>
<%

int tagtrack=0;
String root = request.getParameter("TotalBinding");
%>
<table class="table" >
<tbody>
    <tr>
		<td width="10%" >
			&nbsp;
		</td>
<td width="80%" style="border:1px solid black">
<table class="table" editable="true" width="100%" cellspacing="0">
<%
	String tagContainer  = request.getParameter("TagContainer");
	if (isVoid(tagContainer)) {
		tagContainer = "TagContainer";
	}
	
	String tagElement  = request.getParameter("TagElement");
	if (isVoid(tagElement)) {
		tagElement = "Tag";
	}

	Map identifierAttrMap=null;
	Map descriptorAttrMap=null;
	Map extnIdentifierAttrMap=null;
	Map extnDescriptorAttrMap=null;

	%>  
	 <yfc:hasXMLNode binding="xml:ItemDetails:/Item/InventoryTagAttributes">
	<%
		prepareTagDetails ((YFCElement) request.getAttribute(tagContainer),tagElement,(YFCElement) request.getAttribute("ItemDetails"));
	
		identifierAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ItemDetails"),"IdentifierAttributes");
		descriptorAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ItemDetails"),"DescriptorAttributes");
		extnIdentifierAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ItemDetails"),"ExtnIdentifierAttributes");
		extnDescriptorAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ItemDetails"),"ExtnDescriptorAttributes");
	%>
	 </yfc:hasXMLNode>
	<%

	String modifiable = request.getParameter("Modifiable");
	boolean isModifiable = false;
	if (equals(modifiable,"true")) {
		isModifiable = true;
	}%>
    <thead> 
   <tr>
   <td class="tablecolumnheader">&nbsp
   </td>
   	<%//prompt for serial no if serialized 
	//changes made in RQ-WMS-158
	  if(equals("Y",getValue("ItemDetails","xml:/Item/PrimaryInformation/@SerialCapturedInReturns"))){%>
   <td class="tablecolumnheader"> 
		<yfc:i18n>Serial_#</yfc:i18n>
	</td>
	<%}%>
	<%
	int i = 0;
	while (i < 1) { 
		int j = 0;
		Map normalMap = null;
		Map extnMap = null;
		Map currentMap = null;
		if (i == 0) {
			normalMap = identifierAttrMap;
			extnMap = extnIdentifierAttrMap;
		} else {
			normalMap = descriptorAttrMap;
			extnMap = extnDescriptorAttrMap;
		}		
		if ((normalMap != null) || (extnMap != null)) {
			tagtrack=1;%>
					
							<%while (j < 2) {
							    boolean isExtn = false;
								if (j == 0) {
									currentMap = normalMap;
									isExtn = false;
								} else {
									currentMap = extnMap;
									isExtn = true;
								}
								if (currentMap != null) {
									if (!currentMap.isEmpty()) {
										for (Iterator k = currentMap.keySet().iterator(); k.hasNext();) {
									        String currentAttr = (String) k.next();
									        String currentAttrValue = (String) currentMap.get(currentAttr);%>
											<td class="tablecolumnheader"><yfc:i18n><%=currentAttr%></yfc:i18n></td>
								    	<%
								        }
								    }
								}
					        	
					        	j++;
					        }%>
		<%}
		i++;
	}%>	

		<%//prompt for ship by date if time sensitive
	    if(equals("Y",getValue("ItemDetails","xml:/Item/InventoryParameters/@TimeSensitive"))){%>
		<td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@ShipByDate")%>"><yfc:i18n>Ship_By_Date</yfc:i18n></td>
		<%}%>

        <td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@Quantity")%>"><yfc:i18n>Qty</yfc:i18n></td>
        <td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@DispositionCode")%>"><yfc:i18n>Disposition</yfc:i18n></td>
        <td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@InspectionComments")%>"><yfc:i18n>Comments</yfc:i18n></td>
</tr>
</thead>
<tbody>
	    <%
			 String KitLineCounter=getParameter("optionSetBelongingToLine");
		%>
		<tr>
		
			<td class="tablecolumn" ShouldCopy="true" >
				<img IconName="addSplitLine" class="lookupicon" src="../console/icons/add.gif" onclick="yfcSplitLine(this)" />  
            </td>
		
		<% //prompt for serial no if serialized (Changes made in RQ-WMS-158)
		if(equals(resolveValue("xml:ItemDetails:/Item/PrimaryInformation/@SerialCapturedInReturns"),"Y")) { %>
			<td>
			<input type="text" class="unprotectedinput" NewName="true" NewClassName="unprotectedinput" NewContentEditable="true" NewResetValue="true" 
                    <%=getTextOptions("xml:/" + root + "_" + KitLineCounter + "/@SerialNo", "")%>/>
            </td>
         <%}%>

	<%
	i = 0;
	while (i < 1) { 
		int j = 0;
		Map normalMap = null;
		Map extnMap = null;
		Map currentMap = null;
		if (i == 0) {
			normalMap = identifierAttrMap;
			extnMap = extnIdentifierAttrMap;
		} else {
			normalMap = descriptorAttrMap;
			extnMap = extnDescriptorAttrMap;
		}		
		if ((normalMap != null) || (extnMap != null)) {%>

						<%while (j < 2) {
								
                                boolean isExtn = false;
								if (j == 0) {
									currentMap = normalMap;
									isExtn = false;
							
								} else {
									currentMap = extnMap;
									isExtn = true;
								}

								if (currentMap != null) {
									if (!currentMap.isEmpty()) {
										for (Iterator k = currentMap.keySet().iterator(); k.hasNext();) {
									        String currentAttr = (String) k.next();
									        String currentAttrValue = (String) currentMap.get(currentAttr);
											String sbind="xml:/ShipmentTagSerial/@"+currentAttr;%>

		  <td class="tablecolumn" ShouldCopy="true" >
                <input type="text" class="unprotectedinput" NewName="true" NewClassName="unprotectedinput" NewContentEditable="true" NewResetValue="true" 
                    <%=getTextOptions("xml:/" + root + "_" + KitLineCounter + "/@"+currentAttr, "")%>/>
            </td>
			    	<%
								        }
								    }
								}
					        	
					        	j++;
					        }%>
		<%}
		i++;
	}%>
         
		 
		 <%//prompt for ship by date if time sensitive
			if(equals("Y",getValue("ItemDetails","xml:/Item/InventoryParameters/@TimeSensitive"))){%>
		 <td class="tablecolumn" nowrap="true" ShouldCopy="true" >
                <input type="text" class="unprotectedinput" NewName="true" NewClassName="unprotectedinput" NewContentEditable="true" NewResetValue="true" 
                    <%=getTextOptions("xml:/" + root + "_" + KitLineCounter + "/@ShipByDate", "")%>/>
                <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
            </td>

			<%}%>
            <td class="numerictablecolumn" ShouldCopy="true" >
                <input type="text" class="numericunprotectedinput" NewName="true" NewClassName="numericunprotectedinput" NewContentEditable="true" NewResetValue="true" 
                    <%=getTextOptions("xml:/" + root + "_" + KitLineCounter + "/@Quantity", "")%>/>

                <input type="hidden" NewName="true" 
                    <%=getTextOptions("xml:/" + root + "_" + KitLineCounter + "/@OrderLineKey",  getParameter("OrderLineKey"))%>/>

		        <input type="hidden" NewName="true" 
                    <%=getTextOptions("xml:/" + root + "_" + KitLineCounter + "/@ItemID", getParameter("ItemID"))%>/>

				<input type="hidden" NewName="true" 
                    <%=getTextOptions("xml:/" + root + "_" + KitLineCounter + "/@UnitOfMeasure", getParameter("UOM"))%>/>

                <input type="hidden" NewName="true" 
                    <%=getTextOptions("xml:/" + root + "_" + KitLineCounter + "/@ProductClass",  getParameter("PC"))%>/>

			</td>
            <td class="tablecolumn" ShouldCopy="true" >
                    <select class="combobox" NewName="true" NewResetValue="true" <%=getComboOptions("xml:/" + root + "_" + KitLineCounter + "/@DispositionCode")%>>
                        <yfc:loopOptions binding="xml:/ReturnDispositionList/@ReturnDisposition" name="Description"
                        value="DispositionCode" selected="xml:/Receipt/@DispositionCode" isLocalized="Y"/>
                    </select>
            </td>
            <td class="tablecolumn" nowrap="true" ShouldCopy="true" >
                <input type="text" class="unprotectedinput" NewName="true" NewClassName="unprotectedinput" NewContentEditable="true" NewResetValue="true" 
                    <%=getTextOptions("xml:/" + root + "_" + KitLineCounter + "/@InspectionComments", "")%>/>
            </td>
        </tr>
		</tr>
 </tbody>
</table>
</td>
</tr>
</tbody>
</table>	
