package br.com.bancoada.bancoada.integracao;

import br.com.bancoada.bancoada.BancoAdaApplication;
import br.com.bancoada.bancoada.entity.ContaCorrente;
import br.com.bancoada.bancoada.repository.ContaCorrenteRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(classes = BancoAdaApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BancoAdaIntegracaoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContaCorrenteRepository repository;

    @Test
    void testarGetSaldo() throws Exception {
        // criar conta corrente
        ContaCorrente jorgeContaCorrente = new ContaCorrente();
        jorgeContaCorrente.setTitular("jorge");
        jorgeContaCorrente.setSaldo(new BigDecimal("22"));

        repository.save(jorgeContaCorrente);


        //http://localhost:8080/conta-corrente/saldo?id=1

        // performa uma chamada para o endpoint
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/conta-corrente/saldo").param("id", "1"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        BigDecimal saldoRetornado = new BigDecimal(body);

//        Assertions.assertEquals(new BigDecimal(22), saldoRetornado);
        Assertions.assertTrue(saldoRetornado.compareTo(new BigDecimal(22)) == 0);

    }

    @Test
    void depositar() throws Exception {

        // criando conta para teste
        ContaCorrente cc = new ContaCorrente();
        cc.setSaldo(new BigDecimal("22.39"));
        cc.setTitular("maria");

        //salvando e retornando conta de teste atualizada
        ContaCorrente contaCorrenteSalva = repository.save(cc);

        // testando chamada do endpoint depositar
        mockMvc.perform(MockMvcRequestBuilders
                .put("/conta-corrente/{id}/depositar/11", contaCorrenteSalva.getId()))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

        // buscando conta a qual foi depositada
        ContaCorrente cc1 = repository.findById(contaCorrenteSalva.getId())
                .orElseThrow();

        // testando se saldo foi atualizado
        Assertions.assertEquals(new BigDecimal("33.39"), cc1.getSaldo());
    }
}
