package drawsoundfx.utils;

import rwmidi.MidiOutput;

public class MappingThread implements Runnable{

    private MidiOutput device;
    private boolean alive = true;
    private final int CCNumber;
    private final int channel;

    public MappingThread(MidiOutput device, int channel, int CCNumber){
        this.device = device;
        this.channel = channel;
        this.CCNumber = CCNumber;
    }

    public synchronized boolean isAlive() {
        return alive;
    }

    public synchronized void stop() {
        this.alive = false;
    }

    @Override
    public void run() {
        int i = 0;

        while (isAlive()){
            device.sendController(channel, CCNumber, i);
            i = (i + 1)%128;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
