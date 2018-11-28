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
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;
import static uk.co.caprica.vlcjplayer.Application.resources;
import static uk.co.caprica.vlcjplayer.time.Time.formatTime;
import uk.co.caprica.vlcjplayer.view.BasePanel;
import uk.co.caprica.vlcjplayer.view.main.PlaytimeGet;

public class DistPanel extends BasePanel {
    
    private final JComboBox<String> fightsComboBox, athletesComboBox, distComboBox;
    
    DefaultListModel timeList;
    JList markList;

    public String ev_nameDB, ev_locationDB, fg_namesDB, at_fightDB, at_nameDBtmp;
    
    public static int id_fightArray[], id_athleteArray[], id_distArray[], tech_distArray[], id_marktimeArray[];
    public static String list_fightArray[], list_athleteArray[], list_distArray[];
    
    public static int selectedVideo, tmpBox;
    public static int selTime, selIndex, selAthlete, selDist, at_markltidDB, at_markdistidDB;

    private long TimegetView;
    
    DefaultComboBoxModel<String> athleteModel, distModel;
    
    public DistPanel() {
        
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());

        JPanel controlsPane = new JPanel();
        controlsPane.setLayout(new MigLayout("", "[][][][]", "[]5[]5[]5[]"));

        contentPane.add(controlsPane, BorderLayout.NORTH);
        setLayout(new BorderLayout());
        add(contentPane, BorderLayout.CENTER);
        
        JLabel fightLabel = new JLabel(resources().getString("dialog.analysis.field.fights"));
        JLabel selectedVideoLabel = new JLabel("",JLabel.LEFT);
        
        timeList = new DefaultListModel();
        markList = new JList(timeList);
        JLabel marksLabel = new JLabel(resources().getString("dialog.analysis.field.marks"));
        markList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        markList.setSelectedIndex(0);
        markList.setVisibleRowCount(15);
        markList.setFixedCellWidth(100);
        JScrollPane marksListScrollPane = new JScrollPane(markList);
        
        JLabel actionsLabel = new JLabel(resources().getString("dialog.analysis.field.actions"));
        
        this.fightsDB();

        JButton selFightButton = new JButton(resources().getString("dialog.analysis.field.selfight"));
        JButton markTimeButton = new JButton(resources().getString("dialog.analysis.field.marktime"));
        JButton deleteTimeButton = new JButton(resources().getString("dialog.analysis.field.deletetime"));
        JButton markActionButton = new JButton(resources().getString("dialog.analysis.field.saveaction"));
        JButton importTimeButton = new JButton(resources().getString("dialog.analysis.field.importtime"));
        
        fightsComboBox = new JComboBox<String>();
        fightLabel.setLabelFor(fightsComboBox);
        DefaultComboBoxModel<String> fightsModel = (DefaultComboBoxModel<String>) fightsComboBox.getModel();
        fightsModel.addElement("Select fight");
        for(String presetName : list_fightArray) {
            fightsModel.addElement(presetName);
        }
        
        athletesComboBox = new JComboBox<String>();
        athleteModel = (DefaultComboBoxModel<String>) athletesComboBox.getModel();
        
        distComboBox = new JComboBox<String>();
        distModel = (DefaultComboBoxModel<String>) distComboBox.getModel();
        
        selFightButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            String data = "";
            String presetName = (String) fightsComboBox.getSelectedItem();
            if (presetName != null) {                     
               data = "Selected: " + presetName;
               selectedVideoLabel.setForeground(Color.RED);
               selectedVideoLabel.setText(data);
               selectedVideo = fightsComboBox.getSelectedIndex();
               timeList.clear();
               markDB();
               athletesDB();
            }
            selectedVideoLabel.setText(data);
         }
        });
        
        markTimeButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
                insertTimeDB();
                timeList.clear();
                markDB();
            }
        });
        
        deleteTimeButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
                deleteTimeDB();
                selTime = selTime + 1;
                timeList.clear();
                markDB();
            }
        });
        
        importTimeButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
                importmarkDB();
                timeList.clear();
                markDB();
            }
        });
        
        markActionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                insertDistDB();
            }
        });
        
        controlsPane.add(fightLabel, "cell 0 1, growx");
        controlsPane.add(fightsComboBox, "cell 1 1");
        controlsPane.add(selFightButton, "cell 1 1");
        controlsPane.add(selectedVideoLabel, "cell 2 1");
        
        controlsPane.add(marksLabel, "cell 0 3, growx");
        controlsPane.add(marksListScrollPane, "cell 1 3");
        controlsPane.add(markTimeButton, "cell 1 3, growx");
        controlsPane.add(deleteTimeButton, "cell 2 3");
        controlsPane.add(importTimeButton, "cell 3 3");
        
        controlsPane.add(actionsLabel, "cell 0 4, growx");
        controlsPane.add(athletesComboBox, "cell 1 4");
        controlsPane.add(distComboBox, "cell 1 4");
        controlsPane.add(markActionButton, "cell 1 4");
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
           while ( fg.next() ) {
                at_fightDB = fg.getString("lt_id");
                if (at_fightDB.equals(fg_tmp))
                {
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
           System.err.println( e.getClass().getName() + ": " + e.getMessage() );
           System.exit(0);
        }
        
        ListSelectionListener listSelectionListener = new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {
                JList list = (JList) listSelectionEvent.getSource();
                int selections[] = list.getSelectedIndices();
                Object selectionValues[] = list.getSelectedValues();
                for (int i = 0, n = selections.length; i < n; i++) {
                    int selpositionTime = selections[i];
                    selTime = id_marktimeArray[selpositionTime];
                    selIndex = list.getSelectedIndex();
                }
                loadmarkDB();
            }
        };
        markList.addListSelectionListener(listSelectionListener);
    }

    public void markDB() {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:videok.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
            
            ResultSet cnt_ath = stmt.executeQuery("select count(at.mark_id) as mark_cnt from marks_distance at where at.mark_fgh_id = " + DistPanel.selectedVideo);
            int at_markDB = (cnt_ath.getInt("mark_cnt"));
            id_marktimeArray = new int[at_markDB];
            if (id_marktimeArray != null) {
            
                ResultSet at = stmt.executeQuery("select at.mark_time, at.mark_id from marks_distance at where at.mark_fgh_id = " + DistPanel.selectedVideo + " order by at.mark_time asc");
                int i=0;
                while (at.next()) {
                    id_marktimeArray[i] = at.getInt("mark_id");
                    long at_marks = at.getInt("mark_time");
                    long at_data = at_marks;
                    timeList.addElement(formatTime(at_data));
                    i++;
                }
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
           
            ResultSet cnt_ath = stmt.executeQuery("select count(at.at_id) as athlete_cnt from fights fh left join athlete at ON (at.at_id = fh.lt_at_id_a) OR (at.at_id = fh.lt_at_id_b) where fh.lt_id = " + DistPanel.selectedVideo + " order by fh.lt_id asc");
            int cnt_athleteDB = (cnt_ath.getInt("athlete_cnt") + 1);
            int i = 0;
            id_athleteArray = new int[cnt_athleteDB];
            list_athleteArray = new String[cnt_athleteDB];
            if (athleteModel != null) {
                athleteModel.removeAllElements();
            
                ResultSet at = stmt.executeQuery("select at.at_id, at.at_name from fights fh left join athlete at ON (at.at_id = fh.lt_at_id_a) OR (at.at_id = fh.lt_at_id_b) where fh.lt_id = " + DistPanel.selectedVideo + " order by fh.lt_id asc");
                while ( at.next() ) {
                    id_athleteArray[i] = at.getInt("at_id");
                    list_athleteArray[i] = at.getString("at_name");
                    athleteModel.addElement(list_athleteArray[i]);
                    i++; 
                }
            }
        
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        
        if (athletesComboBox != null) {
            athletesComboBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    if(athletesComboBox.getSelectedIndex()!=-1){
                        int selAthletetmp = athletesComboBox.getSelectedIndex();
                        selAthlete = id_athleteArray[selAthletetmp];
                        DistDB();
                    }
                }
            });
        }
    }
    
    public void DistDB() {
        
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:videok.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();
           
            ResultSet cnt_dist = stmt.executeQuery("select count(at.dist_id) as dist_cnt from distance at order by at.dist_id asc");
            int cnt_distDB = (cnt_dist.getInt("dist_cnt"));
            int i = 0;
            id_distArray = new int[cnt_distDB];
            list_distArray = new String[cnt_distDB];
            if (distModel != null) {
                distModel.removeAllElements();

                ResultSet at = stmt.executeQuery("select at.dist_id, at.dist_name from distance at order by at.dist_id asc");
                while ( at.next() ) {
                    id_distArray[i] = at.getInt("dist_id");
                    list_distArray[i] = at.getString("dist_name");
                    distModel.addElement(list_distArray[i]);
                    i++;
                }
            }
            
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        
        if (distComboBox != null) {
            distComboBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    if(distComboBox.getSelectedIndex()!=-1){
                        int selDisttmp = distComboBox.getSelectedIndex();
                        selDist = id_distArray[selDisttmp];
                    }
                }
            });
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
            stmt.executeUpdate("insert into marks_distance (mark_time, mark_fgh_id) values(" + 
                    TimegetView + ", " + DistPanel.selectedVideo + ")");
            
            /*ResultSet at = stmt.executeQuery("select at.mark_id from marks_move at where at.mark_time=" + MovePanel.selectedVideo + " order by at.mark_time");
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
            stmt.executeUpdate("delete from marks_distance where mark_id = " + selTime);
           
            stmt.close();
            c.close();

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }
    
    public void insertDistDB() {
        
        Connection c = null;
        Statement stmt = null;
        TimegetView = PlaytimeGet.timeGet;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:videok.db");
            stmt = c.createStatement();
            stmt.executeUpdate("update marks_distance set mark_lt_id=" + selAthlete +", mark_dist_id=" + selDist + " where mark_id=" + selTime);
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }
    
    public void importmarkDB() {
        
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:videok.db");
            c.setAutoCommit(true);
            stmt = c.createStatement();
            
            if (distModel != null) {
                distModel.removeAllElements();
            
                stmt.executeUpdate("insert into marks_distance (mark_time, mark_fgh_id, mark_lt_id) select mark_time, mark_fgh_id, mark_lt_id from marks_waza at where at.mark_fgh_id = " + selectedVideo);
            }
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }
    
    public void loadmarkDB() {
    Connection c = null;
    Statement stmtl = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:videok.db");
            c.setAutoCommit(false);
            stmtl = c.createStatement();
            
            ResultSet distDB = stmtl.executeQuery("select * from marks_distance at where at.mark_id = " + selTime);
            while(distDB.next())
            {
                at_markltidDB = (distDB.getInt("mark_lt_id"));
                at_markdistidDB = (distDB.getInt("mark_dist_id"));
            }
            if (at_markltidDB != 0){
                ResultSet athNomeDB = stmtl.executeQuery("select at.at_name from athlete at where at.at_id = " + at_markltidDB);
                String at_name = (athNomeDB.getString("at_name"));
                athleteModel.setSelectedItem(at_name);
                if (at_markdistidDB != 0){
                    distDB = stmtl.executeQuery("select at.dist_name from distance at where at.dist_id = " + at_markdistidDB);
                    String dist_name = (distDB.getString("dist_name"));
                    distModel.setSelectedItem(dist_name);
                }
            }
            stmtl.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.err.println("A");
            System.exit(0);
        }
    }
}