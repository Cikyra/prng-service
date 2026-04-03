package org.example;
import java.io.IOException;
import java.nio.file.*;
import java.util.Random;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main(String[] args) throws IOException, InterruptedException{

        Path filePath = Path.of("C:\\Users\\larla\\OneDrive\\Desktop\\Spring26\\CS361\\Week 1\\prng-service.txt");
        Path directory = filePath.getParent();

        if(directory == null){
            throw new IllegalArgumentException("File must have a parent directory.");
        }

        System.out.println("Watching...");

        changeFile(filePath);

        try(WatchService watcher = FileSystems.getDefault().newWatchService()){
            directory.register(watcher,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);

            while(true) { //always watching...
                WatchKey key = watcher.take();

                for(WatchEvent<?> event : key.pollEvents()){
                    WatchEvent.Kind<?> eventType = event.kind();

                    if(eventType == StandardWatchEventKinds.OVERFLOW){
                        continue;
                    }

                    Path change = (Path) event.context();

                    if (change != null && change.equals(filePath.getFileName())){
                        System.out.println("File changed detected.");

                        if(eventType == StandardWatchEventKinds.ENTRY_DELETE){
                            System.out.println("File was deleted. Terminating program.");
                            return;
                        }else{
                            changeFile(filePath);
                        }

                    }
                }

                boolean valid = key.reset();
                if(!valid){
                    System.out.println("Watch key is no longer valid");
                    break;
                }
            }
        }



    }

    private static void changeFile(Path filePath) {
        try{
            String line = Files.readString(filePath);

            if(line.equalsIgnoreCase("run")){
                Random random = new Random();

                int val = random.nextInt(101);

                Files.writeString(filePath, String.valueOf(val));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
