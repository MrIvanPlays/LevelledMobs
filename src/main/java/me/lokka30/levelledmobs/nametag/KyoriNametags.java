package me.lokka30.levelledmobs.nametag;

import me.lokka30.levelledmobs.LevelledMobs;
import me.lokka30.levelledmobs.result.NametagResult;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

/**
 * @author PenalBuffalo (aka stumper66)
 */
public class KyoriNametags {

    public static @NotNull Object generateComponent(
        final @NotNull LivingEntity livingEntity,
        final @NotNull NametagResult nametagResult
    ) {

        final String nametag = nametagResult.getNametagNonNull();
        final String mobKey = livingEntity.getType().translationKey();
        final Definitions def = LevelledMobs.getInstance().getDefinitions();

        // this component holds the component of the mob name and will show the translated name on clients
        net.kyori.adventure.text.Component mobNameComponent;
        if (nametagResult.overriddenName == null){
            if (def.useTranslationComponents){
                mobNameComponent = net.kyori.adventure.text.Component.translatable(mobKey);
            }
            else{
                mobNameComponent = net.kyori.adventure.text.Component.text(livingEntity.getName());
            }
        }
        else{
            mobNameComponent = LegacyComponentSerializer
                    .legacyAmpersand()
                    .deserialize(nametagResult.overriddenName);
        }

        // replace placeholders and set the new death message
        Component result;
        if (def.getUseLegacySerializer()) {
            result = LegacyComponentSerializer
                    .legacyAmpersand()
                    .deserialize(nametag)
                    .replaceText(
                            TextReplacementConfig.builder()
                                    .matchLiteral("{DisplayName}")
                                    .replacement(mobNameComponent).build()
                    );
        }
        else{
            //Utils.logger.info("Using MiniMessage");
            result = def.mm
                    .deserialize(nametag)
                    .replaceText(
                            TextReplacementConfig.builder()
                                    .matchLiteral("{DisplayName}")
                                    .replacement(mobNameComponent).build()
                    );
        }

        // PaperAdventure.asVanilla(kyoriComponent)
        try {
            return def.method_AsVanilla.invoke(def.clazz_PaperAdventure, result);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }

        return ComponentUtils.getEmptyComponent();
    }
}
