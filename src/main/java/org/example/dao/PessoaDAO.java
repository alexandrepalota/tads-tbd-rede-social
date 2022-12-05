package org.example.dao;

import org.example.model.Pessoa;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.neo4j.driver.Values.parameters;

public class PessoaDAO {

    private Driver driver;

    public PessoaDAO() {
    }

    public void initDriver() {
        this.driver = GraphDatabase.driver("bolt://localhost:7687",
                AuthTokens.basic("neo4j", "password"));
    }

    public void criarPessoa(Pessoa pessoa) {
        if (this.buscarPorCpf(pessoa.getCpf()) != null) {
            System.out.println("Já existe uma pessoa com esse CPF");
            return;
        }
        initDriver();
        try (Session session = driver.session()) {
            session.run("CREATE (p:Pessoa {cpf: $cpf, nome: $nome, email: $email, senha: $senha, dataNascimento: $dataNascimento});",
                    parameters(
                            "cpf", pessoa.getCpf(),
                            "nome", pessoa.getNome(),
                            "email", pessoa.getEmail(),
                            "senha", pessoa.getSenha(),
                            "dataNascimento", pessoa.getDataNascimento()
                    )
            );
        } finally {
            driver.close();
        }
    }

    public void atualizarPessoa(String cpf, Pessoa pessoa) {
        if (this.buscarPorCpf(cpf) == null) {
            System.out.println("Não existe ninguém com este CPF");
            return;
        }
        initDriver();
        try (Session session = driver.session()) {
            session.run("MATCH (p:Pessoa {cpf: $cpf}) " +
                            "SET p.cpf = $novoCpf " +
                            "SET p.nome = $novoNome " +
                            "SET p.email = $novoEmail " +
                            "SET p.senha = $novaSenha " +
                            "SET p.dataNascimento = $novaDataNascimento",
                    parameters(
                            "cpf", cpf,
                            "novoCpf", pessoa.getCpf(),
                            "novoNome", pessoa.getNome(),
                            "novoEmail", pessoa.getEmail(),
                            "novaSenha", pessoa.getSenha(),
                            "novaDataNascimento", pessoa.getDataNascimento()
                    )
            );
        } finally {
            driver.close();
        }
    }

    public void deletarPessoa(String cpf) {
        if (this.buscarPorCpf(cpf) == null) {
            System.out.println("Não existe ninguém com este CPF");
            return;
        }
        initDriver();
        try (Session session = driver.session()) {
            session.run("MATCH (p:Pessoa {cpf: $cpf}) DETACH DELETE p RETURN *", parameters("cpf", cpf));
        } finally {
            driver.close();
        }
    }

    public void criarAmizade(Pessoa p1, Pessoa p2) {
        this.initDriver();
        try (Session session = driver.session()) {
            session.run("MATCH (p1:Pessoa) WHERE p1.cpf = $cpf1 MATCH (p2:Pessoa) WHERE p2.cpf = $cpf2 CREATE (p1)-[:EH_AMIGO]->(p2)",
                    parameters("cpf1", p1.getCpf(), "cpf2", p2.getCpf()));
        } finally {
            driver.close();
        }
    }

    public void desfazerAmizade(Pessoa p1, Pessoa p2) {
        this.initDriver();
        try (Session session = driver.session()) {
            session.run("match (p1: Pessoa {cpf:$cpf1})-[r:EH_AMIGO]->(p2: Pessoa {cpf:$cpf2}) DELETE r",
                    parameters("cpf1", p1.getCpf(), "cpf2", p2.getCpf()));
        } finally {
            driver.close();
        }
    }

    public Pessoa buscarPorCpf(String cpf) {
        this.initDriver();
        Pessoa pessoa = null;
        try (Session session = driver.session()) {
            var result = session.run("MATCH (p: Pessoa {cpf: $cpf}) " +
                    "RETURN p.cpf as CPF, p.nome as NOME, p.email as EMAIL, p.senha as SENHA, " +
                    "p.dataNascimento as DATA_NASCIMENTO", parameters("cpf", cpf));
            if (result.hasNext()) {
                var record = result.next();
                String nome = record.get("NOME").asString();
                String email = record.get("EMAIL").asString();
                String senha = record.get("SENHA").asString();
                LocalDate dataNascimento = record.get("DATA_NASCIMENTO").asLocalDate();
                pessoa = new Pessoa();
                pessoa.setCpf(cpf);
                pessoa.setNome(nome);
                pessoa.setEmail(email);
                pessoa.setSenha(senha);
                pessoa.setDataNascimento(dataNascimento);
            }
        } finally {
            driver.close();
        }
        return pessoa;
    }

    public List<Pessoa> listarPessoas() {
        this.initDriver();
        List<Pessoa> pessoas = new ArrayList<>();
        try (Session session = driver.session()) {
            var result = session.run("MATCH (p: Pessoa) " +
                    "RETURN p.cpf as CPF, p.nome as NOME, p.email as EMAIL, p.senha as SENHA, p.dataNascimento as DATA_NASCIMENTO");
            while (result.hasNext()) {
                Pessoa pessoa = new Pessoa();
                var record = result.next();
                String cpf = record.get("CPF").asString();
                String nome = record.get("NOME").asString();
                String email = record.get("EMAIL").asString();
                String senha = record.get("SENHA").asString();
                LocalDate dataNascimento = record.get("DATA_NASCIMENTO").asLocalDate();
                pessoa.setCpf(cpf);
                pessoa.setNome(nome);
                pessoa.setEmail(email);
                pessoa.setSenha(senha);
                pessoa.setDataNascimento(dataNascimento);
                pessoas.add(pessoa);
            }
        } finally {
            driver.close();
        }
        return pessoas;
    }

    public List<Pessoa> listarAmigos(String cpf) {
        this.initDriver();
        List<Pessoa> pessoas = new ArrayList<>();
        try (Session session = driver.session()) {
            var result = session.run("MATCH (p: Pessoa)-[rel:EH_AMIGO]-(:Pessoa {cpf: $cpf}) " +
                    "RETURN p.cpf as CPF, p.nome as NOME, p.email as EMAIL, p.senha as SENHA, p.dataNascimento as DATA_NASCIMENTO",
                    parameters("cpf", cpf));
            while (result.hasNext()) {
                Pessoa pessoa = new Pessoa();
                var record = result.next();
                String cpfAmigo = record.get("CPF").asString();
                String nome = record.get("NOME").asString();
                String email = record.get("EMAIL").asString();
                String senha = record.get("SENHA").asString();
                LocalDate dataNascimento = record.get("DATA_NASCIMENTO").asLocalDate();
                pessoa.setCpf(cpfAmigo);
                pessoa.setNome(nome);
                pessoa.setEmail(email);
                pessoa.setSenha(senha);
                pessoa.setDataNascimento(dataNascimento);
                pessoas.add(pessoa);
            }
        } finally {
            driver.close();
        }
        return pessoas;
    }

    public void limparBanco() {
        this.initDriver();
        try (Session session = driver.session()) {
            session.run("MATCH (p:Pessoa) DETACH DELETE p RETURN *");
        } finally {
            driver.close();
        }
    }

}
