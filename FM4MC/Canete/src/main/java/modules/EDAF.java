package modules;

import ConfigurationCalculator.Structures.PartialConfiguration;
import FeatureModelReader.Structures.Feature;
import FeatureModelReader.Structures.FeatureModelRead;
import IO.impl.LshwClass;
import com.microsoft.z3.*;
import edgeNodeReader.structures.EdgeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EDAF {

    //only load one configuration? not every possible one?
    public List<HashMap<Feature, EdgeNode>> calculateTaskDeployment(EdgeNode[] edgeNodes, PartialConfiguration configuration, FeatureModelRead featureModel, boolean singleResult, int maxRequirements) {

        var tasks = configuration.getFeatures();
        var taskEdges = new HashMap<Feature, List<Feature>>();
        for (var task : tasks) {
            var connectedAbstractFeatures = featureModel.featureConnectivityInformation.featureConnectivityMap.get(task.getParentFeatureName());
            var connectedTasks = new ArrayList<Feature>();
            tasks.stream().filter(f -> connectedAbstractFeatures.contains(f.getParentFeature())).forEach(connectedTasks::add);
            taskEdges.put(task, connectedTasks);
        }

        var ctx = new Context();

        BoolExpr[][] assignment = new BoolExpr[tasks.size()][edgeNodes.length];
        for (int n = 0; n < edgeNodes.length; n++) {
            for (int t = 0; t < tasks.size(); t++) {
                assignment[t][n] = ctx.mkBoolConst("Assign: " + tasks.get(t).getName() + " -> " + edgeNodes[n].id);
            }
        }

        RealExpr[] ramResources = new RealExpr[edgeNodes.length];

        for (int i = 0; i < edgeNodes.length; i++) {
            ramResources[i] = ctx.mkRealConst("RAM usage at Edge Node " + edgeNodes[i].id);
        }
        /*
        //relation in Module 3
        int[][] dataTransmissions = new int[tasks.size()][tasks.size()];
        for (int i = 0; i < tasks.size(); i++) {
            for (int j = 0; j < tasks.size(); j++) {
                dataTransmissions[i][j] = 0;
                if (taskEdges.get(tasks.get(i)).contains(tasks.get(j))) {
                    dataTransmissions[i][j] = tasks.get(i).hardwareRequirements.get(LshwClass.NETWORK);
                }
            }
        }

         */


        /*
        int[][] constraints = new int[tasks.size()][tasks.size() + 2];
        for (int i = 0; i < tasks.size(); i++) {
            var task = tasks.get(i);
            constraints[i][0] = task.hardwareRequirements.get(LshwClass.MEMORY); //TODO set MaxExecutionTime
            var destinations = taskEdges.get(task);
            constraints[i][1] = destinations.size();
            for (int j = 0; j < destinations.size(); j++) {
                constraints[i][j + 2] = tasks.indexOf(destinations.get(j));
            }
        }*/

        var opt = ctx.mkOptimize();

        //each task assigned to exactly one node
        for (int t = 0; t < tasks.size(); t++) {
            ArithExpr<IntSort> sumAssignment = ctx.mkInt(0);
            BoolExpr requirements = ctx.mkTrue();
            for (int n = 0; n < edgeNodes.length; n++) {
                sumAssignment = ctx.mkAdd(sumAssignment, ctx.mkITE(assignment[t][n], ctx.mkInt(1), ctx.mkInt(0)));

                var hwRequirements = ctx.mkTrue();

                switch (maxRequirements) {
                    case 14:
                        hwRequirements = ctx.mkAnd(hwRequirements, ctx.mkBool(tasks.get(t).getHardwareRequirements().get(LshwClass.WIFI) <= edgeNodes[n].wifi));
                    case 13:
                        hwRequirements = ctx.mkAnd(hwRequirements, ctx.mkBool(tasks.get(t).getHardwareRequirements().get(LshwClass.BLE) <= edgeNodes[n].ble));
                    case 12:
                        hwRequirements = ctx.mkAnd(hwRequirements, ctx.mkBool(tasks.get(t).getHardwareRequirements().get(LshwClass.SENSOR) <= edgeNodes[n].sensor));
                    case 11:
                        hwRequirements = ctx.mkAnd(hwRequirements, ctx.mkBool(tasks.get(t).getHardwareRequirements().get(LshwClass.MICROPHONE) <= edgeNodes[n].microphone));
                    case 10:
                        hwRequirements = ctx.mkAnd(hwRequirements, ctx.mkBool(tasks.get(t).getHardwareRequirements().get(LshwClass.CAMERA) <= edgeNodes[n].camera));
                    case 9:
                        hwRequirements = ctx.mkAnd(hwRequirements, ctx.mkBool(tasks.get(t).getHardwareRequirements().get(LshwClass.ENERGY) <= edgeNodes[n].energy));
                    case 8:
                        hwRequirements = ctx.mkAnd(hwRequirements, ctx.mkBool(tasks.get(t).getHardwareRequirements().get(LshwClass.PRICE) <= edgeNodes[n].price));
                    case 7:
                        hwRequirements = ctx.mkAnd(hwRequirements, ctx.mkBool(tasks.get(t).getHardwareRequirements().get(LshwClass.STORAGE) <= edgeNodes[n].storage));
                    case 6:
                        hwRequirements = ctx.mkAnd(hwRequirements, ctx.mkBool(tasks.get(t).getHardwareRequirements().get(LshwClass.NEURAL) <= edgeNodes[n].npu));
                    case 5:
                        hwRequirements = ctx.mkAnd(hwRequirements, ctx.mkBool(tasks.get(t).getHardwareRequirements().get(LshwClass.TENSOR) <= edgeNodes[n].tpu));
                    case 4:
                        hwRequirements = ctx.mkAnd(hwRequirements, ctx.mkBool(tasks.get(t).getHardwareRequirements().get(LshwClass.NETWORK) <= edgeNodes[n].bandwidth));
                    case 3:
                    case 2:
                        hwRequirements = ctx.mkAnd(hwRequirements, ctx.mkBool(tasks.get(t).getHardwareRequirements().get(LshwClass.PROCESSOR) <= edgeNodes[n].cpu));
                    case 1:
                        hwRequirements = ctx.mkAnd(hwRequirements, ctx.mkBool(tasks.get(t).getHardwareRequirements().get(LshwClass.DISPLAY) <= edgeNodes[n].gpu));
                }

                //if not assigned -> true
                //if assigned -> all requirements fulfilled
                requirements = ctx.mkAnd(requirements, ctx.mkITE(assignment[t][n], hwRequirements, ctx.mkTrue()));


            }
            opt.Add(ctx.mkEq(sumAssignment, ctx.mkInt(1)));
            opt.Add(requirements);
        }

        //ram
        if (maxRequirements >= 3) {
            for (int n = 0; n < edgeNodes.length; n++) {
                opt.Add(ctx.mkLe(ramResources[n], ctx.mkInt(edgeNodes[n].ram)));
            }

            for (int n = 0; n < edgeNodes.length; n++) {
                ArithExpr<IntSort> sumRam = ctx.mkInt(0);
                for (int t = 0; t < tasks.size(); t++) {
                    sumRam = ctx.mkAdd(sumRam, ctx.mkITE(assignment[t][n], ctx.mkInt(tasks.get(t).getHardwareRequirements().get(LshwClass.MEMORY)), ctx.mkInt(0)));
                    opt.Add(ctx.mkLe(sumRam, ramResources[n]));
                }
            }
        }


        /*
        for (int c = 0; c < tasks.size(); c++) {
            for (int n = 0; n < edgeNodes.length; n++) {
                for (int i = 2; i < 2 + constraints[c][1]; i++) {
                    ArithExpr<IntSort> sumConstraints = ctx.mkInt(0);

                    for (int j = 2; j < 2 + constraints[c][1]; j++) {

                        ctx.mkAdd(sumConstraints, ctx.mkDiv(
                                ctx.mkMul(ctx.mkITE(assignment[constraints[c][i]][n], ctx.mkInt(1), ctx.mkInt(0)),
                                        ctx.mkITE(assignment[constraints[c][j]][n], ctx.mkInt(1), ctx.mkInt(0)),
                                        ctx.mkInt(dataTransmissions[constraints[c][i]][constraints[c][j]]),
                                        ctx.mkInt(1000000)),
                                ctx.mkInt(edgeNodes[n].bandwidth)));
                    }

                    var y = ctx.mkMul(ctx.mkITE(assignment[constraints[c][i]][n], ctx.mkInt(1), ctx.mkInt(0)), ctx.mkInt(tasks.get(constraints[c][i]).hardwareRequirements.get(LshwClass.PROCESSOR)));
                    var x = ctx.mkAdd(sumConstraints, ctx.mkDiv(y, ctx.mkInt(edgeNodes[n].cpu)));

                    opt.Add(ctx.mkLe(ctx.mkMul(x, ctx.mkInt(1000000)), ctx.mkInt(constraints[c][1] * 1000000)));
                }
            }
        }*/

        ArrayList<HashMap<Feature, EdgeNode>> result = new ArrayList<>();

        while (opt.Check() == Status.SATISFIABLE) {
            var assignmentMap = new HashMap<Feature, EdgeNode>();
            var assignmentExpression = ctx.mkTrue();
            for (int n = 0; n < edgeNodes.length; n++) {
                for (int t = 0; t < tasks.size(); t++) {
                    if (opt.getModel().getConstInterp(assignment[t][n]).isTrue()) {
                        assignmentMap.put(tasks.get(t), edgeNodes[n]);
                        assignmentExpression = ctx.mkAnd(assignmentExpression, assignment[t][n]);
                    }
                }
            }
            result.add(assignmentMap);
            if (singleResult) {
                ctx.close();
                return result;
            }
            opt.Add(ctx.mkNot(assignmentExpression));
        }
        ctx.close();
        return result;
    }
}