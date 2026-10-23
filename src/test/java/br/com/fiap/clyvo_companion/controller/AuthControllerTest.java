package br.com.fiap.clyvo_companion.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginComCredenciaisValidasRetornaUsuario() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"maria@email.com\",\"senha\":\"senha123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("maria@email.com"))
                .andExpect(jsonPath("$.tpPerfil").value("TUTOR"))
                .andExpect(jsonPath("$.nomeUsuario").value("Maria Silva"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(1800))
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    void loginComSenhaInvalidaRetorna401() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"maria@email.com\",\"senha\":\"errada\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void meSemSessaoRetorna401() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void mePetsELogsAceitamBearerERecusamBasic() throws Exception {
        Tokens tokens = login("maria@email.com", "senha123");

        mockMvc.perform(get("/auth/me").header("Authorization", "Bearer " + tokens.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("maria@email.com"))
                .andExpect(jsonPath("$.accessToken").doesNotExist());

        mockMvc.perform(get("/pets").header("Authorization", "Bearer " + tokens.accessToken()))
                .andExpect(status().isOk());
        mockMvc.perform(get("/logs-saude").header("Authorization", "Bearer " + tokens.accessToken()))
                .andExpect(status().isOk());
        mockMvc.perform(get("/logs-sistema").header("Authorization", "Bearer " + tokens.accessToken()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/auth/me").with(httpBasic("maria@email.com", "senha123")))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/pets").with(httpBasic("maria@email.com", "senha123")))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/logs-saude").with(httpBasic("maria@email.com", "senha123")))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/logs-sistema").with(httpBasic("maria@email.com", "senha123")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refreshRenovaAccessToken() throws Exception {
        Tokens tokens = login("maria@email.com", "senha123");

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + tokens.refreshToken() + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(1800))
                .andExpect(jsonPath("$.email").value("maria@email.com"))
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    void refreshComAccessTokenRetorna401() throws Exception {
        Tokens tokens = login("maria@email.com", "senha123");

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + tokens.accessToken() + "\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refreshTokenNaoAutenticaRotasProtegidas() throws Exception {
        Tokens tokens = login("maria@email.com", "senha123");

        mockMvc.perform(get("/pets").header("Authorization", "Bearer " + tokens.refreshToken()))
                .andExpect(status().isUnauthorized());
    }

    private Tokens login(String email, String senha) throws Exception {
        String body = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"senha\":\"" + senha + "\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return new Tokens(campo(body, "accessToken"), campo(body, "refreshToken"));
    }

    private static String campo(String json, String nome) {
        String chave = "\"" + nome + "\":\"";
        int inicio = json.indexOf(chave);
        if (inicio < 0) {
            throw new AssertionError("Campo ausente: " + nome);
        }
        int de = inicio + chave.length();
        return json.substring(de, json.indexOf('"', de));
    }

    private record Tokens(String accessToken, String refreshToken) {
    }

    @Test
    void criarUsuarioSemAutenticacaoRetorna201() throws Exception {
        String email = "novo.usuario." + System.currentTimeMillis() + "@fiap.com.br";
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nomeUsuario":"Novo Usuario","email":"%s","senha":"senha123","tpPerfil":"TUTOR"}
                                """.formatted(email)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.tpPerfil").value("TUTOR"));
    }

    @Test
    void listarUsuariosSemAutenticacaoRetorna401() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isUnauthorized());
    }
}
