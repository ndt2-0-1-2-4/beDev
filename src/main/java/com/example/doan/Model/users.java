package com.example.doan.Model;

import jakarta.persistence.*;
import java.util.List;
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
    @OneToMany(mappedBy = "playerid")
    private List<sessionPlayer> sessionPlayers;
    public users() {}
    public users(int id, String tk, String mk, String fullname, String email){
        this.id = id;
        this.tk = tk;
        this.mk = mk;
        this.fullname = fullname;
        this.email = email;
    }
    public users( String tk, String mk, String fullname, String email, String role){
       
        this.tk = tk;
        this.mk = mk;
        this.fullname = fullname;
        this.email = email;
        this.role = role;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTk() {
        return tk;
    }
    public void setTk(String tk) {
        this.tk = tk;
    }
    public String getMk() {
        return mk;
    }
    public void setMk(String mk) {
        this.mk = mk;
    }
    public String getFullname() {
        return fullname;
    }
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getEmail() {
        return email;
    }
    public void setRole(String role){
        this.role = role;
    }
    public String getRole() {
        return role;
    }
}
