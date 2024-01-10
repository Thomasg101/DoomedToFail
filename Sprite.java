import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;

public class Sprite {
   public Image image;

   public Sprite(Image i) {
      this.image = i;
   }

   public int getWidth() {
      return this.image.getWidth((ImageObserver)null);
   }

   public int getHeight() {
      return this.image.getHeight((ImageObserver)null);
   }

   public void draw(Graphics g, int x, int y) {
      g.drawImage(this.image, x, y, (ImageObserver)null);
   }

   public void draw(Graphics2D g, AffineTransform affine) {
      g.drawImage(this.image, affine, (ImageObserver)null);
   }
}
