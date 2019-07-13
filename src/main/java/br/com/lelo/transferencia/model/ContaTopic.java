package br.com.lelo.transferencia.model;

public enum ContaTopic {
    COM_MOVIMENTAR, EVT_CONTA_MOV, EVT_CONTA_MOV_ERRO;

    public String getTopic() {
        return this.name().toLowerCase().replaceAll("_", "-");
    }

}
