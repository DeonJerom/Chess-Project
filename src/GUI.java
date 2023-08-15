import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

public class GUI extends JPanel implements  MouseListener{
    private boolean[][] moves = new boolean[8][8];
    private final PIECES_IMG piece_img = new PIECES_IMG();
    private final ChessPieces[][] pieces = new ChessPieces[8][8];
    private final Logic logic = new Logic();
    private int whitePieces = 16;
    private int blackPieces = 16;
    private COLOR turn = COLOR.WHITE;
    private boolean moved = false;
    private int xCapturing, yCapturing;
    private boolean checkmate = false;

    public GUI(){
        initializeBoard();
        addMouseListener(this);
        setPreferredSize(new Dimension(1250, 950));
    }
    private void initializeBoard() {
        for (int i = 0; i < pieces.length; i++) {
            pieces[1][i] = new ChessPieces(PIECE.PAWN, COLOR.BLACK);
            pieces[6][i] = new ChessPieces(PIECE.PAWN, COLOR.WHITE);
            if (i == 0 || i == 7) {
                pieces[0][i] = new ChessPieces(PIECE.ROOK, COLOR.BLACK);
                pieces[7][i] = new ChessPieces(PIECE.ROOK, COLOR.WHITE);
            } else if (i == 1 || i == 6) {
                pieces[0][i] = new ChessPieces(PIECE.KNIGHT, COLOR.BLACK);
                pieces[7][i] = new ChessPieces(PIECE.KNIGHT, COLOR.WHITE);
            } else if (i == 2 || i == 5) {
                pieces[0][i] = new ChessPieces(PIECE.BISHOP, COLOR.BLACK);
                pieces[7][i] = new ChessPieces(PIECE.BISHOP, COLOR.WHITE);
            } else if (i == 3) {
                pieces[0][i] = new ChessPieces(PIECE.QUEEN, COLOR.BLACK);
                pieces[7][i] = new ChessPieces(PIECE.QUEEN, COLOR.WHITE);
            } else {
                pieces[0][i] = new ChessPieces(PIECE.KING, COLOR.BLACK);
                pieces[7][i] = new ChessPieces(PIECE.KING, COLOR.WHITE);
            }
        }
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, 1000, 1000);
        for (int i = 0, y = 100; i < pieces.length; i++, y += 100) {
            for (int x = 0, z = 110; x < pieces[i].length; x++, z += 100) {
                paintBoard(g2, i, x, z, y);
                if(pieces[i][x] != null) {
                    paintPieces(g2, z, y, pieces[i][x]);
                }
                if(moves[i][x] && moved) {
                    g2.drawImage(piece_img.dot, z+5, y+5, 90, 90, null);
                }
            }
        }
    }
    public void paintBoard(Graphics2D g, int row, int col, int x_pos, int y_pos){
        Image board = ((row + col) % 2 == 0) ? piece_img.black : piece_img.white;
        g.drawImage(board, x_pos, y_pos, 100, 100, null);
    }
    private void paintPieces(Graphics2D g, int row, int col, ChessPieces piece){
        row = row + 10;
        col = col + 5;
        PIECE picked = piece.getPiece();
        if (piece.getType()==COLOR.BLACK) {
            switch(picked) {
                case ROOK -> g.drawImage(piece_img.black_rook, row, col, 80, 80, null);
                case BISHOP -> g.drawImage(piece_img.black_bishop, row, col, 80, 80, null);
                case KING -> g.drawImage(piece_img.black_king, row, col, 80, 80, null);
                case KNIGHT -> g.drawImage(piece_img.black_knight,row,col ,80 ,80 ,null );
                case PAWN -> g.drawImage(piece_img.black_pawn,row,col ,80 ,80 ,null );
                case QUEEN -> g.drawImage(piece_img.black_queen,row,col ,80 ,80 ,null );
            }
        }else {
            switch(picked) {
                case ROOK -> g.drawImage(piece_img.white_rook,row,col ,80 ,80 ,null );
                case BISHOP -> g.drawImage(piece_img.white_bishop,row,col ,80 ,80 ,null );
                case KING -> g.drawImage(piece_img.white_king,row,col ,80 ,80 ,null );
                case KNIGHT -> g.drawImage(piece_img.white_knight,row,col ,80 ,80 ,null );
                case PAWN -> g.drawImage(piece_img.white_pawn,row,col ,80 ,80 ,null );
                case QUEEN -> g.drawImage(piece_img.white_queen,row,col ,80 ,80 ,null );
            }
        }

    }
    private void checkMateStatement(){
        COLOR playerWon = (turn == COLOR.WHITE) ? COLOR.BLACK: COLOR.WHITE;
        System.out.println("Player: "+playerWon+", won the match");
        System.exit(0);
    }
    private void avoidCheckPossible(HashMap<Integer, boolean[][]> map, COLOR opponent, COLOR player){
        if(map.containsKey(1)){
            boolean checkAvoided =  false;
            boolean[][] allLegalCheckPlayer = map.get(1); // getting the checked position is working just fine

            // now get the legal moves of the opponent
            boolean[][] opponentLegalMoves = logic.getLegalMoves(opponent, pieces, false);

            // now iterate through foundCheck and if a path is found
            for(int i =0;i<allLegalCheckPlayer.length;i++) {
                for (int x = 0; x < allLegalCheckPlayer[i].length; x++) {
                    if (allLegalCheckPlayer[i][x] && opponentLegalMoves[i][x]) {
                        checkAvoided = true;
                        break;
                    }
                }
            }

            // Checks if player checkmated or not
            if(!checkAvoided){
                // we need to get all possible moves of the player, a modified version
                boolean[][] playerPath = logic.getLegalMoves(player, pieces, true);
                boolean[][] kingMove = logic.kingCheckMoves(opponent, playerPath, pieces);

                for(int i =0;i<kingMove.length;i++){
                    for(int x=0;x<kingMove[i].length;x++){
                        if(kingMove[i][x]){
                            checkAvoided = true;
                            break;
                        }
                    }
                }

            }
            checkmate = (checkAvoided == false) ? true: false;
        }
    }
    public void mouseClicked(MouseEvent e) {
        if(!checkmate){
            int xClicked = (int) Math.floor(getMousePosition().getX() / 100) - 1;
            int yClicked = (int) Math.floor(getMousePosition().getY() / 100) - 1;
            if(xClicked >= 0 && xClicked < pieces.length && yClicked >= 0 && yClicked < pieces.length){
                if(!moved){
                    if(pieces[yClicked][xClicked] != null){ // if the cell selected isn't empty
                        if(turn == pieces[yClicked][xClicked].getType()){
                            // generating moves need to be changed

                            moves = logic.getMoves(pieces[yClicked][xClicked], xClicked, yClicked, pieces);
                            moved = true;
                            xCapturing = xClicked;
                            yCapturing = yClicked;
                            repaint();
                        }
                    }
                }else{
                    moved = false;
                    if(moves[yClicked][xClicked]){
                        pieceCapture(xClicked, yClicked);
                        pieces[yCapturing][xCapturing].setIsMoved(true);
                        // Special case for king # CASTLING #
                        if(pieces[yCapturing][xCapturing].getPiece() == PIECE.KING){
                            handleKingMove(xClicked, yClicked);
                        }else{
                            handleOtherMoves(xClicked, yClicked);
                            COLOR temp = (turn == COLOR.WHITE) ? COLOR.BLACK: COLOR.WHITE;
                            avoidCheckPossible(logic.IsChecked(temp, xClicked, yClicked, pieces), temp, turn); // checks if this moved piece made a check or not
                        }
                        switchTurn(); // switches the color
                    }
                    repaint();
                }
            }
        }else{
            checkMateStatement();
        }
    }
    public void printMoves(){
        System.out.println("+++++++++++++++++++++");
        for(int i =0;i<moves.length;i++){
            for(int x=0;x<moves[i].length;x++){
                if(moves[i][x]){
                    System.out.print(1+":");
                }else{
                    System.out.print(0+":");
                }
            }
            System.out.println();
        }
        System.out.println("+++++++++++++++++++++");
    }
    private void handleOtherMoves(int xClicked, int yClicked){
        pieces[yClicked][xClicked] = pieces[yCapturing][xCapturing];
        pieces[yCapturing][xCapturing] = null;
    }
    private void handleKingMove(int xReleased, int yReleased) {
        if (pieces[yReleased][xReleased] != null) {
            if (pieces[yReleased][xReleased].getPiece() == PIECE.ROOK) {
                int diff = Math.abs(xCapturing - xReleased);
                if (diff == 3) {
                    pieces[yCapturing][5] = pieces[yReleased][xReleased];
                    pieces[yCapturing][6] = pieces[yCapturing][xCapturing];
                    pieces[yCapturing][xCapturing] = null;
                    pieces[yReleased][xReleased] = null;
                }else if (diff == 4) {
                    pieces[yCapturing][3] = pieces[yReleased][xReleased];
                    pieces[yCapturing][2] = pieces[yCapturing][xCapturing];
                    pieces[yCapturing][xCapturing] = null;
                    pieces[yReleased][xReleased] = null;
                }
            }else {
                handleOtherMoves(xReleased, yReleased);
            }
        }else {
            handleOtherMoves(xReleased, yReleased);
        }
    }
    private void pieceCapture(int xReleased, int yReleased) {
        if(pieces[yReleased][xReleased] != null){
            if(pieces[yCapturing][xCapturing].getType() == COLOR.WHITE && pieces[yReleased][xReleased].getType() == COLOR.BLACK) {
                blackPieces -= 1;
            }else if(pieces[yCapturing][xCapturing].getType() == COLOR.BLACK && pieces[yReleased][xReleased].getType() == COLOR.WHITE){
                whitePieces -=1;
            }
        }
    }
    private void switchTurn(){
        turn = (turn == COLOR.WHITE) ? COLOR.BLACK: COLOR.WHITE;
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
