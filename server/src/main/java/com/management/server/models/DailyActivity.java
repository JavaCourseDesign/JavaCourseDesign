package com.management.server.models;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class DailyActivity extends Event{
    private String type;//体育活动、外出旅游、文艺演出、聚会
}
