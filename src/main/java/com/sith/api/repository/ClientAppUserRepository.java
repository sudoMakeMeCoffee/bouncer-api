package com.sith.api.repository;

import com.sith.api.entity.ClientAppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientAppUserRepository extends JpaRepository<ClientAppUser, UUID> {
}
