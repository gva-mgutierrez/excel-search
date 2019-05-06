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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.cvitae.excel.search.fx.service.ExcelSearchService;

public class MainController extends GenericController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @FXML
    private TextField folderPathInput;

    @FXML
    private Label labelContadorExcel;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private TextArea statusTextArea;

    @FXML
    private Button startButton;
    
    @FXML
    private TextArea searchTextArea;
    
    @FXML
    private ImageView image;
    
    @FXML
    private Label waitingLabel;
    
    @FXML
    private AnchorPane imagePane;

    private List<File> currentFileList = null;

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
    public void launchAction(ActionEvent event) {
        startButton.setVisible(false);
        progressIndicator.setVisible(true);
        waitingLabel.setVisible(true);
        
        int width = (int)this.image.getBoundsInLocal().getWidth();
        int height = (int)this.image.getBoundsInLocal().getHeight();
        
        this.image.setImage(new Image("https://source.unsplash.com/" + width + "x" + height + "/?river"));
        image.setVisible(true);
        
        ExcelSearchService service = new ExcelSearchService(getLinesToSearch(), this.currentFileList, this.folderPathInput.getText());
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
        
        this.checkActivateButton();
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
        return result;
    }

    public static Optional<String> getExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }
    
    private void checkActivateButton(){
        if(searchTextArea.getText().trim().equals("") || folderPathInput.getText().trim().equals("")
                || (this.currentFileList == null || this.currentFileList.isEmpty())){
            this.startButton.setDisable(true);
        } else {
            this.startButton.setDisable(false);
        }
    }
    
    @Override
    public void afterInitialization(){
        this.searchTextArea.textProperty().addListener((observable) ->{ checkActivateButton(); });
        // this is fired before currentFileList is set
        //this.folderPathInput.textProperty().addListener((observable) ->{ checkActivateButton(); });
        
        this.image.fitHeightProperty().bind(this.imagePane.heightProperty());
        this.image.fitWidthProperty().bind(this.imagePane.widthProperty());
    }
    
    private String[] getLinesToSearch(){
        return this.searchTextArea.getText().split("\\r?\\n");
    }
}
