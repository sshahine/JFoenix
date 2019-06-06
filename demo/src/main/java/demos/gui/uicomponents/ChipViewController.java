package demos.gui.uicomponents;

import com.jfoenix.controls.JFXChipView;
import com.jfoenix.controls.JFXDefaultChip;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.svg.SVGGlyphLoader;
import io.datafx.controller.ViewController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.util.StringConverter;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Objects;

@ViewController(value = "/fxml/ui/ChipView.fxml", title = "Material Design Example")
public class ChipViewController {
    @FXML
    private JFXChipView<MyShape> chipView;

    @PostConstruct
    public void init() throws Exception {

        chipView.setChipFactory((emailJFXChipView, email) -> new JFXDefaultChip<MyShape>(emailJFXChipView, email){
            {
                if (getItem().image != null) {
                    getItem().image.getStyleClass().add("chip-icon");
                    root.getChildren().add(0, getItem().image);
                }
            }
        });

        HashMap<String, MyShape> suggestions = new HashMap<>();
        suggestions.put("Glass", new MyShape("Glass", SVGGlyphLoader.getIcoMoonGlyph("icomoon.svg.glass")));
        suggestions.put("Star", new MyShape("Star", SVGGlyphLoader.getIcoMoonGlyph("icomoon.svg.star")));
        suggestions.put("Music", new MyShape("Music", SVGGlyphLoader.getIcoMoonGlyph("icomoon.svg.music")));
        final SVGGlyph icoMoonGlyph = SVGGlyphLoader.getIcoMoonGlyph("icomoon.svg.heart");
        icoMoonGlyph.getStyleClass().add("heart");
        suggestions.put("Heart", new MyShape("Heart", icoMoonGlyph));
        suggestions.put("Film", new MyShape("Film", SVGGlyphLoader.getIcoMoonGlyph("icomoon.svg.film")));

        chipView.setConverter(new StringConverter<MyShape>() {
            @Override
            public String toString(MyShape object) {
                return object.toString();
            }

            @Override
            public MyShape fromString(String string) {
                MyShape found = suggestions.get(string);
                return found == null ? new MyShape(string, null) : found;
            }
        });
        chipView.getSuggestions().addAll(suggestions.values());
        chipView.setSuggestionsCellFactory(param -> new JFXListCell<>());

        JFXDepthManager.setDepth(chipView, 2);
    }

    class MyShape {
        String name;
        Node image;

        public MyShape(String name, Node image) {
            this.name = name;
            this.image = image;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MyShape email1 = (MyShape) o;
            return Objects.equals(name, email1.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }
}
