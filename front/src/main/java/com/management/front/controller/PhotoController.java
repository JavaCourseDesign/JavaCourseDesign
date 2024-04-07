package com.management.front.controller;

import com.management.front.request.DataResponse;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.management.front.util.HttpClientUtil.request;

public class PhotoController {
    @FXML
    private Button displayButton;
    @FXML
    private ImageView photoArea;
    private String fileName;
    @FXML
    void initialize() {
        photoArea.setOnDragOver(event -> {
            if (event.getGestureSource() != photoArea && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        photoArea.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                try {
                    File file = db.getFiles().get(0);
                    byte[] fileContent = Files.readAllBytes(file.toPath());
                    String encodedString = Base64.getEncoder().encodeToString(fileContent);
                    Map<String,String> m=new HashMap<>();
                    fileName=file.getName();
                    m.put("fileName", fileName);
                    m.put("fileContent", encodedString);
                   DataResponse r = request("/uploadPhoto", m);
                    if(r.getCode() == 0) {
                        // handle success
                    } else {
                        // handle error
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }
    @FXML
    void onDisplayButton()
    {
        Map<String,String> m=new HashMap<>();
        m.put("fileName","");
        DataResponse r=request("/getPhotoImageStr",m);
        if(r.getCode()==0)
        {
            String base64Image = r.getData().toString();
            byte[] decodedBytes = Base64.getDecoder().decode(base64Image);
            ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes);
            Image image = new Image(bis);
            photoArea.setImage(image);
        }
    }

}
