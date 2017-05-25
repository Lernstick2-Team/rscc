package ch.imedias.rsccfx.view;

import ch.imedias.rsccfx.localization.Strings;
import ch.imedias.rsccfx.model.Rscc;
import ch.imedias.rsccfx.view.util.KeyTextField;
import java.util.logging.Logger;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * Defines all elements shown in the support section.
 */
public class RsccSupportView extends BorderPane {
  private static final Logger LOGGER =
      Logger.getLogger(RsccSupportView.class.getName());
  private static final int GRIDPANE_MARGIN = 25;
  final HeaderView headerView;

  final Label titleLbl = new Label();
  final Label descriptionLbl = new Label();
  final Label statusLbl = new Label();
  final Label keyInputStatusLbl = new Label();
  final Label startServiceDescriptionLbl = new Label();
  final Label startServiceTitleLbl = new Label();

  final HBox statusBox = new HBox();
  final HBox keyInputStatusBox = new HBox();

  final KeyTextField keyFld = new KeyTextField();

  final VBox contentBox = new VBox();

  final GridPane keyInputInnerPane = new GridPane();
  final GridPane startServiceInnerPane = new GridPane();

  final TitledPane keyInputTitledPane = new TitledPane();
  final TitledPane startServiceTitledPane = new TitledPane();

  final Button connectBtn = new Button();
  final Button startServiceBtn = new Button();
  final Strings strings = new Strings();
  final Rscc model;
  final WebView validationImgView = new WebView();
  final WebEngine validationImg = validationImgView.getEngine();

  private Pane emptyPane = new Pane();

  /**
   * Initializes all the GUI components needed to enter the key the supporter received.
   *
   * @param model the model to handle the data.
   */
  public RsccSupportView(Rscc model) {
    this.model = model;
    headerView = new HeaderView(model);
    initFieldData();
    layoutForm();
    bindFieldsToModel();
    layoutKeyInputPane();
    layoutStartServicePane();
  }

  private void initFieldData() {
    // populate fields which require initial data
    titleLbl.setText(strings.supportTitleLbl);
    descriptionLbl.setText(strings.supportDescriptionLbl);
    connectBtn.setText(strings.supportConnectBtn);

    startServiceBtn.textProperty().set(strings.startService);
    startServiceDescriptionLbl.textProperty().set(strings.startServiceDescpriptionLbl);
    startServiceTitleLbl.textProperty().set(strings.startService);

    statusLbl.textProperty().set(strings.supportStatusLblWaiting);
    keyInputStatusLbl.textProperty().set(strings.requestStatusLblWaiting);

    keyInputTitledPane.setText(strings.supportKeyInputPane);
    startServiceTitledPane.setText(strings.supportAdressBookPane);
  }

  private void layoutForm() {
    keyInputTitledPane.setExpanded(true);
    keyInputTitledPane.setId("keyInputTitledPane");

    descriptionLbl.getStyleClass().add("descriptionLbl");
    startServiceDescriptionLbl.getStyleClass().add("descriptionLbl");

    startServiceTitledPane.setExpanded(false);
    startServiceTitledPane.setId("startServiceTitledPane");

    titleLbl.getStyleClass().add("titleLbl");

    descriptionLbl.getStyleClass().add("nameLbl");

    statusLbl.getStyleClass().add("statusLbl");
    keyInputStatusLbl.getStyleClass().add("statusLbl");
    statusBox.getChildren().add(statusLbl);
    statusBox.getStyleClass().add("statusBox");
    keyInputStatusBox.getChildren().add(keyInputStatusLbl);
    keyInputStatusBox.getStyleClass().add("statusBox");

    keyFld.getStyleClass().add("keyFld");

    connectBtn.setId("connectBtn");
    connectBtn.setDisable(true);

    startServiceBtn.setId("startServiceBtn");
    startServiceTitleLbl.getStyleClass().add("titleLbl");
    startServiceDescriptionLbl.getStyleClass().add("nameLbl");

    contentBox.getChildren().addAll(keyInputTitledPane, keyInputInnerPane, startServiceTitledPane);
    VBox.setVgrow(keyInputInnerPane, Priority.ALWAYS);
    keyInputInnerPane.getStyleClass().add("contentSupport");
    VBox.setVgrow(startServiceInnerPane, Priority.ALWAYS);
    startServiceInnerPane.getStyleClass().add("contentSupport");

    validationImgView.setBlendMode(BlendMode.DARKEN); // makes background transparent

    setTop(headerView);
    setCenter(contentBox);
  }

  // TODO: Make layoutKeyInputPane same as Request View @martinfrancois @JenniferMue
  private void layoutKeyInputPane() {
    GridPane.setConstraints(keyFld, 0, 1);
    GridPane.setConstraints(validationImgView, 1, 1);
    GridPane.setConstraints(connectBtn, 0, 2);
    GridPane.setConstraints(titleLbl, 2, 0);
    GridPane.setConstraints(descriptionLbl, 2, 1);
    GridPane.setConstraints(keyInputStatusBox, 0, 3);
    GridPane.setColumnSpan(keyInputStatusBox, 3);

    keyInputInnerPane.getChildren().addAll(keyFld, validationImgView, connectBtn, titleLbl,
        descriptionLbl, keyInputStatusBox);
    keyInputInnerPane.setAlignment(Pos.CENTER);
    keyInputInnerPane.getChildren().stream().forEach(node -> {
      GridPane.setVgrow(node, Priority.ALWAYS);
      GridPane.setHgrow(node, Priority.ALWAYS);
      GridPane.setValignment(node, VPos.CENTER);
      GridPane.setHalignment(node, HPos.CENTER);
      GridPane.setMargin(node, new Insets(GRIDPANE_MARGIN));
    });

    // column division
    ColumnConstraints col1 = new ColumnConstraints();
    col1.setPercentWidth(45);
    ColumnConstraints col2 = new ColumnConstraints();
    col2.setPercentWidth(5);
    ColumnConstraints col3 = new ColumnConstraints();
    col3.setPercentWidth(50);

    keyInputInnerPane.getColumnConstraints().addAll(col1, col2, col3);

    // special styling
    GridPane.setVgrow(statusBox, Priority.NEVER);
    GridPane.setValignment(titleLbl, VPos.BOTTOM);
    GridPane.setHalignment(titleLbl, HPos.LEFT);
    GridPane.setValignment(descriptionLbl, VPos.CENTER);
    GridPane.setValignment(keyFld, VPos.CENTER);
    GridPane.setValignment(validationImgView, VPos.CENTER);
    GridPane.setValignment(connectBtn, VPos.TOP);
    GridPane.setMargin(titleLbl, new Insets(0));
    GridPane.setMargin(descriptionLbl, new Insets(0));
    GridPane.setMargin(keyFld, new Insets(0, 0, 10, 0));
    GridPane.setMargin(validationImgView, new Insets(0));
    GridPane.setMargin(connectBtn, new Insets(0));

    keyInputInnerPane.setPadding(new Insets(10));

  }

  private void layoutStartServicePane() {
    GridPane.setConstraints(startServiceBtn, 0, 1);
    GridPane.setConstraints(startServiceTitleLbl, 1, 0);
    GridPane.setConstraints(startServiceDescriptionLbl, 1, 1);
    GridPane.setConstraints(emptyPane, 0, 2);
    GridPane.setConstraints(statusBox, 0, 3);

    GridPane.setColumnSpan(statusBox, 2);

    startServiceInnerPane.getChildren().addAll(startServiceBtn,
        startServiceDescriptionLbl, startServiceTitleLbl, emptyPane, statusBox);

    // initial styling
    startServiceInnerPane.getChildren().stream().forEach(node -> {
          GridPane.setVgrow(node, Priority.ALWAYS);
          GridPane.setHgrow(node, Priority.ALWAYS);
          GridPane.setValignment(node, VPos.CENTER);
          GridPane.setHalignment(node, HPos.CENTER);
          GridPane.setMargin(node, new Insets(10));
      startServiceInnerPane.setAlignment(Pos.CENTER);
      GridPane.setVgrow(keyInputStatusBox, Priority.NEVER);
      GridPane.setValignment(keyInputStatusBox, VPos.BOTTOM);
        }
    );

    // column division
    ColumnConstraints col1 = new ColumnConstraints();
    col1.setPercentWidth(50);
    ColumnConstraints col2 = new ColumnConstraints();
    col2.setPercentWidth(50);
    startServiceInnerPane.getColumnConstraints().addAll(col1, col2);

    // special styling
    GridPane.setHalignment(startServiceTitleLbl, HPos.LEFT);
    GridPane.setValignment(startServiceTitleLbl, VPos.BOTTOM);
    GridPane.setHalignment(startServiceTitleLbl, HPos.LEFT);
    GridPane.setValignment(startServiceBtn, VPos.CENTER);
    GridPane.setValignment(startServiceDescriptionLbl, VPos.CENTER);
    GridPane.setVgrow(statusBox, Priority.NEVER);
    GridPane.setValignment(statusBox, VPos.BOTTOM);

    GridPane.setMargin(titleLbl, new Insets(0));
  }

  private void bindFieldsToModel() {
    startServiceBtn.setOnAction(e -> model.startViewerReverse());

  }

}
