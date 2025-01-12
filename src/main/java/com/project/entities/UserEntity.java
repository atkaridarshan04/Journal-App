package com.project.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@Data
@NoArgsConstructor
public class UserEntity {
    @Id
    private ObjectId id;
    @Indexed(unique = true) // by default, it does not initialize indexing, so we add one more 1 in properties file
    @NonNull
    private String username;
    @NonNull
    private String password;
    private LocalDateTime Date;

    @DBRef
    private List<JournalEntity> journalEntries = new ArrayList<>();

    private List<String> roles;
}
