package br.edu.sgaedu.infraestrutura;

import br.edu.sgaedu.dominio.entidade.*;
import br.edu.sgaedu.infraestrutura.persistencia.repositorio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

/**
 * Cria dados de teste ao subir a aplicação (perfis dev/padrão).
 * Usuários: *@escola.edu / Senha@123
 * Estrutura: Curso → Turma EM1A-2024 → Avaliação "Prova 1 - Álgebra"
 */
@Component
@Profile("!prod")
public class InicializadorDados implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(InicializadorDados.class);
    private static final String SENHA_PADRAO = "Senha@123";

    private final UsuarioRepositorio usuarioRepo;
    private final CursoRepositorio cursoRepo;
    private final TurmaRepositorio turmaRepo;
    private final AvaliacaoRepositorio avaliacaoRepo;
    private final PasswordEncoder encoder;

    public InicializadorDados(UsuarioRepositorio usuarioRepo,
                              CursoRepositorio cursoRepo,
                              TurmaRepositorio turmaRepo,
                              AvaliacaoRepositorio avaliacaoRepo,
                              PasswordEncoder encoder) {
        this.usuarioRepo = usuarioRepo;
        this.cursoRepo = cursoRepo;
        this.turmaRepo = turmaRepo;
        this.avaliacaoRepo = avaliacaoRepo;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        criarUsuarios();
        criarEstrutura();
    }

    private void criarUsuarios() {
        if (usuarioRepo.existsByEmail("diretor@escola.edu")) {
            log.info("Usuários já existem — pulando criação.");
            return;
        }
        log.info("Criando usuários de teste...");
        String hash = encoder.encode(SENHA_PADRAO);
        criarDiretor(hash);
        criarSecretaria(hash);
        criarProfessor(hash);
        criarAluno(hash);
        log.info("Usuários criados: *@escola.edu / Senha@123");
    }

    private void criarEstrutura() {
        if (cursoRepo.existsByNome("Ensino Médio 1º Ano")) {
            log.info("Estrutura acadêmica já existe — pulando criação.");
            return;
        }
        log.info("Criando estrutura acadêmica (Curso → Turma → Avaliação)...");

        Curso curso = new Curso();
        curso.setNome("Ensino Médio 1º Ano");
        curso.setDescricao("Primeiro ano do Ensino Médio — grade 2024");
        curso.setCargaHorariaTotal(800);
        cursoRepo.save(curso);

        Turma turma = new Turma();
        turma.setCodigo("EM1A-2024");
        turma.setAnoLetivo("2024");
        turma.setTurno("Manhã");
        turma.setCurso(curso);
        turmaRepo.save(turma);

        Avaliacao av = new Avaliacao();
        av.setDescricao("Prova 1 - Álgebra");
        av.setDataAplicacao(LocalDate.of(2024, 3, 15));
        av.setPeso(1.0);
        av.setValorMaximo(10.0);
        av.setTurma(turma);
        avaliacaoRepo.save(av);

        log.info("Estrutura criada: Turma ID={}, Avaliação ID={}", turma.getId(), av.getId());
    }

    private void criarDiretor(String hash) {
        Diretor d = new Diretor();
        d.setNome("Carlos Diretor");
        d.setEmail("diretor@escola.edu");
        d.setSenhaHash(hash);
        d.setDepartamento("Direção Geral");
        usuarioRepo.save(d);
    }

    private void criarSecretaria(String hash) {
        Secretaria s = new Secretaria();
        s.setNome("Maria Secretaria");
        s.setEmail("secretaria@escola.edu");
        s.setSenhaHash(hash);
        s.setRamal("2001");
        usuarioRepo.save(s);
    }

    private void criarProfessor(String hash) {
        Professor p = new Professor();
        p.setNome("Ana Professora");
        p.setEmail("professor@escola.edu");
        p.setSenhaHash(hash);
        p.setEspecialidade("Matemática");
        p.setRegistro("REG-001");
        usuarioRepo.save(p);
    }

    private void criarAluno(String hash) {
        Aluno a = new Aluno();
        a.setNome("João Aluno");
        a.setEmail("aluno@escola.edu");
        a.setSenhaHash(hash);
        a.setMatricula("2024001");
        a.setDataNascimento(LocalDate.of(2008, 3, 15));
        usuarioRepo.save(a);
    }
}
