package demos.gui.uicomponents;

import io.datafx.controller.FXMLController;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.datafx.controller.util.VetoException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXSlider.IndicatorPosition;
import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.svg.SVGGlyphLoader;

@FXMLController(value = "/resources/fxml/ui/SVGLoader.fxml", title = "Material Design Example")
public class SVGLoaderController {
	
	@FXMLViewFlowContext
	private ViewFlowContext context;

	@FXML private StackPane detailsContainer;
	@FXML private JFXButton browseFont;
	@FXML private StackPane iconsContainer;

	private String fileName = "icomoon.svg";
	private GlyphDetailViewer glyphDetailViewer; 
	
	@PostConstruct
	public void init() throws FlowException, VetoException, Exception {
		if(((Pane) context.getRegisteredObject("ContentPane")).getChildren().size() > 0)
			Platform.runLater(()-> ((Pane)((Pane) context.getRegisteredObject("ContentPane")).getChildren().get(0)).getChildren().remove(1));
		final Stage stage = (Stage) context.getRegisteredObject("Stage");
		
		glyphDetailViewer = new GlyphDetailViewer();
		detailsContainer.getChildren().add(glyphDetailViewer);
		
		ScrollPane scrollableGlyphs = allGlyphs();
		scrollableGlyphs.setStyle("-fx-background-insets: 0;");
		
		iconsContainer.getChildren().add(scrollableGlyphs);
		
		browseFont.setOnAction((action)->{
			FileChooser fileChooser = new FileChooser();
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("SVG files (*.svg)", "*.svg");
			fileChooser.getExtensionFilters().add(extFilter);
			File file = fileChooser.showOpenDialog(stage);
			if(file!=null){
				SVGGlyphLoader.clear();     		
				try {
					SVGGlyphLoader.loadGlyphsFont(new FileInputStream(file),file.getName());
					ScrollPane newglyphs = allGlyphs();    		
					newglyphs.setStyle("-fx-background-insets: 0;");
					
					iconsContainer.getChildren().clear();
					iconsContainer.getChildren().add(newglyphs);
					
				} catch (Exception e) {
					e.printStackTrace();
				}	 
			}
		});
		
		
	}
	
	
	private ScrollPane allGlyphs() throws IOException {

		List<SVGGlyph> glyphs = SVGGlyphLoader.getAllGlyphsIDs().stream().map(item -> SVGGlyphLoader.getIcoMoonGlyph(item)).collect(Collectors.toList());
		Collections.sort(glyphs, (o1,o2)-> o1.getName().compareTo(o2.getName()));
		
		
		glyphs.forEach(glyph -> glyph.setSize(16, 16));
		List<Button> iconButtons = glyphs.stream().map(this::createIconButton).collect(Collectors.toList());
		// important to improve the performance of animation in scroll pane so buttons are treated as images
		iconButtons.forEach(button-> button.setCache(true));
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

	private JFXButton lastClicked = null;
	
	private Button createIconButton(SVGGlyph glyph) {
		JFXButton button = new JFXButton(null, glyph);
		button.ripplerFillProperty().bind(glyphDetailViewer.colorPicker.valueProperty());
		glyphDetailViewer.colorPicker.valueProperty().addListener((o,oldVal,newVal)->{
			String webColor = "#" + Integer.toHexString(newVal.hashCode()).substring(0, 6).toUpperCase();
			glyphDetailViewer.sizeSlider.lookup(".thumb").setStyle("-fx-stroke: "+webColor+"; -fx-fill: "+webColor+";");
			if(lastClicked != null) lastClicked.setBackground(new Background(new BackgroundFill(Color.valueOf(glyphDetailViewer.colorPicker.getValue().toString().substring(0, 8)+"33"), null, null)));
		});
		button.setOnAction(event -> {
			if(lastClicked != null) lastClicked.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
			button.setBackground(new Background(new BackgroundFill(Color.valueOf(glyphDetailViewer.colorPicker.getValue().toString().substring(0, 8)+"33"), null, null)));
			lastClicked = button;
			viewGlyphDetail(glyph);	
		});
		Tooltip.install(button, new Tooltip(glyph.getName()));
		return button;
	}

	private void viewGlyphDetail(SVGGlyph glyph) {
		glyphDetailViewer.setGlyph(SVGGlyphLoader.getIcoMoonGlyph(fileName  + "." + glyph.getName()));
	}
	
	class GlyphDetailViewer extends VBox {
		private final int MIN_ICON_SIZE = 8;
		private final int DEFAULT_ICON_SIZE = 128;
		private final int MAX_ICON_SIZE = 256;

		private final ObjectProperty<SVGGlyph> glyph = new SimpleObjectProperty<>();
		private final Label idLabel = new Label();
		private final Label nameLabel = new Label();
		private final ColorPicker colorPicker = new ColorPicker(Color.BLACK);
		private final JFXSlider sizeSlider = new JFXSlider(MIN_ICON_SIZE, MAX_ICON_SIZE, DEFAULT_ICON_SIZE);
		private final Label sizeLabel = new Label();
		private StackPane centeredGlyph = new StackPane();

		public GlyphDetailViewer() {
			GridPane details = new GridPane();
			details.setHgap(10);
			details.setVgap(10);
			details.setPadding(new Insets(24));
			details.setMinSize(GridPane.USE_PREF_SIZE, GridPane.USE_PREF_SIZE);

			Label sizeCalculator = new Label("999");
			Group sizingRoot = new Group(sizeCalculator);
			new Scene(sizingRoot);
			sizingRoot.applyCss();
			sizingRoot.layout();
			sizeLabel.setMinWidth(25);
			sizeLabel.setPrefWidth(sizeCalculator.getWidth());
			sizeLabel.setAlignment(Pos.BASELINE_RIGHT);

			sizeSlider.setIndicatorPosition(IndicatorPosition.RIGHT);
			sizeSlider.getStyleClass().add("svg-slider");
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
			this.setMinWidth(300);
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
	
}


