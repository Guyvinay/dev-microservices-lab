package com.dev.profile.service.serivceImpl;

import com.dev.profile.entity.UserProfile;
import com.dev.profile.exception.ResourceNotFoundException;
import com.dev.profile.repository.UserProfileRepository;
import com.dev.profile.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Override
    public UserProfile createUserProfile(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }

    @Override
    public UserProfile updateUserProfile(UUID id, UserProfile userProfileDetails) {
        UserProfile existingProfile = userProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserProfile not found with id: " + id));

        existingProfile.setEmail(userProfileDetails.getEmail());

        return userProfileRepository.save(existingProfile);
    }

    @Override
    public UserProfile getUserProfileById(UUID id) {
        return userProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserProfile not found with id: " + id));
    }

    // Get all UserProfiles
    @Override
    public List<UserProfile> getAllUserProfiles() {
        return userProfileRepository.findAll();
    }

    @Override
    public void deleteUserProfile(UUID id) {
        UserProfile existingProfile = userProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserProfile not found with id: " + id));
        userProfileRepository.delete(existingProfile);
    }
}
