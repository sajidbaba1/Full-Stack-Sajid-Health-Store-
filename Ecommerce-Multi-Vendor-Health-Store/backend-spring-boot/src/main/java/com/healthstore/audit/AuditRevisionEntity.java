package com.healthstore.audit;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import java.time.LocalDateTime;

/**
 * Custom revision entity that extends DefaultRevisionEntity to include the username
 * of the user who made the changes.
 */
@Entity
@Table(name = "revision_info")
@RevisionEntity(AuditRevisionListener.class)
@Data
@EqualsAndHashCode(callSuper = false)
public class AuditRevisionEntity extends DefaultRevisionEntity {
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "username")
    private String username;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    // Manual getter and setter methods to ensure compilation works when Lombok fails
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
