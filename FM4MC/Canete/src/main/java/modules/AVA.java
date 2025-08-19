package modules;

import FeatureModelReader.Structures.Feature;
import FeatureModelReader.Structures.FeatureModelRead;
import IO.impl.LshwClass;
import edgeNodeReader.structures.EdgeNode;
import com.microsoft.z3.*;

import java.util.*;

public class AVA {

    public List<Feature> adaptApplication(EdgeNode[] edgeNodes, List<Feature> features, int maxRequirements) {

        var ctx = new Context();
        RealExpr nEnabled = ctx.mkRealConst("nEnabled");


        BoolExpr[] enabled = new BoolExpr[features.size()];

        var featureMap = new HashMap<BoolExpr, Feature>();

        for (int i = 0; i < features.size(); i++) {
            enabled[i] = ctx.mkBoolConst("Feature: " + features.get(i).getName());
            featureMap.put(enabled[i], features.get(i));
        }

        var opt = ctx.mkOptimize();

        for (int i = 0; i < features.size(); i++) {
            ArithExpr<IntSort> sum = ctx.mkInt(0);
            for (EdgeNode edgeNode : edgeNodes) {
                var requirements = ctx.mkTrue();

                switch (maxRequirements) {
                    case 14:
                        requirements = ctx.mkAnd(requirements, ctx.mkBool(features.get(i).getHardwareRequirements().get(LshwClass.WIFI) <= edgeNode.wifi));
                    case 13:
                        requirements = ctx.mkAnd(requirements, ctx.mkBool(features.get(i).getHardwareRequirements().get(LshwClass.BLE) <= edgeNode.ble));
                    case 12:
                        requirements = ctx.mkAnd(requirements, ctx.mkBool(features.get(i).getHardwareRequirements().get(LshwClass.SENSOR) <= edgeNode.sensor));
                    case 11:
                        requirements = ctx.mkAnd(requirements, ctx.mkBool(features.get(i).getHardwareRequirements().get(LshwClass.MICROPHONE) <= edgeNode.microphone));
                    case 10:
                        requirements = ctx.mkAnd(requirements, ctx.mkBool(features.get(i).getHardwareRequirements().get(LshwClass.CAMERA) <= edgeNode.camera));
                    case 9:
                        requirements = ctx.mkAnd(requirements, ctx.mkBool(features.get(i).getHardwareRequirements().get(LshwClass.ENERGY) <= edgeNode.energy));
                    case 8:
                        requirements = ctx.mkAnd(requirements, ctx.mkBool(features.get(i).getHardwareRequirements().get(LshwClass.PRICE) <= edgeNode.price));
                    case 7:
                        requirements = ctx.mkAnd(requirements, ctx.mkBool(features.get(i).getHardwareRequirements().get(LshwClass.STORAGE) <= edgeNode.storage));
                    case 6:
                        requirements = ctx.mkAnd(requirements, ctx.mkBool(features.get(i).getHardwareRequirements().get(LshwClass.NEURAL) <= edgeNode.npu));
                    case 5:
                        requirements = ctx.mkAnd(requirements, ctx.mkBool(features.get(i).getHardwareRequirements().get(LshwClass.TENSOR) <= edgeNode.tpu));
                    case 4:
                        requirements = ctx.mkAnd(requirements, ctx.mkBool(features.get(i).getHardwareRequirements().get(LshwClass.NETWORK) <= edgeNode.bandwidth));
                    case 3:
                        requirements = ctx.mkAnd(requirements, ctx.mkBool(features.get(i).getHardwareRequirements().get(LshwClass.MEMORY) <= edgeNode.ram));
                    case 2:
                        requirements = ctx.mkAnd(requirements, ctx.mkBool(features.get(i).getHardwareRequirements().get(LshwClass.PROCESSOR) <= edgeNode.cpu));
                    case 1:
                        requirements = ctx.mkAnd(requirements, ctx.mkBool(features.get(i).getHardwareRequirements().get(LshwClass.DISPLAY) <= edgeNode.gpu));
                }
                sum = ctx.mkAdd(sum, ctx.mkITE(requirements, ctx.mkInt(1), ctx.mkInt(0)));
            }
            opt.Add(ctx.mkITE(enabled[i], ctx.mkGe(sum, ctx.mkInt(1)), ctx.mkITE(enabled[i], ctx.mkFalse(), ctx.mkTrue())));
        }

        ArithExpr<IntSort> sumEnabled = ctx.mkInt(0);
        for (BoolExpr boolExpr : enabled) {
            sumEnabled = ctx.mkAdd(sumEnabled, ctx.mkITE(boolExpr, ctx.mkInt(1), ctx.mkInt(0)));
        }
        opt.Add(ctx.mkEq(nEnabled, sumEnabled));
        opt.MkMaximize(nEnabled);
        opt.Check();

        var res = new ArrayList<Feature>();

        for (BoolExpr boolExpr : enabled) {
            if (opt.getModel().getConstInterp(boolExpr).isFalse()) {
                res.add(featureMap.get(boolExpr));
            }
        }
        ctx.close();

        return res;
    }

    public List<Feature> adaptApplication(EdgeNode[] edgeNodesArray, FeatureModelRead fm, int maxRequirements) {
        List<Feature> concreteFeatures = new ArrayList<>();
        fm.features.stream().filter(x -> isLeaf(x, fm.features)).forEach(concreteFeatures::add);

        return adaptApplication(edgeNodesArray, concreteFeatures, maxRequirements);
    }

    private boolean isLeaf(Feature x, List<Feature> features) {
        return features.stream().noneMatch(feature -> feature.getParentFeatureName().equals(x.getName()));
    }
}

