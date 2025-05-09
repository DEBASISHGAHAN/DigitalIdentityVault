package com.project.digitalidentityvault.service;

import com.project.digitalidentityvault.entity.User;
import com.project.digitalidentityvault.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SchedulerService {
    private final UserRepository userRepository;
    private final MailService mailService;

    @Value("${scheduler.inactivity.days}")
    private long inactivityDays;

    @Value("${spring.mail.username}")
    private String fromMail;

    @Scheduled(cron = "0 0 10 * * ?")
    public void sendInactivityNotifications(){
        log.info("Inactivity Notifier Started: Checking for inactive users...");
        LocalDateTime inactivityThreshold = LocalDateTime.now().minusDays(inactivityDays);
        List<User> inactiveUsers = userRepository.findByLastActiveAtBeforeAndVerifiedTrue(inactivityThreshold);
        for (User user : inactiveUsers){
            sendInactivityEmail(user);
        }
        log.info("Inactivity Notifier Completed: {} users notified.", inactiveUsers.size());
    }

    private void sendInactivityEmail(User user) {
        String subject = "Digital Identity Vault - Inactivity Alert";
        String message = "Hello " + user.getEmail() + ",\n\n" +
                "We noticed you haven't accessed your Digital Identity Vault for " + inactivityDays +
                " days. Please login to keep your account active.\n\nThank you!";
        mailService.sendOtpMail(user.getEmail(), subject, message);
    }
}
