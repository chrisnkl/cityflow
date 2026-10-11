package com.chrisnkl.cfbackend.infrastructure.helper;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class RoleManagementHelper {


    public Set<String> defaultRolesWithPermissions() {
        return Set.of(
                "ROLE_USER",
                "REPORT_ISSUE",
                "RECEIVE_NOTIFICATIONS",
                "VIEW_PROFILE",
                "VIEW_OWN_REPORTS",
                "VIEW_STATISTICS"
        );
    }

    public Set<String> getEmployeeRolesWithPermissions() {
        Set<String> authorities = new HashSet<>(
                defaultRolesWithPermissions()
        );

        authorities.add("ROLE_EMPLOYEE");
        authorities.addAll(Set.of(
                "VIEW_ALL_REPORTS",
                "VALIDATE_REPORT",
                "REJECT_REPORT",
                "UPDATE_REPORT_STATUS"
        ));

        return Set.copyOf(authorities);
    }

    public Set<String> getAdminRolesWithPermissions() {
        Set<String> authorities = new HashSet<>(
                getEmployeeRolesWithPermissions()
        );

        authorities.add("ROLE_ADMIN");
        authorities.addAll(Set.of(
                "MANAGE_SYSTEM_SETTINGS",
                "MANAGE_USERS",
                "MANAGE_CATEGORIES"
        ));

        return Set.copyOf(authorities);
    }


}
