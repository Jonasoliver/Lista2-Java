package com.autobots.automanager.controles;

import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cliente/{clienteId}/telefone")
public class TelefoneControle {
    @Autowired
    private ClienteRepositorio repositorio;

    @PostMapping("/adicionar")
    public ResponseEntity<EntityModel<Telefone>> adicionarTelefone(@PathVariable long clienteId, @RequestBody Telefone telefone) {
        Cliente cliente = repositorio.findById(clienteId).orElse(null);
        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }

        cliente.getTelefones().add(telefone);
        repositorio.save(cliente);

        // Adiciona HATEOAS
        EntityModel<Telefone> model = EntityModel.of(telefone);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TelefoneControle.class)
                .adicionarTelefone(clienteId, telefone)).withSelfRel();
        model.add(selfLink);

        return ResponseEntity.created(selfLink.toUri()).body(model);
    }

    @PutMapping("/atualizar/{telefoneId}")
    public ResponseEntity<EntityModel<Telefone>> atualizarTelefone(@PathVariable long clienteId, @PathVariable long telefoneId, @RequestBody Telefone telefoneAtualizado) {
        Cliente cliente = repositorio.findById(clienteId).orElse(null);
        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }

        Telefone telefone = cliente.getTelefones().stream()
                .filter(t -> t.getId().equals(telefoneId))
                .findFirst()
                .orElse(null);

        if (telefone == null) {
            return ResponseEntity.notFound().build();
        }

        telefone.setDdd(telefoneAtualizado.getDdd());
        telefone.setNumero(telefoneAtualizado.getNumero());
        repositorio.save(cliente);

        // Adiciona HATEOAS
        EntityModel<Telefone> model = EntityModel.of(telefone);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TelefoneControle.class)
                .atualizarTelefone(clienteId, telefoneId, telefoneAtualizado)).withSelfRel();
        model.add(selfLink);

        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/deletar/{telefoneId}")
    public ResponseEntity<Void> deletarTelefone(@PathVariable long clienteId, @PathVariable long telefoneId) {
        Cliente cliente = repositorio.findById(clienteId).orElse(null);
        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }

        Telefone telefone = cliente.getTelefones().stream()
                .filter(t -> t.getId().equals(telefoneId))
                .findFirst()
                .orElse(null);

        if (telefone == null) {
            return ResponseEntity.notFound().build();
        }

        cliente.getTelefones().remove(telefone);
        repositorio.save(cliente);

        return ResponseEntity.noContent().build();
    }
}
