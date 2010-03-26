<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="param">
	<p><xsl:value-of select="@name"/>: <xsl:value-of select="@value | text()"/></p>
</xsl:template>

<xsl:template match="scenario">
<p><i><xsl:value-of select="@name"/></i></p>
<p>Action class: <xsl:value-of select="@action-class-name"/></p>
<pre><xsl:apply-templates select="param"/></pre>
<ul>
	<xsl:for-each select="children/scenario">
		<li><xsl:apply-templates select="."/></li>
	</xsl:for-each>
</ul>
</xsl:template>

<xsl:template match="scenario-set">
<p><b><xsl:value-of select="@name"/></b></p>

<xsl:apply-templates select="scenario"/>
</xsl:template>

<xsl:template match="config-file">
<p><b><xsl:value-of select="@name"/></b></p>
<p>Input: <xsl:value-of select="@input"/></p>
<p>Output: <xsl:value-of select="@output"/></p>
<p>Transform: <xsl:value-of select="@transform"/></p>
<p>Handler class: <xsl:value-of select="handler/@class-name"/></p>
<pre><xsl:apply-templates select="handler/param"/></pre>
</xsl:template>

<xsl:template match="/plan">
  <html>
  <body>
    <h1>Fault injection plan - <xsl:value-of select="@name"/></h1>
    <p>Base directory: <xsl:value-of select="@baseDirectory"/></p>
    <h2>Configuration files</h2>
    <xsl:apply-templates select="config-files"/>
    <h2>Scenario sets</h2>
    <xsl:apply-templates select="scenario-sets"/>
    <h2>Runner</h2>
    <p><b>Runner class: <xsl:value-of select="runner/@class-name"/></b></p>
    <pre><xsl:apply-templates select="runner/param"/></pre>
  </body>
  </html>
</xsl:template>

</xsl:stylesheet>
