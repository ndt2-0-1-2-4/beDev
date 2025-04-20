package com.example.doan.Controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.doan.Model.Message;
import com.example.doan.Repository.MessageRepo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.data.domain.Pageable;


@RestController
@RequestMapping("/mess")
public class MessageController {
    @Autowired
    private MessageRepo messageRepo;

    @PostMapping("getChatHis")
    public ResponseEntity<?> getChatHis(@RequestBody Message entity) {
        List<Message> listMsg= new ArrayList<>();
        int idMy=entity.getIdMy();
        int idFriend=entity.getIdFriend();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("timeSend").descending());
        listMsg=messageRepo.findByIdMyAndIdFriendOrIdMyAndIdFriend(idMy, idFriend, idFriend, idMy,pageable);
        return ResponseEntity.ok(listMsg);
    }
    

}
