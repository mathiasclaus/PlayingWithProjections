package playingWithProjections;

public class Events {
    public static class QuizWasCreated {
        private final String quizTitle;
        private final String quizId;

        public QuizWasCreated(String quizTitle, String quizId) {
            this.quizTitle = quizTitle;
            this.quizId = quizId;
        }

        public String getQuizTitle() {
            return quizTitle;
        }

        public String getQuizId() {
            return quizId;
        }
    }

    public static class GameWasOpened {
        private final String quizId;
        private final String gameId;

        public GameWasOpened(String quizId, String gameId) {
            this.quizId = quizId;
            this.gameId = gameId;
        }

        public String getQuizId() {
            return quizId;
        }

        public String getGameId() {
            return gameId;
        }
    }

    public static class GameWasStarted {
        private final String gameId;

        public GameWasStarted(String gameId) {
            this.gameId = gameId;
        }

        public String getGameId() {
            return gameId;
        }
    }
}
