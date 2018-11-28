/*
 * This file is part of VLCJ.
 *
 * VLCJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VLCJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2015 Caprica Software Limited.
 */
package uk.co.caprica.vlcjplayer.view.analysis.marks;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;
import static uk.co.caprica.vlcjplayer.Application.resources;
import static uk.co.caprica.vlcjplayer.time.Time.formatTime;
import uk.co.caprica.vlcjplayer.view.BasePanel;
import uk.co.caprica.vlcjplayer.view.main.PlaytimeGet;

public class WazaPanel extends BasePanel {

    private final JComboBox<String> fightsComboBox, athletesComboBox, tcpatternComboBox, wazaComboBox, utsuComboBox;

    private final JCheckBox ipponCheckBox;

    private static JTable marksJTable;
    private final ListSelectionModel cellSelectionModel;

    DefaultTableModel timeTableModel;

    public String ev_nameDB, ev_locationDB, fg_namesDB, at_fightDB, at_nameDBtmp;

    public static int id_fightArray[], id_athleteArray[], id_tcpatternArray[], id_wazaArray[], tech_wazaArray[], id_utsuArray[], id_marktimeArray[];
    public static String list_fightArray[], list_athleteArray[], list_tcpatternArray[], list_wazaArray[], list_utsuArray[];
    
    public static int selectedVideo, tmpBox;
    public static int selID, selTime, selAthlete, selTcpattern, selWaza, selUtsu, selIppon,
            at_markltidDB, at_marktcpidDB, at_markwazidDB, at_markutsidDB, at_markippidDB;

    private long TimegetView;

    DefaultComboBoxModel<String> athleteModel, wazaModel, tcpatternModel, utsuModel;

    public WazaPanel() {

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());

        JPanel controlsPane = new JPanel();
        controlsPane.setLayout(new MigLayout("", "[][][][]", "[]5[]5[]"));

        contentPane.add(controlsPane, BorderLayout.NORTH);
        setLayout(new BorderLayout());
        add(contentPane, BorderLayout.CENTER);

        JLabel fightLabel = new JLabel(resources().getString("dialog.analysis.field.fights"));
        JLabel selectedVideoLabel = new JLabel("", JLabel.LEFT);

        JLabel marksLabel = new JLabel(resources().getString("dialog.analysis.field.marks"));

        marksJTable = new JTable(15, 8);
        timeTableModel = (DefaultTableModel) marksJTable.getModel();
        JScrollPane tableJScrollPanel = new JScrollPane(marksJTable);
        tableJScrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableJScrollPanel.setMaximumSize(new Dimension(1000, 267));
        tableJScrollPanel.setPreferredSize(new Dimension(1000, 267));
        marksJTable.setCellSelectionEnabled(true);
        cellSelectionModel = marksJTable.getSelectionModel();
        cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JLabel actionsLabel = new JLabel(resources().getString("dialog.analysis.field.actions"));

        this.fightsDB();

        JButton selFightButton = new JButton(resources().getString("dialog.analysis.field.selfight"));
        JButton markTimeButton = new JButton(resources().getString("dialog.analysis.field.marktime"));
        JButton deleteTimeButton = new JButton(resources().getString("dialog.analysis.field.deletetime"));
        JButton exportcsvButton = new JButton(resources().getString("dialog.analysis.field.exportcsv"));
        JButton markActionButton = new JButton(resources().getString("dialog.analysis.field.saveaction"));

        fightsComboBox = new JComboBox<String>();
        fightLabel.setLabelFor(fightsComboBox);
        DefaultComboBoxModel<String> fightsModel = (DefaultComboBoxModel<String>) fightsComboBox.getModel();
        fightsModel.addElement("Select fight");
        for (String presetName : list_fightArray) {
            fightsModel.addElement(presetName);
        }

        ipponCheckBox = new JCheckBox("Ippon");
        CheckBoxHandler ipponCheckBoxHandler = new CheckBoxHandler();
        ipponCheckBox.addItemListener(ipponCheckBoxHandler);

        athletesComboBox = new JComboBox<String>();
        athleteModel = (DefaultComboBoxModel<String>) athletesComboBox.getModel();

        tcpatternComboBox = new JComboBox<String>();
        tcpatternModel = (DefaultComboBoxModel<String>) tcpatternComboBox.getModel();

        wazaComboBox = new JComboBox<String>();
        wazaModel = (DefaultComboBoxModel<String>) wazaComboBox.getModel();

        utsuComboBox = new JComboBox<String>();
        utsuModel = (DefaultComboBoxModel<String>) utsuComboBox.getModel();

        selFightButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //String data = "";
                String presetName = (String) fightsComboBox.getSelectedItem();
                if (presetName != null) {
                    //data = "Selected: " + presetName;
                    //selectedVideoLabel.setForeground(Color.RED);
                    //selectedVideoLabel.setText(data);
                    selectedVideo = fightsComboBox.getSelectedIndex();
                    //timeList.clear();
                    markDB();
                    athletesDB();
                    techpatternDB();
                    wazaDB();
                    utsuDB();
                }
                //selectedVideoLabel.setText(data);
            }
        });

        markTimeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                insertTimeDB();
                //timeList.clear();
                markDB();
            }
        });

        deleteTimeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteTimeDB();
                selID = selID + 1;
                //timeList.clear();
                markDB();
            }
        });

        exportcsvButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
                String tab = "Waza";
                ExportCSV.main(tab);
            }
        });
        
        markActionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                insertWazaDB();
            }
        });

        /*ipponCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                System.out.println(e.getStateChange() == ItemEvent.SELECTED
                    ? "1" : "0");
            }
        });*/
        controlsPane.add(fightLabel, "cell 0 1, growx");
        controlsPane.add(fightsComboBox, "cell 1 1");
        controlsPane.add(selFightButton, "cell 1 1");
        //controlsPane.add(selectedVideoLabel, "cell 2 1");
        controlsPane.add(exportcsvButton, "cell 2 1");

        controlsPane.add(marksLabel, "cell 0 2, growx");
        //controlsPane.add(marksJTable, "cell 0 2");
        controlsPane.add(tableJScrollPanel, "cell 1 2, growx");
        controlsPane.add(deleteTimeButton, "cell 2 2, grow");
        
        controlsPane.add(markTimeButton, "cell 1 3, growx");

        controlsPane.add(actionsLabel, "cell 0 4, growx");
        controlsPane.add(athletesComboBox, "cell 1 4");
        controlsPane.add(tcpatternComboBox, "cell 1 4");
        controlsPane.add(wazaComboBox, "cell 1 4");
        controlsPane.add(utsuComboBox, "cell 1 4");
        controlsPane.add(ipponCheckBox, "cell 1 4");
        controlsPane.add(markActionButton, "cell 2 4");
    }

    public void fightsDB() {

        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:videok.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();

            ResultSet cnt = stmt.executeQuery("select count(fh.lt_id) as fights_cnt from fights fh");
            int cnt_fightDB = (cnt.getInt("fights_cnt"));
            int i = 0;
            id_fightArray = new int[cnt_fightDB];
            list_fightArray = new String[cnt_fightDB];

            ResultSet fg = stmt.executeQuery("select fh.lt_id, fh.lt_date, at.at_name from fights fh left join athlete at ON (at.at_id = fh.lt_at_id_a) OR (at.at_id = fh.lt_at_id_b) order by fh.lt_id asc");
            String fg_tmp = null;
            while (fg.next()) {
                at_fightDB = fg.getString("lt_id");
                if (at_fightDB.equals(fg_tmp)) {
                    fg_namesDB = at_nameDBtmp + " vs " + fg.getString("at_name");
                    id_fightArray[i] = fg.getInt("lt_id");
                    list_fightArray[i] = fg_namesDB;
                    i++;
                } else {
                    at_nameDBtmp = fg.getString("at_name");
                    fg_tmp = at_fightDB;
                }
            }
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        //item selecionado LISTA
        cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {

                int[] selectedRow = marksJTable.getSelectedRows();
                //int[] selectedColumn = marksJTable.getSelectedColumns();
                for (int i = 0; i < selectedRow.length; i++) {
                selTime = (int) marksJTable.getSelectedRow();
                selID = (int) marksJTable.getValueAt(selectedRow[i], 0);
                
                //System.out.println("Selected: " + selID);
                }
            }
        });
        //loadmarkDB();
    }

    public void markDB() {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:videok.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();

            ResultSet cnt_ath = stmt.executeQuery("select count(at.mark_id) as mark_cnt from marks_waza at where at.mark_fgh_id = " + this.selectedVideo);
            int at_markDB = (cnt_ath.getInt("mark_cnt"));
            id_marktimeArray = new int[at_markDB];
            if (id_marktimeArray != null) {

                ResultSet at = stmt.executeQuery("select mk.mark_id as ID, mk.mark_time as TIME,\n"
                        + "at.at_name as ATHLETE, tp.tcp_name as TECHNICAL, wz.waz_name as WAZA, ut.uts_name as UTSU, mk.mark_ipp_id as IPPON,\n"
                        + " mk.mark_time as MARCA from marks_waza mk\n"
                        + "left join athlete at on (mk.mark_lt_id = at.at_id)\n"
                        + "left join technical_patterns tp on (mk.mark_tcp_id = tp.tcp_id)\n"
                        + "left join waza wz on (mk.mark_waz_id = wz.waz_id)\n"
                        + "left join utsu ut on (mk.mark_uts_id = ut.uts_id)\n"
                        + "where mk.mark_fgh_id = " + this.selectedVideo + " order by mk.mark_time asc");
                
                ResultSetMetaData metaData = at.getMetaData();
                // Names of columns
                Vector<String> columnNames = new Vector<String>();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    columnNames.add(metaData.getColumnName(i));
                }

                // Data of the table
                Vector<Vector<Object>> data = new Vector<Vector<Object>>();
                while (at.next()) {
                    Vector<Object> vector = new Vector<Object>();
                    for (int i = 1; i <= columnCount; i++) {
                        if (i == 2) {
                            long at_marks = ((Number) at.getObject(i)).longValue();
                            long at_data = at_marks;
                            vector.add(formatTime(at_data));
                        } else {
                            vector.add(at.getObject(i));
                        }
                    }
                    data.add(vector);

                }
                timeTableModel.setDataVector(data, columnNames);

                /*
            if (at_markltidDB != 0) {
                ResultSet athNomeDB = stmtl.executeQuery("select at.at_name from athlete at where at.at_id = " + at_markltidDB);
                String at_name = (athNomeDB.getString("at_name"));
                athleteModel.setSelectedItem(at_name);
                ResultSet tcpDB = stmtl.executeQuery("select at.tcp_name from technical_patterns at where at.tcp_id = " + at_marktcpidDB);
                String tcp_name = (tcpDB.getString("tcp_name"));
                tcpatternModel.setSelectedItem(tcp_name);
                ResultSet wazDB = stmtl.executeQuery("select at.waz_name from waza at where at.waz_id = " + at_markwazidDB);
                String waz_name = (wazDB.getString("waz_name"));
                wazaModel.setSelectedItem(waz_name);
                if (at_markutsidDB != 0) {
                    ResultSet utsDB = stmtl.executeQuery("select at.uts_name from utsu at where at.uts_id = " + at_markutsidDB);
                    String uts_name = (utsDB.getString("uts_name"));
                    utsuModel.setSelectedItem(uts_name);
                }
                if (at_markippidDB == 1) {
                    ipponCheckBox.setSelected(true);
                }*/
            }
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    public void athletesDB() {

        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:videok.db");
            c.setAutoCommit(false);

            stmt = c.createStatement();

            ResultSet cnt_ath = stmt.executeQuery("select count(at.at_id) as athlete_cnt from fights fh left join athlete at ON (at.at_id = fh.lt_at_id_a) OR (at.at_id = fh.lt_at_id_b) where fh.lt_id = " + WazaPanel.selectedVideo + " order by fh.lt_id asc");
            int cnt_athleteDB = (cnt_ath.getInt("athlete_cnt") + 1);
            int i = 0;
            id_athleteArray = new int[cnt_athleteDB];
            list_athleteArray = new String[cnt_athleteDB];
            if (athleteModel != null) {
                athleteModel.removeAllElements();

                ResultSet at = stmt.executeQuery("select at.at_id, at.at_name from fights fh left join athlete at ON (at.at_id = fh.lt_at_id_a) OR (at.at_id = fh.lt_at_id_b) where fh.lt_id = " + WazaPanel.selectedVideo + " order by fh.lt_id asc");
                while (at.next()) {
                    id_athleteArray[i] = at.getInt("at_id");
                    list_athleteArray[i] = at.getString("at_name");
                    athleteModel.addElement(list_athleteArray[i]);
                    i++;
                }
            }
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        loadSelectionItems();
    }

    public void techpatternDB() {

        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:videok.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();

            ResultSet cnt_tcp = stmt.executeQuery("select count(at.tcp_id) as tcp_cnt from technical_patterns at order by at.tcp_id asc");
            int cnt_tcpDB = (cnt_tcp.getInt("tcp_cnt"));
            id_tcpatternArray = new int[cnt_tcpDB];
            list_tcpatternArray = new String[cnt_tcpDB];
            int i = 0;
            if (tcpatternModel != null) {
                tcpatternModel.removeAllElements();

                ResultSet at = stmt.executeQuery("select at.tcp_id, at.tcp_name from technical_patterns at order by at.tcp_id asc");
                while (at.next()) {
                    id_tcpatternArray[i] = at.getInt("tcp_id");
                    list_tcpatternArray[i] = at.getString("tcp_name");
                    tcpatternModel.addElement(list_tcpatternArray[i]);
                    i++;
                }
            }
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        loadSelectionItems();
    }

    public void wazaDB() {

        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:videok.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();

            int id_tcpattern = (tcpatternComboBox.getSelectedIndex()) + 1;
    System.out.println(id_tcpattern);
            ResultSet cnt_waza = stmt.executeQuery("select count(at.waz_id) as waza_cnt from waza at where waz_techp_id = " + id_tcpattern + " order by at.waz_id asc");
            //ResultSet cnt_waza = stmt.executeQuery("select count(at.waz_id) as waza_cnt from waza at order by at.waz_id asc");
            int cnt_wazaDB = (cnt_waza.getInt("waza_cnt") + 1);
            int i = 0;
            id_wazaArray = new int[cnt_wazaDB];
            tech_wazaArray = new int[cnt_wazaDB];
            list_wazaArray = new String[cnt_wazaDB];
            tmpBox = tcpatternComboBox.getItemCount();
            if (tmpBox > 0) {
                wazaModel.removeAllElements();
                
                ResultSet at = stmt.executeQuery("select * from waza at where waz_techp_id = " + id_tcpattern + " order by at.waz_id asc");
                //ResultSet at = stmt.executeQuery("select * from waza at order by at.waz_id asc");
                while (at.next()) {
                    id_wazaArray[i] = at.getInt("waz_id");
                    tech_wazaArray[i] = at.getInt("waz_techp_id");
                    list_wazaArray[i] = at.getString("waz_name");
                    wazaModel.addElement(list_wazaArray[i]);
                    i++;
                }
                stmt.close();
            }
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        //loadSelectionItems();
    }

    public void utsuDB() {

        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:videok.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();

            int id_tcpattern = (utsuComboBox.getSelectedIndex()) + 1;
            ResultSet cnt_utsu = stmt.executeQuery("select count(at.uts_id) as utsu_cnt from utsu at order by at.uts_id asc");
            int cnt_utsuDB = (cnt_utsu.getInt("utsu_cnt") + 1);
            int i = 0;
            id_utsuArray = new int[cnt_utsuDB];
            list_utsuArray = new String[cnt_utsuDB];
            if (utsuModel != null) {
                utsuModel.removeAllElements();

                if (selTcpattern != 3) {

                    ResultSet at = stmt.executeQuery("select * from utsu at order by at.uts_id asc");
                    while (at.next()) {
                        id_utsuArray[i] = at.getInt("uts_id");
                        list_utsuArray[i] = at.getString("uts_name");
                        utsuModel.addElement(list_utsuArray[i]);
                        i++;
                    }
                }
            }
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        loadSelectionItems();
    }

    private class CheckBoxHandler implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent evento) {
            if (ipponCheckBox.isSelected()) {
                selIppon = 1;
            } else {
                selIppon = 0;
            }
        }
    }

    public void insertTimeDB() {
        Connection c = null;
        Statement stmt = null;
        TimegetView = PlaytimeGet.timeGet;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:videok.db");
            stmt = c.createStatement();
            stmt.executeUpdate("insert into marks_waza (mark_time, mark_fgh_id) values("
                    + TimegetView + ", " + WazaPanel.selectedVideo + ")");

            /*ResultSet at = stmt.executeQuery("select at.mark_id from marks_waza at where at.mark_time=" + WazaPanel.selectedVideo + " order by at.mark_time");
            while (at.next()) {
                long at_marks = at.getInt("mark_id");
                long at_data = at_marks;
                timeList.addElement(formatTime(at_data));
            }*/
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    public void deleteTimeDB() {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:videok.db");
            stmt = c.createStatement();
            stmt.executeUpdate("delete from marks_waza where mark_id = " + selID);

            stmt.close();
            c.close();
            //System.out.println(selID);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    public void insertWazaDB() {

        Connection c = null;
        Statement stmt = null;

        if (selIppon != 1) {
            selIppon = 0;
        }

        //TimegetView = PlaytimeGet.timeGet;
        long selItem = selID;
        //System.out.println(selID);
        //System.out.println(list_idTimeArray[selID]);
        
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:videok.db");
            stmt = c.createStatement();
            stmt.executeUpdate("update marks_waza set mark_lt_id=" + selAthlete + ", mark_tcp_id="
                    + selTcpattern + ", mark_waz_id=" + selWaza + ", mark_uts_id="
                    + selUtsu + ", mark_ipp_id =" + selIppon + " where mark_id=" + selItem);
            stmt.close();
            c.close();
            
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        markDB();
    }

    public void loadmarkDB() {

        Connection c = null;
        Statement stmtl = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:videok.db");
            c.setAutoCommit(false);
            stmtl = c.createStatement();

            ResultSet wazaDB = stmtl.executeQuery("select * from marks_waza at where at.mark_id = " + selID);
            while (wazaDB.next()) {
                at_markltidDB = (wazaDB.getInt("mark_lt_id"));
                at_marktcpidDB = (wazaDB.getInt("mark_tcp_id"));
                at_markwazidDB = (wazaDB.getInt("mark_waz_id"));
                at_markutsidDB = (wazaDB.getInt("mark_uts_id"));
                at_markippidDB = (wazaDB.getInt("mark_ipp_id"));
            }
            if (at_markltidDB != 0) {
                ResultSet athNomeDB = stmtl.executeQuery("select at.at_name from athlete at where at.at_id = " + at_markltidDB);
                String at_name = (athNomeDB.getString("at_name"));
                athleteModel.setSelectedItem(at_name);
                ResultSet tcpDB = stmtl.executeQuery("select at.tcp_name from technical_patterns at where at.tcp_id = " + at_marktcpidDB);
                String tcp_name = (tcpDB.getString("tcp_name"));
                tcpatternModel.setSelectedItem(tcp_name);
                ResultSet wazDB = stmtl.executeQuery("select at.waz_name from waza at where at.waz_id = " + at_markwazidDB);
                String waz_name = (wazDB.getString("waz_name"));
                wazaModel.setSelectedItem(waz_name);
                if (at_markutsidDB != 0) {
                    ResultSet utsDB = stmtl.executeQuery("select at.uts_name from utsu at where at.uts_id = " + at_markutsidDB);
                    String uts_name = (utsDB.getString("uts_name"));
                    utsuModel.setSelectedItem(uts_name);
                }
                if (at_markippidDB == 1) {
                    ipponCheckBox.setSelected(true);
                }
            }
            stmtl.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }
    
    public void loadSelectionItems() {
        
        if (athletesComboBox != null) {
            athletesComboBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    if (athletesComboBox.getSelectedIndex() != -1) {
                        int selAthletetmp = athletesComboBox.getSelectedIndex();
                        selAthlete = id_athleteArray[selAthletetmp];
                        //techpatternDB();
                    }
                }
            });
        }
        
        if (tcpatternComboBox != null) {
            tcpatternComboBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    if (tcpatternComboBox.getSelectedIndex() != -1) {
                        int selTcpatterntmp = tcpatternComboBox.getSelectedIndex();
                        selTcpattern = id_tcpatternArray[selTcpatterntmp];
                        wazaDB();
                    }
                }
            });
        }

        if (wazaComboBox != null) {
            wazaComboBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    if (wazaComboBox.getSelectedIndex() != -1) {
                        int selWazatmp = wazaComboBox.getSelectedIndex();
                        selWaza = id_wazaArray[selWazatmp];
                        //utsuDB();
                    }
                }
            });
        }
        
        if (utsuComboBox != null) {
            utsuComboBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    if (utsuComboBox.getSelectedIndex() != -1) {
                        int selUtsutmp = utsuComboBox.getSelectedIndex();
                        selUtsu = id_utsuArray[selUtsutmp];
                    }
                }
            });
        }
    }
}
