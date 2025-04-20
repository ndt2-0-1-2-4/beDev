package com.example.doan.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "id_my")
    private int idMy;

    @Column(name = "id_friend")
    private int idFriend;

    private String content;
    private String timeSend;

    @Column(name = "status" , columnDefinition = "tinyint default 0")
    private boolean status;

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public void setIdMy(int idMy) {
        this.idMy = idMy;
    }
    public int getIdMy() {
        return idMy;
    }
    public void setIdFriend(int idFriend) {
        this.idFriend = idFriend;
    }
    public int getIdFriend() {
        return idFriend;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getContent() {
        return content;
    }
    public void setTimeSend(String timeSend) {
        this.timeSend = timeSend;
    }
    public String getTimeSend() {
        return timeSend;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }
    
}
