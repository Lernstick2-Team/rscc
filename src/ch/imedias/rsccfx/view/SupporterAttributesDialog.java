package ch.imedias.rsccfx.view;

import ch.imedias.rsccfx.RsccApp;
import ch.imedias.rsccfx.localization.Strings;
import ch.imedias.rsccfx.model.xml.Supporter;
import ch.imedias.rsccfx.view.util.NumberTextField;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
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

  final Dialog dialog = new Dialog();
  final GridPane attributePane = new GridPane();
  final Label descriptionLbl = new Label();
  final Label addressLbl = new Label();
  final Label portLbl = new Label();
  final Label pictureLbl = new Label();
  final Label chargeableLbl = new Label();
  final Label encryptedLbl = new Label();
  final TextField nameFld = new TextField();
  final TextField addressFld = new TextField();
  final NumberTextField portFld = new NumberTextField();
  final TextField pictureFld = new TextField();
  final ButtonType applyBtnType = ButtonType.APPLY;
  final ButtonType cancelBtnType = ButtonType.CANCEL;
  final CheckBox chargeableCBox = new CheckBox();
  final CheckBox encryptedCBox = new CheckBox();
  Alert alert;
  Strings strings = new Strings();
  private Supporter supporter;

  /**
   * Initializes all the GUI components needed in the DialogPane.
   *
   * @param supporter the supporter for the dialog.
   */
  public SupporterAttributesDialog(Supporter supporter) {
    // TODO: 4K usw.?
    this.getStylesheets().add(RsccApp.styleSheet);
    this.supporter = supporter;
    initFieldData();
    layoutForm();
    createSupporterDialog();
    setupBindings();
    // TODO: Validate that a description has been entered, else 2 + buttons can be created

  }

  private void initFieldData() {
    // populate fields which require initial data
    dialog.setTitle(strings.dialogTitleText);
    descriptionLbl.setText(strings.dialogNameText);
    addressLbl.setText(strings.dialogAddressText);
    portLbl.setText(strings.dialogPortText);
    pictureLbl.setText(strings.dialogImageText);
    chargeableLbl.setText(strings.dialogChargeableLbl);
    encryptedLbl.setText(strings.dialogEncryptedLbl);

    nameFld.setText(supporter.getDescription());
    addressFld.setText(supporter.getAddress());
    portFld.setText(String.valueOf(supporter.getPort()));
    pictureFld.setText("/images/sup.jpg");
    chargeableCBox.setSelected(supporter.isChargeable());
    encryptedCBox.setSelected(supporter.isEncrypted());

    alert = new Alert(Alert.AlertType.INFORMATION,
        strings.supporterNameInformationDialog, ButtonType.OK);

  }

  private void saveData() {
    supporter.setDescription(nameFld.getText());
    supporter.setAddress(addressFld.getText());
    supporter.setPort(Integer.valueOf(portFld.getText()));
    supporter.setEncrypted(encryptedCBox.isSelected());
    supporter.setChargeable(chargeableCBox.isSelected());
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

    attributePane.add(descriptionLbl, 0, 0);
    attributePane.add(nameFld, 1, 0);
    attributePane.add(addressLbl, 0, 1);
    attributePane.add(addressFld, 1, 1);
    attributePane.add(portLbl, 0, 2);
    attributePane.add(portFld, 1, 2);
    attributePane.add(pictureLbl, 0, 3);
    attributePane.add(pictureFld, 1, 3);
    attributePane.add(chargeableLbl, 0, 4);
    attributePane.add(chargeableCBox, 1, 4);
    attributePane.add(encryptedLbl, 0, 5);
    attributePane.add(encryptedCBox, 1, 5);

    this.getButtonTypes().addAll(cancelBtnType, applyBtnType);

    this.setContent(attributePane);
    dialog.setDialogPane(this);
  }

  private void setupBindings() {
    descriptionLbl.textProperty().addListener(
        (observable, oldValue, newValue) ->
            dialog.getDialogPane().lookupButton(applyBtnType).setDisable("".equals(newValue))
    );
  }

  private boolean validateName() {
    if (nameFld.getText().trim().isEmpty()) {
      return false;
    }
    return true;
  }

  private void createSupporterDialog() {
    dialog.showAndWait()
        .filter(response -> response == applyBtnType)
        .ifPresent(response -> saveData());
  }
}
