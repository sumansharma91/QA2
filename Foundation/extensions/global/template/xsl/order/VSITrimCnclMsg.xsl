<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="/OrderList/Order">
	
		<OrderList>
			<xsl:copy-of select="/OrderList/@EmailType" />
			<xsl:copy-of select="/OrderList/@MessageType" />
			<xsl:copy-of select="/OrderList/@Action" />
			<Order>
				<xsl:copy-of select="@CancellationReasonCode" />
				<xsl:copy-of select="@MessageType" />
				<xsl:copy-of select="@OrderNo" />
				<xsl:copy-of select="@EntryType" />
				<xsl:copy-of select="@CustomerPONo" />
				<xsl:copy-of select="@EnterpriseCode" />
				<xsl:copy-of select="@OrderDate" />
				<xsl:copy-of select="@PaymentRuleId" />
				<xsl:copy-of select="@CustomerEMailID" />
				<OverallTotals><xsl:copy-of select="OverallTotals/@*"/></OverallTotals>
				<Extn><xsl:copy-of select="Extn/@*"/></Extn>
				<OrderLines>
					<xsl:for-each select="OrderLines/OrderLine">
						<OrderLine>
							<xsl:copy-of select="@CustomerPONo" />
							<xsl:copy-of select="@LineType" />
							<xsl:copy-of select="@OrderedQty" />
							<Extn>
								<xsl:copy-of select="Extn/@ExtnLastPickDate" />
							</Extn>
							<Item>
								<xsl:copy-of select="Item/@ItemID" />
								<xsl:copy-of select="Item/@ItemDesc" />
							</Item>
							<ItemDetails>
								<Extn>
									<xsl:copy-of select="ItemDetails/Extn/@ExtnActSkuID" />
									<xsl:copy-of select="ItemDetails/Extn/@ExtnBrandTitle" />
									<xsl:copy-of select="ItemDetails/Extn/@ExtnItemUOM" />
									<xsl:copy-of select="ItemDetails/Extn/@ExtnItemType" />
									<xsl:copy-of select="ItemDetails/Extn/@ExtnItemSize" />
								</Extn>
							</ItemDetails>
							<LineOverallTotals>
								<xsl:copy-of select="LineOverallTotals/@ExtendedPrice" />
							</LineOverallTotals>
							<PersonInfoShipTo>
								<xsl:copy-of select="PersonInfoShipTo/@FirstName" />
								<xsl:copy-of select="PersonInfoShipTo/@LastName" />
								<xsl:copy-of select="PersonInfoShipTo/@AddressLine1" />
								<xsl:copy-of select="PersonInfoShipTo/@AddressLine2" />
								<xsl:copy-of select="PersonInfoShipTo/@City" />
								<xsl:copy-of select="PersonInfoShipTo/@State" />
								<xsl:copy-of select="PersonInfoShipTo/@ZipCode" />
								<xsl:copy-of select="PersonInfoShipTo/@Country" />
								<xsl:copy-of select="PersonInfoShipTo/@DayPhone" />
								<xsl:copy-of select="PersonInfoShipTo/@EMailID" />
							</PersonInfoShipTo>
						</OrderLine>
					</xsl:for-each>
				</OrderLines>
				<Promotions>
					<Promotion>
						<xsl:copy-of select="Promotions/Promotion/@Description" />
					</Promotion>
				</Promotions>
				<PaymentMethods>
					<PaymentMethod>
						<xsl:copy-of select="PaymentMethods/PaymentMethod/@PaymentType" />
						<xsl:copy-of select="PaymentMethods/PaymentMethod/@CreditCardType" />
						<xsl:copy-of select="PaymentMethods/PaymentMethod/@DisplayCreditCardNo" />
						<xsl:copy-of select="PaymentMethods/PaymentMethod/@TotalAuthorized" />
					</PaymentMethod>
				</PaymentMethods>
			</Order>
		</OrderList>
            
	</xsl:template>
</xsl:stylesheet>