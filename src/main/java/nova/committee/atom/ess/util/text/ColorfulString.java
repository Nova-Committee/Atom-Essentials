package nova.committee.atom.ess.util.text;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 14:15
 * Version: 1.0
 */
public class ColorfulString {
    public static final char COLOR_CHAR = '§';

    private final MutableComponent text = new TextComponent("");

    public ColorfulString(String rawString) {
        this(Collections.singletonList(rawString));
    }

    public ColorfulString(List<? extends String> rawStrings) {
        for (String raw : rawStrings) {
            TextComponent formatted = new TextComponent("");
            char[] chars = raw.toCharArray();
            for (int j = 0; j < chars.length; j++) {
                char c = chars[j];
                List<ChatFormatting> formatters = new ArrayList<>(5);
                // Get formatters
                while (c == '&' || c == COLOR_CHAR) {
                    formatters.add(fromFormattingCode(chars[j + 1]));
                    j += 2;
                    c = chars[j];
                }
                // Format raw
                int index = j;
                do {
                    index++;
                    if (index == chars.length) break;
                    c = chars[index];
                } while (c != '&' && c != COLOR_CHAR);
                formatted.append(new TextComponent(raw.substring(j, index)).withStyle(this.toArray(formatters)));
                j = index - 1;
            }
            this.text.append(formatted);
            if (rawStrings.size() > 1) {
                this.text.append("\n");
            }
        }
    }

    private ChatFormatting[] toArray(List<ChatFormatting> target) {
        ChatFormatting[] result = new ChatFormatting[target.size()];
        for (int i = 0; i < target.size(); i++) {
            result[i] = target.get(i);
        }
        return result;
    }

    public MutableComponent getText() {
        return this.text;
    }

    // Sorry, I dont know the convenient way to get ChatFormatting from formatting code,
    // because that method is private.
    public static ChatFormatting fromFormattingCode(char formattingCode) {
        char c = Character.toString(formattingCode).toLowerCase(Locale.ROOT).charAt(0);
        return switch (c) {
            case '0' -> ChatFormatting.BLACK;
            case '1' -> ChatFormatting.DARK_BLUE;
            case '2' -> ChatFormatting.DARK_GREEN;
            case '3' -> ChatFormatting.DARK_AQUA;
            case '4' -> ChatFormatting.DARK_RED;
            case '5' -> ChatFormatting.DARK_PURPLE;
            case '6' -> ChatFormatting.GOLD;
            case '7' -> ChatFormatting.GRAY;
            case '8' -> ChatFormatting.DARK_GRAY;
            case '9' -> ChatFormatting.BLUE;
            case 'a' -> ChatFormatting.GREEN;
            case 'b' -> ChatFormatting.AQUA;
            case 'c' -> ChatFormatting.RED;
            case 'd' -> ChatFormatting.LIGHT_PURPLE;
            case 'e' -> ChatFormatting.YELLOW;
            case 'f' -> ChatFormatting.WHITE;
            case 'k' -> ChatFormatting.OBFUSCATED;
            case 'l' -> ChatFormatting.BOLD;
            case 'm' -> ChatFormatting.STRIKETHROUGH;
            case 'n' -> ChatFormatting.UNDERLINE;
            case 'o' -> ChatFormatting.ITALIC;
            case 'r' -> ChatFormatting.RESET;
            default -> null;
        };
    }
}
