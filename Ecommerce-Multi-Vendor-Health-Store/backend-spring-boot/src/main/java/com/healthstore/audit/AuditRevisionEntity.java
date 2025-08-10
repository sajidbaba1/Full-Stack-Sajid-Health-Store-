package com.healthstore.audit;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

/**
 * Custom revision entity that extends DefaultRevisionEntity to include the username
 * of the user who made the changes.
 */
@Entity
@Table(name = "revision_info")
@RevisionEntity(AuditRevisionListener.class)
@Data
public class AuditRevisionEntity extends DefaultRevisionEntity {
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "username")
    private String username;
    
    @Column(name = "ip_address")
    private String ipAddress;
}
