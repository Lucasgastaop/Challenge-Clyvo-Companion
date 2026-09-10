package br.com.fiap.clyvo_companion.controller.web;

import br.com.fiap.clyvo_companion.dto.PetRequestDTO;
import br.com.fiap.clyvo_companion.security.UsuarioDetails;
import br.com.fiap.clyvo_companion.service.PetService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/tutor/pets")
public class TutorPetController {

    private static final List<String> ESPECIES = List.of("Cachorro", "Gato", "Ave", "Roedor", "Outro");

    private final PetService petService;

    public TutorPetController(PetService petService) {
        this.petService = petService;
    }

    @ModelAttribute("pet")
    public PetRequestDTO pet(@AuthenticationPrincipal UsuarioDetails tutor) {
        PetRequestDTO dto = new PetRequestDTO();
        if (tutor != null) {
            dto.setIdUsuario(tutor.getIdUsuario());
        }
        return dto;
    }

    @GetMapping
    public String listar(Model model, @AuthenticationPrincipal UsuarioDetails tutor) {
        model.addAttribute("pets", petService.listarParaSelecao(tutor.getIdUsuario()));
        return "tutor/pets";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("especies", ESPECIES);
        return "tutor/pet-form";
    }

    @PostMapping
    public String salvar(
            @Valid @ModelAttribute("pet") PetRequestDTO dto,
            BindingResult bindingResult,
            @AuthenticationPrincipal UsuarioDetails tutor,
            Model model,
            RedirectAttributes redirectAttributes) {
        dto.setIdUsuario(tutor.getIdUsuario());
        if (WebFormSupport.falhouAoSalvar(bindingResult, () -> petService.criar(dto))) {
            model.addAttribute("especies", ESPECIES);
            return "tutor/pet-form";
        }

        redirectAttributes.addFlashAttribute("sucesso", "Pet cadastrado com sucesso.");
        return "redirect:/tutor/pets";
    }
}
