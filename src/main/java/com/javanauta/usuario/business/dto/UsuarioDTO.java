package com.javanauta.usuario.business.dto;

import com.javanauta.usuario.infrastructure.entity.enums.Role;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioDTO {

    private String nome;
    private String email;
    private String senha;
    private Role role;
    private List<EnderecoDTO> enderecos;
    private List<TelefoneDTO> telefones;
}
