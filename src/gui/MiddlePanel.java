package gui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class MiddlePanel extends JPanel {
    private CivilizationsGUI gui;
    private JTextArea log;
    private JPanel threatPanel;
    private JLabel threatIcon;
    private JLabel threatLabel;

    public MiddlePanel(CivilizationsGUI gui) {
        this.gui = gui;
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Eventos y Estado", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), Color.WHITE));

        log = new JTextArea();
        log.setEditable(false);
        log.setBackground(Color.DARK_GRAY);
        log.setForeground(Color.WHITE);
        log.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(log);
        scroll.setBorder(null);

        threatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        threatPanel.setBackground(Color.BLACK);
        threatIcon = new JLabel(gui.loadIcon("alert.png", 32, 32));
        threatLabel = new JLabel("No hay amenaza");
        threatLabel.setForeground(Color.GREEN);
        threatPanel.add(threatIcon);
        threatPanel.add(threatLabel);

        add(threatPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    public void appendLog(String msg) {
        log.append(msg);
        log.setCaretPosition(log.getDocument().getLength());
    }

    public void refresh() {
        // No necesita actualización extra
    }

    public void setThreatActive(boolean active) {
        if (active) {
            threatLabel.setText("¡AMENAZA PRESENTE!");
            threatLabel.setForeground(Color.RED);
        } else {
            threatLabel.setText("Sin amenaza");
            threatLabel.setForeground(Color.GREEN);
        }
    }
}