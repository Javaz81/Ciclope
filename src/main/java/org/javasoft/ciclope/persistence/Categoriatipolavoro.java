package org.javasoft.ciclope.persistence;
// Generated 10-nov-2016 22.50.10 by Hibernate Tools 4.3.1


import java.util.HashSet;
import java.util.Set;

/**
 * Categoriatipolavoro generated by hbm2java
 */
public class Categoriatipolavoro  implements java.io.Serializable {


     private int idCategoriaTipoLavoro;
     private String nome;
     private Set tipolavoros = new HashSet(0);
     private Set lavoripratichecustoms = new HashSet(0);

    public Categoriatipolavoro() {
    }

	
    public Categoriatipolavoro(int idCategoriaTipoLavoro, String nome) {
        this.idCategoriaTipoLavoro = idCategoriaTipoLavoro;
        this.nome = nome;
    }
    public Categoriatipolavoro(int idCategoriaTipoLavoro, String nome, Set tipolavoros, Set lavoripratichecustoms) {
       this.idCategoriaTipoLavoro = idCategoriaTipoLavoro;
       this.nome = nome;
       this.tipolavoros = tipolavoros;
       this.lavoripratichecustoms = lavoripratichecustoms;
    }
   
    public int getIdCategoriaTipoLavoro() {
        return this.idCategoriaTipoLavoro;
    }
    
    public void setIdCategoriaTipoLavoro(int idCategoriaTipoLavoro) {
        this.idCategoriaTipoLavoro = idCategoriaTipoLavoro;
    }
    public String getNome() {
        return this.nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    public Set getTipolavoros() {
        return this.tipolavoros;
    }
    
    public void setTipolavoros(Set tipolavoros) {
        this.tipolavoros = tipolavoros;
    }
    public Set getLavoripratichecustoms() {
        return this.lavoripratichecustoms;
    }
    
    public void setLavoripratichecustoms(Set lavoripratichecustoms) {
        this.lavoripratichecustoms = lavoripratichecustoms;
    }




}


