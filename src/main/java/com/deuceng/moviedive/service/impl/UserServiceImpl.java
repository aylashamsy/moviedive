package com.deuceng.moviedive.service.impl;

import com.deuceng.moviedive.dto.DtoUser;
import com.deuceng.moviedive.dto.RequestLogin;
import com.deuceng.moviedive.entity.Role;
import com.deuceng.moviedive.entity.User;
import com.deuceng.moviedive.exception.BaseException;
import com.deuceng.moviedive.exception.ErrorMessage;
import com.deuceng.moviedive.exception.MessageType;
import com.deuceng.moviedive.repository.UserRepository;
import com.deuceng.moviedive.service.IUserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;
    private final SessionAuthService sessionAuthService;

    @Override
    public DtoUser getUserById(Long id) {
        DtoUser dtoUser = new DtoUser();
        User user=new User();
        if(userRepository.existsById(id))
        {
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
    public DtoUser saveUser(User user)
    {
        if(userRepository.findByEmail(user.getEmail())!=null)
        {
            throw new BaseException(new ErrorMessage(MessageType.USER_ALREADY_EXISTS,"Bu email zaten kullanılıyor",user.getEmail()));
        }
        userRepository.save(user);
        DtoUser DtoUser = new DtoUser();
        BeanUtils.copyProperties(user,DtoUser);
        return DtoUser;
    }

    @Override
    public String saveUsers(List<User> users)
    {
//        int i=0;
//        if(userRepository.findByEmail(users.get(i).getEmail())!=null)
//        {
//            throw new BaseException(new ErrorMessage(MessageType.USER_ALREADY_EXISTS,"Bu email zaten kullanılıyor",user.getEmail()));
//        }
        userRepository.saveAll(users);

        return "Toplu yükleme başarı ile sonuçlandı";
    }

    @Override
    public String login(RequestLogin request, HttpSession session)
    {
        User user = userRepository.findByEmail(request.getEmail());
        if(user == null)
        {
            throw(new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST,"Kayıt bulunamadı",request.getEmail())));
        }
        if(!user.getPassword().equals(request.getPassword()))
        {
            throw(new BaseException(new ErrorMessage(MessageType.PASSWORD_INCORRECT,"Hatalı şifre",request.getEmail())));
        }
        session.setAttribute("USER_ID",user.getId());
        session.setAttribute("ROLE", user.getRole()); // "ADMIN" / "VIP" / "USER"
        return "login successful";
    }

    @Override
    public User me(HttpSession session)
    {
        Long id = sessionAuthService.requireUserId(session);

        return userRepository.findById(id)
                .orElseThrow(() -> new BaseException(
                        new ErrorMessage(MessageType.NO_RECORD_EXIST, "User not found", id)
                ));
    }

    @Override
    public String logout(HttpSession session)
    {
        //Session’ın TAMAMINI yok eder
        session.invalidate();
        return "logout successful";
    }

//    private void requireAdmin(HttpSession session) {
//        Role role = (Role) session.getAttribute("ROLE");
//        if (role == null || role != role.ADMIN) {
//            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, "Admin required",role));
//        }
//    }
//
//    private void requireVip(HttpSession session) {
//        Role role = (Role) session.getAttribute("ROLE");
//        if (role == null || (role!=role.VIP) ) {
//            throw new BaseException(new ErrorMessage(MessageType.UNAUTHORIZED, "VIP required",role));
//        }
//    }
}



