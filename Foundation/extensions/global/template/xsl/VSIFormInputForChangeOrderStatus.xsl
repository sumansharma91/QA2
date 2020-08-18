<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:template match="/">




		<xsl:variable name="NoOfSTSRestockLines">
			<xsl:value-of
				select="count(Order/OrderLines/OrderLine[@LineType='SHIP_TO_STORE' and @Status = 'Restock'])"></xsl:value-of>
		</xsl:variable>

		<xsl:variable name="NoOfSTSRestockInTranLines">
			<xsl:value-of
				select="count(Order/OrderLines/OrderLine[@LineType='SHIP_TO_STORE' and @Status = 'RestockInTransit'])"></xsl:value-of>
		</xsl:variable>

		<xsl:variable name="NoOfSTSLines">
			<xsl:value-of
				select="count(Order/OrderLines/OrderLine[@LineType='SHIP_TO_STORE'])"></xsl:value-of>
		</xsl:variable>

		<xsl:variable name="NoOfPISLines">
			<xsl:value-of
				select="count(Order/OrderLines/OrderLine[@LineType='PICK_IN_STORE'])"></xsl:value-of>
		</xsl:variable>




		<xsl:choose>


			<xsl:when
				test="((number($NoOfSTSLines) = 0 and number($NoOfPISLines) > 0) or (number($NoOfSTSRestockLines) = number($NoOfSTSLines)))">
				<OrderStatusChange>
					<xsl:attribute name="TransactionId">
				<xsl:text>VSIRestock.0001.ex</xsl:text>
			</xsl:attribute>
					<xsl:attribute name="OrderHeaderKey">
				<xsl:value-of select="Order/@OrderHeaderKey"></xsl:value-of>
			</xsl:attribute>
					<OrderLines>
						<xsl:for-each select="Order/OrderLines/OrderLine">
							<OrderLine>
								<xsl:attribute name="OrderLineKey">
							<xsl:value-of select="@OrderLineKey"></xsl:value-of>
								
						</xsl:attribute>
						<!-- OMS 768 omni 2.0>
								<xsl:attribute name="ChangeForAllAvailableQty">
							<xsl:text>Y</xsl:text>
						</xsl:attribute>
						
						// OMS 768 omni 2.0-->
						
						<!-- OMS 768 omni 2.0 -->
						<xsl:attribute name="Quantity" >
											<xsl:value-of select="@Quantity"/>
											</xsl:attribute>
											
													<xsl:attribute name="OrderReleaseKey" >
											<xsl:value-of select="@OrderReleaseKey"/>
											</xsl:attribute>
											<!-- // OMS 768 omni 2.0-->
											
								<xsl:attribute name="BaseDropStatus">9000.100</xsl:attribute>
							</OrderLine>
						</xsl:for-each>
					</OrderLines>
				</OrderStatusChange>
			</xsl:when>
			<xsl:otherwise>
			<xsl:choose>
			<xsl:when
				test="((number($NoOfPISLines) = 0) and (number($NoOfSTSRestockInTranLines) = number($NoOfSTSLines)))">
				<OrderStatusChange>
					<xsl:attribute name="TransactionId">
				<xsl:text>VSIRestockInTransit.0001.ex</xsl:text>
			</xsl:attribute>
					<xsl:attribute name="OrderHeaderKey">
				<xsl:value-of select="Order/@OrderHeaderKey"></xsl:value-of>
			</xsl:attribute>
					<OrderLines>
						<xsl:for-each select="Order/OrderLines/OrderLine">
							<OrderLine>
								<xsl:attribute name="OrderLineKey">
							<xsl:value-of select="@OrderLineKey"></xsl:value-of>
						</xsl:attribute>
								<!-- OMS 768 omni 2.0>
						
								<xsl:attribute name="ChangeForAllAvailableQty">
							<xsl:text>Y</xsl:text>
						</xsl:attribute>
						
						// OMS 768 omni 2.0-->
						
						<!-- OMS 768 omni 2.0 -->
						<xsl:attribute name="Quantity" >
											<xsl:value-of select="@Quantity"/>
											</xsl:attribute>
											<xsl:attribute name="OrderReleaseKey" >
											<xsl:value-of select="@OrderReleaseKey"/>
											</xsl:attribute>
											<!-- // OMS 768 omni 2.0-->
								<xsl:attribute name="BaseDropStatus">9000.200</xsl:attribute>
							</OrderLine>
						</xsl:for-each>
					</OrderLines>
				</OrderStatusChange>
			</xsl:when>
			<xsl:otherwise>
			
			<MultiApi>
					<API>
						<xsl:attribute name="Name"><xsl:text>changeOrderStatus</xsl:text></xsl:attribute>
						<Input>
							<OrderStatusChange>
								<xsl:attribute name="TransactionId">
				<xsl:text>VSIRestockInTransit.0001.ex</xsl:text>
			</xsl:attribute>
								<xsl:attribute name="OrderHeaderKey">
				<xsl:value-of select="Order/@OrderHeaderKey"></xsl:value-of>
			</xsl:attribute>
								<OrderLines>
									<xsl:for-each
										select="Order/OrderLines/OrderLine[@Status = 'RestockInTransit']">
										<OrderLine>
											<xsl:attribute name="OrderLineKey">
							<xsl:value-of select="@OrderLineKey"></xsl:value-of>
						</xsl:attribute>
						<xsl:attribute name="OrderReleaseKey" >
											<xsl:value-of select="@OrderReleaseKey"/>
											</xsl:attribute>
						<!-- OMS 768 omni 2.0>
						</xsl:attribute>
								<xsl:attribute name="ChangeForAllAvailableQty">
							<xsl:text>Y</xsl:text>
						</xsl:attribute>
						
						// OMS 768 omni 2.0-->
						
						<!-- OMS 768 omni 2.0 -->
						<xsl:attribute name="Quantity" >
											<xsl:value-of select="@Quantity"/>
											</xsl:attribute>
											<xsl:attribute name="OrderReleaseKey" >
											<xsl:value-of select="@OrderReleaseKey"/>
											</xsl:attribute>
											<!-- // OMS 768 omni 2.0-->
											<xsl:attribute name="BaseDropStatus">9000.200</xsl:attribute>
										</OrderLine>
									</xsl:for-each>
								</OrderLines>
							</OrderStatusChange>
						</Input>
					</API>
					<API>
						<xsl:attribute name="Name"><xsl:text>changeOrderStatus</xsl:text></xsl:attribute>
						<Input>
							<OrderStatusChange>
								<xsl:attribute name="TransactionId">
				<xsl:text>VSIRestock.0001.ex</xsl:text>
			</xsl:attribute>
								<xsl:attribute name="OrderHeaderKey">
				<xsl:value-of select="Order/@OrderHeaderKey"></xsl:value-of>
			</xsl:attribute>
								<OrderLines>
									<xsl:for-each
										select="Order/OrderLines/OrderLine[@Status = 'Restock']">
										<OrderLine>
											<xsl:attribute name="OrderLineKey">
							<xsl:value-of select="@OrderLineKey"></xsl:value-of>
						</xsl:attribute>
										<!-- OMS 768 omni 2.0>
						</xsl:attribute>
								<xsl:attribute name="ChangeForAllAvailableQty">
							<xsl:text>Y</xsl:text>
						</xsl:attribute>
						
						// OMS 768 omni 2.0-->
						
						<!-- OMS 768 omni 2.0 -->
						<xsl:attribute name="Quantity" >
											<xsl:value-of select="@Quantity"/>
											</xsl:attribute>
											<xsl:attribute name="OrderReleaseKey" >
											<xsl:value-of select="@OrderReleaseKey"/>
											</xsl:attribute>
											<!-- // OMS 768 omni 2.0-->
											<xsl:attribute name="BaseDropStatus">9000.100</xsl:attribute>
										</OrderLine>
									</xsl:for-each>
								</OrderLines>
							</OrderStatusChange>
						</Input>
					</API>
				</MultiApi>
			
			</xsl:otherwise>
			</xsl:choose>
			
			
			</xsl:otherwise>


		</xsl:choose>

	</xsl:template>


</xsl:stylesheet>