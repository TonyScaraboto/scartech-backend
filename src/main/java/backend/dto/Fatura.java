package backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class Fatura implements Serializable {
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("tipo")
    private String tipo; // "ordem" ou "venda"
    
    @JsonProperty("descricao")
    private String descricao;
    
    @JsonProperty("valor")
    private Double valor;
    
    @JsonProperty("status")
    private String status; // "pendente", "pago", "cancelado"
    
    @JsonProperty("mes")
    private Integer mes;
    
    @JsonProperty("ano")
    private Integer ano;
    
    @JsonProperty("dataEmissao")
    private String dataEmissao;

    public Fatura() {}

    public Fatura(String id, String tipo, String descricao, Double valor, String status, Integer mes, Integer ano, String dataEmissao) {
        this.id = id;
        this.tipo = tipo;
        this.descricao = descricao;
        this.valor = valor;
        this.status = status;
        this.mes = mes;
        this.ano = ano;
        this.dataEmissao = dataEmissao;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getMes() { return mes; }
    public void setMes(Integer mes) { this.mes = mes; }

    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }

    public String getDataEmissao() { return dataEmissao; }
    public void setDataEmissao(String dataEmissao) { this.dataEmissao = dataEmissao; }
}
