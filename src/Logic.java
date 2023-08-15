import javax.xml.stream.FactoryConfigurationError;
import java.util.HashMap;
import java.util.Map;

public class Logic {
    private final int[][] knight = {{-1,-2},{1,-2}, {2,-1}, {2,1},};
    private final int[][] bishop = {{-1,-1}, {1,-1}, {-1,1}, {1,1},};
    private final int[][] rook = {{0,-1}, {1,0}, {0,1}, {-1,0},};
    private final int[][] king = {{0,-1}, {1,-1}, {1,0}, {1,1}, {0,1}, {-1,1}, {-1,0}, {-1,-1}};
    private int checkCounts = 0;
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
    public void recursBarrier(COLOR playerColor, PIECE target, int x, int y, int dx, int dy, ChessPieces[][] pieces, boolean[][] barrierPieces, int xBarrier, int yBarrier, boolean found){
        // Recursive call Base case
        if (x < 0 || x >= pieces.length || y < 0 || y >= pieces.length) {
            return;
        }
        if(pieces[y][x] != null){
            if(pieces[y][x].getType() == playerColor){
                if(!found){
                    found = true;
                    xBarrier = x;
                    yBarrier = y;
                }else{
                    return;
                }
            }else{
                if(found){
                    if(target == PIECE.ROOK){
                        if(pieces[y][x].getPiece() == PIECE.ROOK || pieces[y][x].getPiece() == PIECE.QUEEN){
                            barrierPieces[yBarrier][xBarrier] = true;
                            return;
                        }
                    } else if (target == PIECE.BISHOP) {
                        if(pieces[y][x].getPiece() == PIECE.BISHOP || pieces[y][x].getPiece() == PIECE.QUEEN){
                            barrierPieces[yBarrier][xBarrier] = true;
                            return;
                        }
                    }
                }
            }
        }
        recursBarrier(playerColor, target, x+dx, y+dy, dx, dy, pieces, barrierPieces, xBarrier, yBarrier, found);

    }
    public boolean[][] getLegalMoves(COLOR playerColor, ChessPieces[][] pieces, boolean kingValidate){
        boolean[][] moves = new boolean[8][8];
        boolean[][] barrierPieces = new boolean[8][8];
        // first find where the king piece is at
        for(int i =0;i<pieces.length;i++){
            for(int x=0;x<pieces[i].length;x++){
                if(pieces[i][x] != null){
                    if(pieces[i][x].getPiece() == PIECE.KING && pieces[i][x].getType() == playerColor){
                        barrierPieces = checkBarrier(playerColor, x, i, pieces); // all blocking check pieces are found
                        break;
                    }
                }
            }
        }

        // After finding the blocked pieces, now we can start finding all legal moves
        for(int i =0;i<pieces.length;i++){
            for(int x=0;x<pieces[i].length;x++){
                if(pieces[i][x] != null){
                    if(pieces[i][x].getType() == playerColor){
                        if(pieces[i][x].getPiece() == PIECE.PAWN && !barrierPieces[i][x]){
                            pawnMoves(playerColor, pieces, x, i, moves, kingValidate);
                        } else if (pieces[i][x].getPiece() == PIECE.KNIGHT && !barrierPieces[i][x]) {
                            knightMoves(playerColor, pieces, x, i, moves, kingValidate);
                        } else if (pieces[i][x].getPiece() == PIECE.BISHOP && !barrierPieces[i][x] ) {
                            for (int[] ints : bishop) {
                                int dx = ints[0];
                                int dy = ints[1];
                                recurMoves(playerColor,pieces,x + dx, i + dy, dx, dy, moves, kingValidate);
                            }
                        }else if (pieces[i][x].getPiece() == PIECE.ROOK && !barrierPieces[i][x]) {
                            for (int[] ints : rook) {
                                int dx = ints[0];
                                int dy = ints[1];
                                recurMoves(playerColor,pieces,x + dx, i + dy, dx, dy, moves, kingValidate);
                            }
                        }else if (pieces[i][x].getPiece() == PIECE.QUEEN && !barrierPieces[i][x]) {
                            // For queen moves, since it moves like a rook and bishop combined
                            for (int[] ints : bishop) {
                                int dx = ints[0];
                                int dy = ints[1];
                                recurMoves(playerColor,pieces,x + dx, i + dy, dx, dy, moves, kingValidate);
                            }
                            for (int[] ints : rook) {
                                int dx = ints[0];
                                int dy = ints[1];
                                recurMoves(playerColor,pieces,x + dx, i + dy, dx, dy, moves, kingValidate);
                            }
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
    public HashMap<Integer, boolean[][]> IsChecked(COLOR kingColor, int x, int y, ChessPieces[][] pieces){
        HashMap<Integer, boolean[][]> checkOrNot = new HashMap<>();
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

        checkOrNot.put(checkCounts, checkedMoves);
        return checkOrNot;
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
    private void pawnCheck(COLOR playerColor, COLOR kingcolor, int x, int y, boolean[][] checkedMoves, ChessPieces[][] pieces){
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
    public boolean[][] getMoves(ChessPieces clickedPiece, int x, int y, ChessPieces[][] pieces){
        boolean[][] moves = new boolean[8][8]; // all the possible moves will be marked in this array
        if(clickedPiece.getPiece() == PIECE.PAWN) {
            pawnMoves(clickedPiece.getType(), pieces, x, y, moves, false);
        }else if (clickedPiece.getPiece() == PIECE.KNIGHT) {
            knightMoves(clickedPiece.getType(),pieces, x, y, moves, false);
        }else if (clickedPiece.getPiece() == PIECE.BISHOP) {
            for (int[] ints : bishop) {
                int dx = ints[0];
                int dy = ints[1];
                recurMoves(clickedPiece.getType(),pieces,x + dx, y + dy, dx, dy, moves, false);
            }
        }else if (clickedPiece.getPiece() == PIECE.ROOK) {
            for (int[] ints : rook) {
                int dx = ints[0];
                int dy = ints[1];
                recurMoves(clickedPiece.getType(),pieces,x + dx, y + dy, dx, dy, moves ,false);
            }
        }else if (clickedPiece.getPiece() == PIECE.QUEEN) {
            // For queen moves, since it moves like a rook and bishop combined
            for (int[] ints : bishop) {
                int dx = ints[0];
                int dy = ints[1];
                recurMoves(clickedPiece.getType(),pieces,x + dx, y + dy, dx, dy, moves,  false);
            }
            for (int[] ints : rook) {
                int dx = ints[0];
                int dy = ints[1];
                recurMoves(clickedPiece.getType(),pieces,x + dx, y + dy, dx, dy, moves,  false);
            }
        }else if (clickedPiece.getPiece() == PIECE.KING) {
            if(!clickedPiece.getMoved()) {
                castling(x+1, y, 1, 0,  moves, pieces);
                castling(x-1, y, -1, 0,  moves, pieces);
            }
            return kingMoves(clickedPiece.getType(),pieces, x, y, moves, false);
        }
        return moves;
    }

    private void pawnMoves(COLOR playerColor, ChessPieces[][] pieces, int x, int y, boolean[][] moves, boolean kingValidate) {
        if(playerColor == COLOR.BLACK){
            if(y == 1 && (pieces[y+2][x] == null) && pieces[y+1][x] == null) {
                moves[y+2][x] = true;
            }
            if(y+1 < pieces.length){

                if(pieces[y+1][x] == null) {
                    moves[y+1][x] = true;
                }

                // this is showing that this pawn can protect it's pieces from the king
                if(kingValidate){
                    if((x-1 >= 0) && (pieces[y+1][x-1] != null) && (pieces[y+1][x-1].getType() == playerColor)){
                        moves[y+1][x-1] = true;
                    }
                    if((x+1 < moves.length) && (pieces[y+1][x+1] != null) && (pieces[y+1][x+1].getType() == playerColor)) {
                        moves[y+1][x+1] = true;
                    }
                }
                if((x-1 >= 0) && (pieces[y+1][x-1] != null) && (pieces[y+1][x-1].getType() != playerColor)) {
                    moves[y+1][x-1] = true;
                }
                if((x+1 < moves.length) && (pieces[y+1][x+1] != null) && (pieces[y+1][x+1].getType() != playerColor)) {
                    moves[y+1][x+1] = true;
                }

            }
        } else {
            // First checks if 2 move is possible starting pawn of WHITE
            if(y == 6 && (pieces[y-2][x] == null) && pieces[y-1][x] == null) {
                moves[y-2][x] = true;
            }
            if((y-1 >= 0)) {
                if(pieces[y-1][x] == null) {
                    moves[y-1][x] = true;
                }

                // this is showing that this pawn can protect it's pieces from the king
                if(kingValidate){
                    if((x-1 >= 0) && (pieces[y-1][x-1] != null) && (pieces[y-1][x-1].getType() == playerColor)) {
                        moves[y-1][x-1] = true;
                    }
                    if((x+1 < moves.length) && (pieces[y-1][x+1] != null) && (pieces[y-1][x+1].getType() == playerColor)) {
                        moves[y-1][x+1] = true;
                    }
                }
                if((x-1 >= 0) && (pieces[y-1][x-1] != null) && (pieces[y-1][x-1].getType() != playerColor)) {
                    moves[y-1][x-1] = true;
                }
                if((x+1 < moves.length) && (pieces[y-1][x+1] != null) && (pieces[y-1][x+1].getType() != playerColor)) {
                    moves[y-1][x+1] = true;
                }
            }
        }
    }
    private void knightMoves(COLOR color, ChessPieces[][] pieces,  int x, int y, boolean[][] moves, boolean kingValidate) {
        for (int[] ints : knight) {
            int dy = ints[1];
            int dx = ints[0];
            if ((y + dy < pieces.length) && (y + dy >= 0) && (x + dx < pieces.length) && (x + dx >= 0)) {
                if (pieces[y + dy][x + dx] == null) {
                    moves[y + dy][x + dx] = true;
                } else if (pieces[y + dy][x + dx].getType() != color) {
                    moves[y + dy][x + dx] = true;
                }
                if(kingValidate){
                    if(pieces[y+dy][x+dx] != null && pieces[y + dy][x + dx].getType() == color){
                        moves[y + dy][x + dx] = true;
                    }
                }
            }
            if ((y + dx < moves.length) && (y + dx >= 0) && (x + dy < moves.length) && (x + dy >= 0)) {
                if (pieces[y + dx][x + dy] == null) {
                    moves[y + dx][x + dy] = true;
                } else if (pieces[y + dx][x + dy].getType() != color) {
                    moves[y + dx][x + dy] = true;
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
    private void recurMoves(COLOR color, ChessPieces[][] pieces, int x, int y, int dx, int dy, boolean[][] moves, boolean kingValidate) {
        // Recursive call Base case
        if (x < 0 || x >= pieces.length || y < 0 || y >= pieces.length) {
            return;
        }
        // Checks if the space is empty to occupy or not
        if(pieces[y][x] == null){
            moves[y][x] = true;
        }else{
            if(pieces[y][x].getType() != color){
                moves[y][x] = true;
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
        recurMoves(color,pieces,x+dx,y+dy, dx, dy, moves,  kingValidate);

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
