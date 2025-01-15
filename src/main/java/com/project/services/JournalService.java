package com.project.services;

import com.project.dto.JournalDTO;
import com.project.entities.JournalEntity;
import com.project.entities.UserEntity;
import com.project.repositories.JournalRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class JournalService {

    private final JournalRepo journalRepo;
    private final UserService userService;

    @Autowired
    public JournalService(JournalRepo journalRepo, UserService userService) {
        this.journalRepo = journalRepo;
        this.userService = userService;

    }

    @Transactional
    public JournalEntity saveEntry(JournalDTO entry, String username) {
        try {
            JournalEntity journalEntity = new JournalEntity();
            journalEntity.setTitle(entry.getTitle());
            journalEntity.setContent(entry.getContent());
            journalEntity.setSentiment(entry.getSentiment());
            journalEntity.setDate(LocalDateTime.now());
            JournalEntity saved = journalRepo.save(journalEntity);

            UserEntity user = userService.findUserByUsername(username);
            user.getJournalEntries().add(saved);
            userService.saveUser(user);

            return saved;
        } catch (Exception e) {
            log.error("Exception", e);
            return null;
        }
    }

    @Transactional
    public JournalEntity updateEntry(String id, JournalDTO entry) {
        try {
            ObjectId objectId = new ObjectId(id);
            JournalEntity journalEntity = journalRepo.findById(objectId)
                    .orElseThrow(() -> new IllegalArgumentException("Journal entry not found"));

            journalEntity.setTitle(ObjectUtils.defaultIfNull(entry.getTitle(), journalEntity.getTitle()));
            journalEntity.setContent(ObjectUtils.defaultIfNull(entry.getContent(), journalEntity.getContent()));
            journalEntity.setSentiment(entry.getSentiment());

            return journalRepo.save(journalEntity);
        } catch (Exception e) {
            log.error("Exception while updating journal entry", e);
            return null;
        }
    }


    public List<JournalEntity> getAllEntry() {
        return journalRepo.findAll();
    }

    public Optional<JournalEntity> getEntryById(ObjectId id) {
        return journalRepo.findById(id);
    }

    @Transactional
    public boolean deleteEntryById(ObjectId id, String username) {
        boolean removed = false;
        try {
            UserEntity user = userService.findUserByUsername(username);
            removed = user.getJournalEntries().removeIf(x -> x.getId().equals(id));
            if (removed) {
                userService.saveUser(user);
                journalRepo.deleteById(id);
            }
        } catch (Exception e) {
            log.error("Error occurred", e);
        }
        return removed;
    }

}
