package com.vsi.oms.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.yantra.yfc.log.YFCCallingProgLogRegistry;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCException;

/**
 * 
 * This is a general Utility class.
 * @author nish.pingle
 * 
 */
public class VSIGeneralUtils {

	// Log object
	@SuppressWarnings("unused")
	private static YFCLogCategory cat;
	static {
		cat = YFCLogCategory.instance(com.vsi.oms.utils.VSIGeneralUtils.class);
	}

	/**
	 * This method will throw YFCException with the Error Code and Description
	 * as passed
	 * 
	 * @param errorCode
	 *            Error Code string for the exception
	 * @param errorCodeDesc
	 *            Error description string for the exception
	 * @throws Exception
	 *             This exception is thrown whenever system errors that the
	 *             application cannot handle are encountered. The transaction is
	 *             aborted and changes are rolled back.
	 */
	public static void throwYFSException(String errorCode, String errorCodeDesc) {
		// Exception type is YFCException
		YFCException yfe = new YFCException();
		yfe.setAttribute(VSIConstants.ATTR_ERROR_CODE, errorCode);
		yfe.setAttribute(VSIConstants.ATTR_ERROR_DESC, errorCodeDesc);
		throw yfe;
	}

	/**
	 * This method will throw YFCException with the Error Code and Description
	 * as passed
	 * 
	 * @param errorCode
	 *            Error Code string for the exception
	 * @param errorCodeDesc
	 *            Error description string for the exception
	 * @param errorMessage
	 *            Complete Error message
	 * @throws Exception
	 *             This exception is thrown whenever system errors that the
	 *             application cannot handle are encountered. The transaction is
	 *             aborted and changes are rolled back.
	 */
	public static void throwYFSException(String errorCode,
			String errorCodeDesc, String errorMessage) {
		// Exception type is YFCException
		YFCException yfe = new YFCException();
		yfe.setAttribute(VSIConstants.ATTR_ERROR_CODE, errorCode);
		yfe.setAttribute(VSIConstants.ATTR_ERROR_DESC, errorCodeDesc);
		yfe.setAttribute(VSIConstants.ATTR_ERROR_MESSAGE, errorMessage);
		throw yfe;
	}
	/**
	 * Method to create XPath based on a condition
	 * @param String strXPath1
	 * 		  String strConditionAttribute1
	 * 		  String strConditionValue1
	 * @return String - XPath formed
	 */
	public static String formXPATHWithOneCondition(String strXPath1, String strConditionAttribute1,
			String strConditionValue1) {

		StringBuffer sbFinalXPath = new StringBuffer(strXPath1);
		sbFinalXPath.append("[@").append(strConditionAttribute1).append("='").append(strConditionValue1).append("']");
		
		return sbFinalXPath.toString();
	}
	
	/**
	 * Method to Round off the BigDecimal value into two decimal places
	 * This method rounds off 0.3333333 as 0.33 and 0.66666667 as 0.66
	 * @param Float floatValue - Before Round-off
	 * @return Float floatValue - After Round-off
	 */
	public static BigDecimal roundOffBigDecimal(String stringValue) {

		return new BigDecimal(stringValue).setScale(2, RoundingMode.HALF_EVEN);
	}

	/**
	 * Method to calculate the product of two BigDecimal values
	 * @param BigDecimal bdValue1
	 * 		  BigDecimal bdValue2
	 * @return BigDecimal - Product of the two BigDecimal values as input
	 */
	public static BigDecimal bigDecimalMultiply(BigDecimal bdValue1, BigDecimal bdValue2) {

		return bdValue1.multiply(bdValue2);
	}

	/**
	 * Method to divide two BigDecimal values with RoundingMode as DOWN
	 * @param BigDecimal bdValue1
	 * 		  BigDecimal bdValue2
	 * @return BigDecimal - Product of the two BigDecimal values as input
	 */
	public static BigDecimal bigDecimalDivide(BigDecimal bdValue1, BigDecimal bdValue2) {

		return bdValue1.divide(bdValue2, 2, RoundingMode.DOWN);
	}
	
	
	 /**
	    * Converts date object to date-time string in
	    * default date format
	    *
	    * @param inputDate Date object to be converted
	    * @return          Date-time string in default date format
	    * @throws IllegalArgumentException for Invalid input
	    * @throws Exception for all others
	    * @see getDefaultDateFormat
	    */
	    public static String convertDate(java.util.Date inputDate)
	        throws IllegalArgumentException, Exception
	    {
	        return formatDate(inputDate, getDefaultDateFormat());
	    }
	    
	    /**
	     * Returns default date-time string format i.e.
	     * <code>yyyyMMdd'T'HH:mm:ss</code>
	     * @return Default date-time string i.e. yyyyMMdd'T'HH:mm:ss
	     */
	     protected static String getDefaultDateFormat()
	     {
	         //Yantra default date-time string format
	         return "yyyyMMdd'T'HH:mm:ss";
	     }
	     
	     /**
	      * Converts date object to date-time string
	      * @param inputDate Date object to be converted
	      * @param outputFormat Output format.
	      * Refer to <code>java.text.SimpleDateFormat</code> for date format
	      * codes
	      * @return          Formatted date-time string
	      * @throws IllegalArgumentException for Invalid input
	      * @throws Exception for all others
	      */
	      public static String formatDate(
	          java.util.Date inputDate,
	          String outputFormat)
	          throws IllegalArgumentException, Exception
	      {
	          //Validate input date value
	          if (inputDate == null)
	          {
	              throw new IllegalArgumentException("Input date cannot "
	                      + " be null in DateUtils.formatDate method");
	          }

	          //Validate output date format
	          if (outputFormat == null)
	          {
	              throw new IllegalArgumentException("Output format cannot"
	                      + " be null in DateUtils.formatDate method");
	          }

	          //Apply formatting
	          SimpleDateFormat formatter = new SimpleDateFormat(outputFormat);
	          return formatter.format(inputDate);
	      }
	      
	      /**
	       * Converts date-time string to Date object.
	       * Date-time string should be in default date format
	       *
	       * @param inputDate Date-time string to be converted
	       * @return          Equivalent date object to input string
	       * @throws IllegalArgumentException for Invalid input
	       * @throws Exception for all others
	       */
	       public static java.util.Date convertDate(String inputDate)
	           throws IllegalArgumentException, Exception
	       {

	           //Validate input date value
	           if (inputDate == null)
	           {
	               throw new IllegalArgumentException("Input date cannot "
	                       + " be null in DateUtils.convertDate method");
	           }
	           if (inputDate.indexOf("T") != -1 && inputDate.indexOf("-") == -1)
	           {
	               return convertDate(inputDate, getDefaultDateFormat());
	           }
	           else if(inputDate.indexOf("T") != -1 && inputDate.indexOf("-") != -1)
	           {

	   			return convertDate(inputDate, getDefaultDateFormatISO());
	           }
	           else
	           {

	               return convertDate(inputDate, getShortDefaultDateFormat());
	           }
	       }
	       
	       
	       /**
	        * Returns short default date string format i.e.
	        * <code>yyyyMMdd</code>
	        */
	        protected static String getShortDefaultDateFormat()
	        {
	            //Yantra short default date string format
	            return "yyyyMMdd";
	        }
	       
	       /**
	   	* Returns default date-time string format i.e.
	   	* <code>yyyyMMdd'T'HH:mm:ss</code>
	   	* @return Default date-time string i.e. yyyyMMdd'T'HH:mm:ss
	   	*/
	   	protected static String getDefaultDateFormatISO()
	   	{
	   		//Yantra default date-time string format
	   		return "yyyy-MM-dd'T'HH:mm:ss";
	   	}
	       
	       /**
	        * Converts date-time string to Date object
	        *
	        * @param inputDate Date-time string to be converted
	        * @param inputDateFormat Format of date-time string.
	        * Refer to <code>java.util.SimpleDateFormat</code> for date
	        * format codes
	        * @return          Equivalent date object to input string
	        * @throws IllegalArgumentException for Invalid input
	        * @throws Exception for all others
	        */
	        public static java.util.Date convertDate(
	            String inputDate,
	            String inputDateFormat)
	            throws IllegalArgumentException, Exception
	        {
	            //Validate Input Date value
	            if (inputDate == null)
	            {
	                throw new IllegalArgumentException(
	                    "Input date cannot be null"
	                        + " in DateUtils.convertDate method");
	            }

	            //Validate Input Date format
	            if (inputDateFormat == null)
	            {
	                throw new IllegalArgumentException(
	                    "Input date format cannot"
	                        + " be null in DateUtils.convertDate method");
	            }

	    		//Apply formatting
	            SimpleDateFormat formatter =
	                new SimpleDateFormat(inputDateFormat);

	            ParsePosition position = new ParsePosition(0);
	            return formatter.parse(inputDate, position);
	        }

	        /**
		        * Adds all the APIs/AGENTS/Services to map.
		        * Used to Identify calling programs.
		        */
	        
	        public static boolean identifyCallingProgram(String sTransactionName){
	    		
	   		 ArrayList<String> alstAPIs = (ArrayList<String>)YFCCallingProgLogRegistry.getLogRegistryListForType("API");
	   			if ((alstAPIs != null) && alstAPIs.contains(sTransactionName)) {
	   			    return true;
	   			}

	   			ArrayList<String> alstAgents = (ArrayList<String>)YFCCallingProgLogRegistry.getLogRegistryListForType("AGENTS");
	   			if ((alstAgents != null) && alstAgents.contains(sTransactionName)) {
	   			    return true;
	   			}
	   			ArrayList<String> alstFlows = (ArrayList<String>)YFCCallingProgLogRegistry.getLogRegistryListForType("FLOWS");
	   			if ((alstFlows != null) && alstFlows.contains(sTransactionName)) {
	   			    return true;
	   			}
	   			return false;

	   		    }
	   	
	
}	
