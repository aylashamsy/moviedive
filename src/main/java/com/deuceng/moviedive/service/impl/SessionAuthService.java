package com.deuceng.moviedive.service.impl;

import com.deuceng.moviedive.entity.Role;
import com.deuceng.moviedive.exception.BaseException;
import com.deuceng.moviedive.exception.ErrorMessage;
import com.deuceng.moviedive.exception.MessageType;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class SessionAuthService {

    // Senin login() içinde set ettiğin key buydu:
    private static final String SESSION_USER_ID_KEY = "USER_ID";
    private static final String SESSION_ROLE_KEY = "ROLE";

    public Long requireUserId(HttpSession session) {
        if (session == null) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, "Session does not exist", null));
        }
        Long id = (Long) session.getAttribute(SESSION_USER_ID_KEY);
        if (id == null) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, "User not logged in", null));
        }
        return id;
    }

    public Role requireRole(HttpSession session) {
        if (session == null) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, "Session does not exist", null));
        }
        Role role = (Role) session.getAttribute(SESSION_ROLE_KEY);
        if (role == null) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, "Role not found in session", null));
        }
        return role;
    }

    public void requireAdmin(HttpSession session) {
        Role role = requireRole(session);
        if (role != Role.ADMIN) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, "Admin required", role));
        }
    }

    public void requireVip(HttpSession session) {
        Role role = requireRole(session);
        if (role != Role.VIP) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, "VIP required", role));
        }
    }
}
