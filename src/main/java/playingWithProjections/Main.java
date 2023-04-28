package playingWithProjections;

import java.util.LinkedHashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String file = FilePathFrom(args);
        Map<String, Integer> result = getResult(file, new CountRegisteredPlayersPerMonth());
        result
            .forEach((month, playersRegistered) ->
                System.out.printf("%s: %d%n", month, playersRegistered)
            );
    }

    private static <T> T getResult(String file, Projector<T> projector) {
        new EventStore(projector::projection)
            .replay(file);

        return projector.getResult();
    }

    private static String FilePathFrom(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("Please specify a file to replay");
        }
        return args[0];
    }

    private static class CountEvents implements Projector<Integer> {
        private int counter = 0;

        public Integer getResult() {
            return counter;
        }

        public void projection(Event event) {
            counter++;
        }
    }

    private static class CountRegisteredPlayers implements Projector<Integer> {
        private int counter = 0;

        public Integer getResult() {
            return counter;
        }

        public void projection(Event event) {
            if ("PlayerHasRegistered".equals(event.getType())) {
                counter++;
            }
        }
    }

    private static class CountRegisteredPlayersPerMonth implements Projector<Map<String, Integer>> {
        private Map<String, Integer> numberOfPlayersRegisteredPerMonth = new LinkedHashMap<>();

        public Map<String, Integer> getResult() {
            return numberOfPlayersRegisteredPerMonth;
        }

        public void projection(Event event) {
            if ("PlayerHasRegistered".equals(event.getType())) {
                String monthOfYear = String.format("%s-%s", event.getTimestamp().getYear(), event.getTimestamp().getMonthValue());
                numberOfPlayersRegisteredPerMonth.compute(monthOfYear, (month, playersRegistered) -> playersRegistered == null ? 1 : playersRegistered + 1);
            }
        }
    }
}
