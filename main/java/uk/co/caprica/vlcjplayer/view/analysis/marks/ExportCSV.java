/*
 * Copyright (C) 2018 Katidoki
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.caprica.vlcjplayer.view.analysis.marks;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import static uk.co.caprica.vlcjplayer.time.Time.formatTime;



public class ExportCSV {
    public static void main(String tab) {
        String filename ="VideoK-SQLite-"+tab+".csv";
        try {
            FileWriter fw = new FileWriter(filename);
            File f = new File(filename);
            
            Connection c = null;
            Statement stmt = null;
            
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:videok.db");
            c.setAutoCommit(false);
            stmt = c.createStatement();

            ResultSet rs = stmt.executeQuery("select  mk.mark_fgh_id as LUTA, mk.mark_id as ID, mk.mark_time as TIME,\n"
                        + "at.at_name as ATHLETE, tp.tcp_name as TECHNICAL, wz.waz_name as WAZA, ut.uts_name as UTSU, mk.mark_ipp_id as IPPON\n"
                        + "from marks_waza mk\n"
                        + "left join athlete at on (mk.mark_lt_id = at.at_id)\n"
                        + "left join technical_patterns tp on (mk.mark_tcp_id = tp.tcp_id)\n"
                        + "left join waza wz on (mk.mark_waz_id = wz.waz_id)\n"
                        + "left join utsu ut on (mk.mark_uts_id = ut.uts_id)\n"
                        + "order by LUTA, mk.mark_time asc");
            
            while (rs.next()) {
                fw.append(rs.getString(1));
                fw.append(';');
                fw.append(rs.getString(2));
                fw.append(';');
                fw.append(formatTime(rs.getLong(3)));
                fw.append(';');
                fw.append(rs.getString(4));
                fw.append(';');
                fw.append(rs.getString(5));
                fw.append(';');
                fw.append(rs.getString(6));
                fw.append(';');
                fw.append(rs.getString(7));
                fw.append(';');
                fw.append(rs.getString(8));
                fw.append('\n');
               }
            fw.flush();
            fw.close();
            
            stmt.close();
            c.close();
           
            JOptionPane.showMessageDialog(null, "CSV file is created successfully: "+f.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}