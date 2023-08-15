import javax.swing.ImageIcon;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;
public class PIECES_IMG {
    private static final Map<String, Image> imageCache = new HashMap<>();
    private Image loadImage(String filename) {
        if (imageCache.containsKey(filename)) {
            return imageCache.get(filename);
        } else {
            Image image = new ImageIcon(filename).getImage();
            imageCache.put(filename, image);
            return image;
        }
    }
    Image dot = loadImage("Solid_green.png");
    Image white = loadImage("WHITE_COLUMN.jpeg");
    Image black = loadImage("BLACK_COLUMN.jpeg");
    Image black_pawn = loadImage("black-pawn.png");
    Image black_bishop = loadImage("black-bishop.png");
    Image black_knight = loadImage("black-knight.png");
    Image black_rook = loadImage("black-rook.png");
    Image black_king = loadImage("black-king.png");
    Image black_queen = loadImage("black-queen.png");

    Image white_pawn = loadImage("white-pawn.png");
    Image white_bishop = loadImage("white-bishop.png");
    Image white_knight = loadImage("white-knight.png");
    Image white_rook = loadImage("white-rook.png");
    Image white_king = loadImage("white-king.png");
    Image white_queen = loadImage("white-queen.png");


}
