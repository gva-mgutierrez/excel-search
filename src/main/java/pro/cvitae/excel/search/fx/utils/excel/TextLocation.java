/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pro.cvitae.excel.search.fx.utils.excel;

import java.io.File;

/**
 *
 * @author mikel
 */
public class TextLocation {
    
    private final File file;
    
    private final String searchedText;
    
    private final String cellContent;
    
    private final String sheet;
    
    private final int colIndex;
    
    private final int rowIndex;

    public TextLocation(File file, String searchedText, String cellContent, String sheet, int colIndex, int rowIndex) {
        this.file = file;
        this.searchedText = searchedText;
        this.cellContent = cellContent;
        this.sheet = sheet;
        this.colIndex = colIndex;
        this.rowIndex = rowIndex;
    }

    public File getFile() {
        return file;
    }

    public String getSearchedText() {
        return searchedText;
    }

    public String getCellContent() {
        return cellContent;
    }

    public String getSheet() {
        return sheet;
    }

    public int getColIndex() {
        return colIndex;
    }

    public int getRowIndex() {
        return rowIndex;
    }
    
    public String readableCell(){
        String colStr = Character.toString ((char) (65 + this.colIndex));
        return colStr + this.rowIndex;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(this.getFile().getName()).append("]")
                .append(this.getSheet())
                .append("!").append(this.readableCell())
                .append(" - ").append(this.getSearchedText());
        return sb.toString();
    }
    
    /*@Override
    public int compareTo(TextLocation tl) {
        if (this.file.getName().equals(tl.getFile().getName())){
            if ()
        } else {
            return this.file.getName().compareTo(tl.getFile().getName());
        }
    }*/    
}
