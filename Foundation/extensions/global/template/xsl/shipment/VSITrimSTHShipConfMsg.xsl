<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="/OrderList/Order">
		<OrderList>
			<Order>
				<xsl:copy-of select="@EntryType" />
				<xsl:copy-of select="@MessageType" />
				<xsl:copy-of select="@OrderNo" />
				<Extn>
					<xsl:copy-of select="Extn/@ExtnPointsEarned" />
				</Extn>
				<OrderLines>
					<OrderLine>
						<xsl:copy-of select="OrderLines/OrderLine/@CustomerPONo" />
						<xsl:copy-of select="OrderLines/OrderLine/@LineType" />
						<xsl:copy-of select="OrderLines/OrderLine/@ReqShipDate" />
						<xsl:copy-of select="OrderLines/OrderLine/@OrderedQty" />
						<Extn>
							<xsl:copy-of select="OrderLines/OrderLine/Extn/@ExtnLastPickDate" />
						</Extn>
						<Item>
							<xsl:copy-of select="OrderLines/OrderLine/Item/@ItemID" />
							<xsl:copy-of select="OrderLines/OrderLine/Item/@ItemDesc" />
							<xsl:copy-of select="OrderLines/OrderLine/Item/@ItemSize" />
						</Item>
						<ItemDetails>
							<Extn>
								<xsl:copy-of select="OrderLines/OrderLine/ItemDetails/Extn/@ExtnActSkuID" />
								<xsl:copy-of select="OrderLines/OrderLine/ItemDetails/Extn/@ExtnBrand" />
								<xsl:copy-of select="OrderLines/OrderLine/ItemDetails/Extn/@ExtnBrandTitle" />
								<xsl:copy-of select="OrderLines/OrderLine/ItemDetails/Extn/@ExtnItemUOM" />
								<xsl:copy-of select="OrderLines/OrderLine/ItemDetails/Extn/@ExtnItemType" />
							</Extn>
						</ItemDetails>
						<LineOverallTotals>
							<xsl:copy-of select="OrderLines/OrderLine/LineOverallTotals/@ExtendedPrice" />
						</LineOverallTotals>
						<PersonInfoShipTo>
							<xsl:copy-of select="OrderLines/OrderLine/PersonInfoShipTo/@FirstName" />
							<xsl:copy-of select="OrderLines/OrderLine/PersonInfoShipTo/@LastName" />
							<xsl:copy-of select="OrderLines/OrderLine/PersonInfoShipTo/@AddressLine1" />
							<xsl:copy-of select="OrderLines/OrderLine/PersonInfoShipTo/@AddressLine2" />
							<xsl:copy-of select="OrderLines/OrderLine/PersonInfoShipTo/@City" />
							<xsl:copy-of select="OrderLines/OrderLine/PersonInfoShipTo/@State" />
							<xsl:copy-of select="OrderLines/OrderLine/PersonInfoShipTo/@ZipCode" />
							<xsl:copy-of select="OrderLines/OrderLine/PersonInfoShipTo/@Country" />
							<xsl:copy-of select="OrderLines/OrderLine/PersonInfoShipTo/@DayPhone" />
							<xsl:copy-of select="OrderLines/OrderLine/PersonInfoShipTo/@EMailID" />
						</PersonInfoShipTo>
					</OrderLine>
				</OrderLines>
				<OverallTotals>
					<xsl:copy-of select="OverallTotals/@GrandDiscount" />
					<xsl:copy-of select="OverallTotals/@LineSubTotal" />
					<xsl:copy-of select="OverallTotals/@GrandShippingCharges" />
					<xsl:copy-of select="OverallTotals/@GrandTax" />
					<xsl:copy-of select="OverallTotals/@GrandTotal" />
				</OverallTotals>
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
						<xsl:copy-of select="PaymentMethods/PaymentMethod/@TotalCharged" />
					</PaymentMethod>
				</PaymentMethods>
				<PersonInfoBillTo>
					<xsl:copy-of select="PersonInfoBillTo/@FirstName" />
					<xsl:copy-of select="PersonInfoBillTo/@LastName" />
					<xsl:copy-of select="PersonInfoBillTo/@AddressLine1" />
					<xsl:copy-of select="PersonInfoBillTo/@AddressLine2" />
					<xsl:copy-of select="PersonInfoBillTo/@City" />
					<xsl:copy-of select="PersonInfoBillTo/@State" />
					<xsl:copy-of select="PersonInfoBillTo/@ZipCode" />
					<xsl:copy-of select="PersonInfoBillTo/@Country" />
					<xsl:copy-of select="PersonInfoBillTo/@DayPhone" />
					<xsl:copy-of select="PersonInfoBillTo/@EMailID" />
				</PersonInfoBillTo>
			</Order>
		</OrderList>
            
	</xsl:template>
</xsl:stylesheet>