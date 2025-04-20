package com.example.doan.Model;

import jakarta.persistence.*;

@Entity
@Table(name="sessionplayer")
public class sessionPlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "namegame")
    private String namegame;
    @Column(name = "playerid")
    private int playerid;

    @ManyToOne
    @JoinColumn(name = "playerid" , referencedColumnName = "id",insertable = false, updatable = false )
    private users player;

    @Column(name = "timeoccurs")
    private String timeoccurs;
    @Column(name = "bet")
    private float bet;
    @Column(name = "result")
    private String result;
    @Column(name = "reward")
    private float reward;
    @Column(name = "choice")   
    private String choice;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameGame() {
        return namegame;
    }

    public void setNameGame(String namegame) {
        this.namegame = namegame;
    }

    public int getPlayerId() {
        return playerid;
    }

    public void setPlayerId(int playerid) {
        this.playerid = playerid;
    }

    public void setTimeoccurs( String timeoccurs) {
        this.timeoccurs = timeoccurs;
    }
    public String getTimeoccurs() {
        return timeoccurs;
    }
    public void setBet(float bet) {
        this.bet = bet;
    }
    public float getBet(){
        return bet;
    }
    public void setReward(float reward) {
        this.reward = reward;
    }
    public float getReward(){
        return reward;
    }
    public void setChoice( String choice) {
        this.choice = choice;
    }
    public String getChoice(){
        return choice;
    }
    public void setResult(String rs){
        this.result = rs;
    }
    public String getResult(){
        return result;
    }

}
