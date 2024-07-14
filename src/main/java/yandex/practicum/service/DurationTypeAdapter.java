package yandex.practicum.service;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;

public class DurationTypeAdapter extends TypeAdapter<Duration> {
    private static final Pattern inputRegex = Pattern
            .compile("(-?)(?:(?:([0-9]+):)?([0-9]+):)?([0-9]+)(?:\\.([0-9]+))?");

    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        if (isNull(duration)) {
            jsonWriter.nullValue();
            return;
        }
        long total = duration.getSeconds();
        String neg = total < 0 ? "-" : "";
        if (total < 0)
            total = -total;
        long millis = total % 1000;
        total /= 1000;
        long seconds = total % 60;
        total /= 60;
        long minutes = total % 60;
        total /= 60;
        long hours = total;
        jsonWriter.value(String.format("%s%02d:%02d:%02d.%03d000", neg, hours, minutes, seconds, millis));
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {

        String input = jsonReader.nextString();
        Matcher m = inputRegex.matcher(input);
        while (m.matches()) {
            long hours = m.group(2) != null ? Long.parseLong(m.group(2)) : 0;
            long minutes = m.group(3) != null ? Long.parseLong(m.group(3)) : 0;
            long seconds = m.group(4) != null ? Long.parseLong(m.group(4)) : 0;

            String millisecondsSource = m.group(5) != null ? m.group(5) : "000";
            while (millisecondsSource.length() < 3)
                millisecondsSource += '0';
            millisecondsSource = millisecondsSource.substring(0, 3);
            long millis = Long.parseLong(millisecondsSource);

            long millisTotal = ((hours * 60 + minutes) * 60 + seconds) * 1000 + millis;

            if (m.group(1).equals("-"))
                millisTotal = -millisTotal;

            return Duration.ofMillis(millisTotal);
        }
        throw new RuntimeException("Could not extract duration from \"" + input + "\"");
    }
}
