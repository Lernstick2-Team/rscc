package ch.imedias.rsccfx.view;

import ch.imedias.rsccfx.RsccApp;
import ch.imedias.rsccfx.localization.Strings;
import ch.imedias.rsccfx.model.Rscc;
import ch.imedias.rsccfx.model.util.KeyUtil;
import ch.imedias.rsccfx.view.util.KeyTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.util.logging.Logger;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


/**
 * Defines all elements shown in the request section.
 */
public class RsccRequestView extends BorderPane {
  private static final Logger LOGGER =
      Logger.getLogger(RsccRequestView.class.getName());

  private static final double BUTTON_PADDING = 30;
  private static final double ICON_SIZE = 30;

  final HeaderView headerView;

  final Label titleLbl = new Label();
  final Label descriptionLbl = new Label();
  final Label supporterDescriptionLbl = new Label();
  final Label statusLbl = new Label();

  final GridPane keyGenerationInnerPane = new GridPane();
  final GridPane supporterInnerPane = new GridPane();

  final HBox statusBox = new HBox();

  final HBox supporterInnerBox = new HBox();

  final VBox contentBox = new VBox();

  final TitledPane keyGenerationTitledPane = new TitledPane();
  final TitledPane supporterTitledPane = new TitledPane();

  final ScrollPane scrollPane = new ScrollPane();

  final KeyTextField generatedKeyFld = new KeyTextField();
  private final double scalingFactor = RsccApp.scalingFactor;
  private final Rscc model;
  private final Strings strings = new Strings();

  private final KeyUtil keyUtil;

  Button reloadKeyBtn = new Button();

  private Pane emptyPane = new Pane();

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
    titleLbl.setText(strings.requestTitleLbl);
    descriptionLbl.setText(strings.requestDescriptionLbl);
    generatedKeyFld.setText(strings.requestGeneratedKeyFld);
    supporterDescriptionLbl.setText(strings.requestSupporterDescriptionLbl);
    keyGenerationTitledPane.setText(strings.requestKeyGeneratorPane);
    supporterTitledPane.setText(strings.requestPredefinedAdressessPane);
    statusLbl.setText("");

    FontAwesomeIconView refreshIcon = new FontAwesomeIconView(FontAwesomeIcon.REFRESH);
    refreshIcon.setGlyphSize(ICON_SIZE);
    reloadKeyBtn.setGraphic(refreshIcon);

  }

  private void layoutForm() {
    //setup layout (aka setup specific pane etc.)
    keyGenerationTitledPane.setExpanded(true);
    keyGenerationTitledPane.setId("keyGenerationTitledPane");

    supporterTitledPane.setExpanded(false);
    supporterTitledPane.setId("supporterTitledPane");

    titleLbl.getStyleClass().add("titleLbl");

    descriptionLbl.getStyleClass().add("nameLbl"); // TODO: Styling

    supporterDescriptionLbl.getStyleClass().add("supporterDescriptionLbl");

    statusBox.getStyleClass().add("statusBox");
    statusBox.getChildren().addAll(statusLbl);
    statusLbl.getStyleClass().add("statusLbl");

    generatedKeyFld.setEditable(false);
    generatedKeyFld.getStyleClass().add("keyFld");

    reloadKeyBtn.setPadding(new Insets(BUTTON_PADDING));
    reloadKeyBtn.setId("reloadKeyBtn");

    contentBox.getChildren().addAll(keyGenerationTitledPane, keyGenerationInnerPane,
        supporterTitledPane);
    descriptionLbl.getStyleClass().add("nameLbl"); // TODO: Styling

    VBox.setVgrow(keyGenerationInnerPane, Priority.ALWAYS);
    keyGenerationInnerPane.getStyleClass().add("contentRequest");
    VBox.setVgrow(supporterInnerBox, Priority.ALWAYS);
    supporterInnerBox.getStyleClass().add("contentRequest");

    setTop(headerView);
    setCenter(contentBox);
  }

  private void layoutSupporterPane() {
    supporterInnerBox.getChildren().addAll(scrollPane, supporterDescriptionLbl);

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

  private void layoutKeyGenerationPane() {
    // set elements
    GridPane.setConstraints(generatedKeyFld, 0, 1);
    GridPane.setConstraints(reloadKeyBtn, 1, 1);
    GridPane.setConstraints(titleLbl, 2, 0);
    GridPane.setConstraints(descriptionLbl, 2, 1);
    GridPane.setConstraints(emptyPane, 0, 2);
    GridPane.setConstraints(statusBox, 0, 3);

    GridPane.setColumnSpan(statusBox, 3);

    keyGenerationInnerPane.getChildren().addAll(generatedKeyFld, reloadKeyBtn, titleLbl,
        descriptionLbl, statusBox, emptyPane);

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
    col1.setPercentWidth(40);
    ColumnConstraints col2 = new ColumnConstraints();
    col2.setPercentWidth(10);
    ColumnConstraints col3 = new ColumnConstraints();
    col3.setPercentWidth(50);
    keyGenerationInnerPane.getColumnConstraints().addAll(col1, col2, col3);

    // special styling
    GridPane.setVgrow(statusBox, Priority.NEVER);
    GridPane.setValignment(statusBox, VPos.BOTTOM);
    GridPane.setHalignment(titleLbl, HPos.LEFT);
    GridPane.setValignment(titleLbl, VPos.BOTTOM);
    GridPane.setHalignment(descriptionLbl, HPos.LEFT);
    GridPane.setValignment(reloadKeyBtn, VPos.CENTER);
    GridPane.setMargin(titleLbl, new Insets(0));
    GridPane.setMargin(descriptionLbl, new Insets(0));
    keyGenerationInnerPane.setPadding(new Insets(10 * scalingFactor));

  }

  private void bindFieldsToModel() {
    // make bindings to the model
    generatedKeyFld.textProperty().bind(keyUtil.formattedKeyProperty());
  }
}
