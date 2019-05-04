/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pro.cvitae.excel.search.fx;

import javafx.stage.Stage;

/**
 *
 * @author mikel
 */
public abstract class GenericController {

    private Stage stage = null;

    public Stage getStage() {
        return this.stage;
    }

    public void setStage(final Stage stage) {
        if (this.stage != null) {
            throw new IllegalStateException("Uh oh! You cannot assign a stage. I already have one!");
        }

        this.stage = stage;
    }

    /**
     * This method is intented to do all job that cannot be done in the
     * <code>initialize</code> method.
     */
    public void afterInitialization() {
        // Empty by default
    }
}