/**
 * 
 */
package org.migdb.migdbclient.utils;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;

/**
 * @author KANI
 *
 */
public class ToggleSwitch extends Label {
	
	private SimpleBooleanProperty switchedOn = new SimpleBooleanProperty(true);

    public ToggleSwitch(boolean isSwitched, String id, String trueText, String falseText)
    {
        Button switchBtn = new Button();
        switchBtn.setId(id);
        switchBtn.setPrefWidth(60);
        switchBtn.setPrefHeight(20);
        switchBtn.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent t)
            {
                switchedOn.set(!switchedOn.get());
            }
        });

        setGraphic(switchBtn);

        switchedOn.addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov,
                Boolean t, Boolean t1)
            {
            	String btnId = switchBtn.getId();
            	
                if (t1)
                {
                	//Embedding side
                	System.out.println(btnId+" - Embed");
                    setText(trueText);
                    setStyle("-fx-background-color: #237f4f;-fx-text-fill:white;");
                    setContentDisplay(ContentDisplay.RIGHT);
                }
                else
                {
                	//Referencing side
                	System.out.println(btnId+" - Reference");
                    setText(falseText);
                    setStyle("-fx-background-color: #91bfa7;-fx-text-fill:black;");
                    setContentDisplay(ContentDisplay.LEFT);
                }
            }
        });

        switchedOn.set(isSwitched);
    }

    /*public SimpleBooleanProperty switchOnProperty() { return switchedOn; }*/

}
