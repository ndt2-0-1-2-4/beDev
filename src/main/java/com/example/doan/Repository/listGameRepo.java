package com.example.doan.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.doan.Model.listgame;

public interface listGameRepo extends JpaRepository<listgame, Integer> {
    
}
