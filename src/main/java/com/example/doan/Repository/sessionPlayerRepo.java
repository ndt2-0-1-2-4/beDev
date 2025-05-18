package com.example.doan.Repository;

import com.example.doan.Model.sessionPlayer;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.doan.Model.sessionPlayer;

public interface sessionPlayerRepo extends JpaRepository<sessionPlayer, Integer> {
  @Query(value = "SELECT id, namegame, playerid, timeoccurs, result, bet, reward, choice " +
      "FROM sessionplayer " +
      "WHERE playerid = :idPlayer " +
      "ORDER BY timeoccurs DESC ", nativeQuery = true)
  List<sessionPlayer> findTop5ByIdPlayer(@Param("idPlayer") int idPlayer);

  @Modifying
  @Transactional
  @Query("DELETE FROM sessionPlayer sp WHERE sp.playerid = :id")
  void deleteByPlayerId(@Param("id") int id);

  @Query(value = """
      SELECT SUM(h.bet)
      FROM sessionPlayer h
      WHERE h.playerid = :idPlayer
        AND (
              (h.namegame = 'Reng Reng' AND h.result = 'Thắng')
              OR
              (h.namegame = 'Tài xỉu' AND (
                  (CAST(h.result AS UNSIGNED) > 10 AND h.choice = 'tai') OR
                  (CAST(h.result AS UNSIGNED) < 10 AND h.choice = 'xiu')
              ))
            )
      """, nativeQuery = true)
  Integer sumBetWinAllGame(@Param("idPlayer") Integer idPlayer);

  @Query(value = """
      SELECT SUM(h.bet)
      FROM sessionPlayer h
      WHERE h.playerid = :idPlayer
        AND (
              (h.namegame = 'Reng Reng' AND h.result = 'Thua')
              OR
              (h.namegame = 'Tài xỉu' AND (
                  (CAST(h.result AS UNSIGNED) > 10 AND h.choice = 'xiu') OR
                  (CAST(h.result AS UNSIGNED) < 10 AND h.choice = 'tai')
              ))
            )
      """, nativeQuery = true)
  Integer sumBetLostAllGame(@Param("idPlayer") Integer idPlayer);

  @Query("SELECT SUM(h.bet) FROM sessionPlayer h WHERE h.playerid = :idPlayer AND h.result = 'Thua' AND h.namegame = 'Reng Reng'")
  Integer sumRengLost(@Param("idPlayer") Integer idPlayer);

  @Query("SELECT SUM(h.bet) FROM sessionPlayer h WHERE h.playerid = :idPlayer AND h.result = 'Thắng' AND h.namegame = 'Reng Reng'")
  Integer sumRengWin(@Param("idPlayer") Integer idPlayer);

  @Query(value = """
      SELECT SUM(h.bet)
      FROM sessionPlayer h
      WHERE h.result= "Thua"
        AND h.namegame = 'Reng Reng'
      """, nativeQuery = true)
  Integer sumRengBetLose();

  @Query(value = """
      SELECT SUM(h.bet)
      FROM sessionPlayer h
      WHERE h.result= "Thắng"
        AND h.namegame = 'Reng Reng'
      """, nativeQuery = true)
  Integer sumRengBetWin();



  @Query(value = """
      SELECT SUM(h.bet)
      FROM sessionPlayer h
      WHERE h.playerid = :idPlayer
        AND h.namegame = 'Tài xỉu'
        AND (
              (CAST(h.result AS UNSIGNED) > 10 AND h.choice = 'xiu') OR
                  (CAST(h.result AS UNSIGNED) < 10 AND h.choice = 'tai')
            )
      """, nativeQuery = true)
  Integer sumClLose(@Param("idPlayer") Integer idPlayer);

  @Query(value = """
      SELECT SUM(h.bet)
      FROM sessionPlayer h
      WHERE h.playerid = :idPlayer
        AND h.namegame = 'Tài xỉu'
        AND (
              (CAST(h.result AS UNSIGNED) > 10 AND h.choice = 'tai') OR
              (CAST(h.result AS UNSIGNED) < 10 AND h.choice = 'xiu')
            )
      """, nativeQuery = true)
  Integer sumClWin(@Param("idPlayer") Integer idPlayer);

  @Query(value = """
      SELECT SUM(h.bet)
      FROM sessionPlayer h
      WHERE  h.namegame = 'Tài xỉu'
        AND (
              (CAST(h.result AS UNSIGNED) > 10 AND h.choice = 'xiu') OR
                  (CAST(h.result AS UNSIGNED) < 10 AND h.choice = 'tai')
            )
      """, nativeQuery = true)
  Integer sumTXBetLose();

  @Query(value = """
      SELECT SUM(h.bet)
      FROM sessionPlayer h
      WHERE  h.namegame = 'Tài xỉu'
        AND (
              (CAST(h.result AS UNSIGNED) > 10 AND h.choice = 'tai') OR
              (CAST(h.result AS UNSIGNED) < 10 AND h.choice = 'xiu')
            )
      """, nativeQuery = true)
  Integer sumTXBetWin();

}
