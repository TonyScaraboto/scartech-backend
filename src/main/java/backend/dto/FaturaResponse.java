package backend.dto;

import java.io.Serializable;
import java.util.List;

public class FaturaResponse implements Serializable {
    private List<Fatura> faturas;
    private Double totalPago;
    private Double totalPendente;
    private Double totalGeral;
    private Integer mes;
    private Integer ano;

    public FaturaResponse(List<Fatura> faturas, Double totalPago, Double totalPendente, Double totalGeral, Integer mes, Integer ano) {
        this.faturas = faturas;
        this.totalPago = totalPago;
        this.totalPendente = totalPendente;
        this.totalGeral = totalGeral;
        this.mes = mes;
        this.ano = ano;
    }

    public List<Fatura> getFaturas() { return faturas; }
    public void setFaturas(List<Fatura> faturas) { this.faturas = faturas; }

    public Double getTotalPago() { return totalPago; }
    public void setTotalPago(Double totalPago) { this.totalPago = totalPago; }

    public Double getTotalPendente() { return totalPendente; }
    public void setTotalPendente(Double totalPendente) { this.totalPendente = totalPendente; }

    public Double getTotalGeral() { return totalGeral; }
    public void setTotalGeral(Double totalGeral) { this.totalGeral = totalGeral; }

    public Integer getMes() { return mes; }
    public void setMes(Integer mes) { this.mes = mes; }

    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }
}
