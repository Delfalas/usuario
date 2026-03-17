package com.javanauta.usuario.business;

import com.javanauta.usuario.infrastructure.client.ViaCepClient;
import com.javanauta.usuario.infrastructure.client.ViaCepDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ViaCepService {

    private final ViaCepClient client;

    public ViaCepDTO buscarDadosEndereco(String cep) {
        return client.buscaDadosEndereco(processarCep(cep));
    }

    private String processarCep(String cep) {

        if (cep == null) {
            throw new IllegalArgumentException("CEP não pode ser nulo");
        }

        String cepFormatado = cep.replace(" ", "").
                replace("-", "");

        if(!cepFormatado.matches("\\d{8}")){
            throw new IllegalArgumentException("CEP inválido");
        }
        return cepFormatado;
    }

}
