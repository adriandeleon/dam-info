package com.grokthecode.models.restapi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "idmonitoreodiario",
        "fechamonitoreo",
        "clavesih",
        "nombreoficial",
        "nombrecomun",
        "estado",
        "nommunicipio",
        "regioncna",
        "latitud",
        "longitud",
        "uso",
        "corriente",
        "tipovertedor",
        "inicioop",
        "elevcorona",
        "bordolibre",
        "nameelev",
        "namealmac",
        "namoelev",
        "namoalmac",
        "alturacortina",
        "elevacionactual",
        "almacenaactual",
        "llenano"
})
@Getter
@Setter
public class PresasDto implements Serializable {

    @JsonProperty("idmonitoreodiario")
    public Integer idmonitoreodiario;
    @JsonProperty("fechamonitoreo")
    public String fechamonitoreo;
    @JsonProperty("clavesih")
    public String clavesih;
    @JsonProperty("nombreoficial")
    public String nombreoficial;
    @JsonProperty("nombrecomun")
    public String nombrecomun;
    @JsonProperty("estado")
    public String estado;
    @JsonProperty("nommunicipio")
    public String nommunicipio;
    @JsonProperty("regioncna")
    public String regioncna;
    @JsonProperty("latitud")
    public Double latitud;
    @JsonProperty("longitud")
    public Double longitud;
    @JsonProperty("uso")
    public String uso;
    @JsonProperty("corriente")
    public String corriente;
    @JsonProperty("tipovertedor")
    public String tipovertedor;
    @JsonProperty("inicioop")
    public String inicioop;
    @JsonProperty("elevcorona")
    public String elevcorona;
    @JsonProperty("bordolibre")
    public Double bordolibre;
    @JsonProperty("nameelev")
    public Double nameelev;
    @JsonProperty("namealmac")
    public Double namealmac;
    @JsonProperty("namoelev")
    public Double namoelev;
    @JsonProperty("namoalmac")
    public Double namoalmac;
    @JsonProperty("alturacortina")
    public String alturacortina;
    @JsonProperty("elevacionactual")
    public Double elevacionactual;
    @JsonProperty("almacenaactual")
    public Double almacenaactual;
    @JsonProperty("llenano")
    public Double llenano;
}
