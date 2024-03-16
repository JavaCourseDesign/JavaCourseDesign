package com.management.front;

import com.management.front.request.DataRequest;
import com.management.front.request.DataResponse;
import com.management.front.request.HttpRequestUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        DataRequest req=new DataRequest();
        DataResponse res= HttpRequestUtil.request("/demoStudent",req);
        welcomeText.setText(res.getData().toString());
        
    }
}