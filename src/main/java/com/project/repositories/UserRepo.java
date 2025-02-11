package com.project.repositories;

import com.project.entities.UserEntity;
import jakarta.validation.constraints.NotBlank;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface UserRepo extends MongoRepository<UserEntity, ObjectId> {
    UserEntity findByUsername(String username);

    void deleteUserByUsername(String username);

    boolean existsByUsername(@NotBlank(message = "Username is mandatory") String username);
}