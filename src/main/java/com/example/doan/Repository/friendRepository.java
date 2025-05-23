package com.example.doan.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.doan.Model.friend;

import jakarta.transaction.Transactional;

public interface friendRepository extends JpaRepository<friend, Integer> {
      Optional<friend> findByIdMyAndIdFriend(int idMy, int idFriend);

      List<friend> findIdFriendByIdMy(int idMy);

      List<friend> findByIdMyAndRelative(Integer idMy, String relative);
      // List<friend> deleteAllByUser(int userId);
      List<friend> findByIdFriendAndRelative(Integer idFriend, String relative);
      @Query("SELECT f FROM friend f WHERE (f.idMy = :userId OR f.idFriend = :userId) AND f.relative = 'Bạn bè'")
      List<friend> findAcceptedFriends(@Param("userId") int userId);
      List<friend> findByIdFriendAndRelative(int idFriend,String relative);

      Optional<friend> findRelativeByIdMyAndIdFriend(Integer idMy, Integer idFriend);

      @Query("SELECT  f FROM friend f where ((f.idMy= :id1 and f.idFriend= :id2) or (f.idMy= :id2 and f.idFriend= :id1)) and relative='Bạn bè'")
      Optional<friend> findDeleteFriend(@Param("id1") int id1, @Param("id2") int Id2);

      @Query("select f from friend f where  (f.idMy= :id1 and f.idFriend= :id2) or(f.idMy= :id2 and f.idFriend= :id1)")
      Optional<friend> getRelavtiveUser(@Param("id1") int id1,@Param("id2") int id2);
}
