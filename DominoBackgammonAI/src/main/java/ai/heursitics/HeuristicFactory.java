package ai.heursitics;

public class HeuristicFactory {

    public static Heuristic getHeuristic(String type) {
        return switch (type) {
            case "aggressive" -> new AggressiveHeuristic();
            case "defensive" -> null; // todo
            default -> null;
        };
    }
}
