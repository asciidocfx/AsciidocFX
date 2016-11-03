<?xml version="1.0" encoding="UTF-8"?>
<!--
  Generates a FO document from a DocBook XML document using the DocBook XSL stylesheets.
  See http://docbook.sourceforge.net/release/xsl/1.78.1/doc/fo for all parameters.
-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:db="http://docbook.org/ns/docbook"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                exclude-result-prefixes="db">
    <!--
      The absolute URL imports point to system-wide locations by way of this /etc/xml/catalog entry:

        <rewriteURI
          uriStartString="http://docbook.sourceforge.net/release/xsl/current"
          rewritePrefix="file:///usr/share/sgml/docbook/xsl-stylesheets-%docbook-style-xsl-version%"/>

      %docbook-style-xsl-version% represents the version installed on the system.
    -->
    <!--<xsl:import href="http://docbook.sourceforge.net/release/xsl/current/fo/docbook.xsl"/>-->
    <xsl:import href="../docbook/fo/docbook.xsl"/>
    <xsl:import href="common.xsl"/>
    <xsl:import href="highlight.xsl"/>
    <xsl:import href="callouts.xsl"/>

    <!-- Enable extensions for FOP version 0.90 and later -->
    <xsl:param name="fop1.extensions">1</xsl:param>

    <xsl:attribute-set name="header.content.properties">
        <xsl:attribute name="font-family">Sans-serif,Arial</xsl:attribute>
        <xsl:attribute name="font-size">8pt</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="footer.content.properties">
        <xsl:attribute name="font-family">Sans-serif,Arial</xsl:attribute>
        <xsl:attribute name="font-size">8pt</xsl:attribute>
    </xsl:attribute-set>

    <!--
      AsciiDoc compat
    -->

    <xsl:template match="processing-instruction('asciidoc-br')">
        <fo:block/>
    </xsl:template>

    <xsl:template match="processing-instruction('asciidoc-hr')">
        <fo:block space-after="1em">
            <fo:leader leader-pattern="rule" rule-thickness="0.5pt" rule-style="solid" leader-length.minimum="100%"/>
        </fo:block>
    </xsl:template>

    <xsl:template match="processing-instruction('asciidoc-pagebreak')">
        <fo:block break-after='page'/>
    </xsl:template>

    <!--
      Font selectors
    -->

    <xsl:template name="pickfont-sans">
        <xsl:text>Sans-serif,Arial,Arial Unicode MS,Helvetica,serif,Georgia,Times New Roman</xsl:text>
    </xsl:template>

    <xsl:template name="pickfont-serif">
        <xsl:text>Sans-serif,Arial,Arial Unicode MS,Helvetica,serif,Georgia,Times New Roman</xsl:text>
    </xsl:template>

    <xsl:template name="pickfont-mono">
        <xsl:text>Liberation Mono,Lucida Console,Monaco,Consolas,Courier New,Courier,monospace,Arial Unicode MS,Lucida Sans Unicode</xsl:text>
    </xsl:template>

    <xsl:template name="pickfont-dingbat">
        <xsl:call-template name="pickfont-sans"/>
    </xsl:template>

    <xsl:template name="pickfont-symbol">
        <xsl:text>Symbol,ZapfDingbats</xsl:text>
    </xsl:template>

    <xsl:template name="pickfont-math">
        <xsl:text>Liberation Serif,Sans-serif,serif,Times-Roman</xsl:text>
    </xsl:template>

    <!--
      Fonts
    -->

    <xsl:param name="math.font.family">
        <xsl:call-template name="pickfont-math"/>
    </xsl:param>

    <xsl:param name="body.font.family">
        <xsl:call-template name="pickfont-sans"/>
    </xsl:param>

    <xsl:param name="sans.font.family">
        <xsl:call-template name="pickfont-sans"/>
    </xsl:param>

    <xsl:param name="monospace.font.family">
        <xsl:call-template name="pickfont-mono"/>
    </xsl:param>

    <!--
    <xsl:param name="dingbat.font.family">
      <xsl:call-template name="pickfont-dingbat"/>
    </xsl:param>

    <xsl:param name="symbol.font.family">
      <xsl:call-template name="pickfont-symbol"/>
    </xsl:param>
    -->

    <xsl:param name="title.font.family">
        <xsl:call-template name="pickfont-serif"/>
    </xsl:param>

    <!--
      Text properties
    -->

    <xsl:param name="hyphenate">false</xsl:param>
    <!--
    <xsl:param name="alignment">left</xsl:param>
    -->
    <xsl:param name="alignment">justify</xsl:param>
    <xsl:param name="line-height">1.5</xsl:param>
    <xsl:param name="body.font.master">10</xsl:param>
    <xsl:param name="body.font.size">
        <xsl:value-of select="$body.font.master"/><xsl:text>pt</xsl:text>
    </xsl:param>

    <xsl:attribute-set name="root.properties">
        <xsl:attribute name="color">
            <xsl:value-of select="$text.color"/>
        </xsl:attribute>
    </xsl:attribute-set>

    <!-- normal.para.spacing is the only attribute set applied to all paragraphs -->
    <xsl:attribute-set name="normal.para.spacing">
        <xsl:attribute name="text-indent">0</xsl:attribute>
        <xsl:attribute name="space-before.optimum">0</xsl:attribute>
        <xsl:attribute name="space-before.minimum">0</xsl:attribute>
        <xsl:attribute name="space-before.maximum">0</xsl:attribute>
        <xsl:attribute name="space-after.optimum">1em</xsl:attribute>
        <xsl:attribute name="space-after.minimum">0.8em</xsl:attribute>
        <xsl:attribute name="space-after.maximum">1.2em</xsl:attribute>
        <!--
        <xsl:attribute name="color"><xsl:value-of select="$text.color"/></xsl:attribute>
        -->
    </xsl:attribute-set>

    <xsl:attribute-set name="monospace.properties">
        <xsl:attribute name="color">
            <xsl:value-of select="$code.color"/>
        </xsl:attribute>
        <xsl:attribute name="font-weight">
            <xsl:value-of select="$code.font-weight"/>
        </xsl:attribute>
        <!--
        <xsl:attribute name="font-size">
          <xsl:value-of select="$body.font.master * 0.9"/><xsl:text>pt</xsl:text>
        </xsl:attribute>
        -->
        <!--<xsl:attribute name="background-color">-->
            <!--<xsl:value-of select="$code.background-color"/>-->
        <!--</xsl:attribute>-->
        <!--<xsl:attribute name="padding">-->
            <!--<xsl:choose>-->
                <!--<xsl:when test="$code.background-color != 'transparent'">.3em .25em .1em .25em</xsl:when>-->
                <!--<xsl:otherwise>0</xsl:otherwise>-->
            <!--</xsl:choose>-->
        <!--</xsl:attribute>-->
        <!--<xsl:attribute name="keep-together.within-column">always</xsl:attribute>-->
        <xsl:attribute name="font-size">8.5pt</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="verbatim.properties">
        <xsl:attribute name="color">
            <xsl:value-of select="$text.color"/>
        </xsl:attribute>
        <xsl:attribute name="font-weight">normal</xsl:attribute>
        <xsl:attribute name="border-top-style">solid</xsl:attribute>
        <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
        <xsl:attribute name="border-left-style">solid</xsl:attribute>
        <xsl:attribute name="border-right-style">solid</xsl:attribute>
        <xsl:attribute name="border-width">0.5pt</xsl:attribute>
        <xsl:attribute name="border-color">#BFBFBF</xsl:attribute>
        <xsl:attribute name="background-color">#BFBFBF</xsl:attribute>
        <xsl:attribute name="space-before.minimum">0</xsl:attribute>
        <xsl:attribute name="space-before.optimum">.2em</xsl:attribute>
        <xsl:attribute name="space-before.maximum">.4em</xsl:attribute>
        <xsl:attribute name="space-after.minimum">1em</xsl:attribute>
        <xsl:attribute name="space-after.optimum">1.2em</xsl:attribute>
        <xsl:attribute name="space-after.maximum">1.4em</xsl:attribute>
        <xsl:attribute name="hyphenate">false</xsl:attribute>
        <xsl:attribute name="wrap-option">wrap</xsl:attribute>
        <xsl:attribute name="white-space-collapse">false</xsl:attribute>
        <xsl:attribute name="white-space-treatment">preserve</xsl:attribute>
        <xsl:attribute name="linefeed-treatment">preserve</xsl:attribute>
        <xsl:attribute name="text-align">start</xsl:attribute>
        <!--<xsl:attribute name="keep-together.within-column">always</xsl:attribute>-->
    </xsl:attribute-set>

    <xsl:attribute-set name="monospace.verbatim.properties"
                       use-attribute-sets="monospace.properties verbatim.properties">

        <!--<xsl:attribute name="keep-together.within-column">always</xsl:attribute>-->

        <xsl:attribute name="font-size">8pt</xsl:attribute>
        <xsl:attribute name="text-align">start</xsl:attribute>
        <xsl:attribute name="wrap-option">wrap</xsl:attribute>
        <!--
        <xsl:attribute name="hyphenation-character">&#x25BA;</xsl:attribute>
        -->
    </xsl:attribute-set>

    <!-- shade.verbatim.style is added to listings when shade.verbatim is enabled -->
    <xsl:param name="shade.verbatim">1</xsl:param>

    <xsl:attribute-set name="shade.verbatim.style">
        <xsl:attribute name="background-color">#FBFBFB</xsl:attribute>
        <!--
        <xsl:attribute name="background-color">
          <xsl:choose>
            <xsl:when test="ancestor::db:note">
              <xsl:text>#D6DEE0</xsl:text>
            </xsl:when>
            <xsl:when test="ancestor::db:caution">
              <xsl:text>#FAF8ED</xsl:text>
            </xsl:when>
            <xsl:when test="ancestor::db:important">
              <xsl:text>#E1EEF4</xsl:text>
            </xsl:when>
            <xsl:when test="ancestor::db:warning">
              <xsl:text>#FAF8ED</xsl:text>
            </xsl:when>
            <xsl:when test="ancestor::db:tip">
              <xsl:text>#D5E1D5</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>#FFF</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
        -->
        <xsl:attribute name="color">
            <xsl:value-of select="$text.color"/>
        </xsl:attribute>
        <!--
        <xsl:attribute name="color">
          <xsl:choose>
            <xsl:when test="ancestor::db:note">
              <xsl:text>#334558</xsl:text>
            </xsl:when>
            <xsl:when test="ancestor::db:caution">
              <xsl:text>#334558</xsl:text>
            </xsl:when>
            <xsl:when test="ancestor::db:important">
              <xsl:text>#334558</xsl:text>
            </xsl:when>
            <xsl:when test="ancestor::db:warning">
              <xsl:text>#334558</xsl:text>
            </xsl:when>
            <xsl:when test="ancestor::db:tip">
              <xsl:text>#334558</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>#222</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
        -->
        <xsl:attribute name="padding">1em .5em .75em .5em</xsl:attribute>
        <!-- make sure block it aligns with block title -->
        <xsl:attribute name="margin-left">
            <xsl:value-of select="$title.margin.left"/>
        </xsl:attribute>
        <!--<xsl:attribute name="keep-together.within-column">always</xsl:attribute>-->
    </xsl:attribute-set>

    <!-- Text Decorations -->

    <!-- Underline and Strikethrough -->

    <xsl:template match="del">
        <fo:inline text-decoration="line-through">
            <xsl:call-template name="inline.charseq"/>
        </fo:inline>
    </xsl:template>

    <xsl:template match="u">
        <fo:inline text-decoration="underline">
            <xsl:call-template name="inline.charseq"/>
        </fo:inline>
    </xsl:template>

    <xsl:template match="phrase">
        <xsl:choose>
            <xsl:when test="@role='underline'">
                <fo:inline text-decoration="underline">
                    <xsl:call-template name="inline.charseq"/>
                </fo:inline>
            </xsl:when>
            <xsl:when test="@role='line-through'">
                <fo:inline text-decoration="line-through">
                    <xsl:call-template name="inline.charseq"/>
                </fo:inline>
            </xsl:when>
            <xsl:otherwise>
                <fo:inline color="{@role}">
                    <xsl:apply-templates/>
                </fo:inline>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="emphasis">
        <xsl:variable name="depth">
            <xsl:call-template name="dot.count">
                <xsl:with-param name="string">
                    <xsl:number level="multiple"/>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:variable>

        <xsl:choose>
            <xsl:when test="@role='bold' or @role='strong'">
                <xsl:call-template name="inline.boldseq"/>
            </xsl:when>
            <xsl:when test="@role='marked'">
                <fo:inline background-color="#ff0" font-style="normal">
                    <xsl:apply-templates/>
                </fo:inline>
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="$depth mod 2 = 1">
                        <fo:inline font-style="normal">
                            <xsl:apply-templates/>
                        </fo:inline>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="inline.italicseq"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!--
      Page layout
    -->

    <xsl:param name="page.height.portrait">24cm</xsl:param>
    <xsl:param name="page.width.portrait">16.5cm</xsl:param>

    <xsl:param name="page.margin.top">1.25cm</xsl:param>
    <xsl:param name="body.margin.top">1cm</xsl:param>

    <xsl:param name="page.margin.bottom">1.25cm</xsl:param>
    <xsl:param name="body.margin.bottom">1cm</xsl:param>

    <xsl:param name="region.before.extent">0.5cm</xsl:param>
    <xsl:param name="region.after.extent">0.5cm</xsl:param>

    <xsl:param name="page.margin.inner">2.35cm</xsl:param>
    <xsl:param name="page.margin.outer">1.65cm</xsl:param>

    <xsl:param name="body.margin.inner">0cm</xsl:param>
    <xsl:param name="body.margin.outer">0cm</xsl:param>

    <xsl:param name="body.start.indent">0cm</xsl:param>
    <!-- text indentation -->
    <xsl:param name="body.end.indent">0cm</xsl:param>
    <xsl:param name="double.sided">1</xsl:param>

    <xsl:param name="generate.toc">
        appendix toc,title
        article/appendix nop
        article title
        book toc,title
        chapter nop
        part toc,title
        preface toc,title
        qandadiv toc
        qandaset toc
        reference toc,title
        sect1 toc
        sect2 toc
        sect3 toc
        sect4 toc
        sect5 toc
        section toc
        set toc,title
    </xsl:param>


    <!--
      Table of Contents
    -->

    <xsl:param name="bridgehead.in.toc">0</xsl:param>
    <xsl:param name="toc.section.depth">2</xsl:param>

    <xsl:template name="toc.line">
        <xsl:variable name="id">
            <xsl:call-template name="object.id"/>
        </xsl:variable>

        <xsl:variable name="label">
            <xsl:apply-templates select="." mode="label.markup"/>
        </xsl:variable>

        <fo:block text-align-last="justify" end-indent="{$toc.indent.width}pt"
                  last-line-end-indent="-{$toc.indent.width}pt">
            <fo:inline keep-with-next.within-line="always">
                <fo:basic-link internal-destination="{$id}" color="#005498">
                    <!-- Chapter titles should be bold. -->
                    <!--
                    <xsl:choose>
                      <xsl:when test="local-name(.) = 'chapter'">
                        <xsl:attribute name="font-weight">bold</xsl:attribute>
                      </xsl:when>
                    </xsl:choose>
                    -->
                    <xsl:if test="$label != ''">
                        <!--
                        <xsl:value-of select="'Chapter '"/>
                        -->
                        <xsl:copy-of select="$label"/>
                        <xsl:value-of select="$autotoc.label.separator"/>
                    </xsl:if>
                    <xsl:apply-templates select="." mode="titleabbrev.markup"/>
                </fo:basic-link>
            </fo:inline>
            <fo:inline keep-together.within-line="always">
                <xsl:text> </xsl:text>
                <fo:leader leader-pattern="dots" leader-pattern-width="3pt"
                           leader-alignment="reference-area"
                           keep-with-next.within-line="always"/>
                <xsl:text> </xsl:text>
                <fo:basic-link internal-destination="{$id}" color="#005498">
                    <fo:page-number-citation ref-id="{$id}"/>
                </fo:basic-link>
            </fo:inline>
        </fo:block>
    </xsl:template>

    <!--
      Blocks
     -->

    <xsl:attribute-set name="formal.object.properties">
        <xsl:attribute name="space-before.minimum">0.8em</xsl:attribute>
        <xsl:attribute name="space-before.optimum">1em</xsl:attribute>
        <xsl:attribute name="space-before.maximum">1.2em</xsl:attribute>
        <xsl:attribute name="space-after.minimum">0.8em</xsl:attribute>
        <xsl:attribute name="space-after.optimum">1em</xsl:attribute>
        <xsl:attribute name="space-after.maximum">1.2em</xsl:attribute>
        <!-- Make examples, tables etc. break across pages -->
        <!--<xsl:attribute name="keep-together.within-column">always</xsl:attribute>-->
    </xsl:attribute-set>
    <!--
        <xsl:param name="formal.title.placement">
            figure after
            example before
            table before
        </xsl:param>
        -->

    <xsl:param name="formal.title.placement">
        figure after
        example before
        equation before
        table before
        procedure before
    </xsl:param>

    <xsl:attribute-set name="formal.title.properties">
        <xsl:attribute name="color">
            <xsl:value-of select="$caption.color"/>
        </xsl:attribute>
        <!--<xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>-->
    </xsl:attribute-set>

    <xsl:template match="*" mode="admon.graphic.width">
        <xsl:text>36pt</xsl:text>
    </xsl:template>

    <xsl:attribute-set name="admonition.properties">
        <xsl:attribute name="color">#6F6F6F</xsl:attribute>
        <xsl:attribute name="padding-left">18pt</xsl:attribute>
        <xsl:attribute name="border-left-width">.75pt</xsl:attribute>
        <xsl:attribute name="border-left-style">solid</xsl:attribute>
        <xsl:attribute name="border-left-color">
            <xsl:value-of select="$border.color"/>
        </xsl:attribute>
        <xsl:attribute name="margin-left">0</xsl:attribute>
        <!--<xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>-->
    </xsl:attribute-set>

    <xsl:attribute-set name="graphical.admonition.properties">
        <xsl:attribute name="margin-left">12pt</xsl:attribute>
        <xsl:attribute name="margin-right">12pt</xsl:attribute>
        <!--<xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>-->
    </xsl:attribute-set>

    <xsl:attribute-set name="example.properties" use-attribute-sets="formal.object.properties">
        <xsl:attribute name="border-width">1pt</xsl:attribute>
        <xsl:attribute name="border-style">solid</xsl:attribute>
        <xsl:attribute name="border-color">#E6E6E6</xsl:attribute>
        <xsl:attribute name="padding-top">12pt</xsl:attribute>
        <xsl:attribute name="padding-right">12pt</xsl:attribute>
        <xsl:attribute name="padding-bottom">0</xsl:attribute>
        <xsl:attribute name="padding-left">12pt</xsl:attribute>
        <xsl:attribute name="margin-left">0</xsl:attribute>
        <xsl:attribute name="margin-right">0</xsl:attribute>
        <!--<xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>-->
    </xsl:attribute-set>

    <xsl:attribute-set name="sidebar.properties" use-attribute-sets="formal.object.properties">
        <xsl:attribute name="border-style">solid</xsl:attribute>
        <xsl:attribute name="border-width">1pt</xsl:attribute>
        <xsl:attribute name="border-color">#D9D9D9</xsl:attribute>
        <xsl:attribute name="background-color">#F2F2F2</xsl:attribute>
        <xsl:attribute name="padding-start">16pt</xsl:attribute>
        <xsl:attribute name="padding-end">16pt</xsl:attribute>
        <xsl:attribute name="padding-top">18pt</xsl:attribute>
        <xsl:attribute name="padding-bottom">0</xsl:attribute>
        <!--<xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>-->
    </xsl:attribute-set>

    <xsl:attribute-set name="sidebar.title.properties">
        <xsl:attribute name="font-family">
            <xsl:value-of select="$title.fontset"/>
        </xsl:attribute>
        <xsl:attribute name="font-weight">
            <xsl:value-of select="$header.font-weight"/>
        </xsl:attribute>
        <xsl:attribute name="color">
            <xsl:value-of select="$caption.color"/>
        </xsl:attribute>
        <xsl:attribute name="font-size">
            <xsl:value-of select="$body.font.master * 1.6"/><xsl:text>pt</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="margin-bottom">
            <xsl:value-of select="$body.font.master"/><xsl:text>pt</xsl:text>
        </xsl:attribute>
        <!--<xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>-->
    </xsl:attribute-set>

    <!--
      Tables
    -->

    <xsl:attribute-set name="table.cell.padding">
        <xsl:attribute name="padding-left">4pt</xsl:attribute>
        <xsl:attribute name="padding-right">4pt</xsl:attribute>
        <xsl:attribute name="padding-top">2pt</xsl:attribute>
        <xsl:attribute name="padding-bottom">2pt</xsl:attribute>
    </xsl:attribute-set>

    <xsl:param name="table.frame.border.thickness">0.3pt</xsl:param>
    <xsl:param name="table.cell.border.thickness">0.15pt</xsl:param>
    <xsl:param name="table.cell.border.color">#5c5c4f</xsl:param>
    <xsl:param name="table.frame.border.color">#5c5c4f</xsl:param>
    <xsl:param name="table.cell.border.right.color">white</xsl:param>
    <xsl:param name="table.cell.border.left.color">white</xsl:param>
    <xsl:param name="table.frame.border.right.color">white</xsl:param>
    <xsl:param name="table.frame.border.left.color">white</xsl:param>

    <xsl:attribute-set name="table.cell.padding">
        <xsl:attribute name="padding-left">4pt</xsl:attribute>
        <xsl:attribute name="padding-right">4pt</xsl:attribute>
        <xsl:attribute name="padding-top">2pt</xsl:attribute>
        <xsl:attribute name="padding-bottom">2pt</xsl:attribute>
    </xsl:attribute-set>

    <!--Index recto-->

    <!--<xsl:param name="column.count.index">2</xsl:param>-->



    <!--<xsl:template name="index.titlepage.recto">-->
        <!--<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="index.titlepage.recto.style" margin-left="0pt" font-size="24.8832pt" font-family="{$title.fontset}" font-weight="bold">-->
            <!--<xsl:call-template name="division.title">-->
                <!--<xsl:with-param name="node" select="ancestor-or-self::index[1]"/>-->
            <!--</xsl:call-template>-->
        <!--</fo:block>-->

        <!--<xsl:choose>-->
            <!--<xsl:when test="indexinfo/subtitle">-->
                <!--<xsl:apply-templates mode="index.titlepage.recto.auto.mode" select="indexinfo/subtitle"/>-->
            <!--</xsl:when>-->
            <!--<xsl:when test="docinfo/subtitle">-->
                <!--<xsl:apply-templates mode="index.titlepage.recto.auto.mode" select="docinfo/subtitle"/>-->
            <!--</xsl:when>-->
            <!--<xsl:when test="info/subtitle">-->
                <!--<xsl:apply-templates mode="index.titlepage.recto.auto.mode" select="info/subtitle"/>-->
            <!--</xsl:when>-->
            <!--<xsl:when test="subtitle">-->
                <!--<xsl:apply-templates mode="index.titlepage.recto.auto.mode" select="subtitle"/>-->
            <!--</xsl:when>-->
        <!--</xsl:choose>-->

        <!--<xsl:apply-templates mode="index.titlepage.recto.auto.mode" select="indexinfo/itermset"/>-->
        <!--<xsl:apply-templates mode="index.titlepage.recto.auto.mode" select="docinfo/itermset"/>-->
        <!--<xsl:apply-templates mode="index.titlepage.recto.auto.mode" select="info/itermset"/>-->
    <!--</xsl:template>-->

    <!--
      Graphics
    -->

    <!-- graphicsize.extension only relevant for html output -->
    <!--
    <xsl:param name="graphicsize.extension">1</xsl:param>
    -->
    <xsl:param name="default.image.width">4in</xsl:param>
    <xsl:param name="default.inline.image.height">1em</xsl:param>

    <xsl:template name="process.image">
        <!-- if image is wider than the page, shrink it down to default.image.width -->
        <xsl:variable name="scalefit">
            <xsl:choose>
                <xsl:when test="$ignore.image.scaling != 0">0</xsl:when>
                <xsl:when test="@contentwidth">0</xsl:when>
                <xsl:when test="@contentdepth and @contentdepth != '100%'">0</xsl:when>
                <xsl:when test="@scale">0</xsl:when>
                <xsl:when test="@scalefit">
                    <xsl:value-of select="@scalefit"/>
                </xsl:when>
                <xsl:when test="@width or @depth">1</xsl:when>
                <xsl:otherwise>0</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:variable name="scale">
            <xsl:choose>
                <xsl:when test="$ignore.image.scaling != 0">0</xsl:when>
                <xsl:when test="@contentwidth or @contentdepth">1.0</xsl:when>
                <xsl:when test="@scale">
                    <xsl:value-of select="@scale div 100.0"/>
                </xsl:when>
                <xsl:otherwise>1.0</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:variable name="filename">
            <xsl:choose>
                <xsl:when test="local-name(.) = 'graphic' or local-name(.) = 'inlinegraphic'">
                    <xsl:call-template name="mediaobject.filename">
                        <xsl:with-param name="object" select="."/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:call-template name="mediaobject.filename">
                        <xsl:with-param name="object" select=".."/>
                    </xsl:call-template>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:variable name="content-type">
            <xsl:if test="@format">
                <xsl:call-template name="graphic.format.content-type">
                    <xsl:with-param name="format" select="@format"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:variable>

        <xsl:variable name="bgcolor">
            <xsl:call-template name="pi.dbfo_background-color">
                <xsl:with-param name="node" select=".."/>
            </xsl:call-template>
        </xsl:variable>

        <fo:external-graphic>
            <xsl:attribute name="src">
                <xsl:call-template name="fo-external-image">
                    <xsl:with-param name="filename">
                        <xsl:if test="$img.src.path != '' and not(starts-with($filename, '/')) and not(contains($filename, '://'))">
                            <xsl:value-of select="$img.src.path"/>
                        </xsl:if>
                        <xsl:value-of select="$filename"/>
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:attribute>

            <xsl:attribute name="width">
                <xsl:choose>
                    <xsl:when test="$ignore.image.scaling != 0">auto</xsl:when>
                    <xsl:when test="contains(@width,'%')">
                        <xsl:value-of select="@width"/>
                    </xsl:when>
                    <xsl:when test="@width and not(@width = '')">
                        <xsl:call-template name="length-spec">
                            <xsl:with-param name="length" select="@width"/>
                            <xsl:with-param name="default.units" select="'px'"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="not(@depth) and name(../..) != 'inlinemediaobject' and $default.image.width != ''">
                        <xsl:call-template name="length-spec">
                            <xsl:with-param name="length" select="$default.image.width"/>
                            <xsl:with-param name="default.units" select="'px'"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>auto</xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>

            <xsl:attribute name="height">
                <xsl:choose>
                    <xsl:when test="$ignore.image.scaling != 0">auto</xsl:when>
                    <xsl:when test="contains(@depth,'%')">
                        <xsl:value-of select="@depth"/>
                    </xsl:when>
                    <xsl:when test="@depth">
                        <xsl:call-template name="length-spec">
                            <xsl:with-param name="length" select="@depth"/>
                            <xsl:with-param name="default.units" select="'px'"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="name(../..) = 'inlinemediaobject' and $default.inline.image.height != ''">
                        <xsl:call-template name="length-spec">
                            <xsl:with-param name="length" select="$default.inline.image.height"/>
                            <xsl:with-param name="default.units" select="'px'"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>auto</xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>

            <xsl:attribute name="content-width">
                <xsl:choose>
                    <xsl:when test="$ignore.image.scaling != 0">auto</xsl:when>
                    <xsl:when test="contains(@contentwidth,'%')">
                        <xsl:value-of select="@contentwidth"/>
                    </xsl:when>
                    <xsl:when test="@contentwidth">
                        <xsl:call-template name="length-spec">
                            <xsl:with-param name="length" select="@contentwidth"/>
                            <xsl:with-param name="default.units" select="'px'"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="number($scale) != 1.0">
                        <xsl:value-of select="$scale * 100"/>
                        <xsl:text>%</xsl:text>
                    </xsl:when>
                    <xsl:when test="$scalefit = 1">scale-to-fit</xsl:when>
                    <xsl:otherwise>scale-down-to-fit</xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>

            <xsl:attribute name="content-height">
                <xsl:choose>
                    <xsl:when test="$ignore.image.scaling != 0">auto</xsl:when>
                    <xsl:when test="contains(@contentdepth,'%')">
                        <xsl:value-of select="@contentdepth"/>
                    </xsl:when>
                    <xsl:when test="@contentdepth">
                        <xsl:call-template name="length-spec">
                            <xsl:with-param name="length" select="@contentdepth"/>
                            <xsl:with-param name="default.units" select="'px'"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="number($scale) != 1.0">
                        <xsl:value-of select="$scale * 100"/>
                        <xsl:text>%</xsl:text>
                    </xsl:when>
                    <xsl:when test="$scalefit = 1">scale-to-fit</xsl:when>
                    <xsl:otherwise>scale-down-to-fit</xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>

            <xsl:if test="$content-type != ''">
                <xsl:attribute name="content-type">
                    <xsl:value-of select="concat('content-type:',$content-type)"/>
                </xsl:attribute>
            </xsl:if>

            <xsl:if test="$bgcolor != ''">
                <xsl:attribute name="background-color">
                    <xsl:value-of select="$bgcolor"/>
                </xsl:attribute>
            </xsl:if>

            <xsl:if test="@align">
                <xsl:attribute name="text-align">
                    <xsl:value-of select="@align"/>
                </xsl:attribute>
            </xsl:if>

            <xsl:if test="@valign">
                <xsl:attribute name="display-align">
                    <xsl:choose>
                        <xsl:when test="@valign = 'top'">before</xsl:when>
                        <xsl:when test="@valign = 'middle'">center</xsl:when>
                        <xsl:when test="@valign = 'bottom'">after</xsl:when>
                        <xsl:otherwise>auto</xsl:otherwise>
                    </xsl:choose>
                </xsl:attribute>
            </xsl:if>
        </fo:external-graphic>
    </xsl:template>

    <!--
        Math
      -->

    <xsl:template match="mml:math" xmlns:mml="http://www.w3.org/1998/Math/MathML">
        <fo:instream-foreign-object>
            <xsl:attribute name="font-family">
                <xsl:value-of select="$math.font.family"/>
            </xsl:attribute>
            <xsl:copy>
                <xsl:copy-of select="@*"/>
                <xsl:apply-templates/>
            </xsl:copy>
        </fo:instream-foreign-object>
    </xsl:template>

    <!--
      Titles
    -->

    <xsl:attribute-set name="section.title.properties">
        <xsl:attribute name="font-family">
            <xsl:value-of select="$title.fontset"/>
        </xsl:attribute>
        <xsl:attribute name="font-weight">
            <xsl:value-of select="$header.font-weight"/>
        </xsl:attribute>
        <xsl:attribute name="color">
            <xsl:value-of select="$title.color"/>
        </xsl:attribute>
        <!-- font size is calculated dynamically by section.heading template -->
        <xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
        <xsl:attribute name="space-before.minimum">1em</xsl:attribute>
        <xsl:attribute name="space-before.optimum">1.2em</xsl:attribute>
        <xsl:attribute name="space-before.maximum">1.2em</xsl:attribute>
        <xsl:attribute name="space-after.minimum">0.8em</xsl:attribute>
        <xsl:attribute name="space-after.optimum">1.0em</xsl:attribute>
        <xsl:attribute name="space-after.maximum">1.2em</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
        <!-- make sure block it aligns with block title -->
        <xsl:attribute name="start-indent">
            <xsl:value-of select="$title.margin.left"/>
        </xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="section.title.level1.properties">
        <xsl:attribute name="font-size">12pt</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="section.title.level2.properties">
        <xsl:attribute name="font-size">12pt</xsl:attribute>
        <xsl:attribute name="font-style">italic</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="section.title.level3.properties">
        <xsl:attribute name="font-size">12pt</xsl:attribute>
        <xsl:attribute name="font-style">italic</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="section.title.level4.properties">
        <xsl:attribute name="font-size">12pt</xsl:attribute>
        <xsl:attribute name="font-style">italic</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="section.title.level5.properties">
        <xsl:attribute name="font-size">12pt</xsl:attribute>
        <xsl:attribute name="font-style">italic</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="section.title.level6.properties">
        <xsl:attribute name="font-size">12pt</xsl:attribute>
        <xsl:attribute name="font-style">italic</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="component.title.properties">
        <xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
        <xsl:attribute name="space-before.optimum">
            <xsl:value-of select="concat($body.font.master, 'pt')"/>
        </xsl:attribute>
        <xsl:attribute name="space-before.minimum">
            <xsl:value-of select="concat($body.font.master, 'pt')"/>
        </xsl:attribute>
        <xsl:attribute name="space-before.maximum">
            <xsl:value-of select="concat($body.font.master, 'pt')"/>
        </xsl:attribute>
        <xsl:attribute name="space-after.optimum">
            <xsl:value-of select="concat($body.font.master, 'pt')"/>
        </xsl:attribute>
        <xsl:attribute name="space-after.minimum">
            <xsl:value-of select="concat($body.font.master, 'pt')"/>
        </xsl:attribute>
        <xsl:attribute name="space-after.maximum">
            <xsl:value-of select="concat($body.font.master, 'pt')"/>
        </xsl:attribute>
        <xsl:attribute name="hyphenate">false</xsl:attribute>
        <xsl:attribute name="font-weight">
            <xsl:value-of select="$header.font-weight"/>
        </xsl:attribute>
        <!-- color support on fo:block, to which this gets applied, added in DocBook XSL 1.78.1 -->
        <xsl:attribute name="color">
            <xsl:value-of select="$title.color"/>
        </xsl:attribute>
        <!--
        <xsl:attribute name="color">
          <xsl:choose>
            <xsl:when test="not(parent::db:chapter | parent::db:article | parent::db:appendix)">
              <xsl:value-of select="$title.color"/>
            </xsl:when>
            <xsl:otherwise>inherit</xsl:otherwise>
          </xsl:choose>
        </xsl:attribute>
        -->
        <xsl:attribute name="text-align">
            <xsl:choose>
                <xsl:when
                        test="((parent::db:article | parent::db:articleinfo) and not(ancestor::db:book) and not(self::db:bibliography)) or (parent::db:slides | parent::db:slidesinfo)">
                    center
                </xsl:when>
                <xsl:otherwise>left</xsl:otherwise>
            </xsl:choose>
        </xsl:attribute>
        <xsl:attribute name="start-indent">
            <xsl:value-of select="$title.margin.left"/>
        </xsl:attribute>
    </xsl:attribute-set>

    <!-- override to set color and move to separate line -->
    <xsl:template match="db:formalpara/db:title | formalpara/title">
        <xsl:variable name="titleStr">
            <xsl:apply-templates/>
        </xsl:variable>
        <xsl:variable name="lastChar">
            <xsl:if test="$titleStr != ''">
                <xsl:value-of select="substring($titleStr,string-length($titleStr),1)"/>
            </xsl:if>
        </xsl:variable>

<!--        <fo:inline font-weight="bold"
                   color="{$caption.color}"
                   keep-with-next.within-line="always">
            <xsl:copy-of select="$titleStr"/>
            <xsl:if test="$lastChar != ''
                    and not(contains($runinhead.title.end.punct, $lastChar))">
                <xsl:value-of select="$runinhead.default.title.end.punct"/>
            </xsl:if>
        </fo:inline>-->

        <xsl:if test="$runinhead.default.title.break.after = '1'">
            <fo:block font-weight="bold" color="{$caption.color}">
                <xsl:copy-of select="$titleStr"/>
                <xsl:if test="$lastChar != '' and not(contains($runinhead.title.end.punct, $lastChar))">
                    <xsl:value-of select="$runinhead.default.title.end.punct"/>
                </xsl:if>
            </fo:block>
        </xsl:if>
        <xsl:if test="not($runinhead.default.title.break.after = '1')">
            <fo:inline font-weight="bold"
                       color="{$caption.color}"
                       keep-with-next.within-line="always"
                       padding-end="1em">
                <xsl:copy-of select="$titleStr"/>
                <xsl:if test="$lastChar != '' and not(contains($runinhead.title.end.punct, $lastChar))">
                    <xsl:value-of select="$runinhead.default.title.end.punct"/>
                </xsl:if>
            </fo:inline>
            <xsl:text>&#160;</xsl:text>
        </xsl:if>
    </xsl:template>

    <!--
      Anchors & Links
    -->

    <xsl:attribute-set name="xref.properties">
        <xsl:attribute name="color">
            <xsl:value-of select="$link.color"/>
        </xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="simple.xlink.properties">
        <xsl:attribute name="color">
            <xsl:value-of select="$link.color"/>
        </xsl:attribute>
    </xsl:attribute-set>

    <!--
      Lists
    -->

    <xsl:param name="qandadiv.autolabel">0</xsl:param>
    <xsl:param name="variablelist.as.blocks">1</xsl:param>

    <xsl:attribute-set name="list.block.properties">
        <xsl:attribute name="margin-left">0.4em</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="list.block.spacing">
        <xsl:attribute name="space-before.optimum">1.2em</xsl:attribute>
        <xsl:attribute name="space-before.minimum">1em</xsl:attribute>
        <xsl:attribute name="space-before.maximum">1.4em</xsl:attribute>
        <xsl:attribute name="space-after.optimum">1.2em</xsl:attribute>
        <xsl:attribute name="space-after.minimum">1em</xsl:attribute>
        <xsl:attribute name="space-after.maximum">1.4em</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="list.item.spacing">
        <xsl:attribute name="space-before.optimum">0.4em</xsl:attribute>
        <xsl:attribute name="space-before.minimum">0.4em</xsl:attribute>
        <xsl:attribute name="space-before.maximum">0.4em</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="variablelist.term.properties">
        <xsl:attribute name="font-weight">bold</xsl:attribute>
    </xsl:attribute-set>

    <xsl:template name="itemizedlist.label.markup">
        <xsl:param name="itemsymbol" select="'disc'"/>

        <xsl:choose>
            <xsl:when test="$itemsymbol='none'"></xsl:when>
            <xsl:when test="$itemsymbol='circle'">&#x25E6;</xsl:when>
            <xsl:when test="$itemsymbol='disc'">&#x2022;</xsl:when>
            <xsl:when test="$itemsymbol='square'">&#x25AA;</xsl:when>
            <xsl:when test="$itemsymbol='checked'">&#x25A0;</xsl:when>
            <xsl:when test="$itemsymbol='unchecked'">&#x25A1;</xsl:when>
            <xsl:otherwise>&#x2022;</xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!--
      Title pages
    -->

    <xsl:param name="titlepage.color">#6F6F6F</xsl:param>
    <!--
    <xsl:param name="titlepage.color" select="$title.color"/>
    -->

    <xsl:attribute-set name="book.titlepage.recto.style">
        <xsl:attribute name="font-family">
            <xsl:value-of select="$title.fontset"/>
        </xsl:attribute>
        <xsl:attribute name="color">
            <xsl:value-of select="$titlepage.color"/>
        </xsl:attribute>
        <xsl:attribute name="font-weight">normal</xsl:attribute>
        <xsl:attribute name="font-size">12pt</xsl:attribute>
        <xsl:attribute name="text-align">center</xsl:attribute>
    </xsl:attribute-set>

    <!--<xsl:attribute-set name="chapter.titlepage.recto.style">-->
        <!--<xsl:attribute name="color">-->
            <!--<xsl:value-of select="$chapter.title.color"/>-->
        <!--</xsl:attribute>-->
        <!--<xsl:attribute name="background-color">white</xsl:attribute>-->
        <!--<xsl:attribute name="font-size">24pt</xsl:attribute>-->
        <!--<xsl:attribute name="font-weight">normal</xsl:attribute>-->
        <!--<xsl:attribute name="text-align">left</xsl:attribute>-->
        <!--&lt;!&ndash;xsl:attribute name="wrap-option">no-wrap</xsl:attribute&ndash;&gt;-->
        <!--&lt;!&ndash;-->
        <!--<xsl:attribute name="padding-left">1em</xsl:attribute>-->
        <!--<xsl:attribute name="padding-right">1em</xsl:attribute>-->
        <!--&ndash;&gt;-->
    <!--</xsl:attribute-set>-->


    <xsl:attribute-set name="component.titlepage.properties">
        <xsl:attribute name="span">all</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="chapter.titlepage.recto.style">
        <xsl:attribute name="text-align">right</xsl:attribute>
        <xsl:attribute name="font-family">
            <xsl:value-of select="$title.font.family"/>
        </xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="chapter.titlepage.label.properties">
        <xsl:attribute name="font-size">48pt</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="chapter.titlepage.title.properties">
        <xsl:attribute name="font-size">20pt</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
    </xsl:attribute-set>

    <xsl:template match="title" mode="chapter.titlepage.recto.auto.mode">
        <fo:block xsl:use-attribute-sets="chapter.titlepage.recto.style"  margin-bottom="1.7cm">
            <fo:block xsl:use-attribute-sets="chapter.titlepage.label.properties">
                <xsl:apply-templates select=".." mode="label.markup"/>
            </fo:block>
            <fo:block xsl:use-attribute-sets="chapter.titlepage.title.properties">
                <xsl:apply-templates select=".." mode="title.markup"/>
            </fo:block>
            <fo:block text-align-last="justify">
                <fo:leader
                    leader-pattern="rule"
                    leader-pattern-width="5pt" rule-thickness="5pt"  />
            </fo:block>
        </fo:block>
    </xsl:template>

    <xsl:attribute-set name="preface.titlepage.recto.style" use-attribute-sets="chapter.titlepage.recto.style">
        <xsl:attribute name="font-family">
            <xsl:value-of select="$title.fontset"/>
        </xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="part.titlepage.recto.style">
        <xsl:attribute name="color">
            <xsl:value-of select="$title.color"/>
        </xsl:attribute>
        <xsl:attribute name="text-align">center</xsl:attribute>
    </xsl:attribute-set>

    <!-- override to set different color for book title -->
    <xsl:template match="db:title | title" mode="book.titlepage.recto.auto.mode">
        <fo:block xsl:use-attribute-sets="book.titlepage.recto.style" text-align="center" font-size="24.8832pt"
                  space-before="18.6624pt">
            <!-- FIXME don't use hardcoded value here -->
            <xsl:attribute name="color">black</xsl:attribute>
            <xsl:attribute name="font-weight">
                <xsl:value-of select="$header.font-weight"/>
            </xsl:attribute>
            <xsl:call-template name="division.title">
                <xsl:with-param name="node" select="ancestor-or-self::db:book[1] | ancestor-or-self::book[1]"/>
            </xsl:call-template>
        </fo:block>
    </xsl:template>

    <!-- add revision info on title page -->
    <xsl:template match="db:revision | revision" mode="book.titlepage.recto.auto.mode">
        <fo:block xsl:use-attribute-sets="book.titlepage.recto.style" text-align="center" font-size="14.4pt"
                  space-before="1in" font-family="{$title.fontset}">
            <xsl:call-template name="gentext">
                <xsl:with-param name="key" select="'Revision'"/>
            </xsl:call-template>
            <xsl:call-template name="gentext.space"/>
            <xsl:apply-templates select="db:revnumber | revnumber" mode="titlepage.mode"/>
        </fo:block>
        <fo:block xsl:use-attribute-sets="book.titlepage.recto.style" text-align="center" font-size="14.4pt"
                  font-family="{$title.fontset}">
            <xsl:apply-templates select="db:date | date" mode="titlepage.mode"/>
        </fo:block>
    </xsl:template>

    <!-- override to force use of title, author and one revision on titlepage -->
    <xsl:template name="book.titlepage.recto">
        <xsl:choose>
            <xsl:when test="db:bookinfo/db:title | bookinfo/title">
                <xsl:apply-templates mode="book.titlepage.recto.auto.mode"
                                     select="db:bookinfo/db:title | bookinfo/title"/>
            </xsl:when>
            <xsl:when test="db:info/db:title | info/title">
                <xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="db:info/db:title | info/title"/>
            </xsl:when>
            <xsl:when test="db:title | title">
                <xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="db:title | title"/>
            </xsl:when>
        </xsl:choose>

        <xsl:choose>
            <xsl:when test="db:bookinfo/db:subtitle | bookinfo/subtitle">
                <xsl:apply-templates mode="book.titlepage.recto.auto.mode"
                                     select="db:bookinfo/db:subtitle | bookinfo/subtitle"/>
            </xsl:when>
            <xsl:when test="db:info/db:subtitle | info/subtitle">
                <xsl:apply-templates mode="book.titlepage.recto.auto.mode"
                                     select="db:info/db:subtitle | info/subtitle"/>
            </xsl:when>
            <xsl:when test="db:subtitle | subtitle">
                <xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="db:subtitle | subtitle"/>
            </xsl:when>
        </xsl:choose>

        <xsl:choose>
            <xsl:when test="db:bookinfo//db:author | bookinfo//author">
                <xsl:apply-templates mode="book.titlepage.recto.auto.mode"
                                     select="db:bookinfo//db:author | bookinfo//author"/>
            </xsl:when>
            <xsl:when test="db:info//db:author | info//author">
                <xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="db:info//db:author | info//author"/>
            </xsl:when>
        </xsl:choose>

        <xsl:choose>
            <xsl:when test="db:bookinfo/db:revhistory/db:revision[1] | bookinfo/revhistory/revision[1]">
                <xsl:apply-templates mode="book.titlepage.recto.auto.mode"
                                     select="db:bookinfo/db:revhistory/db:revision[1] | bookinfo/revhistory/revision[1]"/>
            </xsl:when>
            <xsl:when test="db:info/db:revhistory/db:revision[1] | info/revhistory/revision[1]">
                <xsl:apply-templates mode="book.titlepage.recto.auto.mode"
                                     select="db:info/db:revhistory/db:revision[1] | info/revhistory/revision[1]"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <!-- cut out these pages -->
    <xsl:template name="book.titlepage.before.verso"/>
    <xsl:template name="book.titlepage.verso"/>

    <!--
      Footnotes
    -->

    <xsl:param name="footnote.number.format">1</xsl:param>
    <xsl:param name="footnote.number.symbols"/>

    <xsl:param name="footnote.font.size">
        <xsl:value-of select="$body.font.master * 0.8"/><xsl:text>pt</xsl:text>
    </xsl:param>

    <xsl:attribute-set name="footnote.mark.properties">
        <!-- override font-family for mark since we don't need full font set -->
        <xsl:attribute name="font-family">
            <xsl:value-of select="$body.font.family"/>
        </xsl:attribute>
        <xsl:attribute name="font-size">
            <xsl:value-of select="$body.font.master * 0.8"/><xsl:text>pt</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="color">
            <xsl:value-of select="$link.color"/>
        </xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="padding">0 1pt</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="footnote.properties">
        <!-- force color since it will otherwise inherit from location of footnote text -->
        <xsl:attribute name="color">
            <xsl:value-of select="$text.color"/>
        </xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="footnote.sep.leader.properties">
        <xsl:attribute name="color">
            <xsl:value-of select="$border.color"/>
        </xsl:attribute>
        <xsl:attribute name="leader-pattern">rule</xsl:attribute>
        <xsl:attribute name="leader-length">2in</xsl:attribute>
        <xsl:attribute name="rule-thickness">0.5pt</xsl:attribute>
    </xsl:attribute-set>

    <!-- Index does not use normal.para.spacing, so set text.color explicitly -->
    <!--
    <xsl:attribute-set name="index.div.title.properties">
      <xsl:attribute name="color"><xsl:value-of select="$text.color"/></xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="index.entry.properties">
      <xsl:attribute name="color"><xsl:value-of select="$text.color"/></xsl:attribute>
    </xsl:attribute-set>
    -->

    <xsl:attribute-set name="index.entry.properties">
        <xsl:attribute name="font-size">10pt</xsl:attribute>
    </xsl:attribute-set>

    <!--Centering figure title-->
    <xsl:attribute-set name="formal.title.properties">
        <xsl:attribute name="text-align">
            <xsl:choose>
                <xsl:when test="self::figure">center</xsl:when>
                <xsl:otherwise>left</xsl:otherwise>
            </xsl:choose>
        </xsl:attribute>
        <xsl:attribute name="font-size">
            <xsl:value-of select="8"/>
            <xsl:text>pt</xsl:text>
        </xsl:attribute>
        <!--<xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>-->
    </xsl:attribute-set>

    <!-- Center all the images even if they do not have a title-->
    <xsl:attribute-set name="informalfigure.properties">
        <xsl:attribute name="text-align">center</xsl:attribute>
    </xsl:attribute-set>
</xsl:stylesheet>
