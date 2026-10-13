package englishmlog;

import arc.Core;
import arc.Events;
import arc.func.Cons;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.mod.Mod;
import java.util.List;

public class Main extends Mod {
    
    static {
        new BundleChanger().start();
    }
    
    private static class BundleChanger extends Thread {
        private List<String> lines;
        
        @Override
        public void run() {
            readEnglishLines();
            Events.on(ClientLoadEvent.class, e ->
                replaceBundle()
            );
        }
        
        private synchronized void readEnglishLines() {
            lines =
                Core.files.internal("bundles/bundle.properties")
                    .readString()
                    .lines()
                    .filter(line ->
                        line.startsWith("instruction") ||
                        line.startsWith("name.token") ||
                        line.startsWith("laccess.label") ||
                        line.startsWith("graphicstype.label") ||
                        line.startsWith("fetchtype.label") ||
                        line.startsWith("cutsceneaction.label") ||
                        line.startsWith("contenttype.label") ||
                        line.startsWith("blockflag.label") ||
                        line.startsWith("lenum") ||
                        line.startsWith("lunitcontrol") ||
                        line.startsWith("llocate") ||
                        line.startsWith("playsound") ||
                        line.startsWith("unitlocate") ||
                        line.startsWith("control") ||
                        line.startsWith("unitradar") ||
                        line.startsWith("radar") ||
                        line.startsWith("sensor"))
                    .toList();
        }
        
        private synchronized void replaceBundle() {
            var properties = Core.bundle.getProperties();
            lines.forEach(line -> {
                String[] keyValue = line.split(" = ");
                properties.put(keyValue[0], keyValue[1]);
            });
        }
    }
}
