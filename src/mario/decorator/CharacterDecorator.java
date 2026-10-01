package mario.decorator;

/**
 * Classe abstraite de base pour tous les décorateurs de personnage.
 * Implémente la délégation systématique par défaut vers l'objet englobé.
 */
public abstract class CharacterDecorator implements Character {

    /**
     * Le personnage enveloppé par ce décorateur.
     */
    protected Character wrappedCharacter;

    /**
     * Initialise le décorateur avec le personnage à englober.
     *
     * @param wrappedCharacter Le personnage à décorer.
     */
    public CharacterDecorator(Character wrappedCharacter) {
        this.wrappedCharacter = wrappedCharacter;
    }

    @Override
    public String getDescription() {
        return wrappedCharacter.getDescription();
    }

    @Override
    public int getHealth() {
        return wrappedCharacter.getHealth();
    }

    @Override
    public int getPower() {
        return wrappedCharacter.getPower();
    }

    @Override
    public int getSpeed() {
        return wrappedCharacter.getSpeed();
    }
}
