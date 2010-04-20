package ch.epfl.dslab.conferrng.arugula;

/**
 *
 * Describes the modification of a string.
 *
 */
public abstract class StringModification {

    private Description description;

    public class Description {

        private String original;
        private String modified;

        public Description(String original, String modified) {
            this.original = original;
            this.modified = modified;
        }

        @Override
        public String toString() {
            return "Changed from " + original + " to " + modified;
        }
    }

    /**
     * Returns the description of the psychological mechanism that lead to the modification
     *
     * @return a human readable description
     */
    public String getDescription() {
        return description.toString();
    }

    /**
     * Returns the modified text
     *
     * @return the modified text
     */
    public String getNewText(String text) {
        String modified = getModification(text);
        description = new Description(text, modified);
        return modified;
    }

    protected abstract String getModification(String text);
}
