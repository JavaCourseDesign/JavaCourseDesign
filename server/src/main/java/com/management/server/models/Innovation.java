package com.management.server.models;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class Innovation extends Event{
    private String performance;//比赛，论文，项目等评价
    private String type;//1。社会实践2.学科竞赛3.科研成果4.培训讲座5.创新项目6.校外实习
}
