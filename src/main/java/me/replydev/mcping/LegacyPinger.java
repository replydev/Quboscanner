package me.replydev.mcping;

import me.replydev.mcping.data.FinalResponse;
import me.replydev.mcping.rawData.Players;
import me.replydev.mcping.rawData.Version;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Pinger for 1.6 protocol
 * https://wiki.vg/Server_List_Ping#Client_to_server
 */
public class LegacyPinger {
    private InetSocketAddress host;
    private int timeout;
    private int protocolVersion = -1;

    void setAddress(InetSocketAddress host) {
        this.host = host;
    }

    void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public LegacyPingResponse ping() throws IOException {
        Socket socket = new Socket();
        socket.setSoTimeout(this.timeout);
        socket.connect(this.host, this.timeout);
        sendPing(new DataOutputStream(socket.getOutputStream()));
        return readResponse(new DataInputStream(socket.getInputStream()));
    }

    private void sendPing(DataOutputStream dataOutputStream) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream handshake = new DataOutputStream(bytes);
        handshake.writeByte(0xFE);
        handshake.writeByte(0x01);
        handshake.writeByte(0xFA);
        byte[] hostNameBytes = host.getHostName().getBytes(StandardCharsets.UTF_16BE);
        String hostStr = "MC|" + host.getHostName();
        byte[] hostStrBytes = hostStr.getBytes(StandardCharsets.UTF_16BE);
        handshake.writeShort(hostStrBytes.length);
        for (byte aByte : hostStrBytes) {
            handshake.writeByte(aByte);
        }
        handshake.write(7 + hostStrBytes.length);
        handshake.write(protocolVersion);
        handshake.writeShort(hostNameBytes.length);
        for (byte aByte : hostNameBytes) {
            handshake.writeByte(aByte);
        }
        handshake.writeInt(host.getPort());
        dataOutputStream.write(bytes.toByteArray());
    }

    private LegacyPingResponse readResponse(DataInputStream dataInputStream) throws IOException {
        dataInputStream.readByte(); // Single data identifier
        dataInputStream.readShort();
        byte[] bytes = dataInputStream.readNBytes(dataInputStream.available());
        String str = new String(bytes, StandardCharsets.UTF_16BE);
        String[] split = str.split("\\0000");
        return new LegacyPingResponse(Integer.parseInt(split[1]), split[2], split[3], Integer.parseInt(split[4]), Integer.parseInt(split[5]));
    }

    public static final class LegacyPingResponse {
        public final int protocolVersion;
        public final String serverVersion;
        public final String motd;
        public final int players;
        public final int maxPlayers;

        public LegacyPingResponse(int protocolVersion, String serverVersion, String motd, int players, int maxPlayers) {
            this.protocolVersion = protocolVersion;
            this.serverVersion = serverVersion;
            this.motd = motd;
            this.players = players;
            this.maxPlayers = maxPlayers;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("LegacyPingResponse{");
            sb.append("protocolVersion=").append(protocolVersion);
            sb.append(", serverVersion='").append(serverVersion).append('\'');
            sb.append(", motd='").append(motd).append('\'');
            sb.append(", players=").append(players);
            sb.append(", maxPlayers=").append(maxPlayers);
            sb.append('}');
            return sb.toString();
        }

        public FinalResponse toFinalResponse() {
            Players players = new Players();
            players.setOnline(this.players);
            players.setMax(this.maxPlayers);
            Version version = new Version();
            version.setProtocol(this.protocolVersion);
            version.setName(this.serverVersion);
            return new FinalResponse(players, version, null, motd);
        }
    }
}
