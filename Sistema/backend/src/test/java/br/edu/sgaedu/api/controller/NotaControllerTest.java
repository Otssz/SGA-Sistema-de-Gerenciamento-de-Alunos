package br.edu.sgaedu.api.controller;

import br.edu.sgaedu.api.dto.LancarNotaDTO;
import br.edu.sgaedu.aplicacao.NotaService;
import br.edu.sgaedu.dominio.entidade.Nota;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Teste de integração do NotaController — usa MockMvc (sem servidor HTTP real).
 * Verifica: serialização, validação de entrada, RBAC e delegação ao serviço.
 */
@WebMvcTest(NotaController.class)
@DisplayName("NotaController — POST /turmas/{id}/notas")
class NotaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotaService notaService;

    // Mocks necessários para o contexto de segurança carregar sem erro
    @MockBean
    private br.edu.sgaedu.infraestrutura.seguranca.ProvedorJWT provedorJWT;

    @MockBean
    private br.edu.sgaedu.infraestrutura.seguranca.FiltroJWT filtroJWT;

    @Test
    @DisplayName("deve retornar 201 quando PROFESSOR lança nota válida")
    @WithMockUser(username = "prof@escola.edu", roles = {"PROFESSOR"})
    void deveCriarNotaComSucesso() throws Exception {
        LancarNotaDTO dto = new LancarNotaDTO(1L, 2L, 8.5, "Boa prova");

        Nota notaMock = new Nota();
        notaMock.setValor(8.5);

        when(notaService.lancarNota(anyLong(), any(LancarNotaDTO.class), anyString(), anyString()))
                .thenReturn(notaMock);

        mockMvc.perform(post("/turmas/10/notas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.valor").value(8.5));

        verify(notaService).lancarNota(eq(10L), any(LancarNotaDTO.class), eq("prof@escola.edu"), anyString());
    }

    @Test
    @DisplayName("deve retornar 403 quando ALUNO tenta lançar nota")
    @WithMockUser(username = "aluno@escola.edu", roles = {"ALUNO"})
    void deveNegarAcessoParaAluno() throws Exception {
        LancarNotaDTO dto = new LancarNotaDTO(1L, 2L, 9.0, null);

        mockMvc.perform(post("/turmas/10/notas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(notaService);
    }

    @Test
    @DisplayName("deve retornar 400 quando nota está fora do intervalo 0–10")
    @WithMockUser(roles = {"PROFESSOR"})
    void deveRejeitarNotaForaDoIntervalo() throws Exception {
        LancarNotaDTO dto = new LancarNotaDTO(1L, 2L, 11.0, null);

        mockMvc.perform(post("/turmas/10/notas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(notaService);
    }

    @Test
    @DisplayName("deve retornar 400 quando alunoId está ausente")
    @WithMockUser(roles = {"PROFESSOR"})
    void deveRejeitarDTOSemAlunoId() throws Exception {
        String jsonSemAlunoId = "{\"avaliacaoId\":2,\"valor\":7.0}";

        mockMvc.perform(post("/turmas/10/notas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSemAlunoId)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("deve retornar 401 quando não há token JWT")
    void deveNegarAcessoSemAutenticacao() throws Exception {
        LancarNotaDTO dto = new LancarNotaDTO(1L, 2L, 7.0, null);

        mockMvc.perform(post("/turmas/10/notas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }
}
