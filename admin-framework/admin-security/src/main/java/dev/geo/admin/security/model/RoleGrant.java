package dev.geo.admin.security.model;

import java.util.Set;

public record RoleGrant(
        Long roleId,
        String dataScope,
        String roleKey,
        Set<String> permissions
) {
    public RoleGrant {
        permissions = permissions == null ? Set.of() : Set.copyOf(permissions);
    }
}