package com.vsi.oms.process.saorders;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;



public class ProcessSAOrders {


	private static final String DB_DRIVER = "oracle.jdbc.driver.OracleDriver";

	/**
	 * PROD DB details
	 */
	//private static final String DB_CONNECTION = "jdbc:oracle:thin:@//10.3.51.167:1881/OMSPROD_SVC";
	//private static final String DB_USER = "sterling_reader01";
	//private static final String DB_PASSWORD = "xpruhjJ3Px";

	/**
	 * QA DB details
	 */
	//	private String DB_CONNECTION = "jdbc:oracle:thin:@10.3.53.74:1581:omstest";	
	//	private String DB_USER = "sterling";
	//	private String DB_PASSWORD = "st34rl1ng0ms";
	private String STERLING_USERID="Test.Test";  // User - Env Variable




	private YFCLogCategory log = YFCLogCategory.instance(ProcessSAOrders.class);

	public Document  processSAOrders (YFSEnvironment yfsEnv, Document docInXML)
			throws YFSUserExitException, YFSException, RemoteException, YIFClientCreationException, SQLException {

		printDebugLogs("*** Inside Method processSAOrders");
		printDebugLogs("***Printing Input XML"+SCXmlUtil.getString(docInXML));

		Element eleRootEleInXML = docInXML.getDocumentElement();



		String strOrderNo = eleRootEleInXML.getAttribute("OrderNo");
		String strExtnTransactionNo = eleRootEleInXML.getAttribute("ExtnTransactionNo");
		String strAmount = eleRootEleInXML.getAttribute("Amount");
		STERLING_USERID=yfsEnv.getUserId();

		//**Just for Debug**//
		printDebugLogs("*** Printing values*****");
		printDebugLogs("*** strOrderNo "+ strOrderNo);
		printDebugLogs("*** strExtnTransactionNo "+ strExtnTransactionNo);
		printDebugLogs("*** strAmount "+ strAmount);
		printDebugLogs("*** LoggedInUserId******"+STERLING_USERID);	

		printDebugLogs("*** Now logic******");
		//**Just for Debug**//

		//		strOrderNo="";
		//		strExtnTransactionNo="";
		Document doc=logic(yfsEnv,strOrderNo,strExtnTransactionNo);
		return doc;
	}


	//	public static void main(String[] argv) {
	//		try {
	//
	//			ProcessSAOrders psao = new ProcessSAOrders();
	//			String OrderNo="09801200114152548498";
	//			String extnTransNo="916";
	//			psao.logic(null,OrderNo,extnTransNo);
	//
	//
	//
	//		} catch (Exception e) {
	//			System.out.println(e.getMessage());
	//		}
	//	}



	public ArrayList excuteQuery(String sqlStmt, String[] selectArr) throws SQLException {

		Connection dbConnection = null;
		Statement statement = null;
		ResultSet rs =null;
		printDebugLogs("selectTableSQL == "+sqlStmt);
		ArrayList<HashMap<String, String>> alist =null;
		try {
			dbConnection = getDBConnection();
			statement = dbConnection.createStatement();

			// execute select SQL stetement
			rs = statement.executeQuery(sqlStmt);

			if(rs!=null) {

				alist= new ArrayList<HashMap<String,String>>();
				while (rs.next()) {				
					HashMap<String, String> aMap = new HashMap<String, String>();
					for (int i=0 ; i<selectArr.length ; i++) {
						aMap.put(selectArr[i], rs.getString(selectArr[i]));
					}
					alist.add(aMap);
				}
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			if (statement != null) {
				statement.close();
			}
			if (dbConnection != null) {
				dbConnection.close();
			}
			if (rs != null) {
				rs.close();
			}
		}		
		return alist;
	}




	public  boolean excuteUpdateQuery(String sql) throws SQLException {

		boolean success=false;
		printDebugLogs("Insert/Update "+sql);
		Connection dbConnection = null;
		Statement statement = null;

		printDebugLogs("sql == "+sql);

		try {
			dbConnection = getDBConnection();
			statement = dbConnection.createStatement();

			int x = statement.executeUpdate(sql); 
			if (x > 0)  {           
				System.out.println("Successfully Update /Inserted");  
				success=true;
			}
			else{            
				System.out.println("Update /Inserted Failed");
				success=false;
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			if (statement != null) {
				statement.close();
			}
			if (dbConnection != null) {
				dbConnection.close();
			}
		}
		return success;
	}


	private Document logic(YFSEnvironment yfsEnv,String orderNo, String extnTransNo) throws SQLException {

		//getSQLSMTOrderInvoiceTotAmt   this will give pending amount
		//getSQLSMTCH_Key               this will give charge transaction key
		//updateSQLChargeTransaction   use Amt and key above queries
		//updateSQLOrderInvoice

		ArrayList<HashMap<String, String>> aList =null;
		try {
			String[] selectCH_OIArr= {"total_amount","order_invoice_key","charge_transaction_key","request_amount","amount_collected","order_header_key"};
			aList = excuteQuery(getSQLSMTCH_OIKey(orderNo,extnTransNo),selectCH_OIArr);
			if(aList!=null && aList.size()>0) {
				for(int i= 0; i<aList.size();i++) {

					HashMap<String, String> aMap = aList.get(i);

					String total_amount = aMap.get("total_amount");
					String order_invoice_key = aMap.get("order_invoice_key");
					String charge_transaction_key = aMap.get("charge_transaction_key");
					String request_amount = aMap.get("request_amount");
					String orderHeaderKey = aMap.get("order_header_key");
					String amount_collected = aMap.get("amount_collected");
					

					//**Just for Debug**//
					printDebugLogs("total_amount ="+total_amount);
					printDebugLogs("order_invoice_key ="+order_invoice_key);
					printDebugLogs("charge_transaction_key ="+charge_transaction_key);
					printDebugLogs("request_amount ="+request_amount);
					printDebugLogs("orderHeaderKey ="+orderHeaderKey);
					printDebugLogs("amount_collected ="+amount_collected);
					//**Just for Debug**//


					boolean updateCH=excuteUpdateQuery(updateSQLChargeTransaction(total_amount,charge_transaction_key));
					if(updateCH) {
						boolean updateOI=excuteUpdateQuery(updateSQLOrderInvoice(charge_transaction_key, total_amount, order_invoice_key));
						if(updateOI) {
							ArrayList<HashMap<String, String>> aList1 =null;
							try {
								String[] selectOIColArr= {"invoice_collection_key"};
								aList1 = excuteQuery(getSQLSMTInvoiceCollection(order_invoice_key),selectOIColArr);

								boolean isSuccessfullyUpIn=false;
								if(aList1.size()>0) {
									isSuccessfullyUpIn=excuteUpdateQuery(updateSQLInvoiceCollection(charge_transaction_key, total_amount, order_invoice_key));
								}else {
									isSuccessfullyUpIn=excuteUpdateQuery(insertSQLInvoiceCollection(charge_transaction_key, total_amount, order_invoice_key));
								}

								if(isSuccessfullyUpIn) {

									Document inputChangeOrder= createInputRemoveHold(orderHeaderKey);
									Document docChangeOrderOutput = VSIUtils.invokeAPI(yfsEnv,VSIConstants.API_CHANGE_ORDER, inputChangeOrder);
									if(docChangeOrderOutput!=null) {

										Document inputReqCollection= createInputRequestCollection(orderHeaderKey);
										Document docRequestCollectionOutput = VSIUtils.invokeAPI(yfsEnv,"requestCollection", inputReqCollection);
										
									}
									
									return createOutDoc(orderNo, total_amount);

								}
							}catch (Exception exception) {
								exception.printStackTrace();
							}
						}
					}
				}
			}
			
			
		}catch (Exception exception) {
			exception.printStackTrace();
		}
		 return null;
		
	}


	/*
	 * select ych.charge_transaction_key ,yoi.order_invoice_key,
	 * ych.request_amount,yoi.total_amount,yoi.amount_collected from
	 * sterling.yfs_charge_transaction ych, sterling.yfs_order_invoice
	 * yoi,sterling.yfs_shipment ys , sterling.yfs_order_header yoh where
	 * ych.order_header_key=yoh.order_header_key and
	 * yoi.order_header_key=yoh.order_header_key and
	 * ys.order_header_key=yoh.order_header_key and yoi.shipment_key=ys.shipment_key
	 * and ych.request_amount=yoi.total_amount and ych.charge_type='CHARGE' and
	 * ych.status='OPEN' and yoh.order_no='WO40440193' and
	 * ys.extn_transaction_no='347276';
	 */

	private  String getSQLSMTCH_OIKey(String OrderNo,String extTransNo) {
		String selectTableCHSQL = "select ych.charge_transaction_key ,yoi.order_invoice_key, ych.request_amount,yoi.total_amount,yoi.amount_collected,yoi.order_header_key "
				+ "from sterling.yfs_charge_transaction ych, sterling.yfs_order_invoice yoi,sterling.yfs_shipment ys , sterling.yfs_order_header yoh"
				+ " where ych.order_header_key=yoh.order_header_key"
				+ " and yoi.order_header_key=yoh.order_header_key"
				+ " and ys.order_header_key=yoh.order_header_key"
				+ " and yoi.shipment_key=ys.shipment_key"
				+ " and ych.request_amount=yoi.total_amount"
				+ " and ych.charge_type='CHARGE' and ych.status in ('ERROR','OPEN')"
				+ " and yoh.order_no='"+OrderNo+"'"
				+ " and ys.extn_transaction_no='"+extTransNo+"'";

		return selectTableCHSQL;
	}


	private  String getSQLSMTInvoiceCollection(String OrderInvoiceKey) {
		String selectTableICSQL = "select * from sterling.yfs_invoice_collection "
				+ "WHERE order_invoice_key='"+OrderInvoiceKey+"'";

		return selectTableICSQL;
	}

	private  String updateSQLChargeTransaction(String amount,String charge_transaction_key) {
		String credit_amount=amount;
		String distributed_amount="-"+amount;
		String updateTableCHSQL = "UPDATE STERLING.YFS_CHARGE_TRANSACTION set CREDIT_AMOUNT='"+credit_amount+"',"
				+ "DISTRIBUTED_AMOUNT='"+distributed_amount+"',USER_EXIT_STATUS=' ',STATUS='CLOSED',"
				+ "EXECUTION_DATE=SYSDATE WHERE CHARGE_TRANSACTION_KEY='"+charge_transaction_key+"'";

		return updateTableCHSQL;
	}

	private  String updateSQLOrderInvoice(String charge_transaction_key,String amount_collected,String order_invoice_key) {
		String updateTableOISQL = "UPDATE STERLING.YFS_ORDER_INVOICE SET CHARGE_TRANSACTION_KEY='"+charge_transaction_key+"',"
				+ " AMOUNT_COLLECTED='"+amount_collected+"' WHERE ORDER_INVOICE_KEY='"+order_invoice_key+"'";

		return updateTableOISQL;
	}


	private  String updateSQLInvoiceCollection(String charge_transaction_key,String amount_collected,String order_invoice_key) {
		String updateTableICSQL = "UPDATE STERLING.YFS_INVOICE_COLLECTION SET CHARGE_TRANSACTION_KEY='"+charge_transaction_key+"',"
				+ " AMOUNT_COLLECTED='"+amount_collected+"' where ORDER_INVOICE_KEY='"+order_invoice_key+"'";

		return updateTableICSQL;
	}


	private  String insertSQLInvoiceCollection(String charge_transaction_key,String amount_collected,String order_invoice_key) {

		String ts = getTS();
		String primaryKey = getPrimaryKey();
		String insertTableICSQL = "INSERT INTO STERLING.YFS_INVOICE_COLLECTION (INVOICE_COLLECTION_KEY, ORDER_INVOICE_KEY, CHARGE_TRANSACTION_KEY,"
				+ " AMOUNT_COLLECTED, CREATETS, MODIFYTS,"
				+ " CREATEUSERID, MODIFYUSERID, CREATEPROGID, MODIFYPROGID, LOCKID) "
				+ "VALUES('"+primaryKey+"','"+order_invoice_key+"','"+charge_transaction_key+"',"
				+ " '"+amount_collected+"',"+ts+", "+ts+","
				+ "'"+STERLING_USERID+"','"+STERLING_USERID+"', 'Console', 'Console', 0)";

		return insertTableICSQL;
	}



	private  String getTS() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.000000");
		Calendar calendar = Calendar.getInstance();
		Date from_date = calendar.getTime();
		String timeStamp =sdf.format(from_date);

		StringBuffer sb = new StringBuffer(); 
		sb.append("TIMESTAMP ");
		sb.append("'"+timeStamp+"'");

		return sb.toString();
	}

	private  String getPrimaryKey() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Calendar calendar = Calendar.getInstance();
		Date from_date = calendar.getTime();
		printDebugLogs(sdf.format(from_date));
		String timeStamp =sdf.format(from_date);

		return timeStamp;
	}

	private  Connection getDBConnection() {
		Connection dbConnection = null;
		try {
			Class.forName(DB_DRIVER);
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
		try {

			String DB_USER=YFSSystem.getProperty("VSI_DATABASE_USER_NAME");
			String DB_PASSWORD=YFSSystem.getProperty("VSI_DATABASE_PASSWORD");
			String DB_CONNECTION=YFSSystem.getProperty("VSI_DATABASE_CONNECTION_DETAILS");
			
			dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER,
					DB_PASSWORD);

			return dbConnection;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return dbConnection;
	}

	private void printDebugLogs(String message) {
		if(log.isDebugEnabled()){
			log.debug(message);
		}

		System.out.println(message);
	}


	private Document createInputRemoveHold(String sOrderHeaderKey) throws ParserConfigurationException {

		Document changeOrderDoc = XMLUtil.createDocument("Order");

		Element elechangeOrderDoc = changeOrderDoc.getDocumentElement();
		elechangeOrderDoc.setAttribute("OrderHeaderKey", sOrderHeaderKey);
		elechangeOrderDoc.setAttribute("Action", "MODIFY");
		elechangeOrderDoc.setAttribute("Override", "Y");
		Element OrderHoldTypesElement = SCXmlUtil.createChild(elechangeOrderDoc, "OrderHoldTypes");
		Element OrderHoldTypeElement = SCXmlUtil.createChild(OrderHoldTypesElement, "OrderHoldType");
		OrderHoldTypeElement.setAttribute("Status","1300");
		OrderHoldTypeElement.setAttribute("HoldType","VSI_PAYMENT_HOLD");
		OrderHoldTypesElement.appendChild(OrderHoldTypeElement);

		printDebugLogs(SCXmlUtil.getString(changeOrderDoc));

		return changeOrderDoc;

	}



	private Document createInputRequestCollection(String sOrderHeaderKey) throws ParserConfigurationException {

		Document inputReqCollectioDoc = XMLUtil.createDocument("Order");

		Element elechangeOrderDoc = inputReqCollectioDoc.getDocumentElement();
		elechangeOrderDoc.setAttribute("OrderHeaderKey", sOrderHeaderKey);
		elechangeOrderDoc.setAttribute("IgnoreTransactionDependencies", "Y");

		printDebugLogs(SCXmlUtil.getString(inputReqCollectioDoc));

		
		return inputReqCollectioDoc;
		

	}
	
	private  Document createOutDoc(String OrderNo,String total_amount) throws ParserConfigurationException {
		
        Document eleOutDoc = XMLUtil.createDocument("Order");
		
		Element eleOrderOutDoc =eleOutDoc.getDocumentElement();
		eleOrderOutDoc.setAttribute("OrderNo", OrderNo);
		eleOrderOutDoc.setAttribute("amount_collected", total_amount);
		
		printDebugLogs(SCXmlUtil.getString(eleOutDoc));
		return eleOutDoc;
		
		
	}


}