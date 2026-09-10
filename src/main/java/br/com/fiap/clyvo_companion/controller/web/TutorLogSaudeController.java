package br.com.fiap.clyvo_companion.controller.web;

import br.com.fiap.clyvo_companion.dto.LogSaudeRequestDTO;
import br.com.fiap.clyvo_companion.security.UsuarioDetails;
import br.com.fiap.clyvo_companion.service.LogSaudeService;
import br.com.fiap.clyvo_companion.service.MetricaSaudeValidator;
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

import java.time.LocalDateTime;

@Controller
@RequestMapping("/tutor/logs-saude")
public class TutorLogSaudeController {

    private final LogSaudeService logSaudeService;
    private final PetService petService;
    private final MetricaSaudeValidator metricaSaudeValidator;

    public TutorLogSaudeController(
            LogSaudeService logSaudeService,
            PetService petService,
            MetricaSaudeValidator metricaSaudeValidator) {
        this.logSaudeService = logSaudeService;
        this.petService = petService;
        this.metricaSaudeValidator = metricaSaudeValidator;
    }

    @GetMapping
    public String listar(Model model, @AuthenticationPrincipal UsuarioDetails tutor) {
        model.addAttribute("logs", logSaudeService.listarDoTutor(tutor.getIdUsuario()));
        return "tutor/logs-saude";
    }

    @GetMapping("/novo")
    public String novo(Model model, @AuthenticationPrincipal UsuarioDetails tutor) {
        LogSaudeRequestDTO dto = new LogSaudeRequestDTO();
        dto.setDtRegistro(agoraSemSegundos());
        preencherFormulario(model, tutor);
        model.addAttribute("logSaude", dto);
        return "tutor/log-saude-form";
    }

    @PostMapping
    public String salvar(
            @Valid @ModelAttribute("logSaude") LogSaudeRequestDTO dto,
            BindingResult bindingResult,
            @AuthenticationPrincipal UsuarioDetails tutor,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (WebFormSupport.falhouAoSalvar(bindingResult, () -> logSaudeService.criar(dto))) {
            preencherFormulario(model, tutor);
            return "tutor/log-saude-form";
        }

        redirectAttributes.addFlashAttribute("sucesso", "Log de saúde registrado com sucesso.");
        return "redirect:/tutor/logs-saude";
    }

    private void preencherFormulario(Model model, UsuarioDetails tutor) {
        model.addAttribute("pets", petService.listarParaSelecao(tutor.getIdUsuario()));
        model.addAttribute("metricas", metricaSaudeValidator.opcoesFormulario());
    }

    private LocalDateTime agoraSemSegundos() {
        return LocalDateTime.now().withSecond(0).withNano(0);
    }
}
