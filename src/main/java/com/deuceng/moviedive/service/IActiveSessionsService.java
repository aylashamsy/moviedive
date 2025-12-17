package com.deuceng.moviedive.service;

import com.deuceng.moviedive.entity.ActiveSession;

import java.util.List;

public interface IActiveSessionsService
{
    public void upsertOnLogin(String sessionId, Long userId);
    public void touch(String sessionId, Long userId) ;
    public List<ActiveSession> getOnlineUsers(int minutes);
    public void remove(String sessionId) ;
}
