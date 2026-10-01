package mario.decorator;

/**
 * Composant concret représentant le personnage de base Mario.
 * Définit les statistiques initiales de Mario avant tout power-up.
 */
public class Mario implements Character {

    /**
     * Constructeur par défaut de Mario.
     */
    public Mario() {
        // Constructeur explicite pour éviter tout avertissement de javadoc
    }

    @Override
    public String getDescription() {
        return "Mario";
    }

    @Override
    public int getHealth() {
        return 100;
    }

    @Override
    public int getPower() {
        return 10;
    }

    @Override
    public int getSpeed() {
        return 10;
    }
}
