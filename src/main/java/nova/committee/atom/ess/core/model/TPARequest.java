package nova.committee.atom.ess.core.model;

import net.minecraft.server.level.ServerPlayer;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 11:50
 * Version: 1.0
 */
public class TPARequest {


    private final long id;

    //执行tpa的人
    private final ServerPlayer source;

    //收到tpa的人
    private final ServerPlayer target;

    private final long createTime;


    public TPARequest(long id, ServerPlayer source, ServerPlayer target, boolean reverse) {
        this.id = id;
        if (reverse) {
            this.target = source;
            this.source = target;
        } else {
            this.source = source;
            this.target = target;
        }
        this.createTime = System.currentTimeMillis();
    }


    public long getId() {
        return id;
    }

    public ServerPlayer getSource() {
        return source;
    }

    public ServerPlayer getTarget() {
        return target;
    }

    public long getCreateTime() {
        return createTime;
    }
}
