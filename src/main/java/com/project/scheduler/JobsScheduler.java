package com.project.scheduler;

import com.project.app_cache.AppCache;
import com.project.entities.JournalEntity;
import com.project.entities.UserEntity;
import com.project.services.EmailService;
import com.project.repositories.UserRepoImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class JobsScheduler {

    private final EmailService emailService;
    private final UserRepoImpl userRepoImpl;
    private final AppCache appCache;

    public JobsScheduler(EmailService emailService, UserRepoImpl userRepoImpl, AppCache appCache) {
        this.emailService = emailService;
        this.userRepoImpl = userRepoImpl;
        this.appCache = appCache;
    }

    @Scheduled(cron = "0 0 9 * * SUN") // every sunday at 9 am
    public void fetchUsersAndSendMail() {
        List<UserEntity> users = userRepoImpl.getUserForSA();
        for (UserEntity user : users) {
            List<JournalEntity> journalEntries = user.getJournalEntries();
            List<String> filteredContent = journalEntries.stream().filter(x -> x.getDate().isAfter(LocalDateTime.now().minusDays(7))).map(JournalEntity::getTitle).toList();
            String entry = String.join(" ", filteredContent);

            emailService.sendMail(user.getEmail(),
                    "Sentiment Analysis for last 7 days",
                    entry
            );
        }
    }

    @Scheduled(cron = "0 0/10 * ? * *") // every 10 minutes
    public void refreshAppCache(){
        appCache.init();
    }
}
