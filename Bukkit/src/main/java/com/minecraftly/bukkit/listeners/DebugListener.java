/*
 * See provided LICENCE.txt in the project root.
 * Licenced to Minecraftly under GNU-GPLv3.
 */

package com.minecraftly.bukkit.listeners;

import com.minecraftly.bukkit.MinecraftlyBukkitCore;
import com.minecraftly.core.MinecraftlyUtil;
import com.minecraftly.core.util.Callback;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * @author Cory Redmond &lt;ace@ac3-servers.eu&gt;
 */
@RequiredArgsConstructor
public class DebugListener implements PluginMessageListener {

	private final MinecraftlyBukkitCore core;

	Callback<Callable<Void>, PlayerInputPair> loadedCallback = param -> () -> {
		try {
			ScriptEngine engine = getScriptEngine( param.getPlayer() );
			engine.eval( param.getInput() );
		} catch ( Exception e ) {
			param.getPlayer().sendMessage( ChatColor.YELLOW + "There was an error executing the script!" );
			param.getPlayer().sendMessage( ChatColor.YELLOW + e.getClass().getName() );
			param.getPlayer().sendMessage( ChatColor.YELLOW + e.getMessage() );
		}
		return null;
	};

	@Override
	public void onPluginMessageReceived( final String channel, final Player player, byte[] message ) {

		try {

			if ( !channel.equals( "KMCLY" ) || !player.hasPermission( "minecraftly.debug" ) ) return;

			DataInputStream dis = new DataInputStream( new ByteArrayInputStream( message ) );
			String input = dis.readUTF();

			if ( input.equalsIgnoreCase( "URL" ) ) {

				final String url = dis.readUTF();
				core.getOriginObject().getServer().getScheduler().runTaskAsynchronously( core.getOriginObject(), () -> {

					try {
						String downloadedInput = MinecraftlyUtil.downloadText( url );
						Callable<Void> voidCallable = loadedCallback.call( new PlayerInputPair( player, downloadedInput ) );
						core.getOriginObject().getServer().getScheduler().callSyncMethod( core.getOriginObject(), voidCallable );
					} catch ( Exception e ) {
						player.sendMessage( ChatColor.YELLOW + "There was an error executing the script!" );
						player.sendMessage( ChatColor.YELLOW + e.getClass().getName() );
						player.sendMessage( ChatColor.YELLOW + e.getMessage() );
					}

				} );

			} else {
				try {
					loadedCallback.call( new PlayerInputPair( player, input ) ).call();
				} catch ( Exception e ) {
					player.sendMessage( ChatColor.YELLOW + "There was an error executing the script!" );
					player.sendMessage( ChatColor.YELLOW + e.getClass().getName() );
					player.sendMessage( ChatColor.YELLOW + e.getMessage() );
				}
			}

		} catch ( IOException ex ) {
			ex.printStackTrace();
		}

	}

	private ScriptEngine getScriptEngine( Player player ) {

		core.getDebugger().put( "server", Bukkit.getServer() );

		ScriptEngine engine = core.getDebugger().getEngine();
		engine.put( "me", player );
		try {
			engine.eval( "function print( message ) { me.sendMessage( message ); }" );
		} catch ( ScriptException ignored ) {
		}

		return engine;

	}

	@Data
	private class PlayerInputPair {

		private final Player player;
		private final String input;

	}

}
