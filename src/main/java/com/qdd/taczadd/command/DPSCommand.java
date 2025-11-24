package com.qdd.taczadd.command;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.CommandDispatcher;
import com.qdd.taczadd.handler.DPSScoreHandler;

public class DPSCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("dps")
                        .requires(source -> source.hasPermission(4)) // 需要OP权限
                        .then(Commands.literal("start")
                                .executes(ctx -> {
                                    DPSScoreHandler.Show(ctx.getSource().getServer());
                                    return 1;
                                }))
                        .then(Commands.literal("stop")
                                .executes(ctx -> {
                                    DPSScoreHandler.Hide(ctx.getSource().getServer());
                                    return 1;
                                }))
        );
    }
}
