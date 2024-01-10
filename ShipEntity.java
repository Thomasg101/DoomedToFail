import java.awt.geom.AffineTransform;

public class ShipEntity extends Entity {
   private Game game;

   public ShipEntity(Game g, String r, int newX, int newY) {
      super(r, newX, newY);
      this.game = g;
   }

   public void move(long delta) {
      if (!(this.dx < 0.0D) || !(this.x < -20.0D)) {
         if (!(this.dx > 0.0D) || !(this.x > 1850.0D)) {
            if (!(this.dy > 0.0D) || !(this.y > 850.0D)) {
               if (!(this.dy < 0.0D) || !(this.y < -20.0D)) {
                  double difX = this.x + 64.0D - this.game.getMouseCordsX();
                  double difY = this.y + 64.0D - this.game.getMouseCordsY();
                  double angle = Math.atan2(difY, difX);
                  this.affine = AffineTransform.getTranslateInstance(this.x, this.y);
                  this.affine.rotate(angle, 64.0D, 64.0D);
                  super.move(delta);
               }
            }
         }
      }
   }

   public void collidedWith(Entity other) {
      if (other instanceof AlienEntity) {
         this.game.notifyHurt();
      }

      if (other instanceof FastEntity) {
         this.game.notifyHurt();
      }

      if (this.game.getShipHealth() == 0) {
         this.game.notifyDeath();
      }

   }
}
