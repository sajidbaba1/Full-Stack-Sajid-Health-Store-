package com.healthstore.audit;

import com.healthstore.model.User;
import com.healthstore.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.envers.RevisionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Listener that populates the UserRevisionEntity with the currently authenticated user
 * when a new revision is created.
 */
@Component
public class UserRevisionListener implements RevisionListener {

    private static UserRepository userRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        UserRevisionListener.userRepository = userRepository;
    }

    @Override
    public void newRevision(Object revisionEntity) {
        UserRevisionEntity revision = (UserRevisionEntity) revisionEntity;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            // Get the user from the database to ensure we have a managed entity
            User user = userRepository.findByEmail(userDetails.getUsername())
                .orElse(null);
            
            if (user != null) {
                // Ensure the user is in the persistence context
                if (!entityManager.contains(user)) {
                    user = entityManager.merge(user);
                }
                revision.setUser(user);
            }
        }
    }
}
