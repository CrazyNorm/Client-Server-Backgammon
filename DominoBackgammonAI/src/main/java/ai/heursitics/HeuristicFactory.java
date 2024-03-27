package ai.heursitics;

public class HeuristicFactory {

    public static Heuristic getHeuristic(String type) {
        return switch (type) {
            case "aggressive" -> new AggressiveHeuristic();
            case "defensive" -> new DefensiveHeuristic();
            case "balanced" -> null;  // todo: balance aggression & defence
            case "annoying" -> new AnnoyingHeuristic();
            default -> null;
        };
    }
}
