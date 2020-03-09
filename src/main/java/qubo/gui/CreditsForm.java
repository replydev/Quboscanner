package qubo.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.*;
import com.intellij.uiDesigner.core.*;

class CreditsForm extends JFrame{
    private final CreditsForm meMyselfAndI;
    private Point initialClick;

    public CreditsForm() {
        initComponents();
        meMyselfAndI = this;
        //setTitle("Credits");
        setUndecorated(true);
        setContentPane(pannello);
        setSize(425,100);
        setResizable(false);
        setVisible(true);
        closeButton.addActionListener(e -> meMyselfAndI.setVisible(false));
        pannello.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                getComponentAt(initialClick);
            }
        });
        pannello.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                //super.mouseDragged(e);
                // get location of Window
                int thisX = meMyselfAndI.getLocation().x;
                int thisY = meMyselfAndI.getLocation().y;

                // Determine how much the mouse moved since the initial click
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;

                // Move window to this position
                int X = thisX + xMoved;
                int Y = thisY + yMoved;
                meMyselfAndI.setLocation(X, Y);
            }
        });
    }

    private void initComponents() {
        pannello = new JPanel();
        JLabel label1 = new JLabel();
        closeButton = new JButton();
        JLabel label2 = new JLabel();

        //======== pannello ========
        {
            pannello.setBorder (new javax. swing. border. CompoundBorder( new javax .swing .border .TitledBorder (new javax. swing.
            border. EmptyBorder( 0, 0, 0, 0) , "", javax. swing. border. TitledBorder. CENTER
            , javax. swing. border. TitledBorder. BOTTOM, new java .awt .Font ("Dia\u006cog" ,java .awt .Font
            .BOLD ,12 ), java. awt. Color. red) ,pannello. getBorder( )) ); pannello. addPropertyChangeListener (
                e -> {if ("bord\u0065r"
                .equals (e .getPropertyName () )) throw new RuntimeException( ); });
            pannello.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));

            //---- label1 ----
            label1.setText("QuboScanner by @zReply on Telegram - qubo.best - discord.io/quboscanner");
            pannello.add(label1, new GridConstraints(1, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED,
                null, null, null));

            //---- closeButton ----
            closeButton.setText("Close");
            pannello.add(closeButton, new GridConstraints(2, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED,
                null, null, null));

            //---- label2 ----
            label2.setText("Credits");
            pannello.add(label2, new GridConstraints(0, 0, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED,
                null, null, null));
        }
    }
    private JPanel pannello;
    private JButton closeButton;

}
