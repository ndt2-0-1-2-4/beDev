package com.example.doan.Model;

import jakarta.persistence.*;

@Entity
public class friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id ;
    @Column(name = "id_my")
    private int idMy;
    @Column(name = "id_friend")
    private int idFriend;
    private String relative;

    public void setId(int id){
        this.id = id ;
    }
    public int getId(){
        return this.id ;
    }
    public void setIdMy(int idMy){
        this.idMy = idMy ;
    }
    public int getIdMy(){
        return this.idMy ;
    }
    public void setIdFriend(int idFriend){
        this.idFriend = idFriend ;
    }
    public int getIdFriend(){
        return this.idFriend ;
    }
    public void setRelative(String relative){
        this.relative = relative ;
    }
    public String getRelative(){
        return this.relative ;
    }
}
