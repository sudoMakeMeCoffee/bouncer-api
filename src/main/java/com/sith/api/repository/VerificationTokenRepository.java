package com.sith.api.repository;

import com.sith.api.entity.Client;
import com.sith.api.entity.ClientApp;
import com.sith.api.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
    public Optional<VerificationToken> findByToken(String verificationToken);
    public List<VerificationToken> findAllByClient(Client client);
    public void deleteByToken(String token);
}
