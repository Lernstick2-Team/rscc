package ch.imedias.rsccfx.view;

import ch.imedias.rsccfx.RsccApp;
import ch.imedias.rsccfx.localization.Strings;
import ch.imedias.rsccfx.model.Rscc;
import ch.imedias.rsccfx.model.xml.Supporter;
import java.util.Optional;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Creates the DialogPane by SupporterButton click.
 */
public class SupporterAttributesDialog extends DialogPane {

  private static final Logger LOGGER =
      Logger.getLogger(SupporterAttributesDialog.class.getName());

  private static final int DEFAULT_PORT = 5500;

  final Dialog dialog = new Dialog();
  final GridPane attributePane = new GridPane();
  Strings strings = new Strings();
  final Label nameLbl = new Label();
  final Label addressLbl = new Label();
  final Label portLbl = new Label();
  final Label chargeableLbl = new Label();
  final Label encryptedLbl = new Label();
  final Label nameFld = new Label();
  final Label addressFld = new Label();
  final Label portFld = new Label();
  final ButtonType cancelBtnType = ButtonType.CANCEL;
  final ButtonType editBtnType = new ButtonType(strings.dialogEditButtonText);
  final ButtonType callBtnType = new ButtonType(strings.dialogCallButtonText);
  final CheckBox chargeableCBox = new CheckBox();
  final CheckBox encryptedCBox = new CheckBox();
  private Supporter supporter;
  private Rscc model;

  BooleanProperty nameValid = new SimpleBooleanProperty(false);

  /**
   * Initializes all the GUI components needed in the DialogPane.
   *
   * @param supporter the supporter for the dialog.
   */
  public SupporterAttributesDialog(Supporter supporter, Rscc model) {
    this.model = model;
    this.getStylesheets().add(RsccApp.styleSheet);
    this.supporter = supporter;
    initFieldData();
    layoutForm();
    attachEventListeners();
  }

  private void initFieldData() {
    // populate fields which require initial data
    dialog.setTitle(strings.dialogTitleText);
    nameLbl.setText(strings.dialogNameText);
    addressLbl.setText(strings.dialogAddressText);
    portLbl.setText(strings.dialogPortText);
    chargeableLbl.setText(strings.dialogChargeableLbl);
    encryptedLbl.setText(strings.dialogEncryptedLbl);

    nameFld.setText(supporter.getDescription());
    validateName();

    addressFld.setText(supporter.getAddress());
    portFld.setText(String.valueOf(supporter.getPort()));
    chargeableCBox.setSelected(supporter.isChargeable());
    encryptedCBox.setSelected(supporter.isEncrypted());
  }


  private void layoutForm() {
    // Set Hgrow for TextField
    attributePane.setHgrow(addressFld, Priority.ALWAYS);
    attributePane.getStyleClass().add("gridPane");

    //setup layout (aka setup specific pane etc.)
    attributePane.setHgap(20);
    attributePane.setVgap(10);
    attributePane.setPadding(new Insets(25, 25, 25, 25));
    attributePane.autosize();
    //Resizable is false because form would look bad with big font size
    dialog.setResizable(false);
    dialog.setHeight(500);
    dialog.setWidth(500);
    attributePane.setId("dialogAttributePane");

    encryptedCBox.setDisable(true);
    chargeableCBox.setDisable(true);

    attributePane.add(nameLbl, 0, 0);
    attributePane.add(nameFld, 1, 0);
    attributePane.add(addressLbl, 0, 1);
    attributePane.add(addressFld, 1, 1);
    attributePane.add(portLbl, 0, 2);
    attributePane.add(portFld, 1, 2);
    attributePane.add(chargeableLbl, 0, 4);
    attributePane.add(chargeableCBox, 1, 4);
    attributePane.add(encryptedLbl, 0, 5);
    attributePane.add(encryptedCBox, 1, 5);

    this.getButtonTypes().addAll(callBtnType, editBtnType, cancelBtnType);

    this.setContent(attributePane);
    dialog.setDialogPane(this);
  }

  private void attachEventListeners() {
    nameFld.textProperty().addListener(
        (observable, oldValue, newValue) -> validateName()
    );
    Button editBtn = (Button)lookupButton(callBtnType);
    editBtn.setOnAction(event -> {
      System.out.println("SURPRISE!");
    });
  }

  private boolean isEmpty(String string) {
    return "".equals(string.trim());
  }

  /**
   * Shows the current dialog and saves the supporter, if the apply button was pressed.
   *
   * @return true, if the apply button was pressed
   */
  public boolean show() {
    Optional userChoice = dialog.showAndWait();

    if (userChoice.isPresent()) {
      if (userChoice.get() == editBtnType) {
        SupporterAttributesDialogEdit editDialog =
            new SupporterAttributesDialogEdit(this.supporter);
        return editDialog.show();
      }
      if (userChoice.get() == callBtnType) {
        model.callSupporterDirect(supporter.getAddress(),
            supporter.getPort(), supporter.isEncrypted());
      }
    }

    return false;
  }

  private boolean getNameValid() {
    return nameValid.get();
  }

  private BooleanProperty nameValidProperty() {
    return nameValid;
  }

  private void setNameValid(boolean nameValid) {
    this.nameValid.set(nameValid);
  }

  private void validateName() {
    setNameValid(!isEmpty(nameFld.getText()));
  }
}
