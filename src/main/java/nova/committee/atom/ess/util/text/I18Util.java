package nova.committee.atom.ess.util.text;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import nova.committee.atom.ess.Static;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 15:40
 * Version: 1.0
 */
public class I18Util {
    public static final int TITLE_COLOR = 0x404040;

    public static String getTranslationKey(String beforeModid, String afterModid) {
        beforeModid = beforeModid.endsWith(".") ? beforeModid : beforeModid + ".";
        afterModid = afterModid.startsWith(".") ? afterModid : "." + afterModid;
        return beforeModid + Static.MOD_ID + afterModid;
    }

    public static MutableComponent getColoredTextFromI18n(TextColor color, boolean bold, boolean underline, boolean italic, String translationKey, Object... parameters) {
        return new TranslatableComponent(translationKey, parameters)
                .setStyle(Style.EMPTY
                        .withColor(color)
                        .withBold(bold).withUnderlined(underline)
                        .withItalic(italic));
    }

    public static MutableComponent getWhiteTextFromI18n(boolean bold, boolean underline, boolean italic, String translationKey, Object... parameters) {
        return getColoredTextFromI18n(TextColor.fromLegacyFormat(ChatFormatting.WHITE), bold, underline, italic, translationKey, parameters);
    }

    public static MutableComponent getGrayTextFromI18n(boolean bold, boolean underline, boolean italic, String translationKey, Object... parameters) {
        return getColoredTextFromI18n(TextColor.fromLegacyFormat(ChatFormatting.GRAY), bold, underline, italic, translationKey, parameters);
    }

    public static MutableComponent getContainerNameTextFromI18n(boolean bold, boolean underline, boolean italic, String translationKey, Object... parameters) {
        return getColoredTextFromI18n(TextColor.parseColor(String.valueOf(TITLE_COLOR)), bold, underline, italic, translationKey, parameters);
    }

    public static MutableComponent getGreenTextFromI18n(boolean bold, boolean underline, boolean italic, String translationKey, Object... parameters) {
        return getColoredTextFromI18n(TextColor.fromLegacyFormat(ChatFormatting.GREEN), bold, underline, italic, translationKey, parameters);
    }

    public static MutableComponent getRedTextFromI18n(boolean bold, boolean underline, boolean italic, String translationKey, Object... parameters) {
        return getColoredTextFromI18n(TextColor.fromLegacyFormat(ChatFormatting.RED), bold, underline, italic, translationKey, parameters);
    }

    public static MutableComponent getYellowTextFromI18n(boolean bold, boolean underline, boolean italic, String translationKey, Object... parameters) {
        return getColoredTextFromI18n(TextColor.fromLegacyFormat(ChatFormatting.YELLOW), bold, underline, italic, translationKey, parameters);
    }

    public static MutableComponent getColoredTextFromString(TextColor color, boolean bold, boolean underline, boolean italic, String text) {
        return new TextComponent(text)
                .setStyle(Style.EMPTY
                        .withColor(color)
                        .withBold(bold).withUnderlined(underline)
                        .withItalic(italic));
    }

    public static MutableComponent getGreenTextFromString(boolean bold, boolean underline, boolean italic, String text) {
        return getColoredTextFromString(TextColor.fromLegacyFormat(ChatFormatting.GREEN), bold, underline, italic, text);
    }

    public static MutableComponent getYellowTextFromString(boolean bold, boolean underline, boolean italic, String text) {
        return getColoredTextFromString(TextColor.fromLegacyFormat(ChatFormatting.YELLOW), bold, underline, italic, text);
    }

    public static MutableComponent getWhiteTextFromString(boolean bold, boolean underline, boolean italic, String text) {
        return getColoredTextFromString(TextColor.fromLegacyFormat(ChatFormatting.WHITE), bold, underline, italic, text);
    }

    public static String getSeparator(String pattern, int count) {
        return String.valueOf(pattern).repeat(Math.max(0, count));
    }
}
