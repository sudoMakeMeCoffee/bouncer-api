package com.sith.api.service.impl;

import com.sith.api.entity.VerificationToken;
import com.sith.api.repository.VerificationTokenRepository;
import com.sith.api.service.VerificationTokenService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    public VerificationTokenServiceImpl(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Override
    public VerificationToken createVerificationToken(VerificationToken verificationToken) {
        return verificationTokenRepository.save(verificationToken);
    }

    @Override
    public Optional<VerificationToken> findByToken(String verificationToken) {
        return verificationTokenRepository.findByToken(verificationToken);
    }

    @Override
    public void delete(VerificationToken verificationToken) {
        verificationTokenRepository.delete(verificationToken);
    }
}
