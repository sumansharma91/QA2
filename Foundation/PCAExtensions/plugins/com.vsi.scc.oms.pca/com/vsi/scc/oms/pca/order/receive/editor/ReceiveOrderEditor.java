package com.vsi.scc.oms.pca.order.receive.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.vsi.scc.oms.pca.order.receive.wizard.ReceiveOrderWizard;
import com.yantra.yfc.rcp.YRCConstants;
import com.yantra.yfc.rcp.YRCEditorInput;
import com.yantra.yfc.rcp.YRCEditorPart;
import com.yantra.yfc.rcp.YRCPlatformUI;

public class ReceiveOrderEditor extends YRCEditorPart{
	
	public static final String ID_EDITOR = "com.vsi.scc.oms.pca.order.receive.editor.ReceiveOrderEditor";
	
	private String titlekey = "RECEIVE_ORDER";
	
	private Composite pnlroot = null;
	
	/**
	 * Constructor for the class invokes the super class YRCEditorPart.
	 * 
	 */
	public ReceiveOrderEditor() {
		super();
		
	}
	
	

	/**
	 * @see com.yantra.yfc.rcp.YRCEditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
		 super.doSave(monitor);
	}

	/**
	 * @see com.yantra.yfc.rcp.YRCEditorPart#doSaveAs()
	 */
	public void doSaveAs() {
		 super.doSaveAs();
	}

	/**
	 * @see com.yantra.yfc.rcp.YRCEditorPart#isDirty()
	 */
	public boolean isDirty() {
		return false;
	}

	/**
	 * @see com.yantra.yfc.rcp.YRCEditorPart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {

		return false;
	}



	public Composite createPartControl(Composite parent, String task) {
		
		
		YRCEditorInput input = (YRCEditorInput) getEditorInput();
		pnlroot = new ReceiveOrderWizard("com.vsi.scc.oms.pca.order.receive.wizard.ReceiveOrderWizard",parent,input,SWT.NONE);
		
		pnlroot.setData(YRCConstants.YRC_OWNERPART, this);
		setPartName(YRCPlatformUI.getString(titlekey));
		return pnlroot;
	}
	
	
	

	
	
}
