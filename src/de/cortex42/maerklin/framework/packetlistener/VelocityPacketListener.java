package de.cortex42.maerklin.framework.packetlistener;

import de.cortex42.maerklin.framework.CANPacket;
import de.cortex42.maerklin.framework.CS2CANCommands;

/**
 * Created by ivo on 16.12.15.
 */
public abstract class VelocityPacketListener extends PacketListener {
    private int velocity;

    @Override
    public void onPacketEvent(final PacketEvent packetEvent) {
        CANPacket canPacket = packetEvent.getCANPacket();

        if ((canPacket.getCommand() & 0xFE) == CS2CANCommands.VELOCITY
                && (canPacket.getDlc() == CS2CANCommands.VELOCITY_SET_DLC)) {
            byte[] data = canPacket.getData();

            velocity = ((data[4] & 0xFF) << 8 | (data[5] & 0xFF));

            onSuccess();
        }
    }

    public int getVelocity() {
        return velocity;
    }
}