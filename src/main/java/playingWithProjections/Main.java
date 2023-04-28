package playingWithProjections;

public class Main {
    public static void main(String[] args) {
        String file = FilePathFrom(args);
        int result = getResult(file, new CountRegisteredPlayers());
        System.out.printf("Result: %d%n", result);
    }

    private static int getResult(String file, Projector projector) {
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

    private static class CountEvents implements Projector {
        private int counter = 0;

        public int getResult() {
            return counter;
        }

        public void projection(Event event) {
            counter++;
        }
    }

    private static class CountRegisteredPlayers implements Projector {
        private int counter = 0;

        public int getResult() {
            return counter;
        }

        public void projection(Event event) {
            if ("PlayerHasRegistered".equals(event.getType())) {
                counter++;
            }
        }
    }
}
