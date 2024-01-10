import java.awt.geom.AffineTransform;

public class HealthEntity extends Entity {
   private Game game;

   public HealthEntity(Game g, String r, int newX, int newY) {
      super(r, newX, newY);
      this.game = g;
      this.affine = AffineTransform.getTranslateInstance(this.x, this.y);
   }

   public void collidedWith(Entity other) {
      if (other instanceof ShipEntity) {
         this.game.removeEntity(this);
         this.game.addHealth();
      }

   }
}
