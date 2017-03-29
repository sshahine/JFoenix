package demos.datafx;

import io.datafx.controller.context.ViewContext;
import io.datafx.controller.flow.FlowContainer;
import io.datafx.controller.flow.container.AnimatedFlowContainer;
import io.datafx.controller.flow.container.ContainerAnimations;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.List;
import java.util.function.Function;

/**
 * A {@link FlowContainer} that supports animation for the view change.
 */
public class ExtendedAnimatedFlowContainer extends AnimatedFlowContainer implements FlowContainer<StackPane> {

    private final StackPane view;
    private final Duration duration;
    private Function<AnimatedFlowContainer, List<KeyFrame>> animationProducer;
    private Timeline animation;
    private final ImageView placeholder;

    /**
     * Defaults constructor that creates a container with a fade animation that last 320 ms.
     */
    public ExtendedAnimatedFlowContainer() {
        this(Duration.millis(320));
    }

    /**
     * Creates a container with a fade animation and the given duration.
     *
     * @param duration the duration of the animation
     */
    public ExtendedAnimatedFlowContainer(Duration duration) {
        this(duration, ContainerAnimations.FADE);
    }

    /**
     * Creates a container with the given animation type and  duration.
     *
     * @param duration  the duration of the animation
     * @param animation the animation type
     */
    public ExtendedAnimatedFlowContainer(Duration duration, ContainerAnimations animation) {
        this(duration, animation.getAnimationProducer());
    }

    /**
     * Creates a container with the given animation type and duration.
     *
     * @param duration          the duration of the animation
     * @param animationProducer the {@link KeyFrame} instances that define the animation
     */
    public ExtendedAnimatedFlowContainer(Duration duration, Function<AnimatedFlowContainer, List<KeyFrame>>
        animationProducer) {
        this.view = new StackPane();
        this.duration = duration;
        this.animationProducer = animationProducer;
        placeholder = new ImageView();
        placeholder.setPreserveRatio(true);
        placeholder.setSmooth(true);
    }

    public void changeAnimation(ContainerAnimations animation) {
        this.animationProducer = animation.getAnimationProducer();
    }

    @Override
    public <U> void setViewContext(ViewContext<U> context) {
        updatePlaceholder(context.getRootNode());
        if (animation != null) {
            animation.stop();
        }
        animation = new Timeline();
        animation.getKeyFrames().addAll(animationProducer.apply(this));
        animation.getKeyFrames().add(new KeyFrame(duration, (e) -> clearPlaceholder()));
        animation.play();
    }

    /**
     * Returns the {@link ImageView} instance that is used as a placeholder for the old view in each navigation
     * animation.
     *
     * @return image view place holder
     */
    public ImageView getPlaceholder() {
        return placeholder;
    }

    /**
     * Returns the duration for the animation.
     *
     * @return the duration for the animation
     */
    public Duration getDuration() {
        return duration;
    }

    public StackPane getView() {
        return view;
    }

    private void clearPlaceholder() {
        view.getChildren().remove(placeholder);
    }

    private void updatePlaceholder(Node newView) {
        if (view.getWidth() > 0 && view.getHeight() > 0) {
            SnapshotParameters parameters = new SnapshotParameters();
            parameters.setFill(Color.TRANSPARENT);
            Image placeholderImage = view.snapshot(parameters,
                new WritableImage((int) view.getWidth(), (int) view.getHeight()));
            placeholder.setImage(placeholderImage);
            placeholder.setFitWidth(placeholderImage.getWidth());
            placeholder.setFitHeight(placeholderImage.getHeight());
        } else {
            placeholder.setImage(null);
        }
        placeholder.setVisible(true);
        placeholder.setOpacity(1.0);
        view.getChildren().setAll(placeholder, newView);
        placeholder.toFront();
    }
}
