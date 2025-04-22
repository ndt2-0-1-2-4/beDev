package com.example.doan.Repository;

import java.lang.classfile.ClassFile.Option;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.doan.Model.historyBalance;

import jakarta.transaction.Transactional;

@Repository
public interface HisBalanceRepo extends JpaRepository<historyBalance, Integer> {
    @Query(value = "SELECT id_player, timechange, content, trans, balance " +
            "FROM historybalance " +
            "WHERE id_player = :idPlayer " +
            "ORDER BY timechange DESC ", nativeQuery = true)
    List<Object[]> findTop5ByIdPlayer(@Param("idPlayer") int idPlayer);

     // xóa tất cả lịch sử giao dịch của người chơi
    @Modifying
    @Transactional
    @Query("DELETE FROM historyBalance h WHERE h.idPlayer = :userId")
    void deleteAllByUser(@Param("userId") int userId);

    @Query("SELECT SUM(h.trans) FROM historyBalance h WHERE h.idPlayer = :idPlayer AND h.content LIKE %:content%")
    Float sumTotalDepositByIdAndContent(@Param("idPlayer") String idPlayer, @Param("content") String content);

    // @Query("SELECT h FROM historyBalance h WHERE h.idPlayer = :idPlayer AND h.content LIKE %:content%")
    // List<historyBalance> findAllByIdPlayerAndContent(@Param("idPlayer") String idPlayer, @Param("content") String content);


}
