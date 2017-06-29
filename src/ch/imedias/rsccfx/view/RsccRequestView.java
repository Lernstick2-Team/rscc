package ch.imedias.rsccfx.view;

import ch.imedias.rsccfx.RsccApp;
import ch.imedias.rsccfx.localization.Strings;
import ch.imedias.rsccfx.model.Rscc;
import ch.imedias.rsccfx.model.util.KeyUtil;
import ch.imedias.rsccfx.view.util.KeyTextField;
import ch.imedias.rsccfx.view.util.StatusBar;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Defines all elements shown in the request section.
 */
public class RsccRequestView extends BorderPane {
  private static final Logger LOGGER =
      LogManager.getLogger(RsccRequestView.class.getName());

  private static final double BUTTON_PADDING = 30;
  private static final double ICON_SIZE = 30;

  final HeaderView headerView;

  final Label titleLbl = new Label();
  final Label descriptionLbl = new Label();
  final Label supporterDescriptionLbl = new Label();

  final GridPane keyGenerationInnerPane = new GridPane();
  final GridPane supporterInnerPane = new GridPane();

  final StatusBar statusBarKeyGeneration = new StatusBar();
  final StatusBar statusBarSupporter = new StatusBar();

  final HBox supporterInnerBox = new HBox();
  final VBox supporterOuterBox = new VBox();
  final VBox supporterInnerRightBox = new VBox();

  final VBox contentBox = new VBox();

  final TitledPane keyGenerationTitledPane = new TitledPane();
  final TitledPane supporterTitledPane = new TitledPane();

  final ScrollPane scrollPane = new ScrollPane();

  final KeyTextField generatedKeyFld = new KeyTextField();
  private final double scalingFactor = RsccApp.getScalingFactor();
  private final Rscc model;
  private final Strings strings = new Strings();

  private final KeyUtil keyUtil;

  Button reloadKeyBtn = new Button();
  Button resetBtn = new Button();
  final Button disconnectBtn = new Button();


  /**
   * Initializes all the GUI components needed to generate the key the supporter needs.
   *
   * @param model the model to handle the data.
   */
  public RsccRequestView(Rscc model) {
    this.model = model;
    headerView = new HeaderView(model);
    this.keyUtil = model.getKeyUtil();
    initFieldData();
    layoutForm();
    layoutKeyGenerationPane();
    layoutSupporterPane();
    bindFieldsToModel();
  }

  private void initFieldData() {
    // populate fields which require initial data
    disconnectBtn.setText(strings.requestDisconnectBtn);
    titleLbl.setText(strings.requestTitleLbl);
    descriptionLbl.setText(strings.requestDescriptionLbl);
    generatedKeyFld.setText(strings.requestGeneratedKeyFld);
    supporterDescriptionLbl.setText(strings.requestSupporterDescriptionLbl);
    keyGenerationTitledPane.setText(strings.requestKeyGeneratorPane);
    supporterTitledPane.setText(strings.requestPredefinedAdressessPane);

    FontAwesomeIconView refreshIcon = new FontAwesomeIconView(FontAwesomeIcon.REFRESH);
    refreshIcon.setGlyphSize(ICON_SIZE);
    reloadKeyBtn.setGraphic(refreshIcon);
    resetBtn.setText(strings.requestResetSupportersBtn);

  }

  private void layoutForm() {
    //setup layout (aka setup specific pane etc.)
    keyGenerationTitledPane.setExpanded(true);
    keyGenerationTitledPane.setId("keyGenerationTitledPane");

    descriptionLbl.getStyleClass().add("descriptionLbl");
    supporterDescriptionLbl.getStyleClass().add("descriptionLbl");

    supporterTitledPane.setExpanded(false);
    supporterTitledPane.setId("supporterTitledPane");

    titleLbl.getStyleClass().add("titleLbl");

    descriptionLbl.getStyleClass().add("nameLbl");

    supporterDescriptionLbl.getStyleClass().add("supporterDescriptionLbl");

    generatedKeyFld.setEditable(false);
    generatedKeyFld.getStyleClass().add("keyFld");

    reloadKeyBtn.setPadding(new Insets(BUTTON_PADDING));
    reloadKeyBtn.setId("reloadKeyBtn");

    disconnectBtn.setId("connectBtn");
    disconnectBtn.setVisible(false);

    contentBox.getChildren().addAll(keyGenerationTitledPane, keyGenerationInnerPane,
        supporterTitledPane);
    contentBox.getStyleClass().add("contentBox");
    descriptionLbl.getStyleClass().add("nameLbl");

    VBox.setVgrow(keyGenerationInnerPane, Priority.ALWAYS);
    keyGenerationInnerPane.getStyleClass().add("contentRequest");
    VBox.setVgrow(supporterInnerBox, Priority.ALWAYS);
    supporterInnerBox.getStyleClass().add("contentRequest");
    VBox.setVgrow(supporterOuterBox, Priority.ALWAYS);
    VBox.setVgrow(supporterInnerRightBox, Priority.ALWAYS);

    supporterOuterBox.setMargin(statusBarSupporter, new Insets(15, 12, 15, 12));

    resetBtn.setId("connectBtn");

    setTop(headerView);
    setCenter(contentBox);
  }

  private void layoutKeyGenerationPane() {
    // set elements
    GridPane.setConstraints(generatedKeyFld, 0, 1);
    GridPane.setConstraints(reloadKeyBtn, 1, 1);
    GridPane.setConstraints(titleLbl, 2, 0);
    GridPane.setConstraints(descriptionLbl, 2, 1);
    GridPane.setConstraints(statusBarKeyGeneration, 0, 3);
    GridPane.setConstraints(disconnectBtn, 0, 2);
    GridPane.setColumnSpan(statusBarKeyGeneration, 3);

    keyGenerationInnerPane.setAlignment(Pos.CENTER);

    keyGenerationInnerPane.getChildren().addAll(generatedKeyFld, disconnectBtn,  reloadKeyBtn,
        titleLbl, descriptionLbl, statusBarKeyGeneration);

    // initial styling
    keyGenerationInnerPane.getChildren().stream()
        .forEach(node -> {
          GridPane.setVgrow(node, Priority.ALWAYS);
          GridPane.setHgrow(node, Priority.ALWAYS);
          GridPane.setValignment(node, VPos.CENTER);
          GridPane.setHalignment(node, HPos.CENTER);
          GridPane.setMargin(node, new Insets(10 * scalingFactor));
          keyGenerationInnerPane.setAlignment(Pos.CENTER);
        });

    // column division
    ColumnConstraints col1 = new ColumnConstraints();
    col1.setPercentWidth(45);
    keyGenerationInnerPane.getColumnConstraints().addAll(col1);

    // special styling
    GridPane.setVgrow(statusBarKeyGeneration, Priority.NEVER);
    GridPane.setValignment(statusBarKeyGeneration, VPos.BOTTOM);
    GridPane.setHalignment(titleLbl, HPos.LEFT);
    GridPane.setValignment(titleLbl, VPos.BOTTOM);
    GridPane.setHalignment(descriptionLbl, HPos.LEFT);
    GridPane.setValignment(reloadKeyBtn, VPos.CENTER);
    GridPane.setValignment(disconnectBtn, VPos.TOP);
    GridPane.setMargin(titleLbl, new Insets(0));
    GridPane.setMargin(generatedKeyFld, new Insets(0, 0, 10, 0));
    GridPane.setMargin(descriptionLbl, new Insets(0));
    GridPane.setMargin(disconnectBtn, new Insets(0));
    keyGenerationInnerPane.setPadding(new Insets(10 * scalingFactor));

  }

  private void layoutSupporterPane() {
    supporterOuterBox.getChildren().addAll(supporterInnerBox, statusBarSupporter);
    supporterInnerBox.getChildren().addAll(scrollPane, supporterInnerRightBox);
    supporterInnerRightBox.getChildren().addAll(supporterDescriptionLbl, resetBtn);

    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    scrollPane.setContent(supporterInnerPane);
    scrollPane.setId("scrollPane");

    // add column constraints
    ColumnConstraints col1 = new ColumnConstraints();
    ColumnConstraints col2 = new ColumnConstraints();
    ColumnConstraints col3 = new ColumnConstraints();

    supporterInnerPane.getColumnConstraints().addAll(col1, col2, col3);

    int amountOfColumns = supporterInnerPane.getColumnConstraints().size();
    int columnPercentWidth = 100 / amountOfColumns;

    col1.setPercentWidth(columnPercentWidth);
    col2.setPercentWidth(columnPercentWidth);
    col3.setPercentWidth(columnPercentWidth);
  }

  private void bindFieldsToModel() {
    // make bindings to the model
    generatedKeyFld.textProperty().bind(keyUtil.formattedKeyProperty());
  }
}
