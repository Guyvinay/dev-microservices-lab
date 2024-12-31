package com.dev.service;

import com.dev.dto.UserDTO;
import com.dev.modal.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserAuditService {

    @PersistenceContext
    EntityManager entityManager;


    @Autowired
    private ModelMapper modelMapper;

    private AuditReader getAuditReader() {
        return AuditReaderFactory.get(entityManager);
    }

    public List<Number> getUserRevisions(Long userId) {
        return getAuditReader().getRevisions(User.class, userId);
    }

    public User getUserAtRevision(Long userId, int revision) {
        return getAuditReader().find(User.class, userId, revision);
    }


    public Date getRevisionTimestamp(int revision) {
        return getAuditReader().getRevisionDate(revision);
    }

    public List<UserDTO> printUserRevisionHistory(Long userId) {
        List<Number> revisions = getUserRevisions(userId);
        List<User> revUsers = new ArrayList<>();

        for (Number rev : revisions) {
            User userAtRevision = getUserAtRevision(userId, rev.intValue());
            Date timestamp = getRevisionTimestamp(rev.intValue());
            System.out.println("Revision: " + rev + ", Timestamp: " + timestamp);
            System.out.println("User at Revision: " + userAtRevision);
            revUsers.add(userAtRevision);
        }

        log.info("retrieved use audit: [{}]", revUsers);
        return revUsers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private UserDTO convertToDTO(User user) {
        if(user == null) {
            return null;
        }
        return modelMapper.map(user, UserDTO.class);
    }
}
