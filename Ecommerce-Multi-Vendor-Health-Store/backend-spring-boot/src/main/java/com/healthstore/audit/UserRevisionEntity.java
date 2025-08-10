package com.healthstore.audit;

import com.healthstore.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

/**
 * Custom revision entity that extends DefaultRevisionEntity to include the user
 * who made the changes. This entity is used by Hibernate Envers for audit logging.
 */
@Entity
@RevisionEntity(UserRevisionListener.class)
@Table(name = "user_revisions")
@Getter
@Setter
public class UserRevisionEntity extends DefaultRevisionEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
