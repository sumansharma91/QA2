<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:template match="/">
		<s:Envelope xmlns:s="http://www.w3.org/2003/05/soap-envelope"
			xmlns:a="http://www.w3.org/2005/08/addressing">
			<s:Header>
				<a:Action s:mustUnderstand="1">http://epicor.com/retail/CRM/7.0.0/ICrmService/FindCustomers
				</a:Action>
				<a:MessageID>urn:uuid:9c56f1a4-b37e-4f59-972c-8e21e3240da7
				</a:MessageID>
				<a:ReplyTo>
					<a:Address>http://www.w3.org/2005/08/addressing/anonymous
					</a:Address>
				</a:ReplyTo>
				<a:To s:mustUnderstand="1">http://w2k12-webextn1-p/CrmWebService/CrmService.svc
				</a:To>
			</s:Header>
			<s:Body>
				<FindCustomers xmlns="http://epicor.com/retail/CRM/7.0.0/">
					<parameters
						xmlns:b="http://schemas.datacontract.org/2004/07/Epicor.Retail.Crm.CustomerWebService"
						xmlns:i="http://www.w3.org/2001/XMLSchema-instance">
						<b:CustomerNumber>-1</b:CustomerNumber>
						<b:DatabaseGroupID>0</b:DatabaseGroupID>
						<b:LanguageID>-1</b:LanguageID>
						<b:SalesAssociateNumber>9999</b:SalesAssociateNumber>
						<b:StoreNumber>9999</b:StoreNumber>
					</parameters>
					<customerNumber>-1</customerNumber>
					<telephoneNo>
						<xsl:value-of
							select="Customer/CustomerContactList/CustomerContact/@DayPhone" />
					</telephoneNo>
					<postalCode>
						<xsl:value-of
							select="Customer/CustomerContactList/CustomerContact/CustomerAdditionalAddressList/CustomerAdditionalAddress/PersonInfo/@ZipCode" />
					</postalCode>
					<address1 />
					<emailAddress>
						<xsl:value-of
							select="Customer/CustomerContactList/CustomerContact/@EmailID" />
					</emailAddress>
					<firstName>
						<xsl:value-of
							select="Customer/CustomerContactList/CustomerContact/@FirstName" />
					</firstName>
					<lastName>
						<xsl:value-of
							select="Customer/CustomerContactList/CustomerContact/@LastName" />
					</lastName>
					<birthMonth>0</birthMonth>
					<alternateKey1 />
					<alternateKey2 />
				</FindCustomers>
			</s:Body>
		</s:Envelope>
	</xsl:template>
</xsl:stylesheet>