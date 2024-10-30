package com.management.server.controllers;

import com.management.server.models.Lesson;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.LessonRepository;
import com.management.server.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class LessonController {
    @Autowired
    private LessonRepository lessonRepository;
    @PostMapping("/uploadPPT")
    public DataResponse uploadPPT(@RequestBody byte[] barr,
                                  @RequestParam(name = "fileName") String fileName,
                                  @RequestParam(name="paras") String eventId){
        Lesson lesson=lessonRepository.findByEventId(eventId);
        if(lesson.getPpt()!=null){
            FileUtil.deleteFile("ppt",lesson.getPpt());
        }
        lesson.setPpt(fileName);
        lessonRepository.save(lesson);
        return FileUtil.uploadFile(barr,"ppt",fileName);
    }
    @PostMapping("/downloadPPT")
    public DataResponse downloadPPT(@RequestBody Map m){
        String eventId=(String) m.get("eventId");
        Lesson lesson=lessonRepository.findByEventId(eventId);
        if(lesson.getPpt()==null)
        {
            return new DataResponse(-1,null,"未上传PPT");
        }
        return FileUtil.downloadFile("ppt",lesson.getPpt());
    }

}
