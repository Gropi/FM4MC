package Structures.Graph.interfaces;

import Condition.ConditionalWeight;
import Condition.ParameterCost;
import Services.Microservice;
import Structures.Graph.Edge;

import java.util.List;
import java.util.UUID;

public interface IVertex extends IWeight{
    void updateServiceName(String serviceName);
    int getId();
    String getLabel();
    List<ParameterCost> getWeights();

    // TODO: Change to Application Information - since this not a real information for a vertex
    int getQoR();
    void setQoR(int qor);

    // TODO: Change to Application Information - since this not a real information for a vertex
    int getStage();
    void setStage(int value);

    IVertex clone();
    IVertex clone(List<IVertex> alreadyClonedVertices);

    boolean isDecisionMakingVertex();
    void setDecisionMakingVertex(boolean isDecisionMaking);
    List<Edge> getOutgoingEdges();
    void addOutgoingEdge(Edge edge);
    List<ConditionalWeight> getConditionalWeights();
    void addConditionalWeight(IVertex vertex);

    // TODO: Change to Application Information - since this not a real information for a vertex
    int getApplicationIndex();
    void setApplicationIndex(int index);
    int getApproximationIndex();
    void setApproximationIndex(int index);

    // TODO: Same here...
    String getServiceName();
    void bindMicroservice(Microservice service);
    Microservice getMicroservice();
    void unbindMicroservice();
}
