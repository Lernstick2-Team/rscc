package ch.imedias.rsccfx.view;

import ch.imedias.rsccfx.RsccApp;
import ch.imedias.rsccfx.localization.Strings;
import ch.imedias.rsccfx.model.Rscc;
import ch.imedias.rsccfx.model.xml.Supporter;
import ch.imedias.rsccfx.view.util.NumberTextField;
import java.util.Optional;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

  // Description Labels
  final Label nameLbl = new Label();
  final Label addressLbl = new Label();
  final Label portLbl = new Label();
  final Label chargeableLbl = new Label();
  final Label encryptedLbl = new Label();

  // Read mode fields
  final Label nameReadLbl = new Label();
  final Label addressReadLbl = new Label();
  final Label portReadLbl = new Label();

  // Edit mode fields
  final TextField nameEditFld = new TextField();
  final TextField addressEditFld = new TextField();
  final NumberTextField portEditFld = new NumberTextField();

  final CheckBox chargeableCBox = new CheckBox();
  final CheckBox encryptedCBox = new CheckBox();

  final ButtonType editBtnType = new ButtonType(strings.dialogEditButtonText);
  final ButtonType connectBtnType = new ButtonType(strings.dialogConnectButtonText);
  final ButtonType okBtnType = ButtonType.OK;
  final ButtonType applyBtnType = ButtonType.APPLY;
  final ButtonType cancelBtnType = ButtonType.CANCEL;
  Button editBtn;
  Button applyBtn;
  Button cancelBtn;

  /**
   * Monitors if data was saved, so we can return if data was changed or not.
   */
  private boolean wasSaved = false;

  private Supporter supporter;
  private Rscc model;
  private final BooleanProperty editMode = new SimpleBooleanProperty();

  BooleanProperty nameValid = new SimpleBooleanProperty(false);

  /**
   * Initializes all the GUI components needed in the DialogPane.
   *
   * @param supporter the supporter for the dialog.
   */
  public SupporterAttributesDialog(Supporter supporter, Rscc model, boolean editMode) {
    this.model = model;
    this.getStylesheets().add(RsccApp.styleSheet);
    this.supporter = supporter;
    // if a new supporter was opened, start in edit mode
    System.out.println("Edit Mode: " + (editMode || supporter.toString().equals("+")));
    setEditMode(editMode || supporter.toString().equals("+"));
    initFieldData();
    layoutForm();
    attachEventListeners();
    setupBindings();
  }

  private void setupBindings() {
    // bind edit fields and read labels
    addressReadLbl.textProperty().bind(addressEditFld.textProperty());
    nameReadLbl.textProperty().bind(nameEditFld.textProperty());
    portReadLbl.textProperty().bind(portEditFld.textProperty());

    // what should be shown in the read mode
    addressReadLbl.visibleProperty().bind(editModeProperty().not());
    nameReadLbl.visibleProperty().bind(editModeProperty().not());
    portReadLbl.visibleProperty().bind(editModeProperty().not());
    chargeableCBox.disableProperty().bind(editModeProperty());
    encryptedCBox.disableProperty().bind(editModeProperty());

    // what should be shown in edit mode
    addressEditFld.visibleProperty().bind(editModeProperty());
    nameEditFld.visibleProperty().bind(editModeProperty());
    portEditFld.visibleProperty().bind(editModeProperty());
    chargeableCBox.disableProperty().bind(editModeProperty().not());
    encryptedCBox.disableProperty().bind(editModeProperty().not());
  }

  private void initFieldData() {
    // populate fields which require initial data
    dialog.setTitle(strings.dialogTitleText);
    nameLbl.setText(strings.dialogNameText);
    addressLbl.setText(strings.dialogAddressText);
    portLbl.setText(strings.dialogPortText);
    chargeableLbl.setText(strings.dialogChargeableLbl);
    encryptedLbl.setText(strings.dialogEncryptedLbl);

    nameEditFld.setText(supporter.getDescription());
    validateName();

    addressEditFld.setText(supporter.getAddress());
    portEditFld.setText(String.valueOf(supporter.getPort()));
    chargeableCBox.setSelected(supporter.isChargeable());
    encryptedCBox.setSelected(supporter.isEncrypted());
  }

  private void layoutForm() {
    // Set Hgrow for TextField
    attributePane.setHgrow(addressEditFld, Priority.ALWAYS);
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
    attributePane.add(nameReadLbl, 1, 0);
    attributePane.add(nameEditFld, 1, 0);
    attributePane.add(addressLbl, 0, 1);
    attributePane.add(addressReadLbl, 1, 1);
    attributePane.add(addressEditFld, 1, 1);
    attributePane.add(portLbl, 0, 2);
    attributePane.add(portReadLbl, 1, 2);
    attributePane.add(portEditFld, 1, 2);
    attributePane.add(chargeableLbl, 0, 4);
    attributePane.add(chargeableCBox, 1, 4);
    attributePane.add(encryptedLbl, 0, 5);
    attributePane.add(encryptedCBox, 1, 5);

    if (!isEditMode()) {
      this.getButtonTypes().addAll(cancelBtnType, applyBtnType);
    }else{
      getButtonTypes().addAll(editBtnType, okBtnType, connectBtnType);
    }

    this.setContent(attributePane);
    dialog.setDialogPane(this);
  }

  private void attachEventListeners() {
    nameEditFld.textProperty().addListener(
        (observable, oldValue, newValue) -> validateName()
    );

    layoutReadMode();

    editModeProperty().addListener((observable, oldIsEditMode, newIsEditMode) -> {
      if (oldIsEditMode != newIsEditMode) {
        if (newIsEditMode) {
          layoutEditMode();
        } else {
          layoutReadMode();
        }
      }
    });
  }

  private void layoutEditMode() {
    getButtonTypes().removeAll(editBtnType, okBtnType, connectBtnType);
    getButtonTypes().addAll(applyBtnType, cancelBtnType);

    // Set Read mode upon pressing the apply button
    applyBtn = (Button)lookupButton(applyBtnType);
    applyBtn.addEventFilter(
        ActionEvent.ACTION,
        event -> {
          event.consume(); // stops the window from closing
          saveData();
          setEditMode(false);
        }
    );

    // Set Read mode upon pressing the cancel button
    cancelBtn = (Button)lookupButton(cancelBtnType);
    cancelBtn.addEventFilter(
        ActionEvent.ACTION,
        event -> {
          event.consume(); // stops the window from closing
          initFieldData();
          setEditMode(false);
        }
    );
  }

  private void layoutReadMode() {
    getButtonTypes().removeAll(applyBtnType, cancelBtnType);
    getButtonTypes().addAll(connectBtnType, editBtnType, okBtnType);

    // Set Edit mode upon pressing the edit button
    editBtn = (Button)lookupButton(editBtnType);
    editBtn.addEventFilter(
        ActionEvent.ACTION,
        event -> {
          event.consume(); // stops the window from closing
          setEditMode(true);
        }
    );
  }

  private void saveData() {
    supporter.setDescription(nameEditFld.getText());
    supporter.setAddress(addressEditFld.getText());
    if (isEmpty(portEditFld.getText())) {
      portEditFld.setText(String.valueOf(DEFAULT_PORT));
    }
    supporter.setPort(portEditFld.getText());
    supporter.setEncrypted(encryptedCBox.isSelected());
    supporter.setChargeable(chargeableCBox.isSelected());

    wasSaved = true;
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
      if (userChoice.get() == connectBtnType) {
        model.callSupporterDirect(supporter.getAddress(),
            supporter.getPort(), supporter.isEncrypted());
      }
      return wasSaved;
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
    setNameValid(!isEmpty(nameEditFld.getText()));
  }

  public boolean isEditMode() {
    return editMode.get();
  }

  public BooleanProperty editModeProperty() {
    return editMode;
  }

  public void setEditMode(boolean editMode) {
    this.editMode.set(editMode);
  }
}
