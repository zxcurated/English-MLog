package englishmlog;

import arc.Core;
import arc.Events;
import arc.func.Cons;
import mindustry.game.EventType;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.mod.Mod;
import java.util.stream.Stream;

public class Main extends Mod {
    
    static {
        new BundleChanger().start();
    }
    
    private static class BundleChanger extends Thread {
        private boolean isLocked = true;
        private Stream<String> lines;
        
        @Override
        public void run() {
            initLines();
            Events.on(ClientLoadEvent.class, new Cons<>() {
                @Override
                public void get(ClientLoadEvent clientLoadEvent) {
                    replaceBundle();
                    Core.app.post(() -> Events.remove(ClientLoadEvent.class, this));
                }
            });
        }
        
        private synchronized void initLines() {
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
                        line.startsWith("sensor"));
            
            isLocked = false;
            this.notify();
        }
        
        private synchronized void replaceBundle() {
            while (isLocked) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            
            var properties = Core.bundle.getProperties();
            lines.forEach(line -> {
                String[] keyValue = line.split(" = ");
                properties.put(keyValue[0], keyValue[1]);
            });
            
            Core.app.post(() -> Events.remove(EventType.ClientLoadEvent.class, this));
        }
    }
}
