package playingWithProjections;

public class Main {
    public static void main(String[] args) {
        String file = FilePathFrom(args);
        int result = getResult(file, new CountRegisteredPlayers());
        System.out.printf("Result: %d%n", result);
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
}
