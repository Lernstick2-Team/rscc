package ch.imedias.rsccfx.view;

import ch.imedias.rsccfx.localization.Strings;
import ch.imedias.rsccfx.model.Rscc;
import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.util.logging.Logger;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;


/**
 * Defines all elements shown in the header.
 */
public class HeaderView extends GridPane {
  private static final Logger LOGGER =
      Logger.getLogger(Rscc.class.getName());

  private static final double ICON_SIZE = 40;

  private static final DoubleProperty BUTTON_SIZE = new SimpleDoubleProperty(100);
  private static final int BUTTON_MARGIN = 10;

  final Button backBtn = new Button();
  final Button helpBtn = new Button();
  final Button settingsBtn = new Button();
  private final Strings strings = new Strings();
  private final Rscc model;

  /**
   * Initializes all the GUI components needed in the Header.
   *
   * @param model the model to handle the data.
   */
  public HeaderView(Rscc model) {
    this.model = model;
    SvgImageLoaderFactory.install();
    initFieldData();
    layoutForm();
    bindFieldsToModel();
    this.setGridLinesVisible(true);
  }

  private void initFieldData() {
    // populate fields which require initial data
    FontAwesomeIconView questionIcon = new FontAwesomeIconView(FontAwesomeIcon.QUESTION);
    questionIcon.setGlyphSize(ICON_SIZE);
    helpBtn.setGraphic(questionIcon);

    FontAwesomeIconView settingsIcon = new FontAwesomeIconView(FontAwesomeIcon.GEAR);
    settingsIcon.setGlyphSize(ICON_SIZE);
    settingsBtn.setGraphic(settingsIcon);

    FontAwesomeIconView backIcon = new FontAwesomeIconView(FontAwesomeIcon.CARET_LEFT);
    backIcon.setGlyphSize(ICON_SIZE);
    backBtn.setGraphic(backIcon);
  }

  private void layoutForm() {
    //setup layout (aka setup specific pane etc.)
    GridPane.setConstraints(backBtn, 0, 0);
    GridPane.setConstraints(settingsBtn, 2, 0);
    GridPane.setConstraints(helpBtn, 3, 0);

    RowConstraints row1 = new RowConstraints();
    this.getRowConstraints().addAll(row1);

    ColumnConstraints col1 = new ColumnConstraints();
    ColumnConstraints col2 = new ColumnConstraints();
    ColumnConstraints col3 = new ColumnConstraints();
    ColumnConstraints col4 = new ColumnConstraints();

    this.getColumnConstraints().addAll(col1, col2, col3, col4);

    col1.setPercentWidth(10);
    col2.setPercentWidth(70);
    col3.setPercentWidth(10);
    col4.setPercentWidth(10);


    GridPane.setHalignment(backBtn, HPos.LEFT);
    GridPane.setHalignment(settingsBtn, HPos.RIGHT);
    GridPane.setHalignment(helpBtn, HPos.RIGHT);

    this.getChildren().addAll(backBtn, settingsBtn, helpBtn);

    // initial styling
    this.getChildren().stream()
        .forEach(node -> {
          GridPane.setVgrow(node, Priority.ALWAYS);
          GridPane.setHgrow(node, Priority.ALWAYS);
          GridPane.setValignment(node, VPos.CENTER);
          GridPane.setHalignment(node, HPos.CENTER);
          GridPane.setMargin(node, new Insets(BUTTON_MARGIN));

          Button button = (Button) node;
          button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        });

    this.prefHeightProperty().bind(BUTTON_SIZE);

    this.setId("header");

    this.setPadding(new Insets(BUTTON_MARGIN, 0, BUTTON_MARGIN, 0));

    backBtn.setId("backBtn");

    helpBtn.setId("helpBtn");

    settingsBtn.setId("settingsBtn");
  }

  private void bindFieldsToModel() {
    // make bindings to the model
  }




}

