package demos.slideout;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;

/** Example of a sidebar that slides in and out of view */
public class SlideOut extends Application {
	private static final String[] lyrics = {
		"And in the end,\nthe love you take,\nis equal to\nthe love\nyou make.",
		"She came in through\nthe bathroom window\nprotected by\na silver\nspoon.",
		"I've got to admit\nit's getting better,\nA little better\nall the time."
	};

	private static final String[] locs = {
		"http://www.youtube.com/watch?v=osAA8q86COY&feature=player_detailpage#t=367s",
		"http://www.youtube.com/watch?v=IM2Ttov_zR0&feature=player_detailpage#t=30s",
		"http://www.youtube.com/watch?v=Jk0dBZ1meio&feature=player_detailpage#t=25s"
	};
	WebView webView;
	public static void main(String[] args) throws Exception { launch(args); }
	public void start(final Stage stage) throws Exception {
		stage.setTitle("Slide out YouTube demo");

		// create a WebView to show to the right of the SideBar.
		webView = new WebView();
		webView.setPrefSize(800, 600);

		// create a sidebar with some content in it.
		final Pane lyricPane = createSidebarContent();
		SideBar sidebar = new SideBar(250, lyricPane);
		VBox.setVgrow(lyricPane, Priority.ALWAYS);

		// layout the scene.
		final BorderPane layout = new BorderPane();
		Pane mainPane = VBoxBuilder.create().spacing(10)
				.children(
						sidebar.getControlButton(),
						webView
						).build();
		layout.setLeft(sidebar);
		layout.setCenter(mainPane);

		// show the scene.
		Scene scene = new Scene(layout);
		scene.getStylesheets().add(SlideOut.class.getResource("slideout.css").toExternalForm());
		stage.setScene(scene);
		stage.show();
	}

	private BorderPane createSidebarContent() {// create some content to put in the sidebar.
		final Text lyric = new Text();
		lyric.getStyleClass().add("lyric-text");
		final Button changeLyric = new Button("New Song");
		changeLyric.getStyleClass().add("change-lyric");
		changeLyric.setMaxWidth(Double.MAX_VALUE);
		changeLyric.setOnAction(new EventHandler<ActionEvent>() {
			int lyricIndex = 0;
			@Override public void handle(ActionEvent actionEvent) {
				lyricIndex++;
				if (lyricIndex == lyrics.length) {
					lyricIndex = 0;
				}
				lyric.setText(lyrics[lyricIndex]);
				webView.getEngine().load(locs[lyricIndex]);
			}
		});
		changeLyric.fire();
		final BorderPane lyricPane = new BorderPane();
		lyricPane.setCenter(lyric);
		lyricPane.setBottom(changeLyric);
		return lyricPane;
	}

	/** Animates a node on and off screen to the left. */
	class SideBar extends VBox {
		/** @return a control button to hide and show the sidebar */
		public Button getControlButton() { return controlButton; }
		private final Button controlButton;

		/** creates a sidebar containing a vertical alignment of the given nodes */
		SideBar(final double expandedWidth, Node... nodes) {
			getStyleClass().add("sidebar");
			this.setPrefWidth(expandedWidth);
			this.setMinWidth(0);

			// create a bar to hide and show.
			setAlignment(Pos.CENTER);
			getChildren().addAll(nodes);

			// create a button to hide and show the sidebar.
			controlButton = new Button("Collapse");
			controlButton.getStyleClass().add("hide-left");

			// apply the animations when the button is pressed.
			controlButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override public void handle(ActionEvent actionEvent) {
					// create an animation to hide sidebar.
					final Animation hideSidebar = new Transition() {
						{ setCycleDuration(Duration.millis(250)); }
						protected void interpolate(double frac) {
							final double curWidth = expandedWidth * (1.0 - frac);
							setPrefWidth(curWidth);
							setTranslateX(-expandedWidth + curWidth);
						}
					};
					hideSidebar.onFinishedProperty().set(new EventHandler<ActionEvent>() {
						@Override public void handle(ActionEvent actionEvent) {
							setVisible(false);
							controlButton.setText("Show");
							controlButton.getStyleClass().remove("hide-left");
							controlButton.getStyleClass().add("show-right");
						}
					});
					// create an animation to show a sidebar.
					final Animation showSidebar = new Transition() {
						{ setCycleDuration(Duration.millis(250)); }
						protected void interpolate(double frac) {
							final double curWidth = expandedWidth * frac;
							setPrefWidth(curWidth);
							setTranslateX(-expandedWidth + curWidth);
						}
					};
					showSidebar.onFinishedProperty().set(new EventHandler<ActionEvent>() {
						@Override public void handle(ActionEvent actionEvent) {
							controlButton.setText("Collapse");
							controlButton.getStyleClass().add("hide-left");
							controlButton.getStyleClass().remove("show-right");
						}
					});
					if (showSidebar.statusProperty().get() == Animation.Status.STOPPED && hideSidebar.statusProperty().get() == Animation.Status.STOPPED) {
						if (isVisible()) {
							hideSidebar.play();
						} else {
							setVisible(true);
							showSidebar.play();
						}
					}
				}
			});
		}
	}
}	