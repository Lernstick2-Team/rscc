package ch.imedias.rsccfx.view;

import ch.imedias.rsccfx.RsccApp;
import ch.imedias.rsccfx.localization.Strings;
import ch.imedias.rsccfx.model.Rscc;
import java.util.Optional;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.controlsfx.control.ToggleSwitch;

/**
 * Creates the DialogPane for the expert settings.
 */
public class ExpertSettingsDialog extends DialogPane {

  private static final Logger LOGGER =
      Logger.getLogger(ExpertSettingsDialog.class.getName());

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
  final Button addServer = new Button("+");
  final Button removeServer = new Button("-");
  final Button loadDefaults = new Button();
  final ListView<String> stunServersList = new ListView<>();

  final Dialog dialog = new Dialog();
  final GridPane settingsPane = new GridPane();

  private final Strings strings = new Strings();
  private final Rscc model;


  /**
   * Initializes all the GUI components needed in the DialogPane.
   *
   * @param model The model is needed in the constructor.
   */
  public ExpertSettingsDialog(Rscc model) {
    this.model = model;
    this.getStylesheets().add(RsccApp.styleSheet);
    initFieldData();
    layoutForm();
    attachEvents();

    Optional<ButtonType> result = dialog.showAndWait();
    if (result.isPresent()) {
      if (result.get() == ButtonType.APPLY) {
        save();
      }
    }


  }

  private void attachEvents() {
    addServer.setOnAction(e -> stunServersList.getItems().add("new Stun Server"));
    removeServer.setOnAction(e -> stunServersList.getItems()
        .removeAll(stunServersList.getSelectionModel().getSelectedItems()));
  }


  private void initFieldData() {
    // populate fields which require initial data
    forceConnectOverServerLbl.setText(strings.expertForceConnectOverServerLbl);
    keyserverIpLbl.setText(strings.expertKeyserverIpLbl);
    keyServerHttpPortLbl.setText(strings.expertKeyserverHttpLbl);
    vncPortLbl.setText(strings.expertVncPortLbl);
    icePortLbl.setText(strings.expertIcePortLbl);
    udpPackageSizeLbl.setText(strings.expertUdpPackageSizeLbl);
    proxyPortLbl.setText(strings.expertProxyPortLbl);
    stunServersLbl.setText(strings.expertStunserverLbl);
    stunServerPortLbl.setText(strings.expertStunServerPortLbl);
    loadDefaults.setText(strings.editDialogDefaultButtonToolTipText);
    
    forceConnectOverServerTgl.setSelected(model.isForcingServerMode());
    keyServerIpFld.setText(model.getKeyServerIp());
    keyServerHttpPortFld.setText(model.getKeyServerHttpPort());
    vncPortFld.setText(Integer.toString(model.getVncPort()));
    icePortFld.setText(Integer.toString(model.getIcePort()));
    udpPackageSizeFld.setText(Integer.toString(model.getUdpPackageSize()));
    proxyPortFld.setText(Integer.toString(model.getProxyPort()));
    stunServerPortFld.setText(Integer.toString(model.getStunServerPort()));
    for (String server : model.getStunServers()) {
      stunServersList.getItems().add(server);
    }
    stunServersList.setEditable(true);
    stunServersList.setCellFactory(TextFieldListCell.forListView());

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
    settingsPane.add(new HBox(addServer, removeServer), 1, 10);
    settingsPane.add(loadDefaults, 0, 11);


    this.getButtonTypes().add(ButtonType.APPLY);
    this.getButtonTypes().add(ButtonType.CANCEL);
    this.setContent(settingsPane);
    dialog.setDialogPane(this);
  }


  private void save() {
    model.setForcingServerMode(forceConnectOverServerTgl.isSelected());
    model.setKeyServerIp(keyServerIpFld.getText());
    model.setKeyServerHttpPort(keyServerHttpPortFld.getText());
    model.setVncPort(Integer.parseInt(vncPortFld.getText()));
    model.setIcePort(Integer.parseInt(icePortFld.getText()));
    model.setUdpPackageSize(Integer.parseInt(udpPackageSizeFld.getText()));
    model.setProxyPort(Integer.parseInt(proxyPortFld.getText()));
    model.setStunServerPort(Integer.parseInt(stunServerPortFld.getText()));
    String[] stunServers = (String[]) stunServersList.getItems().toArray();
    model.setStunServers(stunServers);
    model.saveUserPreferences();
  }


}


