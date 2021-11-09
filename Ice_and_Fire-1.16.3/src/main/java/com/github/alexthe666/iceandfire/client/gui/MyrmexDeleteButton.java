package com.github.alexthe666.iceandfire.client.gui;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

public class MyrmexDeleteButton extends Button {
    public BlockPos pos;

    public MyrmexDeleteButton(int x, int y, BlockPos pos, ITextComponent delete, Button.IPressable onPress) {
        super(x, y, 50, 20, delete, onPress);
        this.pos = pos;
    }
}
