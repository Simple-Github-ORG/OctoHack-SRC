package me.primooctopus33.octohack.client.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;

public class Command$ChatMessage
extends TextComponentBase {
    private final String text;

    public Command$ChatMessage(String text) {
        Pattern pattern = Pattern.compile("&[0123456789abcdefrlosmk]");
        Matcher matcher = pattern.matcher(text);
        StringBuffer stringBuffer = new StringBuffer();
        while (matcher.find()) {
            String replacement = matcher.group().substring(1);
            matcher.appendReplacement(stringBuffer, replacement);
        }
        matcher.appendTail(stringBuffer);
        this.text = stringBuffer.toString();
    }

    public String getUnformattedComponentText() {
        return this.text;
    }

    public ITextComponent createCopy() {
        return null;
    }

    public ITextComponent shallowCopy() {
        return new Command$ChatMessage(this.text);
    }
}
