package deepslate.server;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.utils.time.TimeUnit;

public class Server {
    public static void main(String[] args) {
        var server = MinecraftServer.init();

        //Biomes.registerBiomes();

        var instance = MinecraftServer.getInstanceManager().createInstanceContainer();
        instance.setChunkGenerator(new ChunkGenerator());

        MinecraftServer.getGlobalEventHandler().addListener(PlayerLoginEvent.class, e -> {
            var p = e.getPlayer();

            p.setRespawnPoint(new Pos(0, 80, 0));
            e.setSpawningInstance(instance);
            p.setGameMode(GameMode.SPECTATOR);
            p.setPermissionLevel(4);

            /*MinecraftServer.getSchedulerManager().buildTask(() -> {
                var c = p.getChunk();
                if (c == null) return;
                var nbt = c.getBlock(p.getPosition().chunkX(), -64, p.getPosition().chunkZ()).nbt();
                if (nbt == null) return;
                p.sendActionBar(Component.text("C: " + nbt.getDouble("continentalness")));
            }).delay(1, TimeUnit.SECOND).repeat(1, TimeUnit.SECOND).schedule();*/
        });

        MinecraftServer.getCommandManager().register(new Command("gamemode", "gm") {{
            addSyntax((sender, context) -> {
                if (sender instanceof Player p) {
                    p.setGameMode(context.get("mode"));
                }
            }, ArgumentType.Enum("mode", GameMode.class).setFormat(ArgumentEnum.Format.LOWER_CASED));
        }});

        server.start("0.0.0.0", 25565);
    }
}
