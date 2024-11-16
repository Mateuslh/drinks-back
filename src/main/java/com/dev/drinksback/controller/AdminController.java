package com.dev.drinksback.controller;

import com.dev.drinksback.dto.AdminRequestDTO;
import com.dev.drinksback.dto.AdminResponseDTO;
import com.dev.drinksback.dto.ResponseEntityDto;
import com.dev.drinksback.model.Admin;
import com.dev.drinksback.service.AdminService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admins")
@Tag(name = "Admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Transactional
    @PostMapping
    public ResponseEntity<?> createEntity(@Valid @RequestBody AdminRequestDTO adminDTO) {
        Admin admin = adminDTO.toEntity();
        adminService.save(admin);
        return ResponseEntity.ok().build();
    }

    @Transactional
    @PutMapping()
    public ResponseEntityDto<AdminResponseDTO> updateEntity(@Valid @RequestBody AdminRequestDTO adminDTO) {
        Admin admin = adminDTO.toEntity();
        return new ResponseEntityDto<>().setContent(adminService.save(admin));
    }
}
