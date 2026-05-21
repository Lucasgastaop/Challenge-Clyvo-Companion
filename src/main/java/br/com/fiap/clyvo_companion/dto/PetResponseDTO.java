package br.com.fiap.clyvo_companion.dto;

import br.com.fiap.clyvo_companion.model.Pet;

import java.time.LocalDate;

public record PetResponseDTO(
        Long idPet,
        Long idUsuario,
        String nomePet,
        String especie,
        LocalDate dtNascimento,
        LocalDate dtCadastro
) {

    public static PetResponseDTO from(Pet pet) {
        return new PetResponseDTO(
                pet.getIdPet(),
                pet.getUsuario().getIdUsuario(),
                pet.getNomePet(),
                pet.getEspecie(),
                pet.getDtNascimento(),
                pet.getDtCadastro()
        );
    }
}
