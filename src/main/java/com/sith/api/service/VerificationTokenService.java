package com.sith.api.service;

import com.sith.api.dto.request.CreateClientAppRequestDto;
import com.sith.api.entity.VerificationToken;

import java.util.Optional;

public interface VerificationTokenService {
    public VerificationToken createVerificationToken(VerificationToken verificationToken);
    public Optional<VerificationToken> findByToken(String verificationToken);

}

