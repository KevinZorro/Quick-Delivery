package com.ufps.Quick_Delivery.repository;

import com.ufps.Quick_Delivery.model.PasswordResetToken;
import com.ufps.Quick_Delivery.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUsuario(Usuario usuario);
}
