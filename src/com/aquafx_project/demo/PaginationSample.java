package com.aquafx_project.demo;

import com.aquafx_project.AquaFx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Pagination;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
 
public class PaginationSample extends Application {
 
    private Pagination pagination;
    final String[] textPages = new String[]{
        "The apple is the pomaceous fruit of the apple tree, species Malus "
        + "domestica in the rose family (Rosaceae). It is one of the most "
        + "widely cultivated tree fruits, and the most widely known of "
        + "the many members of genus Malus that are used by humans. "
        + "The tree originated in Western Asia, where its wild ancestor, "
        + "the Alma, is still found today.",
        "The hawthorn is a large genus of shrubs and trees in the rose family,"
        + "Rosaceae, native to temperate regions of the Northern Hemisphere "
        + "in Europe, Asia and North America. The name hawthorn was "
        + "originally applied to the species native to northern Europe, "
        + "especially the Common Hawthorn C. monogyna, and the unmodified "
        + "name is often so used in Britain and Ireland.",
        "The ivy is a flowering plant in the grape family (Vitaceae) native to "
        + " eastern Asia in Japan, Korea, and northern and eastern China. "
        + "It is a deciduous woody vine growing to 30 m tall or more given "
        + "suitable support,  attaching itself by means of numerous small "
        + "branched tendrils tipped with sticky disks.",
        "The quince is the sole member of the genus Cydonia and is native to "
        + "warm-temperate southwest Asia in the Caucasus region. The "
        + "immature fruit is green with dense grey-white pubescence, most "
        + "of which rubs off before maturity in late autumn when the fruit "
        + "changes color to yellow with hard, strongly perfumed flesh.",
        "Aster (syn. Diplopappus Cass.) is a genus of flowering plants "
        + "in the family Asteraceae. The genus once contained nearly 600 "
        + "species in Eurasia and North America, but after morphologic "
        + "and molecular research on the genus during the 1990s, it was "
        + "decided that the North American species are better treated in a "
        + "series of other related genera. After this split there are "
        + "roughly 180 species within the genus, all but one being confined "
        + "to Eurasia."
    };
 
    public static void main(String[] args) throws Exception {
        launch(args);
    }
 
    public int itemsPerPage() {
        return 1;
    }
 
    public VBox createPage(int pageIndex) {
        VBox box = new VBox(5);
        int page = pageIndex * itemsPerPage();
        for (int i = page; i < page + itemsPerPage(); i++) {
            TextArea text = new TextArea(textPages[i]);
            text.setWrapText(true);
            box.getChildren().add(text);
        }
        return box;
    }
 
    @Override
    public void start(final Stage stage) throws Exception {
        pagination = new Pagination(28, 0);
        pagination.getStyleClass().add(Pagination.STYLE_CLASS_BULLET);
        pagination.setPageFactory(new Callback<Integer, Node>() {
 
            @Override
            public Node call(Integer pageIndex) {
                if (pageIndex >= textPages.length) {
                    return null;
                } else {
                    return createPage(pageIndex);
                }
            }
        });
 
        AnchorPane anchor = new AnchorPane();
        AnchorPane.setTopAnchor(pagination, 10.0);
        AnchorPane.setRightAnchor(pagination, 10.0);
        AnchorPane.setBottomAnchor(pagination, 10.0);
        AnchorPane.setLeftAnchor(pagination, 10.0);
        anchor.getChildren().addAll(pagination);
        Scene scene = new Scene(anchor, 500, 300);
        stage.setScene(scene);
        stage.setTitle("PaginationSample");
        AquaFx.style();
        stage.show();
    }
}