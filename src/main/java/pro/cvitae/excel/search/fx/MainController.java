package pro.cvitae.excel.search.fx;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.cvitae.excel.search.fx.service.ExcelSearchService;

public class MainController extends GenericController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @FXML
    public Button btnSelCarpetaExcel;

    @FXML
    public TextField folderPathInput;

    @FXML
    public Label labelContadorExcel;

    @FXML
    public ProgressIndicator progressIndicator;

    @FXML
    public TextArea statusTextArea;

    List<File> currentFileList = null;

    @FXML
    public void selectFolderModal(ActionEvent event) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Seleccionar ubicación de ficheros Excel");
        File folder = dirChooser.showDialog(this.getStage());
        if (folder != null) {
            folderSelected(folder);
        }
    }

    @FXML
    public void test(ActionEvent event) {
        ExcelSearchService service = new ExcelSearchService();
        service.restart();
        progressIndicator.progressProperty().bind(service.progressProperty());
        statusTextArea.textProperty().bind(service.messageProperty());
    }

    public void folderSelected(File folder) {
        folderPathInput.setText(folder.getAbsolutePath());
        this.currentFileList = new ArrayList<>();
        this.currentFileList.addAll(getAllExcelFiles(folder));
        int size = this.currentFileList.size();
        switch (size) {
            case 0:
                this.labelContadorExcel.setText("No hay ficheros compatibles");
                break;
            case 1:
                this.labelContadorExcel.setText("1 fichero detectado");
                break;
            default:
                this.labelContadorExcel.setText(size + " ficheros detectados");
                break;
        }

    }

    public List<File> getAllExcelFiles(File folder) {
        List<File> result = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            Optional<String> optExtension = getExtension(fileEntry.getName());
            if (optExtension.isPresent()) {
                if ("xlsx".equals(optExtension.get())) {
                    result.add(fileEntry);
                }
            }
        }

        progressIndicator.setProgress(33.33);

        return result;
    }

    public static Optional<String> getExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }
}
