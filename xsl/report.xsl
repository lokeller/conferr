<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="@ret-value | ret-value">
		  <xsl:choose>
			<xsl:when test=". = 0">
			<span style="background-color: #ffff00; font-family: arial, sans-serif, sansserif;">Startup error</span>
			</xsl:when>
			<xsl:when test=". = 1">
			<span style="background-color: #ff0000; font-family: arial, sans-serif, sansserif;">Shutdown error</span>
			</xsl:when>
			<xsl:when test=". = 2">
			<span style="background-color: #ff0000; font-family: arial, sans-serif, sansserif;">Test error</span>
			</xsl:when>
			<xsl:when test=". = 3">
			<span style="background-color: #00ff00; font-family: arial, sans-serif, sansserif;">OK</span>
			</xsl:when>
			<xsl:when test=". = 4">
			<span style="background-color: #ff0000; font-family: arial, sans-serif, sansserif;">Internal error</span>
			</xsl:when>
			<xsl:when test=". = 5">
			<span style="background-color: #aaaaff; font-family: arial, sans-serif, sansserif;">Impossible configuration</span>
			</xsl:when>

		  </xsl:choose>
</xsl:template>


<xsl:template match="/">
  <html>
  <body>
    <h1>Fault injection report</h1>
    <xsl:for-each select="report/result/scenario/text()[not(. = ./../../preceding-sibling::result/scenario/text())]">	
    <p><b><xsl:value-of select="."/></b></p>
    <table border="0">
    <tr><th align="left">Name</th><th align="left">Result</th></tr>
	    <xsl:for-each select="//result[scenario/text() = current() ]">	
	    	<tr><td>
		<xsl:element name="a">
			<xsl:attribute name="href">
				#<xsl:value-of select="generate-id(current())"/>
			</xsl:attribute>
			<xsl:value-of select="description"/>
		</xsl:element>
		</td><td><xsl:apply-templates select="ret-value"/></td></tr>
	    </xsl:for-each>
    </table>
    </xsl:for-each>
    <xsl:for-each select="report/result">
	    <h2>
 		<xsl:value-of select="scenario"/> - <xsl:value-of select="description"/>
		<xsl:element name="a">
			<xsl:attribute name="name">
				<xsl:value-of select="generate-id(current())"/>
			</xsl:attribute>
		</xsl:element>
	    </h2>
	    <p>Return value: <xsl:apply-templates select="ret-value"/></p>
	    <xsl:for-each select="changes/file">
		    <xsl:if test=". != ''">
		    <p><b><xsl:value-of select="@name"/></b></p>
		    <pre><xsl:value-of select="."/></pre>		
		    </xsl:if>
	    </xsl:for-each>
	    <h3>Output</h3>
	    <pre><xsl:value-of select="log"/></pre>	
	    <xsl:if test="./test">
	    <h3>Tests</h3>
	    <table border="0">
	    <tr><th align="left">Name</th><th colspan="2" align="left">Result</th></tr>
	    <xsl:for-each select="./test">
		<tr>
		<td><xsl:value-of select="name"/></td>
		<td><xsl:apply-templates select="@ret-value"/></td>
		<td><pre><xsl:value-of select="result"/></pre></td>
		</tr>
	    </xsl:for-each>
     	    </table>
	    </xsl:if>
    </xsl:for-each>
  </body>
  </html>
</xsl:template>

</xsl:stylesheet>
