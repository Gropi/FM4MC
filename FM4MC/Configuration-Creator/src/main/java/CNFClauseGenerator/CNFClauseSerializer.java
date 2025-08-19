package CNFClauseGenerator;

import FeatureModelReader.Structures.CrossTreeConstraintRelation;
import FeatureModelSlicer.Structures.FeatureModelSliced;
import IO.impl.DriveHandle;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CNFClauseSerializer {

    private final String _DELIMITER = " ";

    public void saveClausesAsDIMACS(FeatureModelSliced featureModelConfigurations, String filePath) {
        var stringBuilder = new StringBuilder();
        addHeader(stringBuilder, featureModelConfigurations.partialFeatureModelClauses.get(0).get(0), featureModelConfigurations.crossTreeConstraints.size());

        for (var Clauses : featureModelConfigurations.partialFeatureModelClauses) {
            var clauseIterator = Clauses.iterator();
            clauseIterator.next(); //skip header
            while (clauseIterator.hasNext()) {
                var clause = clauseIterator.next();
                for (var literal : clause) {
                    stringBuilder.append(literal).append(_DELIMITER);
                }
                stringBuilder.append("0").append("\n");
            }
        }

        for (var constraint : featureModelConfigurations.crossTreeConstraints) {
            if (constraint.getRelation() == CrossTreeConstraintRelation.EXCLUDES) {
                stringBuilder.append(-constraint.getSource().getIndex()).append(_DELIMITER)
                        .append(-constraint.getTarget().getIndex()).append(_DELIMITER)
                        .append("0").append("\n");
            } else if (constraint.getRelation() == CrossTreeConstraintRelation.REQUIRES) {
                stringBuilder.append(-constraint.getSource().getIndex()).append(_DELIMITER)
                        .append(constraint.getTarget().getIndex()).append(_DELIMITER)
                        .append("0").append("\n");
            }

        }
        saveToFile(filePath, stringBuilder);
    }

    private void saveToFile(String filePath, StringBuilder stringBuilder) {
        try {
            new DriveHandle().createFolderFromFile(filePath);
            var writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(stringBuilder.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addHeader(StringBuilder stringBuilder, int[] header, int constraints) {
        stringBuilder.append("p").append(_DELIMITER)
                .append("cnf").append(_DELIMITER)
                .append(header[0]).append(_DELIMITER)
                .append(header[1] + constraints)
                .append("\n");
    }
}
