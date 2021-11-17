package essentialclient.config.clientrule;

import essentialclient.clientscript.ClientScript;
import essentialclient.commands.TravelCommand;
import essentialclient.feature.AFKRules;
import essentialclient.feature.BetterAccurateBlockPlacement;
import essentialclient.feature.HighlightLavaSources;
import essentialclient.utils.EssentialUtils;
import essentialclient.utils.command.ClientNickHelper;
import essentialclient.utils.command.PlayerClientCommandHelper;
import essentialclient.utils.command.PlayerListCommandHelper;
import essentialclient.utils.render.CapeHelper;

import java.util.*;

public class ClientRules {

	private static final Map<String, ClientRule<?>> clientRuleMap = new HashMap<>();

	public static BooleanClientRule COMMAND_CLIENT_NICK = new BooleanClientRule("commandClientNick", "This allows you to rename player names on the client", ClientRuleHelper::refreshCommand);
	public static BooleanClientRule COMMAND_MUSIC = new BooleanClientRule("commandMusic", "This command allows you to manipulate the current music", ClientRuleHelper::refreshCommand);
	public static BooleanClientRule COMMAND_PLAYER_CLIENT = new BooleanClientRule("commandPlayerClient", "This command allows you to save /player... commands and execute them", ClientRuleHelper::refreshCommand);
	public static BooleanClientRule COMMAND_PLAYER_LIST = new BooleanClientRule("commandPlayerList", "This command allows you to execute /player... commands in one command (requires commandPlayerClient)", ClientRuleHelper::refreshCommand);
	public static BooleanClientRule COMMAND_REGION = new BooleanClientRule("commandRegion", "This command allows you to determine the region you are in or the region at set coords", ClientRuleHelper::refreshCommand);
	public static BooleanClientRule COMMAND_SUGGESTOR_IGNORES_SPACES = new BooleanClientRule("commandSuggestorIgnoresSpaces", "This makes the command suggestor ignore spaces");
	public static BooleanClientRule COMMAND_TRAVEL = new BooleanClientRule("commandTravel", "This command allows you to travel to a set location", ClientRuleHelper::refreshCommand);
	public static BooleanClientRule BETTER_ACCURATE_BLOCK_PLACEMENT = new BooleanClientRule("betterAccurateBlockPlacement", "This is the same as accurate block placement for tweakeroo but handled all client side, see controls...");
	public static BooleanClientRule DISABLE_BOB_VIEW_WHEN_HURT = new BooleanClientRule("disableBobViewWhenHurt", "Disables the camera bobbing when you get hurt");
	public static BooleanClientRule DISABLE_HOTBAR_SCROLLING = new BooleanClientRule("disableHotbarScrolling", "This will prevent you from scrolling in your hotbar, learn to use hotkeys :)");
	public static BooleanClientRule DISABLE_JOIN_LEAVE_MESSAGES = new BooleanClientRule("disableJoinLeaveMessages", "This will prevent join/leave messages from displaying");
	public static BooleanClientRule DISABLE_MAP_RENDERING = new BooleanClientRule("disableMapRendering", "This disables maps rendering in item frames" );
	public static BooleanClientRule DISABLE_NARRATOR = new BooleanClientRule("disableNarrator", "Disables cycling narrator when pressing CTRL + B" );
	public static BooleanClientRule DISABLE_NIGHT_VISION_FLASH = new BooleanClientRule("disableNightVisionFlash", "Disables the flash that occurs when night vision is about to run out");
	public static BooleanClientRule DISABLE_OP_MESSAGES = new BooleanClientRule("disableOpMessages", "This will prevent system messages from displaying" );
	public static BooleanClientRule DISABLE_RECIPE_NOTIFICATIONS = new BooleanClientRule("disableRecipeNotifications", "Disables the recipe toast from showing" );
	public static BooleanClientRule DISABLE_TUTORIAL_NOTIFICATIONS = new BooleanClientRule("disableTutorialNotifications", "Disables the tutorial toast from showing"   );
	public static BooleanClientRule DISPLAY_TIME_PLAYED = new BooleanClientRule("displayTimePlayed", "This will display how long you have had your current client open for in the corner of the pause menu");
	public static BooleanClientRule ENABLE_SCRIPT_ON_JOIN = new BooleanClientRule("enableScriptOnJoin", "This will enable your selected script when you join a world automatically");
	public static BooleanClientRule ESSENTIAL_CLIENT_MAIN_MENU = new BooleanClientRule("essentialClientMainMenu", "This renders the Essential Client Menu on the main menu screen");
	public static BooleanClientRule HIGHLIGHT_LAVA_SOURCES = new BooleanClientRule("highlightLavaSources", "Highlights lava sources, credit to plusls for the original code for this", ClientRuleHelper::refreshWorld);
	public static BooleanClientRule INCREASE_SPECTATOR_SCROLL_SPEED = new BooleanClientRule("increaseSpectatorScrollSpeed", "Increases the limit at which you can scroll to go faster in spectator");
	public static BooleanClientRule MISSING_TOOLS = new BooleanClientRule("missingTools", "Adds client functionality to missingTools from Carpet for the client");
	public static BooleanClientRule PERMANENT_CHAT_HUD = new BooleanClientRule("permanentChatHud", "This prevents the chat from being cleared, also applies when chaning worlds/servers");
	public static BooleanClientRule REMOVE_WARN_RECEIVED_PASSENGERS = new BooleanClientRule("removeWarnReceivedPassengers", "This removes the 'Received passengers for unknown entity' warning on the client");
	public static BooleanClientRule STACKABLE_SHULKERS_IN_PLAYER_INVENTORIES = new BooleanClientRule("stackableShulkersInPlayerInventories", "This allows for shulkers to stack only in your inventory");
	public static BooleanClientRule STACKABLE_SHULKERS_WITH_ITEMS = new BooleanClientRule("stackableShulkersWithItems", "This allows for shulkers with items to stack only in your inventory");
	public static BooleanClientRule TOGGLE_TAB = new BooleanClientRule("toggleTab", "This allows you to toggle tab instead of holding to see tab" );
	public static BooleanClientRule UNLOCK_ALL_RECIPES_ON_JOIN = new BooleanClientRule("unlockAllRecipesOnJoin", "Unlocks every recipe when joining a world");

	public static IntegerClientRule ANNOUNCE_AFK = new IntegerClientRule("announceAFK", "This announces when you become afk after a set amount of time (ticks)");
	public static IntegerClientRule AFK_LOGOUT = new IntegerClientRule("afkLogout", "Number of ticks until client will disconnect you from world (must be >= 200 to be active)");
	public static IntegerClientRule AUTO_WALK = new IntegerClientRule("autoWalk", "This will auto walk after you have held your key for set amount of ticks");
	public static IntegerClientRule INCREASE_SPECTATOR_SCROLL_SENSITIVITY = new IntegerClientRule("increaseSpectatorScrollSensitivity", "Increases the sensitivity at which you can scroll to go faster in spectator");
	public static IntegerClientRule MUSIC_INTERVAL = new IntegerClientRule("musicInterval", "The amount of ticks between each soundtrack that is played, 0 = random");
	public static IntegerClientRule SWITCH_TO_TOTEM = new IntegerClientRule("switchToTotem", "This will switch to a totem (if you have one), under a set amount of health");
	public static IntegerClientRule SOUL_SPEED_FOV_MULTIPLIER = new IntegerClientRule("soulSpeedFovMultiplier", "Determines the percentage of Fov scaling when walking on soil soul or soul sand");
	public static IntegerClientRule WATER_FOV_MULTIPLIER = new IntegerClientRule("waterFovMultiplier","Determines the percentage of Fov scaling when fully submerged in water");

	public static DoubleClientRule OVERRIDE_CREATIVE_WALK_SPEED = new DoubleClientRule("overrideCreativeWalkSpeed", "This allows you to override the vanilla walk speed in creative mode", 0.0D);

	public static StringClientRule ANNOUNCE_AFK_MESSAGE = new StringClientRule("announceAFKMessage", "This is the message you announce after you are afk", "I am now AFK");
	public static StringClientRule CLIENT_SCRIPT_FILENAME = new StringClientRule("clientScriptFilename", "This allows you to choose a specific script file name", "clientscript", EssentialUtils::checkifScriptFileExists);

	public static CycleClientRule CUSTOM_CLIENT_CAPE = new CycleClientRule("customClientCape", "This allows you to select a Minecraft cape to wear, this only appears client side", CapeHelper.capeNames, ClientRuleHelper::refreshCape);
	public static CycleClientRule DISPLAY_RULE_TYPE = new CycleClientRule("displayRuleType", "This allows you to choose the order you want rules to be displayed", List.of("Alphabetical", "Rule Type"), ClientRuleHelper::refreshScreen);
	public static CycleClientRule MUSIC_TYPES = new CycleClientRule("musicTypes", "This allows you to select what music types play", List.of("Default", "Overworld", "Nether", "Overwrld + Nethr", "End", "Creative", "Menu", "Credits", "Any"), ClientRuleHelper::refreshMusic);

	public static void init() {
		// IO stuff
		EssentialUtils.checkIfEssentialClientDirExists();
		ClientRuleHelper.readSaveFile();
		PlayerClientCommandHelper.readSaveFile();
		PlayerListCommandHelper.readSaveFile();
		ClientNickHelper.readSaveFile();
		EssentialUtils.checkifScriptFileExists();

		// Registering ticking methods
		AFKRules.INSTANCE.register();
		TravelCommand.register();
		BetterAccurateBlockPlacement.register();
		ClientScript.getInstance().register();

		// Init anything else
		HighlightLavaSources.init();

		for (ClientRule<?> rule : clientRuleMap.values()) {
			rule.run();
		}
	}

	public static void addRule(String name, ClientRule<?> rule) {
		clientRuleMap.put(name, rule);
	}

	public static ClientRule<?> ruleFromString(String name) {
		return clientRuleMap.get(name);
	}

	protected static Map<String, String> rulesAsStringMap() {
		Map<String, String> stringClientRulesMap = new HashMap<>();
		for (ClientRule<?> rule : clientRuleMap.values()) {
			stringClientRulesMap.put(rule.getName(), rule.getValue().toString());
		}
		return stringClientRulesMap;
	}

	public static Collection<ClientRule<?>> getMapAlphabetically() {
		SortedMap<String, ClientRule<?>> sortedMap = new TreeMap<>();
		for (ClientRule<?> rule : clientRuleMap.values()) {
			sortedMap.put(rule.getName(), rule);
		}
		return sortedMap.values();
	}

	public static Collection<ClientRule<?>> getMapInType() {
		SortedMap<String, ClientRule<?>> sortedMap = new TreeMap<>();
		for (ClientRule.Type type : ClientRule.Type.values()) {
			for (ClientRule<?> rule : clientRuleMap.values()) {
				if (rule.getType() == type) {
					sortedMap.put(type.toString() + rule.getName(), rule);
				}
			}
		}
		return sortedMap.values();
	}
}
