# Changelog

All notable changes are documented in this file.

## [Version ~1.7.4](https://github.com/asciidocfx/AsciidocFX/releases/tag/v1.7.4) (23.04.2021)

* install4j 9 upgrade
* dependency updates plantuml, spring boot
* Java and Java FX 16
* Enable Zen (Preview only mode)
* Fix plantuml service crash issue
* Add math2 shortcut
* Fix issue on file output naming which includes more than one dot.
* Font configurations
* Experimental Mermaid support
* Dark theme improvements
* Set config directory from command line

## [Version ~1.7.3](https://github.com/asciidocfx/AsciidocFX/releases/tag/v1.7.3) (16.05.2020)

* /favicon resolution bug fix
* Resolving docname docdir etc.
* Version upgrades (plantuml, fop, spring-boot etc.)

## [Version ~1.7.2](https://github.com/asciidocfx/AsciidocFX/releases/tag/v1.7.2) (07.04.2020)

* Extension bug fixes
* Version upgrades (javafx, plantuml, fop, spring-boot etc.)

## [Version ~1.7.1](https://github.com/asciidocfx/AsciidocFX/releases/tag/v1.7.1) (03.08.2019)

* Extension bug fixes
* Fixing Highlight.js integration

## [Version ~1.7.0](https://github.com/asciidocfx/AsciidocFX/releases/tag/v1.7.0) (08.06.2019)

* JavaFX 12 support
* Asciidoctor.js 2x support
* Show hidden files in file context menu
* Improve preview CSS for different screen types
* TerminalFX, PlantUML, Ace editor version upgrades
* Bux fixes
* Drop Markdown support (Due to Nashorn deprecation)

## [Version ~1.6.9](https://github.com/asciidocfx/AsciidocFX/releases/tag/v1.6.9) (05.10.2018)

* Fix outline issue for include:: content
* Resolve files correctly
* Show detached preview in front

## [Version ~1.6.8](https://github.com/asciidocfx/AsciidocFX/releases/tag/v1.6.8) (26.08.2018)

* Added missing noto serif fonts
* Added missing dejavu sans mono font to ace
* Character code handle improvement in editor

## [Version ~1.6.7](https://github.com/asciidocfx/AsciidocFX/releases/tag/v1.6.7) (18.08.2018)

* Use newer version of TerminalFX
* Use .asc as Asciidoc file extension
* Show alerts always on front

## [Version ~1.4.4](https://github.com/asciidocfx/AsciidocFX/releases/tag/v1.4.4) (13.12.2015)

AsciidocFX was elected as one of [the Duke's Choice Award 2015 Winners](https://community.oracle.com/docs/DOC-949972#wfp)!

As always, you can fully examine the changes between *v1.3.9* and *v1.4.4* via [full changelog](https://github.com/asciidocfx/AsciidocFX/compare/v1.3.9...v1.4.4).

### Features

* AFX supports a spell-checker component. You can enable/disable it and change the default language, which is Turkish, among the existing ones.

### Changes

* Several components' version updated
* Improving UI design based on users experience
* Minor bug fixes

## [Version 1.3.9](https://github.com/asciidocfx/AsciidocFX/releases/tag/v1.3.9) (13.10.2015)

As always, you can fully examine the [full changelog](https://github.com/asciidocfx/AsciidocFX/compare/v1.3.8...v1.3.9).

### Features

* The new embedded local terminal comes bundled with AFX! Right-click a folder in the Workdir panel and click 'Open in Terminal'. In the local terminal, you can create a number of sessions by clicking +. To close an active session, click X or right-click a session tab, and then choose 'Close' option.

## [Version 1.3.8](https://github.com/asciidocfx/AsciidocFX/releases/tag/v1.3.8) (13.09.2015)

After all of the RCs, It's time to express all the noteworthy features. To examine all the details, please visit [Full Changelog](https://github.com/asciidocfx/AsciidocFX/compare/v1.3.6...v1.3.8)

### Features

* Making a presentation in AsciidocFX is not a dream anymore! Thanks to [Rahman Usta](https://github.com/rahmanusta), AsciidocFX now allows you to make slides by means of [Reveal.js](https://github.com/hakimel/reveal.js/) and [Deck.js](https://github.com/imakewebthings/deck.js) backends. To make this possible, just right-click anywhere on the workdir panel and then follow the path `new/slide`.
* As of 1.3.8, AFX also provides a new backend service called Odt. Using the service, you can convert an asciidoc/markdown document into a file ending with `odt` which stands for word processing (text) documents.
* Another facility comes into play in this release which is _Outline_ View! The Outline view displays an outline of a structured asciidoc/markdown file that is currently open in the editor area. You can jump to an intended section easily.
* You can manipulate the default properties of AFX in the settings panel. To reach that panel, click 3dots(...) / Settings *or* `Ctrl+F4` . You will see a few sections such as the settings of preview panel and the locations part which lets you override/edit the default css values as well as set the mathjs and kindlegen path.
* By default, auto-saving feature is enable so that when you switch to other applications or when AFX is idle, your documents will be saved by AFX. To disable it, go to the general Settings panel.
* Since AFX supports 5 different services, logging is a must for those who want to know whether a problem occurs or not during every process. To this end, a log view is now available at the bottom of the editor. You are given a few options based on the log view like classifying the log messages according to the log levels and browsing the corresponding log directory.
* AsciidocFX Users can now edit all file formats such as JSON, XML, HTML and so on in the editor.
* Since AFX lets you edit HTML documents, you can likewise preview the content on the HTML preview section.
* You can save the frequently used directories (i.e. your favorite directories) by right-clicking a folder in the Workdir (i.e. treeview ) panel.
* Viewing rendered documents simultaneously in the preview section is now up to you. You can toggle this option from the preview. Just right click somewhere on the preview section and check "Stop Rendering" [#95]((https://github.com/asciidocfx/AsciidocFX/issues/95))
* Next to the 'stop rendering' feature, AFX also provides 'stop jumping' and 'stop scrolling' features over the preview section.
* AFX has a Turkish README file.

### Changes

* The AsciidocFX Cheetsheat updated.
* Home button removed.
* Minor bugs fixed.

## [Version 1.3.6](https://github.com/asciidocfx/AsciidocFX/releases/tag/v1.3.6) (03.04.2015)

### Features

* A new handy extension comes into play: Users can visually illustrate their data with 8 different chart components (i.e. [pie](https://github.com/asciidocfx/AsciidocFX/wiki/Chart-Extension#pie-chart), [line](https://github.com/asciidocfx/AsciidocFX/wiki/Chart-Extension#line-chart), [area](https://github.com/asciidocfx/AsciidocFX/wiki/Chart-Extension#area-chart), [bar](https://github.com/asciidocfx/AsciidocFX/wiki/Chart-Extension#bar-chart), [scatter](https://github.com/asciidocfx/AsciidocFX/wiki/Chart-Extension#scatter-chart), [bubble](https://github.com/asciidocfx/AsciidocFX/wiki/Chart-Extension#bubble-chart), [stacked-area](https://github.com/asciidocfx/AsciidocFX/wiki/Chart-Extension#stacked-area-chart), and [stacked-bar](https://github.com/asciidocfx/AsciidocFX/wiki/Chart-Extension#stacked-bar-chart) charts) along with a few [options](https://github.com/asciidocfx/AsciidocFX/wiki/Chart-Extension-Options) supported by AsciidocFX, all of which belong to JavaFX.
* AFX has [block macros](https://github.com/asciidocfx/AsciidocFX/wiki/Chart-Extension#using-external-csv-data) to reference data in external CSV file so that, users can directly refer to it without giving data inside block.
* As of 1.3.6, AFX does not prompt a warning dialog for empty "new *" tabs on exit.
* Chart extension menu was added to the editor toolbar for asciidoc users.
* A shortcut to (convert and paste) copied selection (i.e. `Ctrl+V`).
* A shortcut to directly paste copied selection (i.e. RAW content) with no any conversion (i.e. `Ctrl+Shift+V`).

### Changes

* The AsciidocFX Cheetsheat updated.
* The AsciidocFX Sample Book updated.
* Asking "Paste" and "Paste Raw" options removed. Instead, AFX provides two shortcuts which we mention in the features section. Note that, you can still use these two options manually by right-clicking on the editor.

## [Version 1.3.5](https://github.com/asciidocfx/AsciidocFX/releases/tag/v1.3.5) (28.03.2015)

* Almost bug-fixes
* Prevent accident closing when there is changed files.

## [Version 1.3.4](https://github.com/asciidocfx/AsciidocFX/releases/tag/v1.3.4) (22.03.2015)

There are many features in this release, let's see all of them. To examine all the details, please visit [Full Changelog](https://github.com/asciidocfx/AsciidocFX/compare/v1.3.3...v1.3.4).

### Features

* Big news, AsciidocFX salutes markdown users! As of 1.3.4, It facilitates users to open/edit/delete markdown documents along with its features as well as to convert markdown documents to asciidoc documents thanks to [Rahman Usta](https://github.com/rahmanusta), by [extending](https://github.com/asciidocfx/AsciidocFX/blob/master/src/main/resources/public/js/marked-extension.js) the [marked](https://github.com/chjj/marked) framework.
* Users can transfer their [Gitbooks](https://www.gitbook.com/) to Asciibooks. You can find this service from the ellipsis section in the file browser. In order to start this service, after selecting your existing gitbook directory you need to give another empty directory in which the corresponding asciidoc files will be located.
* Our another useful handy framework called " [treeview](https://github.com/asciidocfx/highlight-treeview.js) syntax highlighting" is now utilized. We give you two choices to make your own filesystem view.
* A new *remove* option is of worth in order to delete single or multiple files in the file browser.
* A new *create* file option is available over the file tree view. Note that you can only use this feature if you right-click a folder.
* Opening multiple times of an already opened document was disabled [#58]((https://github.com/asciidocfx/AsciidocFX/issues/58)).
* A new save alert message comes into play for unsaved files on exit in order to prevent losing unsaved work [#52]((https://github.com/asciidocfx/AsciidocFX/issues/52)).
* "Paste" and "Paste Raw" options; If the copied content is HTML, you will be given two choices before pasting the content into the editor.
** When you used "Paste" option, AsciidocFX will convert and paste HTML content to Asciidoc or Markdown format automatically
** When you used "Paste Raw" option, AsciidocFX will paste RAW input
* Since AFX supports two markup languages, users can choose which markup languages they want to use in each "new *" tab at startup. (By default, Ascidoc is selected).
* The editor toolbar includes new facilities for users who want to easily adapt and use Asciidoc features such as blocks, admonitions (e.g. tip), document helpers (e.g. appendix), and extensions AFX itself provides.
* You can fully control the history of the preview section of each rendered document by using "Go Back/Forward" navigation.
* "Copy Source/HTML/Text" features are provided in the preview section for each selected text.
* _Index selection_ feature for asciidoc users; you can specify index terms by using this option after right-clicking (a) selected text(s) in the editor section.
* Several alert messages are included to clearly address some points such as when markdown does not support a text format while asciidoc does.
* Markdown File extension association provided.

### Changes

* The AsciidocFX Sample Book was updated to express all the available features of asciidoc.
* The editor toolbar obtained a new design.
* Since the progress indicator of JavaFX is a bit problematic, we decided to use [progressbar.js](https://github.com/kimmobrunfeldt/progressbar.js).

## [v1.3.3](https://github.com/asciidocfx/AsciidocFX/releases/tag/v1.3.3) (17.02.2015)

### Features

* `.svg` file extension is now supported among the other image files.
* A new HTML to AsciiDoc Converter called [to-asciidoc](_https://github.com/asciidocfx/to-asciidoc)_ maintained by us, is utilized in the program as well so that, user can directly copy an HTML document and paste the converted one including AsciiDoc structure(s) into the editor.

### Changes

* A new refresh button to refresh the preview section, it is especially important for mentioned images (i.e. `image::`) if presents.

## [v1.3.2](https://github.com/asciidocfx/AsciidocFX/releases/tag/v1.3.2) (01.02.2015)

### Features

* Asciidoc File extension association.
* Platform-specific file sorting in the TreeView.
* Filesystem functionalities such as Go Home/Up [#42]((https://github.com/asciidocfx/AsciidocFX/issues/42)).
* _Save as_ functionality for HTML, PDF, DocBook, and Ebook services.
* Files can be now opened from terminal.
* Tooltips for the buttons of the editor bars. [#43]((https://github.com/asciidocfx/AsciidocFX/issues/43)).
* [Gitter.im](https://gitter.im/asciidocfx/AsciidocFX) public chat room.
* Easily accessible to available links such as _bug report_ within the program.
* A shortcut to include a quote block in a doc file (i.e. type `quote` press `tab`).
* Another option is provided to copy the current HTML source with embedded images if included.
* A go-to-workdir facility for each opened tab.
* A shortcut to maximize the editor pane (i.e. `Ctrl+M`).

### Changes

* `book.*` file depencency cancelled. Now each AsciiDoc file independently can be rendered into any existing services.
