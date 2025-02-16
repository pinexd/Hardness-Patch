package piners.hardnesspatch.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import piners.hardnesspatch.HardnessPatchClient;

@Mixin(AbstractBlock.AbstractBlockState.class)
@Environment(EnvType.CLIENT)
public abstract class HardnessClientMixin {
    @Inject(
            method = "getHardness(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F",
            at = @At("HEAD"),
            cancellable = true
    )
    private void modifyClientHardness(BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        BlockState state = (BlockState) (Object) this;
        float hardness = HardnessPatchClient.getAdjustedHardness(state.getBlock());
        if (hardness != state.getBlock().getHardness()) {
            cir.setReturnValue(hardness);
        }
    }
}