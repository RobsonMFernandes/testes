package br.com.bancoada.bancoada.controller;

import br.com.bancoada.bancoada.exception.ContaInexistenteException;
import br.com.bancoada.bancoada.service.ContaCorrenteService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ContaCorrenteControllerTest {

    @Mock
    private ContaCorrenteService service;

    @InjectMocks
    private ContaCorrenteController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void consultaSaldoHttpTest() throws Exception {
        when(service.consultarSaldo(1)).thenReturn(new BigDecimal(20));

        mockMvc.perform(get("/conta-corrente/saldo").param("id", "1"))
                .andExpect(status().is2xxSuccessful());

        verify(service, times(1)).consultarSaldo(1);
    }

    @Test
    void sacarHttpTest() throws Exception {
        when(service.sacar(1, new BigDecimal(20))).thenReturn(BigDecimal.TEN);

        mockMvc.perform(post("/conta-corrente/1/sacar/20"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

        verify(service, times(1)).sacar(1, new BigDecimal(20));
    }

    @Test
    void transferirHttpTest() throws Exception {
        when(service.transferir(1, 2, new BigDecimal(20))).thenReturn(new BigDecimal(34));

        mockMvc.perform(post("/conta-corrente/1/transferir/2/20"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

        verify(service, times(1)).transferir(1, 2, new BigDecimal(20));
    }

    @Test
    void transferirHttpTestComContaInexistente() throws Exception {
        when(service.transferir(1, 2, new BigDecimal(20)))
                .thenThrow(new ContaInexistenteException("conta inexistente exception"));

        mockMvc.perform(post("/conta-corrente/1/transferir/2/20"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertEquals("conta inexistente exception", result.getResolvedException().getMessage()))
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ContaInexistenteException));

    }

    @Test
    void depositarHttpTest() throws Exception {
        // /conta-corrente/{id_conta}/depositar/{valor}

        String uri = "/conta-corrente/1/depositar/20";

        doNothing().when(service).depositar(1, new BigDecimal(20));

        mockMvc.perform(put(uri))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

        verify(service, times(1)).depositar(1, new BigDecimal(20));
    }

    @Test
    void depositarHttpTestComContaInexistente() throws Exception {
        // solucao do cleber
//        when(service.depositar(1, new BigDecimal(40)))
//                .thenThrow(ContaInexistenteException.class);

        doThrow(new ContaInexistenteException("conta inexistente"))
                .when(service).depositar(1, new BigDecimal(40));

        mockMvc.perform(put("/conta-corrente/1/depositar/40"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ContaInexistenteException))
                .andExpect(result -> Assertions.assertEquals("conta inexistente", result.getResolvedException().getMessage()));
    }
}
