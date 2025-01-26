package com.dev.auth.security;

public enum StatusEnum {
    ACTIVE, LOCKED, INACTIVE, INVITED, REVOKED, INVITE_EXPIRED, EXPIRED,
    // User has been onboarded(activated) by admin but has not logged in the system yet
    CREATED,

    //For KeyGen Keys
    EXPIRING, BANNED,

    // Failed to sent invitation mail.
    INVITATION_FAILED
}
