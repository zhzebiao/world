package main;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import service.ReportGenService;
import util.LogUtil;

import java.io.File;


public final class Main extends Application {


    private static final Log LOG = LogFactory.get();

    private ReportGenService reportGenService = new ReportGenService();


    @Override
    public void start(final Stage stage) {
        stage.setTitle("肺功能PDF报告生成器");

        final DirectoryChooser directoryChooser = new DirectoryChooser();

        final TextField folderPath = new TextField();
        final Button searchButton = new Button("运行");

        final TextArea logTextArea = new TextArea();

        LogUtil.setLogTextArea(logTextArea);
        folderPath.setEditable(false);
        folderPath.setPromptText("选择文件夹...");
        folderPath.setOnMouseClicked(
                event -> {
                    File file = directoryChooser.showDialog(stage);
                    if (file != null) {
                        folderPath.setText(file.getAbsolutePath());
                        LogUtil.info("已选择文件夹：" + file.getAbsolutePath());
                        LOG.info("已选择文件夹：" + file.getAbsolutePath());
                        reportGenService.setReportsFloder(file);
                    }
                }
        );
        searchButton.setOnMouseClicked(
                event -> {
                    reportGenService.execute();
                }
        );


        final GridPane inputGridPane = new GridPane();
        GridPane.setConstraints(folderPath, 0, 0);
        GridPane.setConstraints(searchButton, 1, 0);
        inputGridPane.setHgap(6);
        inputGridPane.setVgap(6);
        inputGridPane.getChildren().addAll(folderPath, searchButton);


        final Pane logPane = new Pane();

        logPane.getChildren().addAll(logTextArea);
        logTextArea.setPrefWidth(710);

        final Pane rootGroup = new VBox(12);
        rootGroup.getChildren().addAll(inputGridPane, logPane);
        rootGroup.setPadding(new Insets(12, 12, 12, 12));

        stage.setScene(new Scene(rootGroup));
        stage.setResizable(false);
        stage.show();
        searchButton.requestFocus();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}