package com.deuceng.moviedive.service;

public interface ILoginHistoryService
{
    public void markLogout(String sessionId);
    public void recordLogin(Long userId, String sessionId);
}
