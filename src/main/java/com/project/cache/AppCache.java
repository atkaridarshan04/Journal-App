package com.project.cache;

import com.project.entities.ConfigEntity;
import com.project.repositories.ConfigRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppCache {
    public enum keys {
        WEATHER_API;
    }

    private final ConfigRepo configRepo;

    @Autowired
    private AppCache(ConfigRepo configRepo) {
        this.configRepo = configRepo;
    }

    public Map<String, String> cache;

    @PostConstruct
    public void init() {
        cache = new HashMap<>();
        List<ConfigEntity> all = configRepo.findAll();
        for (ConfigEntity configEntity : all) {
            cache.put(configEntity.getKey(), configEntity.getValue());
        }
    }
}
