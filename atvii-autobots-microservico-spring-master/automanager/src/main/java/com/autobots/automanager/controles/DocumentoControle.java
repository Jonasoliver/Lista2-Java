package com.autobots.automanager.controles;

import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cliente/{clienteId}/documento")
public class DocumentoControle {
    @Autowired
    private ClienteRepositorio repositorio;

    @PostMapping("/adicionar")
    public ResponseEntity<EntityModel<Documento>> adicionarDocumento(@PathVariable long clienteId, @RequestBody Documento documento) {
        Cliente cliente = repositorio.findById(clienteId).orElse(null);
        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }

        cliente.getDocumentos().add(documento);
        repositorio.save(cliente);

        // Adiciona HATEOAS
        EntityModel<Documento> model = EntityModel.of(documento);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DocumentoControle.class)
                .adicionarDocumento(clienteId, documento)).withSelfRel();
        model.add(selfLink);

        return ResponseEntity.created(selfLink.toUri()).body(model);
    }

    @PutMapping("/atualizar/{documentoId}")
    public ResponseEntity<EntityModel<Documento>> atualizarDocumento(@PathVariable long clienteId, @PathVariable long documentoId, @RequestBody Documento documentoAtualizado) {
        Cliente cliente = repositorio.findById(clienteId).orElse(null);
        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }

        Documento documento = cliente.getDocumentos().stream()
                .filter(d -> d.getId().equals(documentoId))
                .findFirst()
                .orElse(null);

        if (documento == null) {
            return ResponseEntity.notFound().build();
        }

        documento.setTipo(documentoAtualizado.getTipo());
        documento.setNumero(documentoAtualizado.getNumero());
        repositorio.save(cliente);

        // Adiciona HATEOAS
        EntityModel<Documento> model = EntityModel.of(documento);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DocumentoControle.class)
                .atualizarDocumento(clienteId, documentoId, documentoAtualizado)).withSelfRel();
        model.add(selfLink);

        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/deletar/{documentoId}")
    public ResponseEntity<Void> deletarDocumento(@PathVariable long clienteId, @PathVariable long documentoId) {
        Cliente cliente = repositorio.findById(clienteId).orElse(null);
        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }

        Documento documento = cliente.getDocumentos().stream()
                .filter(d -> d.getId().equals(documentoId))
                .findFirst()
                .orElse(null);

        if (documento == null) {
            return ResponseEntity.notFound().build();
        }

        cliente.getDocumentos().remove(documento);
        repositorio.save(cliente);

        return ResponseEntity.noContent().build();
    }
}
