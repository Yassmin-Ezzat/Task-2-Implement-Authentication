package com.implementauthentication.implementauthentication.repository;

import com.implementauthentication.implementauthentication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer>{
    User findById(int Id);
    User findByEmail(String email);
}