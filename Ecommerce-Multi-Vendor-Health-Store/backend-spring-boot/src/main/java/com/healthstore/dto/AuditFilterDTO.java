package com.healthstore.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for filtering audit logs.
 * Allows filtering by entity type, entity ID, revision type, and date range.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditFilterDTO {
    /**
     * The name of the entity to filter by (e.g., "Product", "User")
     */
    private String entityName;
    
    /**
     * The ID of the specific entity to filter by
     */
    private Long entityId;
    
    /**
     * The type of revision to filter by (e.g., "ADD", "MOD", "DEL")
     */
    private String revisionType;
    
    /**
     * The start date for filtering revisions
     */
    private LocalDateTime startDate;
    
    /**
     * The end date for filtering revisions
     */
    private LocalDateTime endDate;
}
