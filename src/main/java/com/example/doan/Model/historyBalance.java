package com.example.doan.Model;

import jakarta.persistence.*;

@Entity
@Table(name="historybalance")
public class historyBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id ;
    @Column(name = "id_player")
    private int idPlayer;
    // @ManyToOne 
    // @JoinColumn(name = "idPlayer",insertable = false,updatable = false)
    // private users user;
    @Column(name = "timechange")
    private String timeChange;
    private String content;
    private int trans;
    private int balance;

    public void setPlayerId(int idPlayer){
        this.idPlayer=idPlayer;
    }
    public int getIdPlayer(){
        return idPlayer;
    }

    public void setTimeChange(String timeChange){
        this.timeChange=timeChange;
    }
    public String getTimeChange(){
        return timeChange;
    }

    public void setTrans(int trans){
        this.trans=trans;
    }
    public int getTrans(){
        return trans;
    }

    public void setBalance(int balance){
        this.balance= balance;
    }
    public int getBalance(){
        return balance;
    }

    public void setContent(String content){
        this.content=content;
    }
    public String getContent(){
        return content;
    }

}
