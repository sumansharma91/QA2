package com.vsi.scc.oms.pca.order.receive.wizard;

import org.eclipse.swt.widgets.Composite;

import com.yantra.yfc.rcp.IYRCPanelHolder;
import com.yantra.yfc.rcp.YRCWizard;
import com.yantra.yfc.rcp.YRCWizardBehavior;

public class ReceiveOrderWizard extends YRCWizard{

	
	public static final String Wizard_ID = "com.vsi.scc.oms.pca.order.receive.wizard.ReceiveOrderWizard";
	public static  String ruleReturnString ="";
	private Object inputObject;
	
	public ReceiveOrderWizard(String strTest,Composite parent,Object obj, int style) {
		super(strTest,parent,obj,style);
		this.inputObject=obj;
		initializeWizard();
        start();
	}

	/**
     * @see com.yantra.yfc.rcp.YRCWizard#getFormId()
     */
     public String getFormId() {
        return Wizard_ID;
    }

    /**
     	* Initialize Invoice Wizard Behavior.
      * @see com.yantra.yfc.rcp.YRCWizard#createBehavior()
      */
    protected YRCWizardBehavior createBehavior() {
        return new ReceiveOrderWizardBehaviour(this, Wizard_ID, inputObject);
    } 
    
    /**
     * @see com.yantra.yfc.rcp.YRCWizard#getPanelHolder()
     */
	public IYRCPanelHolder getPanelHolder() {
		return null;
	}

	/**
	 * @see com.yantra.yfc.rcp.YRCWizard#getHelpId()
	 */
	public String getHelpId() {
		return null;
	}

	

}
