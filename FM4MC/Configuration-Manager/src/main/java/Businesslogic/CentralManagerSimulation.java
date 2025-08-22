package Businesslogic;

import ConfigurationSerializer.ConfigurationSerializer;
import FeatureModelMerger.HardwareSensitiveFeatureModelMerger;
import FeatureModelMerger.Structures.AvailableEdgeHardware;
import FeatureModelReader.FeatureModelReader;
import FeatureModelReader.InvalidFeatureModelRelationException;
import Graph.GraphOnlineParser;
import IO.impl.LshwClass;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class CentralManagerSimulation {
    private final Logger _Logger;


    public CentralManagerSimulation(Logger logger) {
        _Logger = logger;
    }

    public void startOnlinePhase(String configurationsPath, String fmFilePath, int edgeIndex) {
        var edgeInformation = getEdgeInformation(edgeIndex);
        if (edgeInformation == null) {
            _Logger.error(edgeIndex + " is not a valid Edge Index");
        } else {
            var fmReader = new FeatureModelReader(_Logger);
            var fmSerializer = new ConfigurationSerializer(_Logger);
            try {
                var featureModelWithConfigurations = fmSerializer.loadConfigurations(fmReader.readFeatureModelJson(new File(fmFilePath)), configurationsPath);

                var merger = new HardwareSensitiveFeatureModelMerger(_Logger);
                _Logger.info("Graph generation started");
                var graph = merger.startForTesting(featureModelWithConfigurations, edgeInformation, 14);
                graph.recalculateGraphStages();
                _Logger.info("Graph generation finished");
                new GraphOnlineParser(_Logger).saveGraphToXML(graph, "D:\\temp.graphml");

            } catch (InvalidFeatureModelRelationException e) {
                _Logger.fatal(e);
            }
        }
    }

    private AvailableEdgeHardware getEdgeInformation(int edgeIndex) {
        AvailableEdgeHardware edgeInformation = null;
        switch (edgeIndex) {
            case 1 -> {
                edgeInformation = new AvailableEdgeHardware(2);
                edgeInformation.edgeHardware.put(LshwClass.DISPLAY, 1);
                edgeInformation.edgeHardware.put(LshwClass.PROCESSOR, 3);
                edgeInformation.edgeHardware.put(LshwClass.MEMORY, 2);
            }
            case 2 -> {
                edgeInformation = new AvailableEdgeHardware(4);
                edgeInformation.edgeHardware.put(LshwClass.DISPLAY, 10);
                edgeInformation.edgeHardware.put(LshwClass.PROCESSOR, 6);
                edgeInformation.edgeHardware.put(LshwClass.MEMORY, 5);
            }
            case 3 -> {
                edgeInformation = new AvailableEdgeHardware(6);
                edgeInformation.edgeHardware.put(LshwClass.DISPLAY, 10);
                edgeInformation.edgeHardware.put(LshwClass.PROCESSOR, 6);
                edgeInformation.edgeHardware.put(LshwClass.MEMORY, 5);
            }
            case 4 -> {
                edgeInformation = new AvailableEdgeHardware(8);
                edgeInformation.edgeHardware.put(LshwClass.DISPLAY, 10);
                edgeInformation.edgeHardware.put(LshwClass.PROCESSOR, 9);
                edgeInformation.edgeHardware.put(LshwClass.MEMORY, 8);
            }
            case 5 -> {
                edgeInformation = new AvailableEdgeHardware(10);
            }
        }
        return edgeInformation;
    }
}
