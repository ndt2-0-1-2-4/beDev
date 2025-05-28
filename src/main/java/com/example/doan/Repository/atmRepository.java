package com.example.doan.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.doan.Model.atm;
import com.example.doan.Model.users;

import jakarta.transaction.Transactional;

@Repository
public interface atmRepository extends JpaRepository<atm, Integer> {
    @Query("""
                SELECT u FROM users u
                WHERE u.isDelete = true
                  AND u.id IN (
                    SELECT a.idPlayer FROM atm a WHERE a.stk = :stk
                  )
            """)
    List<users> findDeletedUsersByStk(@Param("stk") String stk);
    Optional<atm> findByStk(String stk);

    Optional<atm> findByIdPlayer(int idPlayer);

    @Modifying
    @Transactional
    @Query("DELETE FROM atm a WHERE a.idPlayer = :id")
    void deleteByAtmId(@Param("id") int id);

    @Query("SELECT a FROM atm a WHERE a.user.id = :userId")
    Optional<atm> findByUserId(@Param("userId") Integer userId);

    @Query("SELECT a.balance FROM atm a WHERE a.idPlayer = :idPlayer")
    Integer findBalanceByIdPlayer(@Param("idPlayer") Integer idPlayer);
}
