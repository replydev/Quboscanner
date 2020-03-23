package qubo.gui;

import javax.swing.border.*;
import com.intellij.uiDesigner.core.*;
import qubo.Info;
import qubo.InputData;
import qubo.QuboInstance;
import utils.Log;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;

public class MainWindow extends JFrame{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static DefaultTableModel dtm;

    private QuboInstance quboInstance;
    
    private Thread instanceThread;
    private InstanceRunnable instanceRunnable;
    
    private Thread progressBarThread;
    private ProgressBarRunnable progressBarRunnable;
    private Point initialClick;

    private final JFrame meMyselfAndI;

    public MainWindow(){
        initComponents();
        setUndecorated(true);
        meMyselfAndI = this;
        me.setText(" QuboScanner - " + Info.version + " " + Info.otherVersionInfo + " | ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100,750);
        setContentPane(pannello);
        stateLabel.setForeground(Color.green.darker().darker());
        progressBar1.setStringPainted(true);
        progressBar1.setString("Idle");
        setVisible(true);
        setupTable();

        startButton.addActionListener(e -> {
            InputData i;
            try{
                i = new InputData(getArgsFromInputMask());
            }catch (Exception ex){
                MessageWindow.showMessage("Invalid arguments!","Insert a valid input to use the program");
                return;
            }
            running(i);
            quboInstance = null;
        });
        stopButton.addActionListener(e -> idle());
        saveResultsButton.addActionListener(e -> {
            if(resultsTable.getRowCount() <= 0){
                MessageWindow.showMessage("Error during saving","Results table is empty!");
                return;
            }
            try{
                saveToFile();
            } catch (IOException ex) {
                Log.log_to_file(ex.toString(),"log.txt");
            }

        });
        creditsButton.addActionListener(e -> new CreditsForm());
        me.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        Desktop.getDesktop().browse(new URI("https://qubo.best"));
                    } catch (IOException | URISyntaxException ex) {
                        Log.log_to_file(ex.toString(),"log.txt");
                    }
                }
            }
        });
        exitButton.addActionListener(e -> {
            if(instanceRunnable != null) instanceRunnable.stop();
            System.exit(0);

        });
        toolbar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                getComponentAt(initialClick);
            }
        });
        toolbar.addMouseMotionListener(new MouseMotionAdapter() {
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

    public void idle(){
        instanceRunnable.stop();
        instanceThread = null;
        progressBarRunnable.stop();
        progressBarThread = null;
        quboInstance = null;
        progressBar1.setString("100%");
        progressBar1.setValue(100);

        stopButton.setEnabled(false);
        ipStartTextField.setEnabled(true);
        ipEndTextField.setEnabled(true);
        portRangeTextField.setEnabled(true);
        threadTextField.setEnabled(true);
        timeoutTextField.setEnabled(true);
        startButton.setEnabled(true);
        stateLabel.setText("Idle");
        stateLabel.setForeground(Color.green.darker().darker());
        doAllCheckBox.setEnabled(true);
        pingCheckBox.setEnabled(true);
        oldThreadingCheckBox.setEnabled(true);
        motdText.setEnabled(true);
        minPlayersText.setEnabled(true);
        versionText.setEnabled(true);
    }

    private void running(InputData i){
        quboInstance = new QuboInstance(i);
        dtm.setRowCount(0);
        Info.serverFound = 0;
        Info.serverNotFilteredFound = 0;

        instanceRunnable = new InstanceRunnable(quboInstance,this);
        instanceThread = new Thread(instanceRunnable);
        instanceThread.start();

        progressBarRunnable = new ProgressBarRunnable(progressBar1,quboInstance);
        progressBarThread = new Thread(progressBarRunnable);
        progressBarThread.start();

        ipStartTextField.setEnabled(false);
        ipEndTextField.setEnabled(false);
        portRangeTextField.setEnabled(false);
        threadTextField.setEnabled(false);
        timeoutTextField.setEnabled(false);
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        stateLabel.setText("Running");
        stateLabel.setForeground(Color.red);
        doAllCheckBox.setEnabled(false);
        pingCheckBox.setEnabled(false);
        oldThreadingCheckBox.setEnabled(false);
        motdText.setEnabled(false);
        minPlayersText.setEnabled(false);
        versionText.setEnabled(false);
    }

    private void saveToFile() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(meMyselfAndI);
        File file;
        if(option == JFileChooser.APPROVE_OPTION){
            file = fileChooser.getSelectedFile();
        }
        else return;
        if(!file.createNewFile()){
            MessageWindow.showMessage("Error during saving","Cannot create file, try to run me as administrator");
            return;
        }
        PrintWriter os = new PrintWriter(file);
        for (int row = 0; row < resultsTable.getRowCount(); row++) {
            for (int col = 0; col < resultsTable.getColumnCount(); col++) {
                os.print(resultsTable.getValueAt(row,col));
                os.print(" - ");
            }
            os.println();
        }
        os.close();
    }

    private String[] getArgsFromInputMask(){
        //-start <arg> -end <arg> -range <arg> -th <arg> -ti <arg>

        String command = "-range " + ipStartTextField.getText() + "-" + ipEndTextField.getText() + " " +
                "-ports " + portRangeTextField.getText() + " " +
                "-th " + threadTextField.getText() + " " +
                "-ti " + timeoutTextField.getText();

        if(!pingCheckBox.isSelected()) command += " -noping";
        if(doAllCheckBox.isSelected()) command += " -all";
        if(oldThreadingCheckBox.isSelected()) command += " -oldthreading";
        if(!versionText.getText().isEmpty()) command += " -ver " + versionText.getText();
        if(!motdText.getText().isEmpty()) command += " -motd " + motdText.getText();
        if(!minPlayersText.getText().isEmpty()) command += " -on " + minPlayersText.getText();

        return command.split(" ");
    }

    private void setupTable(){
        dtm = new MyTableModel();

        resultsTable.setModel(dtm);
        TableColumnModel columnModel = resultsTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(5);
        columnModel.getColumn(1).setPreferredWidth(100);
        columnModel.getColumn(2).setPreferredWidth(70);
        columnModel.getColumn(3).setPreferredWidth(100);
        columnModel.getColumn(4).setPreferredWidth(136);
        columnModel.getColumn(5).setPreferredWidth(453);
        /*JTableHeader header = resultsTable.getTableHeader();
        header.setOpaque(false);
        header.setBackground(GRAY);
        header.setForeground(Color.WHITE);*/

        resultsTable.setSelectionBackground(Color.white);
        resultsTable.setSelectionForeground(Color.black);
        resultsTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table =(JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    String ip = table.getModel().getValueAt(row, 1).toString();
                    String port = table.getModel().getValueAt(row, 2).toString();
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(new StringSelection(ip + ":" + port),null);
                }
            }
        });
        resultsTable.getTableHeader().setReorderingAllowed(false);
    }

    private void initComponents() {
        pannello = new JPanel();
        ipRangeLabel = new JLabel();
        ipStartTextField = new JTextField();
        ipEndTextField = new JTextField();
        portRangeTextField = new JTextField();
        timeoutTextField = new JTextField();
        timeoutLabel = new JLabel();
        portRangeLabel = new JLabel();
        stopButton = new JButton();
        JScrollPane scrollPane1 = new JScrollPane();
        resultsTable = new JTable();
        progressBar1 = new JProgressBar();
        stateLabel = new JLabel();
        toolbar = new JToolBar();
        me = new JLabel();
        saveResultsButton = new JButton();
        creditsButton = new JButton();
        exitButton = new JButton();
        pingCheckBox = new JCheckBox();
        doAllCheckBox = new JCheckBox();
        threadTextField = new JTextField();
        startButton = new JButton();
        toLabel = new JLabel();
        threadsLabel = new JLabel();
        oldThreadingCheckBox = new JCheckBox();
        JLabel label1 = new JLabel();
        motdText = new JTextField();
        JLabel label2 = new JLabel();
        JLabel label3 = new JLabel();
        minPlayersText = new JTextField();
        versionText = new JTextField();

        //======== pannello ========
        {
            pannello.setEnabled(false);
            pannello.setBorder (new javax. swing. border. CompoundBorder( new javax .swing .border .TitledBorder (new javax. swing
            . border. EmptyBorder( 0, 0, 0, 0) , "", javax. swing. border. TitledBorder
            . CENTER, javax. swing. border. TitledBorder. BOTTOM, new java .awt .Font ("Dia\u006cog" ,java .
            awt .Font .BOLD ,12 ), java. awt. Color. red) ,pannello. getBorder( )) )
            ; pannello. addPropertyChangeListener (e -> {if ("\u0062ord\u0065r" .equals (e .getPropertyName () )) throw new RuntimeException( ); })
            ;
            pannello.setLayout(new GridLayoutManager(7, 9, new Insets(0, 0, 0, 0), -1, -1));

            //---- ipRangeLabel ----
            ipRangeLabel.setText("IP Range");
            pannello.add(ipRangeLabel, new GridConstraints(1, 0, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 1));

            //---- ipStartTextField ----
            ipStartTextField.setToolTipText("Put here the starting ip");
            pannello.add(ipStartTextField, new GridConstraints(1, 1, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED,
                null, null, null));

            //---- ipEndTextField ----
            ipEndTextField.setToolTipText("Put here the ending ip");
            pannello.add(ipEndTextField, new GridConstraints(1, 3, 1, 6,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED,
                null, null, null));

            //---- portRangeTextField ----
            portRangeTextField.setToolTipText("Example: 25565-25577");
            pannello.add(portRangeTextField, new GridConstraints(2, 1, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED,
                null, null, null));

            //---- timeoutTextField ----
            timeoutTextField.setText("");
            timeoutTextField.setToolTipText("Best timeout option is 500");
            pannello.add(timeoutTextField, new GridConstraints(3, 1, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED,
                null, null, null));

            //---- timeoutLabel ----
            timeoutLabel.setText("Timeout");
            pannello.add(timeoutLabel, new GridConstraints(3, 0, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 1));

            //---- portRangeLabel ----
            portRangeLabel.setText("Port Range");
            pannello.add(portRangeLabel, new GridConstraints(2, 0, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 1));

            //---- stopButton ----
            stopButton.setEnabled(false);
            stopButton.setFocusable(false);
            stopButton.setText("Stop");
            pannello.add(stopButton, new GridConstraints(4, 3, 1, 2,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED,
                null, null, null));

            //======== scrollPane1 ========
            {
                scrollPane1.setBorder(new TitledBorder(""));

                //---- resultsTable ----
                resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                resultsTable.setFillsViewportHeight(true);
                resultsTable.setGridColor(Color.black);
                resultsTable.setSelectionForeground(new Color(90, 90, 90));
                scrollPane1.setViewportView(resultsTable);
            }
            pannello.add(scrollPane1, new GridConstraints(6, 0, 1, 9,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                null, null, null));
            pannello.add(progressBar1, new GridConstraints(5, 0, 1, 9,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED,
                null, null, null));

            //---- stateLabel ----
            stateLabel.setText("Idle");
            pannello.add(stateLabel, new GridConstraints(4, 5, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED,
                null, null, null));

            //======== toolbar ========
            {
                toolbar.setFloatable(false);

                //---- me ----
                me.setText("  QuboScanner  ");
                toolbar.add(me);

                //---- saveResultsButton ----
                saveResultsButton.setFocusable(false);
                saveResultsButton.setOpaque(false);
                saveResultsButton.setText("Save Results");
                toolbar.add(saveResultsButton);

                //---- creditsButton ----
                creditsButton.setFocusable(false);
                creditsButton.setText("Credits");
                toolbar.add(creditsButton);

                //---- exitButton ----
                exitButton.setText("Exit");
                toolbar.add(exitButton);
            }
            pannello.add(toolbar, new GridConstraints(0, 0, 1, 9,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED,
                null, null, null));

            //---- pingCheckBox ----
            pingCheckBox.setFocusable(false);
            pingCheckBox.setText("Ping");
            pannello.add(pingCheckBox, new GridConstraints(4, 6, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED,
                null, null, null));

            //---- doAllCheckBox ----
            doAllCheckBox.setFocusable(false);
            doAllCheckBox.setText("Check all");
            pannello.add(doAllCheckBox, new GridConstraints(4, 7, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED,
                null, null, null));

            //---- threadTextField ----
            threadTextField.setToolTipText("Amount of threads to use while scanning.");
            pannello.add(threadTextField, new GridConstraints(2, 3, 1, 6,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED,
                null, null, null));

            //---- startButton ----
            startButton.setAutoscrolls(true);
            startButton.setFocusable(false);
            startButton.setText("Start");
            startButton.setToolTipText("Start the party!");
            pannello.add(startButton, new GridConstraints(4, 0, 1, 3,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 1));

            //---- toLabel ----
            toLabel.setText("to");
            pannello.add(toLabel, new GridConstraints(1, 2, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 1));

            //---- threadsLabel ----
            threadsLabel.setText("Threads");
            pannello.add(threadsLabel, new GridConstraints(2, 2, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 1));

            //---- oldThreadingCheckBox ----
            oldThreadingCheckBox.setFocusable(false);
            oldThreadingCheckBox.setText("Old Threading   ");
            pannello.add(oldThreadingCheckBox, new GridConstraints(4, 8, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_FIXED,
                null, null, null));

            //---- label1 ----
            label1.setText("MOTD");
            pannello.add(label1, new GridConstraints(3, 2, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 1));
            pannello.add(motdText, new GridConstraints(3, 3, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED,
                null, null, null));

            //---- label2 ----
            label2.setText("Version");
            pannello.add(label2, new GridConstraints(3, 4, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 1));

            //---- label3 ----
            label3.setText("MinPlayers");
            pannello.add(label3, new GridConstraints(3, 7, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                GridConstraints.SIZEPOLICY_FIXED,
                GridConstraints.SIZEPOLICY_FIXED,
                null, null, null, 1));
            pannello.add(minPlayersText, new GridConstraints(3, 8, 1, 1,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED,
                null, null, null));
            pannello.add(versionText, new GridConstraints(3, 5, 1, 2,
                GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
                GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED,
                null, null, null));
        }
    }

    private JPanel pannello;
    private JLabel ipRangeLabel;
    private JTextField ipStartTextField;
    private JTextField ipEndTextField;
    private JTextField portRangeTextField;
    private JTextField timeoutTextField;
    private JLabel timeoutLabel;
    private JLabel portRangeLabel;
    private JButton stopButton;
    private JTable resultsTable;
    public JProgressBar progressBar1;
    private JLabel stateLabel;
    private JToolBar toolbar;
    private JLabel me;
    private JButton saveResultsButton;
    private JButton creditsButton;
    private JButton exitButton;
    private JCheckBox pingCheckBox;
    private JCheckBox doAllCheckBox;
    private JTextField threadTextField;
    private JButton startButton;
    private JLabel toLabel;
    private JLabel threadsLabel;
    private JCheckBox oldThreadingCheckBox;
    private JTextField motdText;
    private JTextField minPlayersText;
    private JTextField versionText;
}
