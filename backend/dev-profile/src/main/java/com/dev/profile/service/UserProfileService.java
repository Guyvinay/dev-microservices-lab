package com.dev.profile.service;

import com.dev.profile.entity.UserProfile;
import com.dev.profile.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

public interface UserProfileService {

    public UserProfile createUserProfile(UserProfile userProfile);

    public UserProfile updateUserProfile(UUID id, UserProfile userProfileDetails);

    public UserProfile getUserProfileById(UUID id);

    public List<UserProfile> getAllUserProfiles();

    // Delete a UserProfile by ID
    public void deleteUserProfile(UUID id);
}
