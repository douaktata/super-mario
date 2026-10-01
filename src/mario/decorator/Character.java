package mario.decorator;

/**
 * Interface de base représentant un personnage du jeu Mario.
 * Définit le contrat minimal d'informations (description et statistiques).
 */
public interface Character {

    /**
     * Obtenir la description textuelle du personnage et de ses décorations.
     *
     * @return la description sous forme de chaîne de caractères.
     */
    String getDescription();

    /**
     * Obtenir les points de santé du personnage.
     *
     * @return les points de santé.
     */
    int getHealth();
    
    /**
     * Obtenir les points de puissance du personnage.
     *
     * @return les points de puissance.
     */
    int getPower();

    /**
     * Obtenir les points de vitesse du personnage.
     *
     * @return les points de vitesse.
     */
    int getSpeed();
}
