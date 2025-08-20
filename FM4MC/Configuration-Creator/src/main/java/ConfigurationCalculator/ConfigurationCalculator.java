package ConfigurationCalculator;

import CNFClauseGenerator.CNFClauseGenerator;
import ConfigurationCalculator.Structures.FeatureModelPartiallyCalculated;
import ConfigurationCalculator.Structures.PartialConfiguration;
import FeatureModelReader.Structures.CrossTreeConstraint;
import FeatureModelReader.Structures.CrossTreeConstraintRelation;
import FeatureModelReader.Structures.Feature;
import FeatureModelReader.Structures.FeatureModelRead;
import FeatureModelSlicer.Structures.FeatureModelSliced;
import org.apache.logging.log4j.Logger;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ISolver;
import org.sat4j.tools.ModelIterator;

import java.util.*;
import java.util.stream.Collectors;

public class ConfigurationCalculator {
    private final Logger _ApplicationLogger;
    private final CNFClauseGenerator cnfClauseGenerator;
    private final ModelIterator solver = new ModelIterator(SolverFactory.newDefault());

    public ConfigurationCalculator(Logger logger) {
        _ApplicationLogger = logger;
        cnfClauseGenerator = new CNFClauseGenerator(_ApplicationLogger);
    }

    /**
     * calculates all valid configurations of the abstract layer
     * then passes CTC down to concrete Features if possible
     * then calculates all valid configurations of partial FMs with concrete features
     * will remove and modify CTCs when they can be included in configurations!!!
     *
     * @param featureModelSliced FM that is sliced into abstract layer and multiple concrete partial feature models
     * @return FM with calculated configurations for the abstract layer and configurations for each concrete partial FM
     */
    public FeatureModelPartiallyCalculated calculatePartialConfigurations(FeatureModelSliced featureModelSliced) {

        var fm = new FeatureModelPartiallyCalculated(featureModelSliced);
        var parents = new ArrayList<Feature>();
        featureModelSliced.features.stream().filter(f -> !f.getChildren().isEmpty()).forEach(f -> {
            if (f.getChildren().stream().allMatch(child -> child.getChildren().isEmpty())) {
                parents.add(f);
            }
        });
        calculateAbstractLayer(fm, parents);
        passCrossTreeConstraintsToChildren(fm);
        calculateConcreteConfigurations(fm, parents);

        return fm;
    }

    /**
     * Propagates cross-tree constraints to child features wherever possible.
     * The source feature of a constraint can always be replaced by its
     * children, whereas the target of a {@link CrossTreeConstraintRelation#REQUIRES}
     * relation cannot be split across multiple features and therefore remains
     * unchanged.
     *
     * @param fm the feature model for which constraints should be propagated
     */
    private void passCrossTreeConstraintsToChildren(FeatureModelPartiallyCalculated fm) {
        //pass down ctc source to children
        while (fm.crossTreeConstraints.stream().anyMatch(constraint -> !constraint.getSource().getChildren().isEmpty())) {
            var newCrossTreeConstraints = new ArrayList<CrossTreeConstraint>();
            for (var iter = fm.crossTreeConstraints.iterator(); iter.hasNext(); ) {
                var constraint = iter.next();
                var source = constraint.getSource();
                if (!source.getChildren().isEmpty()) {
                    source.getChildren().forEach(child -> newCrossTreeConstraints.add(new CrossTreeConstraint(child, constraint.getTarget(), constraint.getRelation())));
                    iter.remove();
                }
            }
            fm.crossTreeConstraints.addAll(newCrossTreeConstraints);
        }

        //pass down exclude targets to children
        while (fm.crossTreeConstraints.stream().filter(crossTreeConstraint -> crossTreeConstraint.getRelation() == CrossTreeConstraintRelation.EXCLUDES).anyMatch(constraint -> !constraint.getTarget().getChildren().isEmpty())) {
            var newCrossTreeConstraints = new ArrayList<CrossTreeConstraint>();
            for (var iter = fm.crossTreeConstraints.iterator(); iter.hasNext(); ) {
                var constraint = iter.next();
                if (constraint.getRelation() == CrossTreeConstraintRelation.EXCLUDES) {
                    var target = constraint.getTarget();
                    if (!target.getChildren().isEmpty()) {
                        target.getChildren().forEach(child -> newCrossTreeConstraints.add(new CrossTreeConstraint(constraint.getSource(), child, constraint.getRelation())));
                        iter.remove();
                    }
                }
            }
            fm.crossTreeConstraints.addAll(newCrossTreeConstraints);
        }
    }

    /**
     * Calculates all valid configurations for the concrete partial feature
     * models of the given feature model.
     *
     * @param fm      the feature model holding the partial concrete models
     * @param parents list of abstract parent features used to determine the
     *                relevant concrete features
     */
    private void calculateConcreteConfigurations(FeatureModelPartiallyCalculated fm, List<Feature> parents) {
        for (var partialModel : fm.partialConcreteFeatureModels) {
            List<Feature> currentParents = partialModel.stream().filter(parents::contains).toList();
            var concreteConfigurations = calculateForPFM(fm, currentParents);

            List<PartialConfiguration> partialConfigurations = new ArrayList<>();
            concreteConfigurations.forEach(concreteConfiguration -> {
                    PartialConfiguration partialConfiguration = new PartialConfiguration(concreteConfiguration);
                    partialConfiguration.setAbstractParent(currentParents);
                    sortConfiguration(partialConfiguration, fm.featureConnectivityInformation.featureConnectivityMap);
                    partialConfigurations.add(partialConfiguration);

            });
            fm.configurationsPerPartialFeatureModel.add(partialConfigurations);
        }
    }

    /**
     * Calculates all valid configurations for a single partial feature model.
     *
     * @param fm            the overall feature model
     * @param currentParents abstract parent features defining the partial model
     * @return list of valid concrete feature configurations
     */
    private List<List<Feature>> calculateForPFM(FeatureModelPartiallyCalculated fm, List<Feature> currentParents) {
        var children = new ArrayList<Feature>();
        currentParents.forEach(current -> {
            children.addAll(current.getChildren());
        });
        var clauses = cnfClauseGenerator.createConcretePFMClauses(currentParents, fm.crossTreeConstraints);
        initSolver(clauses);
        var configurationIndicesList = calculateModels(solver);
        var configurationsList = new ArrayList<List<Feature>>();
        for (var configurationIndices : configurationIndicesList) {
            var configurationIndicesAsList = Arrays.stream(configurationIndices).boxed().toList();
            var configurationFeatures = children.stream().filter(x -> configurationIndicesAsList.contains(x.getIndex())).toList();
            configurationsList.add(configurationFeatures);
        }

        return configurationsList;
    }

    /**
     * Calculates all valid configurations of the abstract layer of the feature
     * model. The resulting configurations are stored in the provided model.
     *
     * @param fm      the feature model
     * @param parents list of abstract parent features present in the model
     */
    private void calculateAbstractLayer(FeatureModelPartiallyCalculated fm, List<Feature> parents) {
        var abstractLayerCNF = cnfClauseGenerator.createAbstractLayerClauses(fm);
        initSolver(abstractLayerCNF);
        var configurationIndicesList = calculateModels(solver);
        var configurationsList = new ArrayList<List<Feature>>();

        for (var configurationIndices : configurationIndicesList) {
            var configurationIndicesAsList = Arrays.stream(configurationIndices).boxed().toList();
            var configurationFeatures = parents.stream().filter(x -> configurationIndicesAsList.contains(x.getIndex())).toList();
            configurationsList.add(configurationFeatures);
        }

        fm.abstractConfigurations = configurationsList;
    }


    /**
     * Initializes the SAT solver with the given CNF clauses.
     *
     * @param cnfClauses CNF clauses encoded as DIMACS integer arrays
     */
    private void initSolver(List<int[]> cnfClauses) {
        solver.reset();
        var headerCnfDIMACS = cnfClauses.getFirst();

        solver.newVar(headerCnfDIMACS[0]);
        solver.setExpectedNumberOfClauses(headerCnfDIMACS[1]);

        try {
            for (int i = 1; i < cnfClauses.size(); i++) {
                solver.addClause(new VecInt(cnfClauses.get(i)));
            }
        } catch (Exception e) {
            _ApplicationLogger.fatal("There was an error setting up the solver with clauses: " + e.getMessage());
        }
    }

    /**
     * Computes all satisfiable models for the current solver configuration.
     *
     * @param solver the SAT solver configured with CNF clauses
     * @return list of satisfying models represented as index arrays
     */
    private List<int[]> calculateModels(ISolver solver) {
        var models = new ArrayList<int[]>();

        try {
            while (solver.isSatisfiable()) {
                var model = solver.model();
                model = Arrays.stream(model).filter(x -> x > 1).toArray();
                models.add(model);
            }
        } catch (Exception e) {
            _ApplicationLogger.fatal("There was an error calculating the models: " + e.getMessage());
        }
        return models;
    }

    /**
     * Orders the features within the given partial configuration based on the
     * connectivity information between parent features.
     *
     * @param partialConfiguration configuration to sort
     * @param connectivityMap      map describing connectivity between parent
     *                             features
     */
    private void sortConfiguration(PartialConfiguration partialConfiguration, Map<String, List<Feature>> connectivityMap) {
        if (partialConfiguration.getFeatures().size() > 1) {
            var configurationFeatures = partialConfiguration.getFeatures();
            var uniqueParentFeatures = configurationFeatures.stream().map(Feature::getParentFeatureName).distinct().toList();
            var sortedParentFeatures = sortParentFeatures(uniqueParentFeatures, connectivityMap);
            var sortedConfigurationFeatures = new ArrayList<Feature>();


            for (var parentFeatureName : sortedParentFeatures) {
                var childFeatures = configurationFeatures.stream().filter(x -> x.getParentFeatureName().equals(parentFeatureName)).toList();
                sortedConfigurationFeatures.addAll(childFeatures);
            }
            partialConfiguration.setFeatures(sortedConfigurationFeatures);
        }
    }

    /**
     * Sorts parent feature names according to their reachability described in
     * the connectivity map.
     *
     * @param uniqueParentFeatures list of unique parent feature names
     * @param connectivityMap      map describing connectivity between parent
     *                             features
     * @return parent feature names ordered by reachability
     */
    private List<String> sortParentFeatures(List<String> uniqueParentFeatures, Map<String, List<Feature>> connectivityMap) {
        var parentFeaturesInOrder = new ArrayList<String>();
        var toInvestigate = new ArrayList<String>();
        var startFeature = findFirst(uniqueParentFeatures, connectivityMap);
        toInvestigate.add(startFeature);

        while (!toInvestigate.isEmpty()) {
            var currentFeature = toInvestigate.removeFirst();
            if (!parentFeaturesInOrder.contains(currentFeature)) {
                parentFeaturesInOrder.add(currentFeature);
                toInvestigate.addAll(connectivityMap.get(currentFeature).stream().map(Feature::getName).toList());
            }
        }
        return parentFeaturesInOrder;
    }

    /**
     * Finds the first parent feature that has no predecessors in the
     * connectivity graph.
     *
     * @param uniqueParentFeatures list of unique parent feature names
     * @param connectivityMap      map describing connectivity between parent
     *                             features
     * @return name of the first parent feature without predecessors
     */
    private String findFirst(List<String> uniqueParentFeatures, Map<String, List<Feature>> connectivityMap) {
        var allSuccessors = uniqueParentFeatures.stream().map(connectivityMap::get).flatMap(List::stream).distinct().map(Feature::getName).toList();
        return uniqueParentFeatures.stream().filter(x -> !allSuccessors.contains(x)).findFirst().orElseThrow();
    }

    /**
     * calculates all valid configurations for the full FM
     *
     * @param featureModelRead non-sliced feature model
     * @return FM with calculated configurations
     */
    public FeatureModelPartiallyCalculated calculatedConfigurationForNonSlicedFM(FeatureModelRead featureModelRead) {
        var fm = new FeatureModelPartiallyCalculated(new FeatureModelSliced(featureModelRead));
        var clauses = cnfClauseGenerator.createClausesForNonSlicedFM(fm);
        calculateConfigurationsForPrecalculatedCNFs(fm, clauses);
        return fm;
    }

    /**
     * calculates all valid configurations for an FM based on the given CNF clauses
     *
     * @param fm the feature model
     * @param clauses precalculated CNF-clauses
     */
    public void calculateConfigurationsForPrecalculatedCNFs(FeatureModelPartiallyCalculated fm, List<int[]> clauses) {
        initSolver(clauses);
        var configurationIndicesList = calculateModels(solver);
        var abstractConfigurationSet = new HashSet<List<Feature>>();
        for (var configurationIndices : configurationIndicesList) {
            var concreteConfiguration = new PartialConfiguration();
            var abstractConfiguration = new ArrayList<Feature>();
            var configurationIndicesAsSet = Arrays.stream(configurationIndices).boxed().collect(Collectors.toSet());
            fm.features.forEach(feature -> {
                if (configurationIndicesAsSet.contains(feature.getIndex())) {
                    if (feature.getChildren().isEmpty()) {
                        concreteConfiguration.addFeature(feature);
                    } else if (feature.getChildren().stream().allMatch(child -> child.getChildren().isEmpty())) {
                        abstractConfiguration.add(feature);
                        concreteConfiguration.addAbstractParent(feature);
                    }
                }
            });
            abstractConfigurationSet.add(abstractConfiguration);
            fm.configurationsPerPartialFeatureModel.add(Collections.singletonList(concreteConfiguration));
        }
        fm.abstractConfigurations.addAll(abstractConfigurationSet);
    }

    /*

    private List<Feature> sortParentFeatures(List<Feature> uniqueParentFeatures, Map<String, List<Feature>> connectivityMap) {
        //TODO DOUBLE CHECK LOGIC
        var parentFeaturesInOrder = new ArrayList<Feature>();
        var toInvestigate = new ArrayList<Feature>();
        var startFeature = findFirst(uniqueParentFeatures, connectivityMap);
        toInvestigate.add(startFeature);

        while (!toInvestigate.isEmpty()) {
            var currentFeature = toInvestigate.remove(0);
            if (!parentFeaturesInOrder.contains(currentFeature)) {
                parentFeaturesInOrder.add(currentFeature);
                toInvestigate.addAll(connectivityMap.get(currentFeature.getName()).stream().toList());
            }
        }
        return parentFeaturesInOrder;
    }

    private Feature findFirst(List<Feature> uniqueParentFeatures, Map<String, List<Feature>> connectivityMap) {
        var allSuccessors = uniqueParentFeatures.stream().map(feature -> connectivityMap.get(feature.getName())).flatMap(List::stream).distinct().toList();
        return uniqueParentFeatures.stream().filter(x -> !allSuccessors.contains(x)).findFirst().orElseThrow();
    }*/
}
