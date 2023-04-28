package playingWithProjections;

import java.util.LinkedHashMap;
import java.util.Map;

public class Projections {
    public static class QuizWithPlayedGames {
        private final String quizId;
        private final String quizTitle;
        private final Map<String, Integer> playedGames = new LinkedHashMap<>();

        public QuizWithPlayedGames(Events.QuizWasCreated quizWasCreated) {
            this.quizId = quizWasCreated.getQuizId();
            this.quizTitle = quizWasCreated.getQuizTitle();
        }

        public void enrich(Events.GameWasOpened gameWasOpened) {
            if (quizId.equals(gameWasOpened.getQuizId())) {
                playedGames.put(gameWasOpened.getGameId(), 0);
            }
        }

        public void enrich(Events.GameWasStarted gameWasStarted) {
            playedGames.computeIfPresent(gameWasStarted.getGameId(), (gameId, games) -> games + 1);
        }

        public String getQuizId() {
            return quizId;
        }

        public String getQuizTitle() {
            return quizTitle;
        }

        public Integer getTotalPlayedGames() {
            return playedGames.values().stream().mapToInt(Integer::intValue).sum();
        }
    }
}
