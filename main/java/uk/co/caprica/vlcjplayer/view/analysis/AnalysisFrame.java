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

package uk.co.caprica.vlcjplayer.view.analysis;

import static uk.co.caprica.vlcjplayer.Application.resources;

import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.miginfocom.swing.MigLayout;
import uk.co.caprica.vlcjplayer.event.ShowAnalysisEvent;
import uk.co.caprica.vlcjplayer.view.BaseFrame;
import uk.co.caprica.vlcjplayer.view.analysis.marks.MarksAnalysisPanel;
import uk.co.caprica.vlcjplayer.view.analysis.data.DataAnalysisPanel;

import com.google.common.eventbus.Subscribe;

@SuppressWarnings("serial")
public class AnalysisFrame extends BaseFrame {

    private final JTabbedPane tabbedPane;

    private final DataAnalysisPanel dataAnalysisPanel;
    private final MarksAnalysisPanel marksAnalysisPanel;

    public AnalysisFrame() {
        super(resources().getString("dialog.analysis"));
        
        tabbedPane = new JTabbedPane();
        
        dataAnalysisPanel = new DataAnalysisPanel();
        tabbedPane.addTab(resources().getString("dialog.analysis.tabs.event"), dataAnalysisPanel);

        marksAnalysisPanel = new MarksAnalysisPanel();
        tabbedPane.addTab(resources().getString("dialog.analysis.tabs.fight"), marksAnalysisPanel);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(4,  4,  4,  4));
        contentPane.setLayout(new MigLayout("fill", "[grow]", "[grow]"));
        contentPane.add(tabbedPane, "grow");

        setContentPane(tabbedPane);

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        applyPreferences();
        setResizable(false);
    }

    private void applyPreferences() {
    	
        Preferences prefs = Preferences.userNodeForPackage(AnalysisFrame.class);
        setBounds(
            prefs.getInt("frameX"     , 300),
            prefs.getInt("frameY"     , 300),
            prefs.getInt("frameWidth" , 800),
            prefs.getInt("frameHeight", 500)
        );
        
    }

    @Override
    protected void onShutdown() {
        if (wasShown()) {
            Preferences prefs = Preferences.userNodeForPackage(AnalysisFrame.class);
            prefs.putInt("frameX"      , getX     ());
            prefs.putInt("frameY"      , getY     ());
            //prefs.putInt("frameWidth"  , getWidth ()); //dynamic
            //prefs.putInt("frameHeight" , getHeight()); //dynamic
            prefs.putInt("frameWidth"  , 800);
            prefs.putInt("frameHeight" , 500);
        }
    }

    @Subscribe
    public void onShowAnalysis(ShowAnalysisEvent event) {
        setVisible(true);
    }
}
