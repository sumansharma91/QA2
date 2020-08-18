package com.vsi.oms.api.web;
import java.text.DecimalFormat;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vsi.oms.api.order.VSITransferOrderCreateShipmentBR2;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSISplitOrderUpdated {
	private YFCLogCategory log = YFCLogCategory.instance(VSITransferOrderCreateShipmentBR2.class);
	
     YIFApi api;
	 private Properties props;
	//YIFApi newApi;
	//YIFApi shipApi;
	/**
	 * 
	 * @param env
	 * 	Required. Environment handle returned by the createEnvironment
	 * 	API. This is required for the user exit to either raise errors
	 * 	or access environment information.
	 * @param inXML
	 * 	Required. This contains the input message from ESB.
	 * @return
	 * 
	 * @throws Exception
	 *   This exception is thrown whenever system errors that the
	 *   application cannot handle are encountered. The transaction is
	 *   aborted and changes are rolled back.
	 */
	public Document splitOrderLines(YFSEnvironment env, Document inXML)
	throws Exception {

		try{
		// Start- For common code control over Split to Bosts functionality-Ashutosh
			String bostsSplitFlag="Y";
			String bostsFullSplitFlag="Y";
		// End- For common code control over Split to Bosts functionality-Ashutosh
			
			DecimalFormat df=new DecimalFormat("0.00");
			String enterpriseCode = inXML.getDocumentElement().getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
			Element nOrderLines = (Element)inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINES).item(0);
			NodeList nOrderLine = inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
			/*parsing the order lines in an order and making a call getAvailableInventory API 
			  to check how many quantities can be fullfilled as BOPUS and how many as BOSTS.
			 * Based on the inventory result of the get available inventory call, order line will be split into 
			  BOPUS and BOSTS
			 */
			int originalLength=nOrderLine.getLength();
			// START - Change to process PICK orders only
			Element eleOrderLine = null;
			String strDeliveryMethod = "";
			Boolean bIsPickOrder = false;
			if (originalLength > 0) {
				eleOrderLine = (Element) nOrderLine.item(0);
				strDeliveryMethod = eleOrderLine
						.getAttribute(VSIConstants.ATTR_DELIVERY_METHOD);
				if (!VSIUtils.isEmpty(strDeliveryMethod)) {
						if (strDeliveryMethod.equals("PICK")) {
							bIsPickOrder = true;
					}
				}
			}
			// END - Change to process PICK orders only
			// Start- For common code control over Split to Bosts functionality-Ashutosh
			bostsSplitFlag= getBostsFlag(env,"VSI_SPLIT_BOSTS");
			bostsFullSplitFlag = getBostsFlag(env,"VSI_FULL_SPLIT_BOSTS");
			// End- For common code control over Split to Bosts functionality-Ashutosh

			if (bIsPickOrder) {
			
			for (int i = 0; i < originalLength; i++) {
				//System.out.println("Try 1");
			    double totalLineamount=0.0;
			    int stsQty=0;
			    int stsQty1=0;
				   double pickLinePostChargeamt=0.0;
	                double stskLinePostChargeamt=0.0;
	                double pickLinePostOrderDisChargeamt=0.0;
	                double stskLinePostOrderDisChargeamt=0.0;
				eleOrderLine = (Element) nOrderLine.item(i);
				Element extnEleOrderLine=(Element)eleOrderLine.getElementsByTagName("Extn").item(0);
				int qty=Integer.parseInt(eleOrderLine.getAttribute("OrderedQty"));
				String shipNode=eleOrderLine.getAttribute("ShipNode");
                Element itemEle=(Element)eleOrderLine.getElementsByTagName("Item").item(0);
                Element elinePriceInfor=(Element)eleOrderLine.getElementsByTagName("LinePriceInfo").item(0);
                double dUnitPrice=Double.parseDouble(elinePriceInfor.getAttribute("UnitPrice"));
                String itemID=itemEle.getAttribute("ItemID");
                Element extnEle=(Element)eleOrderLine.getElementsByTagName("Extn").item(0);

				Document createGetAvailInv = XMLUtil.createDocument("Promise");
				Element createGetAvailInvElement = createGetAvailInv.getDocumentElement();
				createGetAvailInvElement.setAttribute(VSIConstants.ATTR_ORG_CODE, enterpriseCode);
				createGetAvailInvElement.setAttribute("CheckInventory", "Y");

				Element promiseLinesEle = XMLUtil.appendChild(createGetAvailInv, createGetAvailInvElement, "PromiseLines", "");
				Element promiseLineEle = XMLUtil.appendChild(createGetAvailInv, promiseLinesEle, "PromiseLine", "");

				promiseLineEle.setAttribute("ShipNode", shipNode);
				promiseLineEle.setAttribute("ItemID", itemID);
				promiseLineEle.setAttribute("UnitOfMeasure", "EACH");
				promiseLineEle.setAttribute("ProductClass", "GOOD");
				promiseLineEle.setAttribute("LineId", "1");
				int availOnHandQty=0;
				int availOnHandQty1=0;
			/*@calling getAvailableinventory call for checking inventory
				@Based on the find inventory calls, order line will be split into multiple order line
			*/
				api = YIFClientFactory.getInstance().getApi();
				Document getAvailInvOutDoc = api.invoke(env,"getAvailableInventory",createGetAvailInv);
				NodeList nInventoryLine = getAvailInvOutDoc.getElementsByTagName("Inventory");
				
				
				/*
				 * iterating the getAvailability inventory output to see if the response returned an inventory
				  tag(if any inventory is present or not for an item)
				 */
				if(nInventoryLine.getLength()>0){
					//System.out.println("Try 2");
					
						Element eleInventory = (Element) nInventoryLine.item(0);
						availOnHandQty=Integer.parseInt(eleInventory.getAttribute("AvailableOnhandQuantity"));
						availOnHandQty1=availOnHandQty;
				}
                        if(nInventoryLine.getLength()>0 && availOnHandQty>0){
						/*
						 Checking if the on hand inventory in the response if not equal to the request invnetory, in 
						 which case we will be splitting the line into BOPUS and BOSTS
						 */
						if(availOnHandQty<qty){
							
							stsQty=qty-availOnHandQty;
							stsQty1=qty-availOnHandQty;
							// Start- For common code control over Split to Bosts functionality-Ashutosh
							if (bostsSplitFlag.equalsIgnoreCase("N")) {
								stsQty=0;
								stsQty1=0;
							}
							// End- For common code control over Split to Bosts functionality-Ashutosh

							eleOrderLine.setAttribute("OrderedQty", Double.toString(availOnHandQty));
						//Creating a new line for the quantity which are not available as STS line type
							Element stsOrderLine = XMLUtil.appendChild(inXML, nOrderLines, "OrderLine", "");

							XMLUtil.copyElement(inXML,eleOrderLine,stsOrderLine);
							stsOrderLine.setAttribute("OrderedQty", Integer.toString(stsQty));
							stsOrderLine.setAttribute("LineType", "SHIP_TO_STORE");
							stsOrderLine.setAttribute("FulfillmentType", "SHIP_TO_STORE");
							eleOrderLine.setAttribute("FulfillmentType", "PICK_IN_STORE");
							Element extnSTSOrderLine=(Element)stsOrderLine.getElementsByTagName("Extn").item(0);
							extnSTSOrderLine.setAttribute("ExtnReleaseID", "2");
							extnEleOrderLine.setAttribute("ExtnReleaseID", "1");

							/*Splitting the line level promotions and BOGO promotions.
							 * Line level promotion are pro-rated and the penny round off difference is applied to last BOPUS item
							 * BOGO are split in a way where the bogo are applied to alternate STS lines first and then the left 
							  BOGO qty  will be applied to BOPUS in alternate manner
							 */
							
							double pennyRoundOfammount=0.00;
			                boolean roundOfDecision=true;
							
							double nonBOGOTotCharge=0.00;
						    double BOGOLineCharge=0.00;
				            int dAdjustedqty=0;
				            double nonBOGOLineCharge=0.00;
				            double nonBogoChargePerLine=0.00;;
				            double nonBogoPICKLineCharge=0.0;
				            double nonBogoSTSLineCharge=0.0;
				            double lineChargePennyIssue=0.0;
				            boolean pennyLineCharge=true;
				            
				            //iterating through line charge to get BOGO and NON-BOGO charges
			                NodeList nlineCharge=eleOrderLine.getElementsByTagName("LineCharge");
			                int nLinecharges=nlineCharge.getLength();
			                for (int l = 0; l < nLinecharges; l++) {	
			                	//System.out.println("length is " +nlineCharge.getLength() );
			                	//System.out.println("Try 3");
			                	
			    				Element eLineCharge = (Element) nlineCharge.item(l);
			    				String chargeName= eLineCharge.getAttribute("ChargeName");
			    				String stringCheckBOGO=chargeName.substring(0, 4);
			    				Element eExtn=(Element) eLineCharge.getElementsByTagName("Extn").item(0);
			    				
			    				//finding if the line charge is due to header level promotion
			    				String isHeaderLevelCharge=eExtn.getAttribute("IsTranLevel");
			    				
			    				//getting BOGO charge amount and BOGO charge QTY
			    				
			    				if(stringCheckBOGO.equalsIgnoreCase("BOGO")){
									//System.out.println("inside bogo charge");

			    					
			    					BOGOLineCharge=Double.parseDouble(eLineCharge.getAttribute("ChargePerLine"));
			    					 dAdjustedqty=Integer.parseInt(eExtn.getAttribute("ExtnQty"));
			    					
			    				}
			    				else{
			    					/*getting NONBOGO and NONOOrderDeiscount(Order discount are pro-rated and placed at line level
			    					by ATG), we will not be considering these order discount in our calculation.
			    					*/
			    					if(!isHeaderLevelCharge.equalsIgnoreCase("Y")){
									//System.out.println("inside non bogo charge");

									nonBOGOLineCharge=Double.parseDouble(df.format(Double.parseDouble(eLineCharge.getAttribute("ChargePerLine"))));
									nonBOGOTotCharge=nonBOGOTotCharge+nonBOGOLineCharge;
									if(log.isDebugEnabled()){
										log.debug("nonBogolinecharge is "+nonBOGOLineCharge);
										log.debug("nonBogoTotlinecharge is "+nonBOGOTotCharge);
										log.debug("availOnHandQty is "+availOnHandQty);
										log.debug("stsQty is "+stsQty);
									}

									//stamping correct linecharge for STS and PICK lines post splitting(pro-rating the charge)
									
									nonBogoPICKLineCharge=Double.parseDouble(df.format(((double)availOnHandQty/(double)qty)*nonBOGOLineCharge));
									nonBogoSTSLineCharge=Double.parseDouble(df.format(((double)stsQty/(double)qty)*nonBOGOLineCharge));
									if(log.isDebugEnabled()){
										log.debug("nonBogoPICKLineCharge is "+nonBogoPICKLineCharge);
										log.debug("nonBogoSTSLineCharge is "+nonBogoSTSLineCharge);
									}

									if(nonBOGOLineCharge<(nonBogoPICKLineCharge+nonBogoSTSLineCharge)){
										lineChargePennyIssue=(nonBogoPICKLineCharge+nonBogoSTSLineCharge)-nonBOGOLineCharge;
										if(log.isDebugEnabled()){
								    		log.debug("lineChargePennyIssue is "+lineChargePennyIssue);
										}

									}
									else{
										lineChargePennyIssue=nonBOGOLineCharge-(nonBogoPICKLineCharge+nonBogoSTSLineCharge);
										pennyLineCharge=false;
										if(log.isDebugEnabled()){
											log.debug("lineChargePennyIssue is "+lineChargePennyIssue);
											log.debug("pennyLineCharge is "+pennyLineCharge);
										}

									}
									
									if(pennyLineCharge){
										nonBogoPICKLineCharge=Double.parseDouble(df.format(nonBogoPICKLineCharge-lineChargePennyIssue));
										if(log.isDebugEnabled()){
								    		log.debug("nonBogoPICKLineCharge peny charge is "+nonBogoPICKLineCharge);
										}

									}
									else{
										nonBogoPICKLineCharge=Double.parseDouble(df.format(nonBogoPICKLineCharge+lineChargePennyIssue));
										if(log.isDebugEnabled()){
								    		log.debug("nonBogoPICKLineCharge peny charge is "+nonBogoPICKLineCharge);
										}

									}
									//setting the pro-rated charge to line charge element of Pick Line
									eLineCharge.setAttribute("ChargePerLine", Double.toString(nonBogoPICKLineCharge));
									eExtn.setAttribute("ExtnQty",Integer.toString(availOnHandQty));
									
									//stamping the pro-rated charges to line charge emelemnt of STS lines
									NodeList nSTSlineCharge=stsOrderLine.getElementsByTagName("LineCharge");
									int stsLineChargeLength=nSTSlineCharge.getLength();
									for (int r=0;r<stsLineChargeLength;r++){
										Element eSTSLineCharge = (Element) nSTSlineCharge.item(r);
					    				String stsChargeName= eSTSLineCharge.getAttribute("ChargeName");
					    				Element eSTSExtn=(Element) eSTSLineCharge.getElementsByTagName("Extn").item(0);
					    				if(stsChargeName.equalsIgnoreCase(chargeName)){
					    					
					    					eSTSLineCharge.setAttribute("ChargePerLine", Double.toString(nonBogoSTSLineCharge));
					    					eSTSExtn.setAttribute("ExtnQty",Integer.toString(stsQty));
					    				}
					    				
										
									}
													
									
			    					
			    				}
			    
			                }
					                
					    			nonBogoChargePerLine=Double.parseDouble(df.format((nonBOGOTotCharge)/(double)qty));
					    			//calculating the penny left amount for non bogo promotions
					    			
					    			if(Double.parseDouble(df.format(nonBOGOTotCharge))>Double.parseDouble(df.format(nonBogoChargePerLine*(double)qty))){
					    				pennyRoundOfammount=Double.parseDouble(df.format(nonBOGOTotCharge))-Double.parseDouble(df.format(nonBogoChargePerLine*(double)qty));
					    			    
					    			}
					    			else{
					    				pennyRoundOfammount=Double.parseDouble(df.format(nonBogoChargePerLine*(double)qty))-Double.parseDouble(df.format(nonBOGOTotCharge));
					    				roundOfDecision=false;
					    			}
					    			
					    			
					    			
					    			
			    			
			                }
			                
			
			                
			                
			                
			                /*
			                 * Creating custom price list for STS line to store price for each quantity which can be used for ATG Order Details 
			                 and downstream emails
			                 */
			                
		    				Element eVSICustomerPriceListSTS = XMLUtil.appendChild(inXML, extnSTSOrderLine, "VSICustomPriceList", "");
		    				 int iteratedAdjustedQty=dAdjustedqty;
		    				 int originalSTSQty=stsQty;
		    				 int originaladjustedQty=dAdjustedqty;
				                double roundedOffAmt=0.00;
				                double customUnitPrice=0.00;
				                double price=0.00;
				                double totalNonBogoCharge=0.00;
				                int bogoChargeQty=0;
				                String nonSTSDiscountedFlag="N";
				                String nonPickDiscountedFlag="N";

				                
			                for(int k=0;k<originalSTSQty;k++){
			                	//System.out.println("Try 4");
                       
			                	//System.out.println("Print KSTS is " + k);
                                     if(dAdjustedqty>0){
                                    	 nonSTSDiscountedFlag="Y";
                                    	 /*below logic is to make sure we round of one BOGO and not then reduce the amount from other bogo
                                    	 For EG: if the BOGO is 9.99/2, then one quantity will get a bog of 7.49 and another of 7.50
                                    	  * 
                                    	  */
                                    	 if(log.isDebugEnabled()){
                                    		 log.debug("dAdjustedqty is  " + dAdjustedqty);
                                    		 log.debug("BOGOLineCharge is  " + BOGOLineCharge);
                                    		 log.debug("roundedOffAmt is  " + roundedOffAmt);
                                    		 log.debug("iteratedAdjustedQty is  " + iteratedAdjustedQty);
                                    		 log.debug("nonBogoChargePerLine is  " + nonBogoChargePerLine);
                                    	 }
         			                	


                                	  price=Double.parseDouble(df.format((BOGOLineCharge-roundedOffAmt)/(double)iteratedAdjustedQty));
                                	  if(log.isDebugEnabled()){
                  			    		log.debug("price is  " + price);
                                	  }

    			    				 iteratedAdjustedQty--;
    			    				 //total BOGO which have been applied on STS lines
    			    				 roundedOffAmt=Double.parseDouble(df.format(roundedOffAmt+price));
    			    				 stsQty--;
    			    				 if(log.isDebugEnabled()){
    			 			    		log.debug("sts qty is  " + stsQty);
    			    				 }

    			    				 dAdjustedqty--;
    			    				 bogoChargeQty++;
     								Element eVSICustomerPriceSTS = XMLUtil.appendChild(inXML, eVSICustomerPriceListSTS, "VSICustomPrice", "");

    			    				 customUnitPrice=Double.parseDouble(df.format(dUnitPrice-price-nonBogoChargePerLine));
    			    				
    			    				 eVSICustomerPriceSTS.setAttribute("Qty","1");
    			    				 eVSICustomerPriceSTS.setAttribute("Amt", Double.toString(customUnitPrice));
                                     }
                                    
                                   
                                     
    			    				 k++;
    			    				
                
			                }
			                //changed for BOP302
			                 if(nonSTSDiscountedFlag.equalsIgnoreCase("N"))
                            {
                           	 
                           	 Element eVSICustomerPriceSTS = XMLUtil.appendChild(inXML, eVSICustomerPriceListSTS, "VSICustomPrice", "");

			    				 customUnitPrice=Double.parseDouble(df.format(dUnitPrice-price-nonBogoChargePerLine));
			    				
			    				 eVSICustomerPriceSTS.setAttribute("Qty",Double.toString(originalSTSQty));
			    				 eVSICustomerPriceSTS.setAttribute("Amt", Double.toString(customUnitPrice));
                            }
                            //** Change for BOP302
			                
			                
			                //Stamping the correct BOGO charges to the order line for order ceate for STS lines
			        		NodeList nSTSlineCharge=stsOrderLine.getElementsByTagName("LineCharge");
							int stsLineChargeLength=nSTSlineCharge.getLength();
							for (int r=0;r<stsLineChargeLength;r++){
	                        	//System.out.println("Print inside sts bogo line charge ");

								Element eSTSLineCharge = (Element) nSTSlineCharge.item(r);
			    				String stsChargeName= eSTSLineCharge.getAttribute("ChargeName");
	                        	//System.out.println("Print stsChargeName" + stsChargeName);

			    				Element eSTSExtn=(Element) eSTSLineCharge.getElementsByTagName("Extn").item(0);
			    				String stringCheckBOGO=stsChargeName.substring(0, 4);
			    				//getting BOGO charge amount and BOGO charge QTY
			    				
			    				if(stringCheckBOGO.equalsIgnoreCase("BOGO")){
			    					
		                        	//System.out.println("Print yes it is a bogo");
		                        	//System.out.println("bogoChargeQty is" + bogoChargeQty);

		                        	//System.out.println("round off amount" + roundedOffAmt);

			    					eSTSLineCharge.setAttribute("ChargePerLine", Double.toString(roundedOffAmt));
			    					eSTSExtn.setAttribute("ExtnQty",Integer.toString(bogoChargeQty));
			    				}
			    				
								
							}
			                
			                //after bogo have been absorbed by STS quantity, rest of the STS quantity will get nonBOgoCharge
							double bogoSTSRoundOff=roundedOffAmt;
			                int stsQtyLeft=stsQty;
			                if(stsQtyLeft>0){
		    				 customUnitPrice=Double.parseDouble(df.format(((double)stsQtyLeft*dUnitPrice)-((double)stsQtyLeft*nonBogoChargePerLine)));
							 Element eVSICustomerPriceSTS1 = XMLUtil.appendChild(inXML, eVSICustomerPriceListSTS, "VSICustomPrice", "");
							 eVSICustomerPriceSTS1.setAttribute("Qty",Integer.toString(stsQtyLeft));
						     eVSICustomerPriceSTS1.setAttribute("Amt", Double.toString(customUnitPrice));
			                }
			               
			                
			                
			                
			                
			                
			                
			                
							//applying the left over BOGO to PICK_IN_STORE lines
			                extnEleOrderLine.setAttribute("ExtnReleaseID", "1");
                            int LeftAdjsutedQty=dAdjustedqty;
                            iteratedAdjustedQty=LeftAdjsutedQty;
                            
                            if(log.isDebugEnabled()){
                            	log.debug("Print originaladjustedQty "+originaladjustedQty);
                            	log.debug("Print dAdjustedqty "+dAdjustedqty);
                            	log.debug("Print LeftAdjsutedQty "+LeftAdjsutedQty);
                            	log.debug("Print iteratedAdjustedQty "+iteratedAdjustedQty);
                            	log.debug("Print BOGOLineCharge "+BOGOLineCharge);
                            	log.debug("Print roundedOffAmt "+roundedOffAmt);
                            }


                           // double pricePick=0.00;
                            int originalPickQty=availOnHandQty;
		    				Element eVSICustomerPriceListPick = XMLUtil.appendChild(inXML, extnEleOrderLine, "VSICustomPriceList", "");
                            double bogoPrice=0.0;
                            for(int k=0;k<originalPickQty;k++){
                            	//System.out.println("Try 5");
                            	//System.out.println("Print K PICK "+k);
                               // if(qty>LeftAdjsutedQty){
                                     if (LeftAdjsutedQty>0){
                                    	 nonPickDiscountedFlag="Y";

                                    	 price=Double.parseDouble(df.format((BOGOLineCharge-roundedOffAmt)/(double)iteratedAdjustedQty));
    			    				 iteratedAdjustedQty--;
    			    				 roundedOffAmt=Double.parseDouble(df.format(roundedOffAmt+price));
    			    				 availOnHandQty--;
    			    				 LeftAdjsutedQty--;
    			    				 bogoPrice=price+bogoPrice;
    			    				 customUnitPrice=Double.parseDouble(df.format(dUnitPrice-price-nonBogoChargePerLine));
     								Element eVSICustomerPricePick = XMLUtil.appendChild(inXML, eVSICustomerPriceListPick, "VSICustomPrice", "");
    			    				 eVSICustomerPricePick.setAttribute("Qty","1");
    			    				 eVSICustomerPricePick.setAttribute("Amt", Double.toString(customUnitPrice));
                                     }
                                  
    			    				 k++;
    			    				
                               // }
			                }
                            
                            //changed for BOP302
			                 if(nonPickDiscountedFlag.equalsIgnoreCase("N"))
                           {     
			                	 double diffPrice=BOGOLineCharge-roundedOffAmt;
                           if(diffPrice==0.0|| diffPrice==0||diffPrice==0.00){
                        	   price=0.0;
                           }
                           else{
			                	 price=Double.parseDouble(df.format((BOGOLineCharge-roundedOffAmt)/(double)iteratedAdjustedQty));
                           }
			                	 customUnitPrice=Double.parseDouble(df.format(dUnitPrice-price-nonBogoChargePerLine));
  								Element eVSICustomerPricePick = XMLUtil.appendChild(inXML, eVSICustomerPriceListPick, "VSICustomPrice", "");
 			    				 eVSICustomerPricePick.setAttribute("Qty",Double.toString(originalPickQty));
 			    				 eVSICustomerPricePick.setAttribute("Amt", Double.toString(customUnitPrice));
                           }
                           //** Change for BOP302
                            
                            //Applying the non-bogo charges to the custom table after BOGO is completely absorbed
                            int pickQtyLeft=originalPickQty-availOnHandQty;
			                if(pickQtyLeft>0){
			                	if(roundOfDecision){
					    			customUnitPrice=Double.parseDouble(df.format(((double)pickQtyLeft*dUnitPrice)-((double)pickQtyLeft*nonBogoChargePerLine)-pennyRoundOfammount));
		
				    			}else{
					    			customUnitPrice=Double.parseDouble(df.format(((double)pickQtyLeft*dUnitPrice)-((double)pickQtyLeft*nonBogoChargePerLine)+pennyRoundOfammount));

				    			}
		    				// customUnitPrice=Double.parseDouble(df.format(((double)pickQtyLeft*dUnitPrice)-((double)stsQtyLeft*nonBogoChargePerLine)));
							 Element eVSICustomerPricePick1 = XMLUtil.appendChild(inXML, eVSICustomerPriceListPick, "VSICustomPrice", "");
							 eVSICustomerPricePick1.setAttribute("Qty",Integer.toString(pickQtyLeft));
							 eVSICustomerPricePick1.setAttribute("Amt", Double.toString(customUnitPrice));
			                }
			                //stamping correct BOGO charges to the order line for order create for Pick Lines
			                
			                NodeList nPicklineCharge=eleOrderLine.getElementsByTagName("LineCharge");
							int pickLineChargeLength=nPicklineCharge.getLength();
							for (int r=0;r<pickLineChargeLength;r++){
								Element ePickLineCharge = (Element) nPicklineCharge.item(r);
			    				String pickChargeName= ePickLineCharge.getAttribute("ChargeName");
			    				Element ePickExtn=(Element) ePickLineCharge.getElementsByTagName("Extn").item(0);
			    				String stringCheckBOGO=pickChargeName.substring(0, 4);
			    				//getting BOGO charge amount and BOGO charge QTY
			    				
			    				if(stringCheckBOGO.equalsIgnoreCase("BOGO")){
			    				
			    					
			    					ePickLineCharge.setAttribute("ChargePerLine", Double.toString(bogoPrice));
			    					ePickExtn.setAttribute("ExtnQty",Integer.toString(dAdjustedqty));
			    				}
			    				
								
							}
			                
							  NodeList nlineChargeNode=eleOrderLine.getElementsByTagName("LineCharge");
				                int nLinechargesint=nlineChargeNode.getLength();
				                for (int l = 0; l < nLinechargesint; l++) {	
				                	//System.out.println("length is " +nlineChargeNode.getLength() );
				                	//System.out.println("Try 3");
				                	
				    				Element eLineCharge = (Element) nlineCharge.item(l);
				    				String chargeName= eLineCharge.getAttribute("ChargeName");
				    				String stringCheckBOGO=chargeName.substring(0, 4);
				    				Element eExtn=(Element) eLineCharge.getElementsByTagName("Extn").item(0);
				    				
				    				//finding if the line charge is due to header level promotion
				    				String isHeaderLevelCharge=eExtn.getAttribute("IsTranLevel");
							if(isHeaderLevelCharge.equalsIgnoreCase("Y")&& !stringCheckBOGO.equalsIgnoreCase("BOGO")){
								//System.out.println("inside order discount charge");
                                double orderDiscountD=0.00;
                                double orderDiscount=0.00;
                                double orderDiscountPick=0.00;
                                double orderDiscountSTS=0.00;
                                double orderDiscountPennyIssue=0.00;
                                boolean pennyOrderDiscount=true;
                                double pickLineAmount=dUnitPrice*(double)availOnHandQty1;
                                if(log.isDebugEnabled()){
                                	log.debug("pickLineAmount is "+pickLineAmount);
                                }

                                double stsLineAmount=dUnitPrice*(double)stsQty1;
                                if(log.isDebugEnabled()){
                                	log.debug("stsLineAmount is "+stsLineAmount);
                                	log.debug("stsQty is "+stsQty1);
                                }

                               
                                pickLinePostChargeamt=pickLineAmount-bogoPrice-nonBogoPICKLineCharge;
                                if(log.isDebugEnabled()){
                                	log.debug("pickLinePostChargeamt is "+pickLinePostChargeamt);
                                	log.debug("bogoPrice is "+bogoPrice);
                                	log.debug("nonBogoPICKLineCharge is "+nonBogoPICKLineCharge);
                                }
                                // update for jira for order with negative charge
								
                               //stskLinePostChargeamt=stsLineAmount-roundedOffAmt-nonBogoSTSLineCharge;
	                            stskLinePostChargeamt=stsLineAmount-bogoSTSRoundOff-nonBogoSTSLineCharge;

	                            if(log.isDebugEnabled()){
	                            	log.debug("nonBogoSTSLineCharge is "+nonBogoSTSLineCharge);
	                            	log.debug("roundedOffAmt is "+roundedOffAmt);
	                            	log.debug("stskLinePostChargeamt is "+stskLinePostChargeamt);
	                            }

								 totalLineamount=pickLinePostChargeamt+stskLinePostChargeamt;
								 if(log.isDebugEnabled()){
							    		log.debug("totalLineamount is "+totalLineamount);
								 }                             
                                
								orderDiscountD=Double.parseDouble(df.format(Double.parseDouble(eLineCharge.getAttribute("ChargePerLine"))));
								orderDiscount=orderDiscount+orderDiscountD;
								

								//stamping correct linecharge for STS and PICK lines post splitting(pro-rating the charge)
								
								//orderDiscountPick=Double.parseDouble(df.format(((double)availOnHandQty/(double)qty)*orderDiscountD));
								//orderDiscountSTS=Double.parseDouble(df.format(((double)stsQty/(double)qty)*orderDiscountD));
								orderDiscountPick=Double.parseDouble(df.format(orderDiscountD*pickLinePostChargeamt/totalLineamount));
								orderDiscountSTS=Double.parseDouble(df.format(orderDiscountD*stskLinePostChargeamt/totalLineamount));

								if(log.isDebugEnabled()){
									log.debug("orderDiscountPick is "+orderDiscountPick);
									log.debug("orderDiscountSTS is "+orderDiscountSTS);
								}


								if(orderDiscountD<(orderDiscountPick+orderDiscountSTS)){
									orderDiscountPennyIssue=(orderDiscountPick+orderDiscountSTS)-orderDiscountD;
									if(log.isDebugEnabled()){
							    		log.debug("lineChargePennyIssue is "+orderDiscountPennyIssue);
									}

								}
								else{
									orderDiscountPennyIssue=orderDiscountD-(orderDiscountPick+orderDiscountSTS);
									pennyOrderDiscount=false;
									if(log.isDebugEnabled()){
										log.debug("lineChargePennyIssue is "+orderDiscountPennyIssue);
										log.debug("pennyLineCharge is "+pennyOrderDiscount);
									}

								}
								
								if(pennyOrderDiscount){
									orderDiscountPick=Double.parseDouble(df.format(orderDiscountPick-orderDiscountPennyIssue));
									if(log.isDebugEnabled()){
							    		log.debug("nonBogoPICKLineCharge peny charge is "+orderDiscountPick);
									}

								}
								else{
									orderDiscountPick=Double.parseDouble(df.format(orderDiscountPick+orderDiscountPennyIssue));
									if(log.isDebugEnabled()){
							    		log.debug("nonBogoPICKLineCharge peny charge is "+orderDiscountPick);
									}

								}
								stskLinePostOrderDisChargeamt=stskLinePostChargeamt-orderDiscountSTS;
								if(log.isDebugEnabled()){
						    		log.debug("stskLinePostChargeamt is "+stskLinePostChargeamt);
								}

								//setting the pro-rated charge to line charge element of Pick Line
								eLineCharge.setAttribute("ChargePerLine", Double.toString(orderDiscountPick));
								eExtn.setAttribute("ExtnQty",Integer.toString(availOnHandQty));
								pickLinePostOrderDisChargeamt=pickLinePostChargeamt-orderDiscountPick;
								if(log.isDebugEnabled()){
						    		log.debug("pickLinePostChargeamt is "+pickLinePostChargeamt);
								}
								
								//stamping the pro-rated charges to line charge emelemnt of STS lines
								NodeList nSTSlineCharge2=stsOrderLine.getElementsByTagName("LineCharge");
								int stsLineChargeLength2=nSTSlineCharge2.getLength();
								for (int r=0;r<stsLineChargeLength2;r++){
									Element eSTSLineCharge = (Element) nSTSlineCharge2.item(r);
				    				String stsChargeName= eSTSLineCharge.getAttribute("ChargeName");
				    				Element eSTSExtn=(Element) eSTSLineCharge.getElementsByTagName("Extn").item(0);
				    				if(stsChargeName.equalsIgnoreCase(chargeName)){
				    					
				    					eSTSLineCharge.setAttribute("ChargePerLine", Double.toString(orderDiscountSTS));
				    					eSTSExtn.setAttribute("ExtnQty",Integer.toString(stsQty));
				    				}
				    				
									
								}
												
								
		    					
		    				} 										
							
						}
				                
				                NodeList nlineTax=eleOrderLine.getElementsByTagName("LineTax");
				                int ilineTaxes=nlineTax.getLength();
				                for (int l = 0; l < ilineTaxes; l++) {
				                	double dPickTax=0.0;
				                	double dSTSTax=0.0;
				                	double totalLineamountpostdiscount=pickLinePostOrderDisChargeamt+stskLinePostOrderDisChargeamt;
									Element eLineTax = (Element) nlineTax.item(l);
									String strTaxName=eLineTax.getAttribute("TaxName");
									double dbTax=Double.parseDouble(eLineTax.getAttribute("Tax"));
									//prorated taxes for STS and Pick Lines are:
	                              // double dSTSTax=Double.parseDouble(df.format((dbTax/(double)qty)*(double)stsQty));
	                              // double dPickTax=dbTax-dSTSTax;
									if(log.isDebugEnabled()){
										log.debug("totalLineamountpostdiscount is "+totalLineamountpostdiscount);
										log.debug("pickLinePostOrderDisChargeamt is "+pickLinePostOrderDisChargeamt);
										log.debug("stskLinePostOrderDisChargeamt is "+stskLinePostOrderDisChargeamt);
									}

                                    if(totalLineamountpostdiscount!=0.0 && pickLinePostOrderDisChargeamt!=0.0 && stskLinePostOrderDisChargeamt!=0.0){
	                               dPickTax=Double.parseDouble(df.format(dbTax*pickLinePostOrderDisChargeamt/totalLineamountpostdiscount));
	                               dSTSTax=Double.parseDouble(df.format(dbTax*stskLinePostOrderDisChargeamt/totalLineamountpostdiscount));
                                    }
                                    else{
      	                             dSTSTax=Double.parseDouble(df.format((dbTax/(double)qty)*(double)stsQty1));;
                                  	 dPickTax=dbTax-dSTSTax;

                                    }

	                            /*
	                             
	                          
	                               double dpennyTax=0.00;
	                              
	                               if(dbTaxName>(dSTSTax+dPickTax)){
	                            	   dpennyTax=dbTaxName-(dSTSTax+dPickTax);
	                            	   dPickTax=dPickTax+dpennyTax;
	                               }
	                               else{
	                            	 
	                            	   dpennyTax=(dSTSTax+dPickTax)-dbTaxName;
	                            	   dPickTax=dPickTax-dpennyTax;
	                               }
	                               */
	                                eLineTax.setAttribute("Tax", Double.toString(dPickTax));
	                                
	                                NodeList nSTSlineTax=stsOrderLine.getElementsByTagName("LineTax");
	    			                int iSTSlineTax=nSTSlineTax.getLength();
	    			                for (int k = 0; k < iSTSlineTax; k++) {
	    			                	Element eSTSLineTax = (Element) nSTSlineTax.item(k);
	    								String strSTSTaxName=eSTSLineTax.getAttribute("TaxName");
	    								if(strSTSTaxName.equalsIgnoreCase(strTaxName)){
	    									eSTSLineTax.setAttribute("Tax", Double.toString(dSTSTax));

	    								}
	                              
				                }
				                }
				                
						}
						
						
						
						/**creating extended price table for emails and details line price
						 
						**/
						
						else{
							/*Below calculation is for the line which is completely Pick In Store
							 which means all the line are present in a store						 
							 */
							extnEleOrderLine.setAttribute("ExtnReleaseID", "1");
							eleOrderLine.setAttribute("FulfillmentType", "PICK_IN_STORE");
							//System.out.println("Try 6");
							//calculation bogo and non bogo charge amounts
							double nonBOGOTotCharge=0.00;
						    double BOGOLineCharge=0.00;
				            int dAdjustedqty=0;
				            double nonBOGOLineCharge=0.00;
			                NodeList nlineCharge=eleOrderLine.getElementsByTagName("LineCharge");
			                for (int l = 0; l < nlineCharge.getLength(); l++) {		
			                	//System.out.println("Try 7");
			    				Element eLineCharge = (Element) nlineCharge.item(l);
			    				String chargeName= eLineCharge.getAttribute("ChargeName");
			    				String stringCheckBOGO=chargeName.substring(0, 4);
		    					Element eExtn = (Element) eLineCharge.getElementsByTagName("Extn").item(0);

			    				String isHeaderLevelCharge=eExtn.getAttribute("IsTranLevel");

			    				if(stringCheckBOGO.equalsIgnoreCase("BOGO")){
									//System.out.println("inside bogo charge");

			    					BOGOLineCharge=Double.parseDouble(eLineCharge.getAttribute("ChargePerLine"));
			    					 dAdjustedqty=Integer.parseInt(eExtn.getAttribute("ExtnQty"));

			    					
			    				}
			    				else{
			    					if(!isHeaderLevelCharge.equalsIgnoreCase("Y")){
									//System.out.println("inside non bogo charge");

									nonBOGOLineCharge=Double.parseDouble(eLineCharge.getAttribute("ChargePerLine"));
			    					nonBOGOTotCharge=nonBOGOTotCharge+nonBOGOLineCharge;
			    				}
			                }
			                }
			                //non bogo charge per line
			                double pennyRoundOfammount;
			                boolean roundOfDecision=true;
			                
			    			double nonBogoChargePerLine=Double.parseDouble(df.format((nonBOGOTotCharge)/(double)qty));
			    			if(Double.parseDouble(df.format(nonBOGOTotCharge))>Double.parseDouble(df.format(nonBogoChargePerLine*(double)qty))){
			    				pennyRoundOfammount=Double.parseDouble(df.format(nonBOGOTotCharge))-Double.parseDouble(df.format(nonBogoChargePerLine*(double)qty));
			    			    
			    			}
			    			else{
			    				pennyRoundOfammount=Double.parseDouble(df.format(nonBogoChargePerLine*(double)qty))-Double.parseDouble(df.format(nonBOGOTotCharge));
			    				roundOfDecision=false;
			    			}
			    			
			    				
			                int iteratedAdjustedQty=dAdjustedqty;
			                double roundedOffAmt=0.00;
			                double customUnitPrice=0.00;
			    			//lopping bogo quantity to find out the price
		    				Element eVSICustomerPriceList = XMLUtil.appendChild(inXML, extnEleOrderLine, "VSICustomPriceList", "");

			    			 for (int b = 0; b < qty; b++) {	
							 for (int n = 0; n < dAdjustedqty; n++) {
									Element eVSICustomerPrice = XMLUtil.appendChild(inXML, eVSICustomerPriceList, "VSICustomPrice", "");

									//System.out.println("inside inner loop");

			    				 double price=Double.parseDouble(df.format((BOGOLineCharge-roundedOffAmt)/(double)iteratedAdjustedQty));
			    				 iteratedAdjustedQty--;
			    				 roundedOffAmt=Double.parseDouble(df.format(price+roundedOffAmt));

			    				 customUnitPrice=Double.parseDouble(df.format(dUnitPrice-price-nonBogoChargePerLine));
			    			     eVSICustomerPrice.setAttribute("Qty","1");
							     eVSICustomerPrice.setAttribute("Amt", Double.toString(customUnitPrice));
			    				  }
								//System.out.println("inside outer loop");
								Element eVSICustomerPrice = XMLUtil.appendChild(inXML, eVSICustomerPriceList, "VSICustomPrice", "");

			    			int remainigQty=qty-dAdjustedqty;
			    			if (remainigQty>0){
			    			if(roundOfDecision){
				    			customUnitPrice=Double.parseDouble(df.format(((double)remainigQty*dUnitPrice)-((double)remainigQty*nonBogoChargePerLine)-pennyRoundOfammount));
	
			    			}else{
				    			customUnitPrice=Double.parseDouble(df.format(((double)remainigQty*dUnitPrice)-((double)remainigQty*nonBogoChargePerLine)+pennyRoundOfammount));

			    			}
			    			eVSICustomerPrice.setAttribute("Qty",Integer.toString(remainigQty));
						    eVSICustomerPrice.setAttribute("Amt", Double.toString(customUnitPrice));	
			    			}
			    			break;				

						}
						}
				}
					
				else{
	
					// Start- For common code control over Split to Bosts functionality-Ashutosh
					if ((bostsSplitFlag.equalsIgnoreCase("N")) && (bostsFullSplitFlag.equalsIgnoreCase("N") )) {
						eleOrderLine.setAttribute("OrderedQty", "0");
					}
					// End- For common code control over Split to Bosts functionality-Ashutosh
					
										
				//when the whole line is only sts, which means no quantity is available onhand
					extnEleOrderLine.setAttribute("ExtnReleaseID", "2");
					eleOrderLine.setAttribute("FulfillmentType", "SHIP_TO_STORE");
					//System.out.println("Try 8");
					//System.out.println("Inside STS");
					eleOrderLine.setAttribute("LineType", "SHIP_TO_STORE");
					
					
					
					//calculation bogo and non bogo charge amounts
					double nonBOGOTotCharge=0.00;
				    double BOGOLineCharge=0.00;
		            int dAdjustedqty=0;
		            double nonBOGOLineCharge=0.00;
	                NodeList nlineCharge=eleOrderLine.getElementsByTagName("LineCharge");
	                for (int l = 0; l < nlineCharge.getLength(); l++) {			
	    				Element eLineCharge = (Element) nlineCharge.item(l);
	    				String chargeName= eLineCharge.getAttribute("ChargeName");
	    				String stringCheckBOGO=chargeName.substring(0, 4);
    					Element eExtn = (Element) eLineCharge.getElementsByTagName("Extn").item(0);
	    				String isHeaderLevelCharge=eExtn.getAttribute("IsTranLevel");

	    				if(stringCheckBOGO.equalsIgnoreCase("BOGO")){
							//System.out.println("inside bogo charge");

	    					BOGOLineCharge=Double.parseDouble(eLineCharge.getAttribute("ChargePerLine"));
	    					 dAdjustedqty=Integer.parseInt(eExtn.getAttribute("ExtnQty"));
	    					
	    				}
	    				else{
	    					if(!isHeaderLevelCharge.equalsIgnoreCase("Y")){
							//System.out.println("inside non bogo charge");

							nonBOGOLineCharge=Double.parseDouble(eLineCharge.getAttribute("ChargePerLine"));
	    					nonBOGOTotCharge=nonBOGOTotCharge+nonBOGOLineCharge;
	    				}
	                }
	                }
	                //non bogo charge per line
	                double pennyRoundOfammount;
	                boolean roundOfDecision=true;
	                
	    			double nonBogoChargePerLine=Double.parseDouble(df.format((nonBOGOTotCharge)/(double)qty));
	    			if(Double.parseDouble(df.format(nonBOGOTotCharge))>Double.parseDouble(df.format(nonBogoChargePerLine*(double)qty))){
	    				pennyRoundOfammount=Double.parseDouble(df.format(nonBOGOTotCharge))-Double.parseDouble(df.format(nonBogoChargePerLine*(double)qty));
	    			    
	    			}
	    			else{
	    				pennyRoundOfammount=Double.parseDouble(df.format(nonBogoChargePerLine*(double)qty))-Double.parseDouble(df.format(nonBOGOTotCharge));
	    				roundOfDecision=false;
	    			}
	    			
	    				
	                int iteratedAdjustedQty=dAdjustedqty;
	                double roundedOffAmt=0.00;
	                double customUnitPrice=0.00;
	    			//lopping bogo quantity to find out the price
    				Element eVSICustomerPriceList = XMLUtil.appendChild(inXML, extnEleOrderLine, "VSICustomPriceList", "");

	    			 for (int b = 0; b < qty; b++) {	
					 for (int n = 0; n < dAdjustedqty; n++) {
							Element eVSICustomerPrice = XMLUtil.appendChild(inXML, eVSICustomerPriceList, "VSICustomPrice", "");

							//System.out.println("inside inner loop");

	    				 double price=Double.parseDouble(df.format((BOGOLineCharge-roundedOffAmt)/(double)iteratedAdjustedQty));
	    				 iteratedAdjustedQty--;
	    				 roundedOffAmt=Double.parseDouble(df.format(price+roundedOffAmt));

	    				 customUnitPrice=Double.parseDouble(df.format(dUnitPrice-price-nonBogoChargePerLine));
	    			     eVSICustomerPrice.setAttribute("Qty","1");
					     eVSICustomerPrice.setAttribute("Amt", Double.toString(customUnitPrice));
	    				  }
						//System.out.println("inside outer loop");
						Element eVSICustomerPrice = XMLUtil.appendChild(inXML, eVSICustomerPriceList, "VSICustomPrice", "");

	    			int remainigQty=qty-dAdjustedqty;
	    			if (remainigQty>0){
	    			if(roundOfDecision){
		    			customUnitPrice=Double.parseDouble(df.format(((double)remainigQty*dUnitPrice)-((double)remainigQty*nonBogoChargePerLine)-pennyRoundOfammount));

	    			}else{
		    			customUnitPrice=Double.parseDouble(df.format(((double)remainigQty*dUnitPrice)-((double)remainigQty*nonBogoChargePerLine)+pennyRoundOfammount));

	    			}
	    			eVSICustomerPrice.setAttribute("Qty",Integer.toString(remainigQty));
				    eVSICustomerPrice.setAttribute("Amt", Double.toString(customUnitPrice));	
	    			}
	    			break;				

				}
				}

			}				
			}
			
            
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return inXML;
	}
	
	public static String getBostsFlag(YFSEnvironment env, String splitType) {
		String bostsFlag = null;
		String splitTypeValue = splitType;
		try{
		Document docForGetCommonCodeList = XMLUtil.createDocument(VSIConstants.ELEMENT_COMMON_CODE);

		Element eleCommonCode = docForGetCommonCodeList.getDocumentElement();
			    
		eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_TYPE, splitTypeValue);
	
		Document docAfterGetCommonCodeList = VSIUtils.invokeAPI(env,VSIConstants.API_COMMON_CODE_LIST,docForGetCommonCodeList);
		
		
		if (docAfterGetCommonCodeList != null) {
			Element eleOutCommonCode=(Element) docAfterGetCommonCodeList.getElementsByTagName(VSIConstants.ELEMENT_COMMON_CODE).item(0);
			if(eleOutCommonCode != null){
				bostsFlag = eleOutCommonCode.getAttribute(VSIConstants.ATTR_CODE_VALUE);
			}
		
			
		}}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return bostsFlag;
	}
}
