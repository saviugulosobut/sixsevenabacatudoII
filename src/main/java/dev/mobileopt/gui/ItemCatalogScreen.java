package dev.mobileopt.gui;

import dev.mobileopt.SpUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ItemCatalogScreen extends SearchListScreen<ItemCatalogScreen.Entry> {
    public record Entry(Item item, ItemStack icon, String id, String name, String key) {}

    private static final int[] AMOUNTS = {0, 1, 16, 64};
    private static int amountIndex = 0;

    public ItemCatalogScreen(Screen parent) {
        super(Text.literal("Catálogo de Itens"), parent);
        List<Entry> list = new ArrayList<>();
        for (Item item : Registries.ITEM) {
            if (item == Items.AIR) continue;
            Identifier id = Registries.ITEM.getId(item);
            ItemStack icon = new ItemStack(item);
            String name = icon.getName().getString();
            String shortId = id.getNamespace().equals("minecraft") ? id.getPath() : id.toString();
            list.add(new Entry(item, icon, shortId, name, (name + " " + id).toLowerCase(Locale.ROOT)));
        }
        list.sort(Comparator.comparing(Entry::id));
        this.all = list;
    }

    @Override
    protected void initExtras(int x, int y, int w) {
        this.addDrawableChild(ButtonWidget.builder(amountLabel(), b -> {
            amountIndex = (amountIndex + 1) % AMOUNTS.length;
            b.setMessage(amountLabel());
        }).dimensions(x, y, w, 20).build());
    }

    private static Text amountLabel() {
        int a = AMOUNTS[amountIndex];
        return Text.literal("Quantidade: " + (a == 0 ? "Máx. da pilha" : String.valueOf(a)));
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
    protected int iconSpace() {
        return 22;
    }

    @Override
    protected void drawIcon(DrawContext context, Entry entry, int x, int y) {
        context.drawItem(entry.icon(), x, y);
    }

    @Override
    protected void onPick(Entry entry) {
        int max = Math.max(1, entry.item().getMaxCount());
        int wanted = AMOUNTS[amountIndex];
        int count = wanted == 0 ? max : Math.min(wanted, max);
        this.status = SpUtil.give(this.client, new ItemStack(entry.item(), count)).message();
    }
}
