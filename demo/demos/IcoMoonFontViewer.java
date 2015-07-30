package demos;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import com.cctintl.c3dfx.controls.C3DButton;
import com.cctintl.c3dfx.controls.C3DSlider;
import com.cctintl.c3dfx.controls.C3DSlider.IndicatorPosition;
import com.cctintl.c3dfx.svg.SVGGlyph;
import com.cctintl.c3dfx.svg.SVGGlyphLoader;

/**
 * @author sshahine
 *
 */

public class IcoMoonFontViewer extends Application {

	private GlyphDetailViewer glyphDetailViewer = new GlyphDetailViewer();

	private static String fileName = "icomoon.svg";
	
	@Override
	public void start(Stage stage) throws Exception {
		
		SVGGlyphLoader.loadGlyphsFont(IcoMoonFontViewer.class.getResource("/resources/fonts/" + fileName));
		
		stage.setTitle("IcoMoon SVG Font Viewer");

		ScrollPane scrollableGlyphs = allGlyphs();
		scrollableGlyphs.setStyle("-fx-background-insets: 0;");
		HBox layout = new HBox(scrollableGlyphs, glyphDetailViewer);
		HBox.setHgrow(scrollableGlyphs, Priority.ALWAYS);

		// display the app.  
		stage.setScene(new Scene(layout));
		stage.show();
	}

	private ScrollPane allGlyphs() throws IOException {
		
		List<SVGGlyph> glyphs = SVGGlyphLoader.getAllGlyphsIDs().stream().map(item -> SVGGlyphLoader.getIcoMoonGlyph(item)).collect(Collectors.toList());
		glyphs.forEach(glyph -> glyph.setSize(16, 16));
		List<Button> iconButtons = glyphs.stream().map(this::createIconButton).collect(Collectors.toList());
		iconButtons.get(0).fire();

		FlowPane glyphLayout = new FlowPane();
		glyphLayout.setHgap(10);
		glyphLayout.setVgap(10);
		glyphLayout.setPadding(new Insets(10));
		glyphLayout.getChildren().setAll(iconButtons);
		glyphLayout.setPrefSize(600, 300);

		ScrollPane scrollableGlyphs = new ScrollPane(glyphLayout);
		scrollableGlyphs.setFitToWidth(true);

		return scrollableGlyphs;
	}

	private Button createIconButton(SVGGlyph glyph) {
		C3DButton button = new C3DButton(null, glyph);
//		button.setStyle("-fx-background-color:#0F9D58;");
		button.setRipplerFill(Color.valueOf("#0F9D58"));
//		glyph.setStyle("-fx-background-color:WHITE;");
		button.setOnAction(event -> viewGlyphDetail(glyph));
		Tooltip.install(button, new Tooltip(glyph.getName()));
		return button;
	}

	private void viewGlyphDetail(SVGGlyph glyph) {
		glyphDetailViewer.setGlyph(SVGGlyphLoader.getIcoMoonGlyph(fileName  + "." + glyph.getName()));
	}

	public static void main(String[] args) {
		launch(args);
	}
}

class GlyphDetailViewer extends VBox {
	private final int MIN_ICON_SIZE = 8;
	private final int DEFAULT_ICON_SIZE = 128;
	private final int MAX_ICON_SIZE = 256;

	private final ObjectProperty<SVGGlyph> glyph = new SimpleObjectProperty<>();
	private final Label idLabel = new Label();
	private final Label nameLabel = new Label();
	private final ColorPicker colorPicker = new ColorPicker(Color.BLACK);
	private final C3DSlider sizeSlider = new C3DSlider(MIN_ICON_SIZE, MAX_ICON_SIZE, DEFAULT_ICON_SIZE);
	private final Label sizeLabel = new Label();
	private StackPane centeredGlyph = new StackPane();

	public GlyphDetailViewer() {
		GridPane details = new GridPane();
		details.setHgap(10);
		details.setVgap(10);
		details.setPadding(new Insets(10));
		details.setMinSize(GridPane.USE_PREF_SIZE, GridPane.USE_PREF_SIZE);

		Label sizeCalculator = new Label("999");
		Group sizingRoot = new Group(sizeCalculator);
		new Scene(sizingRoot);
		sizingRoot.applyCss();
		sizingRoot.layout();
		sizeLabel.setMinWidth(Label.USE_PREF_SIZE);
		sizeLabel.setPrefWidth(sizeCalculator.getWidth());
		sizeLabel.setAlignment(Pos.BASELINE_RIGHT);

		sizeSlider.setIndicatorPosition(IndicatorPosition.RIGHT);
		HBox sizeControl = new HBox(5, sizeLabel, sizeSlider);
		sizeControl.prefWidthProperty().bind(colorPicker.widthProperty());

		details.addRow(0, new Label("Id"), idLabel);
		details.addRow(1, new Label("Name"), nameLabel);

		details.addRow(2, new Label("Color"), colorPicker);
		details.addRow(3, new Label("Size"), sizeControl);

		sizeLabel.textProperty().bind(sizeSlider.valueProperty().asString("%.0f"));

		VBox.setVgrow(centeredGlyph, Priority.ALWAYS);
		StackPane.setMargin(centeredGlyph, new Insets(10));

		centeredGlyph.setPrefSize(MAX_ICON_SIZE + 10 * 2, MAX_ICON_SIZE + 10 * 2);

		glyphProperty().addListener((observable, oldValue, newValue) -> {
			if (oldValue != null) {
				oldValue.fillProperty().unbind();
				oldValue.prefWidthProperty().unbind();
				oldValue.prefHeightProperty().unbind();
			}

			refreshView();
		});

		getChildren().setAll(details, centeredGlyph);
	}

	private void refreshView() {
		if (glyph.getValue() == null) {
			idLabel.setText("");
			nameLabel.setText("");

			return;
		}

		glyph.get().setMinSize(StackPane.USE_PREF_SIZE, StackPane.USE_PREF_SIZE);
		glyph.get().setPrefSize(sizeSlider.getValue(), sizeSlider.getValue());
		glyph.get().setMaxSize(StackPane.USE_PREF_SIZE, StackPane.USE_PREF_SIZE);
		glyph.get().prefWidthProperty().bind(sizeSlider.valueProperty());
		glyph.get().prefHeightProperty().bind(sizeSlider.valueProperty());

		idLabel.setText(String.format("%04d", glyph.get().getGlyphId()));

		nameLabel.setText(glyph.get().getName());

		glyph.get().setFill(colorPicker.getValue());
		glyph.get().fillProperty().bind(colorPicker.valueProperty());

		centeredGlyph.getChildren().setAll(glyph.get());
	}

	public SVGGlyph getGlyph() {
		return glyph.get();
	}

	public ObjectProperty<SVGGlyph> glyphProperty() {
		return glyph;
	}

	public void setGlyph(SVGGlyph glyph) {
		this.glyph.set(glyph);
	}

}