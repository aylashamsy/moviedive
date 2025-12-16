package com.deuceng.moviedive.service.impl;

import com.deuceng.moviedive.entity.LoginHistory;
import com.deuceng.moviedive.repository.LoginHistoryRepository;
import com.deuceng.moviedive.service.ILoginHistoryService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
public class LoginHistoryServiceImpl implements ILoginHistoryService
{
    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

    @Transactional
    public void recordLogin(Long userId, String sessionId) {
        LoginHistory lh = new LoginHistory();
        lh.setUserId(userId);
        lh.setSessionId(sessionId);
        lh.setLoginAt(LocalDateTime.now());
        lh.setLogoutAt(null);
        loginHistoryRepository.save(lh);
    }

    @Transactional
    public void markLogout(String sessionId) {
        loginHistoryRepository.markLogout(sessionId);
    }
}
