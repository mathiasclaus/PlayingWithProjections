package playingWithProjections;

import static java.util.Comparator.comparing;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        String file = FilePathFrom(args);
        String result = getFormattedResult(file, new MostPopularQuizzes());
        System.out.printf(result);
    }

    private static String getFormattedResult(String file, Projector projector) {
        new EventStore(projector::projection)
            .replay(file);

        return projector.getFormattedResult();
    }

    private static String FilePathFrom(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("Please specify a file to replay");
        }
        return args[0];
    }

    private static class CountEvents implements Projector {
        private int counter = 0;

        public String getFormattedResult() {
            return String.format("number of events: %d%n", counter);
        }

        public void projection(Event event) {
            counter++;
        }
    }

    private static class CountRegisteredPlayers implements Projector {
        private int counter = 0;

        public String getFormattedResult() {
            return String.format("number of registered players: %d%n", counter);
        }

        public void projection(Event event) {
            if ("PlayerHasRegistered".equals(event.getType())) {
                counter++;
            }
        }
    }

    private static class CountRegisteredPlayersPerMonth implements Projector {
        private final Map<String, Integer> numberOfPlayersRegisteredPerMonth = new LinkedHashMap<>();

        public String getFormattedResult() {
            return numberOfPlayersRegisteredPerMonth.entrySet().stream()
                .map((entry) -> String.format("%s: %d", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("%n"));
        }

        public void projection(Event event) {
            if ("PlayerHasRegistered".equals(event.getType())) {
                String monthOfYear = String.format("%s-%s", event.getTimestamp().getYear(), event.getTimestamp().getMonthValue());
                numberOfPlayersRegisteredPerMonth.compute(monthOfYear, (month, playersRegistered) -> playersRegistered == null ? 1 : playersRegistered + 1);
            }
        }
    }

    private static class MostPopularQuizzes implements Projector {
        private final List<Projections.QuizWithPlayedGames> quizWithPlayedGamesProjections = new ArrayList<>();

        public String getFormattedResult() {
            List<Projections.QuizWithPlayedGames> sortedByTotalPlayedGames = quizWithPlayedGamesProjections.stream()
                .sorted(comparing(Projections.QuizWithPlayedGames::getTotalPlayedGames).reversed())
                .collect(Collectors.toList());
            return sortedByTotalPlayedGames.stream()
                .limit(10)
                .map(projection -> String.format("quiz id: %s, quiz title: %s, number of players: %d%n", projection.getQuizId(), projection.getQuizTitle(),
                    projection.getTotalPlayedGames()))
                .collect(Collectors.joining("%n"));
        }

        public void projection(Event event) {
            if ("QuizWasCreated".equals(event.getType())) {
                Events.QuizWasCreated quizWasCreated = toQuizWasCreated(event);
                quizWithPlayedGamesProjections.add(toQuizWithPlayesGames(quizWasCreated));
            }
            if ("GameWasOpened".equals(event.getType())) {
                Events.GameWasOpened gameWasOpened = toGameWasOpened(event);
                quizWithPlayedGamesProjections.forEach(projection -> projection.enrich(gameWasOpened));
            }
            if ("GameWasStarted".equals(event.getType())) {
                Events.GameWasStarted gameWasStarted = toGameWasStarted(event);
                quizWithPlayedGamesProjections.forEach(projection -> projection.enrich(gameWasStarted));
            }
        }

        private static Projections.QuizWithPlayedGames toQuizWithPlayesGames(Events.QuizWasCreated quizWasCreated) {
            return new Projections.QuizWithPlayedGames(quizWasCreated);
        }

        private Events.QuizWasCreated toQuizWasCreated(Event event) {
            return new Events.QuizWasCreated(
                event.getPayload().get("quiz_title"),
                event.getPayload().get("quiz_id")
            );
        }

        private Events.GameWasOpened toGameWasOpened(Event event) {
            return new Events.GameWasOpened(
                event.getPayload().get("quiz_id"),
                event.getPayload().get("game_id")
            );
        }

        private Events.GameWasStarted toGameWasStarted(Event event) {
            return new Events.GameWasStarted(
                event.getPayload().get("game_id")
            );
        }
    }
}
