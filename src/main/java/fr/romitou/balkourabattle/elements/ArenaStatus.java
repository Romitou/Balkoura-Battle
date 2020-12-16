package fr.romitou.balkourabattle.elements;

public enum ArenaStatus {

    /*
     * The arena is free to host a match.
     */
    FREE("libre"),
    /*
     * The arena is waiting for approval to host a match.
     */
    VALIDATING("en attente de validation"),
    /*
     * The arena is already used by another match.
     */
    BUSY("occupé");

    ArenaStatus(String message) {
    }

}
