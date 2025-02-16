package piners.hardnesspatch.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import piners.hardnesspatch.HardnessPatchClient;

import java.util.HashMap;
import java.util.Map;

public class NetworkHandler implements CustomPayload {
    public static final CustomPayload.Id<NetworkHandler> ID =
            new CustomPayload.Id<>(Identifier.of("hardnesspatch", "sync_config"));

    public static final PacketCodec<PacketByteBuf, NetworkHandler> CODEC = PacketCodec.of(
            (value, buf) -> {
                buf.writeVarInt(value.config.size());
                value.config.forEach((key, val) -> {
                    buf.writeString(key, 32767);
                    buf.writeFloat(val);
                });
            },
            buf -> {
                int size = buf.readVarInt();
                Map<String, Float> config = new HashMap<>();
                for (int i = 0; i < size; i++) {
                    String key = buf.readString(32767);
                    float value = buf.readFloat();
                    config.put(key, value);
                }
                return new NetworkHandler(config);
            }
    );

    public final Map<String, Float> config;

    public NetworkHandler(Map<String, Float> config) {
        this.config = config;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Environment(EnvType.CLIENT)
    public static class Client {
        public static void initialize() {
            // Only register receiver, type is registered by server
            ClientPlayNetworking.registerGlobalReceiver(ID, (payload, context) -> {
                context.client().execute(() -> {
                    HardnessPatchClient.setServerConfig(payload.config);
                });
            });
        }
    }

    public static void sendConfigToPlayer(Map<String, Float> config, ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new NetworkHandler(config));
    }
}