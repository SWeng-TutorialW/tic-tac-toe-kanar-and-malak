/**
 * Sample Skeleton for 'init.fxml' Controller Class
 */

package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.Serializable;


import static il.cshaifasweng.OCSFMediatorExample.client.App.setRoot;
import static il.cshaifasweng.OCSFMediatorExample.client.SimpleClient.client;

public class InitController implements Serializable {
    @FXML // fx:id="host"
    private TextField host; // Value injected by FXMLLoader

    @FXML // fx:id="port"
    private TextField port; // Value injected by FXMLLoader

    @FXML
    void ready(ActionEvent event) {
        int portNumber;
        if (host.getText() == null || host.getText().isEmpty() || port.getText() == null || port.getText().isEmpty()) {
            Warning warning = new Warning("must fill all fields!");
            System.out.println("1111111111");
            EventBus.getDefault().post(new WarningEvent(warning));
        } else {
            try {
                portNumber = Integer.parseInt(port.getText());
                client = SimpleClient.getClient(host.getText(), portNumber);
                try {
                    client.openConnection();
                    client.sendToServer("ll");/////////////////////////////////////////////////////
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> {
                    try {
                        setRoot("primary");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (NumberFormatException e) {
                Warning warning = new Warning("Invalid port");
                EventBus.getDefault().post(new WarningEvent(warning));
            }
        }
    }
}
