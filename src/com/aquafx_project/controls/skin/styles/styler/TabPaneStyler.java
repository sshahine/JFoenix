package com.aquafx_project.controls.skin.styles.styler;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import com.aquafx_project.controls.skin.styles.ControlSizeVariant;
import com.aquafx_project.controls.skin.styles.IllegalStyleCombinationException;
import com.aquafx_project.controls.skin.styles.StyleDefinition;
import com.aquafx_project.controls.skin.styles.TabPaneType;

/**
 * The TabPaneStyler with fluent API to change the default style of a TabPane.
 * 
 * @author claudinezillmann
 * 
 */
public class TabPaneStyler extends Styler<TabPane> {

    /**
     * TabPaneType of a TabPane.
     */
    private TabPaneType type;

    /**
     * Creates a new Instance of TabPaneStyler. This has to be the first invocation on
     * TabPaneStyler.
     * 
     * @return The TabPaneStyler.
     */
    public static TabPaneStyler create() {
        return new TabPaneStyler();
    }

    @Override public TabPaneStyler setSizeVariant(ControlSizeVariant sizeVariant) {
        return (TabPaneStyler) super.setSizeVariant(sizeVariant);
    }

    /**
     * Adds a TabPaneType to the TabPane
     * 
     * @param type
     *            The TabPaneType for the TabPane.
     * @return the TabPaneStyler with the added TabPaneType.
     */
    public TabPaneStyler setType(TabPaneType type) {
        this.type = type;
        check();
        return this;
    }

    @Override public void check() {
        if (type != null && type.equals(TabPaneType.SMALL_ICON_BUTTONS) && sizeVariant != null) {
            throw new IllegalStyleCombinationException();
        }
    }

    @Override public List<StyleDefinition> getAll() {
        List<StyleDefinition> ret = new ArrayList<>(super.getAll());
        ret.add(sizeVariant);
        ret.add(type);
        return ret;
    }

    @Override public void style(final TabPane tabPane) {
        super.style(tabPane);
        if (type != null && type == TabPaneType.ICON_BUTTONS) {
            Platform.runLater(new Runnable() {

                @Override public void run() {
                    for (Tab tab : tabPane.getTabs()) {
                        if (tab.getGraphic() != null) {
                            tab.getGraphic().getStyleClass().add("icon");
                        }
                    }
                }
            });
        }
    }
}
