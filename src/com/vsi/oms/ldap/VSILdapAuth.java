/*******************************************************************************
 * (C) Copyright  2013 Vitamin Shoppe Inc
 * Author: Harshit Arora
 *******************************************************************************/
package com.vsi.oms.ldap;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import com.vsi.oms.utils.VSIConstants;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.util.YFSAuthenticator;

/**
 * This class provides a sample of how to implement LDAP V2 authentication using
 * JNDI.
 */

public class VSILdapAuth implements YFSAuthenticator, VSIConstants {

	String vsiFirstName = null;
	String vsiLastName = null;
	String vsiFullName = null;

	private YFCLogCategory log = YFCLogCategory.instance(VSILdapAuth.class);

	/*
	 * For authenticating user "u1" with password "p1" perform the following
	 * steps.
	 * 
	 * 1.Use admin credentials to connect to the LDAP server. 2.Then search for
	 * the presense of the actual user, "u1" along with his password "p1", under
	 * the path (This varies based on the DB) 3.We pass the user id and this
	 * code splits the user id into first name and last name and send it to ldap
	 * for authentication based on thier full name 4.If the search returns
	 * results then the password p1 for user u1 is valid. 5. Else wrong userid
	 * or password.
	 */

	int count = 1;

	public Map authenticate(String sLoginID, String sPassword)
			throws AuthenticationException, Exception {
				
		boolean numeric = true;
				
        numeric = sLoginID.matches("-?\\d+(\\.\\d+)?");

        if(numeric){
			log.verbose("Sore User");
		}
        else{
    

		YFSException yfe = new YFSException();
		String ldapFactory = "com.sun.jndi.ldap.LdapCtxFactory";
		Hashtable<String, String> env = new Hashtable<String, String>();
		String sLdapURL = "";

		if (count == 1) {
			sLdapURL = YFSSystem.getProperty("LDAP_URL1");
		} else if (count == 2) {
			sLdapURL = YFSSystem.getProperty("LDAP_URL2");
		} else if (count == 3) {
			sLdapURL = YFSSystem.getProperty("LDAP_URL3");
		}
		String strRegex = "/dc=VSI-NJ,dc=VITSHOPPE,dc=com";
		String sLdapDcUrl = sLdapURL + strRegex;
		if(log.isDebugEnabled()){
			log.verbose("Trying to authenticate with LDAP");
		}
		
		if (YFCObject.isVoid(sPassword)) {
			yfe.setErrorCode("EXTN_00001");
			yfe.setErrorDescription("Authentication Failure");
			throw yfe;
		}

		// Splitting the User Id into first name adn last name
		try {
			env.put(Context.INITIAL_CONTEXT_FACTORY, ldapFactory);

			String[] vsiLoginId = sLoginID.split("\\.");
			if (vsiLoginId.length > 1) {
				vsiFirstName = vsiLoginId[0];
				vsiLastName = vsiLoginId[1];
				vsiFullName = vsiFirstName + " " + vsiLastName;
			} else {
				vsiFullName = sLoginID;
			}
			// setting the environment variable for connection to LDAP
			env.put(Context.SECURITY_AUTHENTICATION, ATTR_SIMPLE);
			env.put(Context.PROVIDER_URL, sLdapDcUrl);
			env.put(Context.SECURITY_PRINCIPAL, vsiFullName);
			env.put(Context.SECURITY_CREDENTIALS, sPassword);
			DirContext ctx = new InitialDirContext(env);
			ctx.close();
		} catch (Exception ex) {
			log.error("ERROR:: " + ex);
			if (ex instanceof AuthenticationException) {
				log.error("Error: " + ex);
				yfe.setErrorCode("EXTN_00001");
				yfe.setErrorDescription("Authentication Failure");
				throw yfe;
			} else if (ex instanceof SocketTimeoutException
					|| ex instanceof RemoteException
					|| ex instanceof CommunicationException
					|| ex instanceof ConnectException) {

				log.error("count:::: " + count);
				if (count >= 3) {
					yfe.setErrorCode("EXTN_00001");
					yfe.setErrorDescription("Authentication Failure");
					throw yfe;
				} else {
					count++;
					authenticate(sLoginID, sPassword);
				}
			} else {
				log.error("ERROR: " + ex);
				yfe.setErrorCode("EXTN_00001");
				yfe.setErrorDescription("Authentication Failure");
				throw yfe;
			}
		}
		}

		return null;
	}
}
