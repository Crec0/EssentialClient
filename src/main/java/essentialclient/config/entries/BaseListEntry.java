package essentialclient.config.entries;

import carpet.settings.ParsedRule;
import com.google.common.collect.ImmutableList;
import essentialclient.config.ConfigListWidget;
import essentialclient.config.clientrule.ClientRule;
import essentialclient.config.rulescreen.RulesScreen;
import essentialclient.utils.render.ITooltipEntry;
import essentialclient.utils.render.RenderHelper;
import essentialclient.utils.render.RuleWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

public abstract class BaseListEntry extends ConfigListWidget.Entry implements ITooltipEntry {
    protected ParsedRule<?> parsedRule = null;
    protected ClientRule<?> clientRule = null;
    protected final String ruleName;
    protected final RulesScreen rulesScreen;
    protected final MinecraftClient client;
    protected RuleWidget ruleWidget;
    protected ButtonWidget editButton;
    protected ButtonWidget resetButton;


    public BaseListEntry(final ParsedRule<?> parsedRule, MinecraftClient client, RulesScreen gui) {
        this.parsedRule = parsedRule;
        this.client = client;
        this.rulesScreen = gui;
        this.ruleName = parsedRule.name;
    }

    public BaseListEntry(final ClientRule<?> clientRule, MinecraftClient client, RulesScreen gui) {
        this.clientRule = clientRule;
        this.client = client;
        this.rulesScreen = gui;
        this.ruleName = clientRule.getName();
    }

    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovering, float delta) {
        TextRenderer font = client.textRenderer;
        float fontX = (float)(x + 90 - ConfigListWidget.length);
        float fontY = (float)(y + height / 2 - 9 / 2);

        this.ruleWidget = new RuleWidget(this.ruleName, x - 50, y + 2, 200, 15);
        this.ruleWidget.drawRule(matrices, font, fontX, fontY, 16777215);

        this.resetButton.x = x + 290;
        this.resetButton.y = y;

        this.resetButton.active = this.parsedRule == null ? this.clientRule.isNotDefault() : !this.parsedRule.getAsString().equals(this.parsedRule.defaultAsString);

        this.editButton.x = x + 180;
        this.editButton.y = y;

        this.editButton.render(matrices, mouseX, mouseY, delta);
        this.resetButton.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void drawTooltip(int slotIndex, int x, int y, int mouseX, int mouseY, int listWidth, int listHeight, int slotWidth, int slotHeight, float partialTicks) {
        if (this.ruleWidget != null && y > 45 && y < listHeight - 50 && this.ruleWidget.isHovered(mouseX, mouseY) ) {
            String description = this.parsedRule == null ? this.clientRule.getDescription() : this.parsedRule.description;
            RenderHelper.drawGuiInfoBox(this.client.textRenderer, description, mouseX, mouseY);
        }
    }

    @Override
    public List<? extends Element> children() {
        return ImmutableList.of(this.editButton, this.resetButton);
    }
}
