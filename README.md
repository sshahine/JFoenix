[![][CircleCI img]][CircleCI]
[![][mavenbadge img]][mavenbadge]
[![][versioneye img]][versioneye]
[![][sonardebt img]][sonardebt]
[![][gitter img]][gitter]

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
* JFoenix for Java 9 - [download jar](http://www.jfoenix.com/download/jfoenix-9.0.0.jar)
* JFoenix - [download jar](http://www.jfoenix.com/download/jfoenix.jar)
* JFoenix for Android - [download jar](http://www.jfoenix.com/download/jfoenix-0.0.0-SNAPSHOT-retrolambda.jar)
* Released builds are available from [Maven Central](http://search.maven.org/#search%7Cga%7C1%7CJFoenix)

# Using JFoenix already?
Feel free to submit your project info to the following <a href="mailto:developers@jfoenix.com" target="_top">email</a>, to be posted on JFoenix github/website.
* One more thing, all contributions are appreciated. Don't hesitate to add your own contributions to JFoenix :)

# Projects using JFoenix
* <a href="http://bcozy.org">BCozy</a>
* Other small projects <a href="https://mayuso.itch.io/lolping-2">LOL ping</a>,
<a href="https://github.com/naeemkhan12/CurrencyConverter.git">Currency Converter</a>

# Build
To build JFoenix, execute the following command:

    gradlew build

To run the main demo, execute the following command:

    gradlew run

**NOTE** : You need to set JAVA_HOME environment variable to point to Java 1.8 directory.

**NOTE** : JFoenix requires **Java 1.8u60** and above.

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
    compile 'com.jfoenix:jfoenix:1.10.0'
}
```

## Maven
### How to Include In Maven Project
```xml
<dependency>
    <groupId>com.jfoenix</groupId>
    <artifactId>jfoenix</artifactId>
    <version>1.10.0</version>
</dependency>
```
# Pics

![Alt text](http://jfoenix.com/gif/button.gif "Button Demo")

![Alt text](http://jfoenix.com/gif/checkbox.gif "Check Box Demo")

![Alt text](http://jfoenix.com/gif/toggle-button.gif "Toggle Buton Demo")

![Alt text](http://jfoenix.com/gif/dialog.gif "Dialog Demo")

![Alt text](http://jfoenix.com/gif/listview.gif "List View Demo")

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

[versioneye]:https://www.versioneye.com/user/projects/58da091024ef3e0045217361
[versioneye img]:https://www.versioneye.com/user/projects/58da091024ef3e0045217361/badge.svg

[sonar]:https://sonarqube.com/dashboard?id=com.jfoenix%3Ajfoenix-root
[sonar img]:https://sonarqube.com/api/badges/gate?key=com.jfoenix:jfoenix-root

[sonardebt]:https://sonarqube.com/dashboard?id=com.jfoenix%3Ajfoenix-root
[sonardebt img]:https://sonarqube.com/api/badges/measure?key=com.jfoenix:jfoenix-root&metric=sqale_debt_ratio

[CircleCI]:https://circleci.com/gh/jfoenixadmin/JFoenix/tree/master
[CircleCI img]:https://circleci.com/gh/jfoenixadmin/JFoenix/tree/master.svg?style=shield

[gitter]:https://gitter.im/JFoenix/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge
[gitter img]:https://badges.gitter.im/JFoenix/Lobby.svg
