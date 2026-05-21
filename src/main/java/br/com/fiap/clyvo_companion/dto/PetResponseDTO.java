package br.com.fiap.clyvo_companion.dto;

import br.com.fiap.clyvo_companion.model.Pet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetResponseDTO {

    private Long idPet;
    private Long idUsuario;
    private String nomePet;
    private String especie;
    private LocalDate dtNascimento;
    private LocalDate dtCadastro;

    public static PetResponseDTO from(Pet pet) {
        PetResponseDTO dto = new PetResponseDTO();
        dto.setIdPet(pet.getIdPet());
        dto.setIdUsuario(pet.getUsuario().getIdUsuario());
        dto.setNomePet(pet.getNomePet());
        dto.setEspecie(pet.getEspecie());
        dto.setDtNascimento(pet.getDtNascimento());
        dto.setDtCadastro(pet.getDtCadastro());
        return dto;
    }
}
