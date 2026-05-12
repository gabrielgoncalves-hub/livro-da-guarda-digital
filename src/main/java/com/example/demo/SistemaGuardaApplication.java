package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.*;

import java.util.List;

@SpringBootApplication
public class SistemaGuardaApplication {
    public static void main(String[] args) {
        SpringApplication.run(SistemaGuardaApplication.class, args);
        System.out.println("\n === SISTEMA CONECTADO E BLINDADO (TABELAS CORRIGIDAS) === \n");
    }
}

// --- 1. ENTIDADES ---
enum StatusApresentacao { PRESENTE, ATRASADO, FALTA }

@Entity
class Sentinela {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int numero;
    private String nomeGuerra;
    @Enumerated(EnumType.STRING)
    private StatusApresentacao status;
    private String atividadeAtual = "Prontidão"; 

    public Sentinela() {}
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }
    public String getNomeGuerra() { return nomeGuerra; }
    public void setNomeGuerra(String nomeGuerra) { this.nomeGuerra = nomeGuerra; }
    public StatusApresentacao getStatus() { return status; }
    public void setStatus(StatusApresentacao status) { this.status = status; }
    public String getAtividadeAtual() { return atividadeAtual; }
    public void setAtividadeAtual(String atividadeAtual) { this.atividadeAtual = atividadeAtual; }
}

@Entity
class Alteracao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String horario;
    private String descricao;

    public Alteracao() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}

@Entity
class RegistroHistorico {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String dataServico;
    private String cmtGuarda;
    
    // CORREÇÃO AQUI: Padrão infalível para suportar até 5 milhões de caracteres
    @Column(length = 5000000) 
    private String dadosJson; 

    public RegistroHistorico() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDataServico() { return dataServico; }
    public void setDataServico(String dataServico) { this.dataServico = dataServico; }
    public String getCmtGuarda() { return cmtGuarda; }
    public void setCmtGuarda(String cmtGuarda) { this.cmtGuarda = cmtGuarda; }
    public String getDadosJson() { return dadosJson; }
    public void setDadosJson(String dadosJson) { this.dadosJson = dadosJson; }
}

class RelatorioFinal {
    private List<Sentinela> sentinelas;
    private List<Alteracao> alteracoes;

    public RelatorioFinal(List<Sentinela> sentinelas, List<Alteracao> alteracoes) {
        this.sentinelas = sentinelas;
        this.alteracoes = alteracoes;
    }
    public List<Sentinela> getSentinelas() { return sentinelas; }
    public List<Alteracao> getAlteracoes() { return alteracoes; }
}

// --- 2. REPOSITÓRIOS ---
interface SentinelaRepository extends JpaRepository<Sentinela, Long> {}
interface AlteracaoRepository extends JpaRepository<Alteracao, Long> {}
interface RegistroHistoricoRepository extends JpaRepository<RegistroHistorico, Long> {}

// --- 3. SERVIÇO ---
@Service
class GuardaService {
    private final SentinelaRepository sentinelaRepo;
    private final AlteracaoRepository alteracaoRepo;
    private final RegistroHistoricoRepository historicoRepo;

    public GuardaService(SentinelaRepository sentinelaRepo, AlteracaoRepository alteracaoRepo, RegistroHistoricoRepository historicoRepo) {
        this.sentinelaRepo = sentinelaRepo;
        this.alteracaoRepo = alteracaoRepo;
        this.historicoRepo = historicoRepo;
    }

    public void registrarSentinela(Sentinela sentinela) { sentinelaRepo.save(sentinela); }
    public void removerSentinela(Long id) { 
        Sentinela sentinela = sentinelaRepo.findById(id).orElseThrow(() -> new RuntimeException("IDOR barrado!"));
        sentinelaRepo.delete(sentinela); 
    }

    public void registrarAlteracao(Alteracao alteracao) { alteracaoRepo.save(alteracao); }
    public void removerAlteracao(Long id) { 
        Alteracao alteracao = alteracaoRepo.findById(id).orElseThrow(() -> new RuntimeException("IDOR barrado!"));
        alteracaoRepo.delete(alteracao); 
    }

    public void atualizarAtividade(Long id, String novaAtividade) {
        Sentinela s = sentinelaRepo.findById(id).orElse(null);
        if (s != null) {
            s.setAtividadeAtual(novaAtividade);
            sentinelaRepo.save(s);
        }
    }

    public RelatorioFinal gerarParteDaGuarda() {
        return new RelatorioFinal(sentinelaRepo.findAll(), alteracaoRepo.findAll());
    }

    public void iniciarNovaGuarda() {
        sentinelaRepo.deleteAll();
        alteracaoRepo.deleteAll();
    }

    public void salvarHistorico(RegistroHistorico historico) { historicoRepo.save(historico); }
    public List<RegistroHistorico> listarHistoricos() { return historicoRepo.findAll(); }

    public void removerHistorico(Long id) {
        historicoRepo.deleteById(id);
    }
}

// --- 4. CONTROLLER ---
@RestController
@RequestMapping("/api/livro-guarda")
class LivroGuardaController {
    private final GuardaService guardaService;

    public LivroGuardaController(GuardaService guardaService) {
        this.guardaService = guardaService;
    }

    @DeleteMapping("/historico/{id}")
    public String removerHistorico(@PathVariable Long id) {
        guardaService.removerHistorico(id);
        return "Registro de histórico removido!";
    }

    @PostMapping("/sentinela")
    public String adicionarSentinela(@RequestBody Sentinela sentinela) { guardaService.registrarSentinela(sentinela); return "Atirador registrado!"; }
    @DeleteMapping("/sentinela/{id}")
    public String removerSentinela(@PathVariable Long id) { guardaService.removerSentinela(id); return "Atirador removido!"; }
    @PutMapping("/sentinela/{id}/atividade")
    public String atualizarAtividade(@PathVariable Long id, @RequestBody String atividade) { guardaService.atualizarAtividade(id, atividade); return "Atividade atualizada!"; }
    @PostMapping("/alteracao")
    public String adicionarAlteracao(@RequestBody Alteracao alteracao) { guardaService.registrarAlteracao(alteracao); return "Alteração registrada!"; }
    @DeleteMapping("/alteracao/{id}")
    public String removerAlteracao(@PathVariable Long id) { guardaService.removerAlteracao(id); return "Alteração removida!"; }
    @GetMapping("/relatorio")
    public RelatorioFinal emitirRelatorio() { return guardaService.gerarParteDaGuarda(); }
    @DeleteMapping("/nova-guarda")
    public String novaGuarda() { guardaService.iniciarNovaGuarda(); return "Livro zerado."; }
    @PostMapping("/historico")
    public String salvarHistorico(@RequestBody RegistroHistorico historico) { guardaService.salvarHistorico(historico); return "Histórico salvo!"; }
    @GetMapping("/historico")
    public List<RegistroHistorico> listarHistoricos() { return guardaService.listarHistoricos(); }
}

// --- 5. CONFIGURAÇÃO DE SEGURANÇA ---
@Configuration
@EnableWebSecurity
class SecurityConfig {

    // NOVO: O Java vai procurar essa variável no servidor (ou no application.properties)
    @org.springframework.beans.factory.annotation.Value("${SENHA_SISTEMA:tg02009}")
    private String senhaSistema;

    @org.springframework.beans.factory.annotation.Value("${SENHA_SUB:sub02009}")
    private String senhaSubtenente;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Restringe a exclusão de histórico apenas para o cargo de Subtenente
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/livro-guarda/historico/**").hasRole("SUBTENENTE")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.permitAll())
            .logout(logout -> logout
                .logoutUrl("/api/logout") 
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/login?logout") 
            );
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails monitor = User.withDefaultPasswordEncoder()
            .username("monitor")
            .password(senhaSistema)
            .roles("MONITOR") // Papel padrão para operações do dia a dia
            .build();

        UserDetails subtenente = User.withDefaultPasswordEncoder()
            .username("subtenente")
            .password(senhaSubtenente)
            .roles("SUBTENENTE") // Único com permissão para deletar histórico
            .build();

        return new InMemoryUserDetailsManager(monitor, subtenente);
    }
}