package net.littlebai.bluearchivebeacon.client.screen;

import net.minecraft.client.gui.screens.Screen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

public class BaBeaconConfigurationScreen implements IConfigScreenFactory {

    @Override
    public Screen createScreen(net.neoforged.fml.ModContainer modContainer, Screen parent) {
        return new net.neoforged.neoforge.client.gui.ConfigurationScreen(
                modContainer,
                parent
        );
    }
} 