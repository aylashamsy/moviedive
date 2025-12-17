package com.deuceng.moviedive.service.impl;

import com.deuceng.moviedive.dto.DtoUser;
import com.deuceng.moviedive.dto.RequestLogin;
import com.deuceng.moviedive.entity.Role;
import com.deuceng.moviedive.entity.User;
import com.deuceng.moviedive.exception.BaseException;
import com.deuceng.moviedive.exception.ErrorMessage;
import com.deuceng.moviedive.exception.MessageType;
import com.deuceng.moviedive.repository.ActiveSessionsRepository;
import com.deuceng.moviedive.repository.LoginHistoryRepository;
import com.deuceng.moviedive.repository.UserRepository;
import com.deuceng.moviedive.service.IActiveSessionsService;
import com.deuceng.moviedive.service.ILoginHistoryService;
import com.deuceng.moviedive.service.IUserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IActiveSessionsService activeSessionService;

    @Autowired
    private ILoginHistoryService loginHistoryService;

    @Override
    public DtoUser getUserById(Long id) {
        DtoUser dtoUser = new DtoUser();
        if(userRepository.existsById(id))
        {
            User user=userRepository.findById(id).orElseThrow(() ->
                    new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST," Kayıt bulunamadı",null)));
            BeanUtils.copyProperties(user,dtoUser);
        }
        else
        {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST,"Böyle bir kullanıcı yok",id));
        }
        return dtoUser;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public DtoUser register(User user)
    {
        if(userRepository.findByEmail(user.getEmail()).isPresent())
        {
            throw new BaseException(new ErrorMessage(MessageType.USER_ALREADY_EXISTS," Bu email zaten kullanılıyor ",user.getEmail()));
        }
        userRepository.save(user);
        DtoUser DtoUser = new DtoUser();
        BeanUtils.copyProperties(user,DtoUser);
        return DtoUser;
    }

    @Override
    public String saveUsers(List<DtoUser> users) {

        List<User> list = users.stream().map(dtoUser -> {
            User u = userRepository.findByEmail(dtoUser.getEmail())
                    .orElseGet(User::new); // varsa id dolu gelir -> update
            // DTO değerlerini her durumda kopyala (update için şart)
            BeanUtils.copyProperties(dtoUser, u, "id");  // id'yi ezme
            u.setPassword(dtoUser.getPassword());
            return u;
        }).toList();

        userRepository.saveAll(list);
        return "Toplu yükleme başarı ile sonuçlandı";
    }

    @Override
    public String login(RequestLogin request, HttpSession session)
    {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST," Kayıt bulunamadı",request.getEmail())));

        if(!user.getPassword().equals(request.getPassword()))
        {
            throw(new BaseException(new ErrorMessage(MessageType.PASSWORD_INCORRECT,"Hatalı şifre",request.getEmail())));
        }
        session.setAttribute("USER_ID",user.getId());
        session.setAttribute("ROLE", user.getRole()); // "ADMIN" / "VIP" / "USER"
        String sessionId = session.getId();
        loginHistoryService.recordLogin(user.getId(), sessionId);
        activeSessionService.upsertOnLogin(sessionId, user.getId());
        return "login successful";
    }

    @Override
    public User me(HttpSession session)
    {
        // Session hiç yoksa
        if (session == null) {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Session does not exist", session));
        }
        Long id = (Long) session.getAttribute("EMPLOYEE_ID");

        // Session var ama login olunmamışsa
        if (id == null) {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "User not logged in", id));
        }
        return userRepository.findById(id).get();
    }

    @Override
    public String logout(HttpSession session)
    {
        String sessionId = session.getId();
        activeSessionService.remove(sessionId);
        loginHistoryService.markLogout(sessionId);
        //Session’ın TAMAMINI yok eder
        session.invalidate();
        return "logout successful";
    }

    private void requireAdmin(HttpSession session) {
        Role role = (Role) session.getAttribute("ROLE");
        if (role == null || role != role.ADMIN) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, "Admin required",role));
        }
    }

    private void requireVip(HttpSession session) {
        Role role = (Role) session.getAttribute("ROLE");
        if (role == null || (role!=role.VIP) ) {
            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, "VIP required",role));
        }
    }
}



