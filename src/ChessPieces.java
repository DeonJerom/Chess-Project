public class ChessPieces {
    private final PIECE piece;
    private final COLOR type;
    private boolean IsMoved;
    public ChessPieces(PIECE piece, COLOR type) {
        this.piece = piece;
        this.type = type;
        this.IsMoved = false;
    }
    public PIECE getPiece() {
        return piece;
    }
    public COLOR getType() {
        return type;
    }

    public boolean getMoved() {
        return IsMoved;
    }
    public void setIsMoved(boolean isMoved) {
        this.IsMoved = isMoved;
    }

}

enum PIECE {
    ROOK,
    BISHOP,
    KNIGHT,
    PAWN,
    KING,
    QUEEN
}
enum COLOR {
    BLACK,
    WHITE
}
