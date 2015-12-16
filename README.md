# JFoenix
* JavaFX Material Design Library (Beta) [download jar](http://www.jfoenix.com/download/jfoenix.jar)
* [JFoenix Site](http://www.jfoenix.com)

# Summary
JFoenix is an open source Java library, that implements Google Material Design using Java components.

# Build
## Ant
To build JFoenix, we created an Ant file named build.xml and build.bat. JFoenix uses Java version **1.8 u60**. Using the command line, you need to move to the JFoenix/make directory
and run the batch file build.bat by typing:

    build.bat

To run the main demo, go to the JFoenix/make directory and run the batch file run-demo.bat :

    run-demo.bat

**NOTE** : You need to update the build.bat to point to Java 1.8 and Apache Ant directories.

## Gradle
To build JFoenix, execute the following command:

    gradle build

To run the main demo, execute the following command:

    gradle run
    
**NOTE** : You need to set JAVA_HOME environment variable to point to Java 1.8 directory.
**NOTE** : JFoinex requires **Java 1.8 u60** and above.

# How Can I Use JFoenix?
 You can download the source code of the library and build it as mentioned previously. Building JFoenix will generate jfoenix.jar under the build/dist folder. To use JFoenix, import jfoenix.jar into your project and start using the new material design Java components :).

# Pics

![Alt text](http://jfoenix.com/gif/button.gif "Button Demo")

![Alt text](http://jfoenix.com/gif/checkbox.gif "Check Box Demo")

![Alt text](http://jfoenix.com/gif/dialog.gif "Dialog Demo")

![Alt text](http://jfoenix.com/gif/listview.gif "List View Demo")

![Alt text](http://jfoenix.com/gif/nodes-list.gif "Nodes List Demo")

![Alt text](http://jfoenix.com/gif/slider.gif "Slider Demo")

![Alt text](http://jfoenix.com/gif/spinner.gif "Spinner Demo")

![Alt text](http://jfoenix.com/gif/colorpicker-beta.gif "Color Picker Demo")
