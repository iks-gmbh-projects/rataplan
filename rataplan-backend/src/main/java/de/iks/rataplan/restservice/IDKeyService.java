package de.iks.rataplan.restservice;

import java.security.PublicKey;

public interface IDKeyService {
    public PublicKey getIDKey(boolean signatureFailure);
}
