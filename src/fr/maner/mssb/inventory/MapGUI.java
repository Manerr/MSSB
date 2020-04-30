package fr.maner.mssb.inventory;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;

import fr.maner.mssb.factory.ItemFactory;
import fr.maner.mssb.game.GameData;
import fr.maner.mssb.inventory.init.InvClickData;
import fr.maner.mssb.inventory.init.InvGUI;
import fr.maner.mssb.utils.map.MapData;

public class MapGUI extends InvGUI {
	
	private int size;
	private List<MapData> mapDataList;

	public MapGUI(GameData gameData, int size) {
		super(size, "§eMenu de sélection de map", gameData);
		
		this.size = size;
		this.mapDataList = getGameData().getGameConfig().getMapConfig().getMapData();
	}

	@Override
	public void initContent() {
		if (mapDataList == null) return;
		
		for (int i = 0; i < mapDataList.size(); i++) {
			MapData mapData = mapDataList.get(i);
			
			setItem(i, mapData.getIs(), invClick -> selectMap(invClick, mapData));
		}
		
		setItem(size - 4, new ItemFactory(Material.BARRIER).setName("&cAnnuler").build(), invClick -> invClick.getPlayer().closeInventory());
	}
	
	private void selectMap(InvClickData invClick, MapData mapData) {
		invClick.getPlayer().playSound(invClick.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.75F, 1F);
		
		getGameData().startGame(mapData);
	}
	
	public static int calculInvSize(List<MapData> mapDataList) {
		int listSize = mapDataList.size();
		if (listSize > 45) throw new IllegalStateException("The plugin can only support a maximum of 45 maps");
		
		int nbLine = (int) Math.ceil((double) listSize / 9);
		
		return (nbLine + 1) * 9;
	}
}