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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.ListSelectionModel;

import net.miginfocom.swing.MigLayout;
import static uk.co.caprica.vlcjplayer.Application.resources;
import uk.co.caprica.vlcjplayer.view.BasePanel;

public class AthletePanel extends BasePanel {
    
    //private final JTextField athleteNewfield;
    //private final JComboBox<String> genderNewField;
    //private final JLabel athleteNewLabel, genderNewLabel,
            private final JLabel statusLabel, athletesLabel;
    DefaultListModel athleteName;

    public AthletePanel() {

        athleteName = new DefaultListModel();
        JList athleteList = new JList(athleteName);
        
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());

        JPanel controlsPane = new JPanel();
        controlsPane.setLayout(new MigLayout("fillx", "[][][][][]", "[]5[]5[]5[]5[]"));

        contentPane.add(controlsPane, BorderLayout.NORTH);
        setLayout(new BorderLayout());
        add(contentPane, BorderLayout.CENTER);
        
        this.athleteDB();
        
        statusLabel = new JLabel("Selected: ",JLabel.CENTER);    
        statusLabel.setSize(600,600);
        
        athletesLabel = new JLabel(resources().getString("dialog.analysis.field.athletes"));
        athleteList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        athleteList.setSelectedIndex(0);
        athleteList.setVisibleRowCount(20); 
        athleteList.setFixedCellWidth(200);
        
        JScrollPane athleteListScrollPane = new JScrollPane(athleteList);    
        JButton showButton = new JButton(resources().getString("dialog.analysis.field.select"));
        
        showButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) { 
            String data = "";
            if (athleteList.getSelectedIndex() != -1) {                     
               data = "Selected: " + athleteList.getSelectedValue(); 
               statusLabel.setText(data);
            }
            statusLabel.setText(data);
         }
      });

        controlsPane.add((athletesLabel), "");
        controlsPane.add(athleteListScrollPane);    
        controlsPane.add(showButton);
        controlsPane.add(statusLabel);
    }
    
    public void athleteDB() {
        
        Connection c = null;
        Statement stmt = null;
        try {
           Class.forName("org.sqlite.JDBC");
           c = DriverManager.getConnection("jdbc:sqlite:videok.db");
           c.setAutoCommit(false);

           stmt = c.createStatement();
           
           ResultSet at = stmt.executeQuery( "select at.at_name, og.or_name from athlete at left join academy og ON og.or_id = at.at_or_id;" );
           while ( at.next() ) {
              String at_name = at.getString("at_name");
              String or_name = at.getString("or_name");
              
              String at_data = at_name + " - " + or_name;
              athleteName.addElement(at_data);
              
              //System.out.println( "Name = " + at_name );
              //System.out.println();
           }
           //at.close();
           stmt.close();
           c.close();
        } catch ( Exception e ) {
           System.err.println( e.getClass().getName() + ": " + e.getMessage() );
           //System.exit(0);
           showMessageDialog(null, e.getClass().getName() + " (athlete): " + e.getMessage());
        }
    }  
}
