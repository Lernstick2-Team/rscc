package ch.imedias.rsccfx.view;

import ch.imedias.rsccfx.localization.Strings;
import ch.imedias.rsccfx.model.Rscc;
import ch.imedias.rsccfx.view.util.KeyTextField;
import ch.imedias.rsccfx.view.util.StatusBar;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Defines all elements shown in the support section.
 */
public class RsccSupportView extends BorderPane {
  private static final Logger LOGGER =
      LogManager.getLogger(RsccSupportView.class.getName());
  private static final int GRIDPANE_MARGIN = 25;
  final HeaderView headerView;

  final Label titleLbl = new Label();
  final Label descriptionLbl = new Label();
  final Label startServiceDescriptionLbl = new Label();
  final Label startServiceTitleLbl = new Label();

  final StatusBar statusBarKeyInput = new StatusBar();
  final StatusBar statusBarStartService = new StatusBar();

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
    GridPane.setConstraints(statusBarKeyInput, 0, 3);
    GridPane.setColumnSpan(statusBarKeyInput, 3);

    keyInputInnerPane.getChildren().addAll(keyFld, validationImgView, connectBtn, titleLbl,
        descriptionLbl, statusBarKeyInput);
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
    GridPane.setVgrow(statusBarKeyInput, Priority.NEVER);
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
    GridPane.setConstraints(statusBarStartService, 0, 3);

    GridPane.setColumnSpan(statusBarStartService, 2);

    startServiceInnerPane.getChildren().addAll(startServiceBtn,
        startServiceDescriptionLbl, startServiceTitleLbl, emptyPane, statusBarStartService);

    // initial styling
    startServiceInnerPane.getChildren().stream().forEach(node -> {
          GridPane.setVgrow(node, Priority.ALWAYS);
          GridPane.setHgrow(node, Priority.ALWAYS);
          GridPane.setValignment(node, VPos.CENTER);
          GridPane.setHalignment(node, HPos.CENTER);
          GridPane.setMargin(node, new Insets(10));
      startServiceInnerPane.setAlignment(Pos.CENTER);
      GridPane.setVgrow(statusBarStartService, Priority.NEVER);
      GridPane.setValignment(statusBarStartService, VPos.BOTTOM);
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
    GridPane.setVgrow(statusBarStartService, Priority.NEVER);
    GridPane.setValignment(statusBarStartService, VPos.BOTTOM);

    GridPane.setMargin(titleLbl, new Insets(0));
  }

  private void bindFieldsToModel() {
    startServiceBtn.setOnAction(e -> model.startViewerReverse());

  }

}
