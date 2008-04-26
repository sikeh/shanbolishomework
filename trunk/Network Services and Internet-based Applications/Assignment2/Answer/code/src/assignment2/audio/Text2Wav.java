package assignment2.audio;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.FreeTTS;
import com.sun.speech.freetts.InputMode;

/**
 * User: Shanbo Li
 * Date: Apr 13, 2008
 * Time: 10:15:09 AM
 *
 * @author Shanbo Li
 */
public class Text2Wav {
    public static void main(String[] args) {
        String text = "This is the default message";
        String outFile = "default.wav";
        text2wav(text, outFile);
    }

    public static void text2wav(String text, String outFile) {
        Voice voice;
        VoiceManager voiceManager = VoiceManager.getInstance();
        voice = voiceManager.getVoice("kevin16");
        if (voice == null) {
            throw new Error("The specified voice is not defined");
        }
        FreeTTS freetts = new FreeTTS(voice);
        freetts.setAudioFile(outFile);
        freetts.setInputMode(InputMode.TEXT);
        freetts.startup();
        freetts.textToSpeech(text);
        freetts.shutdown();
    }
}
