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

package uk.co.caprica.vlcjplayer.view.analysis.data;

import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.DefaultListModel;
import javax.swing.JButton;

import javax.swing.JLabel;
import javax.swing.JList;
import static javax.swing.JOptionPane.showMessageDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import net.miginfocom.swing.MigLayout;
import static uk.co.caprica.vlcjplayer.Application.resources;
import uk.co.caprica.vlcjplayer.view.BasePanel;

public class EventPanel extends BasePanel {
    
    private final JLabel nameLabel, locationLabel, fightsLabel;
    private final JTextField nameEvent, locationEvent;
    DefaultListModel fightsName;
    
    public String ev_nameDB, ev_locationDB, fg_namesDB, at_fightDB, at_nameDBtmp;
    
    public EventPanel() {
        
        fightsName = new DefaultListModel();
        JList fightsList = new JList(fightsName);
        
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());

        JPanel controlsPane = new JPanel();
        controlsPane.setLayout(new MigLayout("fillx", "[][]", "[]5[]"));

        contentPane.add(controlsPane, BorderLayout.NORTH);
        setLayout(new BorderLayout());
        add(contentPane, BorderLayout.CENTER);
        
        this.NovoBanco();
        this.eventDB();
        this.fightsDB();
        
        nameLabel = new JLabel(resources().getString("dialog.analysis.field.name"));
        nameEvent = new JTextField(ev_nameDB, 50);
        locationLabel = new JLabel(resources().getString("dialog.analysis.field.place"));
        locationEvent = new JTextField(ev_locationDB, 50);
        
        fightsLabel = new JLabel(resources().getString("dialog.analysis.field.fights"));
        fightsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fightsList.setSelectedIndex(0);
        fightsList.setVisibleRowCount(20); 
        fightsList.setFixedCellWidth(200);
        
        JScrollPane fightsListScrollPane = new JScrollPane(fightsList);    
        JButton showButton = new JButton(resources().getString("dialog.analysis.field.select"));
        
        controlsPane.add((nameLabel), "");
        controlsPane.add((nameEvent), "wrap");
        controlsPane.add((locationLabel), "");
        controlsPane.add((locationEvent), "wrap");
        
        controlsPane.add((fightsLabel), "");
        controlsPane.add((fightsListScrollPane), "");    
        //controlsPane.add((showButton), "");
    }
    
    public void eventDB() {
        
        Connection c = null;
        Statement stmt = null;
        try {
           Class.forName("org.sqlite.JDBC");
           c = DriverManager.getConnection("jdbc:sqlite:videok.db");
           c.setAutoCommit(false);

           stmt = c.createStatement();
           
           ResultSet ev = stmt.executeQuery( "select ev.ev_name, ev.ev_location from event ev;" );
           if (ev.getString("ev_name") != null)
           {
                ev_nameDB = ev.getString("ev_name");
                ev_locationDB = ev.getString("ev_location");
           }
           
           stmt.close();
           c.close();
        } catch ( Exception e ) {
           System.err.println( e.getClass().getName() + ": " + e.getMessage() );
           //System.exit(0);
           showMessageDialog(null, e.getClass().getName() + " (eventEv): " + e.getMessage());
        }
    }
    
    public void fightsDB() {
        
        Connection c = null;
        Statement stmt = null;
        try {
           Class.forName("org.sqlite.JDBC");
           c = DriverManager.getConnection("jdbc:sqlite:videok.db");
           c.setAutoCommit(false);

           stmt = c.createStatement();
           
           ResultSet fg = stmt.executeQuery( "select fh.lt_id, fh.lt_date, at.at_name from fights fh left join athlete at ON (at.at_id = fh.lt_at_id_a) OR (at.at_id = fh.lt_at_id_b) order by fh.lt_id asc" );
           String fg_tmp = null;
           while ( fg.next() ) {
                at_fightDB = fg.getString("lt_id");
                if (at_fightDB.equals(fg_tmp))
                {
                    fg_namesDB = at_nameDBtmp + " vs " + fg.getString("at_name");
                    fightsName.addElement(fg_namesDB);
                    //System.out.println(fg_namesDB);
                } else {
                    at_nameDBtmp = fg.getString("at_name");
                    fg_tmp = at_fightDB;
                }
           }
           
           stmt.close();
           c.close();
        } catch ( Exception e ) {
           System.err.println( e.getClass().getName() + ": " + e.getMessage() );
           //System.exit(0);
           showMessageDialog(null, e.getClass().getName() + " (eventFg): " + e.getMessage());
        }
    }
    
    public void NovoBanco() {

      Connection c = null;
      Statement stmt = null;
      
      try {
         Class.forName("org.sqlite.JDBC");
         c = DriverManager.getConnection("jdbc:sqlite:videok.db");
         stmt = c.createStatement();
         String sql = 

"CREATE TABLE IF NOT EXISTS academy (or_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, or_name TEXT, or_location TEXT);"+

"CREATE TABLE IF NOT EXISTS athlete (at_id INTEGER NOT NULL PRIMARY KEY, at_name TEXT NOT NULL, at_or_id INTEGER NOT NULL REFERENCES academy (or_id), at_gnd_id TEXT NOT NULL REFERENCES gender (gnd_id), at_age INTEGER NOT NULL);"+

"CREATE TABLE IF NOT EXISTS category (ct_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ct_name TEXT);"+

"CREATE TABLE IF NOT EXISTS class (cl_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, cl_name TEXT NOT NULL);"+
"INSERT OR IGNORE INTO class (cl_id, cl_name) VALUES (1, 'INDIVIDUAL');"+
"INSERT OR IGNORE INTO class (cl_id, cl_name) VALUES (2, 'TEAMS');"+

"CREATE TABLE IF NOT EXISTS datotsu (dtt_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, dtt_mrk_id INTEGER NOT NULL, dtt_ath_id INTEGER NOT NULL, dtt_tcp_id INTEGER NOT NULL, dtt_waz_id INTEGER NOT NULL, dtt_uts_id INTEGER NOT NULL);"+

"CREATE TABLE IF NOT EXISTS event (ev_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ev_name TEXT NOT NULL, ev_location TEXT NOT NULL);"+

"CREATE TABLE IF NOT EXISTS fights (lt_id INTEGER NOT NULL, lt_cl_id TEXT NOT NULL REFERENCES class (cl_id), lt_ct_id TEXT NOT NULL REFERENCES category (ct_id), lt_date TEXT, lt_fase TEXT NOT NULL, lt_ev_id TEXT NOT NULL REFERENCES event (ev_id), lt_at_id_a TEXT NOT NULL, lt_at_id_b TEXT NOT NULL);"+

"CREATE TABLE IF NOT EXISTS gender (gnd_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, gnd_name TEXT NOT NULL);"+
"INSERT OR IGNORE INTO gender (gnd_id, gnd_name) VALUES (1, 'MALE');"+
"INSERT OR IGNORE INTO gender (gnd_id, gnd_name) VALUES (2, 'FEMALE');"+
                 
"CREATE TABLE IF NOT EXISTS distance (dist_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, dist_name TEXT NOT NULL);"+
"INSERT OR IGNORE INTO distance (dist_id, dist_name) VALUES (1, 'CHIKAMA');"+
"INSERT OR IGNORE INTO distance (dist_id, dist_name) VALUES (2, 'ITTOMA');"+
"INSERT OR IGNORE INTO distance (dist_id, dist_name) VALUES (3, 'TOUMA');"+

"CREATE TABLE IF NOT EXISTS movement (move_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, move_name TEXT NOT NULL);"+
"INSERT OR IGNORE INTO movement (move_id, move_name) VALUES (1, 'MAE');"+
"INSERT OR IGNORE INTO movement (move_id, move_name) VALUES (2, 'UCHIRO');"+
"INSERT OR IGNORE INTO movement (move_id, move_name) VALUES (3, 'MIGI');"+
"INSERT OR IGNORE INTO movement (move_id, move_name) VALUES (4, 'HIDARI');"+

"CREATE TABLE IF NOT EXISTS marks_distance (std_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL);"+

"CREATE TABLE IF NOT EXISTS marks_move (tmm_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL);"+

"CREATE TABLE IF NOT EXISTS marks_waza (mark_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, mark_time INTEGER NOT NULL, mark_fgh_id INTEGER NOT NULL, mark_lt_id INTEGER, mark_tcp_id INTEGER, mark_waz_id INTEGER, mark_uts_id INTEGER);"+

"CREATE TABLE IF NOT EXISTS result (res_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, res_name STRING NOT NULL);"+
"INSERT OR IGNORE INTO result (res_id, res_name) VALUES (1, 'WIN');"+
"INSERT OR IGNORE INTO result (res_id, res_name) VALUES (2, 'LOSE');"+

"CREATE TABLE IF NOT EXISTS technical_patterns (tcp_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, tcp_name STRING NOT NULL);"+
"INSERT OR IGNORE INTO technical_patterns (tcp_id, tcp_name) VALUES (1, 'SHIKAKE');"+
"INSERT OR IGNORE INTO technical_patterns (tcp_id, tcp_name) VALUES (2, 'OJI');"+
"INSERT OR IGNORE INTO technical_patterns (tcp_id, tcp_name) VALUES (3, 'HANSOKU');"+

"CREATE TABLE IF NOT EXISTS utsu (uts_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, uts_name STRING NOT NULL);"+
"INSERT OR IGNORE INTO utsu (uts_id, uts_name) VALUES (1, 'MEN');"+
"INSERT OR IGNORE INTO utsu (uts_id, uts_name) VALUES (2, 'KOTE');"+
"INSERT OR IGNORE INTO utsu (uts_id, uts_name) VALUES (3, 'DO');"+
"INSERT OR IGNORE INTO utsu (uts_id, uts_name) VALUES (4, 'TSUKI');"+
"INSERT OR IGNORE INTO utsu (uts_id, uts_name) VALUES (5, 'HANSOKU');"+

"CREATE TABLE IF NOT EXISTS waza (waz_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, waz_name STRING NOT NULL, waz_techp_id INT NOT NULL REFERENCES technical_patterns (tcp_id));"+
"INSERT OR IGNORE INTO waza (waz_id, waz_name, waz_techp_id) VALUES (1, 'RENZOKU', 1);"+
"INSERT OR IGNORE INTO waza (waz_id, waz_name, waz_techp_id) VALUES (2, 'HARAI', 1);"+
"INSERT OR IGNORE INTO waza (waz_id, waz_name, waz_techp_id) VALUES (3, 'DEBANA', 1);"+
"INSERT OR IGNORE INTO waza (waz_id, waz_name, waz_techp_id) VALUES (4, 'HIKI', 1);"+
"INSERT OR IGNORE INTO waza (waz_id, waz_name, waz_techp_id) VALUES (5, 'HIKIBANA', 1);"+
"INSERT OR IGNORE INTO waza (waz_id, waz_name, waz_techp_id) VALUES (6, 'OSAE', 1);"+
"INSERT OR IGNORE INTO waza (waz_id, waz_name, waz_techp_id) VALUES (7, 'KATSUGU', 1);"+
"INSERT OR IGNORE INTO waza (waz_id, waz_name, waz_techp_id) VALUES (8, 'MAKI', 1);"+
"INSERT OR IGNORE INTO waza (waz_id, waz_name, waz_techp_id) VALUES (9, 'SEME', 1);"+
"INSERT OR IGNORE INTO waza (waz_id, waz_name, waz_techp_id) VALUES (20, 'KAESHI', 2);"+
"INSERT OR IGNORE INTO waza (waz_id, waz_name, waz_techp_id) VALUES (21, 'SURIAGE', 2);"+
"INSERT OR IGNORE INTO waza (waz_id, waz_name, waz_techp_id) VALUES (22, 'NUKI', 2);"+
"INSERT OR IGNORE INTO waza (waz_id, waz_name, waz_techp_id) VALUES (23, 'UCHIOTOSHI', 2);"+
"INSERT OR IGNORE INTO waza (waz_id, waz_name, waz_techp_id) VALUES (24, 'KIRIOTOSHI', 2);"+
"INSERT OR IGNORE INTO waza (waz_id, waz_name, waz_techp_id) VALUES (30, 'SHIAIJO OUT', 3);"+
"INSERT OR IGNORE INTO waza (waz_id, waz_name, waz_techp_id) VALUES (31, 'SHINAI DROP', 3);"+
"INSERT OR IGNORE INTO waza (waz_id, waz_name, waz_techp_id) VALUES (32, 'MISCONDUCT', 3);"+
"INSERT OR IGNORE INTO waza (waz_id, waz_name, waz_techp_id) VALUES (33, 'SHIAI DELAY', 3);"+
"INSERT OR IGNORE INTO waza (waz_id, waz_name, waz_techp_id) VALUES (34, 'EQUIPMENT', 3);";

         stmt.executeUpdate(sql);
         stmt.close();
         c.close();
         //showMessageDialog(null, "Banco criado!!");
      } catch ( Exception e ) {
         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
         //System.exit(0);
         showMessageDialog(null, e.getClass().getName() + " (DB creating): " + e.getMessage());
      }
   }
}
