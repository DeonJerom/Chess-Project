public class AI {
    Logic logic = new Logic();

    // create a function to return all starting index of pieces that can be moved, barriers needs to be checked
    public boolean[][] getAllLegalPositions(COLOR playerColor, ChessPieces[][] pieces, boolean[][] allLegalCheckPlayer) {

        boolean[][] legalPositions = new boolean[8][8];
        // First, we need to check barriers or not, which we can do using the logic class function
        boolean[][] barrierPieces = logic.getKingBarrierPiece(playerColor, pieces);

        // now we need to iterate through the all object array and find all player pieces and search
        // After finding the blocked pieces, now we can start finding all legal moves
        for(int i =0;i<pieces.length;i++){
            for(int x=0;x<pieces[i].length;x++){
                if(pieces[i][x] != null){
                    if(pieces[i][x].getType() == playerColor){
                        PIECE piece = pieces[i][x].getPiece(); // gets the type of piece
                        if(piece == PIECE.PAWN){
                            if(!pieces[i][x].IsBarrier()){
                                if(!barrierPieces[i][x]){
                                    getPawnPositions(playerColor, x, i, pieces, legalPositions, allLegalCheckPlayer);
                                }
                                pieces[i][x].setBarrier(false);
                            }else{
                                legalPositions[i][x] = true;
                                pieces[i][x].setBarrier(false);
                            }
                        } else if (piece == PIECE.KNIGHT && !barrierPieces[i][x]) {
                            for (int[] ints : logic.knight) {
                                int dy = ints[1];
                                int dx = ints[0];
                                getKnightPositions(playerColor, x, i, pieces, legalPositions, dy, dx, allLegalCheckPlayer);
                                getKnightPositions(playerColor, x, i, pieces, legalPositions, dx, dy, allLegalCheckPlayer);
                            }
                        } else if (piece == PIECE.BISHOP) {
                            if(!pieces[i][x].IsBarrier()){
                                if(!barrierPieces[i][x]){
                                    for (int[] ints : logic.bishop) {
                                        int dx = ints[0];
                                        int dy = ints[1];
                                        getRecurPositions(playerColor,pieces,x + dx, i + dy, dx, dy, legalPositions, x, i, allLegalCheckPlayer);
                                    }
                                }
                                pieces[i][x].setBarrier(false);
                            }else{
                                legalPositions[i][x] = true;
                                pieces[i][x].setBarrier(false);
                            }
                        } else if (piece == PIECE.ROOK ) {
                            if(!pieces[i][x].IsBarrier()){
                                if( !barrierPieces[i][x]){
                                    for (int[] ints : logic.rook) {
                                        int dx = ints[0];
                                        int dy = ints[1];
                                        getRecurPositions(playerColor,pieces,x + dx, i + dy, dx, dy, legalPositions, x, i, allLegalCheckPlayer);
                                    }
                                }
                                pieces[i][x].setBarrier(false);
                            }else{
                                legalPositions[i][x] = true;
                                pieces[i][x].setBarrier(false);
                            }

                        }else if (piece == PIECE.QUEEN) {
                            if(!pieces[i][x].IsBarrier()){
                                if(!barrierPieces[i][x]){
                                    // For queen moves, since it moves like a rook and bishop combined
                                    for (int[] ints : logic.bishop) {
                                        int dx = ints[0];
                                        int dy = ints[1];
                                        getRecurPositions(playerColor,pieces,x + dx, i + dy, dx, dy, legalPositions, x, i, allLegalCheckPlayer);
                                    }
                                    for (int[] ints : logic.rook) {
                                        int dx = ints[0];
                                        int dy = ints[1];
                                        getRecurPositions(playerColor,pieces,x + dx, i + dy, dx, dy, legalPositions, x, i, allLegalCheckPlayer);
                                    }
                                }
                                pieces[i][x].setBarrier(false);
                            }else{
                                legalPositions[i][x] = true;
                                pieces[i][x].setBarrier(false);
                            }

                        } else if (piece == PIECE.KING) {
                            boolean[][] opponentPath = logic.getLegalMoves((playerColor == COLOR.WHITE) ? COLOR.BLACK : COLOR.WHITE, pieces, true);
                            getKingPositions(playerColor, x, i, opponentPath, pieces, legalPositions);
                            if(!pieces[i][x].getMoved()){
                                if(castling(x+1, i, 1, 0,  pieces)){
                                    legalPositions[i][x] = true;
                                }
                                if(castling(x-1, i, -1, 0, pieces)){
                                    legalPositions[i][x] = true;
                                }
                            }
                        }
                    }
                }

            }
        }
        return legalPositions;
    }
    private void getRecurPositions(COLOR playerColor, ChessPieces[][] pieces, int x, int y, int dx, int dy, boolean[][] legalPositions,
                                   int originalX, int originalY, boolean[][] allLegalCheckPlayer) {

        // Recursive call Base case
        if (x < 0 || x >= pieces.length || y < 0 || y >= pieces.length) {
            return;
        }

        if(pieces[y][x] != null){
            if(pieces[y][x].getType() != playerColor){
                if(allLegalCheckPlayer != null){
                    if(allLegalCheckPlayer[y][x]){
                        legalPositions[originalY][originalX] = true;
                    }
                }else{
                    legalPositions[originalY][originalX] = true;
                }
            }else{
                return;
            }
        }else{
            if(allLegalCheckPlayer != null){
                if(allLegalCheckPlayer[y][x]){
                    legalPositions[originalY][originalX] = true;
                    return;
                }
            }else{
                legalPositions[originalY][originalX] = true;
                return;
            }
        }
        // Recurse in the given direction
        getRecurPositions(playerColor, pieces, x + dx, y + dy, dx, dy, legalPositions, originalX, originalY, allLegalCheckPlayer);

    }

    private boolean castling(int x, int y, int dx, int dy, ChessPieces[][] pieces) {
        // Recursive call Base case
        if (x < 0 || x >= pieces.length || y < 0 || y >= pieces.length) {
            return false;
        }
        if((pieces[y][x] != null) && (pieces[y][x].getPiece() != PIECE.ROOK)) {
            return false;
        }
        if((pieces[y][x] != null) && (pieces[y][x].getPiece() == PIECE.ROOK && !pieces[y][x].getMoved())) {
            return true;
        }
        return castling(x + dx, y + dy, dx, dy, pieces);

    }
    private void getKingPositions(COLOR playerColor, int x, int y , boolean[][] avoidPath, ChessPieces[][] pieces, boolean[][] legalPositions){
        for (int[] ints : logic.king) {
            int dx = ints[0];
            int dy = ints[1];
            if ((y + dy < pieces.length) && (y + dy >= 0) && (x + dx < pieces.length) && (x + dx >= 0)) {
                if(pieces[y+dy][x+dx] == null){
                    if(!avoidPath[y+dy][x+dx]){
                        legalPositions[y][x] = true;
                        return;
                    }
                } else if (pieces[y + dy][x + dx].getType() != playerColor) {
                    if(!avoidPath[y + dy][x + dx]){
                        legalPositions[y][x] = true;
                        return;
                    }
                }
            }
        }
    }
    private void getPawnPositions(COLOR playerColor, int x, int y, ChessPieces[][] pieces, boolean[][] legalPositions, boolean[][] allLegalCheckPlayer){
        if(playerColor == COLOR.BLACK){
            if(y == 1 && (pieces[y+2][x] == null) && pieces[y+1][x] == null) {
                if(allLegalCheckPlayer != null){
                    if(allLegalCheckPlayer[y+2][x]){
                        legalPositions[y][x] = true;
                        return;
                    }
                }else{
                    legalPositions[y][x] = true;
                    return;
                }
            }
            if(y+1 < pieces.length){
                if(pieces[y+1][x] == null) {
                    if(allLegalCheckPlayer != null){
                        if(allLegalCheckPlayer[y+1][x]){
                            legalPositions[y][x] = true;
                            return;
                        }
                    }else{
                        legalPositions[y][x] = true;
                        return;
                    }
                }
                if(allLegalCheckPlayer == null){
                    if((x-1 >= 0) && (pieces[y+1][x-1] != null) && (pieces[y+1][x-1].getType() != playerColor)) {
                        legalPositions[y][x] = true;
                        return;
                    }
                    if((x+1 < pieces.length) && (pieces[y+1][x+1] != null) && (pieces[y+1][x+1].getType() != playerColor)) {
                        legalPositions[y][x] = true;
                    }
                }

            }
        } else {
            // First checks if 2 move is possible starting pawn of WHITE
            if(y == 6 && (pieces[y-2][x] == null) && pieces[y-1][x] == null) {
                if(allLegalCheckPlayer != null){
                    if(allLegalCheckPlayer[y-2][x]){
                        legalPositions[y][x] = true;
                        return;
                    }
                }else{
                    legalPositions[y][x] = true;
                    return;
                }
            }
            if((y-1 >= 0)) {
                if(pieces[y-1][x] == null) {
                    if(allLegalCheckPlayer != null){
                        if(allLegalCheckPlayer[y-1][x]){
                            legalPositions[y][x] = true;
                            return;
                        }
                    }else{
                        legalPositions[y][x] = true;
                        return;
                    }
                }
                if(allLegalCheckPlayer == null){
                    if((x-1 >= 0) && (pieces[y-1][x-1] != null) && (pieces[y-1][x-1].getType() != playerColor)) {
                        legalPositions[y][x] = true;
                        return;
                    }
                    if((x+1 < pieces.length) && (pieces[y-1][x+1] != null) && (pieces[y-1][x+1].getType() != playerColor)) {
                        legalPositions[y][x] = true;
                    }
                }
            }
        }
    }
    private void getKnightPositions(COLOR playerColor, int x, int y, ChessPieces[][] pieces, boolean[][] legalPositions, int dy, int dx, boolean[][] allLegalCheckPlayer) {
        if ((y + dy < pieces.length) && (y + dy >= 0) && (x + dx < pieces.length) && (x + dx >= 0)) {
            if (pieces[y + dy][x + dx] == null) {
                if(allLegalCheckPlayer != null){
                    if(allLegalCheckPlayer[y + dy][x + dx]){
                        legalPositions[y][x] = true;
                        return;
                    }
                }else{
                    legalPositions[y][x] = true;
                    return;
                }
            } else if (pieces[y + dy][x + dx].getType() != playerColor) {
                if(allLegalCheckPlayer != null){
                    if(allLegalCheckPlayer[y + dy][x + dx]){
                        legalPositions[y][x] = true;
                        return;
                    }
                }else{
                    legalPositions[y][x] = true;
                    return;
                }
            }
        }

    }
}
