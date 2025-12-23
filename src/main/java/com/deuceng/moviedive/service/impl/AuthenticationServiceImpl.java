package com.deuceng.moviedive.service.impl;

import com.deuceng.moviedive.dto.RegisterRequest;
import com.deuceng.moviedive.dto.RegisterResponse;
import com.deuceng.moviedive.dto.RequestLogin;
import com.deuceng.moviedive.entity.City;
import com.deuceng.moviedive.entity.Role;
import com.deuceng.moviedive.entity.User;
import com.deuceng.moviedive.exception.BaseException;
import com.deuceng.moviedive.exception.ErrorMessage;
import com.deuceng.moviedive.exception.MessageType;
import com.deuceng.moviedive.repository.CityRepository;
import com.deuceng.moviedive.repository.UserRepository;
import com.deuceng.moviedive.service.IActiveSessionsService;
import com.deuceng.moviedive.service.ILoginHistoryService;
import com.deuceng.moviedive.service.IAuthenticationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements IAuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IActiveSessionsService activeSessionService;

    @Autowired
    private ILoginHistoryService loginHistoryService;
    @Autowired
    private CityRepository cityRepository;

    @Override
    public RegisterRequest getUserById(Long id) {
        RegisterRequest dtoUpdateUserRequest = new RegisterRequest();
        if(userRepository.existsById(id))
        {
            User user= userRepository.findById(id).orElseThrow(() ->
                    new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST," Kayıt bulunamadı",null)));
            dtoUpdateUserRequest.setUsername(user.getUsername());
            dtoUpdateUserRequest.setPassword(user.getPassword());
            dtoUpdateUserRequest.setFirstname(user.getFirstname());
            dtoUpdateUserRequest.setSurname(user.getSurname());
            dtoUpdateUserRequest.setEmail(user.getEmail());
            dtoUpdateUserRequest.setPhoneNumber(user.getPhoneNumber());
            dtoUpdateUserRequest.setRole(user.getRole());
        }
        else
        {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST,"Böyle bir kullanıcı yok",id));
        }
        return dtoUpdateUserRequest;
    }

    @Override
    public RegisterResponse getUserByUserName(String username) {
        RegisterResponse dtoUpdateUserRequest = new RegisterResponse();
        if(userRepository.existsByUsername(username))
        {
            User user= userRepository.findByUsername(username).orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST," Kayıt bulunamadı",null)));
            dtoUpdateUserRequest.setUsername(user.getUsername());
            dtoUpdateUserRequest.setFirstname(user.getFirstname());
            dtoUpdateUserRequest.setSurname(user.getSurname());
            dtoUpdateUserRequest.setEmail(user.getEmail());
            dtoUpdateUserRequest.setPhoneNumber(user.getPhoneNumber());
            dtoUpdateUserRequest.setRole(user.getRole());
        }
        else
        {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST,"Böyle bir kullanıcı yok",username));
        }
        return dtoUpdateUserRequest;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public RegisterResponse register(RegisterRequest dtoUser) {

        if (dtoUser == null) {
            throw new BaseException(new ErrorMessage(
                    MessageType.GENERAL_EXCEPTION,
                    "Bir değer girmediniz",
                    null
            ));
        }

        if (userRepository.findByEmail(dtoUser.getEmail()).isPresent()) {
            throw new BaseException(new ErrorMessage(
                    MessageType.USER_ALREADY_EXISTS,
                    "Bu email zaten kullanılıyor",
                    dtoUser.getEmail()
            ));
        }

        if (userRepository.findByUsername(dtoUser.getUsername()).isPresent()) {
            throw new BaseException(new ErrorMessage(
                    MessageType.USER_ALREADY_EXISTS,
                    "Bu username zaten kullanılıyor",
                    dtoUser.getUsername()
            ));
        }

        // ✅ cityId zorunlu (DTO'n böyle)
        if (dtoUser.getCityId() == null) {
            throw new BaseException(new ErrorMessage(
                    MessageType.GENERAL_EXCEPTION,
                    "City seçmelisiniz",
                    dtoUser.getEmail()
            ));
        }

        City city = cityRepository.findById(dtoUser.getCityId())
                .orElseThrow(() -> new BaseException(
                        new ErrorMessage(MessageType.GENERAL_EXCEPTION, "City bulunamadı", dtoUser.getCityId())
                ));

        User user = new User();
        // cityId map olmaz, o yüzden hariç tut
        BeanUtils.copyProperties(dtoUser, user, "cityId");

        user.setCity(city);

        // role default
        user.setRole(dtoUser.getRole() == null ? Role.USER : dtoUser.getRole());

        user.setPassword(dtoUser.getPassword());

        userRepository.save(user);

        // response
        RegisterResponse response = new RegisterResponse();
        BeanUtils.copyProperties(user, response);
        response.setCityName(city.getName());

        return response;
    }

    //Yardımcı method, tablo doldurmak için
    @Override
    public String saveUsers(List<RegisterRequest> users) {

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
    public boolean login(RequestLogin request, HttpSession session)
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
        return true;
    }

    @Override
    public User me(HttpSession session)
    {
        // Session hiç yoksa
        if (session == null) {
            throw new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, "Session does not exist", session));
        }
        Long id = (Long) session.getAttribute("USER_ID");

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

    public void deleteUser(Long id)
    {
        User user = userRepository.findById(id).orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST," Kayıt bulunamadı",id)));
        userRepository.delete(user);
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


