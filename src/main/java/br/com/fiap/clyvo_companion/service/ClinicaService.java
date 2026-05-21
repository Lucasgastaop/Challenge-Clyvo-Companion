package br.com.fiap.clyvo_companion.service;

import br.com.fiap.clyvo_companion.dto.ClinicaRequestDTO;
import br.com.fiap.clyvo_companion.dto.ClinicaResponseDTO;
import br.com.fiap.clyvo_companion.exception.DuplicateResourceException;
import br.com.fiap.clyvo_companion.exception.ResourceNotFoundException;
import br.com.fiap.clyvo_companion.model.Clinica;
import br.com.fiap.clyvo_companion.repository.ClinicaRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClinicaService {

    private final ClinicaRepository clinicaRepository;

    public ClinicaService(ClinicaRepository clinicaRepository) {
        this.clinicaRepository = clinicaRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "clinicas", key = "#id")
    public ClinicaResponseDTO buscarPorId(Long id) {
        return ClinicaResponseDTO.from(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public Page<ClinicaResponseDTO> listar(String nome, String cnpj, Pageable pageable) {
        return clinicaRepository.buscarComFiltros(nome, cnpj, pageable)
                .map(ClinicaResponseDTO::from);
    }

    @Transactional
    @CacheEvict(value = "clinicas", allEntries = true)
    public ClinicaResponseDTO criar(ClinicaRequestDTO dto) {
        if (clinicaRepository.existsByCnpj(dto.getCnpj())) {
            throw new DuplicateResourceException("CNPJ já cadastrado: " + dto.getCnpj());
        }

        Clinica clinica = Clinica.builder()
                .nomeClinica(dto.getNomeClinica())
                .cnpj(dto.getCnpj())
                .endereco(dto.getEndereco())
                .telefone(dto.getTelefone())
                .build();

        return ClinicaResponseDTO.from(clinicaRepository.save(clinica));
    }

    @Transactional
    @CacheEvict(value = "clinicas", allEntries = true)
    public ClinicaResponseDTO atualizar(Long id, ClinicaRequestDTO dto) {
        Clinica clinica = buscarEntidade(id);
        clinicaRepository.findByCnpj(dto.getCnpj())
                .filter(c -> !c.getIdClinica().equals(id))
                .ifPresent(c -> {
                    throw new DuplicateResourceException("CNPJ já cadastrado: " + dto.getCnpj());
                });

        clinica.setNomeClinica(dto.getNomeClinica());
        clinica.setCnpj(dto.getCnpj());
        clinica.setEndereco(dto.getEndereco());
        clinica.setTelefone(dto.getTelefone());

        return ClinicaResponseDTO.from(clinicaRepository.save(clinica));
    }

    @Transactional
    @CacheEvict(value = "clinicas", allEntries = true)
    public void excluir(Long id) {
        if (!clinicaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Clínica não encontrada: " + id);
        }
        clinicaRepository.deleteById(id);
    }

    private Clinica buscarEntidade(Long id) {
        return clinicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clínica não encontrada: " + id));
    }
}
