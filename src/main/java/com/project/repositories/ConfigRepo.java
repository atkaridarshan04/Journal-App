package com.project.repositories;

import com.project.entities.ConfigEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConfigRepo extends MongoRepository<ConfigEntity, ObjectId> {
}
