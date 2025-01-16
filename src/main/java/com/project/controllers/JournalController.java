package com.project.controllers;

import com.project.dto.JournalDTO;
import com.project.entities.JournalEntity;
import com.project.entities.UserEntity;
import com.project.services.JournalService;
import com.project.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequestMapping("/journal")
@Tag(name = "Journal APIs")
public class JournalController {

    private final JournalService journalEntryService;
    private final UserService userService;

    @Autowired
    public JournalController(JournalService journalEntryService, UserService userService) {
        this.journalEntryService = journalEntryService;
        this.userService = userService;
    }

    @Operation(summary = "Get all Journal Entries by User")
    @GetMapping
    public ResponseEntity<List<JournalEntity>> getAllJournalsEntriesByUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            UserEntity user = userService.findUserByUsername(username);
            List<JournalEntity> all = user.getJournalEntries();
            if (all != null && !all.isEmpty()) {
                return new ResponseEntity<>(all, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.error("Error occurred while fetching journal entries", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // Server error, 500
        }
    }

    @Operation(summary = "Create new Journal Entry")
    @PostMapping
    public ResponseEntity<JournalEntity> addJournalEntry(@Valid @RequestBody JournalDTO entry) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            JournalEntity journalEntity = journalEntryService.saveEntry(entry, username);
            return new ResponseEntity<>(journalEntity, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get Journal Entry by ID", description = "Fetch a journal entry by its MongoDB ObjectId.")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getJournalEntryById(@PathVariable String id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            UserEntity user = userService.findUserByUsername(username);
            Optional<JournalEntity> journalEntry = user.getJournalEntries().stream().filter(x -> x.getId().toString().equals(id)).findFirst();
            if (journalEntry.isPresent()) {
                return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (IllegalArgumentException e) {
            // Handle invalid ObjectId format
            return new ResponseEntity<>("Invalid journal entry ID format.", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error occurred while fetching journal entry by ID", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Delete Journal Entry")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJournalEntry(@PathVariable String id) {
        try {
            ObjectId objId = new ObjectId(id);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            boolean removed = journalEntryService.deleteEntryById(objId, username);

            if (removed) {
                return new ResponseEntity<>("Journal entry deleted successfully.", HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>("Unauthorized to delete this entry.", HttpStatus.FORBIDDEN);
            }
        } catch (IllegalArgumentException e) {
            // Catch invalid ObjectId
            return new ResponseEntity<>("Invalid journal entry ID.", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error occurred during journal entry deletion", e);
            return new ResponseEntity<>("Failed to delete journal entry.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Operation(summary = "Update Journal entry")
    @PutMapping("/{id}")
    public ResponseEntity<JournalEntity> updateJournalEntry(@PathVariable String id, @Valid @RequestBody JournalDTO entry) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            UserEntity user = userService.findUserByUsername(username);

            // Check if the particular entry is of that user only
            Optional<JournalEntity> journal = user.getJournalEntries().stream().filter(x -> x.getId().toString().equals(id)).findFirst();
            if (journal.isPresent()) {
                JournalEntity journalEntity = journalEntryService.updateEntry(id, entry);
                return new ResponseEntity<>(journalEntity, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } catch (IllegalArgumentException e) {
//            When journal entry is not found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
