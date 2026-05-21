package br.com.fiap.clyvo_companion.controller;

import br.com.fiap.clyvo_companion.dto.ClinicaRequestDTO;
import br.com.fiap.clyvo_companion.dto.ClinicaResponseDTO;
import br.com.fiap.clyvo_companion.service.ClinicaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/clinicas")
public class ClinicaController {

    private final ClinicaService clinicaService;

    public ClinicaController(ClinicaService clinicaService) {
        this.clinicaService = clinicaService;
    }

    @GetMapping
    public Page<ClinicaResponseDTO> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cnpj,
            @PageableDefault(size = 10, sort = "nomeClinica", direction = Sort.Direction.ASC) Pageable pageable) {
        return clinicaService.listar(nome, cnpj, pageable);
    }

    @GetMapping("/{id}")
    public ClinicaResponseDTO buscarPorId(@PathVariable Long id) {
        return clinicaService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<ClinicaResponseDTO> criar(@Valid @RequestBody ClinicaRequestDTO dto) {
        ClinicaResponseDTO criado = clinicaService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.getIdClinica())
                .toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public ClinicaResponseDTO atualizar(@PathVariable Long id, @Valid @RequestBody ClinicaRequestDTO dto) {
        return clinicaService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        clinicaService.excluir(id);
    }
}
