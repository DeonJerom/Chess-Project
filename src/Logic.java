import javax.xml.stream.FactoryConfigurationError;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Logic {
    public final int[][] knight = {{-1,-2},{1,-2}, {2,-1}, {2,1},};
    public final int[][] bishop = {{-1,-1}, {1,-1}, {-1,1}, {1,1},};
    public final int[][] rook = {{0,-1}, {1,0}, {0,1}, {-1,0},};
    public final int[][] king = {{0,-1}, {1,-1}, {1,0}, {1,1}, {0,1}, {-1,1}, {-1,0}, {-1,-1}};
    private int checkCounts = 0;

    private int hiddenCheckCounts = 0;
    public boolean[][] getHiddenChecks(COLOR playerColor, ChessPieces[][] pieces){
        this.hiddenCheckCounts = 0;
        boolean[][] hiddenChecks = new boolean[8][8];
        int x = -1, y = -1;
        // iterate through the 2d array and find where king is
        for(int i =0;i<pieces.length;i++){
            for(int k=0;k<pieces[i].length;k++){
                if(pieces[i][k] != null){
                    if(pieces[i][k].getPiece() == PIECE.KING && pieces[i][k].getType() == playerColor){
                        x = k;
                        y = i;
                        break;
                    }
                }
            }
        }
        // I need to search as a rook, queen and bishops, as these are the only pieces to give a hidden check when a piece is movec
        for (int[] ints : rook) {
            int dx = ints[0];
            int dy = ints[1];
            recursHidden(playerColor, PIECE.ROOK, x+dx, y+dy, dx, dy, pieces, hiddenChecks);
        }
        // Next with bishop and queen
        for (int[] ints : bishop) {
            int dx = ints[0];
            int dy = ints[1];
            recursHidden(playerColor, PIECE.BISHOP, x+dx, y+dy, dx, dy, pieces, hiddenChecks);
        }
        if(hiddenCheckCounts == 1){
            return hiddenChecks;
        }
        return null;

    }
    private boolean recursHidden(COLOR playerColor, PIECE target, int x, int y, int dx, int dy, ChessPieces[][] pieces, boolean[][] hiddenChecks) {
        // Recursive call Base case
        if (x < 0 || x >= pieces.length || y < 0 || y >= pieces.length) {
            return false;
        }
        // Things to check off
        if(pieces[y][x] != null){
            if(pieces[y][x].getType() != playerColor){
                if(target == PIECE.ROOK){
                    if(pieces[y][x].getPiece() == PIECE.ROOK || pieces[y][x].getPiece() == PIECE.QUEEN){
                        hiddenChecks[y][x] = true;
                        hiddenCheckCounts = 1;
                        return true;
                    }else{
                        return false;
                    }
                }else{
                    if(pieces[y][x].getPiece() == PIECE.BISHOP || pieces[y][x].getPiece() == PIECE.QUEEN){
                        hiddenChecks[y][x] = true;
                        hiddenCheckCounts = 1;
                        return true;
                    }else{
                        return false;
                    }
                }
            }else{
                return false;
            }
        }
        if(recursHidden(playerColor, target, x+dx, y+dy, dx, dy, pieces, hiddenChecks)){
            hiddenChecks[y][x] = true;
            return true;
        }
        return false;
    }
    /*
        In this function, we collect all the possible moves of a given players pieces,
        before finding the moves, first we need to avoid so calledd barrier pieces that avoids a check
    */
    public boolean[][] checkBarrier(COLOR playerColor, int x, int y, ChessPieces[][] pieces){
        boolean[][] barrierPieces = new boolean[8][8];
        /*
            We are going to search different ways to find a hidden check,
            first we need to detect a same piece, then check if a opposite piece is behind it
         */
        // Starting with rook and queen
        for (int[] ints : rook) {
            int dx = ints[0];
            int dy = ints[1];
            recursBarrier(playerColor, PIECE.ROOK, x+dx, y+dy, dx, dy, pieces, barrierPieces, 0, 0, false);
        }
        // Next with bishop and queen
        for (int[] ints : bishop) {
            int dx = ints[0];
            int dy = ints[1];
            recursBarrier(playerColor, PIECE.BISHOP, x+dx, y+dy, dx, dy, pieces, barrierPieces, 0, 0, false);
        }
        return barrierPieces;

    }
    public void recursBarrier(COLOR playerColor, PIECE target, int x, int y, int dx, int dy, ChessPieces[][] pieces, boolean[][] barrierPieces,
                              int xBarrier, int yBarrier, boolean found) {
        // Recursive call Base case
        if (x < 0 || x >= pieces.length || y < 0 || y >= pieces.length) {
            return;
        }
        if (pieces[y][x] != null) {
            if (pieces[y][x].getType() == playerColor) {
                if (!found) {
                    found = true;
                    xBarrier = x;
                    yBarrier = y;
                } else {
                    return;
                }
            } else {
                if (found) {
                    if (target == PIECE.ROOK) {
                        if (pieces[y][x].getPiece() == PIECE.ROOK || pieces[y][x].getPiece() == PIECE.QUEEN) {
                            if((pieces[yBarrier][xBarrier].getPiece() == PIECE.ROOK || pieces[yBarrier][xBarrier].getPiece() == PIECE.QUEEN)){
                                if(!pieces[yBarrier][xBarrier].IsBarrier()){
                                    pieces[yBarrier][xBarrier].setBarrier(true);
                                    pieces[yBarrier][xBarrier].setCoords(x,y);
                                }else{
                                    barrierPieces[yBarrier][xBarrier] = true;
                                }
                            }else{
                                barrierPieces[yBarrier][xBarrier] = true;
                            }
                            return;
                        }
                    } else if (target == PIECE.BISHOP) {
                        if (pieces[y][x].getPiece() == PIECE.BISHOP || pieces[y][x].getPiece() == PIECE.QUEEN) {
                            if(pieces[yBarrier][xBarrier].getPiece() == PIECE.PAWN){
                                if(Math.abs(y-yBarrier) <= 1 && !pieces[yBarrier][xBarrier].IsBarrier()){
                                    pieces[yBarrier][xBarrier].setBarrier(true);
                                    pieces[yBarrier][xBarrier].setCoords(x,y);
                                }else{
                                    barrierPieces[yBarrier][xBarrier] = true;
                                }
                            } else if ((pieces[yBarrier][xBarrier].getPiece() == PIECE.BISHOP || pieces[yBarrier][xBarrier].getPiece() == PIECE.QUEEN)) {
                                if(!pieces[yBarrier][xBarrier].IsBarrier()){
                                    pieces[yBarrier][xBarrier].setBarrier(true);
                                    pieces[yBarrier][xBarrier].setCoords(x,y);
                                }else{
                                    barrierPieces[yBarrier][xBarrier] = true;
                                }
                            }else{
                                barrierPieces[yBarrier][xBarrier] = true;
                            }
                            return;
                        }
                    }
                }
            }
        }
        recursBarrier(playerColor, target, x + dx, y + dy, dx, dy, pieces, barrierPieces, xBarrier, yBarrier, found);
    }
    public boolean[][] getKingBarrierPiece(COLOR playerColor, ChessPieces[][] pieces){
        // first find where the king piece is at
        for(int i =0;i<pieces.length;i++){
            for(int x=0;x<pieces[i].length;x++){
                if(pieces[i][x] != null){
                    if(pieces[i][x].getPiece() == PIECE.KING && pieces[i][x].getType() == playerColor){
                        return checkBarrier(playerColor, x, i, pieces);
                    }
                }
            }
        }
        return new boolean[8][8];
    }
    public boolean[][] getLegalMoves(COLOR playerColor, ChessPieces[][] pieces, boolean kingValidate){

        boolean[][] moves = new boolean[8][8];
        boolean[][] barrierPieces = getKingBarrierPiece(playerColor, pieces); // finds all barrier pieces, so that we can avoid them
        // After finding the blocked pieces, now we can start finding all legal moves
        for(int i =0;i<pieces.length;i++){
            for(int x=0;x<pieces[i].length;x++){
                if(pieces[i][x] != null){
                    if(pieces[i][x].getType() == playerColor){
                        if(pieces[i][x].getPiece() == PIECE.PAWN){
                            if(!pieces[i][x].IsBarrier() || kingValidate){
                                if(!barrierPieces[i][x]){
                                    pawnMoves(playerColor, pieces, x, i, moves, kingValidate, null);
                                }
                                pieces[i][x].setBarrier(false);
                            }else{
                                int[] c = pieces[i][x].getCoords();
                                moves[c[1]][c[0]] = true;
                                pieces[i][x].setBarrier(false);
                            }
                        } else if (pieces[i][x].getPiece() == PIECE.KNIGHT && !barrierPieces[i][x]) {
                            knightMoves(playerColor, pieces, x, i, moves, kingValidate, null);
                        } else if (pieces[i][x].getPiece() == PIECE.BISHOP) {
                            if(!pieces[i][x].IsBarrier() || kingValidate ){
                                if( !barrierPieces[i][x] ){
                                    for (int[] ints : bishop) {
                                        int dx = ints[0];
                                        int dy = ints[1];
                                        recurMoves(playerColor,pieces,x + dx, i + dy, dx, dy, moves, kingValidate, null);
                                    }
                                }
                                pieces[i][x].setBarrier(false);
                            }else{
                                int[] c = pieces[i][x].getCoords();
                                moves[c[1]][c[0]] = true;
                                pieces[i][x].setBarrier(false);
                            }
                        }else if (pieces[i][x].getPiece() == PIECE.ROOK) {
                            if(!pieces[i][x].IsBarrier() || kingValidate ){
                                if(!barrierPieces[i][x]){
                                    for (int[] ints : rook) {
                                        int dx = ints[0];
                                        int dy = ints[1];
                                        recurMoves(playerColor,pieces,x + dx, i + dy, dx, dy, moves, kingValidate, null);
                                    }
                                }
                                pieces[i][x].setBarrier(false);
                            }else{
                                int[] c = pieces[i][x].getCoords();
                                moves[c[1]][c[0]] = true;
                                pieces[i][x].setBarrier(false);
                            }
                        }else if (pieces[i][x].getPiece() == PIECE.QUEEN ) {
                            if(!pieces[i][x].IsBarrier() || kingValidate ) {
                                if(!barrierPieces[i][x]){
                                    // For queen moves, since it moves like a rook and bishop combined
                                    for (int[] ints : bishop) {
                                        int dx = ints[0];
                                        int dy = ints[1];
                                        recurMoves(playerColor,pieces,x + dx, i + dy, dx, dy, moves, kingValidate, null);
                                    }
                                    for (int[] ints : rook) {
                                        int dx = ints[0];
                                        int dy = ints[1];
                                        recurMoves(playerColor,pieces,x + dx, i + dy, dx, dy, moves, kingValidate, null);
                                    }
                                    pieces[i][x].setBarrier(false);
                                }
                            }
                            else{
                                int[] c = pieces[i][x].getCoords();
                                moves[c[1]][c[0]] = true;
                                pieces[i][x].setBarrier(false);
                            }
                        } else if (pieces[i][x].getPiece() == PIECE.KING) {
                            kingMoves(playerColor, pieces, x, i, moves, kingValidate);
                        }
                    }
                }
            }
        }
        return moves;
    }
    public boolean[][] kingCheckMoves(COLOR opponentColor, boolean[][] avoidPath, ChessPieces[][] pieces){
        boolean[][] moves = new boolean[8][8];
        for(int i =0;i<avoidPath.length;i++){
            for(int x=0;x<avoidPath[i].length;x++){
                if(pieces[i][x] != null && pieces[i][x].getPiece() == PIECE.KING){
                    if(pieces[i][x].getType() == opponentColor){
                        for (int[] ints : king) {
                            int dx = ints[0];
                            int dy = ints[1];
                            if ((i + dy < pieces.length) && (i + dy >= 0) && (x + dx < pieces.length) && (x + dx >= 0)) {
                                if(pieces[i+dy][x+dx] == null){
                                    if(!avoidPath[i+dy][x+dx]){
                                        moves[i+dy][x+dx] = true;
                                    }
                                } else if (pieces[i + dy][x + dx].getType() != opponentColor) {
                                    if(!avoidPath[i + dy][x + dx]){
                                        moves[i+dy][x+dx] = true;
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
        return moves;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean[][] IsChecked(COLOR kingColor, int x, int y, ChessPieces[][] pieces){

        this.checkCounts = 0;
        boolean[][] checkedMoves = new boolean[8][8];
        if(pieces[y][x].getPiece() == PIECE.PAWN){
            // First do a search as a pawn
            pawnCheck(pieces[y][x].getType(), kingColor, x, y, checkedMoves, pieces);
        } else if (pieces[y][x].getPiece() == PIECE.KNIGHT) {
            // Knight Search
            knightCheck(kingColor, x, y, checkedMoves, pieces);
        } else if (pieces[y][x].getPiece() == PIECE.BISHOP) {
            // bishop search
            for (int[] ints : bishop) {
                int dx = ints[0];
                int dy = ints[1];
                if(recurCheck(kingColor, x+dx, y+dy, dx, dy, checkedMoves, pieces)){
                    checkedMoves[y][x] = true;
                }
            }
        } else if (pieces[y][x].getPiece() == PIECE.ROOK) {
            for (int[] ints : rook) {
                int dx = ints[0];
                int dy = ints[1];
                if(recurCheck(kingColor,  x+dx, y+dy, dx, dy, checkedMoves, pieces)){
                    checkedMoves[y][x] = true;
                }
            }
        } else if (pieces[y][x].getPiece() == PIECE.QUEEN) {
            for (int[] ints : bishop) {
                int dx = ints[0];
                int dy = ints[1];
                if(recurCheck(kingColor, x+dx, y+dy, dx, dy, checkedMoves, pieces)){
                    checkedMoves[y][x] = true;
                }
            }
            for (int[] ints : rook) {
                int dx = ints[0];
                int dy = ints[1];
                if(recurCheck(kingColor,  x+dx, y+dy, dx, dy, checkedMoves, pieces)){
                    checkedMoves[y][x] = true;
                }
            }
        }
        if(checkCounts == 0){
            return  null;
        }
        return checkedMoves;
    }
    private boolean recurCheck(COLOR kingColor, int x, int y, int dx, int dy, boolean[][] checkedMoves, ChessPieces[][] pieces) {
        // Recursive call Base case
        if (x < 0 || x >= pieces.length || y < 0 || y >= pieces.length) {
            return false;
        }
        if(pieces[y][x] != null){
            if(pieces[y][x].getType() == kingColor){
                if(pieces[y][x].getPiece() == PIECE.KING){
                    checkCounts += 1;
                    return true;
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }
        if(recurCheck(kingColor,x+dx,y+dy, dx, dy, checkedMoves, pieces)){
            checkedMoves[y][x] = true;
            return true;
        }
        return false;
    }
    private void knightCheck(COLOR color, int x, int y, boolean[][] checkedMoves, ChessPieces[][] pieces) {
        for (int[] ints : knight) {
            int dy = ints[1];
            int dx = ints[0];
            if ((y + dy < checkedMoves.length) && (y + dy >= 0) && (x + dx < checkedMoves.length) && (x + dx >= 0)) {
                if (pieces[y + dy][x + dx] != null && pieces[y+dy][x+dx].getPiece() == PIECE.KING) {
                    if(pieces[y+dy][x+dx].getType() == color) {
                        checkCounts += 1;
                        checkedMoves[y][x] = true;
                    }
                }
            }
            if((y + dx < checkedMoves.length) && (y + dx >= 0) && (x + dy < checkedMoves.length) && (x + dy >= 0)) {
                if (pieces[y + dx][x + dy] != null && pieces[y + dx][x + dy].getPiece() == PIECE.KING) {
                    if(pieces[y + dx][x + dy].getType() == color){
                        checkCounts += 1;
                        checkedMoves[y][x] = true;
                    }
                }
            }
        }
    }
    private void pawnCheck(COLOR playerColor, COLOR kingcolor, int x, int y, boolean[][] checkedMoves, ChessPieces[][] pieces) {
        if(playerColor == COLOR.BLACK) {
            if((y+1 < checkedMoves.length)) {
                if((x-1 >= 0) && (pieces[y+1][x-1] != null) && (pieces[y+1][x-1].getType() == kingcolor)) {
                    if(pieces[y+1][x-1].getPiece() == PIECE.KING){
                        checkCounts+=1;
                        checkedMoves[y][x] = true;
                    }
                }
                if((x+1 < checkedMoves.length) && (pieces[y+1][x+1] != null) && (pieces[y+1][x+1].getType() == kingcolor)) {
                    if(pieces[y+1][x+1].getPiece() == PIECE.KING){
                        checkCounts+=1;
                        checkedMoves[y][x] = true;
                    }
                }
            }
        }else{
            if ((y - 1 >= 0)) {
                if ((x - 1 >= 0) && (pieces[y - 1][x - 1] != null) && pieces[y-1][x-1].getType() == kingcolor) {
                    if(pieces[y-1][x-1].getPiece() == PIECE.KING){
                        checkCounts+=1;
                        checkedMoves[y][x] = true;
                    }
                }
                if ((x + 1 < checkedMoves.length) && (pieces[y - 1][x + 1] != null) && pieces[y-1][x+1].getType() == kingcolor) {
                    if(pieces[y-1][x+1].getPiece() == PIECE.KING){
                        checkCounts+=1;
                        checkedMoves[y][x] = true;
                    }
                }
            }
        }
    }
    public boolean[][] getMoves(ChessPieces clickedPiece, int x, int y, ChessPieces[][] pieces, boolean[][] allLegalCheckPlayer){

        boolean[][] moves = new boolean[8][8]; // all the possible moves will be marked in this array
        boolean[][] barrierPieces = getKingBarrierPiece(clickedPiece.getType(), pieces);

        /*
         Like we did before, we need to check if this piece picked is not a barrier
         */
        if(clickedPiece.getPiece() == PIECE.PAWN) {
            if(!pieces[y][x].IsBarrier()){
                if(!barrierPieces[y][x]){
                    pawnMoves(clickedPiece.getType(), pieces, x, y, moves, false, allLegalCheckPlayer);
                }
            }else{
                int[] c = pieces[y][x].getCoords();
                moves[c[1]][c[0]] = true;
                pieces[y][x].setBarrier(false);
            }
        }else if (clickedPiece.getPiece() == PIECE.KNIGHT && !barrierPieces[y][x]) {
            knightMoves(clickedPiece.getType(),pieces, x, y, moves, false, allLegalCheckPlayer);
        }else if (clickedPiece.getPiece() == PIECE.BISHOP) {
            if(!pieces[y][x].IsBarrier()){
                if(!barrierPieces[y][x]){
                    for (int[] ints : bishop) {
                        int dx = ints[0];
                        int dy = ints[1];
                        recurMoves(clickedPiece.getType(),pieces,x + dx, y + dy, dx, dy, moves, false, allLegalCheckPlayer);
                    }
                }
            }else{
                int[] c = pieces[y][x].getCoords();
                moves[c[1]][c[0]] = true;
                pieces[y][x].setBarrier(false);
            }

        }else if (clickedPiece.getPiece() == PIECE.ROOK) {
            if(!pieces[y][x].IsBarrier()){
                if(!barrierPieces[y][x]){
                    for (int[] ints : rook) {
                        int dx = ints[0];
                        int dy = ints[1];
                        recurMoves(clickedPiece.getType(),pieces,x + dx, y + dy, dx, dy, moves ,false, allLegalCheckPlayer);
                    }
                }
            }else{
                int[] c = pieces[y][x].getCoords();
                moves[c[1]][c[0]] = true;
                pieces[y][x].setBarrier(false);
            }
        }else if (clickedPiece.getPiece() == PIECE.QUEEN) {
            if(!pieces[y][x].IsBarrier()){
                if(!barrierPieces[y][x]){
                    // For queen moves, since it moves like a rook and bishop combined
                    for (int[] ints : bishop) {
                        int dx = ints[0];
                        int dy = ints[1];
                        recurMoves(clickedPiece.getType(),pieces,x + dx, y + dy, dx, dy, moves,  false, allLegalCheckPlayer);
                    }
                    for (int[] ints : rook) {
                        int dx = ints[0];
                        int dy = ints[1];
                        recurMoves(clickedPiece.getType(),pieces,x + dx, y + dy, dx, dy, moves,  false, allLegalCheckPlayer);
                    }
                }
            }else{
                int[] c = pieces[y][x].getCoords();
                moves[c[1]][c[0]] = true;
                pieces[y][x].setBarrier(false);
            }
        } else if (clickedPiece.getPiece() == PIECE.KING) {
            boolean[][] opponentPath = getLegalMoves((clickedPiece.getType() == COLOR.WHITE) ? COLOR.BLACK:COLOR.WHITE , pieces, true);
            moves = kingCheckMoves(clickedPiece.getType(), opponentPath, pieces);
            if(!clickedPiece.getMoved()){
                castling(x+1, y, 1, 0,  moves, pieces);
                castling(x-1, y, -1, 0,  moves, pieces);
            }
        }
        return moves;

    }
    private void pawnMoves(COLOR playerColor, ChessPieces[][] pieces, int x, int y, boolean[][] moves, boolean kingValidate, boolean[][] allLegalCheckPlayer) {
        if(playerColor == COLOR.BLACK){
            if(y == 1 && (pieces[y+2][x] == null) && pieces[y+1][x] == null) {
                if(allLegalCheckPlayer != null){
                    if(allLegalCheckPlayer[y+2][x]){
                        moves[y+2][x] = true;
                    }
                }else{
                    if(!kingValidate){
                        moves[y+2][x] = true;
                    }
                }
            }
            if(y+1 < pieces.length){
                if(pieces[y+1][x] == null && !kingValidate) {
                    if(allLegalCheckPlayer != null){
                        if(allLegalCheckPlayer[y+1][x]){
                            moves[y+1][x] = true;
                        }
                    }else{
                        moves[y+1][x] = true;
                    }
                }
                // this is showing that this pawn can protect it's pieces from the king
                if(kingValidate){
                    if((x-1 >= 0) && (pieces[y+1][x-1] != null) && (pieces[y+1][x-1].getType() == playerColor)){
                        moves[y+1][x-1] = true;
                    }
                    if((x+1 < moves.length) && (pieces[y+1][x+1] != null) && (pieces[y+1][x+1].getType() == playerColor)) {
                        moves[y+1][x+1] = true;
                    }
                    // this is to ensure that the king doesn't pass through where a pawn can take it
                    if((x-1 >= 0) && (pieces[y+1][x-1] == null)){
                        moves[y+1][x-1] = true;
                    }
                    if((x+1 < moves.length) && (pieces[y+1][x+1] == null)) {
                        moves[y+1][x+1] = true;
                    }
                }
                if(allLegalCheckPlayer == null){
                    if((x-1 >= 0) && (pieces[y+1][x-1] != null) && (pieces[y+1][x-1].getType() != playerColor)) {
                        moves[y+1][x-1] = true;
                    }
                    if((x+1 < moves.length) && (pieces[y+1][x+1] != null) && (pieces[y+1][x+1].getType() != playerColor)) {
                        moves[y+1][x+1] = true;
                    }
                }
            }
        } else {
            // First checks if 2 move is possible starting pawn of WHITE
            if(y == 6 && (pieces[y-2][x] == null) && pieces[y-1][x] == null) {
                if(allLegalCheckPlayer != null){
                    if(allLegalCheckPlayer[y-2][x]){
                        moves[y-2][x] = true;
                    }
                }else{
                    if(!kingValidate){
                        moves[y-2][x] = true;
                    }
                }
            }
            if((y-1 >= 0)) {
                if(pieces[y-1][x] == null && !kingValidate) {
                    if(allLegalCheckPlayer != null){
                        if(allLegalCheckPlayer[y-1][x]){
                            moves[y-1][x] = true;
                        }
                    }else{
                        moves[y-1][x] = true;
                    }
                }
                // this is showing that this pawn can protect it's pieces from the king
                if(kingValidate){
                    if((x-1 >= 0) && (pieces[y-1][x-1] != null) && (pieces[y-1][x-1].getType() == playerColor)) {
                        moves[y-1][x-1] = true;
                    }
                    if((x+1 < moves.length) && (pieces[y-1][x+1] != null) && (pieces[y-1][x+1].getType() == playerColor)) {
                        moves[y-1][x+1] = true;
                    }
                    // this is to ensure that the king doesn't pass through where a pawn can take it
                    if((x-1 >= 0) && (pieces[y-1][x-1] == null)) {
                        moves[y-1][x-1] = true;
                    }
                    if((x+1 < moves.length) && (pieces[y-1][x+1] == null)) {
                        moves[y-1][x+1] = true;
                    }
                }

                if(allLegalCheckPlayer == null){
                    if((x-1 >= 0) && (pieces[y-1][x-1] != null) && (pieces[y-1][x-1].getType() != playerColor)) {
                        moves[y-1][x-1] = true;
                    }
                    if((x+1 < moves.length) && (pieces[y-1][x+1] != null) && (pieces[y-1][x+1].getType() != playerColor)) {
                        moves[y-1][x+1] = true;
                    }
                }

            }
        }
    }
    private void knightMoves(COLOR color, ChessPieces[][] pieces, int x, int y, boolean[][] moves, boolean kingValidate, boolean[][] allLegalCheckPlayer) {
        for (int[] ints : knight) {
            int dy = ints[1];
            int dx = ints[0];
            if ((y + dy < pieces.length) && (y + dy >= 0) && (x + dx < pieces.length) && (x + dx >= 0)) {
                if (pieces[y + dy][x + dx] == null) {
                    if(allLegalCheckPlayer != null){
                        if(allLegalCheckPlayer[y + dy][x + dx]){
                            moves[y + dy][x + dx] = true;
                        }
                    }else{
                        moves[y + dy][x + dx] = true;
                    }
                } else if (pieces[y + dy][x + dx].getType() != color) {
                    if(allLegalCheckPlayer != null){
                        if(allLegalCheckPlayer[y + dy][x + dx]){
                            moves[y + dy][x + dx] = true;
                        }
                    }else{
                        moves[y+dy][x+dx] = true;
                    }
                }
                if(kingValidate){
                    if(pieces[y+dy][x+dx] != null && pieces[y + dy][x + dx].getType() == color){
                        moves[y + dy][x + dx] = true;
                    }
                }
            }
            if ((y + dx < moves.length) && (y + dx >= 0) && (x + dy < moves.length) && (x + dy >= 0)) {
                if (pieces[y + dx][x + dy] == null) {
                    if(allLegalCheckPlayer != null){
                        if(allLegalCheckPlayer[y + dx][x + dy]){
                            moves[y + dx][x + dy] = true;
                        }
                    }else{
                        moves[y + dx][x + dy] = true;
                    }
                } else if (pieces[y + dx][x + dy].getType() != color) {
                    if(allLegalCheckPlayer != null){
                        if(allLegalCheckPlayer[y + dx][x + dy]){
                            moves[y + dx][x + dy] = true;
                        }
                    }else{
                        moves[y + dx][x + dy] = true;
                    }
                }
                if(kingValidate){
                    if(pieces[y + dx][x + dy] != null && pieces[y + dx][x + dy].getType() == color){
                        moves[y + dx][x + dy] = true;
                    }
                }
            }
        }
    }
    // Recursive moves can be used for bishops, queen, rooks
    private void recurMoves(COLOR color, ChessPieces[][] pieces, int x, int y, int dx, int dy, boolean[][] moves, boolean kingValidate, boolean[][] allLegalCheckPlayer) {
        // Recursive call Base case
        if (x < 0 || x >= pieces.length || y < 0 || y >= pieces.length) {
            return;
        }
        // Checks if the space is empty to occupy or not
        if(pieces[y][x] == null){
            if(allLegalCheckPlayer != null){
                if(allLegalCheckPlayer[y][x]){
                    moves[y][x] = true;
                }
            }else{
                moves[y][x] = true;
            }
        }else{
            if(pieces[y][x].getType() != color){
                if(allLegalCheckPlayer != null){
                    if(allLegalCheckPlayer[y][x]){
                        moves[y][x] = true;
                    }
                }else{
                    moves[y][x] = true;
                }
                if(kingValidate){
                    if(pieces[y][x].getPiece() != PIECE.KING){
                        return;
                    }
                }else {
                    return;
                }
            }else{
                if(kingValidate){
                    moves[y][x] = true;
                }
                return;
            }
        }
        recurMoves(color,pieces,x+dx,y+dy, dx, dy, moves,  kingValidate, allLegalCheckPlayer);

    }
    private void castling(int x, int y, int dx, int dy, boolean[][] moves, ChessPieces[][] pieces) {
        // Recursive call Base case
        if (x < 0 || x >= pieces.length || y < 0 || y >= pieces.length) {
            return;
        }
        if((pieces[y][x] != null) && (pieces[y][x].getPiece() != PIECE.ROOK)) {
            return;
        }
        if((pieces[y][x] != null) && (pieces[y][x].getPiece() == PIECE.ROOK && !pieces[y][x].getMoved())) {
            moves[y][x] = true;
            return;
        }
        castling(x + dx, y + dy, dx, dy, moves, pieces);

    }
    private boolean[][] kingMoves(COLOR color,ChessPieces[][] pieces, int x, int y, boolean[][] moves, boolean kingValidate){
        for (int[] ints : king) {
            int dx = ints[0];
            int dy = ints[1];
            if ((y + dy < pieces.length) && (y + dy >= 0) && (x + dx < pieces.length) && (x + dx >= 0)) {
                if (pieces[y + dy][x + dx] == null) {
                    moves[y+dy][x+dx] = true;
                } else if (pieces[y + dy][x + dx].getType() != color) {
                    moves[y + dy][x + dx] = true;
                }
                if(kingValidate){
                    if(pieces[y + dy][x + dx] != null && pieces[y + dy][x + dx].getType() == color){
                        moves[y + dy][x + dx] = true;
                    }
                }
            }
        }
        return moves;
    }

}
