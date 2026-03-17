package com.javanauta.usuario.controller;

import com.javanauta.usuario.business.UsuarioService;
import com.javanauta.usuario.business.ViaCepService;
import com.javanauta.usuario.business.dto.EnderecoDTO;
import com.javanauta.usuario.business.dto.TelefoneDTO;
import com.javanauta.usuario.business.dto.UsuarioDTO;
import com.javanauta.usuario.infrastructure.client.ViaCepDTO;
import com.javanauta.usuario.infrastructure.security.JwtUtil;
import com.javanauta.usuario.infrastructure.security.SecurityConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Usuário", description = "Cadastro e login de usuários")
@SecurityRequirement(name = SecurityConfig.SECURITY_SCHEME)
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final ViaCepService viaCepService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping
    @Operation(summary = "Salva usuário", description = "Cria um novo usuário")
    @ApiResponse(responseCode = "200", description = "Usuário salvo com sucesso")
    @ApiResponse(responseCode = "409", description = "Usuário já cadastrado")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ResponseEntity<UsuarioDTO> salvarUsuario(@RequestBody UsuarioDTO usuarioDTO){
        return ResponseEntity.ok(usuarioService.salvaUsuario(usuarioDTO));
    }

    @PostMapping("/login")
    @Operation(summary = "Login de usuário", description = "Login do usuário")
    @ApiResponse(responseCode = "200", description = "Usuário logado com sucesso")
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public String login(@RequestBody UsuarioDTO usuarioDTO){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usuarioDTO.getEmail(),
                        usuarioDTO.getSenha())

        );
        return "Bearer " + jwtUtil.generateToken(authentication.getName());
    }

    @GetMapping("/email")
    @Operation(summary = "Buscar dados de usuário por email", description = "Buscar dados do usuário")
    @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ResponseEntity<UsuarioDTO> buscaUsuarioPorEmail(@RequestParam("email") String email,
                                                           @RequestHeader("Authorization") String token){
        return ResponseEntity.ok(usuarioService.buscarUsuarioPorEmail(email));
    }

    @DeleteMapping("/{email}")
    @Operation(summary = "Delete de usuário por Id", description = "Deletar usuário")
    @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ResponseEntity<Void> deletaUsuarioPorEmail(@PathVariable String email){
        usuarioService.deletarUsuarioPorEmail(email);
        return ResponseEntity.noContent().build();
    }

    //Atualiza os dados de usuario usando o Header do token do usuario(email)
    @PutMapping
    @Operation(summary = "Atualizar dados de usuário", description = "Atualização do usuário")
    @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ResponseEntity<UsuarioDTO> atualizaDadosUsuario(@RequestBody UsuarioDTO usuarioDTO,
                                                           @RequestHeader("Authorization") String token){
        return ResponseEntity.ok(usuarioService.atualizaDadosUsuario(token, usuarioDTO));
    }

    //Atualiza os dados de Endereco usando o Header do token do usuario(email)
    @PutMapping("/endereco")
    @Operation(summary = "Atualizar dados de endereço", description = "Atualizar dados de endereço")
    @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Endereço não encontrado")
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ResponseEntity<EnderecoDTO> atualizaEndereco(@RequestBody EnderecoDTO enderecoDTO,
                                                           @RequestParam("id") Long id){
        return ResponseEntity.ok(usuarioService.atualizarEndereco(id, enderecoDTO));
    }

    //Atualiza os dados de Telefone usando o Header do token do usuario(email)
    @PutMapping("/telefone")
    @Operation(summary = "Atualizar dados de telefone", description = "Atualizar dados de telefone")
    @ApiResponse(responseCode = "200", description = "Telefone atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Telefone não encontrado")
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ResponseEntity<TelefoneDTO> atualizaTelefone(@RequestBody TelefoneDTO telefoneDTO,
                                                            @RequestParam("id") Long id){
        return ResponseEntity.ok(usuarioService.atualizarTelefone(id, telefoneDTO));
    }

    //Cria e cadastra os dados de Endereco usando o Header do token do usuario(email)----------------------------------
    @PostMapping("/endereco")
    @Operation(summary = "Salva dados de endereço", description = "Salva dados de endereço")
    @ApiResponse(responseCode = "200", description = "Endereço salvo com sucesso")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ResponseEntity<EnderecoDTO> cadastrarEndereco(@RequestBody EnderecoDTO enderecoDTO,
                                                        @RequestHeader("Authorization") String token){
        return ResponseEntity.ok(usuarioService.cadastrarEndereco(token, enderecoDTO));
    }

    //Cria e cadastra os dados de Telefone usando o Header do token do usuario(email)-----------------------------------
    @PostMapping("/telefone")
    @Operation(summary = "Salva dados de telefone", description = "Salva dados de telefone")
    @ApiResponse(responseCode = "200", description = "Telefone salvo com sucesso")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ResponseEntity<TelefoneDTO> cadastrarTelefone(@RequestBody TelefoneDTO telefoneDTO,
                                                        @RequestHeader("Authorization") String token){
        return ResponseEntity.ok(usuarioService.cadastrarTelefone(token, telefoneDTO));
    }

    //Método para deletar telefone
    @DeleteMapping("/telefone/{id}")
    @Operation(summary = "Delete de telefone por Id", description = "Deletar telefone")
    @ApiResponse(responseCode = "204", description = "Telefone deletado com sucesso")
    @ApiResponse(responseCode = "404", description = "Telefone não encontrado")
    @ApiResponse(responseCode = "401", description = "Token inválido ou não informado")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<Void> deletarTelefone(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        usuarioService.deletarTelefone(token, id);
        return ResponseEntity.noContent().build();
    }

    //método para deletar endereco
    @Operation(summary = "Delete de endereço por Id", description = "Deletar endereço")
    @ApiResponse(responseCode = "204", description = "Endereço deletado com sucesso")
    @ApiResponse(responseCode = "404", description = "Endereço não encontrado")
    @ApiResponse(responseCode = "401", description = "Token inválido ou não informado")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    @DeleteMapping("/endereco/{id}")
    public ResponseEntity<Void> deletarEndereco(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        usuarioService.deletarEndereco(token, id);
        return ResponseEntity.noContent().build();
    }

    //CADASTRAR TELEFONE EM LOTE
    @PostMapping("/telefones/lote")
    @Operation(summary = "Cadastrar telefones em lote",
            description = "Realiza o cadastro de múltiplos telefones para o usuário autenticado"
    )
    @ApiResponse(responseCode = "200", description = "Telefones cadastrados com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição")
    @ApiResponse(responseCode = "401", description = "Token inválido ou não informado")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<List<TelefoneDTO>> cadastrarTelefones(
            @RequestHeader("Authorization") String token,
            @RequestBody List<TelefoneDTO> telefonesDTO) {

        return ResponseEntity.ok(usuarioService.cadastrarTelefones(token, telefonesDTO));
    }

    //CADASTRAR ENDEREÇO EM LOTE
    @PostMapping("/enderecos/lote")
    @Operation(summary = "Cadastrar endereços em lote",
            description = "Realiza o cadastro de múltiplos endereços para o usuário autenticado"
    )
    @ApiResponse(responseCode = "200", description = "Endereços cadastrados com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição")
    @ApiResponse(responseCode = "401", description = "Token inválido ou não informado")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<List<EnderecoDTO>> cadastrarEnderecos(
            @RequestHeader("Authorization") String token,
            @RequestBody List<EnderecoDTO> enderecosDTO) {

        return ResponseEntity.ok(usuarioService.cadastrarEnderecos(token, enderecosDTO));
    }

    //GET DO ENDEREÇO COM VIA API CEP
    @GetMapping("/endereco/{cep}")
    @Operation(summary = "Response do endereco via api cep",
            description = "Traz as informações do cep via api cep"
    )
    @ApiResponse(responseCode = "200", description = "Endereço encontrado! ")
    @ApiResponse(responseCode = "400", description = "CEP Inválido ")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<ViaCepDTO> buscarDadosCep(@PathVariable("cep") String cep) {

        return ResponseEntity.ok(viaCepService.buscarDadosEndereco(cep));
    }
}
