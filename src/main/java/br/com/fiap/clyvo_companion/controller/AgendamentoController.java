package br.com.fiap.clyvo_companion.controller;

import br.com.fiap.clyvo_companion.dto.AgendamentoRequestDTO;
import br.com.fiap.clyvo_companion.dto.AgendamentoResponseDTO;
import br.com.fiap.clyvo_companion.service.AgendamentoService;
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
@RequestMapping("/api/v1/agendamentos")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    public AgendamentoController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @GetMapping
    public Page<AgendamentoResponseDTO> listar(
            @RequestParam(required = false) Long idPet,
            @RequestParam(required = false) Long idClinica,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10, sort = "dtAgenda", direction = Sort.Direction.ASC) Pageable pageable) {
        return agendamentoService.listar(idPet, idClinica, status, pageable);
    }

    @GetMapping("/{id}")
    public AgendamentoResponseDTO buscarPorId(@PathVariable Long id) {
        return agendamentoService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<AgendamentoResponseDTO> criar(@Valid @RequestBody AgendamentoRequestDTO dto) {
        AgendamentoResponseDTO criado = agendamentoService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.getIdAgendamento())
                .toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public AgendamentoResponseDTO atualizar(@PathVariable Long id, @Valid @RequestBody AgendamentoRequestDTO dto) {
        return agendamentoService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        agendamentoService.excluir(id);
    }
}
