package com.dev.drinksback.dto;

import com.dev.drinksback.model.Admin;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminResponseDTO extends AbstractDTO<Admin, AdminResponseDTO> {
    private String nome;
    private String usuario;

    public AdminResponseDTO() {
    }

    public AdminResponseDTO(Admin admin) {
        super(admin);
    }
}
