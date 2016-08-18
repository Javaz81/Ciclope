/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.javasoft.ciclope.amministrazione;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.javasoft.ciclope.persistence.HibernateUtil;
import org.javasoft.ciclope.servlets.utils.DateUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author andrea
 */
public class AmministrazioneUtils {

    /**
     * Crea la lista di tutte le informazioni della pratica, clienti, veicoli e
     * lavori annessi compresi.
     *
     * @return Tutte le informazioni sulla pratica.
     */
    public static List<CompletePraticaInfo> GetAllPraticaInformation(int pratica_id) {
        CompletePraticaInfo c = null;
        Session s = null;
        Transaction t = null;
        ArrayList<CompletePraticaInfo> praticaInfos = null;
        try {
            s = HibernateUtil.getSessionFactory().openSession();
            t = s.getTransaction();
            t.begin();
            String json = null;
            JSONObject obj = null;
            Query q = null;

            q = s.createSQLQuery("select *\n"
                    + "from ciclope.pratica\n"
                    + "inner join ciclope.cliente on Cliente_idCliente = idCliente\n"
                    + "inner join ciclope.veicolo on Veicolo = idVeicolo\n"
                    + "where ciclope.pratica.idPratica = " + pratica_id
            );

            List<Object[]> aicrecs = q.list();
            t.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            if (t != null) {
                t.rollback();
            }
        }
        return praticaInfos;
    }
}
