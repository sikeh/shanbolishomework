package assignment2.sip;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * Date: Apr 19, 2008
 * Time: 9:55:02 PM
 *
 * @author Shanbo Li
 */
public class GenerateTag {
    static Random rnd = new Random();

    /**
     * generate a tag which must be different with fromTag
     *
     * @param fromTag the tag comes with from
     * @return
     */
    public static String getTag(int fromTag) {
        String toTag;

        int iTag = rnd.nextInt(10000);
        while (true) {
            if (iTag > 1000 && iTag != fromTag) {
                break;
            }
            iTag = rnd.nextInt(10000);
        }
        return Integer.toString(iTag);
    }
}
