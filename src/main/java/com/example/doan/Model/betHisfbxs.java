package com.example.doan.Model;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder.In;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonGetter;

@Entity
@Table(name = "betHisfbxs")
@Access(AccessType.FIELD)
public class betHisfbxs {


    public String toString() {
        return "betHisfbxs{" +
                "id=" + id +
                ", id_player=" + idPlayer +
                ", bet_type=" + betType +
                ", reference_id='" + referenceId + '\'' +
                ", prediction='" + prediction + '\'' +
                ", bet_amount=" + betAmount +
                ", bet_time=" + betTime +
                ", multi=" + multi +
                ", status=" + status +
                '}';
    }

    public enum BetType {
        FOOTBALL,
        LOTTERY
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "idplayer")
    private int idPlayer;

    @Enumerated(EnumType.STRING)
    @Column(name = "bet_type")
    private BetType betType;

    @Column(name = "reference_id")
    private String referenceId;

    private String prediction;

    @Column(name = "bet_amount")
    private int betAmount;

    @Column(name = "bet_time")
    private LocalDateTime betTime;

    @Column(name = "multi")
    private int multi;

    @Column(name = "status", columnDefinition = "BIT DEFAULT 0")
    private Boolean status = false;

    // Đánh dấu trường này là transient (không ánh xạ vào database)
    // @Transient 
    // private Boolean winning;


    public betHisfbxs() {}


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPlayer() {
        return idPlayer;
    }

    public void setIdPlayer(int idPlayer) {
        this.idPlayer = idPlayer;
    }

    public BetType getBetType() {
        return betType;
    }

    public void setBetType(BetType betType) {
        this.betType = betType;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getPrediction() {
        return prediction;
    }

    public void setPrediction(String prediction) {
        this.prediction = prediction;
    }

    public int getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(int betAmount) {
        this.betAmount = betAmount;
    }

    public LocalDateTime getBetTime() {
        return betTime;
    }

    public void setBetTime(LocalDateTime betTime) {
        this.betTime = betTime;
    }

    public int getMulti() {
        return multi;
    }

    public void setMulti(int multi) {
        this.multi = multi;
    }


    public Boolean getStatus() {
        return status;
    }


    public void setStatus(Boolean status) {
        this.status = status;
    }
    // @JsonGetter("winning")
    // public Boolean getWinning() {
    //     return winning;
    // }

    // public void setWinning(Boolean winning) {
    //     this.winning = winning;
    // }

}
