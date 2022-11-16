package io.github.arcaneplugins.levelledmobs.bukkit.logic.function.process.action.impl.setlevel;

import io.github.arcaneplugins.levelledmobs.bukkit.LevelledMobs;
import io.github.arcaneplugins.levelledmobs.bukkit.api.data.EntityDataUtil;
import io.github.arcaneplugins.levelledmobs.bukkit.data.InternalEntityDataUtil;
import io.github.arcaneplugins.levelledmobs.bukkit.logic.LogicHandler;
import io.github.arcaneplugins.levelledmobs.bukkit.logic.context.Context;
import io.github.arcaneplugins.levelledmobs.bukkit.logic.function.process.Process;
import io.github.arcaneplugins.levelledmobs.bukkit.logic.function.process.action.Action;
import io.github.arcaneplugins.levelledmobs.bukkit.logic.function.process.action.impl.setlevel.inheritance.DifferingFormulaResolveType;
import io.github.arcaneplugins.levelledmobs.bukkit.logic.levelling.LevelTuple;
import io.github.arcaneplugins.levelledmobs.bukkit.logic.levelling.strategy.LevellingStrategy;
import io.github.arcaneplugins.levelledmobs.bukkit.logic.levelling.strategy.LevellingStrategyRequestEvent;
import io.github.arcaneplugins.levelledmobs.bukkit.util.StringUtils;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;

public class SetLevelAction extends Action {

    /* vars */

    private final String formula;
    private final Set<LevellingStrategy> strategies = new HashSet<>();

    private final boolean useInheritanceIfAvailable;
    private final String inheritanceBreedingFormula;
    private final String inheritanceTransformationFormula;

    /* constructors */

    public SetLevelAction(
        @NotNull Process parentProcess,
        @NotNull CommentedConfigurationNode actionNode
    ) {
        super(parentProcess, actionNode);

        this.formula = getActionNode().node("formula")
            .getString("no-level");

        this.useInheritanceIfAvailable = getActionNode()
            .node("inheritance", "use-if-available")
            .getBoolean(false);

        this.inheritanceBreedingFormula = getActionNode()
            .node("inheritance", "breeding", "formula")
            .getString("(%father-level% + %mother-level%) / 2");

        this.inheritanceTransformationFormula = getActionNode()
            .node("inheritance", "transformation", "formula")
            .getString("%mother-level%");

        /*
        Here we want to call out for all known levelling strategies to be registered to the
        SetLevelAction.
         */

        // Iterate through each strategyId specified under the strategies section
        for(Entry<Object, CommentedConfigurationNode> strategyNodeEntry : getActionNode()
            .node("strategies")
            .childrenMap().entrySet()
        ) {
            final CommentedConfigurationNode strategyNode = strategyNodeEntry.getValue();

            if(strategyNodeEntry.getKey() == null) {
                throw new IllegalArgumentException("Strategy keys must not be null");
            }

            if(!(strategyNodeEntry.getKey() instanceof String strategyId)) {
                throw new IllegalArgumentException("Strategy keys must be of type String");
            }

            // fire LevellingStrategyRequestEvent
            final LevellingStrategyRequestEvent stratReqEvent =
                new LevellingStrategyRequestEvent(strategyId, strategyNode);

            Bukkit.getPluginManager().callEvent(stratReqEvent);

            if(stratReqEvent.isCancelled()) {
                continue;
            }

            // add all strategies from the events
            getStrategies().addAll(stratReqEvent.getStrategies());
        }

        if(!getFormula().equalsIgnoreCase("no-level") && getStrategies().size() == 0) {
            throw new IllegalArgumentException(
                "SetLevelAction requres at least 1 levelling strategy, unless specifying " +
                    "no-level. " +
                    "For a simple context-based formula, you can use the Basic Levelling Strategy."
            );
        }
    }

    /* methods */

    @Override
    public void run(Context context) {
        Objects.requireNonNull(context, "context");

        if(context.getEntity() == null) {
            throw new IllegalStateException("Requires entity context; missing.");
        }

        if(!(context.getEntity() instanceof final LivingEntity lent)) {
            throw new IllegalStateException("Entity context is not a type of LivingEntity.");
        }

        if (EntityDataUtil.isLevelled(lent, true)) {
            // Looks like the mob was previously levelled, so we need to remove most of the LM stuff
            InternalEntityDataUtil.unlevelMob(lent);
        }

        LevelTuple result;

        result = generateInheritedLevels(context);
        if(result == null) result = generateStandardLevels(context);

        // no level = remove it if it exists
        if(result == null) {
            InternalEntityDataUtil.unlevelMob(lent);
            return;
        }

        InternalEntityDataUtil.setMinLevel(lent, result.getMinLevel(), true);
        InternalEntityDataUtil.setLevel(lent, result.getLevel(), true);
        InternalEntityDataUtil.setMaxLevel(lent, result.getMaxLevel(), true);

        // apply inheritance formulas to (parent) entity.
        if(useInheritanceIfAvailable()) {
            InternalEntityDataUtil.setInheritanceBreedingFormula(lent,
                getInheritanceBreedingFormula(), true);
            InternalEntityDataUtil.setInheritanceTransformationFormula(lent,
                getInheritanceTransformationFormula(), true);
        }
    }

    private @Nullable LevelTuple generateStandardLevels(Context context) {
        return processFormula(context);
    }

    private @Nullable LevelTuple generateInheritedLevels(Context context) {
        if(!useInheritanceIfAvailable()) return null;

        final LivingEntity lent = Objects.requireNonNull(
            (LivingEntity) context.getEntity(),
            "LivingEntity"
        );

        final @Nullable LivingEntity father = EntityDataUtil.getFather(lent, false);
        final @Nullable LivingEntity mother = EntityDataUtil.getMother(lent, false);

        /*
        Entity Breeding Level Inheritance
         */
        if(Boolean.TRUE.equals(EntityDataUtil.wasBred(lent, true))) {


            if(father == null || mother == null) return null;
            context
                .withFather(father)
                .withMother(mother);

            final String fatherFormula = StringUtils.emptyIfNull(
                EntityDataUtil.getInheritanceBreedingFormula(father, true)
            );
            final String motherFormula = StringUtils.emptyIfNull(
                EntityDataUtil.getInheritanceBreedingFormula(mother, true)
            );

            // skip if both are null
            if(fatherFormula.isBlank() && motherFormula.isBlank()) {
                return null;
            }

            // skip if both formulas are 'no-level'
            if(fatherFormula.equalsIgnoreCase("no-level") &&
                motherFormula.equalsIgnoreCase("no-level")
            ) {
                return null;
            }

            final Function<String, Integer> levelEvaluator = (formula) -> {
                if(formula.isBlank() || formula.equalsIgnoreCase("no-level"))
                    return getMinPossibleLevel();

                try {
                    return (int) Math.floor(
                        LogicHandler.evaluateExpression(
                            LogicHandler.replacePapiAndContextPlaceholders(formula, context)
                        )
                    );
                } catch(Exception ex) {
                    throw new RuntimeException(ex);
                }
            };

            final int fatherInheritedLevel = levelEvaluator.apply(fatherFormula);
            final int motherInheritedLevel = levelEvaluator.apply(motherFormula);

            final int minLevel;
            final @Nullable Integer fatherMinLevel = EntityDataUtil
                .getMinLevel(father, true);
            final @Nullable Integer motherMinLevel = EntityDataUtil
                .getMinLevel(mother, true);

            final int maxLevel;
            final @Nullable Integer fatherMaxLevel = EntityDataUtil
                .getMaxLevel(father, true);
            final @Nullable Integer motherMaxLevel = EntityDataUtil
                .getMaxLevel(mother, true);

            if(fatherMinLevel == null && motherMinLevel == null) {
                return null;
            } else if(fatherMinLevel != null && motherMinLevel != null) {
                minLevel = Math.min(fatherMinLevel, motherMinLevel);
            } else {
                minLevel = Objects.requireNonNullElse(fatherMinLevel, motherMinLevel);
            }

            if(fatherMaxLevel == null && motherMaxLevel == null) {
                return null;
            } else if(fatherMaxLevel != null && motherMaxLevel != null) {
                maxLevel = Math.min(fatherMaxLevel, motherMaxLevel);
            } else {
                maxLevel = Objects.requireNonNullElse(fatherMaxLevel, motherMaxLevel);
            }

            // resolve differing formulas
            if(!fatherFormula.equalsIgnoreCase(motherFormula)) {
                return switch(DifferingFormulaResolveType.getFromAdvancedSettings()) {
                    case USE_AVERAGE -> new LevelTuple(
                        minLevel,
                        (fatherInheritedLevel + motherInheritedLevel) / 2,
                        maxLevel
                    );
                    case USE_RANDOM -> new LevelTuple(
                        minLevel,
                        ThreadLocalRandom.current().nextBoolean() ?
                            fatherInheritedLevel : motherInheritedLevel,
                        maxLevel
                    );
                    case USE_NEITHER -> null;
                };
            }

            // yes, we are ignoring fatherLevel since it should be the same
            return new LevelTuple(minLevel, motherInheritedLevel, maxLevel);

        }

        /*
        Entity Transformation Level Inheritance
         */
        if(Boolean.TRUE.equals(EntityDataUtil.wasTransformed(lent, true))) {

            // during transformation, mother == father. we only check for one.
            if(mother == null) return null;

            // yes: it is intentional the father is the same as the mother during transformation.
            context
                .withFather(mother)
                .withMother(mother);

            if(!EntityDataUtil.isLevelled(mother, true)) return null;

            final String formula = StringUtils.emptyIfNull(EntityDataUtil
                .getInheritanceTransformationFormula(mother, true));

            if(formula.isBlank() || formula.equalsIgnoreCase("no-level")) {
                return null;
            }

            //noinspection ConstantConditions
            return new LevelTuple(
                EntityDataUtil.getMinLevel(father, true),

                (int) Math.floor(
                    LogicHandler.evaluateExpression(
                        LogicHandler.replacePapiAndContextPlaceholders(formula, context)
                    )
                ),

                EntityDataUtil.getMaxLevel(father, true)
            );
        }

        /*
        Passenger/Vehicle Level Inheritance
         */
        Entity vehicleEntity = lent;
        while(lent.isInsideVehicle()) {
            if(vehicleEntity instanceof LivingEntity vehicleLentity) {
                if(EntityDataUtil.isLevelled(vehicleLentity, true)) {
                    //noinspection ConstantConditions
                    return new LevelTuple(
                        EntityDataUtil.getMinLevel(vehicleLentity, true),
                        EntityDataUtil.getLevel(vehicleLentity, true),
                        EntityDataUtil.getMaxLevel(vehicleLentity, true)
                    );
                }
            }

            if(!vehicleEntity.isInsideVehicle()) continue;

            vehicleEntity = Objects.requireNonNull(vehicleEntity.getVehicle(), "vehicle");
        }

        // No level could be inherited, so return null.
        return null;
    }

    /**
     * TODO document.
     *
     * @param context TODO doc
     * @return TODO doc
     */
    @Nullable
    public LevelTuple processFormula(final @NotNull Context context) {
        Objects.requireNonNull(context, "context");

        // check if the mob should have no level
        if(getFormula().equalsIgnoreCase("no-level")) {
            // remember:    null = no level
            return null;
        }

        // replace context placeholders in the formula
        String formula = LogicHandler.replacePapiAndContextPlaceholders(getFormula(), context);

        int minLevel = getMinPossibleLevel();
        int maxLevel = getMinPossibleLevel();

        // replace levelling strategy placeholders in the formula
        for(final LevellingStrategy strategy : getStrategies()) {
            formula = strategy.replaceInFormula(formula, context);

            minLevel = Math.min(minLevel, strategy.getMinLevel());
            maxLevel = Math.max(maxLevel, strategy.getMaxLevel());
        }

        if(maxLevel < minLevel) maxLevel = minLevel;

        // evaluate the formula with Crunch
        final int levelEval = (int) Math.round(LogicHandler.evaluateExpression(formula));

        // finally, ensure the evaluated level is between the min and max levels.
        final int level = Math.min(Math.max(levelEval, minLevel), maxLevel);

        return new LevelTuple(minLevel, level, maxLevel);
    }

    /* getters and setters */

    public Set<LevellingStrategy> getStrategies() {
        return strategies;
    }

    // TODO let's move this into a more accessible area - SettingsCfg class?
    public static int getMinPossibleLevel() {
        // we don't want negative values as they create undefined game behaviour
        return Math.max(0, LevelledMobs.getInstance()
            .getConfigHandler().getSettingsCfg()
            .getRoot().node("advanced", "minimum-level").getInt(1)
        );
    }

    @NotNull
    public String getFormula() {
        return formula;
    }

    public boolean useInheritanceIfAvailable() {
        return useInheritanceIfAvailable;
    }

    public String getInheritanceTransformationFormula() {
        return inheritanceTransformationFormula;
    }

    public String getInheritanceBreedingFormula() {
        return inheritanceBreedingFormula;
    }
}
