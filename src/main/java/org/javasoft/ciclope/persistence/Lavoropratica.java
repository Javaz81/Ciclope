package org.javasoft.ciclope.persistence;
// Generated 10-nov-2016 22.50.10 by Hibernate Tools 4.3.1



/**
 * Lavoropratica generated by hbm2java
 */
public class Lavoropratica  implements java.io.Serializable {


     private LavoropraticaId id;
     private String descrizioneAltro;

    public Lavoropratica() {
    }

	
    public Lavoropratica(LavoropraticaId id) {
        this.id = id;
    }
    public Lavoropratica(LavoropraticaId id, String descrizioneAltro) {
       this.id = id;
       this.descrizioneAltro = descrizioneAltro;
    }
   
    public LavoropraticaId getId() {
        return this.id;
    }
    
    public void setId(LavoropraticaId id) {
        this.id = id;
    }
    public String getDescrizioneAltro() {
        return this.descrizioneAltro;
    }
    
    public void setDescrizioneAltro(String descrizioneAltro) {
        this.descrizioneAltro = descrizioneAltro;
    }




}


