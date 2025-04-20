package com.example.doan.Controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.doan.Model.friend;
import com.example.doan.Repository.UsersRepository;
import com.example.doan.Repository.friendRepository;


import java.util.ArrayList;
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
public ResponseEntity<Map<String, String>> addFriend(@RequestBody friend request) {
    Integer idMy = request.getIdMy();
    Integer idFriend = request.getIdFriend(); 

    if (idMy == null || idFriend == null) {
        return ResponseEntity.badRequest().body(Map.of("message", "ID không hợp lệ."));
    }

    // Kiểm tra xem hai người đã là bạn bè hay chưa
    friend existingFriend = friendRepository.findByIdMyAndIdFriend(idMy, idFriend);
    if (existingFriend != null && "Bạn bè".equals(existingFriend.getRelative())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(Map.of("message", "Hai bạn đã là bạn bè!"));
    }

    // Kiểm tra nếu đã gửi lời mời trước đó
    if (existingFriend != null && "Đang chờ".equals(existingFriend.getRelative())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(Map.of("message", "Lời mời đã được gửi trước đó!"));
    }

    // Thêm lời mời kết bạn hai chiều
    friend friendRequest1 = new friend();
    friendRequest1.setIdMy(idMy);
    friendRequest1.setIdFriend(idFriend);
    friendRequest1.setRelative("Đang chờ");

    friend friendRequest2 = new friend();
    friendRequest2.setIdMy(idFriend);
    friendRequest2.setIdFriend(idMy);
    friendRequest2.setRelative("Xác nhận");

    friendRepository.save(friendRequest1);
    friendRepository.save(friendRequest2);

    return ResponseEntity.ok(Map.of("message", "Gửi kết bạn thành công!"));
}

    
    
    
@PostMapping("/acceptFriend")
public ResponseEntity<Map<String, String>> acceptFriend(@RequestBody friend request) {
    Integer idMy = request.getIdMy();
    Integer idFriend = request.getIdFriend();

    if (idMy == null || idFriend == null) {
        return ResponseEntity.badRequest().body(Map.of("message", "ID không hợp lệ."));
    }

    // Tìm lời mời kết bạn theo idMy -> idFriend
    friend friendRequest = friendRepository.findByIdMyAndIdFriend(idMy, idFriend);

    if (friendRequest == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(Map.of("message", "Lời mời kết bạn không tồn tại hoặc đã bị xóa."));
    }

    // Cập nhật trạng thái thành "Bạn bè"
    friendRequest.setRelative("Bạn bè");
    friendRepository.save(friendRequest);

    // Kiểm tra quan hệ ngược (idFriend -> idMy)
    friend reverseFriend = friendRepository.findByIdMyAndIdFriend(idFriend, idMy);
    if (reverseFriend == null) {
        // Nếu chưa có, tạo mới
        friend newFriend = new friend();
        newFriend.setIdMy(idFriend);
        newFriend.setIdFriend(idMy);
        newFriend.setRelative("Bạn bè");
        friendRepository.save(newFriend);
    } else {
        // Nếu đã tồn tại nhưng chưa phải "Bạn bè", cập nhật
        if (!"Bạn bè".equals(reverseFriend.getRelative())) {
            reverseFriend.setRelative("Bạn bè");
            friendRepository.save(reverseFriend);
        }
    }

    return ResponseEntity.ok(Map.of("message", "Chấp nhận kết bạn thành công!"));
}
    
    
@PostMapping("/getListFriend")
public ResponseEntity<?> getListFriend(@RequestBody friend request) {
    Integer idMy = request.getIdMy();

    // Lấy danh sách bạn bè 2 chiều
    List<Object[]> friendNames = friendRepository.findFriendNamesById(idMy);

     // Chuyển đổi danh sách Object[] thành danh sách Map
     List<Map<String, Object>> result = new ArrayList<>();
     for (Object[] row : friendNames) {
         Map<String, Object> friendData = new HashMap<>();
         friendData.put("name", row[1]);
         friendData.put("id", row[0]);
         result.add(friendData);
     }
 
     return ResponseEntity.ok(result);
}


@PostMapping("/getFriendRequests")
public ResponseEntity<?> getRequests(@RequestBody friend request) {
    Integer idMy = request.getIdMy();

    List<Object[]> pendingRequests = friendRepository.findFriendNamesByIdAndRelative(idMy);

    if (pendingRequests.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không có lời mời kết bạn nào.");
    }

    
    // Chuyển đổi danh sách Object[] thành danh sách Map
    List<Map<String, Object>> result = new ArrayList<>();
    for (Object[] row : pendingRequests) {
        Map<String, Object> friendData = new HashMap<>();
        friendData.put("name", row[1]);
        friendData.put("id", row[0]);
        result.add(friendData);
    }

    return ResponseEntity.ok(result);

    // return ResponseEntity.ok(pendingRequests);
}






@DeleteMapping("/deleteFriend")
public ResponseEntity<Map<String, Object>> deleteFriend(@RequestBody friend request) {
    Integer idMy = request.getIdMy();
    Integer idFriend = request.getIdFriend();

    if (idMy == null || idFriend == null) {
        return ResponseEntity.badRequest().body(Map.of("message", "ID không hợp lệ.", "status", "error"));
    }

    friend friend1 = friendRepository.findByIdMyAndIdFriend(idMy, idFriend);
    friend friend2 = friendRepository.findByIdMyAndIdFriend(idFriend, idMy);

    if (friend1 == null || friend2 == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Bạn bè không tồn tại.", "status", "not_found"));
    }

    if ("Bạn bè".equals(friend1.getRelative()) && "Bạn bè".equals(friend2.getRelative())) {
        friendRepository.delete(friend1);
        friendRepository.delete(friend2);
        return ResponseEntity.ok(Map.of("message", "Bạn bè đã bị xóa.", "status", "success"));
    }

    return ResponseEntity.badRequest().body(Map.of("message", "Không thể xóa bạn bè, trạng thái không hợp lệ.", "status", "error"));
}

@DeleteMapping("/deleteFriendRequest")
public ResponseEntity<Map<String, Object>> deleteFriendRequest(@RequestBody friend request) {
    Integer idMy = request.getIdMy();
    Integer idFriend = request.getIdFriend();

    if (idMy == null || idFriend == null) {
        return ResponseEntity.badRequest().body(Map.of("message", "ID không hợp lệ.", "status", "error"));
    }

    friend friendRequest1 = friendRepository.findByIdMyAndIdFriend(idMy, idFriend);
    friend friendRequest2 = friendRepository.findByIdMyAndIdFriend(idFriend, idMy);

    if (friendRequest1 == null || friendRequest2 == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Lời mời kết bạn không tồn tại.", "status", "not_found"));
    }

    if ("Đang chờ".equals(friendRequest1.getRelative()) && "Xác nhận".equals(friendRequest2.getRelative())) {
        friendRepository.delete(friendRequest1);
        friendRepository.delete(friendRequest2);
        return ResponseEntity.ok(Map.of("message", "Lời mời kết bạn đã bị hủy.", "status", "success"));
    }

    return ResponseEntity.badRequest()
            .body(Map.of("message", "Không thể hủy lời mời, trạng thái không hợp lệ.", "status", "error"));
    }

    @PostMapping("/getRelative")
    public ResponseEntity<?> getRelativeFr(@RequestBody friend friend) {
        Optional<friend> f = friendRepository.findRelativeByIdMyAndIdFriend(friend.getIdMy(), friend.getIdFriend());
        if(f.isPresent()){
            return ResponseEntity.ok(f.get());
        }
        return ResponseEntity.ok(null);
    }
}
