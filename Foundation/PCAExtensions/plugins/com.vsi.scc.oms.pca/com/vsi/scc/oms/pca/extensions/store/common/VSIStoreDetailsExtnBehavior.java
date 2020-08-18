	
package com.vsi.scc.oms.pca.extensions.store.common;

/**
 * Created on Dec 28,2013
 *
 */
 
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sun.org.apache.bcel.internal.generic.IfInstruction;
import com.vsi.scc.oms.pca.common.VSIApiNames;
import com.vsi.scc.oms.pca.common.VSIConstants;
import com.vsi.scc.oms.pca.extensions.lineSummary.VSILineSummaryWizExtnBehavior;
import com.vsi.scc.oms.pca.util.VSIPcaUtils;
import com.yantra.yfc.rcp.YRCDesktopUI;
import com.yantra.yfc.rcp.YRCExtendedTableBindingData;
import com.yantra.yfc.rcp.YRCExtentionBehavior;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCWizard;
import com.yantra.yfc.rcp.YRCXmlUtils;
/**
 * @author admin
 * © Copyright IBM Corp. All Rights Reserved.
 */
 public class VSIStoreDetailsExtnBehavior extends YRCExtentionBehavior{
	 
	final String DELIMITER_DASH=VSIConstants.DELIMITER_DASH;
	final String DELIMITER_COMMA=VSIConstants.DELIMITER_COMMA;

	/**
	 * This method initializes the behavior class.
	 */
	public void init() {
		//TODO: Write behavior init here.
		
		YRCWizard currentPage = (YRCWizard) YRCDesktopUI.getCurrentPage();
		
		if(VSIPcaUtils.getCurrentPage().getFormId().equals(VSIConstants.FORM_ID_LINE_SUMMARY_PAGE)){
			
			
			VSILineSummaryWizExtnBehavior lineSummaryWizExtnBehavior = (VSILineSummaryWizExtnBehavior) currentPage
			.getExtensionBehavior();
			
			Element eleOrderLineDetails=lineSummaryWizExtnBehavior.getSourceModel("OrderLineDetails");
			
			if(!YRCPlatformUI.isVoid(eleOrderLineDetails)){
				
				String strBusinessCalendarKey=eleOrderLineDetails.getAttribute(VSIConstants.A_BUSINESS_CALENDAR_KEY);
				
				if(!YRCPlatformUI.isVoid(strBusinessCalendarKey)){
					
					callGetCalendarDetailsForCalender(strBusinessCalendarKey,lineSummaryWizExtnBehavior);
					
				}
				
				
			}
		
		
		}
		
		

		
		
	
	}
	
	
 
 	
 	
	private void callGetCalendarDetailsForCalender(String strBusinessCalendarKey,
			VSILineSummaryWizExtnBehavior lineSummaryWizExtnBehavior) {
		// TODO Auto-generated method stub
		Element eleStoreCalendarDetailsInput=YRCXmlUtils.createDocument(VSIConstants.E_CALENDAR).getDocumentElement();
		eleStoreCalendarDetailsInput.setAttribute(VSIConstants.A_CALENDAR_KEY, strBusinessCalendarKey);
		
		Document output=VSIPcaUtils.invokeApi(VSIApiNames.API_GET_CALENDAR_DETAILS, 
				eleStoreCalendarDetailsInput.getOwnerDocument(), getFormId());
		
		if(!YRCPlatformUI.isVoid(output)){
			
			setExtentionModel("Extn_CalenderDetails", output.getDocumentElement());
			
			Element calendar=output.getDocumentElement();
			
				if(!YRCPlatformUI.isVoid(calendar)){
					
					NodeList nlShifts=(NodeList)output.getDocumentElement().getElementsByTagName(VSIConstants.E_SHIFTS);
					
					if(!YRCPlatformUI.isVoid(nlShifts) && nlShifts.getLength() > 0){
						
						NodeList nlShift=(NodeList)((Element)nlShifts.item(0)).getElementsByTagName(VSIConstants.E_SHIFT);
						
						if(!YRCPlatformUI.isVoid(nlShift) && nlShift.getLength() > 0){
							
							for(int i=0;i < nlShift.getLength() ; i++){
								
								Element eleShift=(Element)nlShift.item(i);
								
								String startTime=eleShift.getAttribute(VSIConstants.A_SHIFT_START_TIME);
								
								if(!YRCPlatformUI.isVoid(startTime)){
									
									startTime=get12hrFormat(startTime);
									
								}
								String endTime=eleShift.getAttribute(VSIConstants.A_SHIFT_END_TIME);
								
								if(!YRCPlatformUI.isVoid(endTime)){
									
									endTime=get12hrFormat(endTime);
									
								}
								
							    String shift=getShiftString(eleShift)+VSIConstants.BLANK+startTime+VSIConstants.DELIMITER_DASH+endTime;
							    
							    output.getDocumentElement().setAttribute(VSIConstants.A_SHIFT+i,shift);
							    
							}
							
							
						}
					}
					
					
					
				}
			
			repopulateModel("Extn_CalenderDetails");
		}
		
		
		
	}





	private String getShiftString(Element eleShift) {
		
		String shift="";
		
		String delimiter="";
		
		//Monday
		
		if(eleShift.getAttribute("MondayValid").equals(VSIConstants.FLAG_Y)){
			
			shift="Mon";
		
		
		} 
		
		//Tuesday
		
		if(eleShift.getAttribute("TuesdayValid").equals(VSIConstants.FLAG_Y)){
			
			if(eleShift.getAttribute("MondayValid").equals(VSIConstants.FLAG_Y)){
				
				delimiter=DELIMITER_DASH;
				shift=shift+delimiter+"Tue";
				
			} else {
				
				if(!shift.equalsIgnoreCase("")){
					delimiter=DELIMITER_COMMA;
					shift=shift+delimiter+"Tue";
					
				} else {
					
					shift="Tue";
				}
				
				
				
			}
			
		} 
		
		//Wednesday
		
		if(eleShift.getAttribute("WednesdayValid").equals(VSIConstants.FLAG_Y)){
			
			if(eleShift.getAttribute("TuesdayValid").equals(VSIConstants.FLAG_Y)){
				
				delimiter=DELIMITER_DASH;
				shift=shift+delimiter+"Wed";
				
			} else {
				
				if(!shift.equalsIgnoreCase("")){
					delimiter=DELIMITER_COMMA;
					shift=shift+delimiter+"Wed";
					
				} else {
					
					shift="Wed";
				}
				
				
				
			}
			
		}
		
		//Thursday
		
		if(eleShift.getAttribute("ThursdayValid").equals(VSIConstants.FLAG_Y)){
			
			if(eleShift.getAttribute("WednesdayValid").equals(VSIConstants.FLAG_Y)){
				
				delimiter=DELIMITER_DASH;
				shift=shift+delimiter+"Thu";
				
			} else {
				
				if(!shift.equalsIgnoreCase("")){
					delimiter=DELIMITER_COMMA;
					shift=shift+delimiter+"Thu";
					
				} else {
					
					shift="Thu";
				}
				
				
				
			}
			
		}
		
		//Friday
		

		if(eleShift.getAttribute("FridayValid").equals(VSIConstants.FLAG_Y)){
			
			if(eleShift.getAttribute("ThursdayValid").equals(VSIConstants.FLAG_Y)){
				
				delimiter=DELIMITER_DASH;
				shift=shift+delimiter+"Fri";
				
			} else {
				
				if(!shift.equalsIgnoreCase("")){
					delimiter=DELIMITER_COMMA;
					shift=shift+delimiter+"Fri";
					
				} else {
					
					shift="Fri";
				}
				
				
				
			}
			
		}
		
		//Saturday
		

		if(eleShift.getAttribute("SaturdayValid").equals(VSIConstants.FLAG_Y)){
			
			if(eleShift.getAttribute("FridayValid").equals(VSIConstants.FLAG_Y)){
				
				delimiter=DELIMITER_DASH;
				shift=shift+delimiter+"Sat";
				
			} else {
				
				if(!shift.equalsIgnoreCase("")){
					delimiter=DELIMITER_COMMA;
					shift=shift+delimiter+"Sat";
					
				} else {
					
					shift="Sat";
				}
				
				
				
			}
			
		}
		
		//Sunday

		if(eleShift.getAttribute("SundayValid").equals(VSIConstants.FLAG_Y)){
			
			if(eleShift.getAttribute("SaturdayValid").equals(VSIConstants.FLAG_Y)){
				
				delimiter=DELIMITER_DASH;
				shift=shift+delimiter+"Sun";
				
			} else {
				
				if(!shift.equalsIgnoreCase("")){
					delimiter=DELIMITER_COMMA;
					shift=shift+delimiter+"Sun";
					
				} else {
					
					shift="Sun";
				}
				
				
				
			}
			
		}
		
		
		
		shift=formatShift(shift);
		
		
		
		return shift;
	}





	private String formatShift(String shift) {
		
		// TODO Auto-generated method stub
		
		String formattedString="";
		
		String commaStrings []=shift.split(DELIMITER_COMMA);
		
		
		if(!YRCPlatformUI.isVoid(commaStrings) && commaStrings.length > 0){
			
			for (int i = 0; i < commaStrings.length; i++) {
				
				String dashStrings []=commaStrings[i].split(DELIMITER_DASH);
				
				if(!YRCPlatformUI.isVoid(dashStrings) && dashStrings.length > 0){
					
					if(dashStrings.length > 1){
					
					commaStrings[i]=dashStrings[0]+DELIMITER_DASH+dashStrings[dashStrings.length-1];
					} else {
						
						commaStrings[i]=dashStrings[0];
					}
					
					
				}	
				
				if(formattedString.equalsIgnoreCase("")){
					formattedString=commaStrings[i];
					
				}else {
				
				formattedString=formattedString+DELIMITER_COMMA+commaStrings[i];
				}
				
			}
			
			return formattedString;
			
			
		} else {
			
			String dashStrings []=shift.split(DELIMITER_DASH);
			
			if(!YRCPlatformUI.isVoid(dashStrings) && dashStrings.length > 0){
				
				shift=dashStrings[0]+DELIMITER_DASH+dashStrings[dashStrings.length-1];
				
				
			}	
			
			
		}
		 
		
		
		return shift;
	}





	private String get12hrFormat(String startTime) {
		
		
		 try {
			   SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
		       SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm:ss");
		       Date date = parseFormat.parse(startTime);   
		       startTime=displayFormat.format(date);
		
		 } catch (ParseException e) {
			YRCPlatformUI.showError("Error","Configuration of Store Hours(HH:mm:ss) format is invalid");
			e.printStackTrace();
		}
		  return startTime;
		
		// TODO Auto-generated method stub
		
		
	}





	/**
	 * Method for validating the text box.
     */
    public YRCValidationResponse validateTextField(String fieldName, String fieldValue) {
    	// TODO Validation required for the following controls.
		
		// TODO Create and return a response.
		return super.validateTextField(fieldName, fieldValue);
	}
    
    /**
     * Method for validating the combo box entry.
     */
    public void validateComboField(String fieldName, String fieldValue) {
    	// TODO Validation required for the following controls.
		
		// TODO Create and return a response.
		super.validateComboField(fieldName, fieldValue);
    }
    
    /**
     * Method called when a button is clicked.
     */
    public YRCValidationResponse validateButtonClick(String fieldName) {
    	// TODO Validation required for the following controls.
		
		// TODO Create and return a response.
		return super.validateButtonClick(fieldName);
    }
    
    /**
     * Method called when a link is clicked.
     */
	public YRCValidationResponse validateLinkClick(String fieldName) {
    	// TODO Validation required for the following controls.
		
		// TODO Create and return a response.
		return super.validateLinkClick(fieldName);
	}
	
	/**
	 * Create and return the binding data for advanced table columns added to the tables.
	 */
	 public YRCExtendedTableBindingData getExtendedTableBindingData(String tableName, ArrayList tableColumnNames) {
	 	// Create and return the binding data definition for the table.
		
	 	// The defualt super implementation does nothing.
	 	return super.getExtendedTableBindingData(tableName, tableColumnNames);
	 }
}