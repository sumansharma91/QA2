<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:output method="html" indent="yes"/>
	<xsl:template match="*">
		<xsl:comment>CONTENT_TYPE=text/html</xsl:comment>
		<html>
			<body style="margin-left: 25px">
				<p>Some ordered quantity has been cancelled on Order No. <xsl:value-of select="//@OrderNo"/>.</p>
				<br/>
				<table style="border:1px solid black;border-collapse:collapse; font-family:Calibri" cellspacing="1" cellpadding="2px">
					<tbody>
						<tr style="background-color:#E6E6E6;border:1px solid black;">
							<th style="border:1px solid black;" align="Middle" width="120">
								<xsl:value-of select="'Partner ID'"/>
							</th>
							<th style="border:1px solid black;" align="Middle" width="100">
								<xsl:value-of select="'Order No.'"/>
							</th>
							<th style="border:1px solid black;" align="Middle" width="100">
								<xsl:value-of select="'Line No.'"/>
							</th>
							<th style="border:1px solid black;" align="Middle" width="100">
								<xsl:value-of select="'SKU ID'"/>
							</th>
							<th style="border:1px solid black;" align="Middle" width="100">
								<xsl:value-of select="'Cancelled Qty'"/>
							</th>
						</tr>
						<xsl:for-each select="//OrderLine">
							<tr align="Justify" style="border:1px solid black;">
								<td style="border:1px solid black;" align="Middle" width="120">
									<xsl:value-of select="//@EnterpriseCode"/>
								</td>
								<td style="border:1px solid black;" align="Middle" width="100">
									<xsl:value-of select="//@OrderNo"/>
								</td>
								<td style="border:1px solid black;" align="Middle" width="100">
									<xsl:value-of select="@PrimeLineNo"/>
								</td>
								<td style="border:1px solid black;" align="Middle" width="100">
									<xsl:value-of select="@ItemID"/>
								</td>
								<td style="border:1px solid black;" align="Middle" width="100">
									<xsl:value-of select="@CancelledQty"/>
								</td>
							</tr>
						</xsl:for-each>
					</tbody>
				</table>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
