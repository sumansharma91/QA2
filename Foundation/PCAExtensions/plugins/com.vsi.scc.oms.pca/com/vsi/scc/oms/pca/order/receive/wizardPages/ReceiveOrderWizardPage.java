package com.vsi.scc.oms.pca.order.receive.wizardPages;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Element;

import com.vsi.scc.oms.pca.util.VSIUtil;
import com.vsi.scc.oms.pca.util.VSIXmlUtils;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.IYRCPanelHolder;
import com.yantra.yfc.rcp.IYRCTableColumnTextProvider;
import com.yantra.yfc.rcp.YRCButtonBindingData;
import com.yantra.yfc.rcp.YRCCellModifier2;
import com.yantra.yfc.rcp.YRCComboBindingData;
import com.yantra.yfc.rcp.YRCConstants;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCTableBindingData;
import com.yantra.yfc.rcp.YRCTblClmBindingData;
import com.yantra.yfc.rcp.YRCTextBindingData;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCWizardBehavior;
import com.yantra.yfc.rcp.YRCXmlUtils;




public class ReceiveOrderWizardPage  extends Composite implements IYRCComposite {

	public static final String FORM_ID = "com.vsi.scc.oms.pca.order.receive.wizardPages.ReceiveOrderWizardPage";
	ReceiveOrderWizardPageBehaviour myBehavior = null;
	private YRCWizardBehavior extnWizBehavior=null;
	private Composite pnlRoot = null;
	private Composite pnlReceiveOrderDtls = null;
	public Composite pnlPayments = null;
	private Composite pnlOrderLines = null;
	private Composite pnlRight = null;
	private Composite pnlMain=null;
	private Composite pnlButtonHolder=null;
	private Button btnApply = null;
	private Button btnOrderReceive = null;
	private Button btnPageClose = null;
	
	private Table tblOrderLines = null;

	private Composite pnlOrderDetails = null;

	
	private Label lblOrderDate = null;
	

	private Composite pnlReasonSelection = null;

	private Label lblRequestedDeliveryDate = null;
	private Text txtReqDeliveryDate = null;

	protected String strOrderType = null;
	
	protected boolean isTransferOrder = false;
	protected String procurementTOJobName;
	protected boolean isProcurementTO = false;
	private Text txtOrderNo = null;
	private Text txtOrderDate = null;
	private Label lblOrderNo = null;
	private Label lblReturnDisposition = null;
	private Combo cmbReturnDisposition=null;
	
    protected YRCCellModifier2 addLinesCellModifier;
	
	public ReceiveOrderWizardPage(Composite parent, int style, Object inputObject) {
		super(parent, style);	
		initialize();	
		setBindingForComponents();	
		myBehavior = new ReceiveOrderWizardPageBehaviour(this, FORM_ID, inputObject);        
        
		
    }
	
	
	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        createPnlRoot();
        this.setLayout(new FillLayout());
		setSize(new org.eclipse.swt.graphics.Point(800,600));			
	}
	
	private void setBindingForComponents() {
		setBindingForReceiveOrderDtls();
				
		setBindingForTable();
	
		setBindingForButtons();
		
		setBindingForComboBox();
	}
	
	private void setBindingForReceiveOrderDtls() {
        
		YRCTextBindingData txtBinding1 = new YRCTextBindingData();
		txtBinding1.setName("OrderNo");
		txtBinding1.setSourceBinding("OrderDetailsModel:/Order/@OrderNo");
		txtOrderNo.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION, txtBinding1);

		YRCTextBindingData txtBinding2 = new YRCTextBindingData();
		txtBinding2.setName("OrderDate");
		txtBinding2.setDataType("Date");
		txtBinding2.setSourceBinding("OrderDetailsModel:Order/@OrderDate");
		txtOrderDate.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION, txtBinding2);
		
		YRCTextBindingData txtBinding3 = new YRCTextBindingData();
		txtBinding3.setName("RequestedDeliveryDate");
		txtBinding3.setSourceBinding("OrderDetailsModel:Order/@OrderDate");
		txtBinding3.setDataType("Date");
		txtReqDeliveryDate.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION, txtBinding3);
				
	}
	private void setBindingForTable() {
		YRCTableBindingData tableBindingData = new YRCTableBindingData();
		YRCTblClmBindingData[] clm = new YRCTblClmBindingData[tblOrderLines.getColumnCount()];

		clm[0] = new YRCTblClmBindingData();
		clm[0].setAttributeBinding("Item/@ItemID");
		clm[0].setColumnBinding("RO_ITEM_ID");
		clm[0].setName("tblClmItemID");
		clm[0].setSortReqd(true);
		clm[0].setFilterReqd(true);
		
		clm[1] = new YRCTblClmBindingData();
		clm[1].setAttributeBinding("Item/@ItemShortDesc");
		clm[1].setColumnBinding("RO_ITEM_DESC");
		clm[1].setName("tblClmItemDescription");
		clm[1].setSortReqd(true);
		clm[1].setFilterReqd(true);
		
		/*clm[1] = new YRCTblClmBindingData();
		clm[1].setAttributeBinding("ItemDetails/Extn/@ExtnIPLongSKU");
		clm[1].setColumnBinding("RO_ITEM_LONG_SKU");
		clm[1].setName("tblClmItemLongSKU");
		clm[1].setSortReqd(true);
		clm[1].setFilterReqd(true);*/

		clm[2] = new YRCTblClmBindingData();
		clm[2].setAttributeBinding("OrderedQty");
		clm[2].setColumnBinding("RO_Qty");
		clm[2].setSortReqd(true);
		clm[2].setName("tblClmQty");
		clm[2].setFilterReqd(true);
		clm[2].setTargetAttributeBinding("@OrderedQty");
		
		clm[3] = new YRCTblClmBindingData();
		clm[3].setAttributeBinding("LinePriceInfo/@UnitPrice");
		clm[3].setColumnBinding("RO_Unit_Price");
		clm[3].setSortReqd(true);
		clm[3].setName("tblClmUnitPrice");
		clm[3].setFilterReqd(false);

		clm[4] = new YRCTblClmBindingData();
		clm[4].setAttributeBinding("ReturnReason");
		clm[4].setColumnBinding("RO_Return_Reason");
		clm[4].setSortReqd(true);
		clm[4].setName("tblClmReturnReason");
		clm[4].setFilterReqd(false);
		clm[4].setMandatory(true);

		clm[5] = new YRCTblClmBindingData();
		clm[5].setAttributeBinding("ReturnDisposition");
		clm[5].setColumnBinding("RO_Disposition");
		clm[5].setSortReqd(true);
		clm[5].setName("tblClmDisposition");
		clm[5].setTargetAttributeBinding("@ReturnDisposition");	
		clm[5].setFilterReqd(true);
		clm[5].setMandatory(true);

		
		
		 String [] cellEditors = new String[tblOrderLines.getColumnCount()];
		 cellEditors[2] = YRCConstants.YRC_TEXT_BOX_CELL_EDITOR;
	        
	     addLinesCellModifier = new YRCCellModifier2() {
		      
	        	protected boolean allowModify(String property, String value, Element element) {
        			
        			if("@OrderedQty".equals(property)) {
        				return true;
        			}
        			return false;
	        	}

				@Override
				protected YRCValidationResponse validateModifiedValue(String property, String value, Element element) {
					
					
					// TODO Auto-generated method stub
					YRCValidationResponse yrcValidationResponse = new YRCValidationResponse();
					if(property.equalsIgnoreCase("@OrderedQty")) {
						//System.out.println("IPrinting element "+VSIXmlUtils.getElementXMLString(element));
						YRCPlatformUI.setMessage("");
						removeFromError(element, "@OrderedQty");
						 double dblReturnableQty=YRCXmlUtils.getDoubleAttribute(element, "StatusQuantity") ;
				        double val=Double.parseDouble(value);
				        if(!(val<=dblReturnableQty)){
				        	yrcValidationResponse.setStatusCode(YRCValidationResponse.YRC_VALIDATION_ERROR);
				        	YRCPlatformUI.showError("Error", "Entered Quantity is greater than the returnable quantity");
				        	addToError(element, "@OrderedQty", YRCPlatformUI.getFormattedString("Entered Quantity is greater than the returnable quantity",value));
				        	return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR, YRCPlatformUI.getFormattedString("Entered Quantity is greater than the returnable quantity",value));
				        }else if(val<1 && val != 0){
				        	yrcValidationResponse.setStatusCode(YRCValidationResponse.YRC_VALIDATION_ERROR);
				        	YRCPlatformUI.showError("Error", "Entered Quantity should be an integer");
				        	addToError(element, "@OrderedQty", YRCPlatformUI.getFormattedString("Entered Quantity should be an integer",value));
				        	return new YRCValidationResponse(YRCValidationResponse.YRC_VALIDATION_ERROR, YRCPlatformUI.getFormattedString("Entered Quantity should be an integer",value));
				        
				        }
				   }
        			yrcValidationResponse.setStatusCode(YRCValidationResponse.YRC_VALIDATION_OK);
        			return yrcValidationResponse;
				}

				@Override
				protected String getModifiedValue(String arg0, String arg1, Element arg2) {
					// TODO Auto-generated method stub
					return arg1;
				}


	     };	
		tableBindingData.setTblClmBindings(clm);
		tableBindingData.setSortRequired(true);
		tableBindingData.setKeyNavigationRequired(true);
		
		tableBindingData.setSourceBinding("OrderDetailsModel:Order/OrderLines/OrderLine");
		tableBindingData.setName("tblOrderLines");
		tableBindingData.setKeyNavigationRequired(true);
		tableBindingData.setFilterReqd(true);
		tableBindingData.setCellModifierRequired(true);
        tableBindingData.setCellModifier(addLinesCellModifier);
	        tableBindingData.setCellTypes(cellEditors);
		tblOrderLines.setData(YRCConstants.YRC_TABLE_BINDING_DEFINATION,tableBindingData);
		
		
	}
	
	private void setBindingForButtons() {
		

		YRCButtonBindingData btnReceive = new YRCButtonBindingData();
		btnReceive.setName("btnReceive");
		btnReceive.setActionId("");
		btnOrderReceive.setData(YRCConstants.YRC_BUTTON_BINDING_DEFINATION,btnReceive);
        
        YRCButtonBindingData btnClose = new YRCButtonBindingData();
        btnClose.setName("btnClose");
        btnClose.setActionId("");
        btnPageClose.setData(YRCConstants.YRC_BUTTON_BINDING_DEFINATION, btnClose);
        
      
	}
		
	
	public String getFormId() {
		// TODO Auto-generated method stub
		return FORM_ID;
	}

	public String getHelpId() {
		// TODO Auto-generated method stub
		return null;
	}

	public IYRCPanelHolder getPanelHolder() {
		// TODO Auto-generated method stub
		return null;
	}

	public Composite getRootPanel() {
		// TODO Auto-generated method stub
		return pnlRoot;
	}
	
	public YRCWizardBehavior getWizardBehavior() {
		return extnWizBehavior;
	}

	public void setWizBehavior(YRCWizardBehavior extnWizBehavior) {
		this.extnWizBehavior = extnWizBehavior;
	}

	/**
	 * This method initializes pnlRoot	
	 *
	 */
	private void createPnlRoot() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = 2;
		gridLayout.verticalSpacing = 2;
		gridLayout.marginWidth = 2;
		pnlRoot = new Composite(this, SWT.NONE);		
		pnlRoot.setLayout(gridLayout);		
		createPnlReceiveOrderDtls();
		createLowerPnlGroup();
		createPnlButtonHolder();
		pnlRoot.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "TaskComposite");
	}

	/**
	 * This method initializes pnlInvoiceDtls	
	 *
	 */
	private void createPnlReceiveOrderDtls() {
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 3;
		gridLayout1.marginHeight = 1;
		gridLayout1.marginWidth = 1;
		gridLayout1.makeColumnsEqualWidth = false;

		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		gridData.verticalAlignment = GridData.CENTER;
		pnlReceiveOrderDtls = new Composite(getRootPanel(), SWT.NONE);		
		pnlReceiveOrderDtls.setLayoutData(gridData);
		pnlReceiveOrderDtls.setLayout(gridLayout1);
		createPnlOrderDetails();

		pnlReceiveOrderDtls.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "TaskComposite");
	}
	
	private void createPnlOrderDetails(){		

		GridLayout gridLayout4 = new GridLayout();
		gridLayout4.numColumns = 8;
		gridLayout4.marginHeight = 1;
		GridData gridData5 = new GridData();
		gridData5.horizontalAlignment = GridData.FILL;
		gridData5.grabExcessHorizontalSpace = true;
		gridData5.grabExcessVerticalSpace = true;
		gridData5.verticalAlignment = GridData.FILL;
		pnlOrderDetails = new Composite(pnlReceiveOrderDtls, SWT.NONE);
		VSIUtil.addPanelHeader(pnlOrderDetails, "RO_pnlhdr_ReceiptDetails", null, 8);
		
		lblOrderNo = new Label(pnlOrderDetails, SWT.RIGHT);
		lblOrderNo.setText("RO_OrderNo");

		txtOrderNo = new Text(pnlOrderDetails, SWT.READ_ONLY);
		
		
		lblOrderDate = new Label(pnlOrderDetails, SWT.NONE);
		lblOrderDate.setText("RO_OrderDate");
		txtOrderDate = new Text(pnlOrderDetails, SWT.READ_ONLY);
		
		
		lblRequestedDeliveryDate = new Label(pnlOrderDetails, SWT.NONE);
		lblRequestedDeliveryDate.setText("RO_ReqDeliveryDate");
		txtReqDeliveryDate = new Text(pnlOrderDetails, SWT.READ_ONLY);
			
				
		pnlOrderDetails.setLayoutData(gridData5);
		pnlOrderDetails.setLayout(gridLayout4);
		

	}
	private void createLowerPnlGroup() {
		GridData gridData6 = new org.eclipse.swt.layout.GridData();
		gridData6.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData6.grabExcessHorizontalSpace = true;
		gridData6.grabExcessVerticalSpace = true;
		gridData6.verticalSpan = 2;
		gridData6.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		GridLayout gridLayout4 = new GridLayout();
		gridLayout4.marginHeight = 1;
		gridLayout4.numColumns = 2;
		gridLayout4.verticalSpacing = 0;
		gridLayout4.horizontalSpacing = 0;
		gridLayout4.makeColumnsEqualWidth = false;
		gridLayout4.marginWidth = 1;
		pnlMain = new Composite(pnlRoot, SWT.NONE);
		pnlMain.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE,"ItalicText");
		pnlMain.setLayoutData(gridData6);
		pnlMain.setLayout(gridLayout4);
		createPnlOrderLines();
		createRightPanel();
		pnlMain.setData(YRCConstants.YRC_CONTROL_NAME,"pnlMain");

	}
	private void createPnlOrderLines() {
		GridLayout gridLayout8 = new GridLayout();
		gridLayout8.marginHeight = 1;
		gridLayout8.verticalSpacing = 0;
		gridLayout8.marginWidth = 1;
		GridData gridData6 = new GridData();
		gridData6.horizontalAlignment = GridData.FILL;
		gridData6.grabExcessHorizontalSpace = true;
		gridData6.grabExcessVerticalSpace = true;
		gridData6.verticalAlignment = GridData.FILL;
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.grabExcessVerticalSpace = true;
		gridData1.verticalAlignment = GridData.FILL;
		pnlOrderLines = new Composite(pnlMain, SWT.NONE);
		pnlOrderLines.setLayoutData(gridData1);
		pnlOrderLines.setLayout(gridLayout8);
		VSIUtil.addPanelHeader(pnlOrderLines, "RO_pnlhdr_OrderLines", null, 1);
		tblOrderLines = new Table(pnlOrderLines, SWT.NONE);
		tblOrderLines.setHeaderVisible(true);
		tblOrderLines.setLayoutData(gridData6);
		tblOrderLines.setLinesVisible(true);
		TableColumn tblClmItemID = new TableColumn(tblOrderLines, SWT.LEFT);
		tblClmItemID.setWidth(180);
		tblClmItemID.setText("ItemID");
		TableColumn tblClmItemDescription = new TableColumn(tblOrderLines, SWT.LEFT);
		tblClmItemDescription.setWidth(120);
		tblClmItemDescription.setText("Item Description");
		/*TableColumn tblClmItemLongSKU = new TableColumn(tblOrderLines, SWT.LEFT);
		tblClmItemLongSKU.setWidth(120);
		tblClmItemLongSKU.setText("Item Description");*/
		TableColumn tblClmQty = new TableColumn(tblOrderLines, SWT.LEFT);
		tblClmQty.setWidth(70);
		tblClmQty.setText("Quantity");
		TableColumn tblClmUnitPrice = new TableColumn(tblOrderLines, SWT.NONE);
		tblClmUnitPrice.setWidth(80);
		tblClmUnitPrice.setText("Unit Price");
		TableColumn tblClmReturnReason = new TableColumn(tblOrderLines, SWT.RIGHT);
		tblClmReturnReason.setWidth(100);
		tblClmReturnReason.setText("Return Reason");
		TableColumn tblClmDisposition = new TableColumn(tblOrderLines, SWT.NONE);
		tblClmDisposition.setWidth(100);
		tblClmDisposition.setText("Disposition");
		
		
	}
	
	private void createReasonPanel() {
		GridData gridData28 = new GridData();
		gridData28.heightHint = 25;
		gridData28.widthHint = -1;
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.horizontalSpacing = 0;
		gridLayout2.marginHeight = 0;
		gridLayout2.numColumns = 2;
		gridLayout2.makeColumnsEqualWidth = true;
		
		
		GridData gridData5 = new GridData();
		gridData5.horizontalAlignment = GridData.FILL;
		gridData5.grabExcessHorizontalSpace = true;
		gridData5.grabExcessVerticalSpace = true;
		gridData5.widthHint = -1;
		gridData5.heightHint = -1;
		gridData5.verticalSpan = 1;
		gridData5.verticalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		VSIUtil.addPanelHeader(pnlRight, "RO_Return_Disposition", null, 2);
		
		pnlReasonSelection = new Composite(pnlRight, SWT.NONE);
		pnlReasonSelection.setLayoutData(gridData5);
		pnlReasonSelection.setLayout(gridLayout2);
		
		
		
		pnlReasonSelection.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "LightBlueBackground");
		pnlReasonSelection.setData(YRCConstants.YRC_CONTROL_NAME,"pnlReasonSelection");

		
		lblReturnDisposition = new Label(pnlReasonSelection, SWT.NONE);
		lblReturnDisposition.setText("RO_ReturnDisposition");
		lblReturnDisposition.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "TextBlueOnLightBlue");
		pnlReasonSelection.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "LightBlueBackground");
		pnlReasonSelection.setData(YRCConstants.YRC_CONTROL_NAME,"pnlReasonSelection");

		GridData gridData11 = new GridData();
		gridData11.horizontalAlignment =GridData.CENTER;
		gridData11.grabExcessHorizontalSpace = true;
		gridData11.verticalAlignment = GridData.CENTER;
		cmbReturnDisposition = new Combo(pnlReasonSelection, SWT.READ_ONLY);
		cmbReturnDisposition.setLayoutData(gridData11);
		
		btnApply = new Button(pnlReasonSelection, SWT.NONE);
		btnApply.setText("Apply_To_All_Order_Lines");
		btnApply.setLayoutData(gridData28);
		btnApply.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE,"TextBlackOnLightBlue");	
		btnApply.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//YRCPlatformUI.fireAction(ReceiveOrderReasonCodesApplyAction.ACTION_ID);
				myBehavior.applyReasonCodes(cmbReturnDisposition.getText()); 
			}
		});
	}

	/**
	 * This method initializes composite6	
	 *
	 */
	private void createRightPanel() {
		GridData gridData21 = new org.eclipse.swt.layout.GridData();
		gridData21.heightHint = -1;
		gridData21.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData21.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridData21.grabExcessHorizontalSpace = false;
		gridData21.grabExcessVerticalSpace = true;
		GridLayout gridLayout6 = new GridLayout();
		gridLayout6.marginHeight = 1;
		gridLayout6.numColumns = 2;
		gridLayout6.marginWidth = 1;
		gridLayout6.verticalSpacing = 0;
		gridLayout6.horizontalSpacing = 0;
		pnlRight = new Composite(pnlMain, SWT.NONE);
		
		
		pnlRight.setLayout(gridLayout6);
		pnlRight.setLayoutData(gridData21);
		pnlRight.setData(YRCConstants.YRC_CONTROL_NAME,"pnlRight");
		createReasonPanel();
		
		pnlRight.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "LightBlueBackground");
		
	}
	
	private void createPnlButtonHolder() {
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 3;
		gridLayout2.marginWidth = 0;
		GridData gridData5 = new GridData();
		gridData5.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
		gridData5.grabExcessHorizontalSpace = true;
		gridData5.verticalAlignment = GridData.CENTER;
		pnlButtonHolder = new Composite(getRootPanel(), SWT.NONE);
		pnlButtonHolder.setLayoutData(gridData5);
		pnlButtonHolder.setLayout(gridLayout2);
		pnlButtonHolder.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "TaskComposite");
		btnOrderReceive = new Button(pnlButtonHolder, SWT.NONE);
		btnOrderReceive.setText("RO_btnReceive");
		btnOrderReceive.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "TaskComposite");
		btnOrderReceive.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				myBehavior.doReceive();	
				
			}
        });
		
		
		btnPageClose = new Button(pnlButtonHolder, SWT.NONE);
		btnPageClose.setText("RO_btnClose");
		btnPageClose.setData(YRCConstants.YRC_CONTROL_NAME,"btnClose");
		btnPageClose.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE,"TaskComposite");
				
		btnPageClose.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				myBehavior.doClose();				
			}
        });
		
	}
	
	private void setBindingForComboBox() {
		
		YRCComboBindingData comboBindingData1 = new YRCComboBindingData();
		comboBindingData1 = new YRCComboBindingData();
		comboBindingData1.setName("CmbReturnDispositionQryType");
		comboBindingData1.setListBinding("ReturnDispositions:ReturnDispositionList/ReturnDisposition");
		comboBindingData1.setCodeBinding("DispositionCode");
		comboBindingData1.setBundleDriven(true);
		comboBindingData1.setDescriptionBinding("Description");
		cmbReturnDisposition.setData(YRCConstants.YRC_COMBO_BINDING_DEFINATION,comboBindingData1);
		comboBindingData1.setTargetBinding("SelectedCodes:Order/@ReturnDispositionCode");
		cmbReturnDisposition.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				myBehavior.setReturnDisposition(cmbReturnDisposition.getText());
				myBehavior.setReturnDispositionToAllSelectedOrderLines(tblOrderLines);
				myBehavior.refreshTable(tblOrderLines);
			}
		});	
	}
	
	public ReceiveOrderWizardPageBehaviour getMyBehavior() {
		return myBehavior;
	}	
	
	public void focusOnApplyButton(){
		btnApply.setFocus();
	}
}
