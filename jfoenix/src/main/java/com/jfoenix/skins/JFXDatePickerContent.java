/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.jfoenix.skins;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.ChronoUnit.YEARS;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DecimalStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.transitions.CachedTransition;

import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

/**
 * @author Shadi Shaheen
 */
public class JFXDatePickerContent extends VBox {

    private static final String SPINNER_LABEL = "spinner-label";
    private static final String ROBOTO = "Roboto";
    private static final Color DEFAULT_CELL_COLOR = Color.valueOf("#9C9C9C");
    private static final Color DEFAULT_COLOR = Color.valueOf("#313131");

    protected JFXDatePicker datePicker;
    private JFXButton backMonthButton;
    private JFXButton forwardMonthButton;
    private ObjectProperty<JFXListCell> selectedYearCell = new SimpleObjectProperty<>(null);
    private Label selectedDateLabel;
    private Label selectedYearLabel;
    private Label monthYearLabel;
    protected GridPane contentGrid;
    private StackPane calendarPlaceHolder = new StackPane();

    // animation
    private CachedTransition showTransition;
    private CachedTransition hideTransition;
    private ParallelTransition tempImageTransition;

    private int daysPerWeek = 7;
    private List<DateCell> weekDaysCells = new ArrayList<>();
    private List<DateCell> weekNumberCells = new ArrayList<>();
    protected List<DateCell> dayCells = new ArrayList<>();
    private LocalDate[] dayCellDates;
    private DateCell currentFocusedDayCell = null;

    private ListView<String> yearsListView = new JFXListView<String>() {
        {
            this.getStyleClass().setAll("date-picker-list-view");
            this.setCellFactory(listView -> new JFXListCell<String>() {
                boolean mousePressed = false;

                {
                    this.getStyleClass().setAll("data-picker-list-cell");
                    setOnMousePressed(click -> mousePressed = true);
                    setOnMouseEntered(enter -> {
                        if (!mousePressed) {
                            setBackground(new Background(new BackgroundFill(Color.valueOf("#EDEDED"),
                                CornerRadii.EMPTY,
                                Insets.EMPTY)));
                        }
                    });
                    setOnMouseExited(enter -> {
                        if (!mousePressed) {
                            setBackground(new Background(new BackgroundFill(Color.WHITE,
                                CornerRadii.EMPTY,
                                Insets.EMPTY)));
                        }
                    });
                    setOnMouseReleased(release -> {
                        if (mousePressed) {
                            setBackground(new Background(new BackgroundFill(Color.WHITE,
                                CornerRadii.EMPTY,
                                Insets.EMPTY)));
                        }
                        mousePressed = false;
                    });
                    setOnMouseClicked(click -> {
                        String selectedItem = yearsListView.getSelectionModel().getSelectedItem();
                        if (selectedItem != null && selectedItem.equals(getText())) {
                            int offset = Integer.parseInt(getText()) - Integer.parseInt(
                                selectedYearLabel.getText());
                            forward(offset, YEARS, false, false);
                            hideTransition.setOnFinished(finish -> {
                                selectedYearCell.set(this);
                                yearsListView.scrollTo(this.getIndex() - 2 >= 0 ? this.getIndex() - 2 : this.getIndex());
                                hideTransition.setOnFinished(null);
                            });
                            hideTransition.play();
                        }
                    });
                    selectedYearLabel.textProperty().addListener((o, oldVal, newVal) -> {
                        if (!yearsListView.isVisible() && newVal.equals(getText())) {
                            selectedYearCell.set(this);
                        }
                    });
                }

                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        cellRippler.setRipplerFill(Color.GREY);
                        setAlignment(Pos.CENTER);
                        if (!item.equals(selectedYearLabel.getText())) {
                            // default style for each cell
                            setStyle("-fx-font-size: 16; -fx-font-weight: NORMAL;");
                            setTextFill(DEFAULT_COLOR);
                        } else {
                            selectedYearCell.set(this);
                        }
                        setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                    }
                }
            });
        }
    };

    // Date formatters
    final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM");
    final DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("y");
    final DateTimeFormatter weekNumberFormatter = DateTimeFormatter.ofPattern("w");
    final DateTimeFormatter weekDayNameFormatter = DateTimeFormatter.ofPattern("ccc");
    final DateTimeFormatter dayCellFormatter = DateTimeFormatter.ofPattern("d");

    private ObjectProperty<YearMonth> selectedYearMonth = new SimpleObjectProperty<>(this, "selectedYearMonth");


    JFXDatePickerContent(final DatePicker datePicker) {
        this.datePicker = (JFXDatePicker) datePicker;
        getStyleClass().add("date-picker-popup");

        LocalDate date = datePicker.getValue();
        selectedYearMonth.set((date != null) ? YearMonth.from(date) : YearMonth.now());
        selectedYearMonth.addListener((observable, oldValue, newValue) -> updateValues());

        // add change listener to change the color of the selected year cell
        selectedYearCell.addListener((o, oldVal, newVal) -> {
            if (oldVal != null) {
                oldVal.setStyle("-fx-font-size: 16; -fx-font-weight: NORMAL;");
                oldVal.setTextFill(DEFAULT_COLOR);
            }
            if (newVal != null) {
                newVal.setStyle("-fx-font-size: 24; -fx-font-weight: BOLD;");
                newVal.setTextFill(this.datePicker.getDefaultColor());
            }
        });

        // create the header pane
        getChildren().add(createHeaderPane());

        contentGrid = new GridPane() {
            @Override
            protected double computePrefWidth(double height) {
                final int nCols = daysPerWeek + (datePicker.isShowWeekNumbers() ? 1 : 0);
                final double leftSpace = snapSpace(getInsets().getLeft());
                final double rightSpace = snapSpace(getInsets().getRight());
                final double hgaps = snapSpace(getHgap()) * (nCols - 1);
                // compute content width
                final double contentWidth = super.computePrefWidth(height) - leftSpace - rightSpace - hgaps;
                return ((snapSize(contentWidth / nCols)) * nCols) + leftSpace + rightSpace + hgaps;
            }

            @Override
            protected void layoutChildren() {
                if (getWidth() > 0 && getHeight() > 0) {
                    super.layoutChildren();
                }
            }
        };
        contentGrid.setFocusTraversable(true);
        contentGrid.getStyleClass().add("calendar-grid");
        contentGrid.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT,
            CornerRadii.EMPTY,
            Insets.EMPTY)));
        contentGrid.setPadding(new Insets(0, 12, 12, 12));
        contentGrid.setVgap(0);
        contentGrid.setHgap(0);

        // create week days cells
        createWeekDaysCells();
        // create month days cells
        createDayCells();

        VBox contentHolder = new VBox();
        // create content pane
        contentHolder.getChildren().setAll(createCalendarMonthLabelPane(), contentGrid);
        // add month arrows pane
        calendarPlaceHolder.getChildren().setAll(contentHolder, createCalendarArrowsPane());

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(calendarPlaceHolder.widthProperty());
        clip.heightProperty().bind(calendarPlaceHolder.heightProperty());
        calendarPlaceHolder.setClip(clip);

        // create years list view
        for (int i = 0; i <= 200; i++) {
            yearsListView.getItems().add(Integer.toString(1900 + i));
        }
        yearsListView.setVisible(false);
        yearsListView.setOpacity(0);
        yearsListView.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT,
            CornerRadii.EMPTY,
            Insets.EMPTY)));

        StackPane contentPlaceHolder = new StackPane();
        yearsListView.maxWidthProperty().bind(contentPlaceHolder.widthProperty());
        yearsListView.maxHeightProperty().bind(contentPlaceHolder.heightProperty());
        contentPlaceHolder.getChildren().setAll(calendarPlaceHolder, yearsListView);
        getChildren().add(contentPlaceHolder);

        refresh();

        addEventHandler(KeyEvent.ANY, event -> {
            Node node = getScene().getFocusOwner();
            if (node instanceof DateCell) {
                currentFocusedDayCell = (DateCell) node;
            }

            switch (event.getCode()) {
                case HOME:
                    // go to the current date
                    init();
                    goToDate(LocalDate.now(), true);
                    event.consume();
                    break;
                case PAGE_UP:
                    if (!backMonthButton.isDisabled()) {
                        forward(-1, MONTHS, true, true);
                    }
                    event.consume();
                    break;
                case PAGE_DOWN:
                    if (!forwardMonthButton.isDisabled()) {
                        forward(1, MONTHS, true, true);
                    }
                    event.consume();
                    break;
                case ESCAPE:
                    datePicker.hide();
                    event.consume();
                    break;
                case F4:
                case F10:
                case UP:
                case DOWN:
                case LEFT:
                case RIGHT:
                case TAB:
                    break;
                default:
                    event.consume();
            }
        });

        // create animation
        showTransition = new CachedTransition(yearsListView,
            new Timeline(
                new KeyFrame(Duration.millis(0),
                    new KeyValue(yearsListView.opacityProperty(),
                        0,
                        Interpolator.EASE_BOTH),
                    new KeyValue(calendarPlaceHolder.opacityProperty(),
                        1,
                        Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(500),
                    new KeyValue(yearsListView.opacityProperty(),
                        0,
                        Interpolator.EASE_BOTH),
                    new KeyValue(calendarPlaceHolder.opacityProperty(),
                        0,
                        Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(1000),
                    new KeyValue(yearsListView.opacityProperty(),
                        1,
                        Interpolator.EASE_BOTH),
                    new KeyValue(calendarPlaceHolder.opacityProperty(),
                        0,
                        Interpolator.EASE_BOTH),
                    new KeyValue(selectedYearLabel.textFillProperty(),
                        Color.WHITE,
                        Interpolator.EASE_BOTH),
                    new KeyValue(selectedDateLabel.textFillProperty(),
                        Color.rgb(255, 255, 255, 0.67),
                        Interpolator.EASE_BOTH)))) {
            {
                setCycleDuration(Duration.millis(320));
                setDelay(Duration.seconds(0));
            }

            @Override
            protected void starting() {
                super.starting();
                yearsListView.setVisible(true);
            }
        };

        hideTransition = new CachedTransition(yearsListView,
            new Timeline(
                new KeyFrame(Duration.millis(0),
                    new KeyValue(yearsListView.opacityProperty(),
                        1,
                        Interpolator.EASE_BOTH),
                    new KeyValue(calendarPlaceHolder.opacityProperty(),
                        0,
                        Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(500),
                    new KeyValue(yearsListView.opacityProperty(),
                        0,
                        Interpolator.EASE_BOTH),
                    new KeyValue(calendarPlaceHolder.opacityProperty(),
                        0,
                        Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.millis(1000),
                    new KeyValue(yearsListView.opacityProperty(),
                        0,
                        Interpolator.EASE_BOTH),
                    new KeyValue(calendarPlaceHolder.opacityProperty(),
                        1,
                        Interpolator.EASE_BOTH),
                    new KeyValue(selectedDateLabel.textFillProperty(),
                        Color.WHITE,
                        Interpolator.EASE_BOTH),
                    new KeyValue(selectedYearLabel.textFillProperty(),
                        Color.rgb(255, 255, 255, 0.67),
                        Interpolator.EASE_BOTH)))) {
            {
                setCycleDuration(Duration.millis(320));
                setDelay(Duration.seconds(0));
            }

            @Override
            protected void stopping() {
                super.stopping();
                yearsListView.setVisible(false);
            }
        };
    }

    @Override
    public String getUserAgentStylesheet() {
        return getClass().getResource("/css/controls/jfx-date-picker.css").toExternalForm();
    }

    ObjectProperty<YearMonth> displayedYearMonthProperty() {
        return selectedYearMonth;
    }

    private void createWeekDaysCells() {
        // create week days names
        for (int i = 0; i < daysPerWeek; i++) {
            DateCell cell = new DateCell();
            cell.getStyleClass().add("day-name-cell");
            cell.setTextFill(DEFAULT_CELL_COLOR);
            cell.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
            cell.setFont(Font.font(ROBOTO, FontWeight.BOLD, 12));
            cell.setAlignment(Pos.BASELINE_CENTER);
            weekDaysCells.add(cell);
        }
        // create week days numbers
        for (int i = 0; i < 6; i++) {
            DateCell cell = new DateCell();
            cell.getStyleClass().add("week-number-cell");
            cell.setTextFill(DEFAULT_CELL_COLOR);
            cell.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
            cell.setFont(Font.font(ROBOTO, FontWeight.BOLD, 12));
            weekNumberCells.add(cell);
        }
    }

    /*
     * header panel represents the selected Date
     * we keep javaFX original style classes
     */
    protected VBox createHeaderPane() {

        // Year label
        selectedYearLabel = new Label();
        selectedYearLabel.getStyleClass().add(SPINNER_LABEL);
        selectedYearLabel.setTextFill(Color.rgb(255, 255, 255, 0.67));
        selectedYearLabel.setFont(Font.font(ROBOTO, FontWeight.BOLD, 14));
        // Year label container
        HBox yearLabelContainer = new HBox();
        yearLabelContainer.getStyleClass().add("spinner");
        yearLabelContainer.getChildren().addAll(selectedYearLabel);
        yearLabelContainer.setAlignment(Pos.CENTER_LEFT);
        yearLabelContainer.setFillHeight(false);
        yearLabelContainer.setOnMouseClicked((click) -> {
            if (!yearsListView.isVisible()) {
                int yearIndex = Integer.parseInt(selectedYearLabel.getText()) - 1900 - 2;
                yearsListView.scrollTo(yearIndex >= 0 ? yearIndex : yearIndex + 2);
                hideTransition.stop();
                showTransition.play();
            }
        });

        // selected date label
        selectedDateLabel = new Label();
        selectedDateLabel.getStyleClass().add(SPINNER_LABEL);
        selectedDateLabel.setTextFill(Color.WHITE);
        selectedDateLabel.setFont(Font.font(ROBOTO, FontWeight.BOLD, 32));
        // selected date label container
        HBox selectedDateContainer = new HBox(selectedDateLabel);
        selectedDateContainer.getStyleClass().add("spinner");
        selectedDateContainer.setAlignment(Pos.CENTER_LEFT);
        selectedDateContainer.setOnMouseClicked((click) -> {
            if (yearsListView.isVisible()) {
                showTransition.stop();
                hideTransition.play();
            }
        });

        VBox headerPanel = new VBox();
        headerPanel.getStyleClass().add("month-year-pane");
        headerPanel.setBackground(new Background(new BackgroundFill(this.datePicker.getDefaultColor(),
            CornerRadii.EMPTY,
            Insets.EMPTY)));
        headerPanel.setPadding(new Insets(12, 24, 12, 24));
        headerPanel.getChildren().add(yearLabelContainer);
        headerPanel.getChildren().add(selectedDateContainer);
        return headerPanel;
    }

    /*
     * methods to create the content of the date picker
     */
    protected BorderPane createCalendarArrowsPane() {

        SVGGlyph leftChevron = new SVGGlyph(0,
            "CHEVRON_LEFT",
            "M 742,-37 90,614 Q 53,651 53,704.5 53,758 90,795 l 652,651 q 37,37 90.5,37 53.5,0 90.5,-37 l 75,-75 q 37,-37 37,-90.5 0,-53.5 -37,-90.5 L 512,704 998,219 q 37,-38 37,-91 0,-53 -37,-90 L 923,-37 Q 886,-74 832.5,-74 779,-74 742,-37 z",
            Color.GRAY);
        SVGGlyph rightChevron = new SVGGlyph(0,
            "CHEVRON_RIGHT",
            "m 1099,704 q 0,-52 -37,-91 L 410,-38 q -37,-37 -90,-37 -53,0 -90,37 l -76,75 q -37,39 -37,91 0,53 37,90 l 486,486 -486,485 q -37,39 -37,91 0,53 37,90 l 76,75 q 36,38 90,38 54,0 90,-38 l 652,-651 q 37,-37 37,-90 z",
            Color.GRAY);
        leftChevron.setFill(DEFAULT_COLOR);
        leftChevron.setSize(6, 11);
        rightChevron.setFill(DEFAULT_COLOR);
        rightChevron.setSize(6, 11);

        backMonthButton = new JFXButton();
        backMonthButton.setMinSize(40, 40);
        backMonthButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT,
            new CornerRadii(40),
            Insets.EMPTY)));
        backMonthButton.getStyleClass().add("left-button");
        backMonthButton.setGraphic(leftChevron);
        backMonthButton.setRipplerFill(this.datePicker.getDefaultColor());
        backMonthButton.setOnAction(t -> forward(-1, MONTHS, false, true));

        forwardMonthButton = new JFXButton();
        forwardMonthButton.setMinSize(40, 40);
        forwardMonthButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT,
            new CornerRadii(40),
            Insets.EMPTY)));
        forwardMonthButton.getStyleClass().add("right-button");
        forwardMonthButton.setGraphic(rightChevron);
        forwardMonthButton.setRipplerFill(this.datePicker.getDefaultColor());
        forwardMonthButton.setOnAction(t -> forward(1, MONTHS, false, true));

        BorderPane arrowsContainer = new BorderPane();
        arrowsContainer.setLeft(backMonthButton);
        arrowsContainer.setRight(forwardMonthButton);
        arrowsContainer.setPadding(new Insets(4, 12, 2, 12));
        arrowsContainer.setPickOnBounds(false);
        return arrowsContainer;
    }

    protected BorderPane createCalendarMonthLabelPane() {
        monthYearLabel = new Label();
        monthYearLabel.getStyleClass().add(SPINNER_LABEL);
        monthYearLabel.setFont(Font.font(ROBOTO, FontWeight.BOLD, 13));
        monthYearLabel.setTextFill(DEFAULT_COLOR);

        BorderPane monthContainer = new BorderPane();
        monthContainer.setMinHeight(50);
        monthContainer.setCenter(monthYearLabel);
        monthContainer.setPadding(new Insets(2, 12, 2, 12));
        return monthContainer;
    }

    void updateContentGrid() {
        contentGrid.getColumnConstraints().clear();
        contentGrid.getChildren().clear();
        int colsNumber = daysPerWeek + (datePicker.isShowWeekNumbers() ? 1 : 0);
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(100);
        for (int i = 0; i < colsNumber; i++) {
            contentGrid.getColumnConstraints().add(columnConstraints);
        }

        // Week days cells
        for (int i = 0; i < daysPerWeek; i++) {
            contentGrid.add(weekDaysCells.get(i), i + colsNumber - daysPerWeek, 1);
        }

        // Week number cells
        if (datePicker.isShowWeekNumbers()) {
            for (int i = 0; i < 6; i++) {
                contentGrid.add(weekNumberCells.get(i), 0, i + 2);
            }
        }

        // Month days cells
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < daysPerWeek; col++) {
                contentGrid.add(dayCells.get(row * daysPerWeek + col), col + colsNumber - daysPerWeek, row + 2);
            }
        }
    }

    private void refresh() {
        updateDayNameCells();
        updateValues();
    }

    private void updateDayNameCells() {
        int weekFirstDay = WeekFields.of(getLocale()).getFirstDayOfWeek().getValue();
        LocalDate date = LocalDate.of(2009, 7, 12 + weekFirstDay);
        for (int i = 0; i < daysPerWeek; i++) {
            String name = weekDayNameFormatter.withLocale(getLocale()).format(date.plus(i, DAYS));
            // Fix Chinese environment week display incorrectly
            if (weekDayNameFormatter.getLocale() == java.util.Locale.CHINA) {
                name = name.substring(2, 3).toUpperCase();
            } else {
                name = name.substring(0, 1).toUpperCase();
            }
            weekDaysCells.get(i).setText(name);
        }
    }

    void updateValues() {
        updateWeekNumberDateCells();
        updateDayCells();
        updateMonthYearPane();
    }

    void updateWeekNumberDateCells() {
        if (datePicker.isShowWeekNumbers()) {
            final Locale locale = getLocale();
            LocalDate firstDayOfMonth = selectedYearMonth.get().atDay(1);
            for (int i = 0; i < 6; i++) {
                LocalDate date = firstDayOfMonth.plus(i, WEEKS);
                String weekNumber = weekNumberFormatter.withLocale(locale)
                    .withDecimalStyle(DecimalStyle.of(locale))
                    .format(date);
                weekNumberCells.get(i).setText(weekNumber);
            }
        }
    }

    private void updateDayCells() {
        Locale locale = getLocale();
        Chronology chrono = getPrimaryChronology();
        // get the index of the first day of the month
        int firstDayOfWeek = WeekFields.of(getLocale()).getFirstDayOfWeek().getValue();
        int firstOfMonthIndex = selectedYearMonth.get().atDay(1).getDayOfWeek().getValue() - firstDayOfWeek;
        firstOfMonthIndex += firstOfMonthIndex < 0 ? daysPerWeek : 0;
        YearMonth currentYearMonth = selectedYearMonth.get();

        int daysInCurMonth = -1;

        for (int i = 0; i < 6 * daysPerWeek; i++) {
            DateCell dayCell = dayCells.get(i);
            dayCell.getStyleClass().setAll("cell", "date-cell", "day-cell");
            dayCell.setPrefSize(40, 42);
            dayCell.setDisable(false);
            dayCell.setStyle(null);
            dayCell.setGraphic(null);
            dayCell.setTooltip(null);
            dayCell.setTextFill(DEFAULT_COLOR);
            dayCell.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT,
                CornerRadii.EMPTY,
                Insets.EMPTY)));

            try {
                if (daysInCurMonth == -1) {
                    daysInCurMonth = currentYearMonth.lengthOfMonth();
                }

                int dayIndex = i - firstOfMonthIndex + 1;

                LocalDate date = currentYearMonth.atDay(dayIndex);
                dayCellDates[i] = date;

                // if it's today
                if (date.equals(LocalDate.now())) {
                    dayCell.setTextFill(this.datePicker.getDefaultColor());
                    dayCell.getStyleClass().add("today");
                }
                // if it's the current selected value
                if (date.equals(datePicker.getValue())) {
                    dayCell.getStyleClass().add("selected");
                    dayCell.setTextFill(Color.WHITE);
                    dayCell.setBackground(
                        new Background(new BackgroundFill(this.datePicker.getDefaultColor(),
                            new CornerRadii(40),
                            Insets.EMPTY)));
                }

                ChronoLocalDate cDate = chrono.date(date);
                String cellText = dayCellFormatter.withLocale(locale)
                    .withChronology(chrono)
                    .withDecimalStyle(DecimalStyle.of(locale))
                    .format(cDate);
                dayCell.setText(cellText);
                if (i < firstOfMonthIndex) {
                    dayCell.getStyleClass().add("previous-month");
                    dayCell.setText("");
                } else if (i >= firstOfMonthIndex + daysInCurMonth) {
                    dayCell.getStyleClass().add("next-month");
                    dayCell.setText("");
                }
                // update cell item
                dayCell.updateItem(date, false);
            } catch (DateTimeException ex) {
                // Disable day cell if its date is out of range
                dayCell.setText("");
                dayCell.setDisable(true);
            }
        }
    }

    protected void updateMonthYearPane() {
        // update date labels
        YearMonth yearMonth = selectedYearMonth.get();
        LocalDate value = datePicker.getValue();
        value = value == null ? LocalDate.now() : value;
        selectedDateLabel.setText(DateTimeFormatter.ofPattern("EEE, MMM dd").format(value));

        selectedYearLabel.setText(formatYear(yearMonth));
        monthYearLabel.setText(formatMonth(yearMonth) + " " + formatYear(yearMonth));

        Chronology chrono = datePicker.getChronology();
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        backMonthButton.setDisable(!isValidDate(chrono, firstDayOfMonth, -1, DAYS));
        forwardMonthButton.setDisable(!isValidDate(chrono, firstDayOfMonth, +1, MONTHS));
    }

    private String formatMonth(YearMonth yearMonth) {
        try {
            Chronology chrono = getPrimaryChronology();
            ChronoLocalDate cDate = chrono.date(yearMonth.atDay(1));
            return monthFormatter.withLocale(getLocale()).withChronology(chrono).format(cDate);
        } catch (DateTimeException ex) {
            // Date is out of range.
            return "";
        }
    }

    private String formatYear(YearMonth yearMonth) {
        try {
            Chronology chrono = getPrimaryChronology();
            ChronoLocalDate cDate = chrono.date(yearMonth.atDay(1));
            return yearFormatter.withLocale(getLocale())
                .withChronology(chrono)
                .withDecimalStyle(DecimalStyle.of(getLocale()))
                .format(cDate);
        } catch (DateTimeException ex) {
            // Date is out of range.
            return "";
        }
    }

    protected LocalDate dayCellDate(DateCell dateCell) {
        assert dayCellDates != null;
        return dayCellDates[dayCells.indexOf(dateCell)];
    }

    protected void forward(int offset, ChronoUnit unit, boolean focusDayCell, boolean withAnimation) {
        if (withAnimation) {
            if (tempImageTransition == null || tempImageTransition.getStatus() == Status.STOPPED) {
                Pane monthContent = (Pane) calendarPlaceHolder.getChildren().get(0);
                this.getParent().setManaged(false);
                SnapshotParameters snapShotparams = new SnapshotParameters();
                snapShotparams.setFill(Color.TRANSPARENT);
                WritableImage temp = monthContent.snapshot(snapShotparams,
                    new WritableImage((int) monthContent.getWidth(),
                        (int) monthContent.getHeight()));
                ImageView tempImage = new ImageView(temp);
                calendarPlaceHolder.getChildren().add(calendarPlaceHolder.getChildren().size() - 2, tempImage);
                TranslateTransition imageTransition = new TranslateTransition(Duration.millis(160), tempImage);
                imageTransition.setToX(-offset * calendarPlaceHolder.getWidth());
                imageTransition.setOnFinished((finish) -> calendarPlaceHolder.getChildren().remove(tempImage));
                monthContent.setTranslateX(offset * calendarPlaceHolder.getWidth());
                TranslateTransition contentTransition = new TranslateTransition(Duration.millis(160), monthContent);
                contentTransition.setToX(0);

                tempImageTransition = new ParallelTransition(imageTransition, contentTransition);
                tempImageTransition.setOnFinished((finish) -> {
                    calendarPlaceHolder.getChildren().remove(tempImage);
                    this.getParent().setManaged(true);
                });
                tempImageTransition.play();
            }
        }
        YearMonth yearMonth = selectedYearMonth.get();
        DateCell dateCell = currentFocusedDayCell;
        if (dateCell == null || !(dayCellDate(dateCell).getMonth() == yearMonth.getMonth())) {
            dateCell = findDayCellOfDate(yearMonth.atDay(1));
        }
        goToDayCell(dateCell, offset, unit, focusDayCell);
    }

    private void goToDayCell(DateCell dateCell, int offset, ChronoUnit unit, boolean focusDayCell) {
        goToDate(dayCellDate(dateCell).plus(offset, unit), focusDayCell);
    }

    private void goToDate(LocalDate date, boolean focusDayCell) {
        if (isValidDate(datePicker.getChronology(), date)) {
            selectedYearMonth.set(YearMonth.from(date));
            if (focusDayCell) {
                findDayCellOfDate(date).requestFocus();
            }
        }
    }

    private void selectDayCell(DateCell dateCell) {
        datePicker.setValue(dayCellDate(dateCell));
        datePicker.hide();
    }

    private DateCell findDayCellOfDate(LocalDate date) {
        for (int i = 0; i < dayCellDates.length; i++) {
            if (date.equals(dayCellDates[i])) {
                return dayCells.get(i);
            }
        }
        return dayCells.get(dayCells.size() / 2 + 1);
    }

    void init() {
        calendarPlaceHolder.setOpacity(1);
        selectedDateLabel.setTextFill(Color.WHITE);
        selectedYearLabel.setTextFill(Color.rgb(255, 255, 255, 0.67));
        yearsListView.setOpacity(0);
        yearsListView.setVisible(false);
    }

    void clearFocus() {
        LocalDate focusDate = datePicker.getValue();
        if (focusDate == null) {
            focusDate = LocalDate.now();
        }
        if (YearMonth.from(focusDate).equals(selectedYearMonth.get())) {
            goToDate(focusDate, true);
        }
    }

    protected void createDayCells() {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < daysPerWeek; col++) {
                DateCell dayCell = createDayCell();
                dayCell.addEventHandler(MouseEvent.MOUSE_CLICKED, click -> {
                    // allow date selection on mouse primary button click
                    if (click.getButton() != MouseButton.PRIMARY) {
                        return;
                    }
                    DateCell selectedDayCell = (DateCell) click.getSource();
                    selectDayCell(selectedDayCell);
                    currentFocusedDayCell = selectedDayCell;
                });
                // add mouse hover listener
                dayCell.setOnMouseEntered((event) -> {
                    if (!dayCell.getStyleClass().contains("selected")) {
                        dayCell.setBackground(new Background(new BackgroundFill(Color.valueOf("#EDEDED"),
                            new CornerRadii(40),
                            Insets.EMPTY)));
                    }
                });
                dayCell.setOnMouseExited((event) -> {
                    if (!dayCell.getStyleClass().contains("selected")) {
                        dayCell.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT,
                            CornerRadii.EMPTY,
                            Insets.EMPTY)));
                    }
                });
                dayCell.setAlignment(Pos.BASELINE_CENTER);
                dayCell.setBorder(
                    new Border(new BorderStroke(Color.TRANSPARENT,
                        BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY,
                        new BorderWidths(5))));
                dayCell.setFont(Font.font(ROBOTO, FontWeight.BOLD, 12));
                dayCells.add(dayCell);
            }
        }
        dayCellDates = new LocalDate[6 * daysPerWeek];
        // position the cells into the grid
        updateContentGrid();
    }

    private DateCell createDayCell() {
        DateCell dayCell = null;
        // call cell factory if set by the user
        if (datePicker.getDayCellFactory() != null) {
            dayCell = datePicker.getDayCellFactory().call(datePicker);
        }
        // else create the defaul day cell
        if (dayCell == null) {
            dayCell = new DateCell();
        }
        return dayCell;
    }

    /**
     * this method must be overriden when implementing other Chronolgy
     */
    protected Chronology getPrimaryChronology() {
        return datePicker.getChronology();
    }

    protected Locale getLocale() {
        // for android compatibility
        return Locale.getDefault(/*Locale.Category.FORMAT*/);
    }

    protected boolean isValidDate(Chronology chrono, LocalDate date, int offset, ChronoUnit unit) {
        return date != null && isValidDate(chrono, date.plus(offset, unit));
    }

    protected boolean isValidDate(Chronology chrono, LocalDate date) {
        try {
            if (date != null) {
                chrono.date(date);
            }
            return true;
        } catch (DateTimeException ex) {
            return false;
        }
    }
}
