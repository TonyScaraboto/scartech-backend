package backend.service;

import backend.dto.User;
import backend.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class UserAuthService {
    private static final Logger logger = Logger.getLogger(UserAuthService.class.getName());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String usersDbPath = "users_db.json";

    /**
     * Registra um novo usuário
     */
    public User registrarUsuario(String email, String password) throws IOException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Senha deve ter no mínimo 6 caracteres");
        }

        // Verifica se email já existe
        if (usuarioExiste(email)) {
            throw new IllegalArgumentException("Este email já está registrado");
        }

        // Cria novo usuário
        String userId = UUID.randomUUID().toString();
        String passwordHash = hashPassword(password);
        User novoUsuario = new User(userId, email, passwordHash, LocalDateTime.now().toString());

        // Salva no arquivo
        List<User> usuarios = carregarUsuarios();
        usuarios.add(novoUsuario);
        salvarUsuarios(usuarios);

        // Cria arquivo de dados isolado para o novo usuário
        criarDadosUsuario(userId);

        logger.info("Novo usuário registrado: " + email);
        return novoUsuario;
    }

    /**
     * Autentica um usuário e retorna o token JWT
     */
    public String autenticar(String email, String password) throws IOException {
        if (email == null || password == null) {
            throw new IllegalArgumentException("Email e senha são obrigatórios");
        }

        List<User> usuarios = carregarUsuarios();
        for (User usuario : usuarios) {
            if (usuario.getEmail().equals(email)) {
                if (verificarSenha(password, usuario.getPasswordHash())) {
                    String token = JwtUtil.generateToken(usuario.getUserId(), usuario.getEmail());
                    logger.info("Usuário autenticado: " + email);
                    return token;
                } else {
                    logger.warning("Falha de autenticação: senha incorreta para " + email);
                    throw new IllegalArgumentException("Email ou senha incorretos");
                }
            }
        }

        logger.warning("Falha de autenticação: usuário não encontrado " + email);
        throw new IllegalArgumentException("Email ou senha incorretos");
    }

    /**
     * Obtém informações do usuário pelo ID
     */
    public User obterUsuario(String userId) throws IOException {
        List<User> usuarios = carregarUsuarios();
        for (User usuario : usuarios) {
            if (usuario.getUserId().equals(userId)) {
                return usuario;
            }
        }
        throw new IOException("Usuário não encontrado");
    }

    /**
     * Carrega todos os usuários do arquivo
     */
    private List<User> carregarUsuarios() throws IOException {
        File file = new File(usersDbPath);
        if (!file.exists()) {
            logger.info("Arquivo de usuários não existe, criando novo");
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(file, new TypeReference<List<User>>() {});
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao carregar usuários", e);
            return new ArrayList<>();
        }
    }

    /**
     * Salva usuários no arquivo
     */
    private void salvarUsuarios(List<User> usuarios) throws IOException {
        try {
            File file = new File(usersDbPath);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, usuarios);
            logger.info("Usuários salvos com sucesso");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao salvar usuários", e);
            throw e;
        }
    }

    /**
     * Verifica se um email já existe
     */
    private boolean usuarioExiste(String email) throws IOException {
        List<User> usuarios = carregarUsuarios();
        for (User usuario : usuarios) {
            if (usuario.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Hash simples de senha (em produção, usar BCrypt)
     */
    private String hashPassword(String password) {
        return Integer.toHexString(password.hashCode());
    }

    /**
     * Verifica se a senha corresponde ao hash
     */
    private boolean verificarSenha(String password, String hash) {
        return hashPassword(password).equals(hash);
    }

    /**
     * Valida um token JWT
     */
    public boolean validarToken(String token) {
        return JwtUtil.validateToken(token);
    }

    /**
     * Extrai userId de um token
     */
    public String extrairUserId(String token) {
        return JwtUtil.extractUserId(token);
    }

    /**
     * Extrai email de um token
     */
    public String extrairEmail(String token) {
        return JwtUtil.extractEmail(token);
    }

    /**
     * Cria arquivo de dados isolado para novo usuário
     */
    private void criarDadosUsuario(String userId) throws IOException {
        try {
            // Cria diretório user_data se não existir
            File userDataDir = new File("user_data");
            if (!userDataDir.exists()) {
                if (!userDataDir.mkdirs()) {
                    logger.warning("Falha ao criar diretório user_data");
                }
            }

            // Cria estrutura padrão vazia para o usuário
            Map<String, Object> userData = new LinkedHashMap<>();
            userData.put("userId", userId);
            userData.put("ordens", new ArrayList<>());
            userData.put("vendas", new ArrayList<>());
            userData.put("produtos", new ArrayList<>());
            userData.put("criadoEm", LocalDateTime.now().toString());
            userData.put("atualizadoEm", LocalDateTime.now().toString());

            // Salva arquivo isolado do usuário
            File userFile = new File("user_data/" + userId + ".json");
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(userFile, userData);

            logger.info("Dados isolados criados para usuário: " + userId);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao criar dados isolados do usuário: " + userId, e);
            throw e;
        }
    }
}
