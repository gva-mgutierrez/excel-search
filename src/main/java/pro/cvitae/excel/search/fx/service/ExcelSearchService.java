/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pro.cvitae.excel.search.fx.service;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pro.cvitae.excel.search.fx.utils.excel.TextLocation;

/**
 *
 * @author mikel
 */
public class ExcelSearchService extends Service<Double> {

    private final List<File> currentFileList;

    public ExcelSearchService(List<File> currentFileList) {
        this.currentFileList = currentFileList;
    }

    @Override
    protected Task<Double> createTask() {
        return new Task<Double>() {
            @Override
            protected Double call() throws Exception {
                int totalFiles = currentFileList.size();
                int done = 0;

                StringBuilder resultBuilder = new StringBuilder("Comenzando lectura de ")
                        .append(totalFiles)
                        .append(" ficheros");
                
                this.updateMessage(resultBuilder.toString());
                // +1 --> includes % for html file creation
                this.updateProgress(done, ++totalFiles);

                List<TextLocation> allLocations = new ArrayList<>();
                for (File excel : currentFileList) {
                    resultBuilder.insert(0, "Leyendo fichero " + excel.getName());
                    this.updateMessage(resultBuilder.toString());
                    
                    allLocations.addAll(processWorkbook(excel));
                    this.updateProgress(++done, totalFiles);
                }

                resultBuilder.insert(0, "Generando fichero de resultados");
                this.updateMessage(resultBuilder.toString());
                
                StringBuilder sb = new StringBuilder();
                for (TextLocation tl : allLocations) {
                    sb.append("<tr>");
                    sb.append(encloseTr(tl.getFile().getName()));
                    sb.append(encloseTr(tl.getSheet()));
                    sb.append(encloseTr(tl.readableCell()));
                    sb.append(encloseTr(tl.getSearchedText()));
                    sb.append(encloseTr(tl.getCellContent()));
                    sb.append("</tr>\n");
                }

                String template;
                try (InputStream is = this.getClass().getResourceAsStream("/listado.html")) {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")))) {
                        template = br.lines().collect(Collectors.joining(System.lineSeparator()));
                    }

                    template = template.replace("##DATE##", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
                    template = template.replace("##TABLE_BODY##", sb.toString());
                }

                String resultFileName = (new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date())) + ".html";
                try (FileOutputStream fos = new FileOutputStream(new File(resultFileName))) {
                    fos.write(template.getBytes("UTF-8"));
                }

                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new File(resultFileName).toURI());
                }
                return 0d;
            }
        };
    }

    public List<TextLocation> processWorkbook(File file) throws IOException {
        List<TextLocation> results = new ArrayList<>();
        try (Workbook wb = getWorkbook(file)) {
            CellStyle style = wb.createCellStyle();
            style.setFillForegroundColor(IndexedColors.LEMON_CHIFFON.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            //style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setWrapText(true);

            //backgroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            for (Sheet sheet : wb) {
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        if (cell.getCellType().equals(CellType.STRING)) {
                            String cellText = cell.getRichStringCellValue().getString().trim();
                            for (String text2Search : TEXTS) {
                                if (StringUtils.containsIgnoreCase(cellText, text2Search)) {
                                    TextLocation tl = new TextLocation(file, text2Search, cellText, sheet.getSheetName(),
                                            cell.getColumnIndex(), cell.getRowIndex());
                                    results.add(tl);
                                    cell.setCellStyle(style);
                                }
                            }
                        }
                    }
                }
            }

            try (FileOutputStream out = new FileOutputStream(file)) {
                wb.write(out);
            }
            return results;
        }
    }

    public String encloseTr(String td){
        return "<td class=\"mdl-data-table__cell--non-numeric\">" 
                + StringEscapeUtils.escapeHtml4(td) + "</td>";
    }

    public Workbook getWorkbook(File file) throws IOException {
        FileInputStream excelFile = new FileInputStream(file);
        return new XSSFWorkbook(excelFile);
    }

    public String[] TEXTS = new String[]{
        "Consentimiento",
        "Interesado",
        "Recursos humanos",
        "Fichero",
        "El personal"};
}
