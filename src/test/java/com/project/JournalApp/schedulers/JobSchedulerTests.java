package com.project.JournalApp.schedulers;

import com.project.scheduler.JobsScheduler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JobSchedulerTests {
    @Autowired
    private JobsScheduler jobsScheduler;

    @Test
    public void testFetchUsersAndSendMail(){
        jobsScheduler.fetchUsersAndSendMail();
    }
}
