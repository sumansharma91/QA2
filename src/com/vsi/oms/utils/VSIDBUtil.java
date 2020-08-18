package com.vsi.oms.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSConnectionHolder;
import com.yantra.yfs.japi.YFSEnvironment;


/**
 * 
 * This class is used for opening a connection to DB and for generating a sequence.
 * 
 *@author nish.pingle
 * 
 */
public class VSIDBUtil {

	private static YFCLogCategory logger;
	static {
		logger = YFCLogCategory
				.instance(com.vsi.oms.utils.VSIDBUtil.class);
	}


	/**
	 * This utility method will get an active DB connection from the connection
	 * pool. An Exception is thrown if connection cannot be established
	 * 
	 * @param env
	 *            Environment object passed by  Sterling 
	 * @throws Exception
	 *             This exception is thrown whenever system errors that the
	 *             application cannot handle are encountered. The transaction is
	 *             aborted and changes are rolled back.
	 */
	public static Connection getConnectionFromEnvironment(YFSEnvironment env) throws Exception {
		logger.beginTimer("getConnectionFromEnvironment");
		Connection objConnection = null;
		try {
			objConnection = ((YFSConnectionHolder) env).getDBConnection();
		} catch (Exception exception) {
			exception.printStackTrace();
			//VSIGeneralUtils.throwYFSException(VSIConstants.ERROR_CODE_0001, VSIConstants.ERROR_CODE_0001_DESC);
		} finally {
			logger.endTimer("getConnectionFromEnvironment");
		}
		return objConnection;
	}

	/**
	 * This utility method will get the next sequence for the specified DB
	 * sequence.
	 * 
	 * @param env
	 *            Environment object passed by  Sterling 
	 * @param seqName
	 *            The name of the DB sequence number to get the next transaction
	 *            number from.
	 * @throws Exception
	 *             This exception is thrown whenever system errors that the
	 *             application cannot handle are encountered. The transaction is
	 *             aborted and changes are rolled back.
	 */
	public static String getNextSequence(YFSEnvironment env, String seqName) throws Exception {

		logger.beginTimer("getNextSequence");

		String nextSeqNumber = "";
		String query = "";

		query = VSIConstants.SELECT + " " + seqName + " " + VSIConstants.FROM_DUAL;
		
		if(logger.isDebugEnabled()){
			logger.verbose("The value of sQuery is :" + query);
		}
		
		Connection connection;
		ResultSet resultSet = null;
		Statement statement = null;
		try {
			connection = getConnectionFromEnvironment(env);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);

			if (resultSet != null && resultSet.next()) {
				nextSeqNumber = resultSet.getString(VSIConstants.SEQ_ID);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			if (statement != null) {
				statement.close();
			}
			// Connection need not be closed as the System handles the connection.
		}

		if(logger.isDebugEnabled()){
			logger.verbose("The value of nextSeqNumber is :" + nextSeqNumber);
			logger.endTimer("getNextSequence");
		}
		
		return nextSeqNumber;
	}

}
