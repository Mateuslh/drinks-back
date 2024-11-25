package com.dev.drinksback.service;

import com.dev.drinksback.dto.AdminResponseDTO;
import com.dev.drinksback.exception.EntidadeNaoEncontradaException;
import com.dev.drinksback.model.Admin;
import com.dev.drinksback.repository.AdminRepository;
import com.dev.drinksback.repository.DrinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@SuppressWarnings("ALL")
@Service
public class AdminService {

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DrinkRepository drinkRepository;

    @Value("${user.creditos.generation:5}")
    private Long CREDITOS_GENERATION;

    public Page<Admin> findAll(Pageable pageable) {
        return adminRepository.findAll(pageable);
    }

    public Admin findById(Long id) {
        return adminRepository.findById(id).orElseThrow(() -> new EntidadeNaoEncontradaException(Admin.class));
    }

    public Admin findByUsername(String username) {
        return adminRepository.findByUsuario(username).orElseThrow(() -> new EntidadeNaoEncontradaException(Admin.class));
    }

    public AdminResponseDTO save(Admin admin) {
        admin.setSenha(passwordEncoder.encode(admin.getSenha()));
        return toAdminResponseDTO(adminRepository.save(admin));
    }

    public void deleteById(Long id) {
        Admin admin = findById(id);
        adminRepository.delete(admin);
    }

    public void delete(Admin admin) {
        adminRepository.delete(admin);
    }

    public Long getCreditos(Long adminId) {
        LocalDateTime lastDay = LocalDateTime.now().minusDays(1);
        return CREDITOS_GENERATION - drinkRepository.countByAdminIdAndCreatedAtAfter(adminId, lastDay);
    }

    public AdminResponseDTO toAdminResponseDTO(Admin admin) {
        return new AdminResponseDTO(admin);
    }
}
