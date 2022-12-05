package org.example;

import org.example.dao.PessoaDAO;
import org.example.model.Pessoa;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {

        PessoaDAO dao = new PessoaDAO();
        dao.limparBanco();

        // Criando Pessoas

        Pessoa p1 = new Pessoa("12345678910", "Peter", "peter@avengers.com", "uncleBen123", LocalDate.of(2000, 10, 10));
        Pessoa p2 = new Pessoa("45612378912", "Mary Janne", "mj@dailybugle.com", "spider456", LocalDate.of(2001, 8, 4));
        Pessoa p3 = new Pessoa("15487564230", "Clark Kent", "kent@dailyplanet.com", "123456", LocalDate.of(1989, 8, 4));
        Pessoa p4 = new Pessoa("85498516261", "Bruce Wayne", "bruce@wayne.com", "morceginho", LocalDate.of(1988, 12, 14));
        dao.criarPessoa(p1);
        dao.criarPessoa(p2);
        dao.criarPessoa(p3);
        dao.criarPessoa(p4);

        // Buscando e exibindo pessoas por CPF
        System.out.println("\nBUSCANDO PESSOAS POR CPF=12345678910");
        System.out.println(dao.buscarPorCpf("12345678910"));

        // LISTANDO PESSOAS
        var pessoas = dao.listarPessoas();
        System.out.println("\nLISTANDO TODAS AS PESSOAS CADASTRADAS");
        pessoas.forEach(System.out::println);

        // Atualizando Pessoas
        p1.setNome("Peter Parker");
        p2.setNome("Mary Janne Wattson");
        dao.atualizarPessoa("12345678910", p1);
        dao.atualizarPessoa("45612378912", p2);
        System.out.println("\nMOSTRANDO DADOS DE PESSOAS APÓS ATUALIZAÇÃO");
        System.out.println(dao.buscarPorCpf("12345678910"));
        System.out.println(dao.buscarPorCpf("45612378912"));

        // Criando amizade entre pessoas

        dao.criarAmizade(p1, p2);
        dao.criarAmizade(p1, p3);
        dao.criarAmizade(p3, p4);

        System.out.println("\nLISTANDO AMIGOS DO CPF=12345678910");
        var amigos = dao.listarAmigos("12345678910");
        amigos.forEach(System.out::println);

        // Desfazendo amizade entre pessoas
        dao.desfazerAmizade(p1, p3);
        System.out.println("\nLISTANDO AMIGOS DO CPF=12345678910 APÓS DESFAZER AMIZADE(S)");
        amigos = dao.listarAmigos("12345678910");
        amigos.forEach(System.out::println);

        // Deletando pessoas
        dao.deletarPessoa("85498516261");
        System.out.println("\nLISTANDO PESSOAS APÓS EXCLUSÃO DO CPF=85498516261");
        pessoas = dao.listarPessoas();
        pessoas.forEach(System.out::println);

    }
}