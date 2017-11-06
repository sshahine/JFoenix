package demos.gui.uicomponents;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXSlider.IndicatorPosition;
import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.svg.SVGGlyphLoader;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@ViewController(value = "/fxml/ui/SVGLoader.fxml", title = "Material Design Example")
public class SVGLoaderController {

    private static final String FX_BACKGROUND_INSETS_0 = "-fx-background-insets: 0;";
    private static final String DEFAULT_OPACITY = "33";
    private static final String THUMB = ".thumb";

    @FXMLViewFlowContext
    private ViewFlowContext context;
    @FXML
    private StackPane detailsContainer;
    @FXML
    private JFXButton browseFont;
    @FXML
    private StackPane iconsContainer;

    private JFXButton lastClicked = null;
    private final String fileName = "icomoon.svg";
    private GlyphDetailViewer glyphDetailViewer;

    /**
     * init fxml when loaded.
     */
    @PostConstruct
    public void init() throws Exception {
        final Stage stage = (Stage) context.getRegisteredObject("Stage");

        glyphDetailViewer = new GlyphDetailViewer();
        detailsContainer.getChildren().add(glyphDetailViewer);


        ScrollPane scrollableGlyphs = allGlyphs();
        scrollableGlyphs.setStyle(FX_BACKGROUND_INSETS_0);

        iconsContainer.getChildren().add(scrollableGlyphs);

        browseFont.setOnAction((action) -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("SVG files (*.svg)", "*.svg");
            fileChooser.getExtensionFilters().add(extFilter);
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                SVGGlyphLoader.clear();
                try {
                    SVGGlyphLoader.loadGlyphsFont(new FileInputStream(file), file.getName());
                    ScrollPane newglyphs = allGlyphs();
                    newglyphs.setStyle(FX_BACKGROUND_INSETS_0);

                    iconsContainer.getChildren().clear();
                    iconsContainer.getChildren().add(newglyphs);

                } catch (IOException ioExc) {
                    ioExc.printStackTrace();
                }
            }
        });
    }


    private ScrollPane allGlyphs() {

        List<SVGGlyph> glyphs = SVGGlyphLoader.getAllGlyphsIDs()
            .stream()
            .map(glyphName -> {
                try {
                    return SVGGlyphLoader.getIcoMoonGlyph(glyphName);
                } catch (Exception e) {
                    return null;
                }
            })
            .collect(Collectors.toList());
        glyphs.sort(Comparator.comparing(SVGGlyph::getName));


        glyphs.forEach(glyph -> glyph.setSize(16));
        List<Button> iconButtons = glyphs.stream().map(this::createIconButton).collect(Collectors.toList());
        // important to improve the performance of animation in scroll pane so buttons are treated as images
        iconButtons.forEach(button -> button.setCache(true));
        Platform.runLater(()->iconButtons.get(0).fire());

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
        JFXButton button = new JFXButton(null, glyph);
        button.setPrefSize(30,30);
        button.setMinSize(30,30);
        button.setMaxSize(30,30);
        button.ripplerFillProperty().bind(glyphDetailViewer.colorPicker.valueProperty());
        glyphDetailViewer.colorPicker.valueProperty().addListener((o, oldVal, newVal) -> {
            String webColor = "#" + Integer.toHexString(newVal.hashCode()).substring(0, 6).toUpperCase();
            BackgroundFill fill = ((Region) glyphDetailViewer.sizeSlider.lookup(THUMB)).getBackground()
                .getFills()
                .get(0);
            ((Region) glyphDetailViewer.sizeSlider.lookup(THUMB)).setBackground(new Background(new BackgroundFill(
                Color.valueOf(webColor),
                fill.getRadii(),
                fill.getInsets())));
            if (lastClicked != null) {
                final String currentColor = glyphDetailViewer.colorPicker.getValue()
                    .toString()
                    .substring(0, 8);
                final BackgroundFill backgroundFill = new BackgroundFill(Color.valueOf(currentColor + DEFAULT_OPACITY),
                    lastClicked.getBackground().getFills().get(0).getRadii(),
                    null);
                lastClicked.setBackground(new Background(backgroundFill));
            }
        });
        button.setOnAction(event -> {
            if (lastClicked != null) {
                lastClicked.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, lastClicked.getBackground().getFills().get(0).getRadii(), null)));
            }
            final String currentColor = glyphDetailViewer.colorPicker.getValue()
                .toString()
                .substring(0, 8);

            button.setBackground(new Background(new BackgroundFill(Color.valueOf(currentColor + DEFAULT_OPACITY),
                button.getBackground() == null ? null : button.getBackground().getFills().get(0).getRadii(),
                null)));
            lastClicked = button;
            viewGlyphDetail(glyph);
        });
        Tooltip.install(button, new Tooltip(glyph.getName()));
        return button;
    }

    private void viewGlyphDetail(SVGGlyph glyph) {
        try {
            glyphDetailViewer.setGlyph(SVGGlyphLoader.getIcoMoonGlyph(fileName + "." + glyph.getName()));
        } catch (Exception e) {
        }
    }

    private static final class GlyphDetailViewer extends VBox {
        private static final int MIN_ICON_SIZE = 8;
        private static final int DEFAULT_ICON_SIZE = 128;
        private static final int MAX_ICON_SIZE = 256;

        private final ObjectProperty<SVGGlyph> glyph = new SimpleObjectProperty<>();
        private final Label idLabel = new Label();
        private final Label nameLabel = new Label();
        private final ColorPicker colorPicker = new ColorPicker(Color.BLACK);
        private final JFXSlider sizeSlider = new JFXSlider(MIN_ICON_SIZE, MAX_ICON_SIZE, DEFAULT_ICON_SIZE);
        private final Label sizeLabel = new Label();
        private final StackPane centeredGlyph = new StackPane();

        GlyphDetailViewer() {
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

            sizeSlider.valueProperty().addListener(observable -> glyph.get().setSize(sizeSlider.getValue()));
            idLabel.setText(String.format("%04d", glyph.get().getGlyphId()));
            nameLabel.setText(glyph.get().getName());
            glyph.get().setFill(colorPicker.getValue());
            glyph.get().fillProperty().bind(colorPicker.valueProperty());
            centeredGlyph.getChildren().setAll(glyph.get());
        }

        public final SVGGlyph getGlyph() {
            return glyph.get();
        }

        final ObjectProperty<SVGGlyph> glyphProperty() {
            return glyph;
        }

        final void setGlyph(SVGGlyph glyph) {
            glyph.setSize(sizeSlider.getValue());
            this.glyph.set(glyph);
        }
    }

}


