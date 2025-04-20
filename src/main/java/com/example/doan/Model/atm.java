package com.example.doan.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
@Entity
public class atm {
    @Id
    @Column(name = "idplayer")
    private Integer idPlayer; 

    @Column(name = "stk")
    private String stk;

    @Column(name = "balance")
    private Float balance;


    @ManyToOne
    @JoinColumn(name = "idplayer", insertable = false, updatable = false)
    private users user; 

    public atm() {}
    public atm(int idPlayer , float balance , String stk){
        this.idPlayer = idPlayer;
        this.balance = balance;
        this.stk = stk;
    }
    
    public int getIdPlayer(){
        return idPlayer;
    }
    public void setIdPlayer(int idPlayer){
        this.idPlayer = idPlayer;
    }
    public float getBalance(){
        return balance;
    }
    public void setBalance(float balance){
        this.balance = balance;
    }
    public String getStk(){
        return stk;
    }
    public void setStk(String stk){
        this.stk = stk;
    }   
}
