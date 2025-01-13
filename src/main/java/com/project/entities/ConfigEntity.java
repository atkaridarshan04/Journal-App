package com.project.entities;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "config")
@Data
public class ConfigEntity {
    String key;
    String value;
}
