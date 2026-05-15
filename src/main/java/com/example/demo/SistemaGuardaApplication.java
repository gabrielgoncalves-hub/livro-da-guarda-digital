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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.*;
import jakarta.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
public class SistemaGuardaApplication {
    public static void main(String[] args) {
        SpringApplication.run(SistemaGuardaApplication.class, args);
        System.out.println("\n === TG 02-009: IDS BLUE TEAM & ESCALA ALGORÍTMICA ATIVADOS === \n");
    }
}

// --- 1. ENTIDADES DE NEGÓCIO ---
enum StatusApresentacao { PRESENTE, ATRASADO, FALTA }

@Entity class Sentinela {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private int numero; private String nomeGuerra; private String funcao = "Sentinela (18h às 06h)"; 
    @Enumerated(EnumType.STRING) private StatusApresentacao status; private String atividadeAtual = "Prontidão"; 
    public Sentinela() {} public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public int getNumero() { return numero; } public void setNumero(int numero) { this.numero = numero; }
    public String getNomeGuerra() { return nomeGuerra; } public void setNomeGuerra(String nomeGuerra) { this.nomeGuerra = nomeGuerra; }
    public String getFuncao() { return funcao; } public void setFuncao(String funcao) { this.funcao = funcao; }
    public StatusApresentacao getStatus() { return status; } public void setStatus(StatusApresentacao status) { this.status = status; }
    public String getAtividadeAtual() { return atividadeAtual; } public void setAtividadeAtual(String atividadeAtual) { this.atividadeAtual = atividadeAtual; }
}

@Entity class Alteracao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String horario; private String descricao;
    public Alteracao() {} public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getHorario() { return horario; } public void setHorario(String horario) { this.horario = horario; }
    public String getDescricao() { return descricao; } public void setDescricao(String descricao) { this.descricao = descricao; }
}

@Entity class Visitante {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String nome; private String rg; private String placaVeiculo; private String motivo; private String horaEntrada; private String horaSaida = "--:--"; 
    public Visitante() {} public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; } public void setNome(String nome) { this.nome = nome; }
    public String getRg() { return rg; } public void setRg(String rg) { this.rg = rg; }
    public String getPlacaVeiculo() { return placaVeiculo; } public void setPlacaVeiculo(String placaVeiculo) { this.placaVeiculo = placaVeiculo; }
    public String getMotivo() { return motivo; } public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getHoraEntrada() { return horaEntrada; } public void setHoraEntrada(String horaEntrada) { this.horaEntrada = horaEntrada; }
    public String getHoraSaida() { return horaSaida; } public void setHoraSaida(String horaSaida) { this.horaSaida = horaSaida; }
}

@Entity class Ronda {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String horarioPrevisto; private String horarioRealizado = "--:--"; private String responsavel = "--"; private boolean concluida = false;
    public Ronda() {} public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getHorarioPrevisto() { return horarioPrevisto; } public void setHorarioPrevisto(String horarioPrevisto) { this.horarioPrevisto = horarioPrevisto; }
    public String getHorarioRealizado() { return horarioRealizado; } public void setHorarioRealizado(String horarioRealizado) { this.horarioRealizado = horarioRealizado; }
    public String getResponsavel() { return responsavel; } public void setResponsavel(String responsavel) { this.responsavel = responsavel; }
    public boolean isConcluida() { return concluida; } public void setConcluida(boolean concluida) { this.concluida = concluida; }
}

@Entity class RegistroHistorico {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String dataServico; private String cmtGuarda; @Column(length = 5000000) private String dadosJson; 
    public RegistroHistorico() {} public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getDataServico() { return dataServico; } public void setDataServico(String dataServico) { this.dataServico = dataServico; }
    public String getCmtGuarda() { return cmtGuarda; } public void setCmtGuarda(String cmtGuarda) { this.cmtGuarda = cmtGuarda; }
    public String getDadosJson() { return dadosJson; } public void setDadosJson(String dadosJson) { this.dadosJson = dadosJson; }
}

@Entity class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(unique = true) private String username; private String password; private String role;
    public Usuario() {}
    public String getUsername() { return username; } public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; } public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; } public void setRole(String role) { this.role = role; }
}

@Entity class LogAuditoria {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String dataHora; private String ipOrigem; private String usuarioAcao; private String descricaoAcao;
    public LogAuditoria() {}
    public void setDataHora(String dataHora) { this.dataHora = dataHora; } public void setIpOrigem(String ipOrigem) { this.ipOrigem = ipOrigem; }
    public void setUsuarioAcao(String usuarioAcao) { this.usuarioAcao = usuarioAcao; } public void setDescricaoAcao(String descricaoAcao) { this.descricaoAcao = descricaoAcao; }
}

// --- 2. DTOs ---
class RelatorioFinal {
    private List<Sentinela> sentinelas; private List<Alteracao> alteracoes; private List<Visitante> visitantes; private List<Ronda> rondas;
    public RelatorioFinal(List<Sentinela> s, List<Alteracao> a, List<Visitante> v, List<Ronda> r) { this.sentinelas = s; this.alteracoes = a; this.visitantes = v; this.rondas = r; }
    public List<Sentinela> getSentinelas() { return sentinelas; } public List<Alteracao> getAlteracoes() { return alteracoes; }
    public List<Visitante> getVisitantes() { return visitantes; } public List<Ronda> getRondas() { return rondas; }
}

class EstatisticasDTO {
    public int totalServicos = 0; public int totalVisitantes = 0; public int totalRondasPendentes = 0;
    public Map<String, Integer> statusTropa = new HashMap<>();
    public List<Map<String, Object>> rankingDisciplina = new ArrayList<>();
}

// --- 3. REPOSITÓRIOS ---
interface SentinelaRepository extends JpaRepository<Sentinela, Long> {}
interface AlteracaoRepository extends JpaRepository<Alteracao, Long> {}
interface VisitanteRepository extends JpaRepository<Visitante, Long> {}
interface RondaRepository extends JpaRepository<Ronda, Long> {}
interface RegistroHistoricoRepository extends JpaRepository<RegistroHistorico, Long> {}
interface UsuarioRepository extends JpaRepository<Usuario, Long> { Optional<Usuario> findByUsername(String username); }
interface LogAuditoriaRepository extends JpaRepository<LogAuditoria, Long> {}

// --- 4. INITIALIZER ---
@Component class DataInitializer implements org.springframework.boot.CommandLineRunner {
    private final UsuarioRepository usuarioRepo; private final PasswordEncoder encoder;
    public DataInitializer(UsuarioRepository u, PasswordEncoder e) { this.usuarioRepo = u; this.encoder = e; }
    @Override public void run(String... args) {
        if(usuarioRepo.count() == 0) {
            Usuario m = new Usuario(); m.setUsername("monitor"); m.setPassword(encoder.encode("tg02009")); m.setRole("ROLE_MONITOR"); usuarioRepo.save(m);
            Usuario s = new Usuario(); s.setUsername("subtenente"); s.setPassword(encoder.encode("sub02009")); s.setRole("ROLE_SUBTENENTE"); usuarioRepo.save(s);
        }
    }
}

// --- 5. SERVIÇOS (COM NOVO IDS) ---

@Service class IdsService {
    private final int MAX_TENTATIVAS = 5;
    private final long TEMPO_BLOQUEIO_MS = 2 * 60 * 60 * 1000; // 2 horas
    private Map<String, Integer> cacheTentativas = new ConcurrentHashMap<>();
    private Map<String, Long> cacheBloqueios = new ConcurrentHashMap<>();
    private int totalFalhasDetectadas = 0;

    public void registrarFalha(String ip) {
        totalFalhasDetectadas++;
        int tentativas = cacheTentativas.getOrDefault(ip, 0) + 1;
        cacheTentativas.put(ip, tentativas);
        if (tentativas >= MAX_TENTATIVAS) {
            cacheBloqueios.put(ip, System.currentTimeMillis() + TEMPO_BLOQUEIO_MS);
            System.out.println("🚨 IDS ALERTA: IP " + ip + " BLOQUEADO POR FORÇA BRUTA.");
        }
    }

    public boolean ipEstaBloqueado(String ip) {
        if (!cacheBloqueios.containsKey(ip)) return false;
        if (cacheBloqueios.get(ip) > System.currentTimeMillis()) return true;
        cacheBloqueios.remove(ip); cacheTentativas.remove(ip); // Expira o castigo
        return false;
    }

    public Map<String, Object> obterEstatisticasIds() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalFalhas", totalFalhasDetectadas);
        stats.put("ipsBloqueados", cacheBloqueios.size());
        return stats;
    }
}

@Service class GuardaService {
    private final SentinelaRepository sentinelaRepo; private final AlteracaoRepository alteracaoRepo; private final VisitanteRepository visitanteRepo;
    private final RondaRepository rondaRepo; private final RegistroHistoricoRepository historicoRepo; private final LogAuditoriaRepository logRepo;

    public GuardaService(SentinelaRepository s, AlteracaoRepository a, VisitanteRepository v, RondaRepository r, RegistroHistoricoRepository h, LogAuditoriaRepository l) {
        this.sentinelaRepo = s; this.alteracaoRepo = a; this.visitanteRepo = v; this.rondaRepo = r; this.historicoRepo = h; this.logRepo = l;
    }

    public void registrarSentinela(Sentinela s) { sentinelaRepo.save(s); } public void removerSentinela(Long id) { sentinelaRepo.deleteById(id); }
    public void registrarAlteracao(Alteracao a) { alteracaoRepo.save(a); } public void removerAlteracao(Long id) { alteracaoRepo.deleteById(id); }
    public void atualizarAtividade(Long id, String n) { Sentinela s = sentinelaRepo.findById(id).orElse(null); if(s != null) { s.setAtividadeAtual(n); sentinelaRepo.save(s); } }
    public void registrarVisitante(Visitante v) { visitanteRepo.save(v); } public void removerVisitante(Long id) { visitanteRepo.deleteById(id); }
    public void registrarSaidaVisitante(Long id, String h) { Visitante v = visitanteRepo.findById(id).orElse(null); if(v != null) { v.setHoraSaida(h); visitanteRepo.save(v); } }
    public void registrarRonda(Ronda r) { rondaRepo.save(r); } public void removerRonda(Long id) { rondaRepo.deleteById(id); }
    public void realizarRonda(Long id, String resp, String hora) { Ronda r = rondaRepo.findById(id).orElse(null); if(r != null) { r.setResponsavel(resp); r.setHorarioRealizado(hora); r.setConcluida(true); rondaRepo.save(r); } }

    public RelatorioFinal gerarParteDaGuarda() { return new RelatorioFinal(sentinelaRepo.findAll(), alteracaoRepo.findAll(), visitanteRepo.findAll(), rondaRepo.findAll()); }
    public void iniciarNovaGuarda() { sentinelaRepo.deleteAll(); alteracaoRepo.deleteAll(); visitanteRepo.deleteAll(); rondaRepo.deleteAll(); }
    public void salvarHistorico(RegistroHistorico h) { historicoRepo.save(h); }
    public List<RegistroHistorico> listarHistoricos() { return historicoRepo.findAll(); }

    public void removerHistoricoAuditado(Long id, String ip, String user) {
        LogAuditoria log = new LogAuditoria();
        log.setDataHora(new java.util.Date().toString()); log.setIpOrigem(ip); log.setUsuarioAcao(user); log.setDescricaoAcao("Exclusão Permanente do Registro ID: " + id);
        logRepo.save(log); historicoRepo.deleteById(id);
    }

    public EstatisticasDTO compilarEstatisticas(String filtroData) {
        EstatisticasDTO dto = new EstatisticasDTO();
        List<RegistroHistorico> hist = historicoRepo.findAll();
        
        if (filtroData != null && !filtroData.isEmpty() && !filtroData.equals("TODOS")) {
            hist = hist.stream().filter(h -> h.getDataServico().equals(filtroData)).toList();
        }

        dto.totalServicos = hist.size();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Integer> mapaInfracoes = new HashMap<>();

        for(RegistroHistorico h : hist) {
            try {
                JsonNode root = mapper.readTree(h.getDadosJson());
                JsonNode vis = root.get("visitantes"); if(vis != null && vis.isArray()) dto.totalVisitantes += vis.size();
                JsonNode ron = root.get("rondas");
                if(ron != null && ron.isArray()) { for(JsonNode r : ron) { if(r.has("concluida") && !r.get("concluida").asBoolean()) dto.totalRondasPendentes++; } }
                JsonNode sen = root.get("sentinelas");
                if(sen != null && sen.isArray()) {
                    for(JsonNode s : sen) {
                        String st = s.has("status") ? s.get("status").asText() : "PRESENTE";
                        dto.statusTropa.put(st, dto.statusTropa.getOrDefault(st, 0) + 1);
                        if(st.equals("FALTA") || st.equals("ATRASADO")) {
                            String num = s.has("numero") ? s.get("numero").asText() : "";
                            String nome = s.has("nomeGuerra") ? s.get("nomeGuerra").asText() : "";
                            String chaveIdentidade = "At " + num + " " + nome;
                            mapaInfracoes.put(chaveIdentidade, mapaInfracoes.getOrDefault(chaveIdentidade, 0) + 1);
                        }
                    }
                }
            } catch(Exception e) {}
        }
        
        List<Map.Entry<String, Integer>> listaOrdenada = new ArrayList<>(mapaInfracoes.entrySet());
        listaOrdenada.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        for(Map.Entry<String, Integer> entry : listaOrdenada) {
            Map<String, Object> r = new HashMap<>(); r.put("nome", entry.getKey()); r.put("ocorrencias", entry.getValue());
            dto.rankingDisciplina.add(r);
        }
        return dto;
    }

    // ALGORITMO GERADOR DE ESCALA
    public List<String> gerarEscalaPunitiva() {
        EstatisticasDTO dto = compilarEstatisticas("TODOS");
        return dto.rankingDisciplina.stream()
                .map(map -> map.get("nome") + " - Motivo: " + map.get("ocorrencias") + " Faltas/Atrasos acumulados")
                .limit(4) // Extrai os 4 piores para fechar as guarnições de final de semana
                .toList();
    }
}

// --- 6. SEGURANÇA E FILTROS TÁTICOS ---

@Component class IdsFilter extends org.springframework.web.filter.OncePerRequestFilter {
    private final IdsService ids;
    public IdsFilter(IdsService ids) { this.ids = ids; }
    @Override protected void doFilterInternal(HttpServletRequest req, jakarta.servlet.http.HttpServletResponse res, jakarta.servlet.FilterChain chain) throws jakarta.servlet.ServletException, java.io.IOException {
        String ip = req.getRemoteAddr();
        if (ids.ipEstaBloqueado(ip) && req.getRequestURI().contains("/login")) {
            res.setStatus(403);
            res.getWriter().write("ACESSO NEGADO (MODO IDS): IP " + ip + " BLOQUEADO POR TENTATIVAS SUSPEITAS.");
            return;
        }
        chain.doFilter(req, res);
    }
}

@Component class SecurityEventListener {
    private final IdsService ids;
    public SecurityEventListener(IdsService ids) { this.ids = ids; }
    
    @org.springframework.context.event.EventListener
    public void authFailed(org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent ev) {
        Object details = ev.getAuthentication().getDetails();
        if (details instanceof org.springframework.security.web.authentication.WebAuthenticationDetails) {
            String ip = ((org.springframework.security.web.authentication.WebAuthenticationDetails) details).getRemoteAddress();
            ids.registrarFalha(ip);
        }
    }
}

@Configuration @EnableWebSecurity
class SecurityConfig {
    @Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean public SecurityFilterChain filterChain(HttpSecurity http, IdsFilter idsFilter) throws Exception {
        http.addFilterBefore(idsFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
        http.csrf(c -> c.disable())
            .authorizeHttpRequests(a -> a
                .requestMatchers("/login.html").permitAll() 
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/livro-guarda/historico/**").hasRole("SUBTENENTE")
                .requestMatchers("/api/livro-guarda/estatisticas", "/api/livro-guarda/ids-status", "/api/livro-guarda/escala-punitiva").hasRole("SUBTENENTE")
                .anyRequest().authenticated()
            )
            .formLogin(f -> f.loginPage("/login.html").loginProcessingUrl("/login").defaultSuccessUrl("/", true).permitAll())
            .logout(l -> l.logoutUrl("/api/logout").invalidateHttpSession(true).deleteCookies("JSESSIONID").logoutSuccessUrl("/login.html?logout"));
        return http.build();
    }
}

@Service class CustomUserDetailsService implements UserDetailsService {
    private final UsuarioRepository repo; public CustomUserDetailsService(UsuarioRepository r) { this.repo = r; }
    @Override public UserDetails loadUserByUsername(String username) {
        Usuario u = repo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("DB Error"));
        return User.withUsername(u.getUsername()).password(u.getPassword()).authorities(u.getRole()).build();
    }
}

// --- 7. CONTROLLER ---
@RestController @RequestMapping("/api/livro-guarda")
class LivroGuardaController {
    private final GuardaService svc; private final IdsService ids;
    public LivroGuardaController(GuardaService g, IdsService i) { this.svc = g; this.ids = i; }

    @PostMapping("/sentinela") public String addS(@RequestBody Sentinela s) { svc.registrarSentinela(s); return "OK"; }
    @DeleteMapping("/sentinela/{id}") public String delS(@PathVariable Long id) { svc.removerSentinela(id); return "OK"; }
    @PutMapping("/sentinela/{id}/atividade") public String updA(@PathVariable Long id, @RequestBody String a) { svc.atualizarAtividade(id, a); return "OK"; }
    @PostMapping("/alteracao") public String addAlt(@RequestBody Alteracao a) { svc.registrarAlteracao(a); return "OK"; }
    @DeleteMapping("/alteracao/{id}") public String delAlt(@PathVariable Long id) { svc.removerAlteracao(id); return "OK"; }
    @PostMapping("/visitante") public String addV(@RequestBody Visitante v) { svc.registrarVisitante(v); return "OK"; }
    @PutMapping("/visitante/{id}/saida") public String outV(@PathVariable Long id, @RequestBody String h) { svc.registrarSaidaVisitante(id, h); return "OK"; }
    @DeleteMapping("/visitante/{id}") public String delV(@PathVariable Long id) { svc.removerVisitante(id); return "OK"; }
    @PostMapping("/ronda") public String addR(@RequestBody Ronda r) { svc.registrarRonda(r); return "OK"; }
    @PutMapping("/ronda/{id}/realizar") public String realR(@PathVariable Long id, @RequestBody Ronda r) { svc.realizarRonda(id, r.getResponsavel(), r.getHorarioRealizado()); return "OK"; }
    @DeleteMapping("/ronda/{id}") public String delR(@PathVariable Long id) { svc.removerRonda(id); return "OK"; }

    @GetMapping("/relatorio") public RelatorioFinal emitir() { return svc.gerarParteDaGuarda(); }
    @DeleteMapping("/nova-guarda") public String nova() { svc.iniciarNovaGuarda(); return "OK"; }
    @PostMapping("/historico") public String saveH(@RequestBody RegistroHistorico h) { svc.salvarHistorico(h); return "OK"; }
    @GetMapping("/historico") public List<RegistroHistorico> listH() { return svc.listarHistoricos(); }
    @DeleteMapping("/historico/{id}") public String delH(@PathVariable Long id, HttpServletRequest req, org.springframework.security.core.Authentication auth) { svc.removerHistoricoAuditado(id, req.getRemoteAddr(), auth.getName()); return "OK"; }

    @GetMapping("/estatisticas") public EstatisticasDTO getEst(@RequestParam(required = false) String data) { return svc.compilarEstatisticas(data); }
    
    // NOVOS ENDPOINTS DA INTELIGÊNCIA
    @GetMapping("/ids-status") public Map<String, Object> getIds() { return ids.obterEstatisticasIds(); }
    @GetMapping("/escala-punitiva") public List<String> getEscala() { return svc.gerarEscalaPunitiva(); }

    @GetMapping("/me") public String getU(org.springframework.security.core.Authentication auth) { return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUBTENENTE")) ? "PAINEL DO SUBTENENTE" : "PAINEL DO MONITOR"; }
}