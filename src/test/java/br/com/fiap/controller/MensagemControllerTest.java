package br.com.fiap.controller;

import br.com.fiap.exception.MensagemNotFoundException;
import br.com.fiap.handler.GlobalExceptionHandler;
import br.com.fiap.helper.MensagemHelper;
import br.com.fiap.model.Mensagem;
import br.com.fiap.service.MensagemService;
import br.com.fiap.service.MensagemServiceImp;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Fail.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MensagemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MensagemService mensagemService;

    AutoCloseable mock;

    @BeforeEach
    void setup(){
        mock = MockitoAnnotations.openMocks(this);
        MensagemController mensagemController = new MensagemController(mensagemService);
        mockMvc = MockMvcBuilders.standaloneSetup(mensagemController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .addFilter((request, response, chain) -> {
                    response.setCharacterEncoding("UTF-8");
                    chain.doFilter(request, response);
                }, "/*")
                .build();
    }

    @AfterEach
    void teardown() throws Exception {
        mock.close();
    }

    @Test
    void devePermitirRegistrarMensagem() throws Exception {
        // Arrange
        var mensagemRequest = MensagemHelper.gerarMensagem();
        when(mensagemService.registrarMensagem(any(Mensagem.class)))
                .thenAnswer(i -> i.getArgument(0));

        // Act + Assert
        mockMvc.perform(post("/mensagens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(mensagemRequest)))
                .andDo(print())
                .andExpect(status().isCreated());
        verify(mensagemService, times(1))
                .registrarMensagem(any(Mensagem.class));
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void devePermitirObterMensagem() throws Exception {
        // Arrange
        var id = UUID.fromString("0e120ea6-1e90-4d1a-959f-738d6316c7a1");
        var mensagemResponse = MensagemHelper.gerarMensagem();
        mensagemResponse.setId(id);
        mensagemResponse.setDataCriacao(LocalDateTime.now());
        mensagemResponse.setDataAlteracao(LocalDateTime.now());

        when(mensagemService.obterMensagem(any(UUID.class)))
                .thenReturn(mensagemResponse);

        // Act + Assert
        mockMvc.perform(get("/mensagens/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(mensagemService, times(1)).obterMensagem(any(UUID.class));
    }

    @Test
    void deveGerarExcecaoAoObterMensagemComIdNaoExistente() throws Exception {
        // Arrange
        var id = UUID.fromString("0e120ea6-1e90-4d1a-959f-738d6316c7a1");

        when(mensagemService.obterMensagem(any(UUID.class)))
                .thenThrow(new MensagemNotFoundException("Mensagem não encontrada"));

        // Act + Assert
        mockMvc.perform(get("/mensagens/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(mensagemService, times(1)).obterMensagem(any(UUID.class));
    }

    @Test
    void devePermitirRemoverMensagem() {
        fail("Não implementado");
    }

}
