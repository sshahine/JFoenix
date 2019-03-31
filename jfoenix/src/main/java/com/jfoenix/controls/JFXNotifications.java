package com.jfoenix.controls;

import com.jfoenix.notification.JFXAbstractNotificationTemplate;
import com.jfoenix.skins.JFXNotificationBar;
import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.Screen;
import javafx.stage.Window;
import javafx.util.Duration;
import java.lang.ref.WeakReference;
import java.util.*;

public class JFXNotifications {

    private Pos position = Pos.BOTTOM_RIGHT;
    private Duration hideAfterDuration = Duration.seconds(5);
    private Window owner;
    private JFXAbstractNotificationTemplate notificationTemplate;
    private Screen screen = Screen.getPrimary();

    /**
     * Call this to begin the process of building a notification to show.
     */
    public static JFXNotifications create() {
        return new JFXNotifications();
    }

    /**
     * Specify the position of the notification on screen, by default it is
     * {@link Pos#BOTTOM_RIGHT bottom-right}.
     */
    public JFXNotifications position(Pos position) {
        this.position = position;
        return this;
    }

    public JFXNotifications template(JFXAbstractNotificationTemplate notificationTemplate){
        this.notificationTemplate = notificationTemplate;
        return this;
    }

    /**
     * The dialog window owner - which can be {@link Screen}, {@link Window}
     * or {@link Node}. If specified, the notifications will be inside
     * the owner, otherwise the notifications will be shown within the whole
     * primary (default) screen.
     */
    public JFXNotifications owner(Object owner) {
        if (owner instanceof Screen) {
            this.screen = (Screen) owner;
        } else {
            this.owner = getWindow(owner);
        }
        return this;
    }

    /**
     * Specify the duration that the notification should show, after which it
     * will be hidden.
     */
    public JFXNotifications hideAfter(Duration duration) {
        this.hideAfterDuration = duration;
        return this;
    }

    /**
     * Instructs the notification to be shown, and that it should use the
     * built-in 'confirm' graphic.
     */
    public void showMessage() {
        show();
    }

    /**
     * Instructs the notification to be shown.
     */
    private void show() {
        JFXNotifications.NotificationPopupHandler.getInstance().show(this);
    }

    /***************************************************************************
     * * Private support classes * *
     **************************************************************************/

    private static final class NotificationPopupHandler {

        private static final JFXNotifications.NotificationPopupHandler INSTANCE = new JFXNotifications.NotificationPopupHandler();

        private double startX;
        private double startY;
        private double screenWidth;
        private double screenHeight;

        static JFXNotifications.NotificationPopupHandler getInstance() {
            return INSTANCE;
        }

        private final Map<Pos, Map<Popup, Double>> popupsMap = new HashMap<>();
        private final double padding = 15;

        private ParallelTransition parallelTransition = new ParallelTransition();

        private boolean isShowing = false;

        void show(JFXNotifications notification) {
            Window window;
            if (notification.owner == null) {
                Rectangle2D screenBounds = notification.screen.getVisualBounds();
                startX = screenBounds.getMinX();
                startY = screenBounds.getMinY();
                screenWidth = screenBounds.getWidth();
                screenHeight = screenBounds.getHeight();

                window = getWindow(null);
            } else {
                startX = notification.owner.getX();
                startY = notification.owner.getY();
                screenWidth = notification.owner.getWidth();
                screenHeight = notification.owner.getHeight();
                window = notification.owner;
            }
            show(window, notification);
        }

        private void show(Window owner, final JFXNotifications notification) {
            final Popup popup = new Popup();
            popup.setAutoFix(false);
            final Pos p = notification.position;

            notification.notificationTemplate.setNotificationPane(new JFXNotificationBar() {

                @Override
                public boolean isShowing() {
                    return isShowing;
                }

                @Override
                public boolean isShowFromTop() {
                    return JFXNotifications.NotificationPopupHandler.this.isShowFromTop(notification.position);
                }

                @Override
                public Node getContentPane() {
                    return notification.notificationTemplate;
                }

                @Override
                public void hide() {
                    isShowing = false;
                    createHideTimeline(popup, this, p, Duration.ZERO).play();
                }

                @Override
                public double getContainerHeight() {
                    return startY + screenHeight;
                }

                @Override
                public void relocateInParent(double x, double y) {
                    switch (p) {
                        case BOTTOM_LEFT:
                        case BOTTOM_CENTER:
                        case BOTTOM_RIGHT:
                            popup.setAnchorY(y - padding);
                            break;
                        default: break;
                    }
                }
            });

            popup.getContent().add(notification.notificationTemplate.getNotificationBar());
            popup.show(owner, 0, 0);

            double anchorX, anchorY;
            final double barWidth = notification.notificationTemplate.getNotificationBar().getWidth();
            final double barHeight = notification.notificationTemplate.getNotificationBar().getHeight();

            switch (p) {
                case TOP_LEFT:
                case CENTER_LEFT:
                case BOTTOM_LEFT:
                    anchorX = padding + startX;
                    break;

                case TOP_CENTER:
                case CENTER:
                case BOTTOM_CENTER:
                    anchorX = startX + (screenWidth / 2.0) - barWidth / 2.0 - padding / 2.0;
                    break;

                default:
                case TOP_RIGHT:
                case CENTER_RIGHT:
                case BOTTOM_RIGHT:
                    anchorX = startX + screenWidth - barWidth - padding;
                    break;
            }
            switch (p) {
                case TOP_LEFT:
                case TOP_CENTER:
                case TOP_RIGHT:
                    anchorY = padding + startY;
                    break;
                case CENTER_LEFT:
                case CENTER:
                case CENTER_RIGHT:
                    anchorY = startY + (screenHeight / 2.0) - barHeight - padding / 2;
                    break;
                default:
                case BOTTOM_LEFT:
                case BOTTOM_CENTER:
                case BOTTOM_RIGHT:
                    anchorY = startY + screenHeight - barHeight - padding;
                    break;
            }

            popup.setAnchorX(anchorX);
            popup.setAnchorY(anchorY);

            isShowing = true;
            notification.notificationTemplate.getNotificationBar().doShow();

            addPopupToMap(p, popup);

            Timeline timeline = createHideTimeline(popup, notification.notificationTemplate.getNotificationBar(), p, notification.hideAfterDuration);
            timeline.play();
        }

        private void hide(Popup popup, Pos p) {
            popup.hide();
            removePopupFromMap(p, popup);
        }

        private Timeline createHideTimeline(final Popup popup, JFXNotificationBar bar, final Pos p, Duration startDelay) {
            KeyValue fadeOutBegin = new KeyValue(bar.opacityProperty(), 1.0);
            KeyValue fadeOutEnd = new KeyValue(bar.opacityProperty(), 0.0);

            KeyFrame kfBegin = new KeyFrame(Duration.ZERO, fadeOutBegin);
            KeyFrame kfEnd = new KeyFrame(Duration.millis(500), fadeOutEnd);

            Timeline timeline = new Timeline(kfBegin, kfEnd);
            timeline.setDelay(startDelay);
            timeline.setOnFinished(e -> hide(popup, p));

            return timeline;
        }

        private void addPopupToMap(Pos p, Popup popup) {
            Map<Popup, Double> popups;
            if (!popupsMap.containsKey(p)) {
                popups = new LinkedHashMap<>();
                popupsMap.put(p, popups);
            } else popups = popupsMap.get(p);

            doAnimation(p, popup);
            if(isShowFromTop(p)) popups.put(popup, null);
            else popups.put(popup, popup.getAnchorY());
        }

        private void removePopupFromMap(Pos p, Popup popup) {
            if (popupsMap.containsKey(p)) {
                LinkedHashMap<Popup, Double> popups = (LinkedHashMap<Popup, Double>) popupsMap.get(p);
                popups.remove(popup);
            }
        }

        private void doAnimation(Pos p, Popup changedPopup) {
            Map<Popup, Double> popups = popupsMap.get(p);
            if (popups == null) return;

            parallelTransition.stop();
            parallelTransition.getChildren().clear();

            final boolean isShowFromTop = isShowFromTop(p);

            popups.forEach((popup, height) -> {
                final double newPopupHeight = changedPopup.getContent().get(0).getBoundsInParent().getHeight();
                final double anchorYTarget;
                if(isShowFromTop) {
                    anchorYTarget = startY + newPopupHeight + padding + ((Objects.isNull(height)) ? 0 : height);
                    if(height == null) popups.put(popup, newPopupHeight);
                    else popups.put(popup, newPopupHeight + height);
                } else {
                    anchorYTarget = ((changedPopup.getAnchorY() - newPopupHeight)) + (height - changedPopup.getAnchorY()) ;
                    popups.put(popup, height - newPopupHeight);
                }

                if (anchorYTarget < 0) popup.hide();
                final double oldAnchorY = popup.getAnchorY();

                final double distance = anchorYTarget - oldAnchorY;

                Transition t = new JFXNotifications.NotificationPopupHandler.CustomTransition(popup, oldAnchorY, distance);
                t.setCycleCount(1);
                parallelTransition.getChildren().add(t);
            });
            parallelTransition.play();
        }

        private boolean isShowFromTop(Pos p) {
            switch (p) {
                case TOP_LEFT:
                case TOP_CENTER:
                case TOP_RIGHT:
                    return true;
                default:
                    return false;
            }
        }

        class CustomTransition extends Transition {

            private WeakReference<Popup> popupWeakReference;
            private double oldAnchorY;
            private double distance;

            CustomTransition(Popup popup, double oldAnchorY, double distance) {
                popupWeakReference = new WeakReference<>(popup);
                this.oldAnchorY = oldAnchorY;
                this.distance = distance;
                setCycleDuration(Duration.millis(350.0));
            }

            @Override
            protected void interpolate(double frac) {
                Popup popup = popupWeakReference.get();
                if (popup != null) {
                    double newAnchorY = oldAnchorY + distance * frac;
                    popup.setAnchorY(newAnchorY);
                }
            }

        }
    }

    private static Window getWindow(Object owner) throws IllegalArgumentException {
        if (owner == null) {
            Window window = null;
            @SuppressWarnings("deprecation")
            Iterator<Window> windows = Window.impl_getWindows();
            while (windows.hasNext()) {
                window = windows.next();
                if (window.isFocused() && !(window instanceof PopupWindow)) {
                    break;
                }
            }
            return window;
        } else if (owner instanceof Window) {
            return (Window) owner;
        } else if (owner instanceof Node) {
            return ((Node) owner).getScene().getWindow();
        } else {
            throw new IllegalArgumentException("Unknown owner: " + owner.getClass());
        }
    }
}
