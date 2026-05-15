package gui;

import civilizations.Battle;
import javax.swing.*;
import java.awt.*;
import java.util.List;  // Importación correcta

public class BattleReportsFrame extends JFrame {
    private List<Battle> battleHistory;  // Ahora es java.util.List
    private JPanel listPanel;
    private JTextArea displayArea;
    private int currentBattleIndex;
    private boolean showingReport;

    public BattleReportsFrame(List<Battle> battleHistory) {
        this.battleHistory = battleHistory;
        this.currentBattleIndex = -1;
        this.showingReport = true;
        setTitle("Informes de Batalla");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel izquierdo con lista de batallas
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.DARK_GRAY);
        listPanel.setBorder(BorderFactory.createTitledBorder("Batallas"));

        for (int i = 0; i < battleHistory.size(); i++) {
            final int idx = i;
            JButton btn = new JButton("Batalla " + (i + 1));
            btn.setBackground(Color.BLACK);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.addActionListener(e -> showBattle(idx));
            listPanel.add(btn);
            listPanel.add(Box.createVerticalStrut(5));
        }

        JScrollPane listScroll = new JScrollPane(listPanel);
        listScroll.setPreferredSize(new Dimension(200, 0));
        add(listScroll, BorderLayout.WEST);

        // Panel central con área de texto
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setBackground(Color.BLACK);
        displayArea.setForeground(Color.WHITE);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane textScroll = new JScrollPane(displayArea);
        add(textScroll, BorderLayout.CENTER);

        // Panel inferior con botones de control
        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton switchViewBtn = new JButton("Cambiar vista (Resumen/Desarrollo)");
        JButton closeBtn = new JButton("Cerrar");

        switchViewBtn.addActionListener(e -> {
            if (currentBattleIndex != -1) {
                showingReport = !showingReport;
                updateDisplay();
            }
        });
        closeBtn.addActionListener(e -> dispose());

        bottomPanel.add(switchViewBtn);
        bottomPanel.add(closeBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        if (!battleHistory.isEmpty()) {
            showBattle(0);
        } else {
            displayArea.setText("No hay batallas registradas.");
        }

        setVisible(true);
    }

    private void showBattle(int index) {
        currentBattleIndex = index;
        showingReport = true;
        updateDisplay();
    }

    private void updateDisplay() {
        if (currentBattleIndex < 0 || currentBattleIndex >= battleHistory.size()) return;
        Battle b = battleHistory.get(currentBattleIndex);
        if (showingReport) {
            displayArea.setText("=== INFORME DE BATALLA " + (currentBattleIndex + 1) + " ===\n\n" + b.getBattleReport());
        } else {
            displayArea.setText("=== DESARROLLO DE BATALLA " + (currentBattleIndex + 1) + " ===\n\n" + b.getBattleDevelopment());
        }
        displayArea.setCaretPosition(0);
    }
}