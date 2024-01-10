import java.awt.geom.AffineTransform;

public class ShotEntity extends Entity {
   private double moveSpeed = -500.0D;
   private boolean used = false;
   private double angle = 0.0D;
   private Game game;

   public ShotEntity(Game g, String r, int newX, int newY) {
      super(r, newX, newY);
      this.game = g;
      double difX = this.game.getShipCordsX() + 64.0D - this.game.getMouseCordsX();
      double difY = this.game.getShipCordsY() + 64.0D - this.game.getMouseCordsY();
      double hypot = Math.sqrt(difX * difX + difY * difY);
      this.affine = AffineTransform.getTranslateInstance(this.x, this.y);
      this.angle = Math.atan2(difY, difX) * 180.0D / 3.141592653589793D;
      this.dx = difX / (hypot / this.moveSpeed);
      this.dy = difY / (hypot / this.moveSpeed);
   }

   public void move(long delta) {
      this.affine = AffineTransform.getTranslateInstance(this.x, this.y);
      this.affine.rotate(Math.toRadians(this.angle) + 3.14159D, 20.0D, 20.0D);
      super.move(delta);
      if (this.y < -100.0D) {
         this.game.removeEntity(this);
      }

   }

   public void collidedWith(Entity other) {
      if (!this.used) {
         if (other instanceof FastEntity) {
            this.game.removeEntity(this);
            this.game.removeEntity(other);
            this.game.notifyAlienKilled();
            this.used = true;
         }

      }
   }
}
