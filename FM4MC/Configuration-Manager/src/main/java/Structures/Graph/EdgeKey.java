package Structures.Graph;

public final class EdgeKey {
    private final int sourceId;
    private final int destinationId;
    private final int hash;  // Vorberechneter Hash-Wert

    public EdgeKey(int sourceId, int destinationId) {
        this.sourceId = sourceId;
        this.destinationId = destinationId;
        // Berechne einen Hash, der auf den HashCodes der Strings basiert
        this.hash = 31 * sourceId + destinationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EdgeKey other)) return false;
        return sourceId == other.sourceId && destinationId == other.destinationId;
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
