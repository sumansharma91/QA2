<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<VSIEnvelope>
			<!-- MessageType Static -->
			<MessageType>AllocationRequest</MessageType>
			<Message>
				<!--map datestamp to createts attribute -->
				<DateTimeStamp>
					<xsl:value-of select="OrderRelease/Order/@Createts" />
				</DateTimeStamp>
				<!-- Check for reverse Allocation -->
				<xsl:variable name="ReverseAllocation">
					<xsl:value-of select="OrderRelease/@ReverseAllocation" />
				</xsl:variable>
				<xsl:variable name="SendRelease">
					<xsl:value-of select="OrderRelease/@SendRelease" />
				</xsl:variable>
				<xsl:variable name="OrderNo">
					<xsl:if test="OrderRelease/@DocumentType='0006'">
						<xsl:value-of select="OrderRelease/@CustomerPoNo" />					
					</xsl:if>
					<xsl:if test="OrderRelease/@DocumentType='0001'">
						<xsl:value-of select="OrderRelease/Order/@OrderNo" />					
					</xsl:if>
				</xsl:variable>				
				<xsl:variable name="OrderType">
					<xsl:if test="OrderRelease/@DocumentType='0006'">
						<xsl:value-of select="'Ship_to_Store'" />					
					</xsl:if>
					<xsl:if test="OrderRelease/@DocumentType='0001'">
						<xsl:value-of select="'Ship_to_Home'" />					
					</xsl:if>
				</xsl:variable>
				<!-- OrderNo -->
				<OrderNo>
					<xsl:value-of select="$OrderNo" />*<xsl:value-of select="OrderRelease/@ReleaseNo" />
				</OrderNo>
				<OrderType><xsl:value-of select="$OrderType" /></OrderType>
				<!-- IntOrderDate -->
				<IntOrderDate>
					<xsl:value-of select="OrderRelease/Order/@OrderDate" />
				</IntOrderDate>
				<!-- Store -->
				<Store>
					<xsl:choose>
						<!-- when STS then Store=Shipnode -->
						<xsl:when test="OrderRelease/@DocumentType='0006'">
							<xsl:value-of select="OrderRelease/@ShipNode" />
						</xsl:when>
						<!-- When STH then use Entered by as Store -->
						<xsl:when test="OrderRelease/@SfsAllocation='Y'">
							<xsl:value-of select="OrderRelease/@ShipNode" />
						</xsl:when>						
						<xsl:otherwise>
							<xsl:value-of select="OrderRelease/Order/@EnteredBy" />
						</xsl:otherwise>
					</xsl:choose>
				</Store>
				<!-- WhseNo -->
				<WhseNo>
					<xsl:choose>
						<!-- STS then WhseNo=STSShipNode -->
						<xsl:when test="OrderRelease/@DocumentType='0006'">
							<xsl:value-of select="OrderRelease/@STSShipNode" />
						</xsl:when>

						<!-- STH then WhseNo=Shipnode -->
						<xsl:when test="OrderRelease/@DocumentType='0001'">
							<xsl:value-of select="OrderRelease/@ShipNode" />
						</xsl:when>
						<xsl:otherwise>
						</xsl:otherwise>
					</xsl:choose>
				</WhseNo>
				<!-- CustNo -->
				<CustNo>
					<xsl:value-of select="OrderRelease/@BillToID" />
				</CustNo>
				<!--Item set for each OrderLine -->
				<xsl:for-each select="OrderRelease/OrderLines/OrderLine">
					<xsl:choose>
						<xsl:when test="$ReverseAllocation = 'Y'">
							<xsl:if test="$SendRelease = 'Y'">
								<Item>
									<!-- JdaSku -->
									<JdaSku>
										<xsl:value-of select="Item/@ItemID" />
									</JdaSku>
									<!-- OrigQty -->
									<OrigQty>
										<xsl:value-of select="@OrderedQty"/>
									</OrigQty>
									<!-- QtyOrdered -->
									<QtyOrdered>
										<xsl:value-of select="'0'" />
									</QtyOrdered>
								</Item>
							</xsl:if>
							<xsl:if test="@OriginalAllocatedQty">
								<Item>
									<!-- JdaSku -->
									<JdaSku>
										<xsl:value-of select="Item/@ItemID" />
									</JdaSku>
									<!-- OrigQty -->
									<OrigQty>
										<xsl:value-of select="@OriginalAllocatedQty"/>
									</OrigQty>
									<!-- QtyOrdered -->
									<QtyOrdered>
										<xsl:value-of select="@OrderedQty" />
									</QtyOrdered>
								</Item>
							</xsl:if>
						</xsl:when>
						<xsl:otherwise>
							<Item>
								<!-- JdaSku -->
								<JdaSku>
									<xsl:value-of select="Item/@ItemID" />
								</JdaSku>
								<!-- OrigQty -->
								<OrigQty>
									<xsl:choose>
										<!-- when original quantity is zero -->
										<xsl:when test="@OriginalAllocatedQty">
											<xsl:value-of select="@OriginalAllocatedQty" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="'0'" />
										</xsl:otherwise>
									</xsl:choose>
								</OrigQty>
								<!-- QtyOrdered -->
								<QtyOrdered>
									<xsl:value-of select="@OrderedQty" />
								</QtyOrdered>
							</Item>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
			</Message>
		</VSIEnvelope>
	</xsl:template>
</xsl:stylesheet>