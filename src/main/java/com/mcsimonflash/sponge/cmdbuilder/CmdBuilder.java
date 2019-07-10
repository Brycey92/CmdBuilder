package com.mcsimonflash.sponge.cmdbuilder;

import com.google.inject.Inject;
import com.mcsimonflash.sponge.cmdbuilder.command.GetMeta;
import com.mcsimonflash.sponge.cmdbuilder.command.SetMeta;
import com.mcsimonflash.sponge.cmdbuilder.internal.Scripts;
import com.mcsimonflash.sponge.cmdbuilder.internal.Util;
import com.mcsimonflash.sponge.cmdbuilder.type.*;
import com.mcsimonflash.sponge.cmdcontrol.core.CmdPlugin;
import com.mcsimonflash.sponge.cmdcontrol.teslalibs.registry.Registry;
import com.mcsimonflash.sponge.cmdcontrol.teslalibs.registry.RegistryService;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import java.util.Optional;

@Plugin(id = "cmdbuilder", name = "CmdBuilder", version = "1.3.0", dependencies = @Dependency(id="cmdcontrol"), url = "https://ore.spongepowered.org/Simon_Flash/CmdBuilder", authors = "Simon_Flash")
public class CmdBuilder extends CmdPlugin {

    private static CmdBuilder instance;
    private static final Registry<ParserType> PARSER_REGISTRY = Registry.of();
    private static final Registry<ValueType> VALUE_REGISTRY = Registry.of();
    public static final RegistryService<ParserType> PARSER_TYPES = RegistryService.of(PARSER_REGISTRY);
    public static final RegistryService<ValueType> VALUE_TYPES = RegistryService.of(VALUE_REGISTRY);

    @Inject
    private Logger logger;

    @Inject
    public CmdBuilder(PluginContainer container) {
        super(container);
        instance = this;
        registerParserType(ParserTypes.BOOLEAN, container);
        registerParserType(ParserTypes.CHOICES, container);
        registerParserType(ParserTypes.DOUBLE, container);
        registerParserType(ParserTypes.INTEGER, container);
        registerParserType(ParserTypes.ITEM, container);
        registerParserType(ParserTypes.JOINED_STRINGS, container);
        registerParserType(ParserTypes.PLAYER, container);
        registerParserType(ParserTypes.SOURCE, container);
        registerParserType(ParserTypes.STRING, container);
        registerParserType(ParserTypes.TRISTATE, container);
        registerParserType(ParserTypes.USER, container);
        registerParserType(ParserTypes.POSITION, container);
        registerParserType(ParserTypes.WORLD, container);
        registerValueType(ValueTypes.BOOLEAN, container);
        registerValueType(ValueTypes.DOUBLE, container);
        registerValueType(ValueTypes.INTEGER, container);
        registerValueType(ValueTypes.ITEM, container);
        registerValueType(ValueTypes.PLAYER, container);
        registerValueType(ValueTypes.SOURCE, container);
        registerValueType(ValueTypes.STRING, container);
        registerValueType(ValueTypes.TRISTATE, container);
        registerValueType(ValueTypes.USER, container);
        registerValueType(ValueTypes.UUID, container);
        registerValueType(ValueTypes.VECTOR_3D, container);
        registerValueType(ValueTypes.WORLD, container);
        OptionalTypes.initialize();
    }

    @Listener
    public void onInit(GameInitializationEvent event) {
        getCommands().register(GetMeta.class);
        getCommands().register(SetMeta.class);
        Util.initialize();
    }

    @Listener
    public void onReload(GameReloadEvent event) {
        Util.initialize();
    }

    @Listener
    public void onSendCommand(SendCommandEvent event, @Root CommandSource src) {
        long time = System.currentTimeMillis();
        src.sendMessage(Text.of("[" + time + "] " + "event.getCommand() = \"" + event.getCommand() + "\", event.getArguments() = \"" + event.getArguments() + "\""));

        Optional<CommandResult> result = Scripts.process(src, event.getCommand() + (event.getArguments().isEmpty() ? "" : " " + event.getArguments()), time);
        if (result.isPresent()) {
            event.setCancelled(true);
            event.setResult(result.get());
        }
    }

    public static CmdBuilder get() {
        return instance;
    }

    public static boolean registerParserType(ParserType type, PluginContainer container) {
        return PARSER_REGISTRY.register(type.getName(), type, container);
    }

    public static boolean registerValueType(ValueType type, PluginContainer container) {
        return VALUE_REGISTRY.register(type.getName(), type, container);
    }

    public static void log(String msg) {
        instance.logger.warn(msg);
    }

}