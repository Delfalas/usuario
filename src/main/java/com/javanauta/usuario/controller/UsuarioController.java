package com.javanauta.usuario.controller;

import com.javanauta.usuario.business.UsuarioService;
import com.javanauta.usuario.business.dto.EnderecoDTO;
import com.javanauta.usuario.business.dto.TelefoneDTO;
import com.javanauta.usuario.business.dto.UsuarioDTO;
import com.javanauta.usuario.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<UsuarioDTO> salvarUsuario(@RequestBody UsuarioDTO usuarioDTO){
        return ResponseEntity.ok(usuarioService.salvaUsuario(usuarioDTO));
    }

    @PostMapping("/login")
    public String login(@RequestBody UsuarioDTO usuarioDTO){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usuarioDTO.getEmail(),
                        usuarioDTO.getSenha())

        );
        return "Bearer " + jwtUtil.generateToken(authentication.getName());
    }

    @GetMapping
    public ResponseEntity<UsuarioDTO> buscaUsuarioPorEmail(@RequestParam("email") String email){
        return ResponseEntity.ok(usuarioService.buscarUsuarioPorEmail(email));
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deletaUsuarioPorEmail(@PathVariable String email){
        usuarioService.deletarUsuarioPorEmail(email);
        return ResponseEntity.ok().build();
    }

    //Atualiza os dados de usuario usando o Header do token do usuario(email)
    @PutMapping
    public ResponseEntity<UsuarioDTO> atualizaDadosUsuario(@RequestBody UsuarioDTO usuarioDTO,
                                                           @RequestHeader("Authorization") String token){
        return ResponseEntity.ok(usuarioService.atualizaDadosUsuario(token, usuarioDTO));
    }

    //Atualiza os dados de Endereco usando o Header do token do usuario(email)
    @PutMapping("/endereco")
    public ResponseEntity<EnderecoDTO> atualizaEndereco(@RequestBody EnderecoDTO enderecoDTO,
                                                           @RequestParam("id") Long id){
        return ResponseEntity.ok(usuarioService.atualizarEndereco(id, enderecoDTO));
    }

    //Atualiza os dados de Telefone usando o Header do token do usuario(email)
    @PutMapping("/telefone")
    public ResponseEntity<TelefoneDTO> atualizaTelefone(@RequestBody TelefoneDTO telefoneDTO,
                                                            @RequestParam("id") Long id){
        return ResponseEntity.ok(usuarioService.atualizarTelefone(id, telefoneDTO));
    }

    //Cria e cadastra os dados de Endereco usando o Header do token do usuario(email)----------------------------------
    @PostMapping("/endereco")
    public ResponseEntity<EnderecoDTO> cadastrarEndereco(@RequestBody EnderecoDTO enderecoDTO,
                                                        @RequestHeader("Authorization") String token){
        return ResponseEntity.ok(usuarioService.cadastrarEndereco(token, enderecoDTO));
    }

    //Cria e cadastra os dados de Telefone usando o Header do token do usuario(email)-----------------------------------
    @PostMapping("/telefone")
    public ResponseEntity<TelefoneDTO> cadastrarTelefone(@RequestBody TelefoneDTO telefoneDTO,
                                                        @RequestHeader("Authorization") String token){
        return ResponseEntity.ok(usuarioService.cadastrarTelefone(token, telefoneDTO));
    }

    //Método para deletar telefone
    @DeleteMapping("/telefone/{id}")
    public ResponseEntity<Void> deletarTelefone(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        usuarioService.deletarTelefone(token, id);
        return ResponseEntity.noContent().build();
    }

    //método para deletar endereco
    @DeleteMapping("/endereco/{id}")
    public ResponseEntity<Void> deletarEndereco(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        usuarioService.deletarEndereco(token, id);
        return ResponseEntity.noContent().build();
    }

    //CADASTRAR TELEFONE EM LOTE
    @PostMapping("/telefones/lote")
    public ResponseEntity<List<TelefoneDTO>> cadastrarTelefones(
            @RequestHeader("Authorization") String token,
            @RequestBody List<TelefoneDTO> telefonesDTO) {

        return ResponseEntity.ok(usuarioService.cadastrarTelefones(token, telefonesDTO));
    }

    //CADASTRAR ENDEREÇO EM LOTE
    @PostMapping("/enderecos/lote")
    public ResponseEntity<List<EnderecoDTO>> cadastrarEnderecos(
            @RequestHeader("Authorization") String token,
            @RequestBody List<EnderecoDTO> enderecosDTO) {

        return ResponseEntity.ok(usuarioService.cadastrarEnderecos(token, enderecosDTO));
    }


}
