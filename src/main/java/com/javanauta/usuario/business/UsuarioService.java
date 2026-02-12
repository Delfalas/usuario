package com.javanauta.usuario.business;

import com.javanauta.usuario.business.converter.UsuarioConverter;
import com.javanauta.usuario.business.dto.EnderecoDTO;
import com.javanauta.usuario.business.dto.TelefoneDTO;
import com.javanauta.usuario.business.dto.UsuarioDTO;
import com.javanauta.usuario.infrastructure.entity.Endereco;
import com.javanauta.usuario.infrastructure.entity.Telefone;
import com.javanauta.usuario.infrastructure.entity.Usuario;
import com.javanauta.usuario.infrastructure.exceptions.ConflictException;
import com.javanauta.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.javanauta.usuario.infrastructure.repository.EnderecoRepository;
import com.javanauta.usuario.infrastructure.repository.TelefoneRepository;
import com.javanauta.usuario.infrastructure.repository.UsuarioRepository;
import com.javanauta.usuario.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EnderecoRepository enderecoRepository;
    private final TelefoneRepository telefoneRepository;

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO){
        emailExiste(usuarioDTO.getEmail());
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO); //converte para usuario
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario)); //salva usuario e converte para usuarioDTO
    }

    public void emailExiste(String email) {
        try {
            boolean existe = verificarEmailExistente(email);
            if (existe) {
                throw new RuntimeException("Email já cadastrado! " + email);
            }
        }catch (ConflictException e) {
            throw new ConflictException("Email já cadastrado! ", e.getCause());
        }
    }

    public boolean verificarEmailExistente(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public UsuarioDTO buscarUsuarioPorEmail(String email) {
        try {
            return usuarioConverter.paraUsuarioDTO(usuarioRepository.findByEmail(email).orElseThrow(
                    () -> new ResourceNotFoundException("Email não encontrado " + email)));
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Email não encontrado " + email);
        }
    }

    public void deletarUsuarioPorEmail(String email){
        usuarioRepository.deleteByEmail(email);
    }

    public UsuarioDTO atualizaDadosUsuario(String token, UsuarioDTO usuarioDTO){
        //Buscamos o email do usuário através do token
        String email = jwtUtil.extrairEmailToken(token.substring(7));

        //Verifica se a senha está nula no DTO, se houver mudança na senha, coloca criptografia novamente
        usuarioDTO.setSenha(usuarioDTO.getSenha() != null ? passwordEncoder.encode(usuarioDTO.getSenha()) : null);

        //Buscamos os dados do usuário no banco de dados
        Usuario usuarioEntity = usuarioRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("Email não encontrado! " + email));
        //Mesclou os dados que recebemos da requisição DTO com os dados do banco ao atualizar usuário
        Usuario usuario = usuarioConverter.updateUsuario(usuarioDTO, usuarioEntity);

        //Salvou os dados do usuário convertido e depois pegou o retorno e converteu novamente para usuário DTO
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));
    }

    //Método para atualizar Endereço
    public EnderecoDTO atualizarEndereco(Long idEndereco, EnderecoDTO enderecoDTO){
        Endereco enderecoEntity = enderecoRepository.findById(idEndereco).orElseThrow(
                () -> new ResourceNotFoundException("Endereço não encontrado!" + idEndereco));

        Endereco endereco = usuarioConverter.updateEndereco(enderecoDTO, enderecoEntity);
        return usuarioConverter.paraEnderecoDTO(enderecoRepository.save(endereco));
    }

    //Método para atualizar Telefone
    public TelefoneDTO atualizarTelefone(Long idTelefone, TelefoneDTO telefoneDTO){
        Telefone telefoneEntity = telefoneRepository.findById(idTelefone).orElseThrow(
                () -> new ResourceNotFoundException("Telefone não encontrado!" + idTelefone));

        Telefone telefone = usuarioConverter.updateTelefone(telefoneDTO, telefoneEntity);
        return usuarioConverter.paraTelefoneDTO(telefoneRepository.save(telefone));
    }

    //Método para cadastrar novo Endereço
    public EnderecoDTO cadastrarEndereco(String token, EnderecoDTO enderecoDTO){
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("Email não encontrado! " + email));

        Endereco endereco = usuarioConverter.paraEnderecoEntity(enderecoDTO, usuario.getId());
        Endereco enderecoEntity = enderecoRepository.save(endereco);
        return usuarioConverter.paraEnderecoDTO(enderecoEntity);
    }

    //Método para cadastrar novo Telefone
    public TelefoneDTO cadastrarTelefone(String token, TelefoneDTO telefoneDTO){
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("Email não encontrado! " + email));

        Telefone telefone = usuarioConverter.paraTelefoneEntity(telefoneDTO, usuario.getId());
        Telefone telefoneEntity = telefoneRepository.save(telefone);
        return usuarioConverter.paraTelefoneDTO(telefoneEntity);
    }

    //Método para deletar telefone
    public void deletarTelefone(String token, Long idTelefone) {

        // 1. Extrai email do token
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        // 2. Busca usuário
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("Usuário não encontrado"));
        // 3. Busca telefone
        Telefone telefone = telefoneRepository.findById(idTelefone).orElseThrow(() ->
                new ResourceNotFoundException("Telefone não encontrado: " + idTelefone));
        // 4. Valida pertencimento
        if (!telefone.getUsuario().getId().equals(usuario.getId())) {
            throw new ConflictException("Telefone não pertence ao usuário autenticado");
        }
        // 5. Deleta
        telefoneRepository.delete(telefone);
    }

    //Método para deletar endereco
    public void deletarEndereco(String token, Long idEndereco) {

        String email = jwtUtil.extrairEmailToken(token.substring(7));

        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("Usuário não encontrado"));

        Endereco endereco = enderecoRepository.findById(idEndereco).orElseThrow(() ->
                new ResourceNotFoundException("Endereço não encontrado: " + idEndereco));

        if (!endereco.getUsuario().getId().equals(usuario.getId())) {
            throw new ConflictException("Endereço não pertence ao usuário autenticado");
        }

        enderecoRepository.delete(endereco);
    }

    //CADASTRAR TELEFONE EM LOTE
    public List<TelefoneDTO> cadastrarTelefones(String token, List<TelefoneDTO> telefonesDTO) {

        String email = jwtUtil.extrairEmailToken(token.substring(7));

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado! " + email));

        // Converte lista de DTO para lista de Entity
        List<Telefone> telefones = telefonesDTO.stream()
                .map(dto -> usuarioConverter.paraTelefoneEntity(dto, usuario.getId()))
                .toList();

        // Salva todos de uma vez
        List<Telefone> telefonesSalvos = telefoneRepository.saveAll(telefones);

        // Converte de volta para DTO
        return telefonesSalvos.stream()
                .map(usuarioConverter::paraTelefoneDTO)
                .toList();
    }

    //CADASTRAR ENDEREÇO EM LOTE
    public List<EnderecoDTO> cadastrarEnderecos(String token, List<EnderecoDTO> enderecosDTO) {

        String email = jwtUtil.extrairEmailToken(token.substring(7));

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado! " + email));

        List<Endereco> enderecos = enderecosDTO.stream()
                .map(dto -> usuarioConverter.paraEnderecoEntity(dto, usuario.getId()))
                .toList();

        List<Endereco> enderecosSalvos = enderecoRepository.saveAll(enderecos);

        return enderecosSalvos.stream()
                .map(usuarioConverter::paraEnderecoDTO)
                .toList();
    }

}
