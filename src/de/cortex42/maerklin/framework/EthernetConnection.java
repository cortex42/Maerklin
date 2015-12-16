package de.cortex42.maerklin.framework;

import de.cortex42.maerklin.framework.packetlistener.PacketEvent;
import de.cortex42.maerklin.framework.packetlistener.PacketListener;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

//todo add documentation

/**
 * Created by ivo on 06.11.15.
 */
//Konkrete Strategie
public class EthernetConnection implements Connection {
    private final DatagramSocket datagramSocket;
    private final int targetPort;
    private final InetAddress targetAddress;

    private final ArrayList<PacketListener> packetListeners = new ArrayList<>();
    private final ArrayList<ExceptionHandler> exceptionHandlers = new ArrayList<>();
    private boolean isListening = false;

    public EthernetConnection(int localPort, int targetPort, String targetAddress) throws FrameworkException {
        this.targetPort = targetPort;

        try {
            this.targetAddress = InetAddress.getByName(targetAddress);

            datagramSocket = new DatagramSocket(localPort);
        } catch (SocketException | UnknownHostException e) {
            throw new FrameworkException(e);
        }
    }

    @Override
    synchronized public void close() { //todo https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
        stopListening();
        datagramSocket.close();
    }

    synchronized public void writeCANPacket(CANPacket canPacket) throws FrameworkException {
        byte[] bytes = canPacket.getBytes();

        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, targetAddress, targetPort);

        try {
            datagramSocket.send(datagramPacket);
        } catch (IOException e) {
            throw new FrameworkException(e);
        }
    }

    synchronized public void addExceptionHandler(ExceptionHandler exceptionHandler) {
        if (!exceptionHandlers.contains(exceptionHandler)) {
            exceptionHandlers.add(exceptionHandler);
        }
    }

    synchronized public void removeExceptionHandler(ExceptionHandler exceptionHandler) {
        exceptionHandlers.remove(exceptionHandler);
    }

    synchronized public void addPacketListener(PacketListener packetListener) {
        if(!packetListeners.contains(packetListener)) {
            packetListeners.add(packetListener);
        }

        startListening();
    }

    synchronized public void removePacketListener(PacketListener packetListener) {
        packetListeners.remove(packetListener);

        if (packetListeners.isEmpty()) {
            stopListening();
        }
    }

    private void startListening(){
        if(!isListening) {
            isListening = true;

            Thread thread = new Thread(new Runnable() {
                public void run() {
                    while (isListening) {
                        DatagramPacket datagramPacket = new DatagramPacket(new byte[CANPacket.CAN_PACKET_SIZE], CANPacket.CAN_PACKET_SIZE);

                        try {
                            datagramSocket.receive(datagramPacket);
                        } catch (IOException e) {
                            FrameworkException frameworkException = new FrameworkException(e);

                            //call exception handlers
                            for (int i = 0; i < exceptionHandlers.size(); i++) {
                                exceptionHandlers.get(i).onException(frameworkException);
                            }

                            break;
                        }

                        CANPacket canPacket = new CANPacket(datagramPacket.getData());

                        for (int i = 0; i < packetListeners.size(); i++) {
                            packetListeners.get(i).packetEvent(new PacketEvent(canPacket));
                        }
                    }
                }
            });

            thread.start();
        }
    }

    private void stopListening(){
        isListening=false;
    }


}
