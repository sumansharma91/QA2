<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:template match="/">

		<xsl:variable name="ModificationReasonCode">
			<xsl:value-of select=" Order/OrderAudit/@ReasonCode" />
		</xsl:variable>
		<xsl:variable name="NoOfSTSorPISLines">
			<xsl:value-of
				select="count(Order/OrderLines/OrderLine[@LineType='SHIP_TO_STORE' or @LineType = 'PICK_IN_STORE'])" />
		</xsl:variable>
		<Order>
			<xsl:attribute name="OrderHeaderKey">
												<xsl:value-of select="Order/@OrderHeaderKey" />
											</xsl:attribute>
		
			<xsl:choose>
				<xsl:when test="number($NoOfSTSorPISLines) &gt; 0">

					<OrderLines>
						<xsl:attribute name="HasOrderLines"><xsl:text>N</xsl:text></xsl:attribute>

						<xsl:for-each
							select="Order/OrderLines/OrderLine[@LineType != 'SHIP_TO_HOME']">
							<xsl:if test="$ModificationReasonCode != 'MISSHIP' ">
							<xsl:if
								test="@LineType = 'PICK_IN_STORE' or @LineType = 'SHIP_TO_STORE'">
								<xsl:variable name="Status">
									<xsl:value-of
										select="StatusBreakupForCanceledQty/CanceledFrom/@Status" />
								</xsl:variable>
								<!-- OMS 768 omni 2.0-->
								<xsl:variable name="Qty">
									<xsl:value-of
										select="StatusBreakupForCanceledQty/CanceledFrom/@Quantity" />
								</xsl:variable>
								<xsl:variable name="ReleaseKey">
									<xsl:value-of
										select="StatusBreakupForCanceledQty/CanceledFrom/@OrderReleaseKey" />
								</xsl:variable>
								<!-- // OMS 768 omni 2.0-->

								<xsl:choose>
									<xsl:when
										test="((number($Status) &gt; 2160.100  and number($Status) &lt; 2160.400) or (number($Status)) = 2160.10)">
										<xsl:attribute name="HasOrderLines"><xsl:text>Y</xsl:text></xsl:attribute>
										<OrderLine>
											<xsl:attribute name="OrderLineKey">
												<xsl:value-of select="@OrderLineKey" />
											</xsl:attribute>
											<xsl:attribute name="OrderReleaseKey">
												<xsl:value-of select="$ReleaseKey" />
											</xsl:attribute>
											<xsl:attribute name="Status" >RestockInTransit</xsl:attribute>
											<!-- OMS 768 omni 2.0-->
											<xsl:attribute name="Quantity" >
											<xsl:value-of select="$Qty"/>
											</xsl:attribute>
											<!-- // OMS 768 omni 2.0-->

											<xsl:attribute name="LineType">
												<xsl:value-of select="@LineType" />
											</xsl:attribute>
										</OrderLine>
									</xsl:when>
									<xsl:otherwise>
										<xsl:choose>
											<xsl:when
												test="(number($Status) != 2160.100 and number($Status) != 2160 and number($Status) &gt; 1500.100 and number($Status) &lt; 3700 )">
												<xsl:attribute name="HasOrderLines"><xsl:text>Y</xsl:text></xsl:attribute>
												<OrderLine>
													<xsl:attribute name="OrderLineKey">
												<xsl:value-of select="@OrderLineKey" />
											</xsl:attribute>
											<!-- OMS 768 omni 2.0--> 
											<xsl:attribute name="Quantity" >
											<xsl:value-of select="$Qty"/>
											</xsl:attribute>
											<!-- // OMS 768 omni 2.0-->
											<xsl:attribute name="OrderReleaseKey">
												<xsl:value-of select="$ReleaseKey" />
											</xsl:attribute>
													<xsl:attribute name="Status" >Restock</xsl:attribute>
													<xsl:attribute name="LineType">
												<xsl:value-of select="@LineType" />
											</xsl:attribute>
												</OrderLine>
											</xsl:when>
										
										
										</xsl:choose>
									</xsl:otherwise>

								</xsl:choose>



							</xsl:if>
							</xsl:if>
						</xsl:for-each>
					</OrderLines>

				</xsl:when>
			</xsl:choose>

		</Order>


	</xsl:template>


</xsl:stylesheet>