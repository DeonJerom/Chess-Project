import javax.swing.*;
public class ChessMain extends JFrame  {
    public static void main(String[] args) {
        new ChessMain();
    }
    public ChessMain(){
        setSize(1000,1000);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        GUI_SecondPlayer chessBoardPanel = new GUI_SecondPlayer();
        add(chessBoardPanel);
        setVisible(true);
    }
}
