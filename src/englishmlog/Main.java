package englishmlog;

import arc.Core;
import mindustry.mod.Mod;

public class Main extends Mod {

    @Override
    public void init() {
        Core.bundle.getProperties().each((key, value) -> {
            if (key.startsWith("instruction.")) {
                 Core.bundle.getProperties().put(key, "инструкция");
            }
        });
    }
}
