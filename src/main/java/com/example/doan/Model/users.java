package com.example.doan.Model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@Entity
public class users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String tk;
    private String mk;
    private String fullname;
    private String email;
    private String role;
    @Column(name = "is_delete", columnDefinition = "BIT DEFAULT 0")
    private Boolean isDelete = false;
    @Column(name = "is_active", columnDefinition = "BIT DEFAULT 1")
    private Boolean isActive = true;
    @OneToMany(mappedBy = "playerid")
    private List<sessionPlayer> sessionPlayers;
    @Column(name = "token_verify")
    private String tokenVerify;
    @Column(name = "is_verify")
    private Boolean isVerify = false;
    @Column(name = "otp")
    private String otp;
    @Column(name = "expiry_otp")
    private LocalDateTime expiryOtp;
    @Column(name = "expiry_recovery")
    private LocalDateTime expiryRecovery;

    public users() {
    }

    public users(int id, String tk, String mk, String fullname, String email) {
        this.id = id;
        this.tk = tk;
        this.mk = mk;
        this.fullname = fullname;
        this.email = email;
    }

    public users(String tk, String mk, String fullname, String email, String role) {

        this.tk = tk;
        this.mk = mk;
        this.fullname = fullname;
        this.email = email;
        this.role = role;
    }

    // public int getId() {
    // return id;
    // }

    // public void setId(int id) {
    // this.id = id;
    // }

    // public String getTk() {
    // return tk;
    // }

    // public void setTk(String tk) {
    // this.tk = tk;
    // }

    // public String getMk() {
    // return mk;
    // }

    // public void setMk(String mk) {
    // this.mk = mk;
    // }

    // public String getFullname() {
    // return fullname;
    // }

    // public void setFullname(String fullname) {
    // this.fullname = fullname;
    // }

    // public void setEmail(String email) {
    // this.email = email;
    // }

    // public String getEmail() {
    // return email;
    // }

    // public void setRole(String role) {
    // this.role = role;
    // }

    // public String getRole() {
    // return role;
    // }

    // public Boolean getIsDelete() {
    // return isDelete;
    // }

    // public void setIsDelete(Boolean isDelete) {
    // this.isDelete = isDelete;
    // }

    // public Boolean getIsActive() {
    // return isActive;
    // }

    // public void setIsActive(Boolean isActive) {
    // this.isActive = isActive;
    // }
}
