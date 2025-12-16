package com.deuceng.moviedive.service.impl;

import com.deuceng.moviedive.entity.ActiveSession;
import com.deuceng.moviedive.repository.ActiveSessionsRepository;
import com.deuceng.moviedive.service.IActiveSessionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActiveSessionServiceImpl implements IActiveSessionsService
{
    @Autowired
    private ActiveSessionsRepository activeSessionRepository;

    // login sırasında upsert mantığı
    @Transactional
    public void upsertOnLogin(String sessionId, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        ActiveSession s = activeSessionRepository.findById(sessionId).orElseGet(ActiveSession::new);
        s.setSessionId(sessionId);
        s.setUserId(userId);

        if (s.getLoginAt() == null) s.setLoginAt(now);
        s.setLastSeenAt(now);

        activeSessionRepository.save(s);
    }

    // her request'te lastSeen güncelle
    @Transactional
    public void touch(String sessionId, Long userId) {
        LocalDateTime now = LocalDateTime.now();
        ActiveSession s = activeSessionRepository.findById(sessionId).orElse(null);

        if (s == null) {
            // nadiren: session var ama DB'de yoksa yeniden oluştur
            s = new ActiveSession();
            s.setSessionId(sessionId);
            s.setUserId(userId);
            s.setLoginAt(now);
        }
        s.setLastSeenAt(now);
        activeSessionRepository.save(s);
    }

    @Transactional
    public void remove(String sessionId) {
        activeSessionRepository.deleteBySessionId(sessionId);
    }

    public List<ActiveSession> getOnlineUsers(int minutes) {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(minutes);
        return activeSessionRepository.findOnlineSince(cutoff);
    }
}
