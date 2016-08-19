/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.javasoft.ciclope.amministrazione;

import java.util.ArrayList;
import java.util.Date;
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
            praticaInfos = new ArrayList<CompletePraticaInfo>(aicrecs.size());
            for(Object[] ob : aicrecs){
                c = new CompletePraticaInfo();               
                c.setIdPratica(ob[0].toString());
                c.setArrivo(ob[1].toString());
                c.setData_arrivo(DateUtils.formatDateForAdministration(((Date)ob[2]), Locale.ITALY));
                c.setUscita(ob[3]==null?"":ob[3].toString());
                c.setData_uscita(ob[4]==null?"":DateUtils.formatDateForAdministration(((Date)ob[4]), Locale.ITALY));
                c.setLavori_segnalati(ob[5]==null?"":ob[5].toString());
                c.setNumero_fattura(ob[6]==null?"":ob[6].toString());
                c.setData_fattura(ob[7]==null?"":DateUtils.formatDateForAdministration(((Date)ob[7]), Locale.ITALY));
                c.setPreventivo_lavori(ob[8]==null?"":ob[8].toString().equals("1")?"SI":"NO");
                c.setRevisione_mctc(ob[9]==null?"NO":ob[9].toString().equals("1")?"SI":"NO");
                c.setCollaudo_usl(ob[10]==null?"NO":ob[10].toString().equals("1")?"SI":"NO");
                c.setRegistro_di_controllo(ob[11]==null?"NO":ob[11].toString().equals("1")?"SI":"NO");
                c.setPreventivo_lavori_data(ob[12]==null?"":DateUtils.formatDateForAdministration(((Date)ob[12]),Locale.ITALY));
                c.setRevisione_mctc_data(ob[13]==null?"":DateUtils.formatDateForAdministration(((Date)ob[13]),Locale.ITALY));
                c.setCollaudo_usl_data(ob[14]==null?"":DateUtils.formatDateForAdministration(((Date)ob[14]),Locale.ITALY));
                c.setRegistro_di_controllo_data(ob[15]==null?"":DateUtils.formatDateForAdministration(((Date)ob[15]),Locale.ITALY));
                c.setIdCliente(ob[16]==null?"":ob[16].toString());
                c.setNomeCliente(ob[19]==null?"":ob[19].toString());
                c.setCognomeCliente(ob[20]==null?"":ob[20].toString());
                c.setCellulareCliente(ob[21]==null?"":ob[21].toString());
                c.setLocalitaCliente(ob[22]==null?"":ob[22].toString());
                c.setIdVeicolo(ob[23]==null?"":ob[23].toString());
                c.setMarcaVeicolo(ob[24]==null?"":ob[24].toString());
                c.setModelloVeicolo(ob[25]==null?"":ob[25].toString());
                c.setTargaVeicolo(ob[26]==null?"":ob[26].toString());
                c.setKilometraggioVeicolo(ob[27]==null?"":ob[27].toString());
                c.setAnnoVeicolo(ob[28]==null?"":ob[28].toString());
                c.setTipoVeicolo(ob[29]==null?"":ob[29].toString());
                c.setMatricolaVeicolo(ob[30]==null?"":ob[30].toString());
                c.setOreVeicolo(ob[31]==null?"":ob[31].toString());
                praticaInfos.add(c);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            if (t != null) {
                t.rollback();
            }
        }
        return praticaInfos;
    }
}
