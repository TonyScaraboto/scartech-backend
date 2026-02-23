package backend.service;

import backend.dto.Fatura;
import backend.util.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

public class FaturamentoService {
    private static final Logger logger = Logger.getLogger(FaturamentoService.class.getName());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String dbFilePath;

    public FaturamentoService() {
        File pythonDir = FileUtil.getPythonDirectory();
        this.dbFilePath = new File(pythonDir, "faturamento_db.json").getAbsolutePath();
    }

    private Map<String, Object> loadDatabase() throws IOException {
        File dbFile = new File(dbFilePath);
        if (!dbFile.exists()) {
            logger.warning("Banco de dados não encontrado. Criando novo...");
            return createEmptyDatabase();
        }
        return objectMapper.readValue(dbFile, new TypeReference<Map<String, Object>>() {});
    }

    private Map<String, Object> createEmptyDatabase() {
        Map<String, Object> db = new HashMap<>();
        db.put("faturas", new ArrayList<>());
        db.put("ordens", new ArrayList<>());
        db.put("vendas", new ArrayList<>());
        try {
            saveDatabase(db);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro ao criar banco de dados", e);
        }
        return db;
    }

    private void saveDatabase(Map<String, Object> db) throws IOException {
        File dbFile = new File(dbFilePath);
        dbFile.getParentFile().mkdirs();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(dbFile, db);
        logger.info("Banco de dados salvo com sucesso");
    }

    public Fatura criarFatura(Fatura fatura) throws IOException {
        if (fatura.getValor() <= 0) {
            throw new IllegalArgumentException("Valor da fatura deve ser maior que zero");
        }
        
        Map<String, Object> db = loadDatabase();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> faturas = (List<Map<String, Object>>) db.get("faturas");
        
        if (fatura.getId() == null) {
            fatura.setId(UUID.randomUUID().toString());
        }
        if (fatura.getMes() == null || fatura.getAno() == null) {
            LocalDate hoje = LocalDate.now();
            fatura.setMes(hoje.getMonthValue());
            fatura.setAno(hoje.getYear());
        }
        if (fatura.getStatus() == null) {
            fatura.setStatus("pendente");
        }
        if (fatura.getDataEmissao() == null) {
            fatura.setDataEmissao(LocalDate.now().toString());
        }
        
        faturas.add(objectMapper.convertValue(fatura, new TypeReference<Map<String, Object>>() {}));
        saveDatabase(db);
        
        logger.info("Fatura criada: " + fatura.getId());
        return fatura;
    }

    public List<Fatura> listarFaturas(Integer mes, Integer ano) throws IOException {
        Map<String, Object> db = loadDatabase();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> faturas = (List<Map<String, Object>>) db.get("faturas");
        
        List<Fatura> resultado = new ArrayList<>();
        LocalDate hoje = LocalDate.now();
        int mesAtual = mes != null ? mes : hoje.getMonthValue();
        int anoAtual = ano != null ? ano : hoje.getYear();
        
        for (Map<String, Object> f : faturas) {
            Integer fMes = ((Number) f.getOrDefault("mes", mesAtual)).intValue();
            Integer fAno = ((Number) f.getOrDefault("ano", anoAtual)).intValue();
            
            if (fMes == mesAtual && fAno == anoAtual) {
                resultado.add(objectMapper.convertValue(f, Fatura.class));
            }
        }
        
        logger.info("Listadas " + resultado.size() + " faturas para " + mesAtual + "/" + anoAtual);
        return resultado;
    }

    public Fatura obterFatura(String id) throws IOException {
        Map<String, Object> db = loadDatabase();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> faturas = (List<Map<String, Object>>) db.get("faturas");
        
        for (Map<String, Object> f : faturas) {
            if (f.get("id").equals(id)) {
                return objectMapper.convertValue(f, Fatura.class);
            }
        }
        
        logger.warning("Fatura não encontrada: " + id);
        throw new IOException("Fatura não encontrada");
    }

    public Fatura atualizarFatura(String id, Fatura fatura) throws IOException {
        Map<String, Object> db = loadDatabase();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> faturas = (List<Map<String, Object>>) db.get("faturas");
        
        for (int i = 0; i < faturas.size(); i++) {
            Map<String, Object> f = faturas.get(i);
            if (f.get("id").equals(id)) {
                fatura.setId(id);
                faturas.set(i, objectMapper.convertValue(fatura, new TypeReference<Map<String, Object>>() {}));
                saveDatabase(db);
                logger.info("Fatura atualizada: " + id);
                return fatura;
            }
        }
        
        logger.warning("Fatura não encontrada para atualização: " + id);
        throw new IOException("Fatura não encontrada");
    }

    public void deletarFatura(String id) throws IOException {
        Map<String, Object> db = loadDatabase();
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> faturas = (List<Map<String, Object>>) db.get("faturas");
        
        boolean removido = faturas.removeIf(f -> f.get("id").equals(id));
        
        if (removido) {
            saveDatabase(db);
            logger.info("Fatura deletada: " + id);
        } else {
            logger.warning("Fatura não encontrada para deleção: " + id);
            throw new IOException("Fatura não encontrada");
        }
    }

    public Map<String, Object> obterResumoMensal(Integer mes, Integer ano) throws IOException {
        List<Fatura> faturas = listarFaturas(mes, ano);
        
        Double totalPago = 0.0;
        Double totalPendente = 0.0;
        Double totalCancelado = 0.0;
        
        for (Fatura f : faturas) {
            if ("pago".equals(f.getStatus())) {
                totalPago += f.getValor();
            } else if ("pendente".equals(f.getStatus())) {
                totalPendente += f.getValor();
            } else if ("cancelado".equals(f.getStatus())) {
                totalCancelado += f.getValor();
            }
        }
        
        Map<String, Object> resumo = new HashMap<>();
        resumo.put("mes", mes != null ? mes : LocalDate.now().getMonthValue());
        resumo.put("ano", ano != null ? ano : LocalDate.now().getYear());
        resumo.put("totalPago", totalPago);
        resumo.put("totalPendente", totalPendente);
        resumo.put("totalCancelado", totalCancelado);
        resumo.put("totalGeral", totalPago + totalPendente + totalCancelado);
        resumo.put("quantidade", faturas.size());
        
        return resumo;
    }
}
