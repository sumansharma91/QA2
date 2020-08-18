package com.vsi.scc.oms.pca.order.receive.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Element;

import com.vsi.scc.oms.pca.order.receive.wizardPages.ReceiveOrderWizardPage;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCDesktopUI;
import com.yantra.yfc.rcp.YRCEditorInput;
import com.yantra.yfc.rcp.YRCEditorPart;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCWizardBehavior;

public class ReceiveOrderWizardBehaviour extends YRCWizardBehavior  {

	private static String strStatusMsg;
	private static String strCustMsg;
	public static  String ruleReturnString ="";
	private Element wizardInput;
	
	
	public ReceiveOrderWizardBehaviour(Composite ownerComposite, String formId, Object obj) {
		// TODO Auto-generated constructor stub
		super(ownerComposite, formId);
		YRCEditorInput editorIn  = (YRCEditorInput) ((YRCEditorPart) YRCDesktopUI
				.getCurrentPart()).getEditorInput();
	       Element eleInput = (Element)editorIn.getInputObject();
	       if(!YRCPlatformUI.isVoid(eleInput)){
	    	   wizardInput=eleInput;
	    	   setModel("OrderDetails", eleInput);
	       
	       }
        init();
	}

	/**
	 * This method initializes the behavior class.
	 * 
	 * @see com.yantra.yfc.rcp.YRCBehavior#init()
	 */   
	public void init() {		
	}

	/**
	 * This method initializes the FindInvoiceWizardPage class and sets this class as its Wizard Behavior.
	 * 
	 * @see com.yantra.yfc.rcp.YRCWizardBehavior#createPage(java.lang.String, org.eclipse.swt.widgets.Composite)
	 */
	public IYRCComposite createPage(String pageIdToBeShown, Composite pnlRoot) {
		IYRCComposite page=null;

		if(pageIdToBeShown.equalsIgnoreCase(ReceiveOrderWizardPage.FORM_ID)) {
			// FindInvoiceWizardPage is initialized.
			ReceiveOrderWizardPage temp = new ReceiveOrderWizardPage(pnlRoot, SWT.NONE, wizardInput);
			temp.setWizBehavior(this);
			page = temp;
		} 
		return page;	
	}

	/**
	 * @see com.yantra.yfc.rcp.YRCWizardBehavior#pageBeingDisposed(java.lang.String)
	 */
	public void pageBeingDisposed(String pageToBeDisposed) {
		
	}

	/**
	 * @see com.yantra.yfc.rcp.IYRCStatusMessageProvider#getStatusMessage()
	 */
	public String getStatusMessage() {
		return strStatusMsg;
	}
		
	
	/**
	 * This method is used to set the Customer Message at the Message panel.
	 */
	public void setCustomerMessage(String CustMsg){
		strCustMsg = CustMsg;
	}
	
	/**
	 * This method is used to set the Status Message at the Message panel.
	 */
	public void setStatusMessage(String statusMsg){
		strStatusMsg = statusMsg;
	}
	
	

}
