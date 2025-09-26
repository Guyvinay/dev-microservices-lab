package com.dev.utility;

import java.util.Set;

public interface RabbitTenantProvider {
    public Set<String> getAllTenants();
}
