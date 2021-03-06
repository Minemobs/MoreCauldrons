package com.mrbysco.morecauldrons.events;

import com.mrbysco.morecauldrons.blocks.inspirations.BlockEnhancedCauldronBase;
import com.mrbysco.morecauldrons.blocks.inspirations.BlockEnhancedWoodenCauldron;
import com.mrbysco.morecauldrons.config.MoreCauldronsConfigGen;
import knightminer.inspirations.common.Config;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.recipe.cauldron.ICauldronRecipe.CauldronState;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron.CauldronContents;
import knightminer.inspirations.recipes.tileentity.TileCauldron;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;

public class EnhancedRecipeEvents {

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void clickCauldron(PlayerInteractEvent.RightClickBlock event) {
		if(!Config.enableCauldronRecipes) {
			return;
		}

		EntityPlayer player = event.getEntityPlayer();
		if(player.isSneaking()) {
			return;
		}

		// basic properties
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		IBlockState state = world.getBlockState(pos);
		if(!(state.getBlock() instanceof BlockEnhancedCauldronBase)) {
			return;
		}
		ItemStack stack = event.getItemStack();
		if(stack.isEmpty()) {
			return;
		}

		boolean result = TileCauldron.interact(world, pos, state, player, event.getHand());
		if(result || InspirationsRegistry.isCauldronBlacklist(stack)) {
			event.setCanceled(true);
			event.setCancellationResult(EnumActionResult.SUCCESS);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void CauldronTickEvent(TickEvent.WorldTickEvent event) {
		if(!MoreCauldronsConfigGen.general.woodBurning) {
			return;
		}

		World world = event.world;
		ArrayList<TileEntity> TileList = new ArrayList<>(world.loadedTileEntityList);
		for (TileEntity tile : TileList)
		{
			if (world.isBlockLoaded(tile.getPos())) {
				if(tile instanceof TileCauldron)
				{
					TileCauldron cauldron = (TileCauldron)tile;
					BlockPos pos = cauldron.getPos();
					if(world.getBlockState(pos).getBlock() instanceof BlockEnhancedWoodenCauldron) {
						if(cauldron.getContentType() == CauldronContents.FLUID)
						{
							CauldronState state = CauldronState.fromNBT(cauldron.serializeNBT().getCompoundTag(cauldron.TAG_STATE));
							if(state.getFluid().getTemperature() >= 450)
							{
								if(world.rand.nextInt(10) < 2)
								{
									if(world.getBlockState(pos.north()).getMaterial() == Material.AIR && world.rand.nextInt(100) < 1)
										world.setBlockState(pos.north(), Blocks.FIRE.getDefaultState(), 3);
									if(world.getBlockState(pos.south()).getMaterial() == Material.AIR && world.rand.nextInt(100) < 1)
										world.setBlockState(pos.south(), Blocks.FIRE.getDefaultState(), 3);
									if(world.getBlockState(pos.west()).getMaterial() == Material.AIR && world.rand.nextInt(100) < 1)
										world.setBlockState(pos.west(), Blocks.FIRE.getDefaultState(), 3);
									if(world.getBlockState(pos.east()).getMaterial() == Material.AIR && world.rand.nextInt(100) < 1)
										world.setBlockState(pos.east(), Blocks.FIRE.getDefaultState(), 3);

									if(MoreCauldronsConfigGen.general.liquidDropping)
									{
										Block fluidBlock = state.getFluid().getBlock();
										if(world.rand.nextInt(100) < 3 && fluidBlock != null)
											world.setBlockState(pos, fluidBlock.getDefaultState(), 6);
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
