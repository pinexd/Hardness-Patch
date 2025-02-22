package piners.hardnesspatch.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import piners.hardnesspatch.HardnessPatch;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class HardnessMixin {
    @Inject(
            method = "getHardness(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F",
            at = @At("HEAD"),
            cancellable = true
    )
    private void modifyHardness(BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        BlockState state = (BlockState) (Object) this;
        Block block = state.getBlock();

        Float hardness = HardnessPatch.customHardnessMap.get(block);
        if (hardness != null) {
            cir.setReturnValue(hardness);
        }
    }
}