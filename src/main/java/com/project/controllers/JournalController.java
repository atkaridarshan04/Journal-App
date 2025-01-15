package com.project.controllers;

import com.project.dto.JournalDTO;
import com.project.entities.JournalEntity;
import com.project.entities.UserEntity;
import com.project.services.JournalService;
import com.project.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/journal")
@Tag(name = "Journal APIs")
public class JournalController {

    private final JournalService journalEntryService;
    private final UserService userService;

    @Autowired
    public JournalController(JournalService journalEntryService, UserService userService){
        this.journalEntryService = journalEntryService;
        this.userService = userService;
    }

    @Operation(summary = "Get all Journal Entries by User")
    @GetMapping
    public ResponseEntity<List<JournalEntity>> getAllJournalsEntriesByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntity user = userService.findUserByUsername(username);
        List<JournalEntity> all = user.getJournalEntries();
        if (all != null && !all.isEmpty()) {
            return new ResponseEntity<>(all, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Create new Journal Entry")
    @PostMapping
    public ResponseEntity<JournalEntity> addJournalEntry(@RequestBody JournalDTO entry) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            JournalEntity journalEntity = journalEntryService.saveEntry(entry, username);
            return new ResponseEntity<>(journalEntity, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Get Journal Entry by ID", description = "Fetch a journal entry by its MongoDB ObjectId.")
    @GetMapping("/{id}")
    public ResponseEntity<JournalEntity> getJournalEntryById(@PathVariable String id) {
        ObjectId objId = new ObjectId(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntity user = userService.findUserByUsername(username);
        List<JournalEntity> collect = user.getJournalEntries().stream().filter(x -> x.getId().toString().equals(id)).toList();
        if (!collect.isEmpty()) {
            Optional<JournalEntity> journalEntry = journalEntryService.getEntryById(objId);
            if (journalEntry.isPresent()) {
                return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Operation(summary = "Delete Journal Entry")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJournalEntry(@PathVariable String id) {
        ObjectId objId = new ObjectId(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        boolean removed = journalEntryService.deleteEntryById(objId, username);
        if (removed) {
            return new ResponseEntity<>("Journal Deleted Successfully", HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Update Journal entry")
    @PutMapping("/{id}")
    public ResponseEntity<JournalEntity> updateJournalEntry(@PathVariable String id, @RequestBody JournalDTO entry) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntity user = userService.findUserByUsername(username);

        // Check if the particular entry is of that user only
        List<JournalEntity> collect = user.getJournalEntries().stream().filter(x -> x.getId().toString().equals(id)).toList();
        if (!collect.isEmpty()) {
            JournalEntity journalEntity = journalEntryService.updateEntry(id, entry);
            return new ResponseEntity<>(journalEntity, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
