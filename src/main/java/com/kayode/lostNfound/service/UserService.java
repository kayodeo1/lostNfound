package com.kayode.lostNfound.service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.kayode.lostNfound.model.User;

@Stateless
public class UserService {

    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;

    @PersistenceContext(unitName = "app")
    private EntityManager em;

    public void createUser(User u, String plainPassword) {
        String salt = generateSalt();
        u.setSalt(salt);
        u.setPassword(hash(plainPassword, salt));
        em.persist(u);
        em.flush();
    }

    public User findById(Long id) {
        return em.find(User.class, id);
    }

    public User findByEmail(String email) {
        try {
            TypedQuery<User> q = em.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class);
            q.setParameter("email", email);
            return q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean emailExists(String email) {
        TypedQuery<Long> q = em.createQuery(
            "SELECT COUNT(u.id) FROM User u WHERE u.email = :email", Long.class);
        q.setParameter("email", email);
        return q.getSingleResult() > 0;
    }

    public User authenticate(String email, String plainPassword) {
        User user = findByEmail(email);
        if (user == null) return null;
        String expected = hash(plainPassword, user.getSalt());
        return expected.equals(user.getPassword()) ? user : null;
    }

    public List<User> findAll() {
        return em.createQuery("SELECT u FROM User u ORDER BY u.createdDate DESC", User.class)
                 .getResultList();
    }

    private String generateSalt() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    private String hash(String password, String salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                Base64.getDecoder().decode(salt),
                ITERATIONS,
                KEY_LENGTH
            );
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            spec.clearPassword();
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }
}
