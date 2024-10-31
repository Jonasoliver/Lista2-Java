package com.autobots.automanager.controles;

import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cliente/{clienteId}/endereco")
public class EnderecoControle {

    @Autowired
    private ClienteRepositorio repositorio;

    @PostMapping("/adicionar")
    public ResponseEntity<EntityModel<Endereco>> adicionarEndereco(@PathVariable long clienteId, @RequestBody Endereco endereco) {
        Cliente cliente = repositorio.findById(clienteId).orElse(null);
        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }

        cliente.setEndereco(endereco);
        repositorio.save(cliente);

        // Adiciona HATEOAS
        EntityModel<Endereco> model = EntityModel.of(endereco);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EnderecoControle.class)
                .adicionarEndereco(clienteId, endereco)).withSelfRel();
        model.add(selfLink);

        return ResponseEntity.created(selfLink.toUri()).body(model);
    }

    @PutMapping("/atualizar/{enderecoId}")
    public ResponseEntity<EntityModel<Endereco>> atualizarEndereco(@PathVariable long clienteId, @PathVariable long enderecoId, @RequestBody Endereco enderecoAtualizado) {
        Cliente cliente = repositorio.findById(clienteId).orElse(null);
        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }

        Endereco endereco = cliente.getEndereco();
        if (endereco == null || !endereco.getId().equals(enderecoId)) {
            return ResponseEntity.notFound().build();
        }

        endereco.setEstado(enderecoAtualizado.getEstado());
        endereco.setNumero(enderecoAtualizado.getNumero());
        endereco.setCidade(enderecoAtualizado.getCidade());
        endereco.setBairro(enderecoAtualizado.getBairro());
        endereco.setRua(enderecoAtualizado.getRua());
        endereco.setInformacoesAdicionais(enderecoAtualizado.getInformacoesAdicionais());
        endereco.setCodigoPostal(enderecoAtualizado.getCodigoPostal());

        repositorio.save(cliente);

        // Adiciona HATEOAS
        EntityModel<Endereco> model = EntityModel.of(endereco);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(EnderecoControle.class)
                .atualizarEndereco(clienteId, enderecoId, enderecoAtualizado)).withSelfRel();
        model.add(selfLink);

        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/deletar/{enderecoId}")
    public ResponseEntity<Void> deletarEndereco(@PathVariable long clienteId, @PathVariable long enderecoId) {
        Cliente cliente = repositorio.findById(clienteId).orElse(null);
        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }

        Endereco endereco = cliente.getEndereco();
        if (endereco == null || !endereco.getId().equals(enderecoId)) {
            return ResponseEntity.notFound().build();
        }

        cliente.setEndereco(null);
        repositorio.save(cliente);

        return ResponseEntity.noContent().build();
    }
}
