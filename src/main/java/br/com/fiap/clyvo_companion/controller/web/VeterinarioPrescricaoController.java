package br.com.fiap.clyvo_companion.controller.web;

import br.com.fiap.clyvo_companion.dto.AgendamentoResponseDTO;
import br.com.fiap.clyvo_companion.dto.PrescricaoRequestDTO;
import br.com.fiap.clyvo_companion.exception.ResourceNotFoundException;
import br.com.fiap.clyvo_companion.service.AgendamentoService;
import br.com.fiap.clyvo_companion.service.PetService;
import br.com.fiap.clyvo_companion.service.PrescricaoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/vet/prescricoes")
public class VeterinarioPrescricaoController {

    private final PrescricaoService prescricaoService;
    private final PetService petService;
    private final AgendamentoService agendamentoService;

    public VeterinarioPrescricaoController(
            PrescricaoService prescricaoService,
            PetService petService,
            AgendamentoService agendamentoService) {
        this.prescricaoService = prescricaoService;
        this.petService = petService;
        this.agendamentoService = agendamentoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("prescricoes", prescricaoService.listarTodas());
        return "vet/prescricoes";
    }

    @GetMapping("/nova")
    public String nova(
            @RequestParam(required = false) Long idPet,
            @RequestParam(required = false) Long idAgendamento,
            Model model) {
        PrescricaoRequestDTO dto = new PrescricaoRequestDTO();
        dto.setDtInicio(LocalDate.now());

        AgendamentoResponseDTO agendamento = carregarAgendamento(idAgendamento);
        if (agendamento != null) {
            dto.setIdPet(agendamento.getIdPet());
            model.addAttribute("agendamento", agendamento);
        } else if (idPet != null) {
            dto.setIdPet(idPet);
        }

        model.addAttribute("prescricao", dto);
        preencherFormulario(model);
        return "vet/prescricao-form";
    }

    @PostMapping
    public String salvar(
            @Valid @ModelAttribute("prescricao") PrescricaoRequestDTO dto,
            BindingResult bindingResult,
            @RequestParam(required = false) Long idAgendamento,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (WebFormSupport.falhouAoSalvar(bindingResult, () -> prescricaoService.criar(dto))) {
            model.addAttribute("agendamento", carregarAgendamento(idAgendamento));
            preencherFormulario(model);
            return "vet/prescricao-form";
        }

        redirectAttributes.addFlashAttribute("sucesso", "Prescrição emitida com sucesso.");
        return "redirect:/vet/prescricoes";
    }

    private void preencherFormulario(Model model) {
        model.addAttribute("pets", petService.listarTodosParaSelecao());
    }

    private AgendamentoResponseDTO carregarAgendamento(Long idAgendamento) {
        if (idAgendamento == null) {
            return null;
        }
        try {
            return agendamentoService.buscarPorId(idAgendamento);
        } catch (ResourceNotFoundException ex) {
            return null;
        }
    }
}
