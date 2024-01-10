import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class Music {
   public static void playSound(String name) {
      try {
         AudioStream audioStream = null;
         audioStream = new AudioStream(Music.class.getResourceAsStream("sprites/" + name));
         AudioPlayer.player.start(audioStream);
         Thread.sleep(10L);
      } catch (Exception var2) {
         System.out.println(var2.getMessage());
      }

   }
}
