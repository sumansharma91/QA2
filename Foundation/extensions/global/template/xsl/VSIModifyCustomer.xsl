<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<s:Envelope xmlns:s="http://www.w3.org/2003/05/soap-envelope"
			xmlns:a="http://www.w3.org/2005/08/addressing">
			<s:Header>
				<a:Action s:mustUnderstand="1">http://epicor.com/retail/CRM/7.0.0/ICrmService/Save
				</a:Action>
				<a:MessageID>urn:uuid:e3a05aa5-fa24-4adc-9069-1953e2aced8d
				</a:MessageID>
				<a:ReplyTo>
					<a:Address>http://www.w3.org/2005/08/addressing/anonymous
					</a:Address>
				</a:ReplyTo>
				<a:To s:mustUnderstand="1">http://w2k12-webextn1-p/CrmWebService/CrmService.svc
				</a:To>
			</s:Header>
			<s:Body>
				<Save xmlns="http://epicor.com/retail/CRM/7.0.0/">
					<parameters
						xmlns:b="http://schemas.datacontract.org/2004/07/Epicor.Retail.Crm.CustomerWebService"
						xmlns:i="http://www.w3.org/2001/XMLSchema-instance">
						<b:CustomerNumber>
							<xsl:value-of select="Customer/@CustomerKey" />
						</b:CustomerNumber>
						<b:DatabaseGroupID>1</b:DatabaseGroupID>
						<b:LanguageID>1033</b:LanguageID>
						<b:SalesAssociateNumber>9999</b:SalesAssociateNumber>
						<b:StoreNumber>9999</b:StoreNumber>
					</parameters>
					<checkDuplicates>false</checkDuplicates>
					<xsl:if test="not(/Customer/@ExcludeCustomer ='Y')">
						<customer
							xmlns:b="http://schemas.datacontract.org/2004/07/Epicor.Retail.Crm.CustomerWebService"
							xmlns:i="http://www.w3.org/2001/XMLSchema-instance">
							<b:BirthDate>2002-01-01T00:00:00</b:BirthDate>
							<b:CustomerDivisions>
								<b:CustomerDivisionWCF>
									<b:DivisionID>2</b:DivisionID>
									<b:PrimaryAddressID>1</b:PrimaryAddressID>
									<b:PrimaryEmailID>1</b:PrimaryEmailID>
									<xsl:if
										test="/Customer/CustomerContactList/CustomerContact/@DayPhone !=''">
										<b:PrimaryPhoneID>1</b:PrimaryPhoneID>
									</xsl:if>
									<b:RecordState>Modified</b:RecordState>
									<b:SalesAssociateNumber>111111</b:SalesAssociateNumber>
									<b:StoreNumber>11</b:StoreNumber>
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
							<b:NextAddressID>2</b:NextAddressID>
							<b:Number>
								<xsl:value-of select="Customer/@CustomerKey" />
							</b:Number>
							<b:OriginalDivisionID>2</b:OriginalDivisionID>
							<b:RecordState>Modified</b:RecordState>
							<b:StatusCode>A</b:StatusCode>
						</customer>
						<customerAddresses
							xmlns:b="http://schemas.datacontract.org/2004/07/Epicor.Retail.Crm.CustomerWebService"
							xmlns:i="http://www.w3.org/2001/XMLSchema-instance">
							<b:CustomerNumber>
								<xsl:value-of select="Customer/@CustomerKey" />
							</b:CustomerNumber>
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
												<b:RecordState>Modified</b:RecordState>
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
										<b:RecordState>Modified</b:RecordState>
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
							<b:CustomerNumber>
								<xsl:value-of select="Customer/@CustomerKey" />
							</b:CustomerNumber>
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
												<b:RecordState>Modified</b:RecordState>
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
										<b:RecordState>Modified</b:RecordState>
									</b:CustomerPhoneWCF>
								</xsl:if>
							</b:Items>
						</customerPhones>
						<customerEmails
							xmlns:b="http://schemas.datacontract.org/2004/07/Epicor.Retail.Crm.CustomerWebService"
							xmlns:i="http://www.w3.org/2001/XMLSchema-instance">
							<b:CustomerNumber>
								<xsl:value-of select="Customer/@CustomerKey" />
							</b:CustomerNumber>
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
											<b:RecordState>Modified</b:RecordState>
										</b:CustomerEmailDivisionWCF>
									</b:EmailDivisions>
									<b:EmailIndicatorID>1</b:EmailIndicatorID>
									<b:EmailTypeCode>PERS</b:EmailTypeCode>
									<b:ID>1</b:ID>
									<b:RecordState>Modified</b:RecordState>
								</b:CustomerEmailWCF>
							</b:Items>
						</customerEmails>
					</xsl:if>
					<xsl:if test="Customer/@HasCustomerAttributes='Y'">
						<customerAttributes
							xmlns:b="http://schemas.datacontract.org/2004/07/Epicor.Retail.Crm.CustomerWebService"
							xmlns:i="http://www.w3.org/2001/XMLSchema-instance">
							<b:Items>
								<xsl:if test="/Customer/@SavePayment='Y'">
									<xsl:for-each
										select="Customer/CustomerPaymentMethodList/CustomerPaymentMethod">
										<b:CustomerAttributeWCF>
											<b:Code>
												<xsl:value-of select="@Code" />
											</b:Code>
											<b:Comment>
												<xsl:value-of select="@CreditCardNo" />:<xsl:value-of select="@CreditCardType" />
											</b:Comment>
											<b:Date>
												<xsl:value-of select="@CreditCardCreatets" />
											</b:Date>
											<b:DivisionID>-1</b:DivisionID>
											<b:GroupCode>TKNV</b:GroupCode>
											<b:OriginalCode>
												<xsl:value-of select="@Code" />
											</b:OriginalCode>
											<b:OriginalGroupCode>TKNV</b:OriginalGroupCode>
											<b:RecordState>
												<xsl:value-of select="@RecordState" />
											</b:RecordState>
											<b:Value>
												<xsl:value-of select="@CreditCardExpDate" />
											</b:Value>
										</b:CustomerAttributeWCF>
									</xsl:for-each>
								</xsl:if>
								<xsl:if test="/Customer/@SetPreferredCarrier='Y'">
									<b:CustomerAttributeWCF>
										<b:Code>
											<xsl:value-of select="Customer/Extn/@ExtnPreferredCarrier" />
										</b:Code>
										<b:Comment></b:Comment>
										<b:Date>1900-01-01T00:00:00</b:Date>
										<b:DivisionID>-1</b:DivisionID>
										<b:GroupCode>PFCR</b:GroupCode>
										<b:OriginalCode>
											<xsl:value-of select="Customer/Extn/@ExtnOriginalPreferredCarrier" />
										</b:OriginalCode>
										<b:OriginalGroupCode>PFCR</b:OriginalGroupCode>
										<b:RecordState>
											<xsl:value-of select="/Customer/@CarrierRecordState" />
										</b:RecordState>
										<b:Value>0</b:Value>
									</b:CustomerAttributeWCF>
								</xsl:if>
								<xsl:if test="/Customer/@SetTaxExemptionCode='Y'">
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
										<b:RecordState>
											<xsl:value-of select="/Customer/@TaxExemptionRecordState" />
										</b:RecordState>
										<b:Value>0</b:Value>
									</b:CustomerAttributeWCF>
								</xsl:if>
							</b:Items>
							<b:CustomerNumber>
								<xsl:value-of select="Customer/@CustomerKey" />
							</b:CustomerNumber>
						</customerAttributes>
					</xsl:if>
					<xsl:if test="Customer/@HasCustomerRemark='Y'">
						<customer/>
						<customerAddresses/>
						<customerPhones/>
						<customerEmails/>
						<customerRemarks
							xmlns:b="http://schemas.datacontract.org/2004/07/Epicor.Retail.Crm.CustomerWebService"
							xmlns:i="http://www.w3.org/2001/XMLSchema-instance">
							<b:CustomerNumber>
								<xsl:value-of select="Customer/@CustomerKey" />
							</b:CustomerNumber>
							<b:Items>
								<b:CustomerRemarkWCF>
									<b:CreateDate>1900-01-01T00:00:00</b:CreateDate>
									<b:CreateUser>9999</b:CreateUser>
									<b:DivisionID>2</b:DivisionID>
									<b:ModifyDate>1900-01-01T00:00:00</b:ModifyDate>
									<b:ModifyUser>9001</b:ModifyUser>
									<b:RecordState>
										<xsl:value-of select="Customer/@NotesRecordState" />
									</b:RecordState>
									<b:Remark>
										<xsl:value-of select="Customer/NoteList/Note/@NoteText" />
									</b:Remark>
									<xsl:choose>
										<xsl:when test="Customer/NoteList/Note/@NoteID != ''">
											<b:RemarkID><xsl:value-of select="Customer/NoteList/Note/@NoteID"/></b:RemarkID>
										</xsl:when>
										<xsl:otherwise>
											<b:RemarkID>-1</b:RemarkID>
										</xsl:otherwise>
									</xsl:choose>
									<b:Subject>test</b:Subject>
								</b:CustomerRemarkWCF>
							</b:Items>
						</customerRemarks>
						<customerAlternateKeys/>
						<customerAttributes/>
						<customerSegmentation/>
						<customerContacts/>
						<customerLoyalties/>
					</xsl:if>
				</Save>
			</s:Body>
		</s:Envelope>
	</xsl:template>
</xsl:stylesheet>
