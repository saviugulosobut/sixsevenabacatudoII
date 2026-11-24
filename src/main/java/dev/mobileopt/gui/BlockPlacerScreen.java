package dev.mobileopt.gui;

import dev.mobileopt.SpUtil;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class BlockPlacerScreen extends SearchListScreen<BlockPlacerScreen.Entry> {
    public record Entry(Block block, String id, String name, String key) {}

    private static boolean onlyNoItem = true;
    private static boolean adjacent = true;
    private static boolean closeAfter = true;

    public BlockPlacerScreen(Screen parent) {
        super(Text.literal("Blocos Técnicos (mira)"), parent);
        reload();
    }

    private void reload() {
        List<Entry> list = new ArrayList<>();
        for (Block block : Registries.BLOCK) {
            if (onlyNoItem && block.asItem() != Items.AIR) continue;
            Identifier id = Registries.BLOCK.getId(block);
            String name = block.getName().getString();
            String shortId = id.getNamespace().equals("minecraft") ? id.getPath() : id.toString();
            list.add(new Entry(block, shortId, name, (name + " " + id).toLowerCase(Locale.ROOT)));
        }
        list.sort(Comparator.comparing(Entry::id));
        this.all = list;
    }

    @Override
    protected void initExtras(int x, int y, int w) {
        int bw = (w - 8) / 3;
        this.addDrawableChild(ButtonWidget.builder(filterLabel(), b -> {
            onlyNoItem = !onlyNoItem;
            b.setMessage(filterLabel());
            reload();
            refresh();
        }).dimensions(x, y, bw, 20).build());

        this.addDrawableChild(ButtonWidget.builder(posLabel(), b -> {
            adjacent = !adjacent;
            b.setMessage(posLabel());
        }).dimensions(x + bw + 4, y, bw, 20).build());

        this.addDrawableChild(ButtonWidget.builder(closeLabel(), b -> {
            closeAfter = !closeAfter;
            b.setMessage(closeLabel());
        }).dimensions(x + 2 * (bw + 4), y, bw, 20).build());
    }

    private static Text filterLabel() {
        return Text.literal("Filtro: " + (onlyNoItem ? "Sem item" : "Todos"));
    }

    private static Text posLabel() {
        return Text.literal("Local: " + (adjacent ? "Adjacente" : "Exato"));
    }

    private static Text closeLabel() {
        return Text.literal("Fechar: " + (closeAfter ? "SIM" : "NÃO"));
    }

    @Override
    protected String searchKey(Entry entry) {
        return entry.key();
    }

    @Override
    protected Text rowLabel(Entry entry) {
        return Text.literal(entry.name() + " §7" + entry.id());
    }

    @Override
    protected void onPick(Entry entry) {
        SpUtil.Result result = SpUtil.placeBlock(this.client, entry.block(), adjacent);
        this.status = result.message();
        if (result.ok() && closeAfter) {
            this.client.setScreen(null);
        }
    }
      }
