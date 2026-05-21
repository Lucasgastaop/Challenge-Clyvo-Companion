package br.com.fiap.clyvo_companion.controller;

import br.com.fiap.clyvo_companion.dto.LogSaudeRequestDTO;
import br.com.fiap.clyvo_companion.dto.LogSaudeResponseDTO;
import br.com.fiap.clyvo_companion.service.LogSaudeService;
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
@RequestMapping("/logs-saude")
public class LogSaudeController {

    private final LogSaudeService logSaudeService;

    public LogSaudeController(LogSaudeService logSaudeService) {
        this.logSaudeService = logSaudeService;
    }

    @GetMapping
    public Page<LogSaudeResponseDTO> listar(
            @RequestParam(required = false) Long idPet,
            @RequestParam(required = false) String metrica,
            @PageableDefault(size = 10, sort = "dtRegistro", direction = Sort.Direction.DESC) Pageable pageable) {
        return logSaudeService.listar(idPet, metrica, pageable);
    }

    @GetMapping("/{id}")
    public LogSaudeResponseDTO buscarPorId(@PathVariable Long id) {
        return logSaudeService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<LogSaudeResponseDTO> criar(@Valid @RequestBody LogSaudeRequestDTO dto) {
        LogSaudeResponseDTO criado = logSaudeService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.getIdLog())
                .toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public LogSaudeResponseDTO atualizar(@PathVariable Long id, @Valid @RequestBody LogSaudeRequestDTO dto) {
        return logSaudeService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        logSaudeService.excluir(id);
    }
}
