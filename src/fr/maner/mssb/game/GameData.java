package fr.maner.mssb.game;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import fr.maner.mssb.MSSB;
import fr.maner.mssb.factory.BookFactory;
import fr.maner.mssb.runnable.GameRun;
import fr.maner.mssb.runnable.ItemEffectRun;
import fr.maner.mssb.runnable.StartRun;
import fr.maner.mssb.type.state.GameState;
import fr.maner.mssb.type.state.InGameState;
import fr.maner.mssb.type.state.LobbyState;
import fr.maner.mssb.utils.map.MapData;
import net.md_5.bungee.api.ChatColor;

public class GameData {

	private transient MSSB pl;

	public GameData(MSSB pl) {
		this.pl = pl;
		this.config = new GameConfig(pl);
		setGameState(new LobbyState(this), true);
	}

	private GameState state;
	private GameConfig config;

	private ItemEffectRun itemEffectRun;
	private GameRun gameRun;

	public void startGame(MapData mapData) {
		if (state.hasGameStart())
			return;

		getGameConfig().setBuildMode(false);
		new StartRun(pl, this, mapData);
	}

	public void stopGame() {
		if (!state.hasGameStart())
			return;

		InGameState inGameState = (InGameState) state;
		setGameState(new LobbyState(this), true);
		LobbyState lobbyState = (LobbyState) state;

		ItemStack book = BookFactory.buildResumeBook(inGameState);
		lobbyState.setBookResume(book);

		Bukkit.getOnlinePlayers().forEach(p -> {
			p.sendTitle("", ChatColor.translateAlternateColorCodes('&', "&eLa partie est terminée !"), 10, 70, 20);
			p.playSound(p.getLocation(), Sound.ENTITY_WITHER_DEATH, 0.25F, 1F);
			lobbyState.initPlayer(p);
			p.openBook(book);
		});
	}

	public void createRunnable() {
		itemEffectRun = new ItemEffectRun(pl);
		gameRun = new GameRun(this);
	}
	
	public void stopRunnable() {
		itemEffectRun.cancel();
		gameRun.cancel();
	}

	public void setGameState(GameState newState, boolean initPlayer) {
		HandlerList.unregisterAll(state);
		this.state = newState;
		pl.getServer().getPluginManager().registerEvents(newState, pl);

		if (initPlayer)
			Bukkit.getOnlinePlayers().forEach(p -> newState.initPlayer(p));
	}

	public ItemEffectRun getItemEffectRun() {
		return itemEffectRun;
	}

	public GameState getState() {
		return state;
	}

	public GameConfig getGameConfig() {
		return config;
	}

	public MSSB getPlugin() {
		return pl;
	}

	public void checkGameOver() {
		if (!getState().hasGameStart())
			return;
		if (!getGameConfig().getGameEnd().isGameOver((InGameState) getState()))
			return;

		stopGame();
	}
}
