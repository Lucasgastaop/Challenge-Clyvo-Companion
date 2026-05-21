package br.com.fiap.clyvo_companion.controller;

import br.com.fiap.clyvo_companion.dto.LogSistemaRequestDTO;
import br.com.fiap.clyvo_companion.dto.LogSistemaResponseDTO;
import br.com.fiap.clyvo_companion.service.LogSistemaService;
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
@RequestMapping("/logs-sistema")
public class LogSistemaController {

    private final LogSistemaService logSistemaService;

    public LogSistemaController(LogSistemaService logSistemaService) {
        this.logSistemaService = logSistemaService;
    }

    @GetMapping
    public Page<LogSistemaResponseDTO> listar(
            @RequestParam(required = false) String nomeProc,
            @RequestParam(required = false) Integer cdErro,
            @PageableDefault(size = 10, sort = "dtErro", direction = Sort.Direction.DESC) Pageable pageable) {
        return logSistemaService.listar(nomeProc, cdErro, pageable);
    }

    @GetMapping("/{id}")
    public LogSistemaResponseDTO buscarPorId(@PathVariable Long id) {
        return logSistemaService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<LogSistemaResponseDTO> criar(@Valid @RequestBody LogSistemaRequestDTO dto) {
        LogSistemaResponseDTO criado = logSistemaService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.getIdErro())
                .toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        logSistemaService.excluir(id);
    }
}
