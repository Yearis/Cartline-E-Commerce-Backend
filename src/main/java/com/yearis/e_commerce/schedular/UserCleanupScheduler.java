package com.yearis.e_commerce.schedular;

import com.yearis.e_commerce.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCleanupScheduler {

    private final UserRepository userRepository;

    @Scheduled(cron = "@midnight")
    @Transactional
    public void removeUnverifiedUsers() {

        // delete users who are unverified for more than 24 hrs
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);

        log.info("Running cleanup task: Deleting unverified users created before {}", cutoffTime);

        try {
            userRepository.deleteByIsVerifiedFalseAndCreatedAtBefore(cutoffTime);
            log.info("Cleanup completed. Junnk Users deleted");
        } catch (Exception e) {
            log.error("Failed to clean up users: {}", e.getMessage());
        }
    }
}
