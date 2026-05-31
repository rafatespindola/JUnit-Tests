package br.com.fiap.service;

import br.com.fiap.helper.MensagemHelper;
import br.com.fiap.model.Mensagem;
import br.com.fiap.repository.MensagemRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@Sql(scripts = {"/db_load.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/db_clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class MensagemServiceIT {

    @Autowired
    private MensagemRepository mensagemRepository;
    @Autowired
    private MensagemService mensagemService;

    @Test
    void devePermitirRegistrarMensagem() {
        // Arrange
        var mensagem = MensagemHelper.gerarMensagem("Natalia", "Adora passear");

        // Act
        var mensagemRegistrada = mensagemService.registrarMensagem(mensagem);

        // Assert
        assertThat(mensagemRegistrada.getUsuario())
                .isEqualTo("Natalia");
        assertThat(mensagemRegistrada.getConteudo())
                .isEqualTo("Adora passear");
    }

    @Test
    void devePermitirObterMensagem() {
        // Arrange
        var id = UUID.fromString("3f59106e-b101-4cb4-8632-ad7c83a615fb");

        // Act
        var mensagemObtida = mensagemService.obterMensagem(id);

        // Assert
        assertThat(mensagemObtida)
                .isInstanceOf(Mensagem.class)
                .isNotNull();
        assertThat(mensagemObtida.getId())
                .isNotNull();
        assertThat(mensagemObtida.getUsuario())
                .isNotNull();
        assertThat(mensagemObtida.getConteudo())
                .isNotNull();
    }

    @Test
    void devePermitirRemoverMensagem() {
        // Arrange
        var id = UUID.fromString("3f59106e-b101-4cb4-8632-ad7c83a615fb");

        // Act
        mensagemService.deletarMensagem(id);

        // Assert
        assertThat(mensagemRepository.findById(id)).isEmpty();
    }

}