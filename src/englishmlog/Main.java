package englishmlog;

import arc.Core;
import arc.Events;
import arc.func.Cons;
import arc.util.Log;
import mindustry.game.EventType;
import mindustry.mod.Mod;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
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
            replaceBundle();
        }
        
        private synchronized void initLines() {
            lines = Core.files.internal("bundles/bundle.properties")
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
            Events.on(EventType.ClientLoadEvent.class, new Cons<>() {
                @Override
                public void get(EventType.ClientLoadEvent clientLoadEvent) {
                    synchronized (BundleChanger.this) {
                        while (isLocked) {
                            try {
                                wait();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    
                    var properties = Core.bundle.getProperties();
                    lines.forEach(line -> {
                        String[] keyValue = line.split(" = ");
                        properties.put(keyValue[0], keyValue[1]);
                    });
                    
                    Core.app.post(() -> Events.remove(EventType.ClientLoadEvent.class, this));
            }
        });
        }
    }

//    @Override
//    public void init() {
//        Vars.ui.logic.addListener(new VisibilityListener() {
//            @Override
//            public boolean shown() {
//                new BundleChanger();
//                Core.app.post(() -> Vars.ui.logic.removeListener(this));
//                return false;
//            }
//        });
//    }
//
//    private static class BundleChanger {
//        private final ObjectMap<String, String> properties = Core.bundle.getProperties();
//        private final ObjectMap<String, String> localProperties = new ObjectMap<>();
//        private final ObjectMap<Label, String> labels = new ObjectMap<>();
//
//        private BundleChanger() {
//            extractText(Vars.ui.logic);
//            replaceProperties();
//            replaceLabelsText();
//        }
//
//        private void extractText(Group group) {
//            for (var element : group.getChildren()) {
//                if (element instanceof Group groupInstance) {
//                    Log.info("group: " + groupInstance);
//                    extractText(groupInstance);
//                } else if (element instanceof Label label) {
//                    String text = label.getText().toString();
//                    Log.info("find key '" + text + '\'');
//                    String key = properties.findKey(text, false);
//                    Log.info("label + '" + key + "': '" + text + "'");
//                    if (key != null) {
//                        labels.put(label, key);
//                        localProperties.put(key, text);
//                    }
//                }
//            }
//        }
//
//        private void replaceProperties() {
//            Core.files.internal("bundles/bundle.properties")
//                .readString()
//                .lines()
//                .forEach(this::replaceFromLine);
//        }
//
//        private void replaceFromLine(String line) {
//            String[] keyAndValue = line.split(" = ");
//            // if it has key
//            if (localProperties.remove(keyAndValue[0]) != null) {
//                // replace
//                properties.put(keyAndValue[0], keyAndValue[1]);
//            }
//        }
//
//        private void replaceLabelsText() {
//            labels.each((label, key) ->
//                label.setText(properties.get(key))
//            );
//        }
//    }
}
