package lynx.lobby;//https://github.com/FabricMC/fabric-example-modpackage com.example;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import net.minecraft.server.world.ServerWorld;


import static net.minecraft.server.command.CommandManager.*;

public class ExampleMod implements ModInitializer {
	public static final String MOD_ID = "Hello Fabric World :3";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	Gson gson = new Gson();
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		createSubdirectory();
		double x;
		double y;
		double z;
		String namespace;
		String key;
		conFig read = Config("./config/lobby/config.json");
		x = read.x;
		y = read.y;
		z = read.z;
		namespace = read.namespace;
		key = read.key;
		String finalNamespace = namespace;
		String finalKey = key;
		double finalX = x;
		double finalY = y;
		double finalZ = z;
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("lobby")
				.executes(context -> {
					// For versions below 1.19, replace "Text.literal" with "new LiteralText".
					// For versions below 1.20, remode "() ->" directly.
					context.getSource().sendFeedback(() -> Text.literal("Sending to Lobby..."), false);
					ServerCommandSource player = context.getSource();
					PlayerData playerData = new PlayerData(player.getPosition(),player.getPlayer().bodyYaw,player.getPlayer().prevPitch);
					String filepath = "./config/lobby/data/" + player.getEntity().getUuid() + ".json";
					writePlayerData(filepath, playerData);
					Set<PositionFlag> flags = Set.of(PositionFlag.X_ROT);
					final RegistryKey<World> LOBBY = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(finalNamespace, finalKey));
					ServerWorld nether = player.getServer().getWorld(LOBBY);
					player.getEntity().teleport(nether, finalX, finalY, finalZ, flags, 0 , 0);
					return 1;
				})));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("worldtp")
				.executes(context -> {
					// For versions below 1.19, replace "Text.literal" with "new LiteralText".
					// For versions below 1.20, remode "() ->" directly.
					context.getSource().sendFeedback(() -> Text.literal("Sending to World..."), false);
					ServerCommandSource player = context.getSource();
					String filepath = "./config/lobby/data/" + player.getEntity().getUuid() + ".json";
					PlayerData playerpos = readPlayerDataFromFile(filepath);
					Vec3d playerPos = playerpos.position;
					float yaw = playerpos.yaw;
					float pitch = playerpos.pitch;
					double x1 = playerPos.x;
					double y1 = playerPos.y;
					double z1 = playerPos.z;

					Set<PositionFlag> flags = Set.of(PositionFlag.X_ROT);
					ServerWorld overworld = player.getServer().getWorld(ServerWorld.OVERWORLD);
					player.getEntity().teleport(overworld, x1, y1, z1, flags, yaw , pitch);


					return 1;
				})));
	}

	public static final Path CONFIG_DIR = Paths.get("config");
	public static final Path JSON_DIR = CONFIG_DIR.resolve("lobby");
	public static final Path SUBDIRECTORY_DIR = JSON_DIR.resolve("data");

	public static void createSubdirectory() {
		try {
			Files.createDirectories(SUBDIRECTORY_DIR);
		} catch (IOException e) {
			System.out.println("Found Directory");
		}
	}
	public conFig Config(String filepath) {
		File file = new File(filepath);
		conFig Default = new conFig(-22.5,93,-31.5,"minecraft","overworld");

		try{
			if(file.createNewFile()){
				System.out.println("File Created: " + file.getAbsolutePath());

				try(FileWriter fileWriter = new FileWriter(filepath)) {
					Gson gson1 = new Gson();
					fileWriter.write(gson1.toJson(Default));
				}

				catch (IOException e){
					LOGGER.error("ERROR: ", e);
				}
				return Default;
			}	else {
				System.out.println("Found conFig");
			}

		} catch (IOException e) {
            throw new RuntimeException(e);
        }
		try(FileReader reader = new FileReader(filepath)){
			Gson gson2 = new Gson();
			return gson2.fromJson(reader, conFig.class);

		} catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
	public PlayerData readPlayerDataFromFile(String filePath) {
		File file = new File(filePath);

		try {
			if (file.createNewFile()) {

				System.out.println("File created: " + file.getAbsolutePath());

			} else {
				System.out.println("File already exists.");

			}

			try (FileReader reader = new FileReader(filePath)) {
				Gson gson = new Gson();
				return gson.fromJson(reader, PlayerData.class);

				// Access the player ID and position from the PlayerData object

				// Do something with the retrieved player data


			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public  void writePlayerData(String filepath, PlayerData playerData){
		File file = new File(filepath);
		try{
			if (file.createNewFile()) {
				System.out.println("File created: " + file.getAbsolutePath());
			} else {
				System.out.println("File already exists.");

			}

			try (FileWriter writer = new FileWriter(file)) {
				Gson gson = new Gson();
				writer.write(gson.toJson(playerData));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}




// ...



// ...