package br.com.fiap.clyvo_companion.service;

import br.com.fiap.clyvo_companion.dto.UsuarioRequestDTO;
import br.com.fiap.clyvo_companion.dto.UsuarioResponseDTO;
import br.com.fiap.clyvo_companion.exception.DuplicateResourceException;
import br.com.fiap.clyvo_companion.exception.ResourceNotFoundException;
import br.com.fiap.clyvo_companion.model.Usuario;
import br.com.fiap.clyvo_companion.repository.UsuarioRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "usuarios", key = "#id")
    public UsuarioResponseDTO buscarPorId(Long id) {
        return UsuarioResponseDTO.from(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listar(String nome, String email, String tpPerfil, Pageable pageable) {
        return usuarioRepository.buscarComFiltros(nome, email, tpPerfil, pageable)
                .map(UsuarioResponseDTO::from);
    }

    @Transactional
    @CacheEvict(value = "usuarios", allEntries = true)
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("E-mail já cadastrado: " + dto.getEmail());
        }

        Usuario usuario = Usuario.builder()
                .nomeUsuario(dto.getNomeUsuario())
                .email(dto.getEmail())
                .senha(dto.getSenha())
                .tpPerfil(dto.getTpPerfil())
                .dtCadastro(LocalDate.now())
                .build();

        return UsuarioResponseDTO.from(usuarioRepository.save(usuario));
    }

    @Transactional
    @CacheEvict(value = "usuarios", allEntries = true)
    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = buscarEntidade(id);
        usuarioRepository.findByEmail(dto.getEmail())
                .filter(u -> !u.getIdUsuario().equals(id))
                .ifPresent(u -> {
                    throw new DuplicateResourceException("E-mail já cadastrado: " + dto.getEmail());
                });

        usuario.setNomeUsuario(dto.getNomeUsuario());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha());
        usuario.setTpPerfil(dto.getTpPerfil());

        return UsuarioResponseDTO.from(usuarioRepository.save(usuario));
    }

    @Transactional
    @CacheEvict(value = "usuarios", allEntries = true)
    public void excluir(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    private Usuario buscarEntidade(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + id));
    }
}
