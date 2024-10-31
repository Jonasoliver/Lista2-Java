package com.autobots.automanager.modelo;

import java.util.List;

import org.springframework.stereotype.Component; // Importar a anotação

import com.autobots.automanager.entidades.Telefone;

@Component // Anotação para registrar como bean
public class TelefoneAtualizador {
	private StringVerificadorNulo verificador = new StringVerificadorNulo();

	public void atualizar(Telefone telefone, Telefone atualizacao) {
		if (atualizacao != null) {
			if (!verificador.verificar(atualizacao.getDdd())) {
				telefone.setDdd(atualizacao.getDdd());
			}
			if (!verificador.verificar(atualizacao.getNumero())) {
				telefone.setNumero(atualizacao.getNumero());
			}
		}
	}

	public void atualizar(List<Telefone> telefones, List<Telefone> atualizacoes) {
		for (Telefone atualizacao : atualizacoes) {
			for (Telefone telefone : telefones) {
				if (atualizacao.getId() != null) {
					if (atualizacao.getId().equals(telefone.getId())) { // Alterado para equals()
						atualizar(telefone, atualizacao);
					}
				}
			}
		}
	}
}
