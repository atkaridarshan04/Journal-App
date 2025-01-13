package com.project.services;

import com.project.entities.JournalEntity;
import com.project.entities.UserEntity;
import com.project.repositories.JournalRepo;
import lombok.extern.slf4j.Slf4j;
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
    public JournalService(JournalRepo journalRepo, UserService userService){
        this.journalRepo = journalRepo;
        this.userService = userService;

    }

    @Transactional
    public void saveEntry(JournalEntity entry, String username) {
        try {
            UserEntity user = userService.findUserByUsername(username);
            entry.setDate(LocalDateTime.now());
            JournalEntity saved = journalRepo.save(entry);
            user.getJournalEntries().add(saved);
            userService.saveUser(user);
        } catch (Exception e) {
            log.error("Exception", e);
        }
    }

    public void saveEntry(JournalEntity entry) {
        try {
            entry.setDate(LocalDateTime.now());
            journalRepo.save(entry);
        } catch (Exception e) {
            log.error("Exception", e);
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
            if (removed){
                userService.saveUser(user);
                journalRepo.deleteById(id);
            }
        } catch (Exception e){
            log.error("Error occurred", e);
        }
        return removed;
    }

}
