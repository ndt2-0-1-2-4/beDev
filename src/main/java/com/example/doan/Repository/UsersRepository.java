package com.example.doan.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.doan.Model.users;
import java.util.List;

public interface UsersRepository extends JpaRepository<users, Integer> {

    Optional<users> findByTkAndIsDelete(String tk, Boolean isDelete);
    Optional<users> findByTk(String tk);

    Optional<users> findByEmailAndIsDelete(String email , Boolean isDelete);
    Optional<users> findByEmail(String email);

    Optional<users> findByFullname(String fullname);

    List<users> findByFullnameContaining(String fullname);

    Optional<users> findIdAndFullnameById(int id);

    List<users> findByIsDeleteFalse();

    List<users> findByIsActiveTrue();

    @Query(value = "SELECT * FROM users", nativeQuery = true)
    List<users> findAllUsers(); // Lấy tất cả người dùng từ bảng users

    users findByTokenVerify(String tokenVerify);

    users findByisVerify(Boolean isVerify);

    Boolean existsByTk(String tk);

    Boolean existsByEmail(String email);

}
