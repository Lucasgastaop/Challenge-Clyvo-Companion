package br.com.fiap.clyvo_companion.controller;

import br.com.fiap.clyvo_companion.dto.PetRequestDTO;
import br.com.fiap.clyvo_companion.dto.PetResponseDTO;
import br.com.fiap.clyvo_companion.service.PetService;
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
@RequestMapping("/api/v1/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping
    public Page<PetResponseDTO> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String especie,
            @RequestParam(required = false) Long idUsuario,
            @PageableDefault(size = 10, sort = "nomePet", direction = Sort.Direction.ASC) Pageable pageable) {
        return petService.listar(nome, especie, idUsuario, pageable);
    }

    @GetMapping("/{id}")
    public PetResponseDTO buscarPorId(@PathVariable Long id) {
        return petService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<PetResponseDTO> criar(@Valid @RequestBody PetRequestDTO dto) {
        PetResponseDTO criado = petService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.idPet())
                .toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @PutMapping("/{id}")
    public PetResponseDTO atualizar(@PathVariable Long id, @Valid @RequestBody PetRequestDTO dto) {
        return petService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        petService.excluir(id);
    }
}
