package com.project.scheduler;

import com.project.cache.AppCache;
import com.project.entities.JournalEntity;
import com.project.entities.UserEntity;
import com.project.enums.Sentiment;
import com.project.services.EmailService;
import com.project.repositories.UserRepoImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            List<Sentiment> sentiments = journalEntries.stream().filter(x -> x.getDate().isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS))).map(JournalEntity::getSentiment).toList();

            Map<Sentiment, Integer> sentimentCounts = new HashMap<>();
            for (Sentiment sentiment : sentiments) {
                if (sentiment != null)
                    sentimentCounts.put(sentiment, sentimentCounts.getOrDefault(sentiment, 0) + 1);
            }

            Sentiment mostFrequentSentiment = null;
            int maxCount = 0;
            for (Map.Entry<Sentiment, Integer> entry : sentimentCounts.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    mostFrequentSentiment = entry.getKey();
                }
            }
            if (mostFrequentSentiment != null) {
                emailService.sendMail(user.getEmail(), "Sentiment for previous week", mostFrequentSentiment.toString());
            }
        }
    }

    @Scheduled(cron = "0 0/10 * ? * *") // every 10 minutes
    public void refreshAppCache(){
        appCache.init();
    }
}
