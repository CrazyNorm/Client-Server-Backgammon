package ai;

public class AIFactory {

    public static AI getAIProfile(String type, String difficulty) {
        return switch (type) {
            case "random" -> new RandomAI();
            case "minimax" -> new MinimaxAI(difficulty);
            default -> null;
        };
    }
}
