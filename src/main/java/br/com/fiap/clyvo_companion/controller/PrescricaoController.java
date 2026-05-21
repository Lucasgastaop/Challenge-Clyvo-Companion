package br.com.fiap.clyvo_companion.controller;

import br.com.fiap.clyvo_companion.dto.PrescricaoRequestDTO;
import br.com.fiap.clyvo_companion.dto.PrescricaoResponseDTO;
import br.com.fiap.clyvo_companion.service.PrescricaoService;
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
@RequestMapping("/api/v1/prescricoes")
public class PrescricaoController {

    private final PrescricaoService prescricaoService;

    public PrescricaoController(PrescricaoService prescricaoService) {
        this.prescricaoService = prescricaoService;
    }

    @GetMapping
    public Page<PrescricaoResponseDTO> listar(
            @RequestParam(required = false) Long idPet,
            @RequestParam(required = false) String medicamento,
            @PageableDefault(size = 10, sort = "dtInicio", direction = Sort.Direction.DESC) Pageable pageable) {
        return prescricaoService.listar(idPet, medicamento, pageable);
    }

    @GetMapping("/{id}")
    public PrescricaoResponseDTO buscarPorId(@PathVariable Long id) {
        return prescricaoService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<PrescricaoResponseDTO> criar(@Valid @RequestBody PrescricaoRequestDTO dto) {
        PrescricaoResponseDTO criado = prescricaoService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.getIdPrescricao())
                .toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public PrescricaoResponseDTO atualizar(@PathVariable Long id, @Valid @RequestBody PrescricaoRequestDTO dto) {
        return prescricaoService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        prescricaoService.excluir(id);
    }
}
