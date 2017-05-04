package ch.imedias.rsccfx.view;

import ch.imedias.rsccfx.localization.Strings;
import ch.imedias.rsccfx.model.Rscc;
import java.util.logging.Logger;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * Defines all elements shown in the support section.
 */
public class RsccSupportView extends BorderPane {
  private static final Logger LOGGER =
      Logger.getLogger(RsccSupportView.class.getName());

  private final Rscc model;
  private final Strings strings = new Strings();
  private static final double KEYFLD_HEIGHT = 60d;

  final HeaderView headerView;

  final Label titleLbl = new Label();
  final Label descriptionLbl = new Label();

  final VBox descriptionBox = new VBox();
  final HBox centerBox = new HBox();
  final HBox keyValidationBox = new HBox();

  final TextField keyFld = new TextField();

  final TitledPane keyInputPane = new TitledPane();
  final TitledPane addressbookPane = new TitledPane();

  ImageView validationImgView = new ImageView();

  final Button connectBtn = new Button();

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
  }

  private void initFieldData() {
    // populate fields which require initial data
    titleLbl.textProperty().set("Enter key");
    descriptionLbl.textProperty().set("Initiate a new remote support connection by entering the "
        + "keyphrase your supporter sent you.");

    validationImgView = new ImageView(getClass()
        .getClassLoader()
        .getResource("dialog-error.png")
        .toExternalForm());                     // TODO: Check what to do here.

    connectBtn.textProperty().set("Connect");

    keyInputPane.setText("Key Input");

    addressbookPane.setText("Addressbook");
  }

  private void layoutForm() {
    addressbookPane.setExpanded(false);

    keyFld.setPrefHeight(KEYFLD_HEIGHT);

    titleLbl.getStyleClass().add("titleLbl");

    descriptionLbl.getStyleClass().add("descriptionLbl");

    //keyFld.setFont(new Font(30)); // TODO: Move to CSS
    keyFld.setId("keyFld");

    validationImgView.setSmooth(true);

    keyValidationBox.getChildren().addAll(keyFld, validationImgView);
    //keyValidationBox.setSpacing(5);       // TODO: Move to CSS.
    HBox.setHgrow(descriptionBox, Priority.ALWAYS);

    keyValidationBox.setId("keyValidationBox");

    descriptionBox.getStyleClass().add("descriptionBox");

    //descriptionBox.setAlignment(Pos.CENTER_LEFT);

    titleLbl.getStyleClass().add("titleLbl");

    descriptionBox.getChildren().addAll(titleLbl, descriptionLbl, connectBtn);

    centerBox.getChildren().addAll(keyValidationBox, descriptionBox);

    centerBox.setId("centerBoxSupport");

    connectBtn.setId("connectBtn");

    keyInputPane.setContent(centerBox);
    keyInputPane.setExpanded(true);
    // TODO: Set content for addressbookPane

    connectBtn.setDisable(true);
    //connectBtn.setFont(new Font(30));       // TODO: Move to CSS.
    setCenter(keyInputPane);
    setTop(headerView);
    setBottom(addressbookPane);
  }


  private void bindFieldsToModel() {
    // make bindings to the model

  }

}
