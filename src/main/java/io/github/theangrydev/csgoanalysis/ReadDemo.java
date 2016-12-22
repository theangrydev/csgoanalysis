package io.github.theangrydev.csgoanalysis;

import io.github.theangrydev.csgoanalysis.NetmessagesPublic.CNETMsg_SignonState;
import javolution.io.Struct;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import static java.lang.String.format;
import static java.nio.ByteOrder.LITTLE_ENDIAN;

public class ReadDemo {

    private static final String DEMO_NAME = "7412_R1_Cloud9.CS_Ninjas_in.dem";

    public static void main(String[] args) throws IOException {
        DemoHeader demoHeader = DemoHeader.readDemoHeader(DEMO_NAME);

        CNETMsg_SignonState.parseFrom(new byte[2]);

        System.out.println("DemoHeader = " + demoHeader);
    }

    private static class DemoHeader extends Struct {
        public final UTF8String header = new UTF8String(8);
        public final Unsigned32 demoProtocol = new Unsigned32();
        public final Unsigned32 networkProtocol = new Unsigned32();
        public final UTF8String serverName = new UTF8String(260);
        public final UTF8String clientName = new UTF8String(260);
        public final UTF8String mapName = new UTF8String(260);
        public final UTF8String gameDirectory = new UTF8String(260);
        public final Float32 playbackTime = new Float32();
        public final Unsigned32 ticks = new Unsigned32();
        public final Unsigned32 frames = new Unsigned32();
        public final Unsigned32 signOnLength = new Unsigned32();

        private DemoHeader() {
            // Use the static factory method
        }

        public static DemoHeader readDemoHeader(String demoName) throws IOException {
            try (ReadableByteChannel channel = openChannelToDemoFile(demoName)) {
                DemoHeader demoHeader = new DemoHeader();
                ByteBuffer byteBuffer = readBytes(channel, demoHeader.size());
                demoHeader.setByteBuffer(byteBuffer, 0);
                return demoHeader;
            }
        }

        private static ReadableByteChannel openChannelToDemoFile(String demoName) {
            return Channels.newChannel(DemoHeader.class.getClassLoader().getResourceAsStream(demoName));
        }

        private static ByteBuffer readBytes(ReadableByteChannel channel, int targetBytesToRead) throws IOException {
            ByteBuffer byteBuffer = ByteBuffer.allocate(targetBytesToRead);
            byteBuffer.order(LITTLE_ENDIAN);
            int actualBytesRead = channel.read(byteBuffer);
            if (actualBytesRead == -1) {
                throw new IllegalStateException(format("Tried to read %d characters but reached the end of the stream!", actualBytesRead));
            } else if (actualBytesRead != targetBytesToRead) {
                throw new IllegalStateException(format("Tried to read %d characters but only read %d", targetBytesToRead, actualBytesRead));
            }
            return byteBuffer;
        }

        @Override
        public ByteOrder byteOrder() {
            return LITTLE_ENDIAN;
        }

        @Override
        public String toString() {
            return "DemoHeader{" +
                    "header=" + header +
                    ", demoProtocol=" + demoProtocol +
                    ", networkProtocol=" + networkProtocol +
                    ", serverName=" + serverName +
                    ", clientName=" + clientName +
                    ", mapName=" + mapName +
                    ", gameDirectory=" + gameDirectory +
                    ", playbackTime=" + playbackTime +
                    ", ticks=" + ticks +
                    ", frames=" + frames +
                    ", signOnLength=" + signOnLength +
                    '}';
        }
    }
}
