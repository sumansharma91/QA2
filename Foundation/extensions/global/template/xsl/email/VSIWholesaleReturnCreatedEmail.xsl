<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:output method="html" indent="yes"/>
	<xsl:template match="*">
		<xsl:comment>CONTENT_TYPE=text/html</xsl:comment>
		<html>
			<body style="margin-left: 25px">
				<p>Wholesale Return no. <xsl:value-of select="//@OrderNo"/> has been created in OMS.</p>
				<br/>
				<table style="border:1px solid black;border-collapse:collapse; font-family:Calibri" cellspacing="1" cellpadding="2px">
					<tbody>
						<tr style="background-color:#E6E6E6;border:1px solid black;">
							<th style="border:1px solid black;" align="Middle" width="120">
								<xsl:value-of select="'Partner ID'"/>
							</th>
							<th style="border:1px solid black;" align="Middle" width="120">
								<xsl:value-of select="'Sales Order No.'"/>
							</th>
							<th style="border:1px solid black;" align="Middle" width="100">
								<xsl:value-of select="'Return No.'"/>
							</th>
							<th style="border:1px solid black;" align="Middle" width="100">
								<xsl:value-of select="'Line No.'"/>
							</th>
							<th style="border:1px solid black;" align="Middle" width="100">
								<xsl:value-of select="'SKU ID'"/>
							</th>
							<th style="border:1px solid black;" align="Middle" width="100">
								<xsl:value-of select="'UPC Code'"/>
							</th>
							<th style="border:1px solid black;" align="Middle" width="100">
								<xsl:value-of select="'Returned Qty'"/>
							</th>
							<th style="border:1px solid black;" align="Middle" width="200">
								<xsl:value-of select="'Return Reason'"/>
							</th>
							<th style="border:1px solid black;" align="Middle" width="200">
								<xsl:value-of select="'Return To DC:'"/>
							</th>
						</tr>
						<xsl:for-each select="/Order/OrderLines/OrderLine">
							<tr align="Justify" style="border:1px solid black;">
								<td style="border:1px solid black;" align="Middle" width="120">
									<xsl:value-of select="//@EnterpriseCode"/>
								</td>
								<td style="border:1px solid black;" align="Middle" width="120">
									<xsl:value-of select="//DerivedFromOrder/@OrderNo"/>
								</td>
								<td style="border:1px solid black;" align="Middle" width="100">
									<xsl:value-of select="//@OrderNo"/>
								</td>
								<td style="border:1px solid black;" align="Middle" width="100">
									<xsl:value-of select="@PrimeLineNo"/>
								</td>
								<td style="border:1px solid black;" align="Middle" width="100">
									<xsl:value-of select="Item/@ItemID"/>
								</td>
								<td style="border:1px solid black;" align="Middle" width="100">
									<xsl:value-of select="Item/@UPCCode"/>
								</td>
								<td style="border:1px solid black;" align="Middle" width="100">
									<xsl:value-of select="@OrderedQty"/>
								</td>
								<td style="border:1px solid black;" align="Middle" width="200">
									<xsl:value-of select="@ReturnReasonDesc"/>
								</td>
								<td style="border:1px solid black;" align="Middle" width="200">
									<xsl:value-of select="@OrderShipNode"/>
								</td>
							</tr>
						</xsl:for-each>
					</tbody>
				</table>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
