<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
<soap:Body>

<Save xmlns="http://NsbGroup.com/webservices/">
<parameters>
<DatabaseGroupID>200</DatabaseGroupID>
<SalesAssociateNumber>999999</SalesAssociateNumber>
<StoreNumber>999</StoreNumber>
<CustomerNumber>-1</CustomerNumber>    
<LanguageID>-1</LanguageID>
</parameters>
<checkDuplicates>true</checkDuplicates>
<customer>
<RecordState>Modified</RecordState>
<ID>-1</ID>
<Number><xsl:value-of select = "Order/@BillToID" /></Number>
<OriginalNumber>-1</OriginalNumber>
<TitleCode />
<FirstName><xsl:value-of select = "Order/PersonInfoBillTo/@FirstName" /></FirstName>
<LastName><xsl:value-of select = "Order/PersonInfoBillTo/@LastName" /></LastName>
<Gender>U</Gender>
<MaritalStatus>U</MaritalStatus>
<StoreNumber>999</StoreNumber>
<LanguageCode>ENG</LanguageCode>

<CreateSource />
<DistributionStatusCode>0</DistributionStatusCode>
<DistributionStatus>
<RecordState>Modified</RecordState>
<Code>0</Code>
<Description>Distribute</Description>
<ShortDescription>Distribute</ShortDescription>
<SystemCode>1000</SystemCode>
</DistributionStatus>
<StatusCode>A</StatusCode>
<Status>
<RecordState>Modified</RecordState>
<Code>A</Code>
<Description>Active</Description>
<ShortDescription>Active</ShortDescription>
<SystemCode>1001</SystemCode>
</Status>
<CreateDate>1900-01-01T00:00:00</CreateDate>
<LastUpdateDate>1900-01-01T00:00:00</LastUpdateDate>
<SalesAssociateNumber>999999</SalesAssociateNumber>
<NumberMailings>0</NumberMailings>
<LastMailingDate>1900-01-01T00:00:00</LastMailingDate>
<MembershipDate>2012-04-17T00:00:00</MembershipDate>
<MembershipTypeCode>FBP </MembershipTypeCode>

<EmailAddress><xsl:value-of select = "Order/@CustomerEMailID" /></EmailAddress>
<Salutation />
<EmailIndicatorID>9</EmailIndicatorID>
<EmailOptInFlagCode>1</EmailOptInFlagCode>
<EmailOptInFlag>
<RecordState>Modified</RecordState>
<Code>0</Code>
<Description>Unknown</Description>
<ShortDescription>Unknown</ShortDescription>
<SystemCode>1001</SystemCode>
</EmailOptInFlag>
<EmailOptInDate>1900-01-01T00:00:00</EmailOptInDate>

<EmailIndicator>
<RecordState>Modified</RecordState>
<ID>9</ID>
<Description>Mail</Description>
<ShortDescription>MAIL</ShortDescription>
<MailIndicatorFlag>true</MailIndicatorFlag>
<EmailIndicatorFlag>true</EmailIndicatorFlag>
<PhoneIndicatorFlag>true</PhoneIndicatorFlag>
</EmailIndicator>
<HouseholdCode />
<OriginalHouseholdCode />
<HeadOfHousehold>false</HeadOfHousehold>
</customer>
<customerAddresses>
<Items>
<CustomerAddressWS>
<RecordState>Modified</RecordState>
<ID>1</ID>
<OriginalID>1</OriginalID>
<!-- jira 803 -->
<xsl:choose>
         <xsl:when test="Order/PersonInfoBillTo/@Country='USA' or Order/PersonInfoBillTo/@Country='US'">
         <CountryCode>USA</CountryCode>
         </xsl:when>
		 <xsl:when test="Order/PersonInfoBillTo/@Country='CAN' or Order/PersonInfoBillTo/@Country='CA' or Order/PersonInfoBillTo/@Country='CN'">
        <CountryCode>CAN</CountryCode>
         </xsl:when> 
         <xsl:otherwise>
      <CountryCode>FGN</CountryCode> 
         </xsl:otherwise>
       </xsl:choose>
<TelephoneNumber><xsl:value-of select = "Order/PersonInfoBillTo/@DayPhone" /></TelephoneNumber>

<AddressTypeCode>HOME</AddressTypeCode>
<MailIndicatorID>0</MailIndicatorID>
<CarrierRoute />
<NcoaDate>1900-01-01T00:00:00</NcoaDate>
<Address1><xsl:value-of select = "Order/PersonInfoBillTo/@AddressLine1" /></Address1>
<Address2><xsl:value-of select = "Order/PersonInfoBillTo/@AddressLine2" /></Address2>
<City><xsl:value-of select = "Order/PersonInfoBillTo/@City" /></City>
<State><xsl:value-of select = "Order/PersonInfoBillTo/@State" /></State>
<Address5/>
<Address6/>
<PostalCode><xsl:value-of select = "Order/PersonInfoBillTo/@ZipCode" /></PostalCode>
<TelephoneExtensionNumber/>
<DateLastAdded>2010-06-09T03:32:00</DateLastAdded>
<Error />
<Longitude>-1.7976931348623157E+308</Longitude>
<Latitude>-1.7976931348623157E+308</Latitude>
<ActiveFlag>true</ActiveFlag>
<EffectiveDate>2010-06-04T00:00:00</EffectiveDate>
<ExpiryDate>1900-01-01T00:00:00</ExpiryDate>
<RecurringFlag>false</RecurringFlag>
<MailOptInFlagCode>1</MailOptInFlagCode>
<PhoneOptInFlagCode>1</PhoneOptInFlagCode>
<PhoneIndicatorID>9</PhoneIndicatorID>
<AddressType>
<RecordState>Added</RecordState>
<Code>HOME</Code>
<Description>Home address</Description>
<TelephoneNumberLabel>Phone:</TelephoneNumberLabel>
<PostOnTelephoneFlag>true</PostOnTelephoneFlag>
</AddressType>
<CountryAddressFormat>
<RecordState>Added</RecordState>
<xsl:choose>
         <xsl:when test="Order/PersonInfoBillTo/@Country='USA' or Order/PersonInfoBillTo/@Country='US'">
         <CountryCode>USA</CountryCode>
         </xsl:when>
		 <xsl:when test="Order/PersonInfoBillTo/@Country='CAN' or Order/PersonInfoBillTo/@Country='CA' or Order/PersonInfoBillTo/@Country='CN'">
        <CountryCode>CAN</CountryCode>
         </xsl:when> 
         <xsl:otherwise>
      <CountryCode>FGN</CountryCode> 
         </xsl:otherwise>
       </xsl:choose>
<!--<CountryName>United States</CountryName> -->
<LanguageCode>ENG</LanguageCode>
<Language>
<RecordState>Added</RecordState>
<RootLanguageCode>ENG</RootLanguageCode>
<Description>English</Description>
</Language>
<Address1Label>Street Address:</Address1Label>
<Address2Label>Apartment/Suite:</Address2Label>
<Address3Label>City:</Address3Label>
<Address4Label>State:</Address4Label>
<Address5Label />
<Address6Label>E-mail:</Address6Label>
<PostCodeLabel>Zip Code:</PostCodeLabel>
<Address1MinimumLength>5</Address1MinimumLength>
<Address2MinimumLength>0</Address2MinimumLength>
<Address3MinimumLength>3</Address3MinimumLength>
<Address4MinimumLength>2</Address4MinimumLength>
<Address5MinimumLength>0</Address5MinimumLength>
<Address6MinimumLength>0</Address6MinimumLength>
<PostCodeMinimumLength>5</PostCodeMinimumLength>
<TelephoneNoMinimumLength>7</TelephoneNoMinimumLength>
<TelephoneNoFormat>(###) ###-####</TelephoneNoFormat>
<PostCodeLookupOption>3</PostCodeLookupOption>
<SequenceNo>0</SequenceNo>
<PrintCountryFlag>false</PrintCountryFlag>
<PrintBarcodeFlag>false</PrintBarcodeFlag>
<CarrierRouteFlag>true</CarrierRouteFlag>
<DateLastAdded>2012-07-10T17:41:00</DateLastAdded>
<TelephoneNoCountryCode />
</CountryAddressFormat>
</CustomerAddressWS>
</Items>
<CustomerNumber>-1</CustomerNumber>
</customerAddresses>
<customerAlternateKeys>
                                <Items>
                                <CustomerAlternateKeyWS>
                  <RecordState>Added</RecordState>
                  <Code>CSNO</Code>
                  <OriginalCode></OriginalCode>
                  <Value><xsl:value-of select = "Order/Extn/@CustomerAltKey" /></Value>
                  <OriginalValue></OriginalValue>
                  <AlternateKeyType>
                     <RecordState>Added</RecordState>
                     <Code>CSNO</Code>
                     <Description>ACT customer #</Description>
                     <EncryptionRestrictionLevel>0</EncryptionRestrictionLevel>
                     <AlternateKeyTypeMaskFKWS>
                           <RecordState>Modified</RecordState>
                           <Length>11</Length>
                           <Prefix><xsl:value-of select = "Order/Extn/@Prefix" /></Prefix>
                        </AlternateKeyTypeMaskFKWS>
                  </AlternateKeyType>
               </CustomerAlternateKeyWS>
                                </Items>
                                <CustomerNumber>-1</CustomerNumber>
                                </customerAlternateKeys>
</Save>
</soap:Body>
</soap:Envelope>
</xsl:template>
</xsl:stylesheet>
