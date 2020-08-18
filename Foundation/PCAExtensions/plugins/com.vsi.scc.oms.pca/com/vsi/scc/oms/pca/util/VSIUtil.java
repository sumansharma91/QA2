package com.vsi.scc.oms.pca.util;

import java.math.RoundingMode;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.yantra.yfc.rcp.YRCApplicationContainerUtils;
import com.yantra.yfc.rcp.YRCConstants;
import com.yantra.yfc.rcp.YRCPlatformUI;


public class VSIUtil {
        
    public static void addPanelHeader(final Composite composite, String sPanelHeader, String sPanelImageTheme,
            int colSpan) {
            GridData gridData = new org.eclipse.swt.layout.GridData();
            gridData.horizontalIndent = 5;
            gridData.heightHint = 17;
            gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
            gridData.horizontalSpan = colSpan;
            gridData.grabExcessHorizontalSpace = true;
            
            final Label lblPanelTitle = new Label(composite,SWT.NONE);
            lblPanelTitle.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE,"PanelHeader");
            lblPanelTitle.setText(YRCPlatformUI.getString(sPanelHeader));
            lblPanelTitle.setLayoutData(gridData);

            composite.addPaintListener(new PaintListener(){
                public void paintControl(PaintEvent e) {
                    GC gc = new GC(composite);
                    Rectangle r = composite.getClientArea();
                    Rectangle r1 = lblPanelTitle.getBounds();
                    gc.setBackground(YRCPlatformUI.getBackGroundColor("PanelHeader"));
                    gc.fillRectangle(r.x,r.y,r.width,r1.height+1);
                    
                    gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));      
                
                    gc.drawLine(r.x,r1.y+r1.height,r.x+r.width-2,r1.y+r1.height);
                    
                    gc.setForeground(YRCPlatformUI.getBackGroundColor("TaskComposite"));        
                    gc.drawLine(r.x+r.width-1,r.y,r.x+r.width-1,r.y+r.height-1);
                    gc.drawLine(r.x,r.y+r.height-1,r.x+r.width-1,r.y+r.height-1);
                    gc.drawLine(r.x,r.y,r.x+r.width,r.y);
                    gc.drawLine(r.x,r.y,r.x,r.y+r.height-1);
                    gc.dispose();
                }
            });
        }
    public static void addPanelHeader(final Composite composite, String sPanelHeader, String lblName, String sPanelImageTheme,
            int colSpan) {
            GridData gridData = new org.eclipse.swt.layout.GridData();
            gridData.horizontalIndent = 5;
            gridData.heightHint = 17;
            gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
            gridData.horizontalSpan = colSpan;
            gridData.grabExcessHorizontalSpace = true;
            
            final Label lblPanelTitle = new Label(composite,SWT.NONE);
            lblPanelTitle.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE,"PanelHeader");
            lblPanelTitle.setData(YRCConstants.YRC_CONTROL_NAME,lblName);
            lblPanelTitle.setText(YRCPlatformUI.getString(sPanelHeader));
            lblPanelTitle.setLayoutData(gridData);

            composite.addPaintListener(new PaintListener(){
                public void paintControl(PaintEvent e) {
                    GC gc = new GC(composite);
                    Rectangle r = composite.getClientArea();
                    Rectangle r1 = lblPanelTitle.getBounds();
                    gc.setBackground(YRCPlatformUI.getBackGroundColor("PanelHeader"));
                    gc.fillRectangle(r.x,r.y,r.width,r1.height+1);
                    
                    gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));      
                
                    gc.drawLine(r.x,r1.y+r1.height,r.x+r.width-2,r1.y+r1.height);
                    
                    gc.setForeground(YRCPlatformUI.getBackGroundColor("TaskComposite"));        
                    gc.drawLine(r.x+r.width-1,r.y,r.x+r.width-1,r.y+r.height-1);
                    gc.drawLine(r.x,r.y+r.height-1,r.x+r.width-1,r.y+r.height-1);
                    gc.drawLine(r.x,r.y,r.x+r.width,r.y);
                    gc.drawLine(r.x,r.y,r.x,r.y+r.height-1);
                    gc.dispose();
                }
            });
        }
	public static Shell getShell() {
		Shell shell = null;
		if(shell == null || shell.isDisposed() || !(shell.getStyle() == getShellStyle())) {
	            shell = new Shell(getActiveShell(), getShellStyle());
	            shell.setLayout(new FillLayout());
	            int midx = shell.getBounds().width;
	            int midy = shell.getBounds().height;
	            shell.setBounds((midx)/2, (midy)/2, 0, 0);
	            String appImageTheme = YRCApplicationContainerUtils.getContainerImageTheme();
	            if(!YRCPlatformUI.isVoid(appImageTheme)) {
	                shell.setImage(YRCPlatformUI.getImage(appImageTheme));
	                MessageDialog.setDefaultImage(YRCPlatformUI.getImage(appImageTheme));
	            }
	            return shell;
	        }
	        return shell;
	}
    public static int getShellStyle() {
    	int style = SWT.APPLICATION_MODAL | SWT.RESIZE | SWT.CLOSE | SWT.BORDER;
		return style;
    }
    
    public static Shell getActiveShell() {
    	Display display = Display.getCurrent();
    	if(display == null) {
    		display = Display.getDefault();
    	}
    	return display.getActiveShell();
    }
 
	//Method gives the round off value (#.##) which is ROUND.HALF_EVEN for the given amount in double 
    public static double roundingDown(double amount) throws Exception {        
        double formattedAmount = 0.0D;               
        // Set the Decimal Format 
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.##");
        //Set the Rounding Mode
        df.setRoundingMode(RoundingMode.HALF_EVEN);
        // Apply the format
        formattedAmount = df.parse(df.format(amount)).doubleValue();
        //return the roundoff value as a double value
        return formattedAmount;
    }
  
}