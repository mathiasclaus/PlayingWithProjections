package playingWithProjections;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        String file = FilePathFrom(args);
        String result = getFormattedResult(file, new CountRegisteredPlayersPerMonth());
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
                .map((entry) -> String.format("%s: %d", entry.getKey(),entry.getValue()))
                .collect(Collectors.joining("%n"));
        }

        public void projection(Event event) {
            if ("PlayerHasRegistered".equals(event.getType())) {
                String monthOfYear = String.format("%s-%s", event.getTimestamp().getYear(), event.getTimestamp().getMonthValue());
                numberOfPlayersRegisteredPerMonth.compute(monthOfYear, (month, playersRegistered) -> playersRegistered == null ? 1 : playersRegistered + 1);
            }
        }
    }
}
