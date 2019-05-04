/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pro.cvitae.excel.search.fx.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 *
 * @author mikel
 */
public class ExcelSearchService extends Service<Double> {

    @Override
    protected Task<Double> createTask() {
        return new Task<Double>() {
            @Override
            protected Double call() throws Exception {
                //DO YOU HARD STUFF HERE
                Double value = 0d;
                StringBuilder sb = new StringBuilder("Starting...");
                while(true || value < Double.MAX_VALUE) {
                    sb.insert(0,"Line number " + value + "\n");
                    this.updateProgress(value++, 100);
                    this.updateMessage(sb.toString());
                    Thread.sleep(100);
                    
                }

                return value;
            }
        };
    }

}
