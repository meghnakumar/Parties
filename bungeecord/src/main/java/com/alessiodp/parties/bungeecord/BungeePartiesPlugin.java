package com.alessiodp.parties.bungeecord;

import com.alessiodp.core.bungeecord.addons.internal.json.BungeeJsonHandler;
import com.alessiodp.core.bungeecord.addons.internal.title.BungeeTitleHandler;
import com.alessiodp.core.bungeecord.scheduling.ADPBungeeScheduler;
import com.alessiodp.core.bungeecord.user.BungeeUser;
import com.alessiodp.core.common.bootstrap.ADPBootstrap;
import com.alessiodp.core.common.configuration.Constants;
import com.alessiodp.parties.api.interfaces.PartiesOptions;
import com.alessiodp.parties.bungeecord.addons.BungeePartiesAddonManager;
import com.alessiodp.parties.bungeecord.addons.external.BungeeMetricsHandler;
import com.alessiodp.parties.bungeecord.api.BungeePartiesOptionsHandler;
import com.alessiodp.parties.bungeecord.commands.BungeePartiesCommandManager;
import com.alessiodp.parties.bungeecord.configuration.BungeePartiesConfigurationManager;
import com.alessiodp.parties.bungeecord.events.BungeeEventManager;
import com.alessiodp.parties.bungeecord.listeners.BungeeChatListener;
import com.alessiodp.parties.bungeecord.listeners.BungeeFollowListener;
import com.alessiodp.parties.bungeecord.listeners.BungeeJoinLeaveListener;
import com.alessiodp.parties.bungeecord.messaging.BungeePartiesMessenger;
import com.alessiodp.parties.bungeecord.parties.BungeePartyManager;
import com.alessiodp.parties.bungeecord.players.BungeePlayerManager;
import com.alessiodp.parties.bungeecord.utils.BungeeEconomyManager;
import com.alessiodp.parties.bungeecord.utils.BungeeMessageUtils;
import com.alessiodp.parties.common.ServerCommonPlugin;
import com.alessiodp.parties.common.configuration.PartiesConstants;
import com.alessiodp.parties.common.players.objects.PartyPlayerImpl;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class BungeePartiesPlugin extends ServerCommonPlugin {
	@Getter private final int bstatsId = PartiesConstants.PLUGIN_BSTATS_BUNGEE_ID;
	
	public BungeePartiesPlugin(ADPBootstrap bootstrap) {
		super(bootstrap);
	}
	
	@Override
	protected void initializeCore() {
		scheduler = new ADPBungeeScheduler(this);
		configurationManager = new BungeePartiesConfigurationManager(this);
		messageUtils = new BungeeMessageUtils(this);
		messenger = new BungeePartiesMessenger(this);
		
		super.initializeCore();
	}
	
	@Override
	protected void loadCore() {
		partyManager = new BungeePartyManager(this);
		playerManager = new BungeePlayerManager(this);
		commandManager = new BungeePartiesCommandManager(this);
		
		super.loadCore();
	}
	
	@Override
	protected void postHandle() {
		addonManager = new BungeePartiesAddonManager(this);
		economyManager = new BungeeEconomyManager(this);
		eventManager = new BungeeEventManager(this);
		
		super.postHandle();
		
		new BungeeMetricsHandler(this);
		((BungeePartiesConfigurationManager) getConfigurationManager()).makeConfigsSync();
	}
	
	@Override
	protected  void initializeJsonHandler() {
		jsonHandler = new BungeeJsonHandler(this);
	}
	
	@Override
	protected  void initializeTitleHandler() {
		titleHandler = new BungeeTitleHandler(this);
	}
	
	@Override
	protected void registerListeners() {
		getLoggerManager().logDebug(Constants.DEBUG_PLUGIN_REGISTERING, true);
		Plugin plugin = (Plugin) getBootstrap();
		PluginManager pm = plugin.getProxy().getPluginManager();
		pm.registerListener(plugin, new BungeeChatListener(this));
		pm.registerListener(plugin, new BungeeFollowListener(this));
		pm.registerListener(plugin, new BungeeJoinLeaveListener(this));
	}
	
	@Override
	public void reloadConfiguration() {
		super.reloadConfiguration();
		
		((BungeePartiesConfigurationManager) getConfigurationManager()).makeConfigsSync();
	}
	
	@Override
	protected PartiesOptions initApiOptions() {
		return new BungeePartiesOptionsHandler();
	}
	
	@Override
	public boolean isBungeeCordEnabled() {
		return false;
	}
	
	@Override
	public String getServerName(PartyPlayerImpl player) {
		if (player != null) {
			BungeeUser user = (BungeeUser) getPlayer(player.getPlayerUUID());
			if (user != null && user.getServer() != null)
				return user.getServer().getName();
		}
		return "";
	}

}
