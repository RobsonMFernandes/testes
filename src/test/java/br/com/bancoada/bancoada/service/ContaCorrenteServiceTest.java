package br.com.bancoada.bancoada.service;

import br.com.bancoada.bancoada.entity.ContaCorrente;
import br.com.bancoada.bancoada.exception.ContaInativaException;
import br.com.bancoada.bancoada.exception.ContaInexistenteException;
import br.com.bancoada.bancoada.exception.ContaSemSaldoException;
import br.com.bancoada.bancoada.repository.ContaCorrenteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ContaCorrenteServiceTest {

    @Mock
    ContaCorrenteRepository repository;

    @InjectMocks
    private ContaCorrenteService service;

    int idConta = 1;
    int idOutraConta = 2;
    ContaCorrente contaCorrente;
    ContaCorrente contaCorrente2;

    @BeforeEach
    void setup() {
        contaCorrente = new ContaCorrente();
        contaCorrente.setTitular("maria");
        contaCorrente.setId(idConta);
        contaCorrente.setSaldo(new BigDecimal(100));

        contaCorrente2 = new ContaCorrente();
        contaCorrente2.setTitular("joao");
        contaCorrente2.setId(idOutraConta);
        contaCorrente2.setSaldo(new BigDecimal(30));
    }

    @Test
    void testaSeRetornaNovoSaldoCorretamente() {
        when(repository.findById(idConta)).thenReturn(Optional.of(contaCorrente));

        BigDecimal novoSaldo = service.sacar(idConta, new BigDecimal(50));
        assertEquals(new BigDecimal(50), novoSaldo);
    }

    @Test
    void testaSacarDeContaInexistente() {
        when(repository.findById(idConta)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,
                () -> service.sacar(idConta, BigDecimal.ONE));
    }

    @Test
    void testaConsultaSaldo() {
        when(repository.findById(idConta)).thenReturn(Optional.of(contaCorrente));

        BigDecimal saldo = service.consultarSaldo(idConta);

        assertEquals(new BigDecimal(100), saldo);
    }

    @Test
    void testaConsultarSaldoContaInexistente() {
        when(repository.findById(idConta)).thenReturn(Optional.empty());
        IllegalStateException exceptionRetornada = assertThrows(IllegalStateException.class,
                () -> service.consultarSaldo(idConta));
        assertEquals("Conta inexistente", exceptionRetornada.getMessage());
    }

    @Test
    void testaTransferirSeAsDuasContasExistem() {
        when(repository.findById(idConta)).thenReturn(Optional.empty());

        BigDecimal valor = new BigDecimal(20);

        IllegalStateException exceptionContaOrigem = assertThrows(IllegalStateException.class,
                () -> service.transferir(idConta,idOutraConta, valor));
        assertEquals("conta origem inexistente", exceptionContaOrigem.getMessage());

        when(repository.findById(idConta)).thenReturn(Optional.of(contaCorrente));
        when(repository.findById(idOutraConta)).thenReturn(Optional.empty());

        IllegalStateException exceptionContaDestino = assertThrows(IllegalStateException.class,
                () -> service.transferir(idConta,idOutraConta, valor));
        assertEquals("conta destino inexistente", exceptionContaDestino.getMessage());
    }

    @Test
    void testaTransferirSeAsDuasContasEstiveremAtivas() {

        contaCorrente.setAtiva(false);
        when(repository.findById(idConta)).thenReturn(Optional.of(contaCorrente));
        when(repository.findById(idOutraConta)).thenReturn(Optional.of(contaCorrente2));

        BigDecimal valor = new BigDecimal(20);

        ContaInativaException exception = assertThrows(ContaInativaException.class,
                () -> service.transferir(idConta, idOutraConta, valor));
        assertEquals("conta de origem inativa", exception.getMessage());

        contaCorrente.setAtiva(true);
        contaCorrente2.setAtiva(false);

        ContaInativaException exception2 = assertThrows(ContaInativaException.class,
                () -> service.transferir(idConta, idOutraConta, valor));
        assertEquals("conta de destino inativa", exception2.getMessage());
    }

    @Test
    void testaTransferirSeContaDeOrigemTemSaldoSuficiente() {

        contaCorrente.setSaldo(new BigDecimal(10));
        when(repository.findById(idConta)).thenReturn(Optional.of(contaCorrente));
        when(repository.findById(idOutraConta)).thenReturn(Optional.of(contaCorrente2));

        BigDecimal valor = new BigDecimal(11);

        ContaSemSaldoException exception = assertThrows(ContaSemSaldoException.class,
                () -> service.transferir(idConta, idOutraConta, valor));
        assertEquals("Saldo insuficiente", exception.getMessage());
    }

    @Test
    void testarTransferir() {

        contaCorrente.setSaldo(new BigDecimal(100));
        when(repository.findById(idConta)).thenReturn(Optional.of(contaCorrente));
        when(repository.findById(idOutraConta)).thenReturn(Optional.of(contaCorrente2));

        BigDecimal valor = new BigDecimal(20);

        BigDecimal novoSaldo = service.transferir(idConta, idOutraConta, valor);

        assertEquals(new BigDecimal(80), novoSaldo);
        assertEquals(new BigDecimal(50), contaCorrente2.getSaldo());
        assertEquals(new BigDecimal(80), contaCorrente.getSaldo());
    }

    @Test
    void testaDepositar() {
        when(repository.findById(idConta)).thenReturn(Optional.of(contaCorrente));
        when(repository.save(Mockito.any())).thenReturn(null);

        service.depositar(idConta, new BigDecimal(20));
    }

    @Test
    void testarMetodoDepositarComContaInativa() {
        contaCorrente.setAtiva(false);
        when(repository.findById(idConta)).thenReturn(Optional.of(contaCorrente));

        ContaInativaException exception = assertThrows(ContaInativaException.class,
                () -> service.depositar(idConta, new BigDecimal(20)));

        assertEquals("conta inativa", exception.getMessage());

    }

    @Test
    void testarDepositoContaInexiste() {
        when(repository.findById(idConta)).thenReturn(Optional.empty());

        IllegalStateException retornoEexception = assertThrows(IllegalStateException.class,
                () -> service.depositar(idConta, new BigDecimal(20)));
        assertEquals("Conta inexistente", retornoEexception.getMessage());
    }

}
