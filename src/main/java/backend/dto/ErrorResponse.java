package backend.dto;

import java.io.Serializable;

public class ErrorResponse implements Serializable {
    private String mensagem;
    private String erro;
    private Integer status;

    public ErrorResponse(String mensagem, String erro, Integer status) {
        this.mensagem = mensagem;
        this.erro = erro;
        this.status = status;
    }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public String getErro() { return erro; }
    public void setErro(String erro) { this.erro = erro; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
