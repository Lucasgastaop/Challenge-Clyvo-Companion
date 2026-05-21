package br.com.fiap.clyvo_companion.service;

import br.com.fiap.clyvo_companion.dto.PrescricaoRequestDTO;
import br.com.fiap.clyvo_companion.dto.PrescricaoResponseDTO;
import br.com.fiap.clyvo_companion.exception.ResourceNotFoundException;
import br.com.fiap.clyvo_companion.model.Pet;
import br.com.fiap.clyvo_companion.model.Prescricao;
import br.com.fiap.clyvo_companion.repository.PetRepository;
import br.com.fiap.clyvo_companion.repository.PrescricaoRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrescricaoService {

    private final PrescricaoRepository prescricaoRepository;
    private final PetRepository petRepository;

    public PrescricaoService(PrescricaoRepository prescricaoRepository, PetRepository petRepository) {
        this.prescricaoRepository = prescricaoRepository;
        this.petRepository = petRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "prescricoes", key = "#id")
    public PrescricaoResponseDTO buscarPorId(Long id) {
        return PrescricaoResponseDTO.from(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public Page<PrescricaoResponseDTO> listar(Long idPet, String medicamento, Pageable pageable) {
        return prescricaoRepository.buscarComFiltros(idPet, medicamento, pageable)
                .map(PrescricaoResponseDTO::from);
    }

    @Transactional
    @CacheEvict(value = "prescricoes", allEntries = true)
    public PrescricaoResponseDTO criar(PrescricaoRequestDTO dto) {
        Pet pet = petRepository.findById(dto.getIdPet())
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado: " + dto.getIdPet()));

        Prescricao prescricao = Prescricao.builder()
                .pet(pet)
                .nomeMedicamento(dto.getNomeMedicamento())
                .dsDosagem(dto.getDsDosagem())
                .frequenciaHoras(dto.getFrequenciaHoras())
                .dtInicio(dto.getDtInicio())
                .dtFim(dto.getDtFim())
                .build();

        return PrescricaoResponseDTO.from(prescricaoRepository.save(prescricao));
    }

    @Transactional
    @CacheEvict(value = "prescricoes", allEntries = true)
    public PrescricaoResponseDTO atualizar(Long id, PrescricaoRequestDTO dto) {
        Prescricao prescricao = buscarEntidade(id);
        Pet pet = petRepository.findById(dto.getIdPet())
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado: " + dto.getIdPet()));

        prescricao.setPet(pet);
        prescricao.setNomeMedicamento(dto.getNomeMedicamento());
        prescricao.setDsDosagem(dto.getDsDosagem());
        prescricao.setFrequenciaHoras(dto.getFrequenciaHoras());
        prescricao.setDtInicio(dto.getDtInicio());
        prescricao.setDtFim(dto.getDtFim());

        return PrescricaoResponseDTO.from(prescricaoRepository.save(prescricao));
    }

    @Transactional
    @CacheEvict(value = "prescricoes", allEntries = true)
    public void excluir(Long id) {
        if (!prescricaoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Prescrição não encontrada: " + id);
        }
        prescricaoRepository.deleteById(id);
    }

    private Prescricao buscarEntidade(Long id) {
        return prescricaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescrição não encontrada: " + id));
    }
}
