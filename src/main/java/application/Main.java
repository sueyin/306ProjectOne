package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import model.DotFileAdapter;
import model.Node;
import model.Notification;
import model.Scheduler;

public class Main {
    //GUI
    //extends Application {

    //By default the visualisation option is not enabled.
    private static boolean _visualisation = false;
    //By default the output file is INPUT_output.dot.
    private static String _outputFile = "_output.dot";
    //By default the execution run sequentially on one core.
    private static int _noOfCores = 1;

    /* No GUI for basic milestone
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../view/sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }
    */

    public static void main(String[] args) {
        if (args.length < 2){
            Notification.message("Error: Please specify the filename and numbers of processors to use");
            System.exit(1);
        }else if (args.length > 7){
            Notification.message("Error: Too many arguments");
            System.exit(1);
        }

        try {
            //Read input file
            String filepath = args[0];
            checkFile(filepath);
            DotFileAdapter reader = new DotFileAdapter(filepath);
            List<Node> graph = reader.getData();

            //Modify output filename
            _outputFile = filepath.substring(0, filepath.length() - 4) + _outputFile;

            //Read number of processors.
            int numberOfProcessor = Integer.parseInt(args[1]);

            //Read optional arguments
            ReadOptionalArgs(args);
            /* No GUI for basic milestone
            if (_visualisation){
                launch(args);
            }
            */
            //No multithreading for basic milestone

            //Run Scheduler to calculate the schedule.
            Scheduler schedule = new Scheduler(graph, numberOfProcessor);
            //schedule.schedule();

            //Write result
            reader.writeSchedule(schedule.getSchedule(), _outputFile);

        }catch(NumberFormatException e){
            Notification.message("Error: second argument must be an integer");
            System.exit(1);
        }catch(FileNotFoundException e){
            Notification.message("Error: File does not exist");
            System.exit(1);
        } catch (IOException e) {
            Notification.message("Error: IO Exception");
            System.exit(1);
        }

    }

    /**
     * Checks if the user input optional arguments are valid
     * @param args arguments that the user inputted in command line
     */
    private static void ReadOptionalArgs(String[] args){
        for (int i = 2; i < args.length; i++){
            //If -p is specified, read how many cores are supplied.
            if (args[i].equals("-p")){
                try {
                    _noOfCores = Integer.parseInt(args[i+1]);
                    i++;
                }catch(NumberFormatException e){
                    Notification.message("Error: Argument after -p must be an integer");
                    System.exit(1);
                }catch(ArrayIndexOutOfBoundsException e1){
                    Notification.message("Error: Please enter an integer after -p");
                    System.exit(1);
                }

            //If -o is specified, read the specified output filename
            }else if(args[i].equals("-o")) {
                try {
                    if (args[i+1].equals("-v")||args[i+1].equals("-p")){
                        Notification.message("Error: filename for output is not valid please choose another name");
                        System.exit(1);
                    }
                    _outputFile = args[i + 1] + ".dot";
                    i++;
                } catch (ArrayIndexOutOfBoundsException e1) {
                    Notification.message("Error: Please enter a filename after -o");
                    System.exit(1);
                }

            //If -v is specified, mark _visualisation as true.
            }else if (args[i].equals("-v")){
                _visualisation = true;

            //Display error message and help for invalid inputs
            }else{
                Notification.invalidInput();
                System.exit(1);
            }
        }
    }

    /**
     * checks if the file has valid type(.dot file) and exists in the directory
     */
    private static void checkFile(String filepath){
        if (!filepath.endsWith(".dot")){
            Notification.message("Error: input file has wrong suffix");
            System.exit(1);
        }

        File file = new File(filepath);
        boolean fileExists = file.exists();
        if (!fileExists){
            Notification.message("Error: File does not exist");
            System.exit(1);
        }
    }
}
