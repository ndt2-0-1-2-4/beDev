package com.example.doan.Repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.doan.Model.sessionGame;

@Repository
public interface sessionGameRepo extends JpaRepository<sessionGame,Integer> {
    public ArrayList<sessionGame> findTop10ByNamegameOrderByIdDesc(String namegame);
}
