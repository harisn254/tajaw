/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

/**
 *
 * @author Hari
 */
public class Main extends GenericForwardComposer {

    private Menubar menubar;
    private List<Menu> menus = new ArrayList<Menu>();
    private Tabs tabs;
    private Tab tab;
    private Tabpanels panels;
    private Tabpanel panel;
    private Tabbox tabboxLink;
    private Menuitem menuitem;
    private Window winMain;
    private Iframe track_frame;
    private String ipAddress = "";
    private Label userIpAddress;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        try {
            Clients.evalJavaScript("load();");

            if (session.getAttribute("pilihanKategori") == null) {
                session.setAttribute("pilihanKategori", "");
            }

            EventQueues.lookup("myEventQueue", EventQueues.DESKTOP, true).subscribe(new EventListener() {

                @Override
                public void onEvent(Event event) throws Exception {
                    if ("onLoadWindow".equals(event.getName())) {
                        try {
//                            if (Messagebox.show("Selamat datang di situs web tajaw.com, situs web ini berisi kumpulan berita dan informasi dari beberapa situs web, klik Yes untuk melanjutkan.", "Konfirmasi", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
//
//                                @Override
//                                public void onEvent(Event evt) {
//                                }
//                            }) == Messagebox.YES) {
//                                EventQueues.lookup("myEventQueue", EventQueues.DESKTOP, true).publish(new Event("onLoadPage", null, null));
//                            } else {
//                                menubar.setVisible(true);
//                            }

                            EventQueues.lookup("myEventQueue", EventQueues.DESKTOP, true).publish(new Event("onConfigRssFeed", null, userIpAddress));
                            try {
                                Window win = (Window) Executions.createComponents("konfig.zul", winMain, null);
                                win.setClosable(true);
                                win.doModal();
                            } catch (Exception ex) {
                                System.out.println(ex);
                            }
                            menubar.setVisible(true);

                        } catch (Exception ex) {
                            System.out.println(ex);
                        }
                    }
                    if ("onRefreshMenu".equals(event.getName())) {
                        try {
                            if (Messagebox.show("Anda akan menyegarkan berita, klik Yes untuk melanjutkan.", "Konfirmasi", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {

                                @Override
                                public void onEvent(Event evt) {
                                }
                            }) == Messagebox.YES) {
//                                for (Iterator<Menu> it = menus.iterator(); it.hasNext();) {
//                                    Menu menu = it.next();
//                                    menu.detach();
//                                }
//
//                                menus = null;
//                                menus = new ArrayList<Menu>();
                                Clients.showBusy("Mohon menunggu, permintaan anda sedang diproses.");
//                                EventQueues.lookup("myEventQueue", EventQueues.DESKTOP, true).publish(new Event("onLoadPage", null, null));
                                Events.echoEvent("onLoadingMenu", winMain, null);
                            } else {
                            }
                            alert(track_frame.toString());
                            System.out.println(track_frame);
                        } catch (Exception ex) {
                            System.out.println(ex);
                        }
                    }
                    if ("onLoadPage".equals(event.getName())) {
                        loadMenu();
                    }
                    if ("onAccessedLink".equals(event.getName())) {
                        try {

                            Connection cons = getConnection();
                            if (cons != null) {
                                Statement smt3 = cons.createStatement();
                                smt3.executeUpdate("update feed set accessed = accessed+1 where link = '" + event.getData().toString() + "' ");
                                smt3.close();
                            }
                            cons.close();
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                }
            });
        } catch (Exception er) {
            try {
                System.out.println(er);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public void onLoadingMenu$winMain(Event e) {
        EventQueues.lookup("myEventQueue", EventQueues.DESKTOP, true).publish(new Event("onLoadPage", null, null));
    }

    public Main() {
        EventQueues.lookup("myEventQueue", EventQueues.DESKTOP, true).publish(new Event("onLoadWindow", null, null));
    }

    public void onClick$refreshFeed(Event e) {
        renderMenu();
    }

    public void onClick$configFeed(Event e) {
        EventQueues.lookup("myEventQueue", EventQueues.DESKTOP, true).publish(new Event("onConfigRssFeed", null, null));
        try {
            Window win = (Window) Executions.createComponents("konfig.zul", winMain, null);
            win.setClosable(true);
            win.doModal();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public void onTrackIp$winMain(Event event) {
        ipAddress = userIpAddress.getValue();
        try {
            Connection cons = getConnection();
            if (cons != null) {
                Statement smt3 = cons.createStatement();
                smt3.executeUpdate("insert into ip_address_accessed(ip_address, link, tgl) values('" + ipAddress + "','', now()) ");
                smt3.close();
            }
            cons.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void renderMenu() {
        EventQueues.lookup("myEventQueue", EventQueues.DESKTOP, true).publish(new Event("onRefreshMenu", null, null));
    }

    public void loadMenu() {
        try {
            for (Iterator<Menu> it = menus.iterator(); it.hasNext();) {
                Menu menu = it.next();
                menu.detach();
            }

            menus = null;
            menus = new ArrayList<Menu>();

            long t1;
            long t2;
            t1 = System.currentTimeMillis();
            t2 = System.currentTimeMillis();
            menubar.setVisible(false);
            Connection con = getConnection();
            if (con != null) {
                PreparedStatement smt = con.prepareStatement("select ID_CATEGORY, NAME from ref_category order by name");
                ResultSet data = smt.executeQuery();
                while (data.next()) {
                    Integer idCategory = data.getInt("ID_CATEGORY");
                    String name = data.getString("NAME");
                    Menu main = new Menu(name, "img/globe.png");
                    Menupopup popUp = new Menupopup();
                    String link = "";
                    boolean isCatSelected = false;
                    try {
                        List<Integer> cat = (List<Integer>) session.getAttribute("pilihanKategori");
                        for (Iterator<Integer> it = cat.iterator(); it.hasNext();) {
                            Integer integer = it.next();
                            if (integer.equals(idCategory)) {
                                isCatSelected = true;
                            }
                        }
                        if (isCatSelected) {
                            PreparedStatement smt2 = con.prepareStatement("select link from user_category a, feed b where a.id_feed = b.id_feed and a.id_user = 1 and a.id_category = " + idCategory);
                            ResultSet dataFeed = smt2.executeQuery();
                            while (dataFeed.next()) {
                                link = dataFeed.getString("LINK");
                                final String linkUpdate = dataFeed.getString("LINK");
                                if (link.contains("http")) {
                                    try {
                                        URL urls = new URL(link);

                                        // Gets the channel information of the feed and 
                                        // display its title
                                        XmlReader reader = null;
                                        reader = new XmlReader(urls);

                                        SyndFeedInput builder = new SyndFeedInput();
                                        SyndFeed feed = builder.build(reader);

                                        String menuName = feed.getTitle();
                                        if (menuName == null || menuName.equals("")) {
                                            menuName = feed.getDescription();
                                        }

                                        Menu menu = new Menu(menuName, "img/RSS.png");
                                        menu.setPopup(link);
                                        Menupopup popup = new Menupopup();

                                        // Gets and iterate the items of the feed 
                                        for (Iterator i = feed.getEntries().iterator(); i.hasNext();) {
                                            final SyndEntry entry = (SyndEntry) i.next();
                                            menuitem = new Menuitem(entry.getTitle(), "img/doc.png");
                                            final Iframe frame = new Iframe();
                                            menuitem.addEventListener(Events.ON_CLICK, new EventListener() {

                                                @Override
                                                public void onEvent(Event event) throws Exception {
                                                    try {
                                                        tab = new Tab(entry.getTitle());
                                                        tab.setClosable(true);
                                                        panel = new Tabpanel();
                                                        frame.setSrc(entry.getLink().toString());
                                                        frame.setHeight("100%");
                                                        frame.setWidth("100%");
                                                        frame.setParent(panel);
                                                        tab.setParent(tabs);
                                                        tabs.setParent(tabboxLink);
                                                        panel.setParent(panels);
                                                        panels.setParent(tabboxLink);
                                                        EventQueues.lookup("myEventQueue", EventQueues.DESKTOP, true).publish(new Event("onAccessedLink", null, linkUpdate));
                                                    } catch (Exception e) {
                                                        System.out.println(e);
                                                    }
                                                }
                                            });
                                            menuitem.setParent(popup);
                                        }

                                        popup.setParent(menu);
                                        menu.setParent(popUp);
                                        urls = null;
                                        feed = null;
                                        if (reader != null) {
                                            reader.close();
                                        }
                                    } catch (Exception e) {
                                        System.out.println("ERROR on link: " + link + " Category: " + name);
                                    }
                                }
                            }
                            smt2.close();
                            dataFeed.close();
                            smt2 = null;
                            dataFeed = null;
                        }
                    } catch (Exception e) {
                        System.out.println("ERROR on link: " + link + " Category: " + name);
                    }
                    if (isCatSelected) {
                        popUp.setParent(main);
                        main.setParent(menubar);
                        menus.add(main);
                    }
                }
                smt.close();
                data.close();
                con.close();
                smt = null;
                data = null;
                con = null;
            }
            menubar.setVisible(true);
            t2 = System.currentTimeMillis();
            System.out.println("Waktu loading: " + (t2 - t1) + " ms");
        } catch (Exception e) {
            System.out.println(e);
        }
        Clients.clearBusy();
    }

    public Connection getConnection() {
        Connection conn = null;
        try {
            Context initContext = new InitialContext();
            DataSource ds = (DataSource) initContext.lookup("java:comp/env/jdbc/tajawdotcom");
//            DataSource ds = (DataSource) initContext.lookup("java:/comp/env/jdbc/feeder_pool");
//            DataSource ds = (DataSource) initContext.lookup("jdbc/feeder_pool");
            conn = ds.getConnection();
        } catch (Exception e) {
            try {
                Messagebox.show("" + e, "Informasi", Messagebox.OK, Messagebox.INFORMATION);
                System.out.println(e);
            } catch (Exception ex) {
            }
            System.out.println(e);
        }
        return conn;
    }
}
