package com.tracker.backend.security;

import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

@Service
public class FirebaseService {

    /**
     * Verifica un token de ID de Firebase.
     *
     * @param idToken El token de ID de Firebase a verificar.
     * @return El token de Firebase decodificado.
     * @throws FirebaseAuthException Si ocurre un error al verificar el token.
     */
    public FirebaseToken verifyToken(String idToken) throws FirebaseAuthException {
        return FirebaseAuth.getInstance().verifyIdToken(idToken);
    }
}
