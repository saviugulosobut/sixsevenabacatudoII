package dev.mobileopt;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public final class SpUtil {
    public record Result(boolean ok, Text message) {}

    private static final Result NOT_SP = new Result(false, Text.literal("§cSó funciona em Singleplayer."));

    private SpUtil() {}

    private static boolean unavailable(MinecraftClient client) {
        return client == null
                || client.player == null
                || client.world == null
                || !client.isInSingleplayer()
                || client.getServer() == null;
    }

    public static Result give(MinecraftClient client, ItemStack stack) {
        if (unavailable(client)) return NOT_SP;
        IntegratedServer server = client.getServer();
        UUID uuid = client.player.getUuid();
        ItemStack copy = stack.copy();
        server.execute(() -> {
            ServerPlayerEntity sp = server.getPlayerManager().getPlayer(uuid);
            if (sp != null) {
                sp.getInventory().offerOrDrop(copy);
            }
        });
        return new Result(true, Text.literal("§aEntregue: " + stack.getCount() + "x " + stack.getName().getString()));
    }

    public static Result duplicateMainHand(MinecraftClient client) {
        if (unavailable(client)) return NOT_SP;
        ItemStack held = client.player.getMainHandStack();
        if (held.isEmpty()) {
            return new Result(false, Text.literal("§eSegure um item na mão primeiro."));
        }
        ItemStack copy = held.copyWithCount(64);
        IntegratedServer server = client.getServer();
        UUID uuid = client.player.getUuid();
        server.execute(() -> {
            ServerPlayerEntity sp = server.getPlayerManager().getPlayer(uuid);
            if (sp != null) {
                sp.getInventory().offerOrDrop(copy);
            }
        });
        return new Result(true, Text.literal("§aDuplicado: 64x " + held.getName().getString()));
    }

    public static Result placeBlock(MinecraftClient client, Block block, boolean adjacent) {
        if (unavailable(client)) return NOT_SP;
        BlockHitResult hit = findTarget(client);
        if (hit == null) {
            return new Result(false, Text.literal("§eMire em um bloco antes de abrir o menu."));
        }
        BlockPos pos = adjacent ? hit.getBlockPos().offset(hit.getSide()) : hit.getBlockPos();
        BlockState state = block.getDefaultState();
        var dimension = client.world.getRegistryKey();
        IntegratedServer server = client.getServer();
        server.execute(() -> {
            ServerWorld world = server.getWorld(dimension);
            if (world != null && !world.isOutOfHeightLimit(pos)) {
                world.setBlockState(pos, state, Block.NOTIFY_ALL);
            }
        });
        return new Result(true, Text.literal("§aColocado: " + block.getName().getString()
                + " §7(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")"));
    }

    private static BlockHitResult findTarget(MinecraftClient client) {
        HitResult crosshair = client.crosshairTarget;
        if (crosshair instanceof BlockHitResult near && near.getType() == HitResult.Type.BLOCK) {
            return near;
        }
        if (client.player != null) {
            HitResult far = client.player.raycast(48.0, 1.0F, false);
            if (far instanceof BlockHitResult farHit && farHit.getType() == HitResult.Type.BLOCK) {
                return farHit;
            }
        }
        return null;
    }
          }
