import javax.swing.*;
public class GUIFrame {
    public static void showgui() {
        // invoke later for tread safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                /* set title for window
                * set size of window
                * assign program will terminate when window close
                * set window is visible
                * detail of content of window will set in MainFrame*/
                JFrame f = new MainFrame("Deduplicater"); //creating instance of JFrame
                f.setSize(500,1000);
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setVisible(true);

            }
        });
    }

}
