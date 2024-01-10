import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

public abstract class Entity {
   protected double x;
   protected double y;
   protected Sprite sprite;
   protected double dx;
   protected double dy;
   protected AffineTransform affine;
   private Rectangle me = new Rectangle();
   private Rectangle him = new Rectangle();

   public Entity(String r, int newX, int newY) {
      this.x = (double)newX;
      this.y = (double)newY;
      this.sprite = SpriteStore.get().getSprite(r);
   }

   public void move(long delta) {
      this.x += (double)delta * this.dx / 1000.0D;
      this.y += (double)delta * this.dy / 1000.0D;
   }

   public void setHorizontalMovement(double newDX) {
      this.dx = newDX;
   }

   public void setVerticalMovement(double newDY) {
      this.dy = newDY;
   }

   public double getHorizontalMovement() {
      return this.dx;
   }

   public double getVerticalMovement() {
      return this.dy;
   }

   public int getX() {
      return (int)this.x;
   }

   public int getY() {
      return (int)this.y;
   }

   public void draw(Graphics g) {
      Graphics2D g2d = (Graphics2D)g;
      this.sprite.draw(g, (int)this.x, (int)this.y);
   }

   public void draw(Graphics2D g) {
      this.sprite.draw(g, this.affine);
   }

   public void doLogic() {
   }

   public boolean collidesWith(Entity other) {
      this.me.setBounds((int)this.x, (int)this.y, this.sprite.getWidth(), this.sprite.getHeight());
      this.him.setBounds(other.getX(), other.getY(), other.sprite.getWidth(), other.sprite.getHeight());
      return this.me.intersects(this.him);
   }

   public abstract void collidedWith(Entity var1);
}
