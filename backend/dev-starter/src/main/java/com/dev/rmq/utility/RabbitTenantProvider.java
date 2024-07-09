package com.dev.rmq.utility;

import java.util.Set;

public interface RabbitTenantProvider {
    public Set<String> getAllTenants();
}
