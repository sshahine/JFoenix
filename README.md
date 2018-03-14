[![HitCount](http://hits.dwyl.io/jfoenixadmin/JFoenix.svg)](http://hits.dwyl.io/jfoenixadmin/JFoenix)
[![][CircleCI img]][CircleCI]
[![][mavenbadge img]][mavenbadge]
[![][sonardebt img]][sonardebt]
[![][gitter img]][gitter]
[![GitHub forks](https://img.shields.io/github/forks/jfoenixadmin/JFoenix.svg)](https://github.com/jfoenixadmin/JFoenix/network)
[![GitHub issues](https://img.shields.io/github/issues/jfoenixadmin/JFoenix.svg)](https://github.com/jfoenixadmin/JFoenix/issues)
[![GitHub license](https://img.shields.io/github/license/jfoenixadmin/JFoenix.svg)](https://github.com/jfoenixadmin/JFoenix/blob/master/LICENSE)
---

<h1 align="center">
    <img src="http://www.jfoenix.com/img/logo-JFX.png">
</h1>
<p align="center">
<sup>
<b>JFoenix is an open source Java library, that implements Google Material Design using Java components</b>
</sup>
</p>

* [JFoenix Site](http://www.jfoenix.com)
* JFoenix for Java 9 - [download jar](http://www.jfoenix.com/download/jfoenix-9.0.1.jar) (9.x.x)
* JFoenix for Java 8 - [download jar](http://www.jfoenix.com/download/jfoenix-8.0.1.jar) (8.x.x)
* JFoenix for Android - [download jar](http://www.jfoenix.com/download/jfoenix-8.0.1-retrolambda.jar)
* Released builds are available from [Maven Central](http://search.maven.org/#search%7Cga%7C1%7CJFoenix)

# Using JFoenix already?
Feel free to submit your project info to the following <a href="mailto:developers@jfoenix.com" target="_top">email</a>, to be posted on JFoenix github/website.
* One more thing, all contributions are appreciated. Don't hesitate to add your own contributions to JFoenix :)

# Projects using JFoenix
* <a href="http://bcozy.org">BCozy</a>
* <a href="https://github.com/ThijsZijdel/Corendon-LostLuggage">Corendon LostLuggage</a>
* <a href="https://github.com/afsalashyana/Library-Assistant">Library Assistant</a>
* <a href="https://github.com/badarshahzad/Jfx-Browser">JFX-Browser</a>
* By <a href="https://github.com/anongrp">Anongrp</a> in several projects: 
<a href="https://github.com/anongrp/Phony">Phony</a>,
<a href="https://github.com/anongrp/ThunderGet">ThunderGet</a>,
<a href="https://github.com/anongrp/Portume">Portume</a>
* Other small projects <a href="https://github.com/mayuso/LoLPing2">LoLPing 2</a>,
<a href="https://github.com/naeemkhan12/CurrencyConverter.git">Currency Converter</a>,
<a href="https://github.com/Anikeshpatel/ColorCode-Pro">ColorCode Pro</a>

# Build
To build JFoenix, execute the following command:

    gradlew build

To run the main demo, execute the following command:

    gradlew run

**NOTE** : You need to set JAVA_HOME environment variable to point to Java 1.8 directory.

**NOTE** : JFoenix requires **Java 1.8u60** and above.

# Demo
For detailed steps on getting the demo running, see [the wiki].

# How can I use JFoenix?
You can download the source code of the library and build it as mentioned previously. Building JFoenix will generate jfoenix-0.0.0-SNAPSHOT.jar under the jfoenix/build/libs folder. To use JFoenix, import jfoenix-0.0.0-SNAPSHOT.jar into your project and start using the new material design Java components :).

## Gradle
### How to Include In Gradle Project
```
repositories {
    mavenCentral()
}
```
Reference the repository from this location using:
```
dependencies {
    compile 'com.jfoenix:jfoenix:8.0.3' // Java 8
    compile 'com.jfoenix:jfoenix:9.0.3' // Java 9
}
```

## Maven
### How to Include In Maven Project
```xml
<dependency>
    <groupId>com.jfoenix</groupId>
    <artifactId>jfoenix</artifactId>
    <version>8.0.1</version>
</dependency>
```
# Pics

![Alt text](http://jfoenix.com/gif/button.gif "Button Demo")

![Alt text](http://jfoenix.com/gif/checkbox.gif "Check Box Demo")

![Alt text](http://jfoenix.com/gif/toggle-button.gif "Toggle Buton Demo")

![Alt text](http://jfoenix.com/gif/dialog.gif "Dialog Demo")

![Alt text](http://jfoenix.com/gif/listview.gif "List View Demo")

![Alt text](http://jfoenix.com/gif/chipview.gif "Chip View")

![Alt text](http://jfoenix.com/gif/nodes-list.gif "Nodes List Demo")

![Alt text](http://jfoenix.com/gif/masonry.gif "Masonry Demo")

![Alt text](http://jfoenix.com/gif/slider.gif "Slider Demo")

![Alt text](http://jfoenix.com/gif/spinner.gif "Spinner Demo")

![Alt text](http://jfoenix.com/gif/icons-snackbar.gif "Icons-Snackbar Demo")

![Alt text](http://jfoenix.com/gif/colorpicker-beta.gif "Color Picker Demo")

![Alt text](http://jfoenix.com/gif/datepicker.gif "Date Picker Demo")

![Alt text](http://jfoenix.com/gif/timepicker.gif "Time Picker Demo")

![Alt text](http://jfoenix.com/gif/treetableview.gif "Tree Table View")

![Alt text](http://jfoenix.com/gif/grouping.gif "Grouping Demo")


[mavenbadge]:https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.jfoenix%22%20AND%20a%3A%22jfoenix%22
[mavenbadge img]:https://maven-badges.herokuapp.com/maven-central/com.jfoenix/jfoenix/badge.svg

[sonar]:https://sonarqube.com/dashboard?id=com.jfoenix%3Ajfoenix-root
[sonar img]:https://sonarqube.com/api/badges/gate?key=com.jfoenix:jfoenix-root

[sonardebt]:https://sonarqube.com/dashboard?id=com.jfoenix%3Ajfoenix-root
[sonardebt img]:https://sonarqube.com/api/badges/measure?key=com.jfoenix:jfoenix-root&metric=sqale_debt_ratio

[CircleCI]:https://circleci.com/gh/jfoenixadmin/JFoenix/tree/master
[CircleCI img]:https://circleci.com/gh/jfoenixadmin/JFoenix/tree/master.svg?style=shield

[gitter]:https://gitter.im/JFoenix/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge
[gitter img]:https://badges.gitter.im/JFoenix/Lobby.svg

[the wiki]: https://github.com/jfoenixadmin/JFoenix/wiki#trying-the-demo
