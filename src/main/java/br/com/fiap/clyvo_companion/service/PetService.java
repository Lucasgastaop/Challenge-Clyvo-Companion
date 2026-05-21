package br.com.fiap.clyvo_companion.service;

import br.com.fiap.clyvo_companion.dto.PetRequestDTO;
import br.com.fiap.clyvo_companion.dto.PetResponseDTO;
import br.com.fiap.clyvo_companion.exception.ResourceNotFoundException;
import br.com.fiap.clyvo_companion.model.Pet;
import br.com.fiap.clyvo_companion.model.Usuario;
import br.com.fiap.clyvo_companion.repository.PetRepository;
import br.com.fiap.clyvo_companion.repository.UsuarioRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final UsuarioRepository usuarioRepository;

    public PetService(PetRepository petRepository, UsuarioRepository usuarioRepository) {
        this.petRepository = petRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "pets", key = "#id")
    public PetResponseDTO buscarPorId(Long id) {
        return PetResponseDTO.from(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public Page<PetResponseDTO> listar(String nome, String especie, Long idUsuario, Pageable pageable) {
        return petRepository.buscarComFiltros(nome, especie, idUsuario, pageable)
                .map(PetResponseDTO::from);
    }

    @Transactional
    @CacheEvict(value = "pets", allEntries = true)
    public PetResponseDTO criar(PetRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.idUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + dto.idUsuario()));

        Pet pet = Pet.builder()
                .usuario(usuario)
                .nomePet(dto.nomePet())
                .especie(dto.especie())
                .dtNascimento(dto.dtNascimento())
                .dtCadastro(LocalDate.now())
                .build();

        return PetResponseDTO.from(petRepository.save(pet));
    }

    @Transactional
    @CacheEvict(value = "pets", allEntries = true)
    public PetResponseDTO atualizar(Long id, PetRequestDTO dto) {
        Pet pet = buscarEntidade(id);
        Usuario usuario = usuarioRepository.findById(dto.idUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + dto.idUsuario()));

        pet.setUsuario(usuario);
        pet.setNomePet(dto.nomePet());
        pet.setEspecie(dto.especie());
        pet.setDtNascimento(dto.dtNascimento());

        return PetResponseDTO.from(petRepository.save(pet));
    }

    @Transactional
    @CacheEvict(value = "pets", allEntries = true)
    public void excluir(Long id) {
        if (!petRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pet não encontrado: " + id);
        }
        petRepository.deleteById(id);
    }

    private Pet buscarEntidade(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado: " + id));
    }
}
