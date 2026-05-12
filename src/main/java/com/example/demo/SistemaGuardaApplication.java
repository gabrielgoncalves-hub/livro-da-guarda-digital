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
        System.out.println("\n === SISTEMA DO TG 02-009 - MÓDULO RONDAS ATIVADO === \n");
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
    private String funcao = "Sentinela (18h às 06h)"; 
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
    public String getFuncao() { return funcao; }
    public void setFuncao(String funcao) { this.funcao = funcao; }
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
class Visitante {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String rg;
    private String placaVeiculo;
    private String motivo;
    private String horaEntrada;
    private String horaSaida = "--:--"; 

    public Visitante() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getRg() { return rg; }
    public void setRg(String rg) { this.rg = rg; }
    public String getPlacaVeiculo() { return placaVeiculo; }
    public void setPlacaVeiculo(String placaVeiculo) { this.placaVeiculo = placaVeiculo; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(String horaEntrada) { this.horaEntrada = horaEntrada; }
    public String getHoraSaida() { return horaSaida; }
    public void setHoraSaida(String horaSaida) { this.horaSaida = horaSaida; }
}

@Entity
class Ronda {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String horarioPrevisto;
    private String horarioRealizado = "--:--";
    private String responsavel = "--";
    private boolean concluida = false;

    public Ronda() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getHorarioPrevisto() { return horarioPrevisto; }
    public void setHorarioPrevisto(String horarioPrevisto) { this.horarioPrevisto = horarioPrevisto; }
    public String getHorarioRealizado() { return horarioRealizado; }
    public void setHorarioRealizado(String horarioRealizado) { this.horarioRealizado = horarioRealizado; }
    public String getResponsavel() { return responsavel; }
    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }
    public boolean isConcluida() { return concluida; }
    public void setConcluida(boolean concluida) { this.concluida = concluida; }
}

@Entity
class RegistroHistorico {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String dataServico;
    private String cmtGuarda;
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
    private List<Visitante> visitantes;
    private List<Ronda> rondas;

    public RelatorioFinal(List<Sentinela> s, List<Alteracao> a, List<Visitante> v, List<Ronda> r) {
        this.sentinelas = s; this.alteracoes = a; this.visitantes = v; this.rondas = r;
    }
    public List<Sentinela> getSentinelas() { return sentinelas; }
    public List<Alteracao> getAlteracoes() { return alteracoes; }
    public List<Visitante> getVisitantes() { return visitantes; }
    public List<Ronda> getRondas() { return rondas; }
}

// --- 2. REPOSITÓRIOS ---
interface SentinelaRepository extends JpaRepository<Sentinela, Long> {}
interface AlteracaoRepository extends JpaRepository<Alteracao, Long> {}
interface VisitanteRepository extends JpaRepository<Visitante, Long> {}
interface RondaRepository extends JpaRepository<Ronda, Long> {}
interface RegistroHistoricoRepository extends JpaRepository<RegistroHistorico, Long> {}

// --- 3. SERVIÇO ---
@Service
class GuardaService {
    private final SentinelaRepository sentinelaRepo;
    private final AlteracaoRepository alteracaoRepo;
    private final VisitanteRepository visitanteRepo;
    private final RondaRepository rondaRepo;
    private final RegistroHistoricoRepository historicoRepo;

    public GuardaService(SentinelaRepository s, AlteracaoRepository a, VisitanteRepository v, RondaRepository r, RegistroHistoricoRepository h) {
        this.sentinelaRepo = s; this.alteracaoRepo = a; this.visitanteRepo = v; this.rondaRepo = r; this.historicoRepo = h;
    }

    public void registrarSentinela(Sentinela s) { sentinelaRepo.save(s); }
    public void removerSentinela(Long id) { sentinelaRepo.deleteById(id); }
    public void registrarAlteracao(Alteracao a) { alteracaoRepo.save(a); }
    public void removerAlteracao(Long id) { alteracaoRepo.deleteById(id); }
    public void atualizarAtividade(Long id, String n) {
        Sentinela s = sentinelaRepo.findById(id).orElse(null);
        if (s != null) { s.setAtividadeAtual(n); sentinelaRepo.save(s); }
    }

    public void registrarVisitante(Visitante v) { visitanteRepo.save(v); }
    public void registrarSaidaVisitante(Long id, String hora) {
        Visitante v = visitanteRepo.findById(id).orElse(null);
        if (v != null) { v.setHoraSaida(hora); visitanteRepo.save(v); }
    }
    public void removerVisitante(Long id) { visitanteRepo.deleteById(id); }

    public void registrarRonda(Ronda r) { rondaRepo.save(r); }
    public void realizarRonda(Long id, String responsavel, String horaRealizada) {
        Ronda r = rondaRepo.findById(id).orElse(null);
        if (r != null) {
            r.setResponsavel(responsavel);
            r.setHorarioRealizado(horaRealizada);
            r.setConcluida(true);
            rondaRepo.save(r);
        }
    }
    public void removerRonda(Long id) { rondaRepo.deleteById(id); }

    public RelatorioFinal gerarParteDaGuarda() {
        return new RelatorioFinal(sentinelaRepo.findAll(), alteracaoRepo.findAll(), visitanteRepo.findAll(), rondaRepo.findAll());
    }

    public void iniciarNovaGuarda() {
        sentinelaRepo.deleteAll();
        alteracaoRepo.deleteAll();
        visitanteRepo.deleteAll();
        rondaRepo.deleteAll();
    }

    public void salvarHistorico(RegistroHistorico h) { historicoRepo.save(h); }
    public List<RegistroHistorico> listarHistoricos() { return historicoRepo.findAll(); }
    public void removerHistorico(Long id) { historicoRepo.deleteById(id); }
}

// --- 4. CONTROLLER ---
@RestController
@RequestMapping("/api/livro-guarda")
class LivroGuardaController {
    private final GuardaService guardaService;
    public LivroGuardaController(GuardaService g) { this.guardaService = g; }

    @PostMapping("/sentinela") public String addS(@RequestBody Sentinela s) { guardaService.registrarSentinela(s); return "OK"; }
    @DeleteMapping("/sentinela/{id}") public String delS(@PathVariable Long id) { guardaService.removerSentinela(id); return "OK"; }
    @PutMapping("/sentinela/{id}/atividade") public String updA(@PathVariable Long id, @RequestBody String a) { guardaService.atualizarAtividade(id, a); return "OK"; }
    
    @PostMapping("/alteracao") public String addAlt(@RequestBody Alteracao a) { guardaService.registrarAlteracao(a); return "OK"; }
    @DeleteMapping("/alteracao/{id}") public String delAlt(@PathVariable Long id) { guardaService.removerAlteracao(id); return "OK"; }
    
    @PostMapping("/visitante") public String addV(@RequestBody Visitante v) { guardaService.registrarVisitante(v); return "OK"; }
    @PutMapping("/visitante/{id}/saida") public String outV(@PathVariable Long id, @RequestBody String h) { guardaService.registrarSaidaVisitante(id, h); return "OK"; }
    @DeleteMapping("/visitante/{id}") public String delV(@PathVariable Long id) { guardaService.removerVisitante(id); return "OK"; }

    @PostMapping("/ronda") public String addR(@RequestBody Ronda r) { guardaService.registrarRonda(r); return "OK"; }
    @PutMapping("/ronda/{id}/realizar") public String realR(@PathVariable Long id, @RequestBody Ronda r) { guardaService.realizarRonda(id, r.getResponsavel(), r.getHorarioRealizado()); return "OK"; }
    @DeleteMapping("/ronda/{id}") public String delR(@PathVariable Long id) { guardaService.removerRonda(id); return "OK"; }

    @GetMapping("/relatorio") public RelatorioFinal emitir() { return guardaService.gerarParteDaGuarda(); }
    @DeleteMapping("/nova-guarda") public String nova() { guardaService.iniciarNovaGuarda(); return "OK"; }
    @PostMapping("/historico") public String saveH(@RequestBody RegistroHistorico h) { guardaService.salvarHistorico(h); return "OK"; }
    @GetMapping("/historico") public List<RegistroHistorico> listH() { return guardaService.listarHistoricos(); }
    @DeleteMapping("/historico/{id}") public String delH(@PathVariable Long id) { guardaService.removerHistorico(id); return "OK"; }
    
    @GetMapping("/me") public String getU(org.springframework.security.core.Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUBTENENTE")) ? "PAINEL DO SUBTENENTE" : "PAINEL DO MONITOR";
    }
}

// --- 5. CONFIGURAÇÃO DE SEGURANÇA ---
@Configuration
@EnableWebSecurity
class SecurityConfig {
    @org.springframework.beans.factory.annotation.Value("${SENHA_SISTEMA:tg02009}") private String sM;
    @org.springframework.beans.factory.annotation.Value("${SENHA_SUB:sub02009}") private String sS;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/livro-guarda/historico/**").hasRole("SUBTENENTE")
                .anyRequest().authenticated()
            )
            .formLogin(f -> f.permitAll())
            .logout(l -> l.logoutUrl("/api/logout").invalidateHttpSession(true).deleteCookies("JSESSIONID").logoutSuccessUrl("/login?logout"));
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails m = User.withDefaultPasswordEncoder().username("monitor").password(sM).roles("MONITOR").build();
        UserDetails s = User.withDefaultPasswordEncoder().username("subtenente").password(sS).roles("SUBTENENTE").build();
        return new InMemoryUserDetailsManager(m, s);
    }
}