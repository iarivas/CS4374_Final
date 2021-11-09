package net.blay09.mods.waystones.mixin;

import net.blay09.mods.waystones.config.WaystonesConfig;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
@Mixin(targets = "net.minecraft.world.level.levelgen.feature.structures.JigsawPlacement$Placer")
public class JigsawPlacementMixin {

    @Final
    @Shadow
    private List<? super PoolElementStructurePiece> pieces;

    @Redirect(method = "tryPlacingChildren(Lnet/minecraft/world/level/levelgen/structure/PoolElementStructurePiece;Lorg/apache/commons/lang3/mutable/MutableObject;IIZLnet/minecraft/world/level/LevelHeightAccessor;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/structures/StructureTemplatePool;getShuffledTemplates(Ljava/util/Random;)Ljava/util/List;"))
    private List<StructurePoolElement> getShuffledTemplates(StructureTemplatePool pool, Random rand) {
        boolean hasWaystone = pieces.stream()
                .map(piece -> ((PoolElementStructurePiece) piece).getElement().toString())
                .anyMatch(pieceName -> pieceName.contains("waystones:") && pieceName.contains("/waystone"));
        boolean forceWaystone = WaystonesConfig.getActive().forceSpawnInVillages();
        if (hasWaystone) {
            return pool.getShuffledTemplates(rand).stream().filter(piece -> {
                String pieceName = piece.toString();
                return !pieceName.contains("waystones:") || !pieceName.contains("/waystone");
            }).collect(Collectors.toList());
        } else if (forceWaystone || Math.random() <= pieces.size() / 200f) {
            StructurePoolElement waystonePiece = null;
            List<StructurePoolElement> original = pool.getShuffledTemplates(rand);
            List<StructurePoolElement> result = new ArrayList<>();
            for (StructurePoolElement piece : original) {
                String pieceName = piece.toString();
                if (pieceName.contains("waystones:") && pieceName.contains("/waystone")) {
                    waystonePiece = piece;
                } else {
                    result.add(piece);
                }
            }
            if (waystonePiece != null) {
                result.add(0, waystonePiece);
            }
            return result;
        }

        return pool.getShuffledTemplates(rand);
    }

}
