<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:template match="/">
     
     <xsl:if test = "/VoucherList/Voucher/@RedeemStatus='N'">
     <Voucher isValid="Y">
     <xsl:copy-of select = "/VoucherList/Voucher/@*"/>
     </Voucher>
     </xsl:if>
     <xsl:if test = "/VoucherList/Voucher/@RedeemStatus='Y'">
     <Voucher isValid="N">
     <xsl:copy-of select = "/VoucherList/Voucher/@*"/>
     </Voucher>
     </xsl:if>
     <xsl:if test = "not(/VoucherList/Voucher)">
     <Voucher isValid="N"/>
     </xsl:if>
     </xsl:template>
</xsl:stylesheet>