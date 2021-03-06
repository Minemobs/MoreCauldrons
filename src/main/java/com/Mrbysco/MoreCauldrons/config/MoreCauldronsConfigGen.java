package com.mrbysco.morecauldrons.config;

import com.mrbysco.morecauldrons.ModReference;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = ModReference.MOD_ID)
@Config.LangKey("morecauldrons.config.title")
public class MoreCauldronsConfigGen {

	@Config.Comment({"General settings"})
	public static General general = new General();
	
	public static class General{
		@Config.Comment("When true the cauldron will drop the liquid that's in if it breaks while full. (when Inspirations isn't installed) [default: false]")
		public boolean liquidDropping = false;
		
		@Config.Comment("When true wooden cauldrons will be set on fire if a hot liquid is inside (when Inspirations is installed) [default: true]")
		public boolean woodBurning = true;
	}
	
	@Mod.EventBusSubscriber(modid = ModReference.MOD_ID)
	private static class EventHandler {

		@SubscribeEvent
		public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
			if (event.getModID().equals(ModReference.MOD_ID)) {
				ConfigManager.sync(ModReference.MOD_ID, Config.Type.INSTANCE);
			}
		}
	}
}
