package com.academia.core.application.clientes;

import com.academia.core.domain.clientes.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteService {

    List<Cliente> listarClientes();

    Optional<Cliente> buscarPorId(Long id);

    Cliente criarCliente(Cliente cliente);

    Cliente atualizarCliente(Long id, Cliente clienteAtualizado);
}
