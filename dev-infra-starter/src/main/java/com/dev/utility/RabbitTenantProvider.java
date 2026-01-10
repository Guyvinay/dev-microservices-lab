package com.dev.utility;

import java.util.Set;

public interface RabbitTenantProvider {
    Set<String> getAllTenants();
}
