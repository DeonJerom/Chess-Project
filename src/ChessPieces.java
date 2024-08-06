public class ChessPieces {
    private final PIECE piece;
    private final COLOR type;
    private boolean IsMoved;
    private int xChecked = -1;
    private int yChecked = -1;
    private boolean checkedPiece = false;
    public void setCoords(int xChecked, int yChecked){
        this.xChecked = xChecked;
        this.yChecked = yChecked;
    }
    public int[] getCoords(){
        return new int[]{xChecked, yChecked};
    }
    public boolean IsBarrier() {
        return checkedPiece;
    }
    public void setBarrier(boolean moved) {
        this.checkedPiece = moved;
    }
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
