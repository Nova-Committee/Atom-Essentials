package nova.committee.atom.ess.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/21 22:07
 * Version: 1.0
 */
public class RewardUtil {
    public static boolean isStringItem(String name) {
        Pattern pattern = Pattern.compile("(\\s+):(\\s+)");
        Matcher matcher = pattern.matcher(name);
        return matcher.find();
    }
}
