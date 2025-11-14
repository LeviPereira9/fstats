package lp.edu.fstats.model.user;

import lombok.Getter;
import lp.edu.fstats.exception.custom.CustomNotFoundException;

@Getter
public enum Role {
    USER(1, "USER"),
    MOD(2, "MOD"),
    ADMIN(3, "ADMIN"),
    SUPER_ADMIN(4, "SUPER_ADMIN");

    private final int hierarchyLevel;
    private final String name;

    Role(int hierarchyLevel, String name){
        this.hierarchyLevel = hierarchyLevel;
        this.name = name;
    }

    public boolean canModify(Role targetRole){
        return this.hierarchyLevel > targetRole.getHierarchyLevel();
    }

    public static Role getRoleByName(String name){
        try{
            return Role.valueOf(name.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e){
            throw CustomNotFoundException.role();
        }
    }

    public boolean hasElevatedPrivileges(){
        return hierarchyLevel > 1;
    }
}
