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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import service.SearchService;
import util.LogUtil;

import java.io.File;


public final class Main extends Application {


    private static final Log LOG = LogFactory.get();

    private SearchService searchService = new SearchService();


    @Override
    public void start(final Stage stage) {
        stage.setTitle("File Searcher");

        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Excel", "*.*"),
                new FileChooser.ExtensionFilter("XLS", "*.xls"),
                new FileChooser.ExtensionFilter("XLSX", "*.xlsx"));
        final DirectoryChooser directoryChooser = new DirectoryChooser();

        final TextField excelPath = new TextField();
        final TextField folderPath = new TextField();
        final TextField startMonth = new TextField();
        final TextField endMonth = new TextField();
        final Button searchButton = new Button("运行");

        final TextArea logTextArea = new TextArea();

        LogUtil.setLogTextArea(logTextArea);

        excelPath.setEditable(false);
        excelPath.setPromptText("选择Excel文件...");
        excelPath.setOnMouseClicked(
                event -> {
                    File file = fileChooser.showOpenDialog(stage);
                    if (file != null) {
                        excelPath.setText(file.getAbsolutePath());
                        LogUtil.info("已选择Excel文件: " + file.getAbsolutePath());
                        LOG.info("已选择Excel文件: " + file.getAbsolutePath());
                        searchService.setXlsxFile(file);
                    }
                }
        );
        folderPath.setEditable(false);
        folderPath.setPromptText("选择文件夹...");
        folderPath.setOnMouseClicked(
                event -> {
                    File file = directoryChooser.showDialog(stage);
                    if (file != null) {
                        folderPath.setText(file.getAbsolutePath());
                        LogUtil.info("已选择文件夹：" + file.getAbsolutePath());
                        LOG.info("已选择文件夹：" + file.getAbsolutePath());
                        searchService.setReportsFloder(file);
                    }
                }
        );
        startMonth.setPromptText("开始月份");
        endMonth.setPromptText("结束月份");
        searchButton.setOnMouseClicked(
                event -> {
                    searchService.setStartMonth(startMonth.getText());
                    searchService.setEndMonth(endMonth.getText());
                    searchService.execute();
                }
        );


        final GridPane inputGridPane = new GridPane();
        GridPane.setConstraints(excelPath, 0, 0);
        GridPane.setConstraints(folderPath, 1, 0);
        GridPane.setConstraints(startMonth, 2, 0);
        GridPane.setConstraints(endMonth, 3, 0);
        GridPane.setConstraints(searchButton, 4, 0);
        inputGridPane.setHgap(6);
        inputGridPane.setVgap(6);
        inputGridPane.getChildren().addAll(excelPath, folderPath, startMonth, endMonth, searchButton);


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