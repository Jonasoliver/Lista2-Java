package com.autobots.automanager.controles;

import java.util.List;
import java.util.stream.Collectors;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.modelo.ClienteAtualizador;
import com.autobots.automanager.modelo.ClienteSelecionador;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/cliente")
public class ClienteControle {

	@Autowired
	private ClienteRepositorio repositorio;

	@Autowired
	private ClienteSelecionador selecionador;

	@GetMapping("/{id}")
	public EntityModel<Cliente> obterCliente(@PathVariable long id) {
		Cliente cliente = selecionador.selecionar(repositorio.findAll(), id);

		// Criação do modelo HATEOAS
		EntityModel<Cliente> clienteModel = EntityModel.of(cliente);

		// Adiciona link para o próprio cliente (self link)
		clienteModel.add(linkTo(methodOn(ClienteControle.class).obterCliente(id)).withSelfRel());
		clienteModel.add(linkTo(methodOn(ClienteControle.class).obterClientes()).withRel("todos-clientes"));

		return clienteModel;
	}

	@GetMapping
	public CollectionModel<EntityModel<Cliente>> obterClientes() {
		List<EntityModel<Cliente>> clientes = repositorio.findAll().stream()
				.map(cliente -> EntityModel.of(cliente,
						linkTo(methodOn(ClienteControle.class).obterCliente(cliente.getId())).withSelfRel(),
						linkTo(methodOn(ClienteControle.class).obterClientes()).withRel("todos-clientes")
				)).collect(Collectors.toList());

		return CollectionModel.of(clientes, linkTo(methodOn(ClienteControle.class).obterClientes()).withSelfRel());
	}

	@PostMapping("/cadastro")
	public EntityModel<Cliente> cadastrarCliente(@RequestBody Cliente cliente) {
		Cliente clienteSalvo = repositorio.save(cliente);

		return EntityModel.of(clienteSalvo,
				linkTo(methodOn(ClienteControle.class).obterCliente(clienteSalvo.getId())).withSelfRel(),
				linkTo(methodOn(ClienteControle.class).obterClientes()).withRel("todos-clientes"));
	}

	@PutMapping("/atualizar")
	public EntityModel<Cliente> atualizarCliente(@RequestBody Cliente atualizacao) {
		Cliente cliente = repositorio.findById(atualizacao.getId()).orElse(null);

		if (cliente != null) {
			ClienteAtualizador atualizador = new ClienteAtualizador();
			atualizador.atualizar(cliente, atualizacao);
			repositorio.save(cliente);
		}

		return EntityModel.of(cliente,
				linkTo(methodOn(ClienteControle.class).obterCliente(cliente.getId())).withSelfRel(),
				linkTo(methodOn(ClienteControle.class).obterClientes()).withRel("todos-clientes"));
	}

	@DeleteMapping("/excluir/{id}")
	public void excluirCliente(@PathVariable long id) {
		Cliente cliente = repositorio.findById(id).orElse(null);
		if (cliente != null) {
			repositorio.delete(cliente);
		}
	}
}
