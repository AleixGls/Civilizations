package gui;

import civilizations.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ThreatFrame extends JFrame {
    public ThreatFrame(ArrayList<MilitaryUnit> enemyArmy) {
        setTitle("Amenaza Enemiga");
        setSize(500, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new FlowLayout());
        getContentPane().setBackground(Color.BLACK);

        int[] counts = new int[4];
        for (MilitaryUnit u : enemyArmy) {
            if (u instanceof Swordsman) counts[0]++;
            else if (u instanceof Spearman) counts[1]++;
            else if (u instanceof Crossbow) counts[2]++;
            else if (u instanceof Cannon) counts[3]++;
        }
        String[] names = {"Espadachín", "Lancero", "Ballesta", "Cañón"};
        String[] icons = {"swordsman.png", "spearman.png", "crossbow.png", "cannon.png"};

        for (int i = 0; i < 4; i++) {
            if (counts[i] > 0) {
                JPanel panel = new JPanel();
                panel.setBackground(Color.DARK_GRAY);
                panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                ImageIcon icon = loadIcon(icons[i], 64, 64);
                panel.add(new JLabel(icon));
                JLabel text = new JLabel(names[i] + " x" + counts[i]);
                text.setForeground(Color.WHITE);
                panel.add(text);
                add(panel);
            }
        }
        setVisible(true);
    }

    private ImageIcon loadIcon(String fileName, int width, int height) {
        try {
            String path = "./src/gui/images/" + fileName;
            ImageIcon icon = new ImageIcon(path);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return new ImageIcon();
        }
    }
}