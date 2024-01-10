import java.awt.geom.AffineTransform;

public class BossEntity extends Entity {
   private double moveSpeed = 100.0D;
   private boolean used = false;
   private Game game;
   public int health = 40;

   public BossEntity(Game g, String r, int newX, int newY) {
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
      this.affine.rotate(Math.toRadians(angle) + 1.5707963267948966D, 150.0D, 100.0D);
      super.move(delta);
   }

   public void doLogic(long delta) {
   }

   public void collidedWith(Entity other) {
      if (!this.used) {
         if (other instanceof ShotEntity) {
            this.game.removeEntity(other);
            --this.health;
            this.game.damageBoss();
            if (this.health <= 0) {
               this.game.removeEntity(this);
               this.game.notifyBossKilled();
               this.used = true;
            }
         }

         if (other instanceof BombEntity) {
            this.game.removeEntity(other);
            --this.health;
            this.game.damageBoss();
            if (this.health <= 0) {
               this.game.removeEntity(this);
               this.game.notifyBossKilled();
               this.used = true;
            }
         }

      }
   }
}
