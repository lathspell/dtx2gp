== Overview ==

This program converts DTXMania .dtx files to GuitarPro .gp5 files.

The latest .jar release is in the directory RELEASE/ !

== Starting ==

Via Java Webstart with:

  javaws RELEASE/launch.jnlp

or via CLI with:

  java -jar dtx2gp-1.0-jar-with-dependencies.jar  my.dtx  my.gp5

== Build from Source ==

The project is maven based so just run "mvn assembly:assembly".
It can then be tested with "javaws target/jnlp/launch.jnlp".

Sadly it was written in an early development version of the Kotlin
programming language and I can't get it to compile anymore. 

== Deployment ==

 mvn release:prepare
 mvn release:perform

== Links ==

* DTXMania: http://en.sourceforge.jp/projects/dtxmania/
* DTX Specs: http://mainori-se.sakura.ne.jp/dtxmania/wiki.cgi?page=qa\_dtx\_spec\_e
* GuitarPro: http://www.guitar-pro.com/

== Credits and Copyright ==

This software was written in 2012 by Christian Hammers <ch@lathspell.de> and
licensed under the LGPL v3 (see COPYING).

The Java classes to read and write GP5 files were taken from TuxGuitar at
http://sourceforge.net/projects/tuxguitar/ which was licensed under LGPL v2.1.

== Changes ==

v1.4, 2012-08-24, Christian Hammers <ch@lathspell.de>
* Fixed Logging

v1.3, 2012-08-24, Christian Hammers <ch@lathspell.de>
* Added WebStart frontend.

v1.0, 2012-07-15, Christian Hammers <ch@lathspell.de>
* Initial version released.

