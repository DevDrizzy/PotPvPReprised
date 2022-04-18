package net.frozenorb.potpvp.profile.elo;

import lombok.Getter;

public final class EloCalculator {

    private final double kPower;
    private final int minEloGain;
    private final int maxEloGain;
    private final int minEloLoss;
    private final int maxEloLoss;

    public EloCalculator(double kPower, int minEloGain, int maxEloGain, int minEloLoss, int maxEloLoss) {
        this.kPower = kPower;
        this.minEloGain = minEloGain;
        this.maxEloGain = maxEloGain;
        this.minEloLoss = minEloLoss;
        this.maxEloLoss = maxEloLoss;
    }

    public Result calculate(int winnerElo, int loserElo) {
        double winnerQ = Math.pow(10, ((double) winnerElo) / 300D);
        double loserQ = Math.pow(10, ((double) loserElo) / 300D);

        double winnerE = winnerQ / (winnerQ + loserQ);
        double loserE = loserQ / (winnerQ + loserQ);

        int winnerGain = (int) (kPower * (1 - winnerE));
        int loserGain = (int) (kPower * (0 - loserE));

        winnerGain = Math.min(winnerGain, maxEloGain);
        winnerGain = Math.max(winnerGain, minEloGain);

        // loserGain will be negative so pay close attention here
        loserGain = Math.min(loserGain, -minEloLoss);
        loserGain = Math.max(loserGain, -maxEloLoss);

        return new Result(winnerElo, winnerGain, loserElo, loserGain);
    }

    public static class Result {

        @Getter private final int winnerOld;
        @Getter private final int winnerGain;
        @Getter private final int winnerNew;

        @Getter private final int loserOld;
        @Getter private final int loserGain;
        @Getter private final int loserNew;

        Result(int winnerOld, int winnerGain, int loserOld, int loserGain) {
            this.winnerOld = winnerOld;
            this.winnerGain = winnerGain;
            this.winnerNew = winnerOld + winnerGain;

            this.loserOld = loserOld;
            this.loserGain = loserGain;
            this.loserNew = loserOld + loserGain;
        }

    }

}