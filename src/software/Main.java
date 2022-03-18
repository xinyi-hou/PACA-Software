package software;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import software.controller.ChooseNICFrameController;

import java.io.IOException;


public class Main extends Application {
    double x, y = 0;
    private static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage stage) throws Exception {
        Main.stage = stage;
        initChooseNICFrame();
    }

    public void initChooseNICFrame() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/ChooseNICFrame.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.initStyle(StageStyle.UNDECORATED);
        //move around
        root.setOnMousePressed(evt -> {
            x = evt.getSceneX();
            y = evt.getSceneY();
        });
        root.setOnMouseDragged(evt -> {
            stage.setX(evt.getScreenX() - x);
            stage.setY(evt.getScreenY() - y);
        });
        stage.setScene(scene);
        ChooseNICFrameController controller = loader.getController();
        controller.setNICStage(stage);
        stage.getIcons().add(new Image("software\\view\\icons\\attack.png"));
        stage.show();
    }

    public Pane initScanHostFrame() {
        return loadFrame("view/ScanHostFrame.fxml");
    }

    public Pane initARPAttackFrame() {
        return loadFrame("view/ARPAttackFrame.fxml");
    }

    public Pane initDDoSAttackFrame(){
        return loadFrame("view/DDoSAttackFrame.fxml");
    }

    public Pane initPacketFrame(){
        return loadFrame("view/CapturePacketFrame.fxml");
    }

    public Pane initExtendFrame(){
        return loadFrame("view/ExtendFrame.fxml");
    }

    public Pane loadFrame(String name) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(name));
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

