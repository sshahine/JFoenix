package demos.components;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.transitions.template.JFXAnimationTemplate;
import com.jfoenix.transitions.template.JFXTemplateBuilder;
import javafx.animation.Interpolator;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.function.Consumer;

public class AnimationTemplateDemo extends Application {

  // For CSS see:
  // https://github.com/daneden/animate.css/blob/master/source/attention_seekers/heartBeat.css
  private static final JFXTemplateBuilder<Node> HEART_BEAT =
      JFXAnimationTemplate.create()
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

  // For CSS see:
  // https://github.com/daneden/animate.css/blob/master/source/attention_seekers/flash.css
  private static final JFXTemplateBuilder<Node> FLASH =
      JFXAnimationTemplate.create()
          .from()
          .percent(50)
          .action(b -> b.target(Node::opacityProperty).endValue(0))
          .percent(25)
          .percent(75)
          .action(b -> b.target(Node::opacityProperty).endValue(1))
          .config(b -> b.duration(Duration.seconds(1.5)).interpolator(Interpolator.EASE_BOTH));

  // For CSS see:
  // https://github.com/daneden/animate.css/blob/master/source/attention_seekers/tada.css
  private static final JFXTemplateBuilder<Node> TADA =
      JFXAnimationTemplate.create()
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

  // For CSS see: https://github.com/daneden/animate.css/blob/master/source/specials/hinge.css
  private static JFXTemplateBuilder<Node> hinge() {
    Rotate rotate = new Rotate(0, 0, 0);
    return JFXAnimationTemplate.create()
        .from()
        // One execution for init behaviour.
        .action(
            b -> b.executions(1).onFinish((node, actionEvent) -> node.getTransforms().add(rotate)))
        .percent(1)
        .action(b -> b.target(rotate.angleProperty()).endValue(0))
        .percent(20, 60)
        .action(b -> b.target(rotate.angleProperty()).endValue(80))
        .percent(40, 80)
        .action(b -> b.target(rotate.angleProperty()).endValue(60))
        .action(b -> b.target(Node::opacityProperty).endValue(1))
        .action(b -> b.target(Node::translateYProperty).endValue(0))
        .to()
        .action(b -> b.target(Node::opacityProperty).endValue(0))
        .action(b -> b.target(Node::translateYProperty).endValue(700))
        // just for resetting the animation.
        .action(
            b ->
                b.onFinish(
                    (node, actionEvent) -> {
                      rotate.setAngle(0);
                      node.setTranslateY(0);
                      node.setOpacity(1);
                    }))
        // just for resetting the animation.
        .config(b -> b.duration(Duration.seconds(2)).interpolator(Interpolator.EASE_BOTH));
  }

  // For CSS see:
  // https://github.com/daneden/animate.css/blob/master/source/attention_seekers/wobble.css
  private static final JFXTemplateBuilder<Node> WOBBLE =
      JFXAnimationTemplate.create()
          .from()
          .action(b -> b.target(Node::translateXProperty, Node::translateYProperty).endValue(0))
          .action(b -> b.target(Node::rotateProperty).endValue(0))
          .percent(15)
          .action(
              b ->
                  b.target(Node::translateXProperty)
                      .endValue(node -> -0.25 * node.getBoundsInParent().getWidth()))
          .action(b -> b.target(Node::rotateProperty).endValue(-5))
          .percent(30)
          .action(
              b ->
                  b.target(Node::translateXProperty)
                      .endValue(node -> 0.2 * node.getBoundsInParent().getWidth()))
          .action(b -> b.target(Node::rotateProperty).endValue(3))
          .percent(45)
          .action(
              b ->
                  b.target(Node::translateXProperty)
                      .endValue(node -> -0.15 * node.getBoundsInParent().getWidth()))
          .action(b -> b.target(Node::rotateProperty).endValue(-3))
          .percent(60)
          .action(
              b ->
                  b.target(Node::translateXProperty)
                      .endValue(node -> 0.1 * node.getBoundsInParent().getWidth()))
          .action(b -> b.target(Node::rotateProperty).endValue(2))
          .percent(75)
          .action(
              b ->
                  b.target(Node::translateXProperty)
                      .endValue(node -> -0.05 * node.getBoundsInParent().getWidth()))
          .action(b -> b.target(Node::rotateProperty).endValue(-1))
          .to()
          .action(b -> b.target(Node::translateXProperty).endValue(0))
          .action(b -> b.target(Node::rotateProperty).endValue(0))
          .config(b -> b.duration(Duration.seconds(1.3)).interpolator(Interpolator.EASE_BOTH));

  // For CSS see:
  // https://github.com/daneden/animate.css/blob/master/source/bouncing_entrances/bounceInUp.css
  private static final JFXTemplateBuilder<Node> BOUNCE_IN_UP =
      JFXAnimationTemplate.create()
          .from()
          .action(b -> b.target(Node::opacityProperty).endValue(0))
          .action(b -> b.target(Node::translateYProperty).endValue(3000))
          .percent(60)
          .action(b -> b.target(Node::opacityProperty).endValue(1))
          .action(b -> b.target(Node::translateYProperty).endValue(-20))
          .percent(75)
          .action(b -> b.target(Node::translateYProperty).endValue(10))
          .percent(90)
          .action(b -> b.target(Node::translateYProperty).endValue(-5))
          .to()
          .action(b -> b.target(Node::translateYProperty).endValue(0))
          .config(
              b ->
                  b.duration(Duration.seconds(1.2))
                      .interpolator(Interpolator.SPLINE(0.215, 0.61, 0.355, 1)));

  private static final JFXTemplateBuilder<Node> INTRO_ANIMATION =
      JFXAnimationTemplate.create()
          .from()
          .action(
              b ->
                  b.withAnimationObject("firstRow", "secondRow", "thirdRow")
                      .onFinish((node, actionEvent) -> node.setVisible(false)))
          .percent(50)
          .action(
              b ->
                  b.withAnimationObject("firstRow")
                      .onFinish((node, actionEvent) -> BOUNCE_IN_UP.build(node).play()))
          .percent(51)
          .action(
              b ->
                  b.withAnimationObject("firstRow")
                      .onFinish((node, actionEvent) -> node.setVisible(true)))
          .percent(62)
          .action(
              b ->
                  b.withAnimationObject("secondRow")
                      .onFinish((node, actionEvent) -> BOUNCE_IN_UP.build(node).play()))
          .percent(63)
          .action(
              b ->
                  b.withAnimationObject("secondRow")
                      .onFinish((node, actionEvent) -> node.setVisible(true)))
          .percent(68)
          .action(
              b ->
                  b.withAnimationObject("thirdRow")
                      .onFinish((node, actionEvent) -> BOUNCE_IN_UP.build(node).play()))
          .percent(69)
          .action(
              b ->
                  b.withAnimationObject("thirdRow")
                      .onFinish((node, actionEvent) -> node.setVisible(true)))
          .config(b -> b.duration(Duration.millis(700)));

  private final ObjectProperty<Paint> colorTransitionProperty =
      new SimpleObjectProperty<>(Color.TRANSPARENT);

  private JFXTemplateBuilder<Node> createColorTransition() {
    return JFXAnimationTemplate.create()
        .from()
        .action(b -> b.target(colorTransitionProperty).endValue(Color.ORANGERED))
        .percent(16)
        .action(b -> b.target(colorTransitionProperty).endValue(Color.LAWNGREEN))
        .percent(32)
        .action(b -> b.target(colorTransitionProperty).endValue(Color.DODGERBLUE))
        .percent(48)
        .action(b -> b.target(colorTransitionProperty).endValue(Color.YELLOWGREEN))
        .percent(64)
        .action(b -> b.target(colorTransitionProperty).endValue(Color.SPRINGGREEN))
        .percent(80)
        .action(b -> b.target(colorTransitionProperty).endValue(Color.DEEPPINK))
        .to()
        .action(b -> b.target(colorTransitionProperty).endValue(Color.ORANGERED))
        .config(
            b ->
                b.duration(Duration.minutes(2.5))
                    .infiniteCycle()
                    .interpolator(Interpolator.EASE_BOTH));
  }

  private String toRGBCode(Color color) {
    return String.format(
        "#%02X%02X%02X",
        (int) (color.getRed() * 255),
        (int) (color.getGreen() * 255),
        (int) (color.getBlue() * 255));
  }

  private Group createHeader() {

    StringBinding brighterColorStringBinding =
        Bindings.createStringBinding(
            () -> toRGBCode(((Color) colorTransitionProperty.get())), colorTransitionProperty);
    StringBinding darkerColorStringBinding =
        Bindings.createStringBinding(
            () -> toRGBCode(((Color) colorTransitionProperty.get()).darker()),
            colorTransitionProperty);

    Label label = new Label("JFXAnimation.template");
    label
        .styleProperty()
        .bind(
            Bindings.format(
                "-fx-text-fill: linear-gradient(to bottom, %s, %s); -fx-font-size: 50",
                brighterColorStringBinding, darkerColorStringBinding));
    return new Group(label);
  }

  private Group createBody(ObservableMap<String, Timeline> animations) {

    JFXComboBox<String> comboBox =
        createBodyComboBox(animationName -> animations.get(animationName).play());
    comboBox.getItems().addAll(animations.keySet());
    animations.addListener(
        (MapChangeListener<? super String, ? super Timeline>)
            change -> {
              if (change.wasAdded()) {
                comboBox.getItems().add(change.getKey());
              }
            });
    comboBox.getSelectionModel().selectFirst();

    JFXButton button =
        createBodyButton(
            () -> animations.get(comboBox.getSelectionModel().getSelectedItem()).play());

    HBox hBox = new HBox(comboBox, button);
    hBox.setAlignment(Pos.BOTTOM_CENTER);
    hBox.setSpacing(23);
    return new Group(hBox);
  }

  private JFXComboBox<String> createBodyComboBox(Consumer<String> selectionChangedConsumer) {

    JFXComboBox<String> comboBox = new JFXComboBox<>();
    comboBox.setPrefWidth(170);
    comboBox
        .getSelectionModel()
        .selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> selectionChangedConsumer.accept(newValue));
    comboBox.focusColorProperty().bind(colorTransitionProperty);
    comboBox.unFocusColorProperty().bind(colorTransitionProperty);
    return comboBox;
  }

  private JFXButton createBodyButton(Runnable buttonClickedRunnable) {

    StringBinding darkerColorStringBinding =
        Bindings.createStringBinding(
            () -> toRGBCode(((Color) colorTransitionProperty.get()).darker()),
            colorTransitionProperty);

    JFXButton button = new JFXButton("Animate it");
    button.setPrefWidth(120);
    button.setDisableVisualFocus(true);
    button.setOnMouseClicked(event -> buttonClickedRunnable.run());
    button
        .styleProperty()
        .bind(
            Bindings.format(
                "-fx-border-color: %s; \n"
                    + "-fx-background-color: transparent; \n"
                    + "-fx-text-fill: %s; \n"
                    + "-fx-border-width: 2; \n"
                    + "-fx-border-radius: 5;\n"
                    + "-fx-font-size: 16",
                darkerColorStringBinding, darkerColorStringBinding));
    button.ripplerFillProperty().bind(colorTransitionProperty);
    return button;
  }

  private Group createFooter() {

    Line line = new Line(0, 0, 450, 0);
    line.setStyle(
        "-fx-stroke: linear-gradient(to right, transparent 1%, #dadada 50%, transparent 99%); -fx-stroke-width: 1.3");
    VBox.setMargin(line, new Insets(-9, 0, -15, 0));

    Label subLabel = new Label("Another thing from JFoenix.");

    Label subLabel2 = new Label("Inspired by animate.css");
    subLabel2.setStyle("-fx-font-size: 13; -fx-text-fill: #a5a5a5;");
    VBox.setMargin(subLabel2, new Insets(-30, 0, 0, 0));

    VBox footerVBox = new VBox(40, line, subLabel, subLabel2);
    footerVBox.setAlignment(Pos.CENTER);
    return new Group(footerVBox);
  }

  @Override
  public void start(Stage primaryStage) {

    VBox vBox = new VBox();
    vBox.setStyle("-fx-background-color:WHITE");

    Group header = createHeader();
    header.setVisible(false);

    ObservableMap<String, Timeline> animations = FXCollections.observableHashMap();
    animations.put("Flash", FLASH.build(header));
    animations.put("Heart Beat", HEART_BEAT.build(header));
    animations.put("Tada", TADA.build(header));
    animations.put("BounceIn Up", BOUNCE_IN_UP.build(header));
    animations.put("Wobble", WOBBLE.build(header));
    animations.put("Hinge", hinge().build(header));

    Group body = createBody(animations);
    body.setVisible(false);

    Group footer = createFooter();
    footer.setVisible(false);

    vBox.setAlignment(Pos.CENTER);
    vBox.setSpacing(40);
    vBox.getChildren().addAll(header, body, footer);

    final Scene scene = new Scene(vBox, 1100, 600);
    scene
        .getStylesheets()
        .add(ButtonDemo.class.getResource("/css/jfoenix-components.css").toExternalForm());
    primaryStage.setTitle("JFX Animation Template Demo");
    primaryStage.setScene(scene);
    primaryStage.show();

    Timeline introAnimation =
        INTRO_ANIMATION.build(
            b ->
                b.namedObject("firstRow", header)
                    .namedObject("secondRow", body)
                    .namedObject("thirdRow", footer));
    animations.put("Intro Animation", introAnimation);

    createColorTransition().build().play();
    introAnimation.play();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
