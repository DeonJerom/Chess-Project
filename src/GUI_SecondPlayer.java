import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GUI_SecondPlayer extends JPanel implements  MouseListener{
    private boolean[][] moves = new boolean[8][8];
    private boolean[][] allLegalCheckPlayer;
    private boolean[][] kingMove;
    private final PIECES_IMG piece_img = new PIECES_IMG();
    private final ChessPieces[][] pieces = new ChessPieces[8][8];
    private final Logic logic = new Logic();
    private final AI AI = new AI();
    private int whitePieces = 16;
    private int blackPieces = 16;
    private COLOR turn = COLOR.WHITE;
    private boolean moved = false;
    private int xCapturing, yCapturing;
    private boolean InCheck = false;

    public GUI_SecondPlayer(){
        
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
        g.setColor(Color.BLACK);
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
        COLOR playerWon = (turn == COLOR.WHITE) ? COLOR.WHITE: COLOR.BLACK;
        System.out.println("Player: "+playerWon+", won the match");
        removeMouseListener(this);
        System.exit(0);
    }
    private void staleMateStatement(){
        System.out.println("Game has stopped either because of not enough pieces/no moves possible");
        removeMouseListener(this);
        System.exit(0);
    }
    private boolean avoidCheckPossible(boolean[][] allLegalCheckPlayer, COLOR opponent, COLOR player){

        boolean checkMateAvoided = false;
        this.allLegalCheckPlayer = allLegalCheckPlayer; // getting the checked position is working just fine
        // now get the legal moves of the opponent
        boolean[][] opponentLegalMoves = logic.getLegalMoves(opponent, pieces, false);
        // now iterate through foundCheck and if a path is found
        for(int i =0;i<allLegalCheckPlayer.length;i++) {
            for (int x = 0; x < allLegalCheckPlayer[i].length; x++) {
                if (allLegalCheckPlayer[i][x] && opponentLegalMoves[i][x]) {
                    InCheck = true;
                    checkMateAvoided = true;
                    break;
                }
            }
        }

        // Checks if player checkmated or not
        if(!checkMateAvoided){
            // we need to get all possible moves of the player, a modified version
            boolean[][] playerPath = logic.getLegalMoves(player, pieces, true);
            kingMove = logic.kingCheckMoves(opponent, playerPath, pieces);
            for (boolean[] booleans : kingMove) {
                for (boolean aBoolean : booleans) {
                    if (aBoolean) {
                        checkMateAvoided = true;
                        break;
                    }
                }
            }
        }
        return !checkMateAvoided;
    }
    public void mouseClicked(MouseEvent e) {

        int xClicked = (int) Math.floor(getMousePosition().getX() / 100) - 1;
        int yClicked = (int) Math.floor(getMousePosition().getY() / 100) - 1;
        if(xClicked >= 0 && xClicked < pieces.length && yClicked >= 0 && yClicked < pieces.length){
            if(!moved){
                if(pieces[yClicked][xClicked] != null){ // if the cell selected isn't empty
                    if(turn == pieces[yClicked][xClicked].getType()){
                        // Check if king is in trouble meaning, only king can move
                        if(kingMove == null){
                            // generating moves need to be changed, first check if there is a check or not ?
                            if(InCheck){
                                // only create moves that can avoid it
                                moves = logic.getMoves(pieces[yClicked][xClicked], xClicked, yClicked, pieces, allLegalCheckPlayer);
                            }else{
                                moves = logic.getMoves(pieces[yClicked][xClicked], xClicked, yClicked, pieces, null);
                            }
                            moved = true;
                            xCapturing = xClicked;
                            yCapturing = yClicked;
                            repaint();
                        }else{
                            if(pieces[yClicked][xClicked].getPiece() == PIECE.KING){
                                moves = kingMove;
                                moved = true;
                                xCapturing = xClicked;
                                yCapturing = yClicked;
                                repaint();
                            }
                        }
                    }
                }
            }else{
                moved = false;
                if(moves[yClicked][xClicked]){
                    setPieceMoved(xClicked, yClicked, xCapturing, yCapturing);
                    movePiece();
                }
                repaint();
            }
        }

    }
    private void makeAIMove(){
        while(true){
            movePiece();
            repaint();
        }
    }
    private void movePiece(){

        // gets the color of player moving
        COLOR playerColor = turn;
        // now we need to pick a position at random
        int aiXCapturing = -1, aiYCapturing = -1;
        int aiXCaptured = -1, aiYCaptured = -1;
        boolean[][] legal_Positions = new boolean[8][8];
        boolean[][] possibleMoves = new boolean[8][8];
        // check whether only king is allowed to move
        if(kingMove == null){
            // next check whether in check or not
            if(InCheck){
                // create position can be made to avoid the check
                // allLegalCheckPlayer -> use this to modify getAllegalPositions
                legal_Positions = AI.getAllLegalPositions(playerColor, pieces, allLegalCheckPlayer);

                // iterate through the array can find a position to move
                for(int i =0;i<legal_Positions.length;i++){
                    for(int j=0;j<legal_Positions[i].length;j++){
                        if(legal_Positions[i][j]){
                            aiYCapturing = i;
                            aiXCapturing = j;
                            break;
                            // position to be moved is selected
                        }
                    }
                }


                // gets all possible moves for this particular piece
                possibleMoves = logic.getMoves(pieces[aiYCapturing][aiXCapturing], aiXCapturing, aiYCapturing, pieces, allLegalCheckPlayer);

            }else{  // create a normal function to return things

                // create a function to return all possible starting positions
                legal_Positions = AI.getAllLegalPositions(playerColor, pieces, null);
                // iterate through the array can find a position to move
                for(int i =0;i<legal_Positions.length;i++){
                    for(int j=0;j<legal_Positions[i].length;j++){
                        if(legal_Positions[i][j]){
                            aiYCapturing = i;
                            aiXCapturing = j;
                            break;
                            // position to be moved is selected
                        }
                    }
                }
                // gets all possible moves for this particular piece
                possibleMoves = logic.getMoves(pieces[aiYCapturing][aiXCapturing], aiXCapturing, aiYCapturing, pieces, null);
            }
        }else{

            // gets all possible moves for this king piece
            possibleMoves = this.kingMove;
            // iterate through the array to find the position of the king
            for(int i =0;i<pieces.length;i++){
                for(int j=0;j<pieces[i].length;j++){
                    if(pieces[i][j] != null){
                        if(pieces[i][j].getPiece() == PIECE.KING && pieces[i][j].getType() == playerColor){
                            aiXCapturing = j;
                            aiYCapturing = i;
                            break;
                        }
                    }
                }
            }

        }

        // gets all random possible moves
        for(int i =0;i<possibleMoves.length;i++){
            for(int j=0;j<possibleMoves[i].length;j++){
                if(possibleMoves[i][j]){
                    aiYCaptured = i;
                    aiXCaptured = j;
                    break;
                    // position to be moved is selected
                }
            }
        }

        setPieceMoved(aiXCaptured, aiYCaptured, aiXCapturing, aiYCapturing); // moves piece
    }
    private void setPieceMoved(int xCaptured, int yCaptured, int xCapturing, int yCapturing){

        pieceCapture(xCaptured, yCaptured, xCapturing , yCapturing);
        pieces[yCapturing][xCapturing].setIsMoved(true);
        // Special case for king # CASTLING #
        if(pieces[yCapturing][xCapturing].getPiece() == PIECE.KING){
            handleKingMove(xCaptured, yCaptured, xCapturing, yCapturing);
        }else{
            handleOtherMoves(xCaptured, yCaptured, xCapturing, yCapturing);
            COLOR temp = (turn == COLOR.WHITE) ? COLOR.BLACK : COLOR.WHITE;
            // checks if this moved piece made a check or not
            if(IsStalemate(temp)){
                staleMateStatement();
            }

            boolean[][] legalChecks = logic.IsChecked(temp, xCaptured, yCaptured, pieces);
            if(legalChecks != null){
                if(avoidCheckPossible(legalChecks, temp, turn)){
                    checkMateStatement();
                }
            }else {
                boolean[][] hiddenChecks = logic.getHiddenChecks(temp, pieces);
                if (hiddenChecks != null) {
                    if (avoidCheckPossible(hiddenChecks, temp, turn)) {
                        checkMateStatement();
                    }
                }
            }
        }
        switchTurn();

    }
    private boolean IsStalemate(COLOR playerColor) {
        boolean IsStalemate = true;
        // Get all legal moves for the current player
        boolean[][] playerMoves = logic.getLegalMoves(playerColor, pieces, false);

        // Check if there are any legal moves left
        for (boolean[] playerMove : playerMoves) {
            for (boolean IsMovable : playerMove) {
                if (IsMovable) {
                    IsStalemate = false; // At least one legal move found, not stalemate
                    break;
                }
            }
        }
        if(IsStalemate){
            // might need to check king move
            boolean[][] kingMove = new boolean[8][8];
            for (int i = 0; i < pieces.length; i++) {
                for (int j = 0; j < pieces[i].length; j++) {
                    if(pieces[i][j] != null && pieces[i][j].getPiece() == PIECE.KING && pieces[i][j].getType() == playerColor){
                        kingMove =  logic.getMoves(pieces[i][j], j, i, pieces, null);
                        break;
                    }
                }
            }
            // Now check if a move exists
            for (boolean[] booleans : kingMove) {
                for (boolean aBoolean : booleans) {
                    if (aBoolean) {
                        IsStalemate = false; // At least one legal move found, not stalemate
                        break;
                    }
                }
            }
        }
        return IsStalemate; // No legal moves left, stalemate
    }
    public void printMoves(boolean[][] arr){
        System.out.println("+++++++++++++++++++++");
        if(arr != null){
            for (boolean[] booleans : arr) {
                for (boolean aBoolean : booleans) {
                    if (aBoolean) {
                        System.out.print(1 + ":");
                    } else {
                        System.out.print(0 + ":");
                    }
                }
                System.out.println();
            }
        }
        System.out.println("+++++++++++++++++++++");
    }
    private void handleOtherMoves(int xCaptured, int yCaptured, int xCapturing, int yCapturing){
        pieces[yCaptured][xCaptured] = pieces[yCapturing][xCapturing];
        pieces[yCapturing][xCapturing] = null;
        this.InCheck = false;
        this.kingMove = null;
    }
    private void handleKingMove(int xCaptured, int yCaptured, int xCapturing, int yCapturing) {
        if (pieces[yCaptured][xCaptured] != null) {
            if (pieces[yCaptured][xCaptured].getPiece() == PIECE.ROOK && pieces[yCaptured][xCaptured].getType() == turn) {
                int diff = Math.abs(xCapturing - xCaptured);
                if (diff == 3) {
                    pieces[yCapturing][5] = pieces[yCaptured][xCaptured];
                    pieces[yCapturing][6] = pieces[yCapturing][xCapturing];
                    pieces[yCapturing][xCapturing] = null;
                    pieces[yCaptured][xCaptured] = null;
                }else if (diff == 4) {
                    pieces[yCapturing][3] = pieces[yCaptured][xCaptured];
                    pieces[yCapturing][2] = pieces[yCapturing][xCapturing];
                    pieces[yCapturing][xCapturing] = null;
                    pieces[yCaptured][xCaptured] = null;
                }
                this.InCheck = false;
                this.kingMove = null;
            }else {
                handleOtherMoves(xCaptured, yCaptured, xCapturing, yCapturing);
            }
        }else {
            handleOtherMoves(xCaptured, yCaptured, xCapturing, yCapturing);
        }
    }
    private void pieceCapture(int xCaptured, int yCaptured, int xCapturing, int yCapturing) {
        if(pieces[yCaptured][xCaptured] != null){
            if(pieces[yCapturing][xCapturing].getType() == COLOR.WHITE && pieces[yCaptured][xCaptured].getType() == COLOR.BLACK) {
                blackPieces -= 1;
            }else if(pieces[yCapturing][xCapturing].getType() == COLOR.BLACK && pieces[yCaptured][xCaptured].getType() == COLOR.WHITE){
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
