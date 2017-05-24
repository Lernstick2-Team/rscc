package ch.imedias.rsccfx.view;

import ch.imedias.rsccfx.RsccApp;
import ch.imedias.rsccfx.localization.Strings;
import ch.imedias.rsccfx.model.Rscc;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.converter.NumberStringConverter;
import org.controlsfx.control.ToggleSwitch;

/**
 * Creates the DialogPane for the expert settings.
 */
public class ExpertSettingsDialog extends DialogPane {

  final Label keyserverIpLbl = new Label();
  final Label forceConnectOverServerLbl = new Label();
  final Label keyServerHttpPortLbl = new Label();
  final Label vncPortLbl = new Label();
  final Label icePortLbl = new Label();
  final Label udpPackageSizeLbl = new Label();
  final Label proxyPortLbl = new Label();
  final Label stunServerPortLbl = new Label();
  final Label stunServersLbl = new Label();

  final ToggleSwitch forceConnectOverServerTgl = new ToggleSwitch();

  final TextField keyServerIpFld = new TextField();
  final TextField keyServerHttpPortFld = new TextField();
  final TextField vncPortFld = new TextField();
  final TextField icePortFld = new TextField();
  final TextField udpPackageSizeFld = new TextField();
  final TextField proxyPortFld = new TextField();
  final TextField stunServerPortFld = new TextField();

  final ListView stunServersList = new ListView();

  final Dialog dialog = new Dialog();

  final GridPane settingsPane = new GridPane();

  private final Strings strings = new Strings();
  private final Rscc model;


  /**
   * Initializes all the GUI components needed in the DialogPane.
   */
  public ExpertSettingsDialog(Rscc model) {
    this.model = model;
    this.getStylesheets().add(RsccApp.styleSheet);
    initFieldData();
    layoutForm();
    bindFieldsToModel();
    dialog.show();
  }

  private void initFieldData() {
    // populate fields which require initial data
    forceConnectOverServerLbl.textProperty().set(strings.expertForceConnectOverServerLbl);
    keyserverIpLbl.textProperty().set(strings.expertKeyserverIpLbl);
    keyServerHttpPortLbl.textProperty().set(strings.expertKeyserverHttpLbl);
    vncPortLbl.textProperty().set(strings.expertVncPortLbl);
    icePortLbl.textProperty().set(strings.expertIcePortLbl);
    udpPackageSizeLbl.textProperty().set(strings.expertUdpPackageSizeLbl);
    proxyPortLbl.textProperty().set(strings.expertProxyPortLbl);
    stunServersLbl.textProperty().set(strings.expertStunserverLbl);
    stunServerPortLbl.textProperty().set(strings.expertStunServerPortLbl);
  }

  private void layoutForm() {
    //setup layout (aka setup specific pane etc.)
    settingsPane.setHgap(20);
    settingsPane.setVgap(10);
    settingsPane.setPadding(new Insets(25));
    dialog.setResizable(true);
    dialog.setHeight(500);
    dialog.setWidth(500);
    dialog.setTitle(strings.expertSettingsDialogTitle);

    settingsPane.getStyleClass().add("settingsPane");

    forceConnectOverServerTgl.getStyleClass().add("toggles");

    settingsPane.add(forceConnectOverServerLbl, 0, 1);
    settingsPane.add(forceConnectOverServerTgl, 1, 1);
    settingsPane.add(keyserverIpLbl, 0, 2);
    settingsPane.add(keyServerIpFld, 1, 2);
    settingsPane.add(keyServerHttpPortLbl, 0, 3);
    settingsPane.add(keyServerHttpPortFld, 1, 3);
    settingsPane.add(vncPortLbl, 0, 4);
    settingsPane.add(vncPortFld, 1, 4);
    settingsPane.add(icePortLbl, 0, 5);
    settingsPane.add(icePortFld, 1, 5);
    settingsPane.add(udpPackageSizeLbl, 0, 6);
    settingsPane.add(udpPackageSizeFld, 1, 6);
    settingsPane.add(proxyPortLbl, 0, 7);
    settingsPane.add(proxyPortFld, 1, 7);
    settingsPane.add(stunServerPortLbl, 0, 8);
    settingsPane.add(stunServerPortFld, 1, 8);
    settingsPane.add(stunServersLbl, 0, 9);
    settingsPane.add(stunServersList, 1, 9);

    this.getButtonTypes().add(ButtonType.APPLY);
    this.getButtonTypes().add(ButtonType.CLOSE);
    this.getButtonTypes().add(ButtonType.PREVIOUS);


    this.setContent(settingsPane);
    dialog.setDialogPane(this);
  }

  private void bindFieldsToModel() {
    // make bindings to the model
    forceConnectOverServerTgl.selectedProperty().bindBidirectional(
        model.isForcingServerModeProperty());
    keyServerIpFld.textProperty().bindBidirectional(model.keyServerIpProperty());
    keyServerHttpPortFld.textProperty().bindBidirectional(model.keyServerHttpPortProperty());
    vncPortFld.textProperty().bindBidirectional(model.vncPortProperty(),
        new NumberStringConverter("#"));
    icePortFld.textProperty().bindBidirectional(model.icePortProperty(),
        new NumberStringConverter("#"));
    // TODO: These properties were missing - please check if they are alright.
    udpPackageSizeFld.textProperty().bindBidirectional(model.udpPackageSizeProperty(),
        new NumberStringConverter("#"));
    proxyPortFld.textProperty().bindBidirectional(model.proxyPortProperty(),
        new NumberStringConverter("#"));
    stunServerPortFld.textProperty().bindBidirectional(model.stunServerPortProperty(),
        new NumberStringConverter("#"));
    // TODO: Stunserver-List needs to be binded
  }

}


