package com.healthstore.dto;

import com.healthstore.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * DTO representing user activity report data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityReportDTO {
    private long totalUsers;
    private long activeUsersLast30Days;
    private List<User> recentUsers;

    // Manual getter and setter methods to ensure compilation works when Lombok fails
    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getActiveUsersLast30Days() {
        return activeUsersLast30Days;
    }

    public void setActiveUsersLast30Days(long activeUsersLast30Days) {
        this.activeUsersLast30Days = activeUsersLast30Days;
    }

    public List<User> getRecentUsers() {
        return recentUsers;
    }

    public void setRecentUsers(List<User> recentUsers) {
        this.recentUsers = recentUsers;
    }
}
