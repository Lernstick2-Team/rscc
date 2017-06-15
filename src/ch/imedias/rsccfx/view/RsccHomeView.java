package ch.imedias.rsccfx.view;

import ch.imedias.rsccfx.localization.Strings;
import ch.imedias.rsccfx.model.Rscc;
import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory;
import java.io.InputStream;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Defines all elements shown on the start page.
 */
public class RsccHomeView extends BorderPane {
  private static final Logger LOGGER =
      Logger.getLogger(RsccHomeView.class.getName());
  private static final Insets BOX_INSETS = new Insets(10, 25, 10, 40);
  final HeaderView headerView;
  final Button requestViewBtn = new Button();
  final Button supportViewBtn = new Button();

  final VBox contentBox = new VBox();
  final HBox requestBox = new HBox();
  final VBox requestBoxLabels = new VBox();
  final HBox supportBox = new HBox();
  final VBox supportBoxLabels = new VBox();

  private final Rscc model;
  final Strings strings = new Strings();

  Image requestImg;
  Image supportImg;
  ImageView requestImgView;
  ImageView supportImgView;
  Label requestBigLbl = new Label();
  Label requestSmallLbl = new Label();
  Label supportBigLbl = new Label();
  Label supportSmallLbl = new Label();

  /**
   * Initializes all the GUI components needed on the start page.
   *
   * @param model the model to handle the data.
   */
  public RsccHomeView(Rscc model) {
    this.model = model;
    headerView = new HeaderView(model);
    SvgImageLoaderFactory.install();
    initFieldData();
    layoutForm();
    bindFieldsToModel();
  }

  private void initFieldData() {
    requestBigLbl.textProperty().set(strings.homeRequestBigLbl);
    requestSmallLbl.textProperty().set(strings.homeRequestSmallLbl);

    supportBigLbl.textProperty().set(strings.homeSupportBigLbl);
    supportSmallLbl.textProperty().set(strings.homeSupportSmallLbl);
  }

  private void layoutForm() {

    requestBigLbl.getStyleClass().add("BigFont");
    requestSmallLbl.getStyleClass().add("SmallFont");

    InputStream requestHelpImagePath = getClass().getClassLoader()
        .getResourceAsStream("images/needHelp.svg");
    requestImg = new Image(requestHelpImagePath);
    requestImgView = new ImageView(requestImg);
    requestImgView.setPreserveRatio(true);
    requestBoxLabels.getChildren().addAll(requestBigLbl, requestSmallLbl);
    requestBoxLabels.setAlignment(Pos.CENTER_LEFT);
    requestBoxLabels.getStyleClass().add("boxLabels");

    requestBox.getChildren().addAll(requestImgView, requestBoxLabels);
    requestBox.getStyleClass().add("homeButtonBoxes");
    requestBox.setPadding(BOX_INSETS);

    requestViewBtn.setGraphic(requestBox);
    requestViewBtn.getStyleClass().add("HomeNavigationBtn");
    supportBigLbl.getStyleClass().add("BigFont");
    supportSmallLbl.getStyleClass().add("SmallFont");

    InputStream offerSupportImagePath = getClass().getClassLoader()
        .getResourceAsStream("images/support.svg");
    supportImg = new Image(offerSupportImagePath);
    supportImgView = new ImageView(supportImg);
    supportImgView.setPreserveRatio(true);
    supportBoxLabels.getChildren().addAll(supportBigLbl, supportSmallLbl);
    supportBoxLabels.setAlignment(Pos.CENTER_LEFT);
    supportBoxLabels.getStyleClass().add("boxLabels");

    supportBox.getChildren().addAll(supportImgView, supportBoxLabels);
    supportBox.getStyleClass().add("homeButtonBoxes");
    supportBox.setPadding(BOX_INSETS);

    supportViewBtn.setGraphic(supportBox);
    supportViewBtn.getStyleClass().add("HomeNavigationBtn");

    contentBox.setId("contentBox");
    contentBox.setAlignment(Pos.CENTER);
    contentBox.getChildren().addAll(requestViewBtn, supportViewBtn);

    this.setTop(headerView);
    this.setCenter(contentBox);
  }

  private void bindFieldsToModel() {
    // make bindings to the model
  }
}
