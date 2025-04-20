package com.example.doan.Model;

import jakarta.persistence.*;


@Entity
public class listgame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "namegame")
    private String namegame;

    public listgame(){}
    public void setId(int id ){
        this.id = id ;
    }
    public int getId() {
        return id;
    }
    public void setNamegame(String namegame) {
        this.namegame = namegame;
    }
    public String getNamegame() {
        return namegame;
    }

}
