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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PetService {

    private static final int LIMITE_SELECAO = 100;

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

    @Transactional(readOnly = true)
    public List<PetResponseDTO> listarParaSelecao(Long idUsuario) {
        return petRepository.buscarComFiltros(
                        null,
                        null,
                        idUsuario,
                        PageRequest.of(0, LIMITE_SELECAO, Sort.by("nomePet")))
                .map(PetResponseDTO::from)
                .getContent();
    }

    @Transactional(readOnly = true)
    public List<PetResponseDTO> listarTodosParaSelecao() {
        return listarParaSelecao(null);
    }

    @Transactional
    @CacheEvict(value = {"pets", "petsResumo"}, allEntries = true)
    public PetResponseDTO criar(PetRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + dto.getIdUsuario()));

        Pet pet = Pet.builder()
                .usuario(usuario)
                .nomePet(dto.getNomePet())
                .especie(dto.getEspecie())
                .dtNascimento(dto.getDtNascimento())
                .dtCadastro(LocalDate.now())
                .build();

        return PetResponseDTO.from(petRepository.save(pet));
    }

    @Transactional
    @CacheEvict(value = {"pets", "petsResumo"}, allEntries = true)
    public PetResponseDTO atualizar(Long id, PetRequestDTO dto) {
        Pet pet = buscarEntidade(id);
        Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + dto.getIdUsuario()));

        pet.setUsuario(usuario);
        pet.setNomePet(dto.getNomePet());
        pet.setEspecie(dto.getEspecie());
        pet.setDtNascimento(dto.getDtNascimento());

        return PetResponseDTO.from(petRepository.save(pet));
    }

    @Transactional
    @CacheEvict(value = {"pets", "petsResumo"}, allEntries = true)
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
