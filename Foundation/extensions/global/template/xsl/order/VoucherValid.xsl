<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:output version="1.0" indent="yes" encoding="UTF-8" method="xml" />
   <xsl:template match="/">
   <VoucherList>
      <xsl:attribute name="isValid">
    <xsl:choose>
     <xsl:when test="VoucherList/Voucher and VoucherList/Voucher/@RedeemStatus='Y'">Y</xsl:when>
  <xsl:otherwise>N</xsl:otherwise>
  </xsl:choose>
  </xsl:attribute>
  <xsl:copy-of select = "VoucherList/@*"/>
  <xsl:copy-of select = "VoucherList/*"/>
  </VoucherList>
   </xsl:template>
</xsl:stylesheet>

