package com.phucx.passwordHashing;

import org.keycloak.credential.hash.PasswordHashProvider;
import org.keycloak.models.PasswordPolicy;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.favre.lib.crypto.bcrypt.BCrypt;


public class BcryptPasswordHashingProvider implements PasswordHashProvider{
    private Logger log = LoggerFactory.getLogger(PasswordHashProvider.class);
    private String providerId;
    private int defaultIterations;
    

    public BcryptPasswordHashingProvider(String providerId, int defaultIterations) {
        this.providerId = providerId;
        this.defaultIterations = defaultIterations;
    }

    @Override
    public boolean policyCheck(final PasswordPolicy policy, final PasswordCredentialModel credential) {
        final int policyHashIterations = policy.getHashIterations() == -1 ? defaultIterations : policy.getHashIterations();

        return credential.getPasswordCredentialData().getHashIterations() == policyHashIterations
                && providerId.equals(credential.getPasswordCredentialData().getAlgorithm());
    }

    @Override
    public PasswordCredentialModel encodedCredential(final String rawPassword, final int iterations) {
        final String encodedPassword = encode(rawPassword, iterations);

        // bcrypt salt is stored as part of the encoded password so no need to store salt separately
        return PasswordCredentialModel.createFromValues(providerId, new byte[0], iterations, encodedPassword);
    }

    @Override
    public String encode(final String rawPassword, final int iterations) {
        log.info("encode({}, {})", rawPassword, iterations);
        final int cost = iterations == -1 ? defaultIterations : iterations;
        return BCrypt.with(BCrypt.Version.VERSION_2A).hashToString(cost, rawPassword.toCharArray());
    }

    @Override
    public void close() {

    }

    @Override
    public boolean verify(final String rawPassword, final PasswordCredentialModel credential) {
        log.info("verify({}, {})", rawPassword, credential.getCredentialData());
        final String hash = credential.getPasswordSecretData().getValue();
        final BCrypt.Result verifier = BCrypt.verifyer(BCrypt.Version.VERSION_2A).verify(rawPassword.toCharArray(), hash.toCharArray());
        return verifier.verified;
    }
    
}
