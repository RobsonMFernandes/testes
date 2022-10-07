package br.com.bancoada.bancoada.service;

import br.com.bancoada.bancoada.entity.ContaCorrente;
import br.com.bancoada.bancoada.exception.ContaInativaException;
import br.com.bancoada.bancoada.exception.ContaSemSaldoException;
import br.com.bancoada.bancoada.repository.ContaCorrenteRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class ContaCorrenteService {

    private final ContaCorrenteRepository repository;

    public ContaCorrenteService(ContaCorrenteRepository repository) {
        this.repository = repository;
    }

    public ContaCorrente criarConta(ContaCorrente contaCorrente) {

        //Implementar o método criar conta no service;
        //1. um titular deve ser informado
        if (contaCorrente.getTitular() == null) {
            throw new IllegalArgumentException("Um Titular deve ser informado");
        }
        //2. não é possível criar uma conta com zero ou menos reais, deve começar com um valor
        if (contaCorrente.getSaldo() == null || contaCorrente.getSaldo().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ContaSemSaldoException("Saldo inicial deve ser maior que 0.");
        }

        return repository.save(contaCorrente);

    public BigDecimal sacar(int contaCorrenteId, BigDecimal valor) {

        ContaCorrente conta = repository.findById(contaCorrenteId)
                .orElseThrow();

        if (valor != null && conta.getSaldo().compareTo(valor) >= 0) {
            efetuarSaque(conta, valor);
            return conta.getSaldo();
        }

        throw new RuntimeException("Erro ao sacar");
    }

    public BigDecimal transferir(int contaOrigemId, int contaDestinoId, BigDecimal valor) {
        ContaCorrente contaOrigem = repository.findById(contaOrigemId)
                .orElseThrow(() -> new IllegalStateException("conta origem inexistente"));

        ContaCorrente contaDestino = repository.findById(contaDestinoId)
                .orElseThrow(() -> new IllegalStateException("conta destino inexistente"));

        if (!contaOrigem.isAtiva()) {
            throw new ContaInativaException("conta de origem inativa");
        }

        if (!contaDestino.isAtiva()) {
            throw new ContaInativaException("conta de destino inativa");
        }

        if (valor == null || contaOrigem.getSaldo().compareTo(valor) < 0) {
            throw new ContaSemSaldoException("Saldo insuficiente");
        }

        efetuarTransferencia(contaOrigem, contaDestino, valor);

        // retornar o novo saldo da conta origem
        return contaOrigem.getSaldo();
    }

    public BigDecimal consultarSaldo(int idConta) {

        Optional<ContaCorrente> optional = repository.findById(idConta);

        if (optional.isPresent()) {
            ContaCorrente conta = optional.get();

            return conta.getSaldo();
        }

        throw new IllegalStateException("Conta inexistente");
    }

    public void depositar(int idConta, BigDecimal valor) {
        ContaCorrente conta = repository.findById(idConta)
                .orElseThrow(() -> new IllegalStateException("Conta inexistente"));

        if (!conta.isAtiva()) {
            throw new ContaInativaException("conta inativa");
        }

        conta.setSaldo(conta.getSaldo().add(valor));
        repository.save(conta);
    }

    private void efetuarSaque(ContaCorrente conta, BigDecimal valor) {
        conta.setSaldo(conta.getSaldo().subtract(valor));
        repository.save(conta);
    }

    private void efetuarTransferencia(ContaCorrente origem, ContaCorrente dest, BigDecimal valor) {
        origem.setSaldo(origem.getSaldo().subtract(valor));
        dest.setSaldo(dest.getSaldo().add(valor));

        // atualizando banco de dados
        repository.save(origem);
        repository.save(dest);
    }
}
