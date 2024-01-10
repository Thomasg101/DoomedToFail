import java.awt.geom.AffineTransform;

public class AlienEntity extends Entity {
   private double moveSpeed = 150.0D;
   private boolean used = false;
   private Game game;
   private int health = 2;

   public AlienEntity(Game g, String r, int newX, int newY) {
      super(r, newX, newY);
      this.game = g;
   }

   public void move(long delta) {
      double diffX = this.game.getShipCordsX() + 92.0D - this.x;
      double diffY = this.game.getShipCordsY() + 92.0D - this.y;
      double hypo = Math.sqrt(diffY * diffY + diffX * diffX);
      double ratio = hypo / this.moveSpeed;
      this.dx = diffX / ratio;
      this.dy = diffY / ratio;
      double angle = Math.atan2(diffY, diffX) * 180.0D / 3.141592653589793D;
      this.affine = AffineTransform.getTranslateInstance(this.x, this.y);
      this.affine.rotate(Math.toRadians(angle) + 1.5707963267948966D, 20.0D, 20.0D);
      super.move(delta);
   }

   public void doLogic(long delta) {
   }

   public void collidedWith(Entity other) {
      if (!this.used) {
         if (other instanceof ShotEntity) {
            this.game.removeEntity(other);
            --this.health;
            if (this.health == 0) {
               this.game.removeEntity(this);
               this.game.notifyAlienKilled();
               this.used = true;
            }
         }

      }
   }
}
