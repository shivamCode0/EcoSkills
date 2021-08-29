package com.willfp.ecoskills;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.command.impl.PluginCommand;
import com.willfp.eco.core.integrations.IntegrationLoader;
import com.willfp.ecoskills.commands.CommandEcoskills;
import com.willfp.ecoskills.commands.CommandSkills;
import com.willfp.ecoskills.data.PlayerBlockListener;
import com.willfp.ecoskills.data.SaveHandler;
import com.willfp.ecoskills.data.DataListener;
import com.willfp.ecoskills.config.DataYml;
import com.willfp.ecoskills.config.EffectsYml;
import com.willfp.ecoskills.data.LeaderboardHandler;
import com.willfp.ecoskills.effects.Effect;
import com.willfp.ecoskills.effects.Effects;
import com.willfp.ecoskills.integrations.EcoEnchantsEnchantingLeveller;
import com.willfp.ecoskills.skills.Skill;
import com.willfp.ecoskills.skills.SkillDisplayListener;
import com.willfp.ecoskills.skills.SkillLevellingListener;
import com.willfp.ecoskills.skills.Skills;
import com.willfp.ecoskills.stats.DamageIndicatorListener;
import com.willfp.ecoskills.stats.Stat;
import com.willfp.ecoskills.stats.Stats;
import com.willfp.ecoskills.stats.modifier.StatModifierListener;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class EcoSkillsPlugin extends EcoPlugin {
    /**
     * Instance of EcoSkills.
     */
    private static EcoSkillsPlugin instance;

    /**
     * data.yml.
     */
    private final DataYml dataYml;

    /**
     * effects.yml.
     */
    private final EffectsYml effectsYml;

    /**
     * Internal constructor called by bukkit on plugin load.
     */
    public EcoSkillsPlugin() {
        super(0, 12205, "&#ff00ae");
        instance = this;
        dataYml = new DataYml(this);
        effectsYml = new EffectsYml(this);
    }

    @Override
    protected void handleReload() {
        for (Effect effect : Effects.values()) {
            this.getEventManager().unregisterListener(effect);
            this.getEventManager().registerListener(effect);
        }
        for (Stat stat : Stats.values()) {
            this.getEventManager().unregisterListener(stat);
            this.getEventManager().registerListener(stat);
        }
        for (Skill skill : Skills.values()) {
            this.getEventManager().unregisterListener(skill);
            this.getEventManager().registerListener(skill);
        }
        SaveHandler.Companion.save(this);

        this.getScheduler().runTimer(new SaveHandler.Runnable(this), 20000, 20000);
        this.getScheduler().runTimer(new LeaderboardHandler.Runnable(), 50, 2400);
    }

    @Override
    protected void handleDisable() {
        try {
            dataYml.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get data.yml.
     *
     * @return data.yml.
     */
    public DataYml getDataYml() {
        return dataYml;
    }

    /**
     * Get effects.yml.
     *
     * @return effects.yml.
     */
    public EffectsYml getEffectsYml() {
        return effectsYml;
    }

    /**
     * Get the instance of EcoSkills.
     *
     * @return Instance.
     */
    public static EcoSkillsPlugin getInstance() {
        return instance;
    }

    @Override
    protected List<Listener> loadListeners() {
        return Arrays.asList(
                new SkillLevellingListener(),
                new SkillDisplayListener(this),
                new StatModifierListener(),
                new DataListener(),
                new PlayerBlockListener(this),
                new EcoSkillsEventModifierHandler(this)
        );
    }

    @Override
    protected List<PluginCommand> loadPluginCommands() {
        return Arrays.asList(
                new CommandEcoskills(this),
                new CommandSkills(this)
        );
    }

    @Override
    protected List<IntegrationLoader> loadIntegrationLoaders() {
        return Arrays.asList(
                new IntegrationLoader("HolographicDisplays", () -> this.getEventManager().registerListener(new DamageIndicatorListener(this))),
                new IntegrationLoader("EcoEnchants", () -> this.getEventManager().registerListener(new EcoEnchantsEnchantingLeveller(this)))
        );
    }
}
