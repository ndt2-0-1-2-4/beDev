package com.example.doan.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.doan.Model.Message;

import jakarta.transaction.Transactional;

@Repository
public interface MessageRepo extends JpaRepository<Message, Integer> {
    List<Message> findByIdMyAndIdFriendOrIdMyAndIdFriend(int idMy, int idFriend, int idFriend2, int idMy2,
            org.springframework.data.domain.Pageable pageable);

    @Modifying
    @Transactional
    @Query("DELETE FROM Message m WHERE m.idMy = :userId OR m.idFriend = :userId")
    void deleteAllMessagesByUser(@Param("userId") int userId);
}
