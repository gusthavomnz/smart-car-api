package com.glc.smartcar.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.glc.smartcar.user.Usuario;

@Repository
public interface UserRepository extends JpaRepository<Usuario, Long> {

}