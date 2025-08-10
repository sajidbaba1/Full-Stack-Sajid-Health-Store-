package com.healthstore.audit;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Listener that populates the audit revision entity with the current user and IP address.
 */
public class AuditRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        AuditRevisionEntity auditRevisionEntity = (AuditRevisionEntity) revisionEntity;
        
        // Get the current authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                auditRevisionEntity.setUsername(userDetails.getUsername());
                
                // If you have a User entity with ID, you can set it here
                // For example, if you can get the user ID from UserDetails:
                // auditRevisionEntity.setUserId(userDetails.getId());
            } else if (principal instanceof String) {
                // Handle the case where the principal is just a string (e.g., username)
                auditRevisionEntity.setUsername((String) principal);
            }
        }
        
        // Get the client IP address
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String ipAddress = request.getHeader("X-FORWARDED-FOR");
            
            if (ipAddress == null || ipAddress.isEmpty()) {
                ipAddress = request.getRemoteAddr();
            }
            
            auditRevisionEntity.setIpAddress(ipAddress);
        } catch (Exception e) {
            // If we can't get the request (e.g., in a batch job), just ignore
            auditRevisionEntity.setIpAddress("SYSTEM");
        }
    }
}
