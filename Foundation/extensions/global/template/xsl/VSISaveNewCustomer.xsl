<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<s:Envelope xmlns:s="http://www.w3.org/2003/05/soap-envelope"
			xmlns:a="http://www.w3.org/2005/08/addressing">
			<s:Header>
				<a:Action s:mustUnderstand="1">http://epicor.com/retail/CRM/7.0.0/ICrmService/SaveNewCustomer
				</a:Action>
				<a:MessageID>urn:uuid:46ac1d14-104e-443b-87ac-389ce182aca8
				</a:MessageID>
				<a:ReplyTo>
					<a:Address>http://www.w3.org/2005/08/addressing/anonymous
					</a:Address>
				</a:ReplyTo>
				<a:To s:mustUnderstand="1">http://w2k12-webextn1-p/CrmWebService/CrmService.svc
				</a:To>
			</s:Header>
			<s:Body>
				<SaveNewCustomer xmlns="http://epicor.com/retail/CRM/7.0.0/">
					<parameters
						xmlns:b="http://schemas.datacontract.org/2004/07/Epicor.Retail.Crm.CustomerWebService"
						xmlns:i="http://www.w3.org/2001/XMLSchema-instance">
						<b:CustomerNumber>-1</b:CustomerNumber>
						<b:DatabaseGroupID>1</b:DatabaseGroupID>
						<b:LanguageID>1033</b:LanguageID>
						<b:SalesAssociateNumber>9999</b:SalesAssociateNumber>
						<b:StoreNumber>9999</b:StoreNumber>
					</parameters>
					<checkDuplicates>false</checkDuplicates>
					<customer
						xmlns:b="http://schemas.datacontract.org/2004/07/Epicor.Retail.Crm.CustomerWebService"
						xmlns:i="http://www.w3.org/2001/XMLSchema-instance">
						<b:BirthDate>1900-01-01T00:00:00</b:BirthDate>
						<b:CreatedSource>VS012</b:CreatedSource>
						<b:CustomerDivisions>
							<b:CustomerDivisionWCF>
								<b:CreatedSource>VS012</b:CreatedSource>
								<b:DivisionID>2</b:DivisionID>
								<b:PrimaryAddressID>1</b:PrimaryAddressID>
								<b:PrimaryEmailID>1</b:PrimaryEmailID>
								<xsl:if
									test="/Customer/CustomerContactList/CustomerContact/@DayPhone !=''">
									<b:PrimaryPhoneID>1</b:PrimaryPhoneID>
								</xsl:if>
								<b:RecordState>Added</b:RecordState>
								<b:SalesAssociateNumber>9999</b:SalesAssociateNumber>
								<b:StoreNumber>9999</b:StoreNumber>
							</b:CustomerDivisionWCF>
						</b:CustomerDivisions>
						<b:DistributionStatusCode>0</b:DistributionStatusCode>
						<b:FirstName>
							<xsl:value-of
								select="Customer/CustomerContactList/CustomerContact/@FirstName" />
						</b:FirstName>
						<xsl:choose>
							<xsl:when
								test="Customer/CustomerContactList/CustomerContact/@ExtnGender='Male'">
								<b:Gender>M</b:Gender>
							</xsl:when>
							<xsl:when
								test="Customer/CustomerContactList/CustomerContact/@ExtnGender='Female'">
								<b:Gender>F</b:Gender>
							</xsl:when>
							<xsl:otherwise>
								<b:Gender>U</b:Gender>
							</xsl:otherwise>
						</xsl:choose>
						<b:LanguageCode>ENG</b:LanguageCode>
						<b:LastName>
							<xsl:value-of
								select="Customer/CustomerContactList/CustomerContact/@LastName" />
						</b:LastName>
						<b:MaritalStatus>U</b:MaritalStatus>
						<b:Number>-1</b:Number>
						<b:OriginalDivisionID>2</b:OriginalDivisionID>
						<b:RecordState>Added</b:RecordState>
						<b:StatusCode>A</b:StatusCode>
					</customer>
					<customerAddresses
						xmlns:b="http://schemas.datacontract.org/2004/07/Epicor.Retail.Crm.CustomerWebService"
						xmlns:i="http://www.w3.org/2001/XMLSchema-instance">
						<b:CustomerNumber>-1</b:CustomerNumber>
						<b:Items>
							<xsl:for-each
								select="Customer/CustomerContactList/CustomerContact/CustomerAdditionalAddressList/CustomerAdditionalAddress">
								<b:CustomerAddressWCF>
									<b:Address1>
										<xsl:value-of select="PersonInfo/@AddressLine1" />
									</b:Address1>
									<b:Address2>
										<xsl:value-of select="PersonInfo/@AddressLine2" />
									</b:Address2>
									<b:Address5 />
									<b:Address6 />
									<b:AddressDivisions>
										<b:CustomerAddressDivisionWCF>
											<b:AddressID>1</b:AddressID>
											<b:DivisionID>2</b:DivisionID>
											<xsl:choose>
												<xsl:when test="/Customer/@PostalOptIn='N'">
													<b:MailOptInFlagCode>2</b:MailOptInFlagCode>
												</xsl:when>
												<xsl:when test="/Customer/@PostalOptIn='Y'">
													<b:MailOptInFlagCode>1</b:MailOptInFlagCode>
												</xsl:when>
												<xsl:otherwise>
													<b:MailOptInFlagCode>1</b:MailOptInFlagCode>
												</xsl:otherwise>
											</xsl:choose>
											<b:RecordState>Added</b:RecordState>
										</b:CustomerAddressDivisionWCF>
									</b:AddressDivisions>
									<b:AddressTypeCode>
										<xsl:value-of select="@AddressTypeCode" />
									</b:AddressTypeCode>
									<b:City>
										<xsl:value-of select="PersonInfo/@City" />
									</b:City>
									<b:CountryCode>
										<xsl:value-of select="PersonInfo/@Country" />
									</b:CountryCode>
									<b:ID>
										<xsl:value-of select="PersonInfo/@ID" />
									</b:ID>
									<b:MailIndicatorID>0</b:MailIndicatorID>
									<b:PostalCode>
										<xsl:value-of select="PersonInfo/@ZipCode" />
									</b:PostalCode>
									<b:RecordState>Added</b:RecordState>
									<b:State>
										<xsl:value-of select="PersonInfo/@State" />
									</b:State>
								</b:CustomerAddressWCF>
							</xsl:for-each>
						</b:Items>
					</customerAddresses>
					<customerPhones
						xmlns:b="http://schemas.datacontract.org/2004/07/Epicor.Retail.Crm.CustomerWebService"
						xmlns:i="http://www.w3.org/2001/XMLSchema-instance">
						<b:CustomerNumber>-1</b:CustomerNumber>
						<b:Items>
							<xsl:if
								test="/Customer/CustomerContactList/CustomerContact/@DayPhone !=''">
								<b:CustomerPhoneWCF>
									<b:CountryCode>USA</b:CountryCode>
									<b:ID>1</b:ID>
									<b:PhoneDivisions>
										<b:CustomerPhoneDivisionWCF>
											<b:DivisionID>2</b:DivisionID>
											<xsl:choose>
												<xsl:when test="/Customer/@PhoneOptIn='N'">
													<b:PhoneOptInFlagCode>2</b:PhoneOptInFlagCode>
												</xsl:when>
												<xsl:when test="/Customer/@PhoneOptIn='Y'">
													<b:PhoneOptInFlagCode>1</b:PhoneOptInFlagCode>
												</xsl:when>
												<xsl:otherwise>
													<b:PhoneOptInFlagCode>1</b:PhoneOptInFlagCode>
												</xsl:otherwise>
											</xsl:choose>
											<b:RecordState>Added</b:RecordState>
											<b:TextOptInFlagCode>0</b:TextOptInFlagCode>
										</b:CustomerPhoneDivisionWCF>
									</b:PhoneDivisions>
									<b:PhoneExtensionNumber />
									<b:PhoneIndicatorID>9</b:PhoneIndicatorID>
									<b:PhoneNumber>
										<xsl:value-of
											select="/Customer/CustomerContactList/CustomerContact/@DayPhone" />
									</b:PhoneNumber>
									<b:PhoneTypeCode>HOME</b:PhoneTypeCode>
									<b:RecordState>Added</b:RecordState>
								</b:CustomerPhoneWCF>
							</xsl:if>
						</b:Items>
					</customerPhones>
					<customerEmails
						xmlns:b="http://schemas.datacontract.org/2004/07/Epicor.Retail.Crm.CustomerWebService"
						xmlns:i="http://www.w3.org/2001/XMLSchema-instance">
						<b:CustomerNumber>-1</b:CustomerNumber>
						<b:Items>
							<b:CustomerEmailWCF>
								<b:EmailAddress>
									<xsl:value-of
										select="Customer/CustomerContactList/CustomerContact/@EmailID" />
								</b:EmailAddress>
								<b:EmailDivisions>
									<b:CustomerEmailDivisionWCF>
										<b:DivisionID>2</b:DivisionID>
										<b:EmailID>1</b:EmailID>
										<xsl:choose>
											<xsl:when test="/Customer/@EmailOptIn='N'">
												<b:EmailOptInFlagCode>2</b:EmailOptInFlagCode>
											</xsl:when>
											<xsl:when test="/Customer/@EmailOptIn='Y'">
												<b:EmailOptInFlagCode>1</b:EmailOptInFlagCode>
											</xsl:when>
											<xsl:otherwise>
												<b:EmailOptInFlagCode>1</b:EmailOptInFlagCode>
											</xsl:otherwise>
										</xsl:choose>
										<b:RecordState>Added</b:RecordState>
									</b:CustomerEmailDivisionWCF>
								</b:EmailDivisions>
								<b:EmailIndicatorID>1</b:EmailIndicatorID>
								<b:EmailTypeCode>PERS</b:EmailTypeCode>
								<b:ID>1</b:ID>
								<b:RecordState>Added</b:RecordState>
							</b:CustomerEmailWCF>
						</b:Items>
					</customerEmails>
					<customerAttributes
						xmlns:b="http://schemas.datacontract.org/2004/07/Epicor.Retail.Crm.CustomerWebService"
						xmlns:i="http://www.w3.org/2001/XMLSchema-instance">
						<b:CustomerNumber>-1</b:CustomerNumber>
						<b:Items>
							<xsl:if test="Customer/Extn/@ExtnTaxExemptionCode !=''">
								<b:CustomerAttributeWCF>
									<b:Code>TXEP</b:Code>
									<b:Comment>
										<xsl:value-of select="Customer/Extn/@ExtnTaxExemptionCode" />
									</b:Comment>
									<b:Date>1900-01-01T00:00:00</b:Date>
									<b:DivisionID>-1</b:DivisionID>
									<b:GroupCode>TXEP</b:GroupCode>
									<b:OriginalCode>TXEP</b:OriginalCode>
									<b:OriginalGroupCode>TXEP</b:OriginalGroupCode>
									<b:RecordState>Added</b:RecordState>
									<b:Value>0</b:Value>
								</b:CustomerAttributeWCF>
							</xsl:if>
						</b:Items>
					</customerAttributes>
				</SaveNewCustomer>
			</s:Body>
		</s:Envelope>
	</xsl:template>
</xsl:stylesheet>