package com.example.doan.Service;

import java.util.Optional;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.doan.Model.users;
import com.example.doan.Repository.UsersRepository;

@Service
public class UserService {
    @Autowired
    private UsersRepository usersRepository;

    public void userVerify(String token) {
        Optional<users> OptionalUser = usersRepository.findByTokenVerify(token);

        if (OptionalUser.isPresent()) {
            users u = OptionalUser.get();
            u.setIsVerify(true);
            usersRepository.save(u);
        } else {
            throw new RuntimeException("Invalid Token");
        }
    }
}
