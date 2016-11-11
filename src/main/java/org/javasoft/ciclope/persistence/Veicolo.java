package org.javasoft.ciclope.persistence;
// Generated 10-nov-2016 22.50.10 by Hibernate Tools 4.3.1


import java.util.HashSet;
import java.util.Set;

/**
 * Veicolo generated by hbm2java
 */
public class Veicolo  implements java.io.Serializable {


     private Integer idVeicolo;
     private String marca;
     private String modello;
     private String targa;
     private Integer anno;
     private String tipo;
     private String matricola;
     private Set praticas = new HashSet(0);

    public Veicolo() {
    }

	
    public Veicolo(String matricola) {
        this.matricola = matricola;
    }
    public Veicolo(String marca, String modello, String targa, Integer anno, String tipo, String matricola, Set praticas) {
       this.marca = marca;
       this.modello = modello;
       this.targa = targa;
       this.anno = anno;
       this.tipo = tipo;
       this.matricola = matricola;
       this.praticas = praticas;
    }
   
    public Integer getIdVeicolo() {
        return this.idVeicolo;
    }
    
    public void setIdVeicolo(Integer idVeicolo) {
        this.idVeicolo = idVeicolo;
    }
    public String getMarca() {
        return this.marca;
    }
    
    public void setMarca(String marca) {
        this.marca = marca;
    }
    public String getModello() {
        return this.modello;
    }
    
    public void setModello(String modello) {
        this.modello = modello;
    }
    public String getTarga() {
        return this.targa;
    }
    
    public void setTarga(String targa) {
        this.targa = targa;
    }
    public Integer getAnno() {
        return this.anno;
    }
    
    public void setAnno(Integer anno) {
        this.anno = anno;
    }
    public String getTipo() {
        return this.tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public String getMatricola() {
        return this.matricola;
    }
    
    public void setMatricola(String matricola) {
        this.matricola = matricola;
    }
    public Set getPraticas() {
        return this.praticas;
    }
    
    public void setPraticas(Set praticas) {
        this.praticas = praticas;
    }




}


