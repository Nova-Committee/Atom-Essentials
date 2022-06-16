package nova.committee.atom.ess.init.handler;

import net.minecraft.server.level.ServerPlayer;
import nova.committee.atom.ess.core.model.TPARequest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 11:51
 * Version: 1.0
 */
public class TpaHandler {
    public static final Map<Long, TPARequest> TPA_REQUEST = new HashMap<>();


    public static @Nonnull
    TPARequest getInstance(long id, ServerPlayer source, ServerPlayer target, boolean reverse) {
        TPARequest instance = getInstance(id);
        if (instance == null) {
            instance = new TPARequest(id, source, target, reverse);
            TPA_REQUEST.put(id, instance);
        }
        return instance;
    }

    public static @Nullable
    TPARequest getInstance(long id) {
        return TPA_REQUEST.get(id);
    }

    public static Map<Long, TPARequest> getTpaRequest() {
        return TPA_REQUEST;
    }


}
