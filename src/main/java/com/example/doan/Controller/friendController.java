package com.example.doan.Controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.doan.Model.friend;
import com.example.doan.Model.users;
import com.example.doan.Repository.UsersRepository;
import com.example.doan.Repository.friendRepository;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
@RequestMapping("friend")
@RestController
public class friendController {
    
    @Autowired
    private friendRepository friendRepository;

    @Autowired
    private UsersRepository usersRepository;
    
    @PostMapping("/addFriend")
    public ResponseEntity<?> addFriend(@RequestBody friend request) {
        Integer idMy = request.getIdMy();
        Integer idFriend = request.getIdFriend(); 
        Map<String,Object> response=new HashMap<>();
        if(idMy == idFriend){
            response.put("status", "error");
        }
        else{
            request.setRelative("Đã gửi");
            friendRepository.save(request);
            response.put("status", "success");
        }

        return ResponseEntity.ok(response);
    }

    
    
    
    @PostMapping("/acceptFriend")
    public ResponseEntity<?> acceptFriend(@RequestBody friend request) {
        int idMy = request.getIdMy();
        int idFriend = request.getIdFriend();
        Map<String ,Object> response=new HashMap<>();
        Optional<friend> f=friendRepository.findByIdMyAndIdFriend(idMy,idFriend);
        if(f.isPresent() && f.get().getRelative().equals("Đã gửi")){
            friend friendAccpted=f.get();
            friendAccpted.setRelative("Bạn bè");
            friendRepository.save(friendAccpted);
            response.put("status", "success");
            return ResponseEntity.ok(response);
        }
        else{
            response.put("status", "error");
            return ResponseEntity.ok(response);
        }
    }
        
        
    @PostMapping("/getListFriend")
    public ResponseEntity<?> getListFriend(@RequestBody friend request) {
        Integer idMy = request.getIdMy();
        List<friend> friends= friendRepository.findAcceptedFriends(idMy);
        List<users> InfoFriends=new ArrayList<>();
        Map<String , Object> response= new HashMap<>();
        if(friends != null){
            for (friend friend : friends) {
                int UserFriend;
                if(friend.getIdMy() !=idMy){
                    UserFriend=friend.getIdMy();
                }
                else{
                    UserFriend=friend.getIdFriend();
                }
                Optional<users> user=usersRepository.findById(UserFriend);
                users u= new users();
                u.setFullname(user.get().getFullname());
                u.setId(UserFriend);
                InfoFriends.add(u);
            }
            return ResponseEntity.ok(InfoFriends);
        }
        else{
            response.put("status", "error");
            return ResponseEntity.ok(response);
        }
    
    }


    @PostMapping("/getFriendRequests")
    public ResponseEntity<?> getRequests(@RequestBody friend request) {
    Integer idMy = request.getIdMy();
    Map<String ,Object> response = new HashMap<>();
    List<users> InfoFriends=new ArrayList<>();
    List<friend> friendInvites= friendRepository.findByIdFriendAndRelative(idMy, "Đã gửi"); 
    if(!friendInvites.isEmpty()){
        for (friend friend : friendInvites) {
            int UserFriend;
            if(friend.getIdMy() !=idMy){
                UserFriend=friend.getIdMy();
            }
            else{
                UserFriend=friend.getIdFriend();
            }
            Optional<users> user=usersRepository.findById(UserFriend);
            users u= new users();
            u.setFullname(user.get().getFullname());
            u.setId(UserFriend);
            InfoFriends.add(u);
        }
        return ResponseEntity.ok(InfoFriends);
    }
    else{
        response.put("error", "Không có lời mời kết bạn nào");
        return ResponseEntity.ok(response);
    }
}






    @DeleteMapping("/deleteFriend")
    public ResponseEntity<Map<String, Object>> deleteFriend(@RequestBody friend request) {
        int idMy = request.getIdMy();
        int idFriend = request.getIdFriend();
        Map<String, Object> response = new HashMap<>();
        Optional<friend> f = friendRepository.findDeleteFriend(idMy, idFriend);
        if (f.isPresent() && f.get().getRelative().equals("Bạn bè")) {
            friendRepository.delete(f.get());
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            return ResponseEntity.ok(response);
        }
    }

    @DeleteMapping("/deleteFriendRequest")
    public ResponseEntity<Map<String, Object>> deleteFriendRequest(@RequestBody friend request) {
        int idMy = request.getIdMy();
        int idFriend = request.getIdFriend();
        Map<String ,Object> response=new HashMap<>();
        Optional<friend> f=friendRepository.findByIdMyAndIdFriend(idMy,idFriend);
        if(f.isPresent() && f.get().getRelative().equals("Đã gửi")){
            friendRepository.delete(f.get());
            response.put("status", "success");
            return ResponseEntity.ok(response);
        }
        else{
            response.put("status", "error");
            return ResponseEntity.ok(response);
        }
    }
}
