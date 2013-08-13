/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import data.RefCategory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;

/**
 * @author Hari
 */
public class Config extends GenericForwardComposer {

    private Listbox listFeeds;
    private List<Integer> menuKonfig;
    private List<RefCategory> kategori;
    private Button btnSimpan;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        EventQueues.lookup("myEventQueue", EventQueues.DESKTOP, true).subscribe(new EventListener() {

            @Override
            public void onEvent(Event event) throws Exception {
                if ("onConfigRssFeed".equals(event.getName())) {
                    try {
                        Connection con = getConnection();
                        if (con != null) {
                            kategori = new ArrayList<RefCategory>();
                            PreparedStatement smt = con.prepareStatement("select ID_CATEGORY, NAME from ref_category order by name");
                            ResultSet data = smt.executeQuery();
                            while (data.next()) {
                                kategori.add(new RefCategory(data.getInt("ID_CATEGORY"), data.getString("NAME")));
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }

                    listFeeds.setModel(new ListModelList(kategori));
                }
            }
        });

        listFeeds.setItemRenderer(new ListitemRenderer<Object>() {

            @Override
            public void render(final Listitem lstm, Object o, int index) throws Exception {
                boolean isValid = false;
                if (session.getAttribute("pilihanKategori") != null) {
                    try {
                        menuKonfig = (List<Integer>) session.getAttribute("pilihanKategori");
                        isValid = true;
                    } catch (Exception e) {
                    }
                }
                RefCategory category = (RefCategory) o;
                new Listcell(category.getName(), "img/globe.png").setParent(lstm);
                lstm.setValue(category.getIdCategory());
                if (isValid) {
                    for (Iterator<Integer> it = menuKonfig.iterator(); it.hasNext(); ) {
                        Integer integer = it.next();
                        if (integer.equals(category.getIdCategory())) {
                            lstm.setSelected(true);
                        }
                    }
                }
            }
        });
    }

    public void onClick$btnSimpan(Event ev) {
        try {
            btnSimpan.setVisible(false);
            menuKonfig = null;
            menuKonfig = new ArrayList<Integer>();
            Set<Listitem> items = listFeeds.getSelectedItems();
            for (Iterator<Listitem> it = items.iterator(); it.hasNext(); ) {
                Listitem listitem = it.next();
                menuKonfig.add((Integer) listitem.getValue());
            }
            session.setAttribute("pilihanKategori", menuKonfig);
        } catch (Exception e) {
            System.out.println(e);
        }
        if (menuKonfig.size() > 0) {
            if (self.getParent() != null) {
                Clients.showBusy("Mohon menunggu, permintaan anda sedang diproses.");
                Events.echoEvent("onLoadingMenu", self.getParent(), null);
                self.detach();
            } else {
                System.out.println("Get Parent is NULL!!!");
            }
        } else {
            try {
                Messagebox.show("Mohon memilih kategori berita yang ingin ditampilkan untuk melanjutkan.", "Informasi", Messagebox.OK, Messagebox.INFORMATION);
            } catch (Exception e) {
            }
            btnSimpan.setVisible(true);
        }
    }

    public Connection getConnection() {
        Connection conn = null;
        try {
            Context initContext = new InitialContext();
            DataSource ds = (DataSource) initContext.lookup("java:comp/env/jdbc/tajawdotcom");
//            DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/feeder_pool");
            conn = ds.getConnection();
        } catch (Exception e) {
            try {
                Messagebox.show(e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
                System.out.println(e);
            } catch (Exception ex) {
            }
            System.out.println(e);
        }
        return conn;
    }
}
