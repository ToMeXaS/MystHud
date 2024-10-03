package lt.tomexas.mysthud.utils;

import org.bukkit.entity.Entity;

public class EntityUtils {

    public static boolean isUnderWater(Entity entity) {
        return entity.isInWater();
    }

}
