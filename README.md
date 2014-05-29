AsciidocFX
==========

### Asciidoc Editor based on JavaFX 8

AsciidocFX is editor for [Asciidoc Markup Language](http://www.methods.co.nz/asciidoc/).

It is a basic editor that reflects any changes in editor to Preview Pane immediately.

#### FullScreen Support

Follow the View > Full Screen

<img src="http://kodcu.com/ascii-full-screen.png" width="700" height="371"/>

#### Closable Preview Pane

Follow the View > Toggle Preview

<img src="http://kodcu.com/ascii-toggle-preview.png" width="700" height="371"/>

#### External Browser Support

Open http://localhost:8080/ and see changes on your favorite Browser.

<img src="http://kodcu.com/ascii-external.png" width="700" height="371"/>

#### Table Generator Feature

Follow the View > Generate Table

<img src="http://kodcu.com/ascii-table-generator.png" width="700" height="371"/>

### How to Build executable JAR

1. Install JDK 8 and set JAVA_HOME properly
2. Enter AsciidocFX directory and run
    * $ mvn clean install
3. Follow to target directory and run
    * $ java -jar AsciidocFX.jar

### How to Build Native installer (RPM,DKG,DEB,EXE,MSI)
1. If you want to build native installer on your platform
    * Run $ mvn jfx:native
2. Dependencies
    * For Windows
        * Wix 3.0 or Inno Setup 5
    * For Linux
        * RPMBuild or dpkg

If you want not to build AsciidocFX, download the Jar with this [Link](http://kodcu.com/AsciidocFX.jar)