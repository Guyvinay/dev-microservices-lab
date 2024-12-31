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

import java.util.*;
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
    public Map<Number, User> findUserRevisions(Set<Number> revisions) {
        return getAuditReader().findRevisions(User.class, revisions);
    }

    public List<UserDTO> printUserRevisionHistory(Long userId) {
        List<Number> revisions = getUserRevisions(userId);
        List<User> revUsers = new ArrayList<>();
//        getUserByRevisions(userId);
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
    //TODO: to be improved
    public void getUserByRevisions(Long userId) {
        List<Number> revisions = getUserRevisions(userId);
        Map<Number, User> userRevisions = findUserRevisions(new HashSet<>(revisions));
        log.info("User revisions: {}", userRevisions);
    }

    private UserDTO convertToDTO(User user) {
        if(user == null) {
            return null;
        }
        return modelMapper.map(user, UserDTO.class);
    }
}
