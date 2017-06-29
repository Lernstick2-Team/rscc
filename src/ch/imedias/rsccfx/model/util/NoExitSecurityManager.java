package ch.imedias.rsccfx.model.util;

import java.security.Permission;

public class NoExitSecurityManager extends SecurityManager {

    private SecurityManager baseSecurityManager;

    public NoExitSecurityManager(SecurityManager baseSecurityManager) {
        this.baseSecurityManager = baseSecurityManager;
    }

    @Override
    public void checkPermission(Permission permission) {
        if (permission.getName().startsWith("exitVM.*")) {
            throw new SecurityException("System exit not allowed");
        }
        if (baseSecurityManager != null) {
            baseSecurityManager.checkPermission(permission);
        } else {
            return;
        }
    }

}
