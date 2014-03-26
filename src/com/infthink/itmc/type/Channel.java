package com.infthink.itmc.type;

import java.io.Serializable;

public class Channel implements Serializable {
    public static final int TV_CHANNEL_ID = 150994944;
    private static final long serialVersionUID = 1L;
    public int channelID;
    public String channelName;
    public int channelType;
    public Channel[] subChannels;

    @Override
    public boolean equals(Object obj) {
        if ((obj != null) && ((obj instanceof Channel)))
            return ((Channel) obj).channelID == this.channelID;
        return super.equals(obj);
    }

    public boolean isTvChannel() {
        return this.channelID == TV_CHANNEL_ID;
    }
}
