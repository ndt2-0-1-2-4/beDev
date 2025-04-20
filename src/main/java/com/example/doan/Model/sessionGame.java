package com.example.doan.Model;

import jakarta.persistence.*;
@Entity
@Table(name = "sessiongame")
public class sessionGame {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;
    private String namegame;
    private String result ;
    private String timeoccurs;


    public void setId(int id ){
        this.id = id ;
    }
    public int getId() {
        return id;
    }
    // public void setIdgame(int idgame) {
    //     this.idgame = idgame;
    // }
    // public int getIdgame() {
    //     return idgame;
    // }
    public void setNamegame( String namegame) {
        this.namegame = namegame;
    }
    public String getNamegame() {
        return namegame;
    }
    public void setResult(String result) {
        this.result = result;
    }
    public String getResult() {
        return result;
    }
    public void setTimeoccurs(String timeoccurs) {
        this.timeoccurs = timeoccurs;
    }
    public String getTimeoccurs() {
        return timeoccurs;
    }
}
