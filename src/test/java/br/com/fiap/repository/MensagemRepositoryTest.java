package br.com.fiap.repository;

import br.com.fiap.helper.MensagemHelper;
import br.com.fiap.model.Mensagem;
import jakarta.validation.constraints.Max;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class MensagemRepositoryTest {

    @Autowired
    private MensagemRepository mensagemRepository;

    @Test
    void devePermitirRegistrarMensagem() {
        var mensagem = MensagemHelper.gerarMensagem();
        var salva = mensagemRepository.save(mensagem);
        assertThat(salva.getId()).isNotNull();
        assertThat(salva.getUsuario()).isEqualTo(mensagem.getUsuario());
    }

    @Test
    void devePermitirConsultarMensagem() {
        var mensagemParaSalvar = MensagemHelper.gerarMensagem();
        var mensagemSalva = mensagemRepository.save(mensagemParaSalvar);
        var mensagemEncontrada = mensagemRepository.findById(mensagemParaSalvar.getId());
        assertThat(mensagemEncontrada).isPresent().contains(mensagemSalva);
    }

    @Test
    void devePermitirApagarMensagem(){
        var mensagem = MensagemHelper.gerarMensagem();
        var mensagemSalva = mensagemRepository.save(mensagem);
        mensagemRepository.deleteById(mensagemSalva.getId());
        var mensagemDeletada = mensagemRepository.findById(mensagemSalva.getId());
        assertThat(mensagemDeletada).isEmpty();
    }

    @Test
    void devePermitirListarMensagens() {
        var mensagens = MensagemHelper.gerarListaMensagens();
        mensagemRepository.saveAll(mensagens);
        var mensagensSalvas = mensagemRepository.findAll();
        assertThat(mensagensSalvas)
                .contains(mensagens.get(0), mensagens.get(1))
                .hasSizeGreaterThanOrEqualTo(2);
    }

}
