package net.flytre.flytre_lib.api.base.game;

import com.google.common.collect.ImmutableList;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ServerScoreboard;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.*;

/**
 * An API used to display custom sidebars that keeps the sidebar neatly formatted
 */
public class SidebarDisplayer {

    private final ServerScoreboard scoreboard;
    private final ScoreboardObjective objective;
    private final List<Entry> entries;
    private transient int entrySize;

    public SidebarDisplayer(ServerScoreboard scoreboard, ScoreboardObjective objective, List<Entry> entries) {
        this.entries = entries;
        this.entrySize = calculateEntrySize();
        this.objective = objective;
        this.scoreboard = scoreboard;
    }

    public SidebarDisplayer(ServerScoreboard scoreboard, ScoreboardObjective objective) {
        List<ScoreboardPlayerScore> scores = scoreboard
                .getAllPlayerScores(objective)
                .stream()
                .filter(i -> i.getScore() >= 0 && i.getScore() <= 15)
                .sorted(Comparator.comparing(ScoreboardPlayerScore::getScore))
                .toList();

        this.scoreboard = scoreboard;
        this.objective = objective;
        this.entries = new ArrayList<>();
        this.entrySize = 0;

        for (int i = 0; i < scores.size(); i++) {
            ScoreboardPlayerScore score = scores.get(i);
            String text = score.getPlayerName();
            if (text.replace("§r", "").length() == 0) {
                addEntry(new BlankEntry());
            } else if (i + 1 < scores.size() && NumberUtils.isCreatable(scores.get(i + 1).getPlayerName())) {
                double next = Double.parseDouble(scores.get(i++ + 1).getPlayerName());
                addEntry(new ValueEntry(text, next));
            } else {
                addEntry(new TextEntry(text));
            }
        }
    }

    public ServerScoreboard getScoreboard() {
        return scoreboard;
    }

    private int calculateEntrySize() {
        return entries.stream().map(Entry::getHeight).reduce(0, Integer::sum);
    }


    public void addEntry(Entry entry) {
        if (entrySize + entry.getHeight() > 16)
            return; //silent return
        entries.add(entry);
        entrySize += entry.getHeight();
    }

    public void clearEntries() {
        entries.clear();
        entrySize = 0;
    }


    public void update() {

        Map<String, Integer> usedScores = new HashMap<>();
        Set<String> usedScores2 = new HashSet<>();

        int score = 15;
        for (Entry entry : entries) {
            for (String line : entry.getText()) {
                usedScores.put(line, usedScores.getOrDefault(line, 0) + 1);
                line += " ".repeat(usedScores.get(line) - 1);
                usedScores2.add(line);
                scoreboard.getPlayerScore(line, objective).setScore(score--);
            }
        }

        scoreboard.getAllPlayerScores(objective).forEach(i -> {
            if (!usedScores2.contains(i.getPlayerName()))
                scoreboard.resetPlayerScore(i.getPlayerName(), objective);
        });
    }

    public ImmutableList<Entry> getEntries() {
        return ImmutableList.copyOf(entries);
    }

    public static class Entry {
        protected final int height;
        protected List<String> text;

        public Entry(List<String> text, int height) {
            this.text = text;
            this.height = height;
        }

        public List<String> getText() {
            return text;
        }

        public int getHeight() {
            return height;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "height=" + height +
                    ", text=" + text +
                    '}';
        }
    }

    public static final class TextEntry extends Entry {

        public TextEntry(String text) {
            super(Collections.singletonList(text), 1);
        }
    }

    public static final class ValueEntry extends Entry {


        private double value;

        public ValueEntry(String text, double value) {
            super(List.of(text, valueFor(value)), 2);
            this.value = value;
        }

        public static String valueFor(double x) {
            if (x == Math.rint(x)) {
                return "" + (int) x;
            }
            return "" + x;
        }

        public void setValue(double value) {
            if (this.value != value) {
                this.text = List.of(text.get(0), valueFor(value));
                this.value = value;
            }
        }
    }

    public static final class BlankEntry extends Entry {

        public BlankEntry() {
            super(List.of(""), 1);
        }
    }
}
