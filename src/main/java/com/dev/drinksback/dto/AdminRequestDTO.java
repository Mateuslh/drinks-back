package com.dev.drinksback.dto;

import com.dev.drinksback.model.Admin;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRequestDTO extends AbstractDTO<Admin, AdminRequestDTO> {
    private String senha;
    private String usuario;

    public AdminRequestDTO() {
    }

    public AdminRequestDTO(Admin admin) {
        super(admin);
    }
}
