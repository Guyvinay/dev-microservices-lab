package com.dev.controller;

import com.dev.dto.ActionDTO;
import com.dev.dto.PrivilegeDTO;
import com.dev.entity.enums.Privilege;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/privileges")
public class PrivilegeCatalogController {

    @GetMapping("/catalog")
    public ResponseEntity<List<PrivilegeDTO>> getPrivilegeCatalog() {
        List<PrivilegeDTO> catalog = Arrays.stream(Privilege.values())
                .map(privilege -> {
                    List<ActionDTO> actions = privilege.getActions().stream()
                            .map(act -> new ActionDTO(act.name(), act.getDescription()))
                            .collect(Collectors.toList());

                    return new PrivilegeDTO(
                            privilege.name(),
                            privilege.getDescription(),
                            privilege.getArea().name(),
                            actions
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(catalog);
    }
}

