import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Game extends Canvas {
   private int healthTimer = 1;
   private long damageTime = 1000L;
   private long lastHurt = 0L;
   private int shipHealth;
   private int upgradePoints = 0;
   private BufferStrategy strategy;
   private boolean waitingForKeyPress = true;
   private final int GAME_WIDTH = 2000;
   private final int GAME_HEIGHT = 1000;
   private boolean ifWin = false;
   private boolean ifLoss = false;
   private long lastUpgrade = 0L;
   private boolean spawned = false;
   private int speedUpgradeCount;
   private int firingUpgradeCount;
   private boolean leftPressed = false;
   private boolean rightPressed = false;
   private boolean upPressed = false;
   private boolean downPressed = false;
   private boolean mPressed = false;
   private boolean bPressed = false;
   private boolean addLifeKey = false;
   private boolean addBombKey = false;
   private boolean speedUpgradeKey = false;
   private boolean gameRunning = true;
   private ArrayList entities = new ArrayList();
   private ArrayList removeEntities = new ArrayList();
   private Entity ship;
   private Entity alien;
   private Entity bossEntity;
   private double moveSpeed = 600.0D;
   private long lastFire = 0L;
   private long firingInterval = 500L;
   private long upgradeInterval = 500L;
   private boolean shotUpgradeKey;
   private int alienCount;
   private String message = "";
   private long lastSpawn;
   private long lastBombSpawn;
   private long lastHealthSpawn;
   private int dropInterval = 4000;
   private int dropHealthInterval = 2000;
   private int spawnInterval = 8000;
   private int numOfBomb = 5;
   private int score;
   private int bossHealth = 40;
   private boolean logicRequiredThisLoop = false;
   private static Image heart = null;
   private static Image bomb = null;
   private static Image startImg;
   private static Image victory;
   private static Image loss;
   private static Image img = null;

   public Game() {
      JFrame container = new JFrame("Doomed to Fail");
      JPanel panel = (JPanel)container.getContentPane();
      panel.setPreferredSize(new Dimension(2000, 1000));
      panel.setLayout((LayoutManager)null);
      this.setBounds(0, 0, 2000, 1000);
      panel.add(this);
      this.setIgnoreRepaint(true);
      container.pack();
      container.setResizable(false);
      container.setVisible(true);
      container.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {
            System.exit(0);
         }
      });
      this.addKeyListener(new Game.KeyInputHandler((Game.KeyInputHandler)null));
      this.addMouseListener(new Game.ClickListner((Game.ClickListner)null));
      this.requestFocus();
      this.createBufferStrategy(2);
      this.strategy = this.getBufferStrategy();
      this.initEntities();
      Music.playSound("music_01.wav");
      this.gameLoop();
   }

   private void initEntities() {
      Graphics2D g = (Graphics2D)this.strategy.getDrawGraphics();
      this.ship = new ShipEntity(this, "sprites/ship.gif", 1000, 500);
      this.entities.add(this.ship);

      int i;
      for(i = 0; i < 7; ++i) {
         AlienEntity alien;
         if (i % 2 == 0) {
            alien = new AlienEntity(this, "sprites/alien.gif", (int)(Math.random() * 2000.0D), (int)(Math.random() * -1000.0D));
         } else {
            alien = new AlienEntity(this, "sprites/alien.gif", (int)(Math.random() * 2000.0D), (int)(Math.random() * 1000.0D + 1000.0D));
         }

         this.entities.add(alien);
         ++this.alienCount;
      }

      for(i = 0; i < 4; ++i) {
         FastEntity fastAlien;
         if (i % 2 == 0) {
            fastAlien = new FastEntity(this, "sprites/fastalien.png", (int)(Math.random() * 2000.0D), (int)(Math.random() * -1000.0D));
         } else {
            fastAlien = new FastEntity(this, "sprites/fastalien.png", (int)(Math.random() * 2000.0D), (int)(Math.random() * 1000.0D + 1000.0D));
         }

         this.entities.add(fastAlien);
         ++this.alienCount;
      }

   }

   public void tryToAddAlien() {
      Graphics2D g = (Graphics2D)this.strategy.getDrawGraphics();
      if (System.currentTimeMillis() - this.lastSpawn >= (long)this.spawnInterval && !this.waitingForKeyPress) {
         int i;
         for(i = 0; i < 7; ++i) {
            AlienEntity alien;
            if (i % 2 == 0) {
               alien = new AlienEntity(this, "sprites/alien.gif", (int)(Math.random() * 2000.0D), (int)(Math.random() * -1000.0D));
            } else {
               alien = new AlienEntity(this, "sprites/alien.gif", (int)(Math.random() * 2000.0D), (int)(Math.random() * 1000.0D + 1000.0D));
            }

            this.entities.add(alien);
            ++this.alienCount;
         }

         for(i = 0; i < 4; ++i) {
            FastEntity fastAlien;
            if (i % 2 == 0) {
               fastAlien = new FastEntity(this, "sprites/fastalien.png", (int)(Math.random() * 2000.0D), (int)(Math.random() * -1000.0D));
            } else {
               fastAlien = new FastEntity(this, "sprites/fastalien.png", (int)(Math.random() * 2000.0D), (int)(Math.random() * 1000.0D + 1000.0D));
            }

            this.entities.add(fastAlien);
            ++this.alienCount;
         }

         if (this.score > 50 && !this.spawned) {
            this.bossEntity = new BossEntity(this, "sprites/meldrum.png", (int)(Math.random() * 2000.0D), (int)(Math.random() * -1000.0D));
            this.spawned = true;
            this.entities.add(this.bossEntity);
         }

         this.lastSpawn = System.currentTimeMillis();
      }
   }

   public void updateLogic() {
      this.logicRequiredThisLoop = true;
   }

   public void removeShip(Entity entity) {
      if (entity.equals(this.ship) && this.getShipHealth() <= 0) {
         this.removeEntities.add(entity);
      }

   }

   public void removeEntity(Entity entity) {
      this.removeEntities.add(entity);
   }

   public void notifyDeath() {
      this.message = "You have been defeated by Mr. Meldrum. You scored: " + this.score + " points";
      this.ifLoss = true;
      this.waitingForKeyPress = true;
   }

   public void notifyBossKilled() {
      this.notifyWin();
   }

   public void notifyHurt() {
      if (System.currentTimeMillis() - this.lastHurt > this.damageTime || this.healthTimer % 10000 == 0) {
         this.setShipHealth(this.getShipHealth() - 1);
         Music.playSound("Male Hurt - Sound Effect.wav");
      }

      ++this.healthTimer;
      this.lastHurt = System.currentTimeMillis();
   }

   public void notifyAlienKilled() {
      --this.alienCount;
      ++this.score;
      if (this.score % 5 == 0) {
         ++this.upgradePoints;
      }

   }

   public void notifyWin() {
      this.message = "You have defeated by Mr. Meldrum. You scored: " + this.score;
      this.ifWin = true;
      this.waitingForKeyPress = true;
   }

   public void tryToFire() {
      if (System.currentTimeMillis() - this.lastFire >= this.firingInterval) {
         Music.playSound("1911-.45-ACP-Close-Single-Gunshot-C-www.fesliyanstudios.com.wav");
         this.lastFire = System.currentTimeMillis();
         ShotEntity shot = new ShotEntity(this, "sprites/shot.gif", this.ship.getX() + 45, this.ship.getY() + 50);
         this.entities.add(shot);
      }
   }

   public void tryToUpgradeBomb() {
      if (System.currentTimeMillis() - this.lastUpgrade >= this.upgradeInterval) {
         this.lastUpgrade = System.currentTimeMillis();
         --this.upgradePoints;
         this.addBombKey();
      }
   }

   public void tryToUpgradeSpeed() {
      if (System.currentTimeMillis() - this.lastUpgrade >= this.upgradeInterval) {
         Music.playSound("Skyrim Skill Increase Sound Effect.wav");
         this.lastUpgrade = System.currentTimeMillis();
         --this.upgradePoints;
         ++this.speedUpgradeCount;
         this.moveSpeed += 20.0D;
      }
   }

   public void tryToUpgradeFiringInterval() {
      if (System.currentTimeMillis() - this.lastUpgrade >= this.upgradeInterval) {
         Music.playSound("Skyrim Skill Increase Sound Effect.wav");
         this.lastUpgrade = System.currentTimeMillis();
         --this.upgradePoints;
         ++this.firingUpgradeCount;
         this.firingInterval -= 30L;
      }
   }

   public void tryToAddLife() {
      if (System.currentTimeMillis() - this.lastUpgrade >= this.upgradeInterval) {
         this.lastUpgrade = System.currentTimeMillis();
         --this.upgradePoints;
         this.addHealth();
      }
   }

   public void tryToAddNewDrop() {
      if (System.currentTimeMillis() - this.lastBombSpawn >= (long)this.dropInterval && !this.waitingForKeyPress) {
         Entity drop = new DropEntity(this, "sprites/drop.png", (int)(Math.random() * 2000.0D), (int)(Math.random() * 900.0D));
         this.entities.add(drop);
         this.lastBombSpawn = System.currentTimeMillis();
      }
   }

   public void tryToAddNewHealth() {
      if (System.currentTimeMillis() - this.lastHealthSpawn >= (long)this.dropHealthInterval && !this.waitingForKeyPress) {
         Entity health = new HealthEntity(this, "sprites/healthDrop.png", (int)(Math.random() * 2000.0D), (int)(Math.random() * 900.0D));
         this.entities.add(health);
         this.lastHealthSpawn = System.currentTimeMillis();
      }
   }

   public void gameLoop() {
      long lastLoopTime = System.currentTimeMillis();

      while(this.gameRunning) {
         this.tryToAddAlien();
         long delta = System.currentTimeMillis() - lastLoopTime;
         lastLoopTime = System.currentTimeMillis();
         Graphics2D g = (Graphics2D)this.strategy.getDrawGraphics();
         g.drawImage(img, 0, 0, 2000, 1000, (ImageObserver)null);
         g.setFont(new Font("TimesRoman", 0, 50));
         g.setColor(Color.ORANGE);
         g.drawString("Upgrade points: " + Integer.toString(this.upgradePoints), 1500, 50);
         g.drawString("Score Count: " + Integer.toString(this.score), 10, 50);
         g.setFont(new Font("TimesRoman", 0, 25));
         g.drawString("Speed Upgrade Level: " + Integer.toString(this.speedUpgradeCount), 1600, 80);
         g.drawString("Firing Upgrade Level: " + Integer.toString(this.firingUpgradeCount), 1600, 110);
         if (this.spawned) {
            g.setFont(new Font("TimesRoman", 0, 50));
            g.setColor(Color.ORANGE);
            g.drawString("Boss Health: " + Integer.toString(this.bossHealth), 750, 80);
         }

         int i;
         for(i = 0; i < this.getShipHealth(); ++i) {
            g.drawImage(heart, 20 + 50 * i, 950, 50, 45, (ImageObserver)null);
         }

         for(i = 0; i < this.numOfBomb; ++i) {
            g.drawImage(bomb, 1850 - 50 * i, 950, 50, 45, (ImageObserver)null);
         }

         Entity entity;
         if (!this.waitingForKeyPress) {
            for(i = 0; i < this.entities.size(); ++i) {
               entity = (Entity)this.entities.get(i);
               entity.move(delta);
            }
         }

         for(i = 0; i < this.entities.size(); ++i) {
            entity = (Entity)this.entities.get(i);
            entity.draw(g);
         }

         if (this.waitingForKeyPress) {
            g.drawImage(startImg, 0, 0, 2000, 1000, (ImageObserver)null);
         }

         if (this.waitingForKeyPress && this.ifWin) {
            g.drawImage(victory, 0, 0, 2000, 1000, (ImageObserver)null);
         }

         if (this.waitingForKeyPress && this.ifLoss) {
            g.drawImage(loss, 0, 0, 2000, 1000, (ImageObserver)null);
         }

         for(i = 0; i < this.entities.size(); ++i) {
            for(int j = i + 1; j < this.entities.size(); ++j) {
               Entity me = (Entity)this.entities.get(i);
               Entity him = (Entity)this.entities.get(j);
               if (me.collidesWith(him)) {
                  me.collidedWith(him);
                  him.collidedWith(me);
               }
            }
         }

         this.entities.removeAll(this.removeEntities);
         this.removeEntities.clear();
         if (this.logicRequiredThisLoop) {
            for(i = 0; i < this.entities.size(); ++i) {
               entity = (Entity)this.entities.get(i);
               entity.doLogic();
            }

            this.logicRequiredThisLoop = false;
         }

         if (this.waitingForKeyPress) {
            g.setColor(Color.CYAN);
            g.setFont(new Font("TimesRoman", 0, 50));
            g.drawString(this.message, (2000 - g.getFontMetrics().stringWidth(this.message)) / 2, 750);
            g.drawString("Press any key to begin", (2000 - g.getFontMetrics().stringWidth("Press any key to begin")) / 2, 800);
         }

         g.dispose();
         this.strategy.show();
         this.ship.setHorizontalMovement(0.0D);
         this.ship.setVerticalMovement(0.0D);
         if (this.leftPressed && !this.rightPressed) {
            this.ship.setHorizontalMovement(-this.moveSpeed);
         }

         if (this.rightPressed && !this.leftPressed) {
            this.ship.setHorizontalMovement(this.moveSpeed);
         }

         if (this.upPressed) {
            this.ship.setVerticalMovement(-this.moveSpeed);
         }

         if (this.downPressed) {
            this.ship.setVerticalMovement(this.moveSpeed);
         }

         if (this.speedUpgradeKey && this.upgradePoints > 0 && this.speedUpgradeCount < 10) {
            this.tryToUpgradeSpeed();
         }

         if (this.shotUpgradeKey && this.upgradePoints > 0 && this.firingUpgradeCount < 10) {
            this.tryToUpgradeFiringInterval();
         }

         if (this.addLifeKey && this.upgradePoints > 0) {
            this.tryToAddLife();
         }

         if (this.addBombKey && this.upgradePoints > 0) {
            this.tryToUpgradeBomb();
         }

         if (this.mPressed) {
            this.tryToFire();
            this.tryToAddNewDrop();
         }

         if (this.bPressed) {
            this.tryToBomb();
            this.tryToAddNewHealth();
         }
      }

   }

   public void tryToBomb() {
      if (System.currentTimeMillis() - this.lastFire >= this.firingInterval && this.numOfBomb != 0) {
         Music.playSound("12-Gauge-Pump-Action-Shotgun-Close-Gunshot-A-www.fesliyanstudios.com.wav");
         this.lastFire = System.currentTimeMillis();
         BombEntity bomb = new BombEntity(this, "sprites/bomb.gif", this.ship.getX() + 80, this.ship.getY() + 25);
         this.entities.add(bomb);
         --this.numOfBomb;
      }
   }

   public void addBombKey() {
      Music.playSound("Gun Reload sound effect.wav");
      ++this.numOfBomb;
   }

   public void addHealth() {
      Music.playSound("Super Mario Bros. - Mushroom Sound Effect_01.wav");
      ++this.shipHealth;
   }

   private void startGame() {
      this.entities.clear();
      this.initEntities();
      this.leftPressed = false;
      this.rightPressed = false;
      this.mPressed = false;
      this.upPressed = false;
      this.downPressed = false;
   }

   public double getMouseCordsX() {
      return MouseInfo.getPointerInfo().getLocation().getX();
   }

   public double getMouseCordsY() {
      return MouseInfo.getPointerInfo().getLocation().getY();
   }

   public double getShipCordsX() {
      return (double)this.ship.getX();
   }

   public double getShipCordsY() {
      return (double)this.ship.getY();
   }

   public double getAlienCordsX() {
      return (double)this.alien.getX();
   }

   public double getAlienCordsY() {
      return (double)this.alien.getY();
   }

   public int getShipHealth() {
      return this.shipHealth;
   }

   public void setShipHealth(int shipHealth) {
      this.shipHealth = shipHealth;
   }

   public void damageBoss() {
      --this.bossHealth;
   }

   public static void main(String[] args) {
      img = Toolkit.getDefaultToolkit().getImage(Game.class.getResource("sprites/bg.png"));
      heart = Toolkit.getDefaultToolkit().getImage(Game.class.getResource("sprites/heart.gif"));
      bomb = Toolkit.getDefaultToolkit().getImage(Game.class.getResource("sprites/bomb.gif"));
      startImg = Toolkit.getDefaultToolkit().getImage(Game.class.getResource("sprites/startImg.png"));
      victory = Toolkit.getDefaultToolkit().getImage(Game.class.getResource("sprites/victory.png"));
      loss = Toolkit.getDefaultToolkit().getImage(Game.class.getResource("sprites/loss.png"));
      new Game();
   }

   private class ClickListner extends MouseAdapter {
      private ClickListner() {
      }

      public void mousePressed(MouseEvent e) {
         if (!Game.this.waitingForKeyPress) {
            if (e.getButton() == 1) {
               Game.this.mPressed = true;
            }

            if (e.getButton() == 3) {
               Game.this.bPressed = true;
            }

         }
      }

      public void mouseReleased(MouseEvent e) {
         if (!Game.this.waitingForKeyPress) {
            if (e.getButton() == 1) {
               Game.this.mPressed = false;
            }

            if (e.getButton() == 3) {
               Game.this.bPressed = false;
            }

         }
      }

      // $FF: synthetic method
      ClickListner(Game.ClickListner var2) {
         this();
      }
   }

   private class KeyInputHandler extends KeyAdapter {
      private int pressCount;

      private KeyInputHandler() {
         this.pressCount = 1;
      }

      public void keyPressed(KeyEvent e) {
         if (!Game.this.waitingForKeyPress) {
            if (e.getKeyCode() == 65) {
               Game.this.leftPressed = true;
            }

            if (e.getKeyCode() == 68) {
               Game.this.rightPressed = true;
            }

            if (e.getKeyCode() == 87) {
               Game.this.upPressed = true;
            }

            if (e.getKeyCode() == 83) {
               Game.this.downPressed = true;
            }

            if (e.getKeyCode() == 49) {
               Game.this.speedUpgradeKey = true;
            }

            if (e.getKeyCode() == 50) {
               Game.this.shotUpgradeKey = true;
            }

            if (e.getKeyCode() == 51) {
               Game.this.addLifeKey = true;
            }

            if (e.getKeyCode() == 52) {
               Game.this.addBombKey = true;
            }

         }
      }

      public void keyReleased(KeyEvent e) {
         if (!Game.this.waitingForKeyPress) {
            if (e.getKeyCode() == 65) {
               Game.this.leftPressed = false;
            }

            if (e.getKeyCode() == 68) {
               Game.this.rightPressed = false;
            }

            if (e.getKeyCode() == 83) {
               Game.this.downPressed = false;
            }

            if (e.getKeyCode() == 87) {
               Game.this.upPressed = false;
            }

            if (e.getKeyCode() == 49) {
               Game.this.speedUpgradeKey = false;
            }

            if (e.getKeyCode() == 50) {
               Game.this.shotUpgradeKey = false;
            }

            if (e.getKeyCode() == 51) {
               Game.this.addLifeKey = false;
            }

            if (e.getKeyCode() == 52) {
               Game.this.addBombKey = false;
            }

         }
      }

      public void keyTyped(KeyEvent e) {
         if (Game.this.waitingForKeyPress) {
            if (this.pressCount == 1) {
               Game.this.ifWin = false;
               Game.this.ifLoss = false;
               Game.this.waitingForKeyPress = false;
               Game.this.score = 0;
               Game.this.firingInterval = 500L;
               Game.this.numOfBomb = 5;
               Game.this.upgradePoints = 0;
               Game.this.moveSpeed = 600.0D;
               Game.this.setShipHealth(3);
               Game.this.startGame();
               Game.this.speedUpgradeCount = 0;
               Game.this.firingUpgradeCount = 0;
               this.pressCount = 0;
               Game.this.spawned = false;
            } else {
               ++this.pressCount;
            }
         }

         if (e.getKeyChar() == 27) {
            System.exit(0);
         }

      }

      // $FF: synthetic method
      KeyInputHandler(Game.KeyInputHandler var2) {
         this();
      }
   }
}
