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
    Image black_pawn = loadImage("bp.png");
    Image black_bishop = loadImage("bb.png");
    Image black_knight = loadImage("bn.png");
    Image black_rook = loadImage("br.png");
    Image black_king = loadImage("bk.png");
    Image black_queen = loadImage("bq.png");

    Image white_pawn = loadImage("wp.png");
    Image white_bishop = loadImage("wb.png");
    Image white_knight = loadImage("wn.png");
    Image white_rook = loadImage("wr.png");
    Image white_king = loadImage("wk.png");
    Image white_queen = loadImage("wq.png");


}
