package com.project.repositories;

import com.project.entities.JournalEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface JournalRepo extends MongoRepository<JournalEntity, ObjectId> {
}
