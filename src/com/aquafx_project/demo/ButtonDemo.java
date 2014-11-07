package com.aquafx_project.demo;


import javafx.application.Application;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import com.aquafx_project.AquaFx;
import com.aquafx_project.controls.skin.styles.ButtonType;

public class ButtonDemo extends Application {
    final Tab tabH = new Tab();
    final Tab tabI = new Tab();

    final ObservableList<Person> data = FXCollections.observableArrayList(
            new Person("John", "Doe", "john.doe@foo.com", "jd@foo.com", true),
            new Person("Jane", "Doe", "jane.doe@example.com", "jane.d@foo.com", true),
            new Person("Steve", "Schmidt", "steve.schmidt@example.com", null, false),
            new Person("Lisa", "Jones", "lisa.jones@foo.com", "lisa.jones@foo.com", true),
            new Person("Marcel", "Miller", "marcel.miller@foo.com", "", false));

    @Override public void start(Stage stage) throws Exception {
        AquaFx.styleStage(stage, StageStyle.UNIFIED);
        BorderPane pane = new BorderPane();

        ToolBar toolBar = new ToolBar();
        ColorPicker colorTB = new ColorPicker(Color.rgb(194, 222, 254));

        Separator seperateIt = new Separator();
        ToggleGroup toolbarGroup = new ToggleGroup();
        ToggleButton sampleButton4 = new ToggleButton("TG1");
        sampleButton4.setToggleGroup(toolbarGroup);
        sampleButton4.setSelected(true);
        AquaFx.createToggleButtonStyler().setType(ButtonType.LEFT_PILL).style(sampleButton4);
        ToggleButton sampleButton5 = new ToggleButton("TG2");
        sampleButton5.setToggleGroup(toolbarGroup);
        sampleButton5.setSelected(true);
        AquaFx.createToggleButtonStyler().setType(ButtonType.CENTER_PILL).style(sampleButton5);
        ToggleButton sampleButton6 = new ToggleButton("TG3");
        sampleButton6.setToggleGroup(toolbarGroup);
        sampleButton6.setSelected(true);
        AquaFx.createToggleButtonStyler().setType(ButtonType.RIGHT_PILL).style(sampleButton6);

        Separator seperateIt2 = new Separator();

        Button menuPillButton1 = new Button("PB 1");
        AquaFx.createButtonStyler().setType(ButtonType.LEFT_PILL).style(menuPillButton1);
        Button menuPillButton2 = new Button("PB 2");
        AquaFx.createButtonStyler().setType(ButtonType.CENTER_PILL).style(menuPillButton2);
        Button menuPillButton3 = new Button("PB 3");
        AquaFx.createButtonStyler().setType(ButtonType.RIGHT_PILL).style(menuPillButton3);

        Button sampleButton = new Button("Button");
        ToggleButton sampleButton1 = new ToggleButton("Toggle");
        sampleButton1.setDisable(true);
        ToggleButton sampleButton2 = new ToggleButton("Toggle");
        ToggleButton sampleButton3 = new ToggleButton("Toggle2");
        sampleButton3.setSelected(true);
        toolBar.getItems().addAll(colorTB, sampleButton, sampleButton1, sampleButton2, sampleButton3, seperateIt, sampleButton4,
                sampleButton5, sampleButton6, seperateIt2, menuPillButton1, menuPillButton2, menuPillButton3);

        pane.setTop(toolBar);

        /**
         * TabPane
         */
        TabPane buttonTabPane = new TabPane();

        // Create Tabs
        Tab tabD = new Tab();
        tabD.setText("Buttons");
        VBox buttonBox = new VBox();
        buttonBox.setSpacing(10);
        buttonBox.setPadding(new Insets(10));
        Button b1 = new Button();
        b1.setText("Default (push to enable Tab 'Progress')");
        b1.setDefaultButton(true);
        b1.setTooltip(new Tooltip("This is a ToolTip"));
        b1.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                tabI.setDisable(false);
                tabH.setDisable(false);
            }
        });
        buttonBox.getChildren().add(b1);
        Button b2 = new Button();
        b2.setText("Default");
        b2.setDisable(true);
        b2.setDefaultButton(true);
        buttonBox.getChildren().add(b2);
        Button b3 = new Button();
        b3.setText("Normal (push to disable Tab 'Progress')");
        b3.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                tabH.setDisable(true);
            }
        });
        buttonBox.getChildren().add(b3);
        Button b4 = new Button();
        b4.setText("Normal");
        b4.setDisable(true);
        buttonBox.getChildren().add(b4);

        Button helpButton = new Button("?");
        AquaFx.createButtonStyler().setType(ButtonType.HELP).style(helpButton);
        buttonBox.getChildren().add(helpButton);

        Hyperlink link = new Hyperlink("Hyperlink");
        Hyperlink link2 = new Hyperlink("disabled Hyperlink");
        link2.setDisable(true);
        buttonBox.getChildren().add(link);
        buttonBox.getChildren().add(link2);
        ScrollBar scBar = new ScrollBar();
        buttonBox.getChildren().add(scBar);

        tabD.setContent(buttonBox);
        buttonTabPane.getTabs().add(tabD);

        Tab tabE = new Tab();
        tabE.setText("RadioButtons");
        VBox radioButtonBox = new VBox();
        radioButtonBox.setSpacing(10);
        radioButtonBox.setPadding(new Insets(10));
        RadioButton raBu1 = new RadioButton("Normal");
        radioButtonBox.getChildren().add(raBu1);
        RadioButton raBu2 = new RadioButton("Normal");
        raBu2.setDisable(true);
        radioButtonBox.getChildren().add(raBu2);
        RadioButton raBu3 = new RadioButton("Selected");
        raBu3.setSelected(true);
        radioButtonBox.getChildren().add(raBu3);
        RadioButton raBu4 = new RadioButton("Selected");
        raBu4.setDisable(true);
        raBu4.setSelected(true);
        radioButtonBox.getChildren().add(raBu4);
        tabE.setContent(radioButtonBox);
        buttonTabPane.getTabs().add(tabE);

        Tab tabF = new Tab();
        tabF.setText("CheckBoxes");
        VBox checkBoxBox = new VBox();
        checkBoxBox.setSpacing(10);
        checkBoxBox.setPadding(new Insets(10));
        CheckBox box1 = new CheckBox("Normal");
        checkBoxBox.getChildren().add(box1);
        CheckBox box2 = new CheckBox("Normal");
        box2.setDisable(true);
        checkBoxBox.getChildren().add(box2);
        CheckBox box3 = new CheckBox("Selected");
        box3.setSelected(true);
        checkBoxBox.getChildren().add(box3);
        CheckBox box4 = new CheckBox("Selected");
        box4.setSelected(true);
        box4.setDisable(true);
        checkBoxBox.getChildren().add(box4);
        CheckBox box5 = new CheckBox("Indeterminate");
        box5.setIndeterminate(true);
        checkBoxBox.getChildren().add(box5);
        CheckBox box6 = new CheckBox("Indeterminate");
        box6.setIndeterminate(true);
        box6.setDisable(true);
        checkBoxBox.getChildren().add(box6);
        tabF.setContent(checkBoxBox);
        buttonTabPane.getTabs().add(tabF);

        Tab tabG = new Tab();
        tabG.setText("Toggles & Pills");
        VBox togglesBox = new VBox();
        togglesBox.setSpacing(10);
        togglesBox.setPadding(new Insets(10));
        HBox toggleGroupBox = new HBox();
        ToggleGroup group = new ToggleGroup();
        ToggleButton tb1 = new ToggleButton("First");
        tb1.setToggleGroup(group);
        tb1.setSelected(true);
        AquaFx.createToggleButtonStyler().setType(ButtonType.LEFT_PILL).style(tb1);
        toggleGroupBox.getChildren().add(tb1);
        ToggleButton tb2 = new ToggleButton("Second");
        tb2.setToggleGroup(group);
        AquaFx.createToggleButtonStyler().setType(ButtonType.CENTER_PILL).style(tb2);
        toggleGroupBox.getChildren().add(tb2);
        ToggleButton tb3 =  new ToggleButton("Third");
        tb3.setToggleGroup(group);
        AquaFx.createToggleButtonStyler().setType(ButtonType.RIGHT_PILL).style(tb3);
        toggleGroupBox.getChildren().add(tb3);
        togglesBox.getChildren().add(toggleGroupBox);
        ToggleButton tb4 = new ToggleButton("Alone");
        tb4.setSelected(true);
        togglesBox.getChildren().add(tb4);
        HBox pillButtonBox = new HBox();
        Button pb1 = new Button();
        pb1.setText("Button 1");
        pb1.setTooltip(new Tooltip("This is a ToolTip"));
        AquaFx.createButtonStyler().setType(ButtonType.LEFT_PILL).style(pb1);
        pillButtonBox.getChildren().add(pb1);
        Button pb2 = new Button();
        pb2.setText("Button 2");
        pb2.setDisable(true);
        AquaFx.createButtonStyler().setType(ButtonType.CENTER_PILL).style(pb2);
        pillButtonBox.getChildren().add(pb2);
        Button pb3 = new Button();
        pb3.setText("Button 3");
        AquaFx.createButtonStyler().setType(ButtonType.CENTER_PILL).style(pb3);
        pillButtonBox.getChildren().add(pb3);
        Button pb4 = new Button();
        pb4.setText("Button 4");
        AquaFx.createButtonStyler().setType(ButtonType.RIGHT_PILL).style(pb4);
        pillButtonBox.getChildren().add(pb4);
        togglesBox.getChildren().add(pillButtonBox);
        tabG.setContent(togglesBox);
        buttonTabPane.getTabs().add(tabG);

        // Tab tabH = new Tab();
        tabH.setText("Progress");
        final Float[] values = new Float[] { -1.0f, 0f, 0.6f, 1.0f };
        final ProgressBar[] pbs = new ProgressBar[values.length];
        final ProgressIndicator[] pins = new ProgressIndicator[values.length];
        final HBox hbs[] = new HBox[values.length];
        for (int i = 0; i < values.length; i++) {
            final Label label = new Label();
            label.setText("progress: " + values[i]);
            label.setPrefWidth(100d);

            final ProgressBar pb = pbs[i] = new ProgressBar();
            pb.setProgress(values[i]);

            final ProgressIndicator pin = pins[i] = new ProgressIndicator();
            pin.setProgress(values[i]);
            final HBox hb = hbs[i] = new HBox();
            hb.setSpacing(10);
            hb.setAlignment(Pos.CENTER_LEFT);
            hb.getChildren().addAll(label, pb, pin);
        }
        final VBox vb = new VBox();
        vb.setSpacing(5);
        vb.setPadding(new Insets(10));
        vb.getChildren().addAll(hbs);
        tabH.setContent(vb);
        buttonTabPane.getTabs().add(tabH);

        tabI.setText("Disabled Tab");
        tabI.setDisable(true);
        TabPane innerTabPane = new TabPane();
        Label label = new Label("Lipsum");
        Tab onlyTab = new Tab("single tab");
        onlyTab.setContent(label);
        innerTabPane.getTabs().add(onlyTab);
        tabI.setContent(innerTabPane);
        buttonTabPane.getTabs().add(tabI);

        Tab tabTexts = new Tab();
        tabTexts.setText("Texts");
        VBox txts = new VBox();
        HBox textfieldBox1 = new HBox();
        textfieldBox1.setSpacing(10);
        textfieldBox1.setPadding(new Insets(10));
        Menu item1 = new Menu("test submenu");
        MenuItem subMenuItem1 = new MenuItem("Sub Menu Item 1");
        MenuItem subMenuItem2 = new MenuItem("Sub Menu Item 2");
        MenuItem subMenuItem3 = new MenuItem("Sub Menu Item 3");
        item1.getItems().addAll(subMenuItem1, subMenuItem2, subMenuItem3);
        TextField tf1 = new TextField("Textfield");
        ContextMenu cm = new ContextMenu(new MenuItem("test"), item1, new MenuItem("test"));
        tf1.setContextMenu(cm);
        textfieldBox1.getChildren().add(tf1);
        TextField tf2 = new TextField();
        textfieldBox1.getChildren().add(tf2);
        HBox textfieldBox2 = new HBox();
        textfieldBox2.setSpacing(10);
        textfieldBox2.setPadding(new Insets(10));
        TextField tf3 = new TextField("disabled Textfield");
        tf3.setDisable(true);
        tf3.setEditable(false);
        textfieldBox2.getChildren().add(tf3);
        TextField tf4 = new TextField();
        tf4.setPromptText("prompt text");
        textfieldBox2.getChildren().add(tf4);
        txts.getChildren().add(textfieldBox2);
        HBox textfieldBox3 = new HBox();
        textfieldBox3.setSpacing(10);
        textfieldBox3.setPadding(new Insets(10));
        TextField tf5 = new TextField("non-editable textfield");
        tf5.setEditable(false);
        textfieldBox3.getChildren().add(tf5);
        PasswordField pw1 = new PasswordField();
        pw1.setText("password");
        textfieldBox3.getChildren().add(pw1);
        txts.getChildren().add(textfieldBox3);
        VBox textareaBox = new VBox();
        textareaBox.setSpacing(10);
        textareaBox.setPadding(new Insets(10));
        TextArea area = new TextArea();
        area.setPromptText("TextArea with promptText");
        area.setPrefWidth(290);
        area.setPrefHeight(50);
        area.setPrefColumnCount(80);
        textareaBox.getChildren().add(area);
        TextArea area2 = new TextArea();
        area2.setText("Disabled");
        area2.setDisable(true);
        area2.setPrefWidth(290);
        area2.setPrefHeight(50);
        textareaBox.getChildren().add(area2);
        txts.getChildren().add(textareaBox);
        tabTexts.setContent(txts);
        buttonTabPane.getTabs().add(tabTexts);

        pane.setCenter(buttonTabPane);

        TabPane tabPane = new TabPane();

        Tab tabChoiceBox = new Tab();
        tabChoiceBox.setText("Combo- etc");
        VBox collectorVBox = new VBox();
        HBox choiceBoxBox = new HBox();
        choiceBoxBox.setSpacing(10);
        choiceBoxBox.setPadding(new Insets(10));
        ChoiceBox<String> choices = new ChoiceBox<String>(FXCollections.observableArrayList("4", "10", "12"));
        choices.getSelectionModel().selectFirst();
        choiceBoxBox.getChildren().add(choices);
        ChoiceBox<String> choices2 = new ChoiceBox<String>(FXCollections.observableArrayList("A", "B", "C"));
        choices2.getSelectionModel().selectFirst();
        choices2.setDisable(true);
        choiceBoxBox.getChildren().add(choices2);
        collectorVBox.getChildren().add(choiceBoxBox);
        ObservableList<String> items = FXCollections.observableArrayList("A", "B", "C");
        HBox editableComboBoxBox =new HBox();
        editableComboBoxBox.setSpacing(10);
        editableComboBoxBox.setPadding(new Insets(10));
        ComboBox<String> combo1 = new ComboBox<String>(items);
        combo1.setEditable(true);
        editableComboBoxBox.getChildren().add(combo1);
        ComboBox<String> combo2 =  new ComboBox<String>(items);
        combo2.setDisable(true);
        combo2.setEditable(true);
        editableComboBoxBox.getChildren().add(combo2);
        collectorVBox.getChildren().add(editableComboBoxBox);
        HBox comboBoxBox = new HBox();
        comboBoxBox.setSpacing(10);
        comboBoxBox.setPadding(new Insets(10));
        ComboBox<String> combo3 =  new ComboBox<String>(items);
        combo3.setPromptText("test");
        combo3.setEditable(false);
        comboBoxBox.getChildren().add(combo3);
        ComboBox<String> combo4 = new ComboBox<String>(items);
        combo4.setPromptText("test");
        combo4.setEditable(false);
        combo4.setDisable(true);
        comboBoxBox.getChildren().add(combo4);
        collectorVBox.getChildren().add(comboBoxBox);

        HBox colorPickerBox = new HBox();
        colorPickerBox.setSpacing(10);
        colorPickerBox.setPadding(new Insets(10));
        ColorPicker color = new ColorPicker(Color.rgb(194, 222, 254));
        colorPickerBox.getChildren().add(color);
        ColorPicker color2 = new ColorPicker(Color.rgb(194, 222, 254));
        color2.getStyleClass().add("button");
        colorPickerBox.getChildren().add(color2);
        ColorPicker color3 = new ColorPicker(Color.rgb(194, 222, 254));
        color3.getStyleClass().add("split-button");
        colorPickerBox.getChildren().add(color3);
        collectorVBox.getChildren().add(colorPickerBox);
        tabChoiceBox.setContent(collectorVBox);
        tabPane.getTabs().add(tabChoiceBox);

        Tab tabHTMLBox = new Tab();
        tabHTMLBox.setText("HTML");
        VBox htmlbox = new VBox();
        htmlbox.setPadding(new Insets(5));
        HTMLEditor htmlEditor = new HTMLEditor();
        htmlEditor.setPrefHeight(200);
        htmlEditor.setPrefWidth(300);
        
        htmlbox.getChildren().add(htmlEditor);
        tabHTMLBox.setContent(htmlbox);
        tabPane.getTabs().add(tabHTMLBox);

        Tab tabSliderBox = new Tab();
        tabSliderBox.setText("Sliders");
        HBox slidersBox = new HBox();
        slidersBox.setSpacing(10);
        slidersBox.setPadding(new Insets(10));
        Slider vSlider = new Slider();
        vSlider.setOrientation(Orientation.VERTICAL);
        slidersBox.getChildren().add(vSlider);
        Slider vTickSlider = new Slider();
        vTickSlider.setMin(0);
        vTickSlider.setMax(100);
        vTickSlider.setValue(40);
        vTickSlider.setShowTickLabels(true);
        vTickSlider.setShowTickMarks(true);
        vTickSlider.setMajorTickUnit(50);
        vTickSlider.setMinorTickCount(4);
        vTickSlider.setBlockIncrement(10);
        vTickSlider.setOrientation(Orientation.VERTICAL);
        slidersBox.getChildren().add(vTickSlider);
        VBox horizontalSliderBox = new VBox();
        horizontalSliderBox.setSpacing(10);
        horizontalSliderBox.setPadding(new Insets(10));
        Slider simpleSlider = new Slider();
        horizontalSliderBox.getChildren().add(simpleSlider);
        Slider slider = new Slider();
        slider.setMin(0);
        slider.setMax(100);
        slider.setValue(40);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(50);
        slider.setMinorTickCount(4);
        slider.setBlockIncrement(10);
        horizontalSliderBox.getChildren().add(slider);
        Slider simpleDisabledSlider = new Slider();
        simpleDisabledSlider.setDisable(true);
        horizontalSliderBox.getChildren().add(simpleDisabledSlider);
        Slider disabledSlider = new Slider();
        disabledSlider.setMin(0);
        disabledSlider.setMax(100);
        disabledSlider.setValue(40);
        disabledSlider.setShowTickLabels(true);
        disabledSlider.setShowTickMarks(true);
        disabledSlider.setMajorTickUnit(50);
        disabledSlider.setMinorTickCount(4);
        disabledSlider.setBlockIncrement(10);
        disabledSlider.setDisable(true);
        horizontalSliderBox.getChildren().add(disabledSlider);
        slidersBox.getChildren().add(horizontalSliderBox);
        tabSliderBox.setContent(slidersBox);
        tabPane.getTabs().add(tabSliderBox);

        Tab tabTableBox = new Tab();
        tabTableBox.setText("Table");
        // Create a table..
        HBox tableContainer = new HBox();
        tableContainer.setPadding(new Insets(10));
        TableView<Person> table = new TableView<Person>();
        table.setPrefHeight(250);
        table.setPrefWidth(650);
        table.setEditable(true);
        // table.getSelectionModel().setCellSelectionEnabled(true) ;

        TableColumn<Person, String> firstNameCol = new TableColumn<Person, String>("First Name");
        // firstNameCol.setMinWidth(100);
        firstNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("firstName"));
        TableColumn lastNameCol = new TableColumn("Last Name");
        lastNameCol.setEditable(true);
        lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameCol.setOnEditCommit(new EventHandler<CellEditEvent<Person, String>>() {
            @Override public void handle(CellEditEvent<Person, String> t) {
                ((Person) t.getTableView().getItems().get(t.getTablePosition().getRow())).setLastName(t.getNewValue());
            }
        });
        lastNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("lastName"));
        // TableColumn emailCol = new TableColumn("Email");
        TableColumn<Person, String> firstEmailCol = new TableColumn<Person, String>("Primary");
        // firstEmailCol.setMinWidth(200);
        firstEmailCol.setCellValueFactory(new PropertyValueFactory<Person, String>("primaryEmail"));
        TableColumn<Person, String> secondEmailCol = new TableColumn<Person, String>("Secondary");
        // secondEmailCol.setMinWidth(200);
        secondEmailCol.setCellValueFactory(new PropertyValueFactory<Person, String>("secondaryEmail"));
        // emailCol.getColumns().addAll(firstEmailCol, secondEmailCol);
        TableColumn<Person, Boolean> vipCol = new TableColumn<Person, Boolean>("VIP");
        vipCol.setEditable(true);
        vipCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Person, Boolean>, ObservableValue<Boolean>>() {

            @Override public ObservableValue<Boolean> call(
                    javafx.scene.control.TableColumn.CellDataFeatures<Person, Boolean> param) {
                return new ReadOnlyBooleanWrapper(param.getValue().getVip());
            }
        });
        vipCol.setCellFactory(CheckBoxTableCell.forTableColumn(vipCol));
        vipCol.setOnEditCommit(new EventHandler<CellEditEvent<Person, Boolean>>() {
            @Override public void handle(CellEditEvent<Person, Boolean> t) {
                ((Person) t.getTableView().getItems().get(t.getTablePosition().getRow())).setVip(t.getNewValue());
            }
        });
        table.getColumns().addAll(firstNameCol, lastNameCol, firstEmailCol, secondEmailCol, vipCol);
        table.setItems(data);
        table.setTableMenuButtonVisible(true);
        tableContainer.getChildren().add(table);
        tabTableBox.setContent(tableContainer);
        tabPane.getTabs().add(tabTableBox);

        Tab tabTreeBox = new Tab();
        tabTreeBox.setText("Tree");
        HBox treeContainer = new HBox();
        treeContainer.setPadding(new Insets(10));
        TreeItem<String> rootItem = new TreeItem<String>("People");
        rootItem.setExpanded(true);
        for (Person person : data) {
            TreeItem<String> personLeaf = new TreeItem<String>(person.getFirstName());
            boolean found = false;
            for (TreeItem<String> statusNode : rootItem.getChildren()) {
                if (statusNode.getValue().equals((!person.getVip() ? "no " : "") + "VIP")) {
                    statusNode.getChildren().add(personLeaf);
                    found = true;
                    break;
                }
            }
            if (!found) {
                TreeItem<String> statusNode = new TreeItem<String>((!person.getVip() ? "no " : "") + "VIP");
                rootItem.getChildren().add(statusNode);
                statusNode.getChildren().add(personLeaf);
            }
        }
        TreeView<String> tree = new TreeView<String>(rootItem);
        tree.setPrefHeight(250);
        tree.setPrefWidth(400);
        treeContainer.getChildren().add(tree);
        tabTreeBox.setContent(treeContainer);
        tabPane.getTabs().add(tabTreeBox);

        Tab tabTreeTableBox = new Tab();
        tabTreeTableBox.setText("TreeTable");
        HBox treeTableContainer =  new HBox();
        treeTableContainer.setPadding(new Insets(10));
        TreeItem<Person> rootTreeTableItem = new TreeItem<Person>(new Person("Chef", "Chef", "chef@business.de", "chef@business.de", true));
        rootTreeTableItem.setExpanded(true);
        for (Person person : data) {
            TreeItem<Person> personLeaf = new TreeItem<Person>(person);
            boolean found = false;
            for (TreeItem<Person> statusNode : rootTreeTableItem.getChildren()) {
                if (statusNode.getValue().getVip() == person.getVip()) {
                    statusNode.getChildren().add(personLeaf);
                    found = true;
                    break;
                }
            }
            if (!found) {
                TreeItem<Person> statusNode = new TreeItem<Person>(person);
                rootTreeTableItem.getChildren().add(statusNode);
                statusNode.getChildren().add(personLeaf);
            }
        }
        TreeTableView<Person> treeTable = new TreeTableView<Person>(rootTreeTableItem);
        TreeTableColumn<Person, String> firstNameTreeCol = new TreeTableColumn<Person, String>("First Name");
        firstNameTreeCol.setPrefWidth(100);
        firstNameTreeCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Person, String>, ObservableValue<String>>() {

            @Override public ObservableValue<String> call(CellDataFeatures<Person, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getValue().getFirstName());
            }
        });

        TreeTableColumn<Person, String> lastNameTreeCol = new TreeTableColumn<Person, String>("Last Name");
        lastNameTreeCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Person, String>, ObservableValue<String>>() {

            @Override public ObservableValue<String> call(CellDataFeatures<Person, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getValue().getLastName());
            }
        });
        TreeTableColumn<Person, String> primaryMailCol = new TreeTableColumn<Person, String>("primary Mail");
        primaryMailCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Person, String>, ObservableValue<String>>() {

            @Override public ObservableValue<String> call(CellDataFeatures<Person, String> param) {
                return new ReadOnlyStringWrapper(param.getValue().getValue().getPrimaryEmail());
            }
        });
        TreeTableColumn<Person, Boolean> vipTreeTableCol = new TreeTableColumn<Person, Boolean>("VIP");
        vipTreeTableCol.setCellFactory(CheckBoxTreeTableCell.forTreeTableColumn(vipTreeTableCol));
        vipTreeTableCol.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Person, Boolean>, ObservableValue<Boolean>>() {

            @Override public ObservableValue<Boolean> call(CellDataFeatures<Person, Boolean> param) {
                return new ReadOnlyBooleanWrapper(param.getValue().getValue().getVip());
            }
        });
        treeTable.getColumns().setAll(firstNameTreeCol, lastNameTreeCol, primaryMailCol, vipTreeTableCol);
        treeTable.setPrefHeight(250);
        treeTable.setPrefWidth(600);
        treeTableContainer.getChildren().add(treeTable);
        tabTreeTableBox.setContent(treeTableContainer);
        tabPane.getTabs().add(tabTreeTableBox);

        Tab tabListBox = new Tab();
        tabListBox.setText("List");
        HBox listContainer =  new HBox();
        listContainer.setSpacing(10);
        listContainer.setPadding(new Insets(10));
        ListView<String> list = new ListView<String>();
        ObservableList<String> listItems = FXCollections.observableArrayList("Item 1", "Item 2", "Item 3", "Item 4");
        list.setItems(listItems);
        list.setPrefWidth(150);
        list.setPrefHeight(70);
        listContainer.getChildren().add(list);
        TableView<Person> listTable = new TableView<Person>();
        listTable.getStyleClass().add("hide-header");
        listTable.setPrefHeight(250);
        listTable.setPrefWidth(150);
        TableColumn<Person, String> firstNameListCol = new TableColumn<Person, String>("First Name");
        firstNameListCol.setMinWidth(100);
        firstNameListCol.setCellValueFactory(new PropertyValueFactory<Person, String>("firstName"));
        listTable.getColumns().add(firstNameListCol);
        listTable.setItems(data);
        listTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        listContainer.getChildren().add(listTable);
        ListView<String> horizontalList = new ListView<String>();
        horizontalList.setItems(listItems);
        horizontalList.setPrefWidth(150);
        horizontalList.setPrefHeight(50);
        horizontalList.setOrientation(Orientation.HORIZONTAL);
        listContainer.getChildren().add(horizontalList);

        tabListBox.setContent(listContainer);
        tabPane.getTabs().add(tabListBox);
        tabPane.getSelectionModel().select(tabListBox);

        pane.setBottom(tabPane);
        Scene myScene = new Scene(pane, 700, 600);

        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        menuFile.getItems().addAll(new MenuItem("New"), new MenuItem("Open File..."));
        Menu menuEdit = new Menu("Edit");
        menuEdit.getItems().addAll(new MenuItem("Undo"), new MenuItem("Redo"));
        Menu menuView = new Menu("View");
        menuView.getItems().addAll(new MenuItem("Zoom In"), new MenuItem("Zoom Out"));
        menuBar.getMenus().addAll(menuFile, menuEdit, menuView);
        pane.getChildren().add(menuBar);

        AquaFx.style();
        stage.setTitle("AquaFX");
        stage.setScene(myScene);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
