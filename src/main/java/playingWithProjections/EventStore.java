package playingWithProjections;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class EventStore {
    private Consumer<Event>[] projections;

    public EventStore(Consumer<Event> ... projections) {
        this.projections = projections;
    }

    public void replay(String filePath) {
        System.out.printf("reading events from '%s' ...%n", filePath);
        File file = new File(filePath);

        System.out.println("parsing events ...");
        List<Event> events = parse(file);

        System.out.println("replaying events ...");
        for (Event event : events) {
            for (Consumer<Event> projection : projections) {
                projection.accept(event);
            }

        }

    }

    private List<Event> parse(File file) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(file, new TypeReference<List<Event>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
