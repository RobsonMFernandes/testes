package br.com.bancoada.bancoada.controller;

import br.com.bancoada.bancoada.entity.ContaCorrente;
import br.com.bancoada.bancoada.service.ContaCorrenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/conta-corrente")
@RequiredArgsConstructor
public class ContaCorrenteController {

    private final ContaCorrenteService service;

    @PostMapping("nova-conta")
    public ContaCorrente criarNovaConta(@RequestBody ContaCorrente contaCorrente) {
        contaCorrente = new ContaCorrente();
        contaCorrente.setId(idConta);

        return null;
    }

    // www.bancoada.com/conta-corrente/saldo?id=1
    @GetMapping("/saldo")
    public BigDecimal consultarSaldo(@RequestParam("id") int idConta) {
        BigDecimal saldo = service.consultarSaldo(idConta);
        return saldo;
    }

    // www.bancoada.com/conta-corrente/1/sacar/20
    @PostMapping("/{id_conta}/sacar/{valor}")
    public ResponseEntity<BigDecimal> sacar(@PathVariable("id_conta") int idconta, @PathVariable BigDecimal valor) {
        BigDecimal novoSaldo = service.sacar(idconta, valor);
        return ResponseEntity.ok(novoSaldo);
    }

    // www.bancoada.com/conta-corrente/1/transferir/2/20
    @PostMapping("/{id_conta_origem}/transferir/{id_conta_destino}/{valor}")
    public ResponseEntity<BigDecimal> transferir(@PathVariable("id_conta_origem") int idContaOrigem,
                                                 @PathVariable("id_conta_destino") int idContaDestino,
                                                 @PathVariable BigDecimal valor) {

        BigDecimal novoSaldoOrigem = service.transferir(idContaOrigem, idContaDestino, valor);
        return ResponseEntity.ok(novoSaldoOrigem);
    }

    @PutMapping("/{idConta}/depositar/{valor}")
    public ResponseEntity<Void> depositar(@PathVariable int idConta, @PathVariable BigDecimal valor) {
        service.depositar(idConta, valor);
        return ResponseEntity.ok().build();
    }

    /**
     *TRABALHO FINAL
     *
     * Implementar o teste unitário do criar conta;
     * Implementar o teste de integração do criar conta;
     *
     * Implementar o método criar conta no service;
     * Implementar o endpoint criar conta;
     *
     *
     * - requisitos
     * 1. um titular deve ser informado
     * 2. não é possível criar uma conta com zero ou menos reais, deve começar com um valor
     *
     * * bonus
     * 3. deve ser retornado um status 201 created em caso de sucesso com o objeto da nova conta com id
     */
}
