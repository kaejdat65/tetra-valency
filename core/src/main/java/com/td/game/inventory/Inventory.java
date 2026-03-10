package com.td.game.inventory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.td.game.elements.Element;

/**
 * Sadece orb'lar için envanter sistemi - Değişmez Slotlu
 * Orblar boşalan ilk slota girer.
 */
public class Inventory implements Disposable {

    private static final int MAX_ORBS = 6;
    private static final int COLS = 3;
    private static final int ROWS = 2;

    // Slotlar - sabit boyutlu dizi
    private Element[] slots;
    private int selectedIndex;
    private int unlockedSlots;

    private float x, y;
    private float slotSize;
    private float padding;

    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;

    public Inventory(float x, float y) {
        this.x = x;
        this.y = y;
        this.slotSize = 50f;
        this.padding = 5f;

        this.slots = new Element[MAX_ORBS];
        this.selectedIndex = -1;
        this.unlockedSlots = 3;

        this.shapeRenderer = new ShapeRenderer();
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.font.getData().setScale(0.8f);
    }

    public boolean addOrb(Element element) {
        if (element == null)
            return false;

        for (int i = 0; i < unlockedSlots; i++) {
            if (slots[i] == null) {
                slots[i] = element;
                return true;
            }
        }
        return false;
    }

    public boolean isFull() {
        for (int i = 0; i < unlockedSlots; i++) {
            Element e = slots[i];
            if (e == null)
                return false;
        }
        return true;
    }

    public Element removeOrbAt(int index) {
        if (index < 0 || index >= unlockedSlots)
            return null;
        Element e = slots[index];
        slots[index] = null;
        return e;
    }

    public boolean hasSelection() {
        return selectedIndex >= 0 && selectedIndex < unlockedSlots && slots[selectedIndex] != null;
    }

    public Element getSelectedOrb() {
        if (!hasSelection())
            return null;
        return slots[selectedIndex];
    }

    public Element takeSelected() {
        if (!hasSelection())
            return null;
        Element e = slots[selectedIndex];
        slots[selectedIndex] = null;
        selectedIndex = -1;
        return e;
    }

    public void cancelSelection() {
        selectedIndex = -1;
    }

    public void clear() {
        for (int i = 0; i < MAX_ORBS; i++) {
            slots[i] = null;
        }
        selectedIndex = -1;
    }

    public void handleClick(int screenX, int screenY) {
        int index = getSlotAt(screenX, screenY);
        if (index >= 0 && index < unlockedSlots) {
            if (slots[index] != null) {
                selectedIndex = index;
            } else {
                selectedIndex = -1;
            }
        } else {
            selectedIndex = -1;
        }
    }

    public int getSlotAt(float screenX, float screenY) {
        float totalW = COLS * (slotSize + padding);
        float totalH = ROWS * (slotSize + padding);

        if (screenX < x || screenX > x + totalW)
            return -1;
        if (screenY < y || screenY > y + totalH)
            return -1;

        int col = (int) ((screenX - x) / (slotSize + padding));
        int rowFromBottom = (int) ((screenY - y) / (slotSize + padding));
        int row = (ROWS - 1) - rowFromBottom;

        if (col < 0 || col >= COLS || row < 0 || row >= ROWS)
            return -1;

        int index = row * COLS + col;
        return index < unlockedSlots ? index : -1;
    }

    public int getEmptySlotCount() {
        int count = 0;
        for (int i = 0; i < unlockedSlots; i++) {
            Element e = slots[i];
            if (e == null)
                count++;
        }
        return count;
    }

    public void render() {
        float totalW = COLS * (slotSize + padding);
        float totalH = ROWS * (slotSize + padding);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.1f, 0.12f, 0.15f, 0.9f);
        shapeRenderer.rect(x - 5, y - 5, totalW + 10, totalH + 30);
        shapeRenderer.end();

        batch.begin();
        font.setColor(Color.GOLD);
        font.draw(batch, "INVENTORY", x, y + totalH + 20);
        batch.end();

        // Slotlar
        for (int i = 0; i < MAX_ORBS; i++) {
            int col = i % COLS;
            int row = i / COLS;
            float slotX = x + col * (slotSize + padding);
            float slotY = y + row * (slotSize + padding);
            boolean isLocked = i >= unlockedSlots;

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            // Slot arka planı
            if (isLocked) {
                shapeRenderer.setColor(0.07f, 0.07f, 0.1f, 0.8f);
            } else if (i == selectedIndex) {
                shapeRenderer.setColor(0.4f, 0.6f, 0.3f, 0.9f); // Seçili (yeşilimsi)
            } else {
                shapeRenderer.setColor(0.15f, 0.15f, 0.18f, 0.7f); // Boş/Dolu standart
            }
            shapeRenderer.rect(slotX, slotY, slotSize, slotSize);

            // Orb varsa çiz
            if (!isLocked && slots[i] != null) {
                Element e = slots[i];
                shapeRenderer.setColor(e.getR(), e.getG(), e.getB(), 1f);
                float orbSize = slotSize * 0.7f;
                shapeRenderer.circle(slotX + slotSize / 2, slotY + slotSize / 2, orbSize / 2);
            }

            shapeRenderer.end();

            // Slot çerçevesi
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(isLocked ? 0.25f : 0.4f, isLocked ? 0.25f : 0.4f, isLocked ? 0.3f : 0.5f, 1f);
            shapeRenderer.rect(slotX, slotY, slotSize, slotSize);
            shapeRenderer.end();

            if (isLocked) {
                batch.begin();
                font.setColor(Color.DARK_GRAY);
                font.draw(batch, "LOCK", slotX + 6, slotY + slotSize / 2 + 5);
                batch.end();
            }
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return COLS * (slotSize + padding);
    }

    public float getHeight() {
        return ROWS * (slotSize + padding);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void resize(int width, int height) {
        float scale = Math.min(width / 1920f, height / 1080f);
        slotSize = 70 * scale;
        if (slotSize < 52)
            slotSize = 52;
        padding = 8 * scale;
        if (padding < 6)
            padding = 6;

        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
        shapeRenderer.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    }

    public void unlockSlots(int amount) {
        if (amount <= 0)
            return;
        unlockedSlots += amount;
        if (unlockedSlots > MAX_ORBS)
            unlockedSlots = MAX_ORBS;
    }

    public int getUnlockedSlots() {
        return unlockedSlots;
    }

    public Element getOrbAt(int index) {
        if (index < 0 || index >= unlockedSlots) {
            return null;
        }
        return slots[index];
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public float getSlotSize() {
        return slotSize;
    }

    public float getPadding() {
        return padding;
    }

    public void save(com.td.game.systems.SaveData data) {
        data.inventoryOrbs.clear();
        for (int i = 0; i < MAX_ORBS; i++) {
            if (slots[i] != null) {
                data.inventoryOrbs.add(slots[i].name());
            } else {
                data.inventoryOrbs.add(null);
            }
        }
        data.unlockedInventorySlots = this.unlockedSlots;
    }

    public void load(com.td.game.systems.SaveData data) {
        this.clear();
        this.unlockedSlots = data.unlockedInventorySlots;
        if (this.unlockedSlots == 0) {
            this.unlockedSlots = 3; 
        }

        for (int i = 0; i < MAX_ORBS && i < data.inventoryOrbs.size; i++) {
            String orbName = data.inventoryOrbs.get(i);
            if (orbName != null && !orbName.isEmpty()) {
                slots[i] = Element.valueOf(orbName);
            } else {
                slots[i] = null;
            }
        }
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
    }
}
