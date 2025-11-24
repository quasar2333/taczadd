package com.qdd.taczadd.handler;

import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class KeyBindingHandler {
    public static final KeyMapping GamSetting=new KeyMapping("key.taczadd.gamsetting", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_X,"key.categories.taczadd");
    public static final KeyMapping count40=new KeyMapping("key.taczadd.count40", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Z,"key.categories.taczadd");
    public static final KeyMapping count60=new KeyMapping("key.taczadd.count60", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C,"key.categories.taczadd");
}
