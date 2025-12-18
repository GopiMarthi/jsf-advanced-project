package com.example.services;

import com.example.model.User;
import com.example.exceptions.BusinessException;
import jakarta.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.primefaces.model.SortOrder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class UserService {

    @PersistenceContext
    private EntityManager em;

    public List<User> findAll() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    public User getUserById(Long id) {
        return em.find(User.class, id);
    }

    public User findByUsername(String username) {
        try {
            return em.createNamedQuery("User.findByUsername", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    public void save(User user) throws BusinessException {
        try {
            if (user.getId() == null) {
                em.persist(user);
            } else {
                em.merge(user);
            }
        } catch (Exception e) {
            throw new BusinessException("Error saving user", e);
        }
    }

    @Transactional
    public void delete(User user) throws BusinessException {
        try {
            em.remove(em.contains(user) ? user : em.merge(user));
        } catch (Exception e) {
            throw new BusinessException("Error deleting user", e);
        }
    }

    public List<User> search(String term) {
        return em.createNamedQuery("User.search", User.class)
                .setParameter("term", "%" + term + "%")
                .getResultList();
    }

    // For lazy loading
    public List<User> getUsersLazy(int first, int pageSize, String sortField, org.primefaces.model.SortOrder sortOrder,
            Map<String, String> filters) {
        String query = "SELECT u FROM User u WHERE 1=1";
        Map<String, Object> params = new HashMap<>();

        // Add filters
        if (filters != null) {
            for (Map.Entry<String, String> entry : filters.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().trim().isEmpty()) {
                    query += " AND LOWER(u." + entry.getKey() + ") LIKE LOWER(:filter" + entry.getKey() + ")";
                    params.put("filter" + entry.getKey(), "%" + entry.getValue() + "%");
                }
            }
        }

        // Add sorting
        if (sortField != null) {
            query += " ORDER BY u." + sortField
                    + (sortOrder == org.primefaces.model.SortOrder.ASCENDING ? " ASC" : " DESC");
        }

        javax.persistence.Query q = em.createQuery(query, User.class);
        for (Map.Entry<String, Object> param : params.entrySet()) {
            q.setParameter(param.getKey(), param.getValue());
        }

        return q.setFirstResult(first).setMaxResults(pageSize).getResultList();
    }

    public int getUserCount(Map<String, String> filters) {
        String query = "SELECT COUNT(u) FROM User u WHERE 1=1";
        Map<String, Object> params = new HashMap<>();

        // Add filters
        if (filters != null) {
            for (Map.Entry<String, String> entry : filters.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().trim().isEmpty()) {
                    query += " AND LOWER(u." + entry.getKey() + ") LIKE LOWER(:filter" + entry.getKey() + ")";
                    params.put("filter" + entry.getKey(), "%" + entry.getValue() + "%");
                }
            }
        }

        javax.persistence.Query q = em.createQuery(query, Long.class);
        for (Map.Entry<String, Object> param : params.entrySet()) {
            q.setParameter(param.getKey(), param.getValue());
        }

        return ((Long) q.getSingleResult()).intValue();
    }

    public List<User> getAllUsers() {
        return findAll();
    }

    public List<User> searchUsers(String term) {
        return search(term);
    }

    @Transactional
    public void updateUser(User user) throws BusinessException {
        save(user);
    }

    @Transactional
    public void createUser(User user) throws BusinessException {
        save(user);
    }

    @Transactional
    public void deleteUser(Long id) throws BusinessException {
        User user = getUserById(id);
        if (user != null) {
            delete(user);
        }
    }

    @Transactional
    public void deleteUsers(List<User> users) throws BusinessException {
        for (User user : users) {
            delete(user);
        }
    }
}