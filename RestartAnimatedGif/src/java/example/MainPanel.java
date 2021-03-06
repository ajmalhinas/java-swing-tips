// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;

public final class MainPanel extends JPanel {
  private MainPanel() {
    super(new BorderLayout());

    URL url = getClass().getResource("9-0.gif");
    BufferedImage bi;
    try {
      bi = ImageIO.read(url);
    } catch (IOException ex) {
      ex.printStackTrace();
      bi = makeMissingImage();
    }
    ImageIcon icon9 = new ImageIcon(bi);
    ImageIcon animatedIcon = new ImageIcon(url);

    JTextArea textArea = new JTextArea();
    JButton button = new JButton(icon9) {
      @Override protected void fireStateChanged() {
        ButtonModel m = getModel();
        if (isRolloverEnabled() && m.isRollover()) {
          textArea.append("JButton: Rollover, Image: flush\n");
          animatedIcon.getImage().flush();
        }
        super.fireStateChanged();
      }
    };
    button.setRolloverIcon(animatedIcon);
    button.setPressedIcon(new Icon() {
      @Override public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(Color.BLACK);
        g2.fillRect(x, y, getIconWidth(), getIconHeight());
        g2.dispose();
      }

      @Override public int getIconWidth() {
        return icon9.getIconWidth();
      }

      @Override public int getIconHeight() {
        return icon9.getIconHeight();
      }
    });

    JLabel label = new JLabel(animatedIcon);
    label.addMouseListener(new MouseAdapter() {
      @Override public void mousePressed(MouseEvent e) {
        textArea.append("JLabel: mousePressed, Image: flush\n");
        animatedIcon.getImage().flush();
        repaint(getBounds());
      }
    });

    JPanel p = new JPanel(new GridLayout(1, 2, 5, 5));
    p.add(makeTitledPanel("JButton#setRolloverIcon", button));
    p.add(makeTitledPanel("mousePressed: flush", label));
    add(p, BorderLayout.NORTH);
    add(new JScrollPane(textArea));
    setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    setPreferredSize(new Dimension(320, 240));
  }

  private static Component makeTitledPanel(String title, Component c) {
    JPanel p = new JPanel();
    p.setBorder(BorderFactory.createTitledBorder(title));
    p.add(c);
    return p;
  }

  private static BufferedImage makeMissingImage() {
    Icon missingIcon = UIManager.getIcon("OptionPane.errorIcon");
    int w = missingIcon.getIconWidth();
    int h = missingIcon.getIconHeight();
    BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2 = bi.createGraphics();
    missingIcon.paintIcon(null, g2, 0, 0);
    g2.dispose();
    return bi;
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(MainPanel::createAndShowGui);
  }

  private static void createAndShowGui() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
      ex.printStackTrace();
      Toolkit.getDefaultToolkit().beep();
    }
    JFrame frame = new JFrame("@title@");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.getContentPane().add(new MainPanel());
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}
