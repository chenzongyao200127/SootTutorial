package dev.navids.soottutorial;

import dev.navids.soottutorial.android.AndroidClassInjector;
import dev.navids.soottutorial.android.AndroidLogger;
import dev.navids.soottutorial.android.AndroidCallgraph;
import dev.navids.soottutorial.android.AndroidPointsToAnalysis;
import dev.navids.soottutorial.basicapi.BasicAPI;
import dev.navids.soottutorial.hellosoot.HelloSoot;
import dev.navids.soottutorial.intraanalysis.npanalysis.NPAMain;
import dev.navids.soottutorial.intraanalysis.usagefinder.UsageFinder;

import java.util.Arrays;

public class Main {
    public static void main(String[] args){
        if (args.length == 0){
            System.err.println("You must provide the name of the Java class file that you want to run.");
            return;
        }
        String[] restOfTheArgs = Arrays.copyOfRange(args, 1, args.length);
        switch (args[0]) {
            case "HelloSoot":
                HelloSoot.main(restOfTheArgs);
                break;
            case "BasicAPI":
                BasicAPI.main(restOfTheArgs);
                break;
            case "AndroidLogger":
                AndroidLogger.main(restOfTheArgs);
                break;
            case "AndroidClassInjector":
                AndroidClassInjector.main(restOfTheArgs);
                break;
            case "AndroidCallGraph":
                AndroidCallgraph.main(restOfTheArgs);
                break;
            case "AndroidPTA":
                AndroidPointsToAnalysis.main(restOfTheArgs);
                break;
            case "UsageFinder":
                UsageFinder.main(restOfTheArgs);
                break;
            case "NullPointerAnalysis":
                NPAMain.main(restOfTheArgs);
                break;
            default:
                System.err.println("The class '" + args[0] + "' does not exists or does not have a main method.");
                break;
        }
    }
}
