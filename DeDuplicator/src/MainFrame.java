import javax.swing.*;
import javax.swing.plaf.basic.BasicOptionPaneUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MainFrame extends JFrame {

    // Frames component

    private final JTextArea progressDisplay = new JTextArea();
    private final JTextArea staDisplay = new JTextArea();
    private final JTextField fnameField = new JTextField();
    private final JTextField lockerField = new JTextField();
    private final JTextArea searchRes = new JTextArea();
    private final JRadioButton rb1 = new JRadioButton("ASCII");
    private final JRadioButton rb2 = new JRadioButton("UTF8");
    private final JRadioButton rb3 = new JRadioButton("UTF16");
//    private final JButton store = new JButton("Store");
//    private final JButton extract = new JButton("Extract");
//    private final JButton search = new JButton("Search");



    public MainFrame(String title) {
        // grid bag layout
        // swing visual guide
        super(title);

        // set main frame property
        setSize(500, 1000);
        setPreferredSize(getSize());

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // set layout manager for component
//        setLayout(new BorderLayout()); // let you add top bottom left right center...
        GridBagLayout format = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        setLayout(format);
        setTitle("DeDuplicator");
        this.setLayout(format);

         final JLabel topLabel = new JLabel("storage progress");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.01;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        this.add(topLabel, gbc);

//        final JTextArea progressDisplay = new JTextArea();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.05;
        gbc.weighty = 0.05;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        JScrollPane prog_scroll = new JScrollPane(progressDisplay);
//        progressDisplay.setEnabled(false);
        prog_scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(prog_scroll, gbc);
//        gbc.ipady = 50;
//        gbc.ipadx = 300;


        final JLabel staLabel = new JLabel("storage statics");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 0.01;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.NONE;
//        gbc.ipady = 0;
//        gbc.ipadx = 0;
        this.add(staLabel, gbc);

//        final JTextArea staDisplay = new JTextArea();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.05;
        gbc.weighty = 0.05;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
//        gbc.ipady = 0;
//        gbc.ipadx = 0;
        JScrollPane sta_scroll = new JScrollPane(staDisplay);
//        progressDisplay.setEnabled(false);
        prog_scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(sta_scroll, gbc);


        final JLabel fname = new JLabel("FileName: ");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.1;
        gbc.weighty = 0.01;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        this.add(fname, gbc);

//        final JTextField fnameField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 0.1;
        gbc.weighty = 0.01;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        this.add(fnameField, gbc);

        final JLabel lockname = new JLabel("LockerName: ");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.1;
        gbc.weighty = 0.01;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        this.add(lockname, gbc);

//        final JTextField lockerField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 0.1;
        gbc.weighty = 0.01;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        this.add(lockerField, gbc);

        final JLabel searchSub = new JLabel("Search substring: ");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.1;
        gbc.weighty = 0.01;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        this.add(searchSub, gbc);

        final JTextField findField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weightx = 0.1;
        gbc.weighty = 0.01;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        this.add(findField, gbc);

        final JButton store = new JButton("Store");
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0.1;
        gbc.weighty = 0.01;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        // add behavior for button
        store.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Runtime runtime = Runtime.getRuntime();
                try{
                    runtime.exec("java dedup -addFile "+fnameField.getText()+
                            " -locker "+lockerField.getText());
                    System.exit(0);
                }catch(IOException ioe){
                    ioe.printStackTrace();
                }
            }
        });
        this.add(store, gbc);

        final JButton extract = new JButton("Extract");
        extract.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Runtime runtime = Runtime.getRuntime();
                try{
                    runtime.exec("java dedup -retrieveFile "+fnameField.getText()+
                            " -locker "+lockerField.getText());
                    System.exit(0);
                }catch(IOException ioe){
                    ioe.printStackTrace();
                }
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.weightx = 0.1;
        gbc.weighty = 0.01;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        this.add(extract, gbc);

        final JButton search = new JButton("Search");
        search.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Runtime runtime = Runtime.getRuntime();
                try{
                    runtime.exec("java dedup -findSubString "+findField.getText()+
                            " -locker "+lockerField.getText()+" "+checkformat());
//                    runtime.exec("java dedup -findSubString ABC -locker testLocker 0");
                    System.exit(0);
                }catch(IOException ioe){
                    ioe.printStackTrace();
                }
            }
        });
        gbc.gridx = 2;
        gbc.gridy = 7;
        gbc.weightx = 0.1;
        gbc.weighty = 0.01;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        this.add(search, gbc);

        final JButton deletion = new JButton("Delete");
        deletion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Runtime runtime = Runtime.getRuntime();
                try{
                    runtime.exec("java dedup -deleteFile "+fnameField.getText()+
                            " -locker "+lockerField.getText());
//                    runtime.exec("java dedup -findSubString ABC -locker testLocker 0");
                    System.exit(0);
                }catch(IOException ioe){
                    ioe.printStackTrace();
                }
            }
        });
        gbc.gridx = 3;
        gbc.gridy = 7;
        gbc.weightx = 0.1;
        gbc.weighty = 0.01;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        this.add(deletion, gbc);

        final JLabel searchLabel = new JLabel("SearchResult: ");
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.weightx = 0.1;
        gbc.weighty = 0.01;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        this.add(searchLabel, gbc);

//        [ASCII, UTF8, UTF16]
//        final JRadioButton rb1 = new JRadioButton("ASCII");
        gbc.gridx = 0; gbc.gridy = 9;
        gbc.weightx = 0.1; gbc.weighty = 0.01;
        this.add(rb1, gbc);

//        final JRadioButton rb2 = new JRadioButton("UTF8");
        gbc.gridx = 0; gbc.gridy = 10;
        gbc.weightx = 0.1; gbc.weighty = 0.01;
        this.add(rb2, gbc);

//        final JRadioButton rb3 = new JRadioButton("UTF16");
        gbc.gridx = 0; gbc.gridy = 11;
        gbc.weightx = 0.1; gbc.weighty = 0.01;
        this.add(rb3, gbc);

//        final JTextArea searchRes = new JTextArea();
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.weightx = 0.1;
        gbc.weighty = 0.1;
        gbc.gridwidth = 3; gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.BOTH;
        JScrollPane search_scroll = new JScrollPane(searchRes);
//        progressDisplay.setEnabled(false);
        prog_scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(search_scroll, gbc);
        setVisible(true);
    }

//    private final JTextArea progressDisplay = new JTextArea();
//    private final JTextArea staDisplay = new JTextArea();
//    private final JTextField fnameField = new JTextField();
//    private final JTextField lockerField = new JTextField();
//    private final JTextArea searchRes = new JTextArea();
    // adding textfield writing method
    public void write2progress(String text) {
        progressDisplay.append(text+"\n");
    }

    public void delProgress() {
        progressDisplay.setText(null);
    }

    public void write2status(String text) {
        staDisplay.append(text+"\n");
    }

    public void delStatus() {
        staDisplay.setText(null);
    }

    public void write2searchRes (String text) {
        searchRes.append(text+"\n");
    }

    public void delSearchRes() {
        searchRes.setText(null);
    }

    public Integer checkformat () {
        Integer ans = 10; // default is ascii
//        while (!rb1.isSelected() && !rb2.isSelected() && !rb3.isSelected()) {
//            Runtime.getRuntime().halt(0);
//        }
        if (rb1.isSelected()) {
            ans = 0;
        } else if (rb2.isSelected()) {
            ans = 1; // utf8
        } else if (rb3.isSelected()) {
            ans = 2; // UTF16
        }
        return ans;
    }

    // add behavior for button










    // add swing components to content pane
//        Container c = getContentPane();
//        c.add(testArea, BorderLayout.CENTER);
//        c.add(button, BorderLayout.SOUTH);



        // add behavior ******
//        button.addActionListener(new ActionListener(){
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                testArea.append("Hello World! \n");
//            }
//        });
//
//    }


}