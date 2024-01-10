import java.awt.geom.AffineTransform;

public class BombEntity extends Entity {
   private double moveSpeed = -1500.0D;
   private Game game;

   public BombEntity(Game g, String r, int newX, int newY) {
      super(r, newX, newY);
      this.game = g;
      double difX = this.game.getShipCordsX() + 64.0D - this.game.getMouseCordsX();
      double difY = this.game.getShipCordsY() + 64.0D - this.game.getMouseCordsY();
      double hypot = Math.sqrt(difX * difX + difY * difY);
      this.dx = difX / (hypot / this.moveSpeed);
      this.dy = difY / (hypot / this.moveSpeed);
   }

   public void move(long delta) {
      this.affine = AffineTransform.getTranslateInstance(this.x, this.y);
      super.move(delta);
      if (this.y < -100.0D) {
         this.game.removeEntity(this);
      }

   }

   public void collidedWith(Entity other) {
      if (other instanceof AlienEntity) {
         this.game.removeEntity(other);
         this.game.notifyAlienKilled();
      }

      if (other instanceof FastEntity) {
         this.game.removeEntity(other);
         this.game.notifyAlienKilled();
      }

   }
}
