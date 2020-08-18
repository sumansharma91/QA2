<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:output method="text" indent="yes"/>
	<xsl:template match="*">
		<xsl:variable name="headerCharge" select="sum(//HeaderCharge[@IsDiscount!='Y']/@ChargeAmount)"/>
		<xsl:variable name="headerDiscount" select="sum(//HeaderCharge[@IsDiscount='Y']/@ChargeAmount)"/>
		<xsl:variable name="documentType" select="//@DocumentType"/>
		<xsl:variable name="noOfInvoices" select="count(/InvoiceDetailList/InvoiceDetail/InvoiceHeader)"/>
		<xsl:variable name="count" select="0"/>
Invoice No(s): <xsl:for-each select="//InvoiceHeader">
<xsl:sort select="@InvoiceNo"/>
<xsl:value-of select="@InvoiceNo"/><xsl:if test="position() != last()">, </xsl:if>
</xsl:for-each>
Invoice Type: <xsl:value-of select="translate(//@InvoiceType, '_', ' ')"/>
		<xsl:text>&#10;</xsl:text>
Wholesale Partner: <xsl:value-of select="//@EnterpriseCode"/>
Wholesale Partner ID: <xsl:value-of select="//VSIWhCustCreditDetails/@CustomerID"/>
Purchase Order No.: <xsl:value-of select="//@CustomerPONo"/>
<xsl:if test="$documentType = '0001'">
Sales Order No.: <xsl:value-of select="//@OrderNo"/>
</xsl:if>
<xsl:if test="$documentType = '0003'">
Return Order No: <xsl:value-of select="//@OrderNo"/>
Sales Order No: <xsl:value-of select="//OrderReleaseList/OrderRelease/Order/@OrderNo"/>
</xsl:if>
Create Date: <xsl:value-of select="substring-before(//@OrderDate,'T')"/>
Posted Date: <xsl:value-of select="substring-before(//@DateInvoiced,'T')"/>
Posted Total: <xsl:value-of select="translate(format-number(sum(//InvoiceHeader/@TotalAmount), '00.00'), '-', '')"/>
		<xsl:text> </xsl:text>
		<xsl:value-of select="//@Currency"/>
		<xsl:text>&#10;</xsl:text>
		<!-- START - DELETE EVERYTHING BELOW THIS WHEN THE SALES AUDIT INTERFACE FOR WHOLESALE ORDERS IS IMPLEMENTED -->
		<xsl:if test="//InvoiceHeader/Shipment">
Ship From DC: 
<xsl:value-of select="//InvoiceHeader/Shipment/@ShipNode"/>
			<xsl:text>&#10;</xsl:text>
			<xsl:value-of select="//InvoiceHeader/Shipment/ShipNode/ShipNodePersonInfo/@AddressLine1"/>
			<xsl:text>&#10;</xsl:text>
			<xsl:if test="//InvoiceHeader/Shipment/ShipNode/ShipNodePersonInfo/@AddressLine2 != ''">
				<xsl:value-of select="//InvoiceHeader/Shipment/ShipNode/ShipNodePersonInfo/@AddressLine2"/>
				<xsl:text>&#10;</xsl:text>
			</xsl:if>
			<xsl:value-of select="//InvoiceHeader/Shipment/ShipNode/ShipNodePersonInfo/@City"/>
			<xsl:text> </xsl:text>
			<xsl:value-of select="//InvoiceHeader/Shipment/ShipNode/ShipNodePersonInfo/@State"/>
			<xsl:text> </xsl:text>
			<xsl:value-of select="//InvoiceHeader/Shipment/ShipNode/ShipNodePersonInfo/@Country"/>
			<xsl:text> </xsl:text>
			<xsl:value-of select="//InvoiceHeader/Shipment/ShipNode/ShipNodePersonInfo/@ZipCode"/>
			<xsl:text>&#10;</xsl:text>
		</xsl:if>
		<xsl:if test="//OrderReleaseList/OrderRelease">
Ship From DC: 
<xsl:value-of select="//OrderReleaseList/OrderRelease/@ShipNode"/>
			<xsl:text>&#10;</xsl:text>
			<xsl:value-of select="//OrderReleaseList/OrderRelease/ShipNode/ShipNodePersonInfo/@AddressLine1"/>
			<xsl:text>&#10;</xsl:text>
			<xsl:if test="//OrderReleaseList/OrderRelease/ShipNode/ShipNodePersonInfo/@AddressLine2 != ''">
				<xsl:value-of select="//OrderReleaseList/OrderRelease/ShipNode/ShipNodePersonInfo/@AddressLine2"/>
				<xsl:text>&#10;</xsl:text>
			</xsl:if>
			<xsl:value-of select="//OrderReleaseList/OrderRelease/ShipNode/ShipNodePersonInfo/@City"/>
			<xsl:text> </xsl:text>
			<xsl:value-of select="//OrderReleaseList/OrderRelease/ShipNode/ShipNodePersonInfo/@State"/>
			<xsl:text> </xsl:text>
			<xsl:value-of select="//OrderReleaseList/OrderRelease/ShipNode/ShipNodePersonInfo/@Country"/>
			<xsl:text> </xsl:text>
			<xsl:value-of select="//OrderReleaseList/OrderRelease/ShipNode/ShipNodePersonInfo/@ZipCode"/>
			<xsl:text>&#10;</xsl:text>
			</xsl:if>
Ship To Address:
<xsl:value-of select="//PersonInfoShipTo/@FirstName"/>
			<xsl:text>&#10;</xsl:text>
			<xsl:value-of select="//PersonInfoShipTo/@AddressLine1"/>
			<xsl:text>&#10;</xsl:text>
			<xsl:if test="//PersonInfoShipTo/@AddressLine2 != ''">
				<xsl:value-of select="//PersonInfoShipTo/@AddressLine2"/>
				<xsl:text>&#10;</xsl:text>
			</xsl:if>
			<xsl:value-of select="//PersonInfoShipTo/@City"/>
			<xsl:text> </xsl:text>
			<xsl:value-of select="//PersonInfoShipTo/@State"/>
			<xsl:text> </xsl:text>
			<xsl:value-of select="//PersonInfoShipTo/@Country"/>
			<xsl:text> </xsl:text>
			<xsl:value-of select="//PersonInfoShipTo/@ZipCode"/>
			<xsl:text>&#10;</xsl:text>
		<xsl:if test="$headerCharge != 0 or $headerDiscount != 0">
Order Charges:
	Freight Charge: <xsl:value-of select="translate(format-number($headerCharge, '00.00'), '-', '')"/>
			<xsl:text> </xsl:text>
			<xsl:value-of select="//@Currency"/>
	Freight Discount: <xsl:value-of select="translate(format-number($headerDiscount, '00.00'), '-', '')"/>
			<xsl:text> </xsl:text>
			<xsl:value-of select="//@Currency"/>
	Freight Total: <xsl:value-of select="translate(format-number($headerCharge - $headerDiscount, '00.00'), '-', '')"/>
			<xsl:text> </xsl:text>
			<xsl:value-of select="//@Currency"/>
			<xsl:text>&#10;</xsl:text>
		</xsl:if>
		<xsl:for-each select="//LineDetail">
			<xsl:sort select="OrderLine/@PrimeLineNo"/>
			<xsl:variable name="orderLineKey" select="@OrderLineKey"/>
Line No.: <xsl:value-of select="OrderLine/@PrimeLineNo"/>
Item ID: <xsl:value-of select="OrderLine/Item/@ItemID"/>
UPC Code: <xsl:value-of select="OrderLine/Item/@UPCCode"/>
Quantity: <xsl:value-of select="@ShippedQty"/>
Unit Price: <xsl:value-of select="translate(format-number(@UnitPrice, '00.00'), '-', '')"/>
			<xsl:text> </xsl:text>
			<xsl:value-of select="//@Currency"/>
Extended Price: <xsl:value-of select="translate(format-number(@ShippedQty * @UnitPrice, '00.00'), '-', '')"/>
			<xsl:text> </xsl:text>
			<xsl:value-of select="//@Currency"/>
			<xsl:for-each select="LineCharges/LineCharge[@IsDiscount='N']">
Line Charges:
<xsl:text>&#x9;</xsl:text>
				<xsl:value-of select="@ChargeName"/>
				<xsl:text>: </xsl:text>
				<xsl:value-of select="translate(format-number(@ChargeAmount, '00.00'), '-', '')"/>
				<xsl:text> </xsl:text>
				<xsl:value-of select="//@Currency"/>
			</xsl:for-each>
			<xsl:for-each select="LineCharges/LineCharge[@IsDiscount='Y']">
Line Discounts:
<xsl:text>&#x9;</xsl:text>
				<xsl:value-of select="@ChargeName"/>
				<xsl:text>: </xsl:text>
				<xsl:value-of select="translate(format-number(@ChargeAmount, '00.00'), '-', '')"/>
				<xsl:text> </xsl:text>
				<xsl:value-of select="//@Currency"/>
			</xsl:for-each>
Line Total: <xsl:value-of select="translate(format-number(@LineTotal, '00.00'), '-', '')"/>
			<xsl:text> </xsl:text>
			<xsl:value-of select="//@Currency"/>
<xsl:if test="$documentType = '0001'">
Sales Order No.: <xsl:value-of select="//@OrderNo"/>
</xsl:if>
<xsl:if test="$documentType = '0003'">
Return Order No: <xsl:value-of select="//@OrderNo"/>
Sales Order No: <xsl:value-of select="//OrderReleaseList/OrderRelease/Order/@OrderNo"/>
</xsl:if>
			<xsl:text>&#10;</xsl:text>
		</xsl:for-each>
		<!-- END - DELETE EVERYTHING ABOVE THIS WHEN THE SALES AUDIT INTERFACE FOR WHOLESALE ORDERS IS IMPLEMENTED -->
	</xsl:template>
</xsl:stylesheet>
