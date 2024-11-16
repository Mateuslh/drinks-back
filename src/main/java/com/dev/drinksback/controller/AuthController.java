package com.dev.drinksback.controller;

import com.dev.drinksback.dto.LoginRequestDTO;
import com.dev.drinksback.dto.ResponseEntityDto;
import com.dev.drinksback.dto.TokenResponseDTO;
import com.dev.drinksback.security.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Login")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Transactional
    @PostMapping("/login")
    public ResponseEntityDto<TokenResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        String token = authService.authenticate(loginRequestDTO.getUsuario(), loginRequestDTO.getSenha());

        if (token != null) {
            return new ResponseEntityDto<>().setContent(new TokenResponseDTO(token));
        } else {
            ResponseEntityDto responseEntityDto = new ResponseEntityDto<>();
            responseEntityDto.setSuccess(Boolean.FALSE);
            responseEntityDto.setMessage("Usuario ou senha incorreto.");
            return responseEntityDto;
        }
    }
}
