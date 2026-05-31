package br.com.fiap.service;

import br.com.fiap.exception.MensagemNotFoundException;
import br.com.fiap.helper.MensagemHelper;
import br.com.fiap.model.Mensagem;
import br.com.fiap.repository.MensagemRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MensagemServiceTest {

    @Mock
    private MensagemRepository mensagemRepository;
    private MensagemService mensagemService;

    AutoCloseable mock;

    @BeforeEach
    void setup(){
        mock = MockitoAnnotations.openMocks(this);
        mensagemService = new MensagemServiceImp(mensagemRepository);
    }

    @AfterEach
    void teardown() throws Exception {
        mock.close();
    }

    @Test
    void devePermitirRegistrarMensagem() {
        // Arrange
        var mensagem = MensagemHelper.gerarMensagem("Natalia", "Adora passear");
        when(mensagemRepository.save(any(Mensagem.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        var mensagemRegistrada = mensagemService.registrarMensagem(mensagem);

        // Assert
        verify(mensagemRepository, times(1)).save(mensagem);
        assertThat(mensagemRegistrada.getUsuario())
                .isEqualTo("Natalia");
        assertThat(mensagemRegistrada.getConteudo())
                .isEqualTo("Adora passear");
    }

    @Test
    void naoDevePermitirRegistrarMensagemQuandoRepositorioFalhar() {
        // Arrange
        var mensagem = MensagemHelper.gerarMensagem("Natalia", "Adora passear");
        when(mensagemRepository.save(any(Mensagem.class)))
                .thenThrow(new RuntimeException("Erro ao salvar"));

        // Act + Assert
        assertThatThrownBy(() -> mensagemService.registrarMensagem(mensagem))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Erro ao salvar");
        verify(mensagemRepository, times(1)).save(mensagem);
    }

    @Test
    void devePermitirObterMensagemPorId() {
        // Arrange
        var id = UUID.randomUUID();
        var mensagem = MensagemHelper.gerarMensagem("Rafael", "I like to play");
        mensagem.setId(id);

        when(mensagemRepository.findById(any(UUID.class))).thenReturn(Optional.of(mensagem));

        // Act
        var mensagemObtida = mensagemService.obterMensagem(id);

        // Assert
        verify(mensagemRepository, times(1)).findById(id);
        assertThat(mensagemObtida.getId()).isEqualTo(id);
        assertThat(mensagemObtida.getConteudo()).isEqualTo("I like to play");
        assertThat(mensagemObtida.getUsuario()).isEqualTo("Rafael");
    }

    @Test
    void naoDevePermitirObterMensagemPorIdQuandoIdNaoExistir(){
        // Arrange
        var id = UUID.randomUUID();
        when(mensagemRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> mensagemService.obterMensagem(id))
                .isInstanceOf(MensagemNotFoundException.class)
                .hasMessage("Mensagem não encontrada");
        verify(mensagemRepository, times(1)).findById(id);
    }

    @Test
    void devePermitirObterMensagens() {
        // Arrange
        var mensagens = MensagemHelper.gerarListaMensagens();
        when(mensagemRepository.findAll()).thenReturn(mensagens);

        // Act
        var listaObtida = mensagemService.obterMensages();

        // Assert
        verify(mensagemRepository, times(1)).findAll();
        assertThat(listaObtida)
                .isNotNull()
                .hasSize(mensagens.size())
                .containsExactlyInAnyOrderElementsOf(mensagens);
    }

    @Test
    void naoDevePermitirObterListaDeMensagensQuandoRepositorioFalhar() {
        // Arrange
        when(mensagemRepository.findAll())
                .thenThrow(new RuntimeException("Erro ao obter lista de mensagem"));

        // Act + Assert
        assertThatThrownBy(() -> mensagemService.obterMensages())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Erro ao obter lista de mensagem");
        verify(mensagemRepository, times(1)).findAll();
    }

    @Test
    void devePermitirAlterarMensagem() {
        // Arrange
        UUID id = UUID.randomUUID();
        Mensagem mensagemExistente = MensagemHelper.gerarMensagem("Rafael", "Uma frase linda");
        mensagemExistente.setId(id);

        Mensagem mensagemNova = MensagemHelper.gerarMensagem("Rafael", "Uma frase alterada");

        when(mensagemRepository.findById(any(UUID.class))).thenReturn(Optional.of(mensagemExistente));
        when(mensagemRepository.save(any(Mensagem.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        var mensagemSalva = mensagemService.alterarMensagem(mensagemNova, id);

        // Assert
        verify(mensagemRepository, times(1)).findById(id);
        verify(mensagemRepository, times(1)).save(mensagemExistente);
        assertThat(mensagemSalva.getId()).isEqualTo(id);
        assertThat(mensagemSalva.getConteudo()).isEqualTo("Uma frase alterada");
        assertThat(mensagemSalva.getUsuario()).isEqualTo("Rafael");
    }

    @Test
    void naoDevePermitirAlterarMensagemQuandoIdNaoExistir() {
        // Arrange
        UUID id = UUID.randomUUID();
        Mensagem mensagemNova = MensagemHelper.gerarMensagem();
        when(mensagemRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> mensagemService.alterarMensagem(mensagemNova, id))
                .isInstanceOf(MensagemNotFoundException.class)
                .hasMessage("Mensagem não encontrada");
        verify(mensagemRepository, never()).save(any(Mensagem.class));
        verify(mensagemRepository, times(1)).findById(id);
    }

    @Test
    void devePermitirDeletarMensagem() {
        // Arrange
        var id = UUID.randomUUID();
        var mensagem = MensagemHelper.gerarMensagem("Rafael", "My name is Teles too");
        mensagem.setId(id);

        when(mensagemRepository.findById(any(UUID.class))).thenReturn(Optional.of(mensagem));
        doNothing().when(mensagemRepository).deleteById(any(UUID.class));

        // Act
        mensagemService.deletarMensagem(id);

        // Assert
        verify(mensagemRepository, times(1)).findById(id);
        verify(mensagemRepository, times(1)).deleteById(id);
    }

    @Test
    void naoDevePermitirDeletarMensagemQuandoIdNaoExistir() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(mensagemRepository.findById(any(UUID.class)))
                .thenReturn(Optional.empty());


        // Act + Assert
        assertThatThrownBy(() -> mensagemService.deletarMensagem(id))
                .isInstanceOf(MensagemNotFoundException.class)
                .hasMessage("Mensagem não encontrada");
        verify(mensagemRepository, never()).deleteById(any(UUID.class));
        verify(mensagemRepository, times(1)).findById(id);
    }
}
