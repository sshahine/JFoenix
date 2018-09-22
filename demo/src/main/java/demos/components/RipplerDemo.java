package demos.components;

import com.jfoenix.controls.JFXRippler;
import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.svg.SVGGlyphLoader;
import com.jfoenix.transitions.creator.JFXAnimationCreator;
import com.jfoenix.transitions.JFXAnimationTimer;
import com.jfoenix.transitions.JFXKeyFrame;
import com.jfoenix.transitions.JFXKeyValue;
import demos.MainDemo;
import javafx.animation.Interpolator;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class RipplerDemo extends Application {

  public static final JFXAnimationCreator.Builder<Node> HEART_BEAT =
      JFXAnimationCreator.create()
                         .percent(0)
                         .action(b -> b.target(Node::scaleXProperty, Node::scaleYProperty).endValue(1))
                         .percent(14)
                         .action(b -> b.target(Node::scaleXProperty, Node::scaleYProperty).endValue(1.3))
                         .percent(28)
                         .action(b -> b.target(Node::scaleXProperty, Node::scaleYProperty).endValue(1))
                         .percent(42)
                         .action(b -> b.target(Node::scaleXProperty, Node::scaleYProperty).endValue(1.3))
                         .percent(70)
                         .action(b -> b.target(Node::scaleXProperty, Node::scaleYProperty).endValue(1))
                         .config(b -> b.duration(Duration.seconds(1.3)).interpolator(Interpolator.EASE_BOTH));

  public static final JFXAnimationCreator.Builder<Node> FLASH =
      JFXAnimationCreator.create()
                         .from()
                         .percent(50)
                         .action(b -> b.target(Node::opacityProperty).endValue(0))
                         .percent(25, 75)
                         .action(b -> b.target(Node::opacityProperty).endValue(1))
                         .config(b -> b.duration(Duration.seconds(1.5)).interpolator(Interpolator.EASE_BOTH));

  public static final JFXAnimationCreator.Builder<Node> TADA =
      JFXAnimationCreator.create()
                         .from()
                         .action(b -> b.target(Node::scaleXProperty, Node::scaleYProperty).endValue(1))
                         .percent(10, 20)
                         .action(b -> b.target(Node::scaleXProperty, Node::scaleYProperty).endValue(0.9))
                         .action(b -> b.target(Node::rotateProperty).endValue(-3))
                         .percent(30, 50, 70, 90)
                         .action(b -> b.target(Node::scaleXProperty, Node::scaleYProperty).endValue(1.1))
                         .action(b -> b.target(Node::rotateProperty).endValue(3))
                         .percent(40, 60, 80)
                         .action(b -> b.target(Node::scaleXProperty, Node::scaleYProperty).endValue(1.1))
                         .action(b -> b.target(Node::rotateProperty).endValue(-3))
                         .to()
                         .action(b -> b.target(Node::scaleXProperty, Node::scaleYProperty).endValue(1))
                         .action(b -> b.target(Node::rotateProperty).endValue(0))
                         .config(b -> b.duration(Duration.seconds(1.3)).interpolator(Interpolator.EASE_BOTH));

  private static final String ADDIDAS_LOGO =
      "m 244.85323,877.1711 c 11.58792,0 21.36521,-3.50046 30.05611,-8.69082 v 6.39738 h 24.26218 v -99.46282 h -24.26218 v 7.48384 c -8.6909,-5.79389 -19.07172,-8.69081 -30.05611,-8.69081 -28.3662,0 -51.42132,22.57217 -51.42132,51.542 0,28.3662 23.05512,51.42123 51.42132,51.42123 z m -27.76267,-51.42123 c 0,-15.69195 13.27778,-28.96983 28.3662,-28.96983 16.1748,0 29.45258,13.27788 29.45258,28.96983 0,14.96769 -13.27778,28.3662 -29.45258,28.3662 -15.08842,0 -28.3662,-13.39851 -28.3662,-28.3662 m 142.31391,51.42123 c 10.98434,0 20.76168,-3.50046 29.45258,-8.69082 v 6.39738 h 24.86571 V 741.25474 h -24.86571 v 41.64394 c -8.6909,-5.79389 -18.46824,-8.69081 -29.45258,-8.69081 -28.3662,0 -52.02485,22.57217 -52.02485,51.542 0,28.3662 23.65865,51.42123 52.02485,51.42123 z m -28.3662,-51.42123 c 0,-15.69195 13.27778,-28.96983 29.45258,-28.96983 15.08842,0 28.3662,13.27788 28.3662,28.96983 0,14.96769 -13.27778,28.3662 -28.3662,28.3662 -16.1748,0 -29.45258,-13.39851 -29.45258,-28.3662 m 94.87594,49.12779 h 24.26217 v -99.46282 h -24.26217 z m 0,-108.75726 h 24.26217 v -24.86566 h -24.26217 v 24.86566 m 115.63762,-24.86566 v 41.64394 c -8.69095,-5.79389 -18.46824,-8.69081 -29.45259,-8.69081 -28.36624,0 -51.54204,22.57217 -51.54204,51.542 0,28.3662 23.1758,51.42123 51.54204,51.42123 10.98435,0 21.36517,-3.50046 29.45259,-8.69082 v 6.39738 H 565.814 V 741.25474 Z m -57.21531,84.49513 c 0,-15.69195 13.27784,-28.96983 28.24553,-28.96983 15.69195,0 28.96978,13.27788 28.96978,28.96983 0,14.96769 -13.27783,28.3662 -28.96978,28.3662 -14.96769,0 -28.24553,-13.39851 -28.24553,-28.3662 m 141.58976,51.42123 c 11.10497,0 20.88235,-3.50046 28.96973,-8.69082 v 6.39738 h 24.86575 v -99.46282 h -24.86575 v 7.48384 c -8.08738,-5.79389 -17.86476,-8.69081 -28.96973,-8.69081 -28.24557,0 -52.0249,22.57217 -52.0249,51.542 0,28.3662 23.77933,51.42123 52.0249,51.42123 z m -27.64205,-51.42123 c 0,-15.69195 13.27784,-28.96983 28.84906,-28.96983 15.08846,0 27.76272,13.27788 27.76272,28.96983 0,14.96769 -12.67426,28.3662 -27.76272,28.3662 -15.57122,0 -28.84906,-13.39851 -28.84906,-28.3662 m 136.3993,51.42123 c 24.26213,0 44.541,-9.77729 44.541,-34.03942 0,-13.88136 -9.77728,-23.17584 -25.46923,-27.27987 -16.1748,-3.9833 -36.93652,-3.37977 -36.93652,-13.88136 1.08647,-6.88036 7.48384,-8.69086 17.26127,-8.69086 15.08833,0 15.69186,8.08733 16.29548,13.39842 h 24.86561 c -1.81059,-19.07177 -17.38177,-32.47014 -42.24756,-32.47014 -26.07277,0 -41.04042,13.88122 -41.04042,31.86665 0,10.38091 5.19037,19.67535 13.88136,24.26213 6.2767,2.89706 14.96765,5.1905 25.46924,7.001 10.86362,1.08647 20.76158,2.29344 21.36521,9.77742 0,5.19041 -5.1905,11.58793 -16.77842,11.58793 -16.77833,0 -20.27874,-9.29462 -20.27874,-15.57132 h -25.95209 c 0.48281,20.15806 17.26109,34.03942 45.02381,34.03942 M 765.9467,743.54809 c 7.001,0 12.67421,5.79398 12.67421,13.39851 0,6.88036 -5.67321,12.67434 -12.67421,12.67434 -7.00104,0 -12.67425,-5.79398 -12.67425,-12.67434 0,-7.60453 5.67321,-13.39851 12.67425,-13.39851 z m 0,1.81059 c -6.39751,0 -10.98429,5.19054 -10.98429,11.58792 0,5.79403 4.58678,10.98443 10.98429,10.98443 5.79403,0 10.9844,-5.1904 10.9844,-10.98443 0,-6.39738 -5.19037,-11.58792 -10.9844,-11.58792 z m 2.89693,11.58792 c 0.60362,0 2.29344,0.48285 2.29344,2.89706 v 3.9833 h -2.29344 v -1.68982 c 0,-2.8971 -0.60349,-4.10407 -2.29344,-4.10407 h -2.89692 v 5.79389 h -2.29349 v -14.48489 h 4.58692 c 2.89693,0 5.19037,1.81064 5.19037,4.10408 0,1.68981 -1.20697,3.50045 -2.29344,3.50045 z m -5.19036,-5.19041 v 3.98344 h 2.29343 c 1.08643,0 2.89693,-0.60367 2.89693,-1.68996 0,-1.20715 -1.20697,-2.29348 -2.29344,-2.29348 h -2.89692 M 735.28702,572.9887 c -3.50041,7.00099 -9.77724,16.77828 -15.57114,22.57231 H 255.23405 c -3.37978,-4.58688 -11.4672,-15.08847 -14.9677,-22.57231 h 495.02067 m -34.6429,46.23095 c -5.79403,8.08734 -14.48499,15.69181 -23.77947,20.88236 H 296.39527 c -7.60457,-5.19055 -15.08841,-12.79502 -22.08946,-20.88236 h 426.33831 m -52.62847,42.85105 c -41.16119,30.65964 -86.18499,46.23096 -120.34518,48.52426 0,-12.67421 0,-30.05602 3.5005,-48.52426 H 648.01565 M 778.62091,421.50103 c 3.50059,37.54 -8.69081,83.28802 -31.74597,129.51898 H 575.71197 c 6.27679,-10.98444 14.48489,-21.36521 23.65865,-31.8668 49.73136,-58.30154 126.13916,-94.27241 179.25029,-97.65218 M 446.67589,710.59496 c -32.95313,-2.2933 -78.09766,-17.86462 -119.62097,-48.52426 h 117.93106 c 2.29344,18.46824 2.29344,35.85005 1.68991,48.52426 m 41.04045,3.50059 C 474.43856,701.4213 462.85064,684.03939 450.77991,662.0707 h 73.39006 c -11.58792,21.96869 -24.26218,39.3506 -36.45363,52.02485 M 400.44497,551.02001 H 228.67848 c -23.17584,-46.23096 -34.64304,-91.97898 -31.26322,-129.51898 52.62838,3.37977 129.63966,39.35064 179.37107,97.65218 8.57023,10.50159 17.26113,20.88236 23.65864,31.8668 m 161.38569,0 h -148.1079 c -2.29344,-13.27788 -2.29344,-26.07277 -2.29344,-41.0406 0,-80.99454 35.8501,-164.88613 76.28702,-202.42614 39.95413,37.54001 76.40775,121.4316 76.40775,202.42614 0,14.96783 -1.2071,27.76272 -2.29343,41.0406";

  private static final String NIKE_LOGO =
      "M159.23,431.966c-5.84-0.232-10.618-1.83-14.354-4.798c-0.713-0.567-2.412-2.267-2.982-2.984\n"
          + "\tc-1.515-1.905-2.545-3.759-3.232-5.816c-2.114-6.332-1.026-14.641,3.112-23.76c3.543-7.807,9.01-15.55,18.548-26.274\n"
          + "\tc1.405-1.578,5.589-6.193,5.616-6.193c0.01,0-0.218,0.395-0.505,0.876c-2.48,4.154-4.602,9.047-5.758,13.283\n"
          + "\tc-1.857,6.797-1.633,12.63,0.656,17.153c1.579,3.116,4.286,5.815,7.33,7.307c5.329,2.611,13.131,2.827,22.659,0.632\n"
          + "\tc0.656-0.152,33.162-8.781,72.236-19.176c39.074-10.396,71.049-18.895,71.054-18.888c0.011,0.009-90.78,38.859-137.911,59.014\n"
          + "\tc-7.464,3.191-9.46,3.997-12.969,5.229C173.76,430.721,165.725,432.224,159.23,431.966z";

  private static final String FX_BACKGROUND_COLOR_WHITE = "-fx-background-color:WHITE;";
  private static int counter = 0;
  private static int step = 1;

  @Override
  public void start(Stage stage) {

    // TODO drop shadow changes the width and height thus need to be considered
    FlowPane main = new FlowPane();
    main.setVgap(20);
    main.setHgap(20);

    Label label = new Label("Click Me");
    label.setStyle(FX_BACKGROUND_COLOR_WHITE);
    label.setPadding(new Insets(20));
    JFXRippler rippler = new JFXRippler(label);

    rippler.setEnabled(false);
    main.getChildren().add(rippler);

    label.setOnMousePressed(
        (e) -> {
          if (counter == 5) {
            step = -1;
          } else if (counter == 0) {
            step = 1;
          }
          JFXDepthManager.setDepth(label, counter += step % JFXDepthManager.getLevels());
        });




    Label l1 = new Label("Subtract");
    l1.setStyle(FX_BACKGROUND_COLOR_WHITE);
    l1.setPadding(new Insets(20));
    JFXRippler rippler1 = new JFXRippler(l1);
    rippler1.setRipplerInAnimation(Duration.seconds(1));
    rippler1.setRipplerFill(Color.HOTPINK);



      JFXAnimationTimer timer = new JFXAnimationTimer();
      try {
          timer.addKeyFrame(new JFXKeyFrame(Duration.seconds(1), JFXKeyValue.builder().setTarget(rippler1.translateYProperty()).setEndValue(100).build()));
          timer.start();
          timer.setOnFinished(() -> timer.reverseAndContinue());
      } catch (Exception e) {
          e.printStackTrace();
      }

      try {
      SVGGlyphLoader.loadGlyphsFont(
          MainDemo.class.getResourceAsStream("/fonts/icomoon.svg"), "icomoon.svg");
    } catch (IOException ioExc) {
      ioExc.printStackTrace();
    }

    rippler1.setSVGCoutout(SVGGlyphLoader.getGlyph("icomoon.svg.film"), false);
    main.getChildren().add(rippler1);
    JFXDepthManager.setDepth(rippler1, 1);

    Label l2 = new Label("Intersect");
    l2.setStyle(FX_BACKGROUND_COLOR_WHITE);
    l2.setPadding(new Insets(20));
    JFXRippler rippler2 = new JFXRippler(l2);
    rippler2.setSVGCoutout(SVGGlyphLoader.getGlyph("icomoon.svg.film"), true);
    main.getChildren().add(rippler2);
    JFXDepthManager.setDepth(rippler2, 2);

    Label l3 = new Label("Addidas intersect \n Tada");
    l3.setStyle(FX_BACKGROUND_COLOR_WHITE);
    l3.setPadding(new Insets(20));
    JFXRippler rippler3 = new JFXRippler(l3);
    main.getChildren().add(rippler3);
    JFXDepthManager.setDepth(rippler3, 3);
    rippler3.setSVGCoutout(new SVGGlyph(ADDIDAS_LOGO), true);
    rippler3.setRipplerFill(Color.DODGERBLUE);
    Timeline tada = TADA.build(rippler3);
    rippler3.setOnMouseClicked(event -> tada.play());

    Label l4 = new Label("Addidas \n HeartBeat");
    l4.setStyle(FX_BACKGROUND_COLOR_WHITE);
    l4.setPadding(new Insets(20));
    JFXRippler rippler4 = new JFXRippler(l4);
    main.getChildren().add(rippler4);
    JFXDepthManager.setDepth(rippler4, 4);
    rippler4.setSVGCoutout(new SVGGlyph(ADDIDAS_LOGO), false);
    rippler4.setRipplerFill(Color.DARKOLIVEGREEN);

    Timeline heartBeat = HEART_BEAT.build(rippler4);
    rippler4.setOnMouseClicked(event -> heartBeat.play());

    Label l5 = new Label("Nike \n Flash");
    l5.setStyle(FX_BACKGROUND_COLOR_WHITE);
    l5.setPadding(new Insets(20));
    JFXRippler rippler5 = new JFXRippler(l5);
    main.getChildren().add(rippler5);
    JFXDepthManager.setDepth(rippler5, 5);
    rippler5.setSVGCoutout(new SVGGlyph(NIKE_LOGO), false);
    rippler5.setRipplerFill(Color.ORANGERED);


    Timeline flash = FLASH.build(rippler5);
    rippler5.setOnMouseClicked(event -> flash.play());

    Label l6 = new Label("Nike intersect");
    l6.setStyle(FX_BACKGROUND_COLOR_WHITE);
    l6.setPadding(new Insets(20));
    JFXRippler rippler6 = new JFXRippler(l6);
    main.getChildren().add(rippler6);
    JFXDepthManager.setDepth(rippler6, 5);
    rippler6.setSVGCoutout(new SVGGlyph(NIKE_LOGO), true);
    rippler6.setRipplerFill(Color.ORANGERED);

    StackPane pane = new StackPane();
    pane.getChildren().add(main);
    StackPane.setMargin(main, new Insets(100));
    pane.setStyle("-fx-background-color:WHITE");

    final Scene scene = new Scene(pane, 600, 400);


    stage.setTitle("JavaFX Ripple effect and shadows ");
    stage.setScene(scene);
    stage.setResizable(false);
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
