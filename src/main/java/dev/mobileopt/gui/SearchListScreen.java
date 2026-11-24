package dev.mobileopt.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class SearchListScreen<T> extends Screen {
    protected final Screen parent;
    protected List<T> all = new ArrayList<>();
    protected List<T> filtered = new ArrayList<>();
    protected String query = "";
    protected Text status = Text.empty();
    protected int page = 0;

    private final List<ButtonWidget> rowButtons = new ArrayList<>();
    private final List<T> rowItems = new ArrayList<>();
    private ButtonWidget prevButton;
    private ButtonWidget nextButton;
    private int listLeft;
    private int listWidth;
    private int listTop;
    private int perPage;

    protected SearchListScreen(Text title, Screen parent) {
        super(title);
        this.parent = parent;
    }

    protected abstract String searchKey(T entry);

    protected abstract Text rowLabel(T entry);

    protected abstract void onPick(T entry);

    protected void initExtras(int x, int y, int w) {}

    protected int iconSpace() {
        return 0;
    }

    protected void drawIcon(DrawContext context, T entry, int x, int y) {}

    @Override
    protected void init() {
        rowButtons.clear();
        rowItems.clear();

        listWidth = Math.min(this.width - 16, 340);
        listLeft = (this.width - listWidth) / 2;
        listTop = 70;
        perPage = Math.max(2, (this.height - listTop - 58) / 22);

        TextFieldWidget search = new TextFieldWidget(this.textRenderer, listLeft, 20, listWidth, 20, Text.literal("Buscar"));
        search.setMaxLength(48);
        search.setPlaceholder(Text.literal("Buscar (nome ou id)..."));
        search.setText(query);
        search.setChangedListener(s -> {
            query = s;
            refresh();
        });
        this.addDrawableChild(search);

        initExtras(listLeft, 44, listWidth);

        prevButton = this.addDrawableChild(ButtonWidget.builder(Text.literal("< Ant."), b -> {
            page--;
            rebuildRows();
        }).dimensions(listLeft, this.height - 24, 70, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Voltar"), b -> close())
                .dimensions(this.width / 2 - 45, this.height - 24, 90, 20).build());

        nextButton = this.addDrawableChild(ButtonWidget.builder(Text.literal("Próx. >"), b -> {
            page++;
            rebuildRows();
        }).dimensions(listLeft + listWidth - 70, this.height - 24, 70, 20).build());

        applyFilter();
        rebuildRows();
    }

    protected void refresh() {
        applyFilter();
        page = 0;
        rebuildRows();
    }

    private void applyFilter() {
        String q = query.trim().toLowerCase(Locale.ROOT);
        if (q.isEmpty()) {
            filtered = all;
            return;
        }
        List<T> out = new ArrayList<>();
        for (T entry : all) {
            if (searchKey(entry).contains(q)) {
                out.add(entry);
            }
        }
        filtered = out;
    }

    private int maxPage() {
        return Math.max(0, (filtered.size() - 1) / perPage);
    }

    private void rebuildRows() {
        for (ButtonWidget b : rowButtons) {
            this.remove(b);
        }
        rowButtons.clear();
        rowItems.clear();

        int max = maxPage();
        page = Math.max(0, Math.min(page, max));
        int start = page * perPage;
        int bx = listLeft + iconSpace();
        int bw = listWidth - iconSpace();

        for (int i = 0; i < perPage && start + i < filtered.size(); i++) {
            T entry = filtered.get(start + i);
            ButtonWidget b = ButtonWidget.builder(rowLabel(entry), btn -> onPick(entry))
                    .dimensions(bx, listTop + i * 22, bw, 20).build();
            rowButtons.add(b);
            rowItems.add(entry);
            this.addDrawableChild(b);
        }
        prevButton.active = page > 0;
        nextButton.active = page < max;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 6, 0xFFFFFF);

        for (int i = 0; i < rowButtons.size(); i++) {
            drawIcon(context, rowItems.get(i), listLeft + 2, rowButtons.get(i).getY() + 2);
        }

        String info = "Pág. " + (page + 1) + "/" + (maxPage() + 1) + " · " + filtered.size() + " resultados";
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(info), this.width / 2, this.height - 46, 0xAAAAAA);
        context.drawCenteredTextWithShadow(this.textRenderer, status, this.width / 2, this.height - 35, 0xFFFFFF);
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }
}
