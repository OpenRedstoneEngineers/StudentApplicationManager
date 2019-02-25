package org.stonecipher;

import org.bukkit.entity.Player;

public class Applicant {
    private Player player;
    private int progress;

    public Applicant(Player p) {
        this.player = p;
        this.progress = 0;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void incrementProgress() {
        this.progress++;
    }

    public boolean evaluate(Question.Answer answer, Question q) {
        if (q.getActionRequired().equals(answer)) {
            return true;
        } else if (q.getActionRequired().equals(Question.Answer.EITHER)) {
            return true;
        }
        return false;
    }

    public int getProgress() {
        return this.progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
