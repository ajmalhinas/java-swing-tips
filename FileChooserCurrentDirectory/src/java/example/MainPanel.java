// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import javax.swing.*;

public final class MainPanel extends JPanel {
  private final JTextArea log = new JTextArea();
  private final JTextField field = new JTextField(24);
  private final JCheckBox check1 = new JCheckBox("Change !dir.exists() case");
  private final JCheckBox check2 = new JCheckBox("isParent reset?");
  private final JFileChooser fc0 = new JFileChooser();
  private final JFileChooser fc1 = new JFileChooser();
  private final JFileChooser fc2 = new JFileChooser() {
    @Override public void setCurrentDirectory(File dir) {
      File current = dir;
      if (Objects.nonNull(current) && !isTraversable(current)) {
        current = current.getParentFile();
        while (Objects.nonNull(current) && !isTraversable(current)) {
          current = current.getParentFile();
        }
      }
      super.setCurrentDirectory(current);
    }

    // @Override public void setCurrentDirectory(File dir) {
    //   if (Objects.nonNull(dir) && !dir.exists()) {
    //     this.setCurrentDirectory(dir.getParentFile());
    //   } else {
    //     super.setCurrentDirectory(dir);
    //   }
    // }
  };

  @Override public void updateUI() {
    super.updateUI();
    EventQueue.invokeLater(() -> {
      SwingUtilities.updateComponentTreeUI(fc0);
      SwingUtilities.updateComponentTreeUI(fc1);
      SwingUtilities.updateComponentTreeUI(fc2);
    });
  }

  private MainPanel() {
    super(new BorderLayout());

    JPanel p = new JPanel(new GridBagLayout());
    p.setBorder(BorderFactory.createTitledBorder("JFileChooser.DIRECTORIES_ONLY"));
    fc0.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fc1.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fc2.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    try {
      field.setText(new File(".").getCanonicalPath());
    } catch (IOException ex) {
      ex.printStackTrace();
      UIManager.getLookAndFeel().provideErrorFeedback(field);
    }

    JButton button1 = new JButton("setCurrentDirectory");
    button1.addActionListener(e -> {
      File f = new File(field.getText().trim());
      JFileChooser fc = check1.isSelected() ? fc2 : fc0;
      fc.setCurrentDirectory(f);
      int retValue = fc.showOpenDialog(p);
      if (retValue == JFileChooser.APPROVE_OPTION) {
        log.setText(fc.getSelectedFile().getAbsolutePath());
      }
    });

    JButton button2 = new JButton("setSelectedFile");
    button2.addActionListener(e -> {
      File f = new File(field.getText().trim());
      JFileChooser fc = fc1;
      System.out.format(
          "isAbsolute: %s, isParent: %s%n",
          f.isAbsolute(), !fc.getFileSystemView().isParent(fc.getCurrentDirectory(), f));
      fc.setSelectedFile(f);
      int retValue = fc.showOpenDialog(p);
      if (retValue == JFileChooser.APPROVE_OPTION) {
        log.setText(fc.getSelectedFile().getAbsolutePath());
      }
      if (check2.isSelected()) {
        fc.setSelectedFile(f.getParentFile()); // XXX: reset???
      }
    });

    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.insets = new Insets(5, 0, 0, 0);

    c.gridwidth = 2;
    p.add(field, c);

    c.gridwidth = 1;
    c.gridy = 1;
    p.add(button1, c);
    p.add(check1, c);

    c.gridy = 2;
    p.add(button2, c);
    p.add(check2, c);

    add(p, BorderLayout.NORTH);
    add(new JScrollPane(log));
    setPreferredSize(new Dimension(320, 240));
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(MainPanel::createAndShowGui);
  }

  private static void createAndShowGui() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      // UIManager.put("FileChooser.readOnly", Boolean.TRUE);
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
